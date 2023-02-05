package in.wynk.subscription.test.migration;

import in.wynk.subscription.core.dao.entity.*;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MigrationTestData {

    //Friday, 30 July 2021 11:34:27 GMT+05:30
    private static final long EXPIRES = 1627625067000L;
    private static final long TODAY = System.currentTimeMillis();
    @Getter
    private static final String UID = "zalKRzuDl81b_Av8T0";
    @Getter
    public static final String AIRTELTV = "airteltv";
    public static final int OLD_PRODUCT_ID = 30088;
    public static final String WYNK_ATV_BASE = "wynk_atv_base";
    public static final int OLD_PARTNER_PRODUCT_ID = 3008;
    public static final String PAYU = "payu";
    public static final int NEW_PLAN_ID = 1000182;
    public static final int NEW_OFFER_ID = 1100180;
    public static final String WYNK_EROSNOW = "wynk_erosnow";
    public static final String NEW_PRODUCT_ID = "16004";
    public static final String MSISDN = "msisdn";
    @Getter
    public static final String MSISDN_VALUE = "+919005334276";

    public static List<Subscription> getOneTimePaidSubscriptionsForMigration(){
        List<Subscription> subscriptions = new ArrayList<>();
        Map<String, String> meta = new HashMap<>();
        meta.put(MSISDN, MSISDN_VALUE);
        Subscription subscription = Subscription.builder()
                .validTillDate(new Date(EXPIRES))
                .unsubscriptionDate(new Date(EXPIRES))
                .uid(UID)
                .subStatus(null)
                .subscriptionInProgress(false)
                .subscriptionEndDate(new Date(EXPIRES))
                .subscriptionDate(new Date(TODAY))
                .service(AIRTELTV)
                .renewalUnderProcess(false)
                .productId(OLD_PRODUCT_ID)
                .active(true)
                .autoRenewalOff(true)
                .deactivationChannel(null)
                .nextChargingDate(null)
                .packGroup(WYNK_ATV_BASE)
                .partnerProductId(OLD_PARTNER_PRODUCT_ID)
                .paymentMetaData(meta)
                .paymentMethod(PAYU)
                .build();
        subscriptions.add(subscription);
        return subscriptions;
    }

    public static List<Subscription> getRecurringPaidSubscriptionsForMigration(){
        List<Subscription> subscriptions = new ArrayList<>();
        Map<String, String> meta = new HashMap<>();
        meta.put(MSISDN, MSISDN_VALUE);
        Subscription subscription = Subscription.builder()
                .validTillDate(new Date(EXPIRES))
                .unsubscriptionDate(new Date(EXPIRES))
                .uid(UID)
                .subStatus(null)
                .subscriptionInProgress(false)
                .subscriptionEndDate(new Date(EXPIRES))
                .subscriptionDate(new Date(TODAY))
                .service(AIRTELTV)
                .renewalUnderProcess(false)
                .productId(OLD_PRODUCT_ID)
                .active(true)
                .autoRenewalOff(false)
                .deactivationChannel(null)
                .nextChargingDate(new Date(TODAY))
                .packGroup(WYNK_ATV_BASE)
                .partnerProductId(OLD_PARTNER_PRODUCT_ID)
                .paymentMetaData(meta)
                .paymentMethod(PAYU)
                .build();
        subscriptions.add(subscription);
        return subscriptions;
    }

    public static List<Subscription> getPayuRecurringPaidSubscriptionForMigration(){
        List<Subscription> subscriptions = new ArrayList<>();
        Map<String, String> meta = new HashMap<>();
        meta.put("user_credentials", "aU2Uoi:B8NClsZs5cnDYbsHS0");
        meta.put("cardToken", "2aec9d61ceb65918965823");
        meta.put("subsId", "11734910047");
        meta.put("msisdn", "+918887528761");
        meta.put("transactionid", "b10fcb80348111ebabff15aacd7d155e1111");
        Subscription subscription = Subscription.builder()
                .validTillDate(new Date(EXPIRES))
                .unsubscriptionDate(new Date(EXPIRES))
                .uid("B8NClsZs5cnDYbsHS01")
                .subStatus(null)
                .subscriptionInProgress(false)
                .subscriptionEndDate(new Date(EXPIRES))
                .subscriptionDate(new Date(TODAY))
                .service(AIRTELTV)
                .renewalUnderProcess(false)
                .productId(OLD_PRODUCT_ID)
                .active(true)
                .autoRenewalOff(false)
                .deactivationChannel(null)
                .nextChargingDate(new Date(TODAY))
                .packGroup(WYNK_ATV_BASE)
                .partnerProductId(OLD_PARTNER_PRODUCT_ID)
                .paymentMetaData(meta)
                .paymentMethod(PAYU)
                .build();
        subscriptions.add(subscription);
        return subscriptions;
    }

    public static List<UserPlanDetails> getEmptyUserPlanDetails() {
        List<UserPlanDetails> userPlanDetailsList = new ArrayList<>();
        return userPlanDetailsList;
    }

    public static List<UserPlanDetails> getPartialUserPlanDetails() {
        UserPlanDetails userPlanDetails = UserPlanDetails.builder()
                .unsubscribeOn(new Date(EXPIRES))
                .uid(UID)
                .startDate(new Date(TODAY))
                .service(AIRTELTV)
                .referenceId(null)
                .planType(null)
                .planId(2001)
                .paymentCode(null)
                .paymentChannel(null)
                .offerId(1001)
                .meta(null)
                .endDate(new Date(EXPIRES))
                .autoRenew(false)
                .build();
        List<UserPlanDetails> userPlanDetailsList = getEmptyUserPlanDetails();
        userPlanDetailsList.add(userPlanDetails);
        return userPlanDetailsList;
    }

    public static List<UserPlanDetails> getMigratedUserPlanDetails() {
        UserPlanDetails userPlanDetails = UserPlanDetails.builder()
                .unsubscribeOn(new Date(EXPIRES))
                .uid(UID)
                .startDate(new Date(TODAY))
                .service(AIRTELTV)
                .referenceId(null)
                .planType(null)
                .planId(2002)
                .paymentCode(null)
                .paymentChannel(null)
                .offerId(1002)
                .meta(null)
                .endDate(new Date(EXPIRES))
                .autoRenew(false)
                .build();
        List<UserPlanDetails> userPlanDetailsList = getPartialUserPlanDetails();
        userPlanDetailsList.add(userPlanDetails);
        return userPlanDetailsList;
    }

    public static List<OfferMsisdnMapping> getOfferMsisdnMappings() {
        List<OfferMsisdnMapping> list = new ArrayList<>();
        OfferMsisdnMapping offerMsisdnMapping1 = OfferMsisdnMapping.builder()
                .tid(null)
                .service(AIRTELTV)
                .offerId(101)
                .msisdn(MSISDN_VALUE)
                .id(null)
                .channel(null)
                .build();
        OfferMsisdnMapping offerMsisdnMapping2 = OfferMsisdnMapping.builder()
                .tid(null)
                .service(AIRTELTV)
                .offerId(102)
                .msisdn(MSISDN_VALUE)
                .id(null)
                .channel(null)
                .build();
        list.add(offerMsisdnMapping1);
        list.add(offerMsisdnMapping2);
        list.add(offerMsisdnMapping1);
        return list;
    }

    public static Map<String, Map<Integer, OfferPlanMapping>> getOldOfferIdToNewOfferPlanMapping() {
        Map<String, Map<Integer, OfferPlanMapping>> map = new HashMap<>();
        Map<Integer, OfferPlanMapping> map1 = new HashMap<>();
        map1.put(101, OfferPlanMapping.builder().newOfferId(1001).newPlanId(2001).build());
        map1.put(102, OfferPlanMapping.builder().newOfferId(1002).newPlanId(2002).build());
        map.put(AIRTELTV, map1);
        return map;
    }

    public static Map<Integer, Integer> getOldPackToNewPlanMapping() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(OLD_PARTNER_PRODUCT_ID, NEW_PLAN_ID);
        return map;
    }

    public static Plan getNewPlan() {
        Period period = Period.builder().grace(2).retryInterval(1).timeUnit(TimeUnit.DAYS).build();
        return Plan.builder()
                .id(NEW_PLAN_ID).period(period).price(Price.builder().amount(1).build())
                .linkedOfferId(NEW_OFFER_ID)
                .build();
    }

    public static Map<Integer, Offer> getOffers(){
        Map<Integer, Offer> map = new HashMap<>();
        map.put(NEW_OFFER_ID, getNewOffer());
        return map;
    }

    public static Offer getNewOffer(){
        Map<String, String> products = new HashMap<>();
        products.put(NEW_PRODUCT_ID, WYNK_EROSNOW);
        return  Offer.builder()
                .id(NEW_OFFER_ID)
                .products(products)
                .build();
    }

    public static Product getNewProduct(){
        return Product.builder()
                .packGroup(WYNK_EROSNOW)
                .id(Integer.parseInt(NEW_PRODUCT_ID))
                .build();
    }
}
