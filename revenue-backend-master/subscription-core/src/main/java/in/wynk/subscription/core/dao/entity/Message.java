package in.wynk.subscription.core.dao.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private String priority;
    private String message;
    private boolean enabled;


    public static final class Builder {
        private String priority = "LOW";
        private String message;
        private boolean enabled;

        public Builder() {
        }

        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.message = this.message;
            message.enabled = this.enabled;
            message.priority = this.priority;
            return message;
        }
    }
}
