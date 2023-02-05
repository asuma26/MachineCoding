package in.wynk.events.controller;

import in.wynk.events.utils.EventsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Abhishek
 * @created 20/04/20
 */
@RestController
@RequestMapping("/wynk/v1/branch")
public class BranchController {
    private static final Logger branchPublisher = LoggerFactory.getLogger("branchPublisher");

    @GetMapping(value = "/publisher/track")
    public ResponseEntity<Void> branchPublisherTrack(@RequestParam MultiValueMap<String, String> params) {
        String json = EventsUtils.getJsonString(params);
        if (StringUtils.isNotEmpty(json)) {
            branchPublisher.info(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/publisher/install")
    public ResponseEntity<Void> branchPublisherInstall(@RequestParam MultiValueMap<String, String> params) {
        String json = EventsUtils.getJsonString(params);
        if (StringUtils.isNotEmpty(json)) {
            branchPublisher.info(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
