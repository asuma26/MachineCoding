package in.wynk.utils.domain;

import java.util.List;

@Deprecated
public class PackMessages {

    private String                activationMessage;
    private String                deactivationMessage;
    private String                renewalMessage;
    private String                downloadMessage;
    private boolean               sendActivationNotification;
    private boolean               sendDeactivationNotification;
    private boolean               sendRenewalNotification;

    private List<ReminderMessage> reminderMessages;

    public String getActivationMessage() {
        return activationMessage;
    }

    public String getDeactivationMessage() {
        return deactivationMessage;
    }

    public String getRenewalMessage() {
        return renewalMessage;
    }

    public String getDownloadMessage() {
        return downloadMessage;
    }

    public List<ReminderMessage> getReminderMessages() {
        return reminderMessages;
    }

    public void setActivationMessage(String activationMessage) {
        this.activationMessage = activationMessage;
    }

    public void setDeactivationMessage(String deactivationMessage) {
        this.deactivationMessage = deactivationMessage;
    }

    public void setRenewalMessage(String renewalMessage) {
        this.renewalMessage = renewalMessage;
    }

    public void setDownloadMessage(String downloadMessage) {
        this.downloadMessage = downloadMessage;
    }

    public void setReminderMessages(List<ReminderMessage> reminderMessages) {
        this.reminderMessages = reminderMessages;
    }

    public boolean isSendActivationNotification() {
        return sendActivationNotification;
    }

    public void setSendActivationNotification(boolean sendActivationNotification) {
        this.sendActivationNotification = sendActivationNotification;
    }

    public boolean isSendDeactivationNotification() {
        return sendDeactivationNotification;
    }

    public void setSendDeactivationNotification(boolean sendDeactivationNotification) {
        this.sendDeactivationNotification = sendDeactivationNotification;
    }

    public boolean isSendRenewalNotification() {
        return sendRenewalNotification;
    }

    public void setSendRenewalNotification(boolean sendRenewalNotification) {
        this.sendRenewalNotification = sendRenewalNotification;
    }

    @Override
    public String toString() {
        return "PackMessages [activationMessage=" + activationMessage + ", deactivationMessage=" + deactivationMessage
                + ", renewalMessage=" + renewalMessage + ", downloadMessage=" + downloadMessage
                + ", sendActivationNotification=" + sendActivationNotification + ", sendDeactivationNotification="
                + sendDeactivationNotification + ", sendRenewalNotification=" + sendRenewalNotification
                + ", reminderMessages=" + reminderMessages + "]";
    }


}