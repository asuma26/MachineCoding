package in.wynk.payment.dto.response.payu;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.wynk.payment.dto.payu.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PayUUserCardDetailsResponse {

  @JsonProperty("status")
  private int status;

  @JsonProperty("msg")
  private String message;

  @JsonProperty("user_cards")
  private Map<String, CardDetails> userCards;

  public Map<String, CardDetails> getUserCards(){
    if (userCards == null)
      userCards = new HashMap<>();
    return userCards;
  }
}
