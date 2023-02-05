package in.wynk.ut.base.model.request;

import in.wynk.ut.base.constant.ErrorCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class BaseUploadRequest {

    @Pattern(regexp = "([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.csv)$", message = ErrorCode.FILE_NAME_ERROR)
    private String fileName;
    @NotBlank(message = ErrorCode.EMPTY_ADID)
    private String adid;
    @NotBlank(message = ErrorCode.EMPTY_INITIATOR)
    private String initiatedBy;
    @NotBlank(message = ErrorCode.EMPTY_TARGET)
    private Boolean targeted;
    private Long ttl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(Boolean targeted) {
        this.targeted = targeted;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
