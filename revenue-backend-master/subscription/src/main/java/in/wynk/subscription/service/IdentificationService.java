package in.wynk.subscription.service;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.service.IUsermetaService;
import in.wynk.subscription.dto.MsisdnIdentificationMeta;
import in.wynk.subscription.dto.OfferEligibilityCheckRequest;
import in.wynk.subscription.dto.OfferEligibilityCheckResponse;
import in.wynk.subscription.dto.SourceIdentificationResponse;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.dto.response.IdentificationResponse.MsisdnIdentificationResponse;
import in.wynk.subscription.dto.response.IdentificationResponse.MsisdnIdentificationResponse.MsisdnIdentificationResponseBuilder;
import in.wynk.subscription.enums.LoginChannel;
import in.wynk.subscription.enums.OfferEligibilityStatus;
import in.wynk.subscription.service.impl.OfferProcessingService;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import in.wynk.vas.client.enums.UserType;
import in.wynk.vas.client.service.VasClientService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static in.wynk.exception.constants.ExceptionConstants.REQUEST_ID;
import static in.wynk.subscription.core.constants.SubscriptionLoggingMarkers.MSISDN_IDENTIFICATION_ERROR;

/**
 * @author Abhishek
 * @created 24/06/20
 */
@Slf4j
@Service
public class IdentificationService {

    public static final int TIMEOUT = 2;

    private final VasClientService vasClientService;
    private final ExecutorService executorService;
    private final IUsermetaService usermetaService;
    private final IOfferProvisionService offerProcessingService;
    private final List<IMsisdnIdentificationService> allIdentificationSource;

    public IdentificationService(OfferProcessingService offerProcessingService, VasClientService vasClientService, IUsermetaService usermetaService, ExecutorService executorService, List<IMsisdnIdentificationService> allIdentificationSource) {
        this.offerProcessingService = offerProcessingService;
        this.vasClientService = vasClientService;
        this.usermetaService = usermetaService;
        this.executorService = executorService;
        this.allIdentificationSource = allIdentificationSource;
    }

    public MsisdnIdentificationResponse identifyBestMsisdn(MsisdnIdentificationRequest msisdnIdentificationRequest) throws Exception {
        MsisdnIdentificationMeta msisdnIdentificationMeta = new MsisdnIdentificationMeta();
        CountDownLatch countDownLatch = new CountDownLatch(allIdentificationSource.size());
        for (IMsisdnIdentificationService identificationSource : allIdentificationSource) {
            executorService.submit(new MsisdnBestOfferIdentifier(MDC.get(REQUEST_ID), msisdnIdentificationMeta, countDownLatch, identificationSource, msisdnIdentificationRequest));
        }
        countDownLatch.await(TIMEOUT, TimeUnit.SECONDS);
        Map<String, SourceIdentificationResponse> allMsisdn = msisdnIdentificationMeta.getAllMsisdn();
        Map<String, Integer> msisdnOfferHierarchy = msisdnIdentificationMeta.getMsisdnOfferHierarchy();
        log.info("allMsisdn identified: {}", allMsisdn);
        log.debug("msisdnOfferHierarchy: {}", msisdnOfferHierarchy);
        String bestMsisdn = getBestMsisdn(msisdnOfferHierarchy);
        MsisdnIdentificationResponseBuilder responseBuilder = MsisdnIdentificationResponse.builder();
        if (StringUtils.isNotEmpty(bestMsisdn)) {
            SourceIdentificationResponse sourceIdentificationResponse = allMsisdn.get(bestMsisdn);
            LoginChannel loginChannel = null;
            if (sourceIdentificationResponse != null) {
                loginChannel = sourceIdentificationResponse.getLoginChannel();
            }
            MsisdnOperatorDetails msisdnOperatorDetails = msisdnIdentificationMeta.getAllMsisdnOperatorDetails().get(bestMsisdn);
            responseBuilder.channel(loginChannel).msisdn(bestMsisdn).operatorDetails(msisdnOperatorDetails);
        }
        return responseBuilder.build();
    }

    private String getBestMsisdn(Map<String, Integer> msisdnOfferHierarchy) {
        String bestMsisdn = StringUtils.EMPTY;
        int highestHierarchy = -1;
        if (!CollectionUtils.isEmpty(msisdnOfferHierarchy)) {
            for (Map.Entry<String, Integer> entry : msisdnOfferHierarchy.entrySet()) {
                String msisdn = entry.getKey();
                Integer hierarchy = entry.getValue();
                if (hierarchy > 0 && hierarchy > highestHierarchy) {
                    highestHierarchy = hierarchy;
                    bestMsisdn = msisdn;
                }
            }
        }
        return bestMsisdn;
    }

