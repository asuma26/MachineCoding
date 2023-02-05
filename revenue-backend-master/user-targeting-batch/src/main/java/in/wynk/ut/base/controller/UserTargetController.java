package in.wynk.ut.base.controller;

import in.wynk.ut.base.model.request.BaseUploadRequest;
import in.wynk.ut.base.model.response.BatchJobStatus;
import in.wynk.ut.base.service.UserTargetService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "https://wcf-admin-production.wynk.in", maxAge = 3600)
@RequestMapping("/wynk/ut/base")
public class UserTargetController {

    protected static final Log logger = LogFactory.getLog(UserTargetController.class);

    @Autowired
    @Qualifier("utService")
    private UserTargetService service;

    @PostMapping("/v1/process")
    public Mono<BatchJobStatus> process(@RequestBody BaseUploadRequest request) throws Exception {
        return service.process(request);
    }

}
