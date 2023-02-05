package in.wynk.subscription.core.dao.repository.filterUsers;

import in.wynk.subscription.core.dao.entity.FilterUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class FilterDbDao implements IFilterDbDao {

    @Qualifier("filterUsersMongoTemplate")
    @Autowired
    private MongoOperations filterUsersMongoTemplate;

    @Override
    public FilterUserDetails getFilterUserDetails(String msisdn, String collectionName) {
        return filterUsersMongoTemplate.findById(msisdn, FilterUserDetails.class, collectionName);
    }

    @Override
    public void upsertFilterUserDetails(String msisdn, String collectionName) {
        filterUsersMongoTemplate.save(FilterUserDetails.builder().msisdn(msisdn).build(), collectionName);
    }

}
