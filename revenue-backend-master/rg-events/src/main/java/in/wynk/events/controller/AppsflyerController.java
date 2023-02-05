package in.wynk.events.controller;

import com.google.gson.JsonObject;
import in.wynk.events.utils.EventsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static in.wynk.events.constants.EventsConstants.OS;
import static in.wynk.events.constants.EventsConstants.SOURCE;

/**
 * @author Abhishek
 * @created 23/04/20
 */
@RestController
@RequestMapping("/wynk/v1/appsflyer")
public class AppsflyerController {
    private static final Logger appsflyerPublisher = LoggerFactory.getLogger("appsflyerPublisher");
    private static final Logger appsflyerClient = LoggerFactory.getLogger("appsflyerClient");

    @GetMapping(value = "/publisher/track")
    public ResponseEntity<Void> appsflyerPublisherTrack(@RequestParam MultiValueMap<String, String> params) {
        String json = EventsUtils.getJsonString(params);
        if (StringUtils.isNotEmpty(json)) {
            appsflyerPublisher.info(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/publisher/install")
    public ResponseEntity<Void> appsflyerPublisherInstall(@RequestParam MultiValueMap<String, String> params) {
        String json = EventsUtils.getJsonString(params);
        if (StringUtils.isNotEmpty(json)) {
            appsflyerPublisher.info(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/client/track")
    public ResponseEntity<Void> appsflyerClientTrack(@RequestBody String requestPayload, @RequestParam(OS) String os,
                                                     @RequestParam(SOURCE) String source) {
        JsonObject jsonObject = EventsUtils.getJsonObjectFromJsonString(requestPayload);
        if (jsonObject == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        jsonObject.addProperty(OS, os);
        jsonObject.addProperty(SOURCE, source);
        String json = EventsUtils.getJsonString(jsonObject);
        if (StringUtils.isNotEmpty(json)) {
            appsflyerClient.info(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
