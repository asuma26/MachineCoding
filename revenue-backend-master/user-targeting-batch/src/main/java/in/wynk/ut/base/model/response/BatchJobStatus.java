package in.wynk.ut.base.model.response;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;

import java.util.Date;

public class BatchJobStatus {

    private long id;
    private long version;
    private String batchStatus;
    private Date startTime;
    private Date endTime;
    private Date createdTime;
    private Date lastUpdated;
    private ExitStatus exitStatus;
    private JobParameters jobParameters;

    public BatchJobStatus(long id, long version, String batchStatus, Date startTime, Date endTime, Date createdTime, Date lastUpdated, ExitStatus exitStatus, JobParameters jobParameters) {
        this.id = id;
        this.version = version;
        this.batchStatus = batchStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdTime = createdTime;
        this.lastUpdated = lastUpdated;
        this.exitStatus = exitStatus;
        this.jobParameters = jobParameters;
    }

    public static class BatchStatusBuilder {
        private long id;
        private long version;
        private String batchStatus;
        private Date startTime;
        private Date endTime;
        private Date createdTime;
        private Date lastUpdated;
        private ExitStatus exitStatus;
        private JobParameters jobParameters;

        public BatchJobStatus build() {
            return new BatchJobStatus(id, version, batchStatus, startTime, endTime, createdTime, lastUpdated, exitStatus, jobParameters);
        }

        public BatchStatusBuilder id(long id) {
            this.id = id;
            return this;
        }

        public BatchStatusBuilder version(long version) {
            this.version = version;
            return this;
        }

        public BatchStatusBuilder batchStatus(String batchStatus) {
            this.batchStatus = batchStatus;
            return this;
        }

        public BatchStatusBuilder startTime(Date startTime) {
            this.startTime = startTime;
            return this;
        }

        public BatchStatusBuilder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
        }

        public BatchStatusBuilder createdTime(Date createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public BatchStatusBuilder lastUpdated(Date lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public BatchStatusBuilder exitStatus(ExitStatus exitStatus) {
            this.exitStatus = exitStatus;
            return this;
        }

        public BatchStatusBuilder jobParameters(JobParameters jobParameters) {
            this.jobParameters = jobParameters;
            return this;
        }
    }

    public long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public String getBatchStatus() {
        return batchStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public ExitStatus getExitStatus() {
        return exitStatus;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }
}
