package in.wynk.order.mock;

import in.wynk.common.utils.MsisdnUtils;
import org.springframework.data.domain.PageRequest;

import java.util.Calendar;

public interface OrderMockConstants {

    int SELECTED_PLAN_ID = 123;
    long ORDER_AMOUNT = 1;
    String CURRENCY = "INR";
    String MSISDN = "+919457778448";
    String UID = MsisdnUtils.getUidFromMsisdn(MSISDN);
    String MOCK_ORDER_ID = "698310ba-cb10-4185-84c6-100df894325e";
    String DUMMY_PARTNER = "paytm";
    Calendar MOCK_LAST_UNTIL_DATE = Calendar.getInstance();
    PageRequest MOCK_PAGE_REQUEST = PageRequest.of(0, 10);
    Integer MOCK_LIMIT = 100;

}
