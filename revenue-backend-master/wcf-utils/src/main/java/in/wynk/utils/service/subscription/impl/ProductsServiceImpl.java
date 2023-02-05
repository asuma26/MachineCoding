package in.wynk.utils.service.subscription.impl;

import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.core.dao.repository.subscription.ProductDao;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.subscription.IProductsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsServiceImpl implements IProductsService {

    private final ProductDao productsDao;

    public ProductsServiceImpl(ProductDao productsDao) {
        this.productsDao = productsDao;
    }

    @Override
    public Product save(Product product) {
        return productsDao.save(product);
    }

    @Override
    public Product update(Product product) {
        Product product1 = find(product.getId());
        return save(product);
    }

    @Override
    public void switchState(Integer id, State state) {
        Product product = find(id);
        product.setState(state);
        save(product);
    }

    @Override
    public Product find(Integer id) {
        return productsDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF018));
    }

    @Override
    public List<Product> findAll(Pageable pageable) {
        return productsDao.findAll(pageable).getContent();
    }
}
