package in.wynk.subscription.service.impl;

import in.wynk.common.utils.MsisdnUtils;
import in.wynk.subscription.dto.SourceIdentificationResponse;
import in.wynk.subscription.dto.SourceIdentificationResponse.SourceIdentificationResponseBuilder;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.enums.LoginChannel;
import in.wynk.subscription.service.IMsisdnIdentificationService;
import in.wynk.vas.client.dto.DthUserInfo;
import in.wynk.vas.client.service.VasClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Abhishek
 * @created 02/07/20
 */
@Service
public class DthMsisdnIdentificationService implements IMsisdnIdentificationService {

    @Autowired
    private VasClientService vasClientService;

    @Override
    public SourceIdentificationResponse identifyMsisdn(MsisdnIdentificationRequest request) {
        SourceIdentificationResponseBuilder responseBuilder = SourceIdentificationResponse.builder();
        if(StringUtils.isNotBlank(request.getDthCustID())) {
            String dthCustId = request.getDthCustID();
            DthUserInfo dthUserInfo = vasClientService.dthUserInfo(dthCustId, null);
            if(Objects.nonNull(dthUserInfo) && StringUtils.isNotBlank(dthUserInfo.getRtn())) {
                String[] linkedNumbers = dthUserInfo.getRtn().split(",");
                for(String _msisdn : linkedNumbers) {
                    String rtn = MsisdnUtils.normalizePhoneNumber(_msisdn);
                    if(StringUtils.isNotBlank(rtn)) {
                        responseBuilder.msisdn(rtn).loginChannel(LoginChannel.DTH).build();
                        break;
                    }
                }
            }
        }
        return responseBuilder.build();
    }
}
