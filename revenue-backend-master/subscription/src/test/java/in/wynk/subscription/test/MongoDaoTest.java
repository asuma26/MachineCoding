package in.wynk.subscription.test;

import in.wynk.common.enums.WynkService;
import in.wynk.http.config.HttpClientConfig;
import in.wynk.subscription.SubscriptionApplication;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.dao.repository.sedb.SubscriptionPackDao;
import in.wynk.subscription.core.dao.repository.subscription.OfferDao;
import in.wynk.subscription.core.dao.repository.subscription.PartnerDao;
import in.wynk.subscription.core.dao.repository.subscription.PlanDao;
import in.wynk.subscription.core.dao.repository.subscription.ProductDao;
import in.wynk.subscription.test.utils.SubscriptionTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@SpringBootTest(classes = {SubscriptionApplication.class, HttpClientConfig.class})
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-subscription-core-local.properties")
public class MongoDaoTest {

    private static final String[] ELIGIBLE_APP_IDS = {"MOBILITY", "LARGESCREEN", "WEB"};
    private static final Map<String, String> partners = new LinkedHashMap<>();

    static {
        partners.put("EROSNOW", "wynk_eros");
        partners.put("SONYLIV", "wynk_sony_liv");
        partners.put("WYNk_MUSIC", "wynk_music");
        partners.put("HOICHOI", "wynk_hoichoi");
        partners.put("WYNK_BOOK", "juggernaut_books");
        partners.put("HUNGAMA", "wynk_hungama");
        partners.put("SHEMAROOME", "wynk_shemaroo");
        partners.put("ULTRA", "wynk_ultra");
        partners.put("CURIOSITYSTREAM", "wynk_curiositystream");
        partners.put("LIONSGATEPLAY", "wynk_lionsgateplay");
        partners.put("DEVILS_CIRCUIT", "wynk_devils_circuit");
        partners.put("CREATOR", "wynk_creator");
        partners.put("SILLYMONKS", "wynk_sillymonks");
        partners.put("SRIGANESHVIDEO", "wynk_sriganeshvideo");
        partners.put("KEYENTERTAINMENTS", "wynk_keyentertainments");
        partners.put("SRIBALAJIVIDEO", "wynk_sribalajivideo");
        partners.put("VOLGAVIDEOS", "wynk_volgavideos");
        partners.put("MILLENNIUMVIDEOS", "wynk_millenniumvideos");
        partners.put("WHACKEDOUTMEDIA", "wynk_whackedoutmedia");
        partners.put("NODWIN", "wynk_nodwin");
        partners.put("EDITORJIVOD", "wynk_editorjivod");
        partners.put("VOOT", "wynk_voot");
        partners.put("DIVO", "wynk_divo");
        partners.put("THEQYOU", "wynk_theqyou");
        partners.put("MWTV", "wynk_livetv");
    }

    @Autowired
    private ProductDao productDao;
    @Autowired
    private PlanDao planDao;
    @Autowired
    private OfferDao offerDao;
    @Autowired
    private PartnerDao partnerDao;
    @Autowired
    private SubscriptionPackDao subscriptionPackDao;

    @Test
    public void getSubscriptionPack() {
        List<SubscriptionPack> subscriptionPacks = subscriptionPackDao.findAll();
        HashMap<Integer, List<String>> hashMap = new HashMap<>();
        for (SubscriptionPack subscriptionPack : subscriptionPacks) {
            hashMap.put(subscriptionPack.getId(), new ArrayList<>());
        }
        for (SubscriptionPack subscriptionPack : subscriptionPacks) {
            List<String> paymentMethods = hashMap.get(subscriptionPack.getId());
            paymentMethods.add(subscriptionPack.getPaymentMethod());
            hashMap.put(subscriptionPack.getId(), paymentMethods);
        }
        System.out.println(hashMap.toString());
    }

    @Test
    public void insertProduct() {
        Product product = SubscriptionTestUtils.dummyProduct();
        productDao.save(product);
    }

    @Test
    public void findSubscription() {
        List<Product> productList = productDao.findAll();
        assert productList.size() > 0;
    }

    @Test
    public void insertPlan() {
        Plan plan = SubscriptionTestUtils.dummyPlan();
        planDao.save(plan);
    }

    @Test
    public void findPlanTest() {
        List<Plan> plans = planDao.findAll();
        assert plans.size() > 0;
    }

    @Test
    public void insertOfferTest() {
        Offer offer = SubscriptionTestUtils.dummyOffer();
        Offer offer1 = offerDao.save(offer);
        Assert.assertTrue(offer1.getId() > 0);
    }

    @Test
    public void findOfferTest() {
        List<Offer> offers = offerDao.findAll();
        assert offers.size() > 0;
    }

    @Test
    public void insertPartnerTest() {
        Partner partner = SubscriptionTestUtils.dummyPartner();
        partnerDao.insert(partner);
    }

    @Test
    public void findPartnerTest() {
        List<Partner> partners = partnerDao.findAll();
        assert partners.size() > 0;
    }

    @Test
    public void insertPartners() {
        for (Map.Entry<String, String> entry : partners.entrySet()) {
            Partner partner = SubscriptionTestUtils.partner(entry.getKey(), entry.getValue());
            partnerDao.save(partner);
        }
    }

    @Test
    public void createProducts() {
        int i = 1;
        for (Map.Entry<String, String> entry : partners.entrySet()) {
            for (int j = 1; j <= 3; j++) {
                String service = entry.getKey().contains("music") ? WynkService.MUSIC.getValue() : WynkService.AIRTEL_TV.getValue();
                Product product = SubscriptionTestUtils.product(i * 1000 + j, entry.getKey(), entry.getValue(), Arrays.asList(ELIGIBLE_APP_IDS), service);
                productDao.save(product);
            }
            i++;
        }
    }
}

