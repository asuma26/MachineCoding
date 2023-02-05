package in.wynk.utils.service.subscription;

import in.wynk.data.enums.State;
import in.wynk.subscription.core.dao.entity.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductsService {

    Product save(Product product);

    Product update(Product product);

    void switchState(Integer id, State state);

    Product find(Integer id);

    List<Product> findAll(Pageable pageable);

}
