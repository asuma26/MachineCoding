package in.wynk.subscription.test.utils;

import in.wynk.common.dto.SessionDTO;
import in.wynk.common.enums.AppId;
import in.wynk.common.enums.Os;
import in.wynk.common.enums.PaymentEvent;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.data.enums.State;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.dto.OfferProvisionRequest;
import in.wynk.subscription.dto.SubscriptionProvisionRequest;
import in.wynk.subscription.dto.SubscriptionUnProvisionRequest;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static in.wynk.common.constant.BaseConstants.*;
import static in.wynk.subscription.test.migration.MigrationTestData.MSISDN;

public class SubscriptionTestUtils {

    private static final String DUMMY_MSISDN = "1111111105";
    public static final String DUMMY_UID = MsisdnUtils.getUidFromMsisdn(DUMMY_MSISDN);
    public static final String DUMMY_PACK_GROUP = "wynk_erosnow";
    public static final String DUMMY_DEVICE_ID = "dummy_device";
    public static final int DUMMY_BUILD_NO = 13000;
    public static final String DUMMY_APP_VERSION = "1.1.1.1";
    public static final List<String> IMSI_LIST = new ArrayList<>();


    public static Product dummyProduct() {
        return Product.builder().cpName("EROSNOW").hierarchy(10).state(State.ACTIVE).packGroup("wynk_eros").id(10001).build();
    }

    public static Plan dummyPlan() {
        Price price = Price.builder().amount(10).cur("INR").monthlyAmount(5).savings("Save 20%").bestPlan(true).build();
        Period period = Period.builder().endDate(1650000000).grace(3).preReminder(-3).retryInterval(1).suspension(5).timeUnit(TimeUnit.DAYS).validity(30).build();
        Map<String, String> paymentProductIds = new HashMap<>();
        paymentProductIds.put("itunes", "10000");
        paymentProductIds.put("google_wallet", "1222");
        Map<String, Object> meta = new HashMap<>();
        meta.put("img", "https://dummy.com/img.png");
        return Plan.builder().hierarchy(100).id(1000365).linkedOfferId(1000)
                .sku(paymentProductIds).service(WynkService.AIRTEL_TV.name())
                .meta(meta).state(State.ACTIVE).price(price).period(period).build();
    }

    public static Offer dummyOffer() {
        Message message = new Message.Builder().message("test").enabled(true).build();
        Map<String, String> products = new HashMap<>();
        products.put("10001", "wynk_eros");

        ReminderMessage reminderMessage = new ReminderMessage.Builder().message("reminder").enabled(true).sms(true)
                .duration(-1).timeUnit(TimeUnit.DAYS).build();
        Messages messages = Messages.builder().activation(message).deactivation(message)
                .reminder(Collections.singletonList(reminderMessage)).build();

        return Offer.builder().description("description").title("title").hierarchy(100).id(1000)
                .messages(messages).packGroup("wynk_eros").products(products).plans(Collections.singletonList(1000365))
                .provisionType(ProvisionType.FREE).ruleExpression("isUserInSegment('ATVPLUS')").service(WynkService.AIRTEL_TV.name()).state(State.ACTIVE).build();

    }

    public static SessionDTO dummyAtvSession(String msisdn) {
        Map<String, Object> map = new HashMap<>();
        map.put(MSISDN, msisdn);
        map.put(UID, MsisdnUtils.getUidFromMsisdn(msisdn));
        map.put(DEVICE_ID, DUMMY_DEVICE_ID);
        map.put(SERVICE, WynkService.AIRTEL_TV.getValue());
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setSessionPayload(map);
        return sessionDTO;
    }

    public static Partner dummyPartner() {
        return Partner.builder().colorCode("abcf").description("Description").id("EROSNOW")
                .logo("https://dummy.com/img.png").packGroup("wynk_eros").name("EROSNOW").state(State.ACTIVE).build();
    }

    public static Partner partner(String cp) {
        return Partner.builder().colorCode("linear-gradient(180deg, #932ba1 0%, #661570 100%)").description("Description")
                .id(cp).icon("https://image.airtel.tv/grandslam/content/2020_7_2/hooq-icon.svg")
                .logo("https://image.airtel.tv/grandslam/content/2020_7_2/hooq-logo.png")
                .packGroup("wynk_"+cp.toLowerCase()).name(cp).state(State.ACTIVE).build();
    }

