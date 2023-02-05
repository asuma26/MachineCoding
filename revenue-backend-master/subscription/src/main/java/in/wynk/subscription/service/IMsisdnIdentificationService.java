package in.wynk.subscription.service;

import in.wynk.subscription.dto.SourceIdentificationResponse;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;

/**
 * @author Abhishek
 * @created 02/07/20
 */
public interface IMsisdnIdentificationService {
    SourceIdentificationResponse identifyMsisdn(MsisdnIdentificationRequest msisdnIdentificationRequest);
}
