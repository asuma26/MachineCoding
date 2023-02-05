package in.wynk.subscription.service.impl;

import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.dao.repository.subscription.*;
import in.wynk.subscription.service.IMongoDbCollectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoDbCollectionsServiceImpl implements IMongoDbCollectionsService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OfferDao offerDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private PartnerDao partnerDao;

    @Autowired
    private OfferMapDao offerMapDao;

    @Override
    public MongoDbCollections populateMongoDbCollections() {
        return MongoDbCollections.builder().offerMaps(offerMapDao.findAll())
                .offers(offerDao.findAll())
                .partners(partnerDao.findAll())
                .plans(planDao.findAll())
                .products(productDao.findAll())
                .build();
    }
}
