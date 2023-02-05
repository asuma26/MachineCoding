package in.wynk.events.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import in.wynk.common.dto.EmptyResponse;
import in.wynk.events.utils.EventsUtils;
import in.wynk.logging.BaseLoggingMarkers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EventsController {

    private static final Logger eventsLogger = LoggerFactory.getLogger("events");
    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Value("${music.encryptionkey}")
    private String encryptionKey;

    @Autowired
    private Gson gson;
    
    @PostMapping(value = "/wynk/v1/payment/track")
    public Map<String, String> storePaymentEvents(@RequestBody String requestPayload) throws Exception {
        Map<String, String> response = new HashMap<>();
        if (StringUtils.isEmpty(requestPayload)) {
            response.put("status", "failure");
            return response;
        }
        try {
            String uid = "";
            JsonObject jsonObject = gson.fromJson(requestPayload, JsonObject.class);
            try {
                if(jsonObject.get("uid") != null && !StringUtils.isEmpty(jsonObject.get("uid").getAsString())) {
                    uid = jsonObject.get("uid").getAsString();
                } else if (jsonObject.get("userId") != null && !StringUtils.isEmpty(jsonObject.get("userId").getAsString())) {
                    uid = EventsUtils.decrypt(jsonObject.get("userId").getAsString(), encryptionKey);
                }
            } catch (Exception e) {
                uid = jsonObject.get("userId").getAsString();
            }
            jsonObject.addProperty("uid", uid);
            eventsLogger.info(gson.toJson(jsonObject));
            response.put("status", "success");
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.APPLICATION_ERROR, e.getMessage(), e);
            response.put("status", "failure");
            throw e;
        }
        return response;
    }


    @PostMapping(value = "/wynk/v1/track")
    public EmptyResponse trackEvent(@RequestBody String requestPayload) {
        eventsLogger.info(requestPayload);
        return EmptyResponse.response();
    }
}
