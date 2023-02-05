
package in.wynk.subscription.core.dao.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class Messages {

    private Message activation;
    private Message deactivation;
    private Message renewal;
    private Message download;
    private List<ReminderMessage> reminder;


}
