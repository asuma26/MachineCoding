package in.wynk.subscription.core.dao.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReminderMessage extends Message {

    private int duration;
    private TimeUnit timeUnit;
    private boolean sms;
    private boolean appNotification;


    public static final class Builder {
        private String message;
        private boolean enabled;
        private int duration;
        private TimeUnit timeUnit;
        private boolean sms;
        private boolean appNotification;

        public Builder() {
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder sms(boolean sms) {
            this.sms = sms;
            return this;
        }

        public Builder appNotification(boolean appNotification) {
            this.appNotification = appNotification;
            return this;
        }

        public ReminderMessage build() {
            ReminderMessage reminderMessage = new ReminderMessage();
            reminderMessage.setMessage(this.message);
            reminderMessage.duration = this.duration;
            reminderMessage.timeUnit = this.timeUnit;
            reminderMessage.setEnabled(this.enabled);
            reminderMessage.sms = this.sms;
            reminderMessage.appNotification = this.appNotification;
            return reminderMessage;
        }
    }
}
