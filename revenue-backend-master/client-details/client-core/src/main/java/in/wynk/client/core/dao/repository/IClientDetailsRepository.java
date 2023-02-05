package in.wynk.client.core.dao.repository;

import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.data.enums.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientDetailsRepository extends MongoRepository<ClientDetails, String> {

    ClientDetails findByIdAndState(String id, State state);

}
