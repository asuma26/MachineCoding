package in.wynk.subscription.core.dao.repository.subscription;


import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends MongoRepository<Product, Integer> {

    Page<Product> findAllByState(State state, Pageable pageable);

    List<Product> findAllByState(State state);
}
