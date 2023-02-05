package in.wynk.subscription.scheduler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.google.gson.Gson;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.service.IMongoDbCollectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static in.wynk.logging.BaseLoggingMarkers.MONGO_ERROR;
import static in.wynk.subscription.core.constants.SubscriptionLoggingMarkers.AMAZON_SERVICE_ERROR;
import static in.wynk.subscription.core.constants.SubscriptionLoggingMarkers.SDK_CLIENT_ERROR;

@Slf4j
@Service
public class SubscriptionMongoDumpService {

    public static final String MONGO_COLLECTIONS = "mongo_dump/";

    public static final String MONGO_COLLECTIONS_OFFER = "/offer.json";
    public static final String MONGO_COLLECTIONS_OFFERMAP = "/offerMap.json";
    public static final String MONGO_COLLECTIONS_PLAN = "/plan.json";
    public static final String MONGO_COLLECTIONS_PRODUCT = "/product.json";
    public static final String MONGO_COLLECTIONS_PARTNER = "/partner.json";


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${subscription.mongodb.s3.bucket}")
    private String bucket;

    @Autowired
    private IMongoDbCollectionsService mongoDbCollectionsServiceImpl;

    @Autowired
    private Gson gson;

    public void startMongoS3Export() {
        log.info("Starting subscription s3 export!!");
        Calendar cal = Calendar.getInstance();
        putMongoDataOnS3(cal);
        log.info("Done for today",cal);
    }

    private void putMongoDataOnS3(Calendar cal) {
        MongoDbCollections mongoDbCollections = MongoDbCollections.builder().build();
        try {
            mongoDbCollections = getMongoDBCollections();
        } catch(Exception ex) {
            log.error(MONGO_ERROR,"Unable to load Mongo collections "+ ex.getMessage());
            return;
        }
        try {
            putOffersOnS3Bucket(mongoDbCollections.getOffers(), cal);
            putOfferMapOnS3Bucket(mongoDbCollections.getOfferMaps(), cal);
            putPlansOnS3Bucket(mongoDbCollections.getPlans(), cal);
            putProductsOnS3Bucket(mongoDbCollections.getProducts(), cal);
            putPartnersOnS3Bucket(mongoDbCollections.getPartners(), cal);
        } catch(AmazonServiceException ex) {
            // amazons3 could not process
            log.error(AMAZON_SERVICE_ERROR,"AmazonServiceException "+ ex.getErrorMessage());
        } catch(SdkClientException e) {
            // amazon s3 could not be contacted
            log.error(SDK_CLIENT_ERROR,"SdkClientException "+e.getMessage());
        }
    }

    private void putOffersOnS3Bucket(List<Offer> offers,Calendar cal) {
        if(!offers.isEmpty()) {
        String fileName = MONGO_COLLECTIONS + dateFormat.format(cal.getTime()) + MONGO_COLLECTIONS_OFFER;
        String collectionDump = gson.toJson(offers);
        log.info("Putting offers on S3");
        putObjectOnAmazonS3(fileName,collectionDump);
        }
    }

    private void putOfferMapOnS3Bucket(List<OfferMap> offerMaps, Calendar cal) {
        if(!offerMaps.isEmpty()) {
            String fileName = MONGO_COLLECTIONS + dateFormat.format(cal.getTime()) + MONGO_COLLECTIONS_OFFERMAP;
            String collectionDump = gson.toJson(offerMaps);
            log.info("Putting offer maps on S3");
            putObjectOnAmazonS3(fileName, collectionDump);
        }
    }

    private void putPlansOnS3Bucket(List<Plan> plans, Calendar cal) {
        if(!plans.isEmpty()) {
            String fileName = MONGO_COLLECTIONS + dateFormat.format(cal.getTime()) + MONGO_COLLECTIONS_PLAN;
            String collectionDump = gson.toJson(plans);
            log.info("Putting plans on S3");
            putObjectOnAmazonS3(fileName, collectionDump);
        }
    }

    private void putProductsOnS3Bucket(List<Product> products, Calendar cal) {
        if(!products.isEmpty()) {
            String fileName = MONGO_COLLECTIONS + dateFormat.format(cal.getTime()) + MONGO_COLLECTIONS_PRODUCT;
            String collectionDump = gson.toJson(products);
            log.info("Putting products on S3");
            putObjectOnAmazonS3(fileName, collectionDump);
        }
    }

    private void putPartnersOnS3Bucket(List<Partner> partners,Calendar cal) {
        if(!partners.isEmpty()) {
            String fileName = MONGO_COLLECTIONS + dateFormat.format(cal.getTime()) + MONGO_COLLECTIONS_PARTNER;
            String collectionDump = gson.toJson(partners);
            log.info("Putting partners on S3");
            putObjectOnAmazonS3(fileName, collectionDump);
        }
    }

    private void putObjectOnAmazonS3(String fileName, String object) {
        try {
            amazonS3Client.putObject(bucket,fileName,object);
        } catch(Exception ex) {
            log.error(AMAZON_SERVICE_ERROR,"Amazon error occurred "+ ex.getMessage());
            throw ex;
        }
    }

    private MongoDbCollections getMongoDBCollections() {
        return mongoDbCollectionsServiceImpl.populateMongoDbCollections();
    }
}