    public static Partner partner(String cp, String packGroup) {
        return Partner.builder().colorCode("linear-gradient(180deg, #932ba1 0%, #661570 100%)").description("Description")
                .id(cp).icon("https://image.airtel.tv/grandslam/content/2020_7_2/hooq-icon.svg")
                .logo("https://image.airtel.tv/grandslam/content/2020_7_2/hooq-logo.png")
                .packGroup(packGroup).name(cp).service(WynkService.AIRTEL_TV.getValue()).state(State.ACTIVE).build();
    }

    public static OfferProvisionRequest dummyATVOfferProvisionRequest(String msisdn) {
        return dummyATVOfferProvisionRequest(msisdn, DUMMY_DEVICE_ID);
    }

    public static OfferProvisionRequest dummyATVOfferProvisionRequest(String msisdn, String deviceId) {
        return dummyATVOfferProvisionRequest(msisdn, deviceId, AppId.MOBILITY);
    }

    public static OfferProvisionRequest dummyATVOfferProvisionRequest(String msisdn, AppId appId) {
        return dummyATVOfferProvisionRequest(msisdn, DUMMY_DEVICE_ID, appId);
    }

    public static OfferProvisionRequest dummyATVOfferProvisionRequest(String msisdn, String deviceId, AppId appId) {
        return OfferProvisionRequest.builder().service(WynkService.AIRTEL_TV.getValue()).appId(appId.name()).appVersion(DUMMY_APP_VERSION).buildNo(DUMMY_BUILD_NO)
                .uid(MsisdnUtils.getUidFromMsisdn(msisdn)).msisdn(MsisdnUtils.normalizePhoneNumber(msisdn)).deviceId(deviceId).os(Os.ANDROID.getValue()).build();
    }

    public static OfferProvisionRequest dummyMusicOfferProvisionRequest(String msisdn) {
        return OfferProvisionRequest.builder().service(WynkService.MUSIC.getValue()).appId(AppId.MOBILITY.name()).appVersion(DUMMY_APP_VERSION).buildNo(DUMMY_BUILD_NO)
                .uid(MsisdnUtils.getUidFromMsisdn(msisdn)).msisdn(MsisdnUtils.normalizePhoneNumber(msisdn)).deviceId(DUMMY_DEVICE_ID).os(Os.ANDROID.getValue()).build();
    }

    public static MsisdnIdentificationRequest dummyAtvMobilityIdentificationRequest(String headerMsisdn) {
        return MsisdnIdentificationRequest.builder().appId(AppId.MOBILITY.getValue())
                .appVersion(DUMMY_APP_VERSION).buildNo(DUMMY_BUILD_NO).deviceId(DUMMY_DEVICE_ID).xMsisdn(headerMsisdn)
                .os(Os.ANDROID.getValue()).service(WynkService.AIRTEL_TV.getValue()).imsi(IMSI_LIST).build();
    }

    public static MsisdnIdentificationRequest dummyAtvDTHIdentificationRequest(String headerMsisdn) {
        return MsisdnIdentificationRequest.builder().appId(AppId.MOBILITY.getValue())
                .appVersion(DUMMY_APP_VERSION).buildNo(DUMMY_BUILD_NO).deviceId(DUMMY_DEVICE_ID).dthCustID("3027945468")
                .os(Os.ANDROID.getValue()).service(WynkService.AIRTEL_TV.getValue()).imsi(IMSI_LIST).build();
    }

    public static SubscriptionProvisionRequest dummySubscriptionProvisioningRequest(PaymentEvent event, String txnId, WynkService service) {
        Map<Plan, List<Product>> planToProducts = new HashMap<>();
        planToProducts.put(SubscriptionTestUtils.dummyPlan(), Collections.singletonList(SubscriptionTestUtils.dummyProduct()));
        return SubscriptionProvisionRequest.builder().msisdn(DUMMY_MSISDN).uid(DUMMY_UID).referenceId(txnId).autoRenew(event == PaymentEvent.SUBSCRIBE).service(service.getValue()).paymentMethod("free").activeSubscriptions(Collections.EMPTY_LIST).build();
    }

    public static SubscriptionUnProvisionRequest dummySubscriptionUnProvisioningRequest(int planId, WynkService service) {
        return SubscriptionUnProvisionRequest.builder().planId(planId).uid(DUMMY_UID).service(service.getValue()).build();
    }

    public static Product product(int id, String cpName, String packGroup, List<String> eligibleAppIds, String service) {
        return Product.builder().packGroup(packGroup).cpName(cpName).id(id).hierarchy(id % 10)
                .eligibleAppIds(eligibleAppIds).service(service).title(cpName).state(State.ACTIVE).build();
    }

}
