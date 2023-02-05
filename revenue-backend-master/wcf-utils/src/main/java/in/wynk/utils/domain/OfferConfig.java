package in.wynk.utils.domain;

import com.google.gson.annotations.Expose;
import in.wynk.utils.constant.OfferEligiblityAction;
import in.wynk.utils.constant.ProvisionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Deprecated
@Document(collection = "OfferConfigTemp")
public class OfferConfig {

    @Id
    @Expose
    private OfferConfigKey key;
    @Expose
    private String title;
    private String description;
    @Expose
    private String externalProductId;
    @Expose
    private ProvisionType provisionType;
    private String ruleExpression;
    private String internalRuleExpression;
    private String postEligibilityCheckRuleExpression;
    private String provisionRuleExpression;
    @Expose
    private Map<Integer, OfferEligiblityAction> packIds;
    private Map<String, String> meta;
    private PackMessages messages;
    @Expose
    private int hierarchy;
    @Expose
    private boolean isSystemOffer;
    private boolean skipFailureTransactionLog;
    private boolean addToThePreviousValidity;
    private Integer linkedOfferId;

    public OfferConfig() {
    }

    public static class OfferConfigKey {

        @Expose
        private int offerId;
        @Expose
        private String service;

        OfferConfigKey() {

        }

        public OfferConfigKey(int offerId, String service) {
            this.offerId = offerId;
            this.service = service;
        }

        public int getOfferId() {
            return offerId;
        }

        public String getService() {
            return service;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + offerId;
            result = prime * result + ((service == null) ? 0 : service.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            OfferConfigKey other = (OfferConfigKey) obj;
            if (offerId != other.offerId) {
                return false;
            }
            if (service == null) {
                return other.service == null;
            } else return service.equals(other.service);
        }

        @Override
        public String toString() {
            return "OfferConfigKey [offerId=" + offerId + ", service=" + service + "]";
        }


    }

    public OfferConfigKey getKey() {
        return key;
    }

