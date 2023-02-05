package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "SubscriptionPack")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionPack implements Serializable {

    @Id
    private Integer id;
    private Double price;
    private String paymentMethod;
    private Integer partnerProductId;

}