    private OfferEligibilityCheckResponse getOfferEligibilityCheckResponse(MsisdnIdentificationRequest msisdnIdentificationRequest, String msisdn, MsisdnOperatorDetails msisdnOperatorDetails) {
        WynkService service = msisdnIdentificationRequest.getService();
        MultiValuedMap<UserType, String> allUserTypes = msisdnOperatorDetails.getAllUserTypes();
        Set<String> allSi = new HashSet<>(allUserTypes.values());
        Map<String, List<ThanksUserSegment>> allThanksUserSegments = usermetaService.getAllThankSegments(msisdn, allSi);
        OfferEligibilityCheckRequest request = OfferEligibilityCheckRequest.builder()
                .wynkService(service)
                .deviceId(msisdnIdentificationRequest.getDeviceId())
                .buildNo(msisdnIdentificationRequest.getBuildNo())
                .appId(msisdnIdentificationRequest.getAppId())
                .msisdn(msisdn)
                .os(msisdnIdentificationRequest.getOs())
                .allThanksSegment(allThanksUserSegments)
                .msisdnOperatorDetails(msisdnOperatorDetails)
                .build();
        return offerProcessingService.checkEligibility(request);
    }

    private int getHighestHierarchyOfEligibleOffer(OfferEligibilityCheckResponse response) {
        int highestHierarchy = -1;
        for (OfferCheckEligibility offerCheckEligibility : response.getOfferEligibilityStatus()) {
            if (offerCheckEligibility.getStatus() == OfferEligibilityStatus.ELIGIBLE && offerCheckEligibility.getOffer().getProvisionType() == ProvisionType.FREE) {
                highestHierarchy = Math.max(highestHierarchy, offerCheckEligibility.getOffer().getHierarchy());
            }
        }
        return highestHierarchy;
    }

    @Getter
    private class MsisdnBestOfferIdentifier implements Runnable {
        private final String requestId;
        private final CountDownLatch countDownLatch;
        private final MsisdnIdentificationMeta msisdnIdentificationMeta;
        private final IMsisdnIdentificationService identificationSource;
        private final MsisdnIdentificationRequest msisdnIdentificationRequest;

        private MsisdnBestOfferIdentifier(String requestId, MsisdnIdentificationMeta msisdnIdentificationMeta, CountDownLatch countDownLatch, IMsisdnIdentificationService identificationSource, MsisdnIdentificationRequest msisdnIdentificationRequest) {
            this.msisdnIdentificationMeta = msisdnIdentificationMeta;
            this.identificationSource = identificationSource;
            this.msisdnIdentificationRequest = msisdnIdentificationRequest;
            this.countDownLatch = countDownLatch;
            this.requestId = requestId;
        }

        @Override
        public void run() {
            try {
                MDC.put(REQUEST_ID, requestId);
                SourceIdentificationResponse response = identificationSource.identifyMsisdn(msisdnIdentificationRequest);
                String msisdn = response.getMsisdn();
                if (StringUtils.isNotBlank(msisdn)) {
                    msisdnIdentificationMeta.getAllMsisdn().put(msisdn, response);
                    if (!msisdnIdentificationMeta.getMsisdnOfferHierarchy().containsKey(msisdn)) {
                        MsisdnOperatorDetails msisdnOperatorDetails = vasClientService.allOperatorDetails(msisdn);
                        msisdnIdentificationMeta.getAllMsisdnOperatorDetails().put(msisdn, msisdnOperatorDetails);
                        OfferEligibilityCheckResponse offerEligibilityCheckResponse = getOfferEligibilityCheckResponse(msisdnIdentificationRequest, msisdn, msisdnOperatorDetails);
                        msisdnIdentificationMeta.getMsisdnOfferHierarchy().put(msisdn, getHighestHierarchyOfEligibleOffer(offerEligibilityCheckResponse));
                    }
                }
            } catch (Exception ex) {
                log.error(MSISDN_IDENTIFICATION_ERROR, "Error while identifying msisdn: {}", ex.getMessage(), ex);
            }
            countDownLatch.countDown();
        }
    }
}
