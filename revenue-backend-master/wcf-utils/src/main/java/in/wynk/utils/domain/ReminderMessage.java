package in.wynk.utils.domain;

import java.util.concurrent.TimeUnit;

@Deprecated
public class ReminderMessage {

    private int      duration;

    private TimeUnit timeUnit;

    private String   message;

    private boolean  sendSMS;

    private boolean  sendNotifiction;

    public int getDuration() {
        return duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSendSMS() {
        return sendSMS;
    }

    public boolean isSendNotifiction() {
        return sendNotifiction;
    }

    public void setSendNotifiction(boolean sendNotifiction) {
        this.sendNotifiction = sendNotifiction;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSendSMS(boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    @Override
    public String toString() {
        return "ReminderMessage [duration=" + duration + ", timeUnit=" + timeUnit + ", message=" + message
                + ", sendSMS=" + sendSMS + ", sendNotifiction=" + sendNotifiction + "]";
    }


}