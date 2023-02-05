package in.wynk.partner.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.dto.WynkResponse;
import in.wynk.order.dto.request.OrderAirtelRequest;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderAirtelResponse;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.scheduler.DeferredOrdersScheduler;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;
import in.wynk.partner.service.IPaymentPartnerService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/wynk/v1/payment/partner")
public class PaymentPartnerController {

    private final DeferredOrdersScheduler scheduler;
    private final IPaymentPartnerService paymentPartnerService;

    public PaymentPartnerController(DeferredOrdersScheduler scheduler, IPaymentPartnerService paymentPartnerService) {
        this.scheduler = scheduler;
        this.paymentPartnerService = paymentPartnerService;
    }

    @GetMapping(value = "/plans/all")
    @AnalyseTransaction(name = "allPlansListing")
    public WynkResponse<EligiblePlansListingResponse> getAllPlans(Principal principal, @RequestParam String service) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        AnalyticService.update(BaseConstants.CLIENT_ID, principal.getName());
        WynkResponse<EligiblePlansListingResponse> response = WynkResponse.<EligiblePlansListingResponse>builder().body(paymentPartnerService.getAllPlans(principal.getName(), service)).build();
        AnalyticService.update(response);
        return response;
    }

    @GetMapping(value = "/plans/active")
    @AnalyseTransaction(name = "activePlanListing")
    public WynkResponse<ActivePlansListingResponse> activePlans(Principal principal, @RequestParam String msisdn, @RequestParam String service) {
        AnalyticService.update(BaseConstants.MSISDN, msisdn);
        AnalyticService.update(BaseConstants.SERVICE, service);
        AnalyticService.update(BaseConstants.CLIENT_ID, principal.getName());
        WynkResponse<ActivePlansListingResponse> response = WynkResponse.<ActivePlansListingResponse>builder().body(paymentPartnerService.getActivePlansForUser(msisdn, service)).build();
        AnalyticService.update(response);
        return response;
    }

    @PostMapping(value = "/order/place")
    @AnalyseTransaction(name = "placeOrder")
    public WynkResponse<OrderResponse> placeOrder(Principal principal, @RequestBody OrderPlacementRequest request) {
        AnalyticService.update(request);
        AnalyticService.update(BaseConstants.CLIENT_ID, principal.getName());
        final String partnerId = principal.getName();
        WynkResponse<OrderResponse> response = placeOrder(partnerId, request);
        AnalyticService.update(response);
        return response;
    }

    @AnalyseTransaction(name = "orderStatus")
    @GetMapping(value = "/order/status/{orderId}")
    public WynkResponse<OrderResponse> orderStatus(Principal principal, @PathVariable String orderId, @RequestParam String msisdn) {
        AnalyticService.update(BaseConstants.MSISDN, msisdn);
        AnalyticService.update(BaseConstants.ORDER_ID, orderId);
        AnalyticService.update(BaseConstants.CLIENT_ID, principal.getName());
        WynkResponse<OrderResponse> response = WynkResponse.<OrderResponse>builder().body(paymentPartnerService.orderStatus(orderId, msisdn)).build();
        AnalyticService.update(response);
        return response;
    }

    @AnalyseTransaction(name = "placeOrderAirtel")
    @PostMapping(value = "/order/place/airtel")
    public WynkResponse<OrderAirtelResponse> airtelPlaceOrder(Principal principal, @RequestBody OrderAirtelRequest orderAirtelRequest) {
        String partnerId = principal.getName();
        OrderPlacementRequest request = orderAirtelRequest.convert();
        AnalyticService.update(BaseConstants.CLIENT_ID, principal.getName());
        WynkResponse<OrderAirtelResponse> response = WynkResponse.<OrderAirtelResponse>builder().body(OrderAirtelResponse.from(Objects.requireNonNull(placeOrder(partnerId, request).getBody()).getData())).build();
        AnalyticService.update(response);
        return response;
    }

    @GetMapping(value = "/order/schedule/start")
    public WynkResponse<Void> scheduleDeferred() {
        scheduler.processDeferredOrders();
        return WynkResponse.<Void>builder().build();
    }

    private WynkResponse<OrderResponse> placeOrder(String partnerId, OrderPlacementRequest request) {
        AnalyticService.update(request);
        AnalyticService.update(BaseConstants.CLIENT_ID, partnerId);
        OrderResponse response = paymentPartnerService.placeOrder(partnerId, request);
        AnalyticService.update(response);
        return WynkResponse.<OrderResponse>builder().body(response).build();
    }

}
