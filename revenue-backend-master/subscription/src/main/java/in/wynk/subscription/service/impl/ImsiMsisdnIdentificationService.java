package in.wynk.subscription.service.impl;

import in.wynk.common.utils.MsisdnUtils;
import in.wynk.subscription.dto.SourceIdentificationResponse;
import in.wynk.subscription.dto.SourceIdentificationResponse.SourceIdentificationResponseBuilder;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.enums.LoginChannel;
import in.wynk.subscription.service.IMsisdnIdentificationService;
import in.wynk.vas.client.dto.ImsiUserInfo;
import in.wynk.vas.client.service.VasClientService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Abhishek
 * @created 02/07/20
 */
@Service
public class ImsiMsisdnIdentificationService implements IMsisdnIdentificationService {

    @Autowired
    private VasClientService vasClientService;

    //TODO: let's hit the vas with multiple imsi request in one go and get the response.
    @Override
    public SourceIdentificationResponse identifyMsisdn(MsisdnIdentificationRequest msisdnIdentificationRequest) {
        SourceIdentificationResponseBuilder responseBuilder = SourceIdentificationResponse.builder();
        if(CollectionUtils.isNotEmpty(msisdnIdentificationRequest.getImsi())) {
            for (String imsi : msisdnIdentificationRequest.getImsi()) {
                if(StringUtils.isNotBlank(imsi)) {
                    ImsiUserInfo imsiUserInfo = vasClientService.imsiUserInfo(imsi);
                    if (imsiUserInfo != null && StringUtils.isNotBlank(imsiUserInfo.getMsisdn())) {
                        responseBuilder.msisdn(MsisdnUtils.normalizePhoneNumber(imsiUserInfo.getMsisdn())).loginChannel(LoginChannel.IMSI);
                    }
                }
            }
        }
        return responseBuilder.build();
    }
}
