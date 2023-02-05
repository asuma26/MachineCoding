package in.wynk.subscription.dto;

import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

/**
 * @author Abhishek
 * @created 07/07/20
 */
@Data
public class MsisdnIdentificationMeta {
    Map<String, SourceIdentificationResponse> allMsisdn = new ConcurrentHashMap<>();
    Map<String, Integer> msisdnOfferHierarchy = new ConcurrentHashMap<>();
    Map<String, MsisdnOperatorDetails> allMsisdnOperatorDetails = new ConcurrentHashMap<>();
}
