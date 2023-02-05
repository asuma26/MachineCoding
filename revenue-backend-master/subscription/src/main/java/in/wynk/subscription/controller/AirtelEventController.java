package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.subscription.dto.request.AirtelEventRequest;
import in.wynk.subscription.dto.response.AirtelEventResponse;
import in.wynk.subscription.service.IAirtelEventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wynk/s2s/v1/airtel")
public class AirtelEventController {

    private final IAirtelEventService airtelEventService;

    public AirtelEventController(IAirtelEventService airtelEventService) {
        this.airtelEventService = airtelEventService;
    }

    @PostMapping("/event")
    @AnalyseTransaction(name = "airtelEvent")
    public AirtelEventResponse airtelEvent(@RequestBody AirtelEventRequest airtelEventRequest) {
        AnalyticService.update(airtelEventRequest);
        airtelEventService.saveMsisdnToCollection(airtelEventRequest);
        return AirtelEventResponse.builder().build();
    }

}
