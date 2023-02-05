package in.wynk.sms.common.message;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.queue.dto.WynkQueue;
import in.wynk.sms.common.constant.Country;
import in.wynk.sms.common.constant.SMSPriority;
import lombok.*;

@Getter
@Builder
@ToString
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor
@WynkQueue(queueName = "${sms.notification.queue.name}", delaySeconds = "${sms.notification.queue.delayInSecond}")
public class SmsNotificationMessage {

    @Analysed
    private String service;
    @Analysed
    private String msisdn;
    @Analysed
    private String message;
    @Analysed
    private String priority;

    public Country getCountry() {
        return msisdn.contains(Country.SRILANKA.getCountryCode()) ? Country.SRILANKA : Country.INDIA;
    }

    public SMSPriority getPriority() {
        return SMSPriority.fromString(priority);
    }

}
