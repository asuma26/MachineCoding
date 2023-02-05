package in.wynk.utils.constant;

@Deprecated
public enum PaymentMethod {
    IBM(1, "ibm"),
    FREE(2, "free"),
    IPAYY(3, "ipayy"),
    PAY_TM(4, "paytm"),
    ITUNES(5, "itunes"),
    SMART_API(6, "smartapi"),
    TWSS(7, "twss"),
    PAY_U(8, "payu"),
    BROADBAND(9, "broadband"),
    CRM(10, "crm"),
    GOOGLE_WALLET(11, "googleWallet"),
    SE(12, "se"),
    APB(13, "apb"),
    PHONEPE(14, "phonepe"),
    THANKS(15, "thanks"),
    AMAZON_IAP(16, "amazon"),
    UNKNOWN(-1, "unknown");

    private final int id;
    private final String value;

    PaymentMethod(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static PaymentMethod getPaymentMethod(int id) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getId() == id) {
                return method;
            }
        }
        return UNKNOWN;
    }

    public static PaymentMethod fromValue(String value) {
        for(PaymentMethod method : PaymentMethod.values()) {
            if(method.getValue().equals(value)) {
                return method;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}