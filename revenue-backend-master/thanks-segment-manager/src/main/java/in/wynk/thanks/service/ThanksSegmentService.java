package in.wynk.thanks.service;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import com.google.gson.Gson;
import in.wynk.common.utils.BCEncryptor;
import in.wynk.common.utils.EncryptionUtils;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.common.utils.BCEncryptor;
import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.service.IUsermetaService;
import in.wynk.thanks.dto.ThanksSegmentDTO;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import in.wynk.thanks.publisher.AtvThanksPublisher;
import in.wynk.thanks.publisher.MusicThanksPublisher;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.concurrent.ExecutorService;

import static in.wynk.thanks.utils.ThanksConstants.*;

@Service
public class ThanksSegmentService {

    private static final Logger logger = LoggerFactory.getLogger(ThanksSegmentService.class);

    private static String token;

    @Value("${user.wcf-thanks.key}")
    private String thanksToken;
    @Value("${user.token.key}")
    private String uidToken;

    @Autowired
    private Gson gson;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private IUsermetaService usermetaService;
    @Autowired
    private AtvThanksPublisher atvThanksPublisher;
    @Autowired
    private MusicThanksPublisher musicThanksPublisher;

    @PostConstruct
    public void init() {
        token = EncryptionUtils.generateAppToken(thanksToken, uidToken);
    }

    //process kinesis record.
    @AnalyseTransaction(name = "thanksSegments")
    public void processRecords(String data) {
        AnalyticService.update("dataStr", data);
        ThanksSegmentDTO thanksSegmentDTO = gson.fromJson(data, ThanksSegmentDTO.class);
        if(isInvalid(thanksSegmentDTO)){
            logger.error(ThanksLoggingMarkers.THANKS_ERROR, "SI is blank {}", data);
        }
        updateSegment(thanksSegmentDTO);
    }

    public boolean isInvalid(ThanksSegmentDTO thanksSegmentDTO) {
        return StringUtils.isAnyBlank(thanksSegmentDTO.getSi(), thanksSegmentDTO.getServicePack(), thanksSegmentDTO.getEvent());
    }

    public void updateSegment(ThanksSegmentDTO thanksSegmentDTO) {
        ThanksUserSegment segment = insert(thanksSegmentDTO);
        //send to downstream only for activation event.
        if (StringUtils.equalsIgnoreCase(ACTIVATION, thanksSegmentDTO.getEvent())) {
            sendToDownstreamQueues(thanksSegmentDTO, segment);
        }
    }

    private ThanksUserSegment insert(ThanksSegmentDTO thanksSegmentDTO) {
        ThanksUserSegment segment = transform(thanksSegmentDTO);
        AnalyticService.update(segment);
        AnalyticService.update("insert-init", true);
        usermetaService.saveThanksUserSegment(segment.getSi(), segment);
        AnalyticService.update("inserted", true);
        return segment;
    }

    private ThanksUserSegment transform(ThanksSegmentDTO thanksSegmentDTO) {
        String si = BCEncryptor.decrypt(thanksSegmentDTO.getSi(), token);
        Date thanksTimestamp = new Date(NumberUtils.toLong(thanksSegmentDTO.getTimestamp()));
        Date validity = StringUtils.isNotBlank(thanksSegmentDTO.getValidity()) ? new Date(NumberUtils.toLong(thanksSegmentDTO.getValidity())) : null;
        return ThanksUserSegment.builder()
                .si(si)
                .validity(validity)
                .thanksTimestamp(thanksTimestamp)
                .event(thanksSegmentDTO.getEvent())
                .encryptedSi(thanksSegmentDTO.getSi())
                .lob(getLob(thanksSegmentDTO.getServicePack()))
                .servicePack(thanksSegmentDTO.getServicePack())
                .updatedAt(new Date(System.currentTimeMillis()))
                .service(getService(thanksSegmentDTO.getServicePack()))
                .build();
    }

    private void sendToDownstreamQueues(ThanksSegmentDTO thanksSegmentDTO, ThanksUserSegment segment) {
        if (segment.getService().equalsIgnoreCase(AIRTEL_TV)) {
            //todo: remove validity
            thanksSegmentDTO.setValidity(System.currentTimeMillis() + "");
            atvThanksPublisher.publishEventToKinesis(thanksSegmentDTO.getSi(), thanksSegmentDTO);
        }
        if (StringUtils.equalsAnyIgnoreCase(segment.getService(), MUSIC, HELLOTUNES)) {
            String uid = MsisdnUtils.getUidFromMsisdn(segment.getSi());
            if (StringUtils.isNotBlank(uid)) {
                MusicThanksPublisher.MusicThanksSegment musicThanksSegment = new MusicThanksPublisher.MusicThanksSegment(uid, segment.getSi());
                musicThanksPublisher.publishEventToKinesis(uid, musicThanksSegment);
            }
        }
    }

    private String getService(String str) {
        String service = null;
        if (StringUtils.startsWithIgnoreCase(str, ATV) || StringUtils.equalsIgnoreCase(str, ULTRA)) {
            service = AIRTEL_TV;
        } else if (StringUtils.startsWithIgnoreCase(str, BOOKS)) {
            service = BOOKS;
        } else if (StringUtils.startsWithIgnoreCase(str, WYNK)) {
            service = MUSIC;
        } else if (StringUtils.startsWithIgnoreCase(str, HELLOTUNES)) {
            service = HELLOTUNES;
        }
        return service;
    }

    private String getLob(String servicePack) {
        String lob = null;
        if (servicePack.equalsIgnoreCase(BOOKS)) {
            lob = POSTPAID;
        }
        return lob;
    }

}
