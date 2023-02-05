package in.wynk.utils.domain;

import com.google.gson.annotations.Expose;

import java.util.concurrent.TimeUnit;

@Deprecated
public class PackPeriod {

    @Expose
    private final int validity;
    private final int preReminder;
    private final int grace;
    private final int suspension;
    private final int retryInterval;
    private final Long packEndDate;
    @Expose
    private final TimeUnit timeUnit;
    private final Integer validBillingCycles;

    public static PackPeriod newSubscriptionPeriod(int validity, int preReminder, int grace, int suspension, int retryInterval, Long packEndDate, TimeUnit timeUnit, Integer validBillingCycles) {
        return new PackPeriod(validity, preReminder, grace, suspension, retryInterval, packEndDate, timeUnit, validBillingCycles);
    }

    public Long getValidityDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(validity, timeUnit);

    }

    public Long getPreReminderDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(preReminder, timeUnit);

    }

    public Long getGraceDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(grace, timeUnit);

    }

    public Long getSuspensionDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(suspension, timeUnit);

    }

    public Long getRetryIntervalDurationInMillis() {
        return TimeUnit.MILLISECONDS.convert(retryInterval, timeUnit);
    }

    public int getValidityInDays() {
        return ((Long) TimeUnit.DAYS.convert(validity, timeUnit)).intValue();

    }

    public int getPreReminderInDays() {
        return ((Long) TimeUnit.DAYS.convert(preReminder, timeUnit)).intValue();

    }

    public int getGraceInDays() {
        return ((Long) TimeUnit.DAYS.convert(grace, timeUnit)).intValue();

    }

    public int getSuspensionInDays() {
        return ((Long) TimeUnit.DAYS.convert(suspension, timeUnit)).intValue();

    }

    public int getRetryIntervalInDays() {
        return ((Long) TimeUnit.DAYS.convert(retryInterval, timeUnit)).intValue();

    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getValidity() {
        return validity;
    }

    public int getPreReminder() {
        return preReminder;
    }

    public int getGrace() {
        return grace;
    }

    public int getSuspension() {
        return suspension;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public Long getPackEndDate() {
        return packEndDate;
    }

    public Integer getValidBillingCycles() {
        return validBillingCycles == null? 1 : validBillingCycles;
    }

    private PackPeriod() {
        validity = 0;
        preReminder = 0;
        grace = 0;
        suspension = 0;
        timeUnit = null;
        retryInterval = 0;
        packEndDate = null;
        validBillingCycles = null;
    }

    private PackPeriod(int validity, int preReminder, int grace, int suspension, int retryInterval, Long packEndDate, TimeUnit timeUnit, Integer validBillingCycles) {
        super();
        this.validity = validity;
        this.preReminder = preReminder;
        this.grace = grace;
        this.suspension = suspension;
        this.timeUnit = timeUnit;
        this.retryInterval = retryInterval;
        this.packEndDate = packEndDate;
        this.validBillingCycles = validBillingCycles;
    }

    @Override
    public String toString() {
        return "[validity=" + validity + ", preReminder=" + preReminder + ", grace=" + grace + ", suspension=" + suspension + ", retryInterval=" + retryInterval + ", packEndDate=" + packEndDate
                + ", timeUnit=" + timeUnit + "]";
    }

}