package in.wynk.subscription.service.impl;

import in.wynk.common.utils.MsisdnUtils;
import in.wynk.subscription.dto.SourceIdentificationResponse;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.enums.LoginChannel;
import in.wynk.subscription.service.IMsisdnIdentificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Abhishek
 * @created 02/07/20
 */
@Service
public class HeaderMsisdnIdentificationService implements IMsisdnIdentificationService {
    @Override
    public SourceIdentificationResponse identifyMsisdn(MsisdnIdentificationRequest msisdnIdentificationRequest) {
        SourceIdentificationResponse.SourceIdentificationResponseBuilder responseBuilder = SourceIdentificationResponse.builder();
        if(StringUtils.isNotBlank(msisdnIdentificationRequest.getxMsisdn())) {
            responseBuilder.msisdn(MsisdnUtils.normalizePhoneNumber(msisdnIdentificationRequest.getxMsisdn())).loginChannel(LoginChannel.HEADER_ENRICHMENT);
        }
        return responseBuilder.build();
    }
}
