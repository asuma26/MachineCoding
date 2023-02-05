package in.wynk.targeting.core.dao.repository.mongo;

import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.constant.AdType;
import in.wynk.targeting.core.dao.entity.mongo.AdConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AdsConfigMongoRepository extends MongoRepository<AdConfig, String> {
    List<AdConfig> findAllByStateAndTypeIn(AdState adState, Collection<AdType> type);

    List<AdConfig> findAllByStateAndClientIdAndTypeIn(AdState adState, String clientId, Collection<AdType> type);

    List<AdConfig> findAllByStateAndClientId(AdState adState, String clientId);

}
