package in.wynk.ut.base.service;

import in.wynk.ut.base.model.request.BaseUploadRequest;
import in.wynk.ut.base.model.response.BatchJobStatus;
import reactor.core.publisher.Mono;

public interface UserTargetService {

     Mono<BatchJobStatus> process(BaseUploadRequest request) throws Exception;

}
