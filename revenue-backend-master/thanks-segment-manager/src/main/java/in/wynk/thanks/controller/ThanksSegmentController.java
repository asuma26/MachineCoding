package in.wynk.thanks.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import com.google.gson.Gson;
import in.wynk.common.dto.EmptyResponse;
import in.wynk.exception.WynkErrorType;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.thanks.dto.ThanksSegmentDTO;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import in.wynk.thanks.service.ThanksSegmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import static in.wynk.thanks.utils.ThanksConstants.CLIENT_ID;

@RestController
@RequestMapping("/wynk/s2s/v1/thanks")
@Slf4j
public class ThanksSegmentController {

    private final ThanksSegmentService thanksSegmentService;
    private final Gson gson;

    public ThanksSegmentController(ThanksSegmentService thanksSegmentService, Gson gson) {
        this.thanksSegmentService = thanksSegmentService;
        this.gson = gson;
    }

    @PostMapping("/update")
    @AnalyseTransaction(name = "updateThanksSegment")
    public EmptyResponse updateThanksUserSegment(@RequestParam String clientId, @RequestBody ThanksSegmentDTO thanksSegment) {
        String dataStr = gson.toJson(thanksSegment);
        AnalyticService.update("dataStr", dataStr);
        AnalyticService.update(CLIENT_ID, clientId);
        if (thanksSegmentService.isInvalid(thanksSegment) || StringUtils.isBlank(thanksSegment.getSource())) {
            log.error(ThanksLoggingMarkers.THANKS_ERROR, "Invalid value {}", dataStr);
            throw new WynkRuntimeException(WynkErrorType.UT001, ThanksLoggingMarkers.THANKS_ERROR);
        }
        thanksSegmentService.updateSegment(thanksSegment);
        return EmptyResponse.response();
    }

}
