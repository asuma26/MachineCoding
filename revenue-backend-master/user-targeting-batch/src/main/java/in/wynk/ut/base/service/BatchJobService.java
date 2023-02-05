package in.wynk.ut.base.service;

import in.wynk.ut.base.model.response.BatchJobStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface BatchJobService {

     Mono<BatchJobStatus> getJobStatus(Long jobId);

     Flux<List<Map<Long, BatchJobStatus>>> getPaginatedJobStatusByName(int page, String jobName);

}
