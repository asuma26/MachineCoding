package in.wynk.subscription.core.dao.repository.filterUsers;

import in.wynk.subscription.core.dao.entity.FilterUserDetails;

public interface IFilterDbDao {
    FilterUserDetails getFilterUserDetails(String msisdn, String collectionName);
    void upsertFilterUserDetails(String msisdn, String collectionName);
}