    public void setKey(OfferConfigKey key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAddToThePreviousValidity() {
        return addToThePreviousValidity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    public ProvisionType getProvisionType() {
        return provisionType;
    }

    public void setProvisionType(ProvisionType provisionType) {
        this.provisionType = provisionType;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public String getInternalRuleExpression() {
        return internalRuleExpression;
    }

    public void setInternalRuleExpression(String internalRuleExpression) {
        this.internalRuleExpression = internalRuleExpression;
    }

    public String getPostEligibilityCheckRuleExpression() {
        return postEligibilityCheckRuleExpression;
    }

    public void setPostEligibilityCheckRuleExpression(String postEligibilityCheckRuleExpression) {
        this.postEligibilityCheckRuleExpression = postEligibilityCheckRuleExpression;
    }

    public String getProvisionRuleExpression() {
        return provisionRuleExpression;
    }

    public void setProvisionRuleExpression(String provisionRuleExpression) {
        this.provisionRuleExpression = provisionRuleExpression;
    }

    public Map<Integer, OfferEligiblityAction> getPackIds() {
        return packIds;
    }

    public List<Integer> getAutoProvisionPackIds() {
        List<Integer> listAutoProvision = new ArrayList<Integer>();
        if (packIds != null) {
            for (Entry<Integer, OfferEligiblityAction> e : packIds.entrySet()) {
                if (e.getValue() == OfferEligiblityAction.AUTO_PROVISION) {
                    listAutoProvision.add(e.getKey());
                }
            }
        }
        return listAutoProvision;
    }

    public void setPackIds(Map<Integer, OfferEligiblityAction> packIds) {
        this.packIds = packIds;
    }

    public PackMessages getMessages() {
        return messages;
    }

    public void setMessages(PackMessages messages) {
        this.messages = messages;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isSystemOffer() {
        return isSystemOffer;
    }

    public void setSystemOffer(boolean isSystemOffer) {
        this.isSystemOffer = isSystemOffer;
    }

    public boolean isSkipFailureTransactionLog() {
        return skipFailureTransactionLog;
    }

    public Integer getLinkedOfferId() {
        return linkedOfferId;
    }

    public void setLinkedOfferId(Integer linkedOfferId) {
        this.linkedOfferId = linkedOfferId;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public static class Builder {

        private OfferConfigKey key;
        private String title;
        private String description;
        private String externalProductId;
        private ProvisionType provisionType;
        private String ruleExpression;
        private String postEligibilityCheckRuleExpression;
        private String provisionRuleExpression;
        private Map<Integer, OfferEligiblityAction> packIds;
        private Map<String, String> meta;
        private PackMessages messages;
        private int hierarchy;
        private boolean isSystemOffer;
        private boolean skipFailureTransactionLog;
        private boolean addToThePreviousValidity;
        private Integer linkedOfferId;
        private List<String> platforms;

        public Builder isSystemOffer(boolean isSystemOffer) {
            this.isSystemOffer = isSystemOffer;
            return this;
        }

        public Builder addToThePreviousValidity(boolean addToThePreviousValidity) {
            this.addToThePreviousValidity = addToThePreviousValidity;
            return this;
        }

        public Builder skipFailureTransactionLog(boolean skipFailureTransactionLog) {
            this.skipFailureTransactionLog = skipFailureTransactionLog;
            return this;
        }

        public Builder key(OfferConfigKey key) {
            this.key = key;
            return this;
        }

        public Builder hierarchy(int hierarchy) {
            this.hierarchy = hierarchy;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder externalProductId(String externalProductId) {
            this.externalProductId = externalProductId;
            return this;
        }

        public Builder provisionType(ProvisionType provisionType) {
            this.provisionType = provisionType;
            return this;
        }

        public Builder ruleExpression(String ruleExpression) {
            this.ruleExpression = ruleExpression;
            return this;
        }

        public Builder postEligibilityCheckRuleExpression(String postEligibilityCheckRuleExpression) {
            this.postEligibilityCheckRuleExpression = postEligibilityCheckRuleExpression;
            return this;
        }

        public Builder provisionRuleExpression(String provisionRuleExpression) {
            this.provisionRuleExpression = provisionRuleExpression;
            return this;
        }

        public Builder packIds(Map<Integer, OfferEligiblityAction> packIds) {
            this.packIds = packIds;
            return this;
        }

        public Builder meta(Map<String, String> meta) {
            this.meta = meta;
            return this;
        }

        public Builder messages(PackMessages messages) {
            this.messages = messages;
            return this;
        }

        public Builder linkedOfferId(Integer linkedOfferId) {
            this.linkedOfferId = linkedOfferId;
            return this;
        }

        public Builder platforms(List<String> platforms) {
            this.platforms = platforms;
            return this;
        }

        public OfferConfig build() {
            return new OfferConfig(this);
        }
    }

    private OfferConfig(Builder builder) {
        this.key = builder.key;
        this.title = builder.title;
        this.description = builder.description;
        this.externalProductId = builder.externalProductId;
        this.provisionType = builder.provisionType;
        this.ruleExpression = builder.ruleExpression;
        this.postEligibilityCheckRuleExpression = builder.postEligibilityCheckRuleExpression;
        this.provisionRuleExpression = builder.provisionRuleExpression;
        this.packIds = builder.packIds;
        this.meta = builder.meta;
        this.messages = builder.messages;
        this.hierarchy = builder.hierarchy;
        this.isSystemOffer = builder.isSystemOffer;
        this.skipFailureTransactionLog = builder.skipFailureTransactionLog;
        this.addToThePreviousValidity = builder.addToThePreviousValidity;
        this.linkedOfferId = builder.linkedOfferId;
    }

    @Override
    public String toString() {
        return "Offer [key=" + key + ", title=" + title + ", description=" + description + ", externalProductId="
                + externalProductId + ", provisionType=" + provisionType + ", ruleExpression=" + ruleExpression
                + ", internalRuleExpression=" + internalRuleExpression + ", postEligibilityCheckRuleExpression="
                + postEligibilityCheckRuleExpression + ", provisionRuleExpression=" + provisionRuleExpression
                + ", packIds=" + packIds + ", meta=" + meta + ", messages=" + messages + ", hierarchy=" + hierarchy
                + ", isSystemOffer=" + isSystemOffer + ", skipFailureTransactionLog=" + skipFailureTransactionLog
                + ", addToThePreviousValidity=" + addToThePreviousValidity + ", linkedOfferId=" + linkedOfferId + "]";
    }


}
