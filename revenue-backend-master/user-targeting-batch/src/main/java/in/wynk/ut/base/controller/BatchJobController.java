package in.wynk.ut.base.controller;

import in.wynk.ut.base.model.response.BatchJobStatus;
import in.wynk.ut.base.service.BatchJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://wcf-admin-production.wynk.in", maxAge = 3600)
@RequestMapping("/wynk/ut/job")
public class BatchJobController {

    @Autowired
    private BatchJobService batchJobService;

    @GetMapping("/v1/status/{jobId}")
    public Mono<BatchJobStatus> getJobStatus(@PathVariable Long jobId) {
        return batchJobService.getJobStatus(jobId);
    }

    @GetMapping("/v1/list/{jobName}/{page}")
    public Flux<List<Map<Long, BatchJobStatus>>> getJobStatus(@PathVariable String jobName, @PathVariable int page) {
        return batchJobService.getPaginatedJobStatusByName(page, jobName);
    }


}
