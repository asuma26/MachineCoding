package in.wynk.order.testcases;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FreshOrderPlacementTest.class,
        RenewOrderPlacementTest.class,
        DeferredOrderPlacementTest.class,
        CancelOrderPlacementTest.class
})
@TestMethodOrder(OrderAnnotation.class)
public class OrderTestSuite {
}
