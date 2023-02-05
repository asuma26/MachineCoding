package in.wynk.ut.base.service.impl;

import in.wynk.ut.base.constant.AppConstant;
import in.wynk.ut.base.model.response.BatchJobStatus;
import in.wynk.ut.base.service.BatchJobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BatchJobServiceImpl implements BatchJobService {

    protected static final Log logger = LogFactory.getLog(BatchJobServiceImpl.class);

    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public Mono<BatchJobStatus> getJobStatus(Long jobId) {
        BatchJobStatus status = null;
        JobExecution jobExecution = jobExplorer.getJobExecution(jobId);
        if(jobExecution != null)
            status = new BatchJobStatus.BatchStatusBuilder()
                    .id(jobExecution.getId())
                    .version(jobExecution.getVersion())
                    .batchStatus(jobExecution.getStatus().name())
                    .startTime(jobExecution.getStartTime())
                    .endTime(jobExecution.getEndTime())
                    .createdTime(jobExecution.getCreateTime())
                    .lastUpdated(jobExecution.getLastUpdated())
                    .exitStatus(jobExecution.getExitStatus())
                    .jobParameters(jobExecution.getJobParameters())
                    .build();
        return status == null ? Mono.empty() : Mono.just(status);
    }

    @Override
    public Flux<List<Map<Long, BatchJobStatus>>> getPaginatedJobStatusByName(int page, String jobName) {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName,0 , page * AppConstant.DEFAULT_RECORDS_PER_PAGE);
        List<Map<Long, BatchJobStatus>> batchJobsStatus = null;
        if(!jobInstances.isEmpty()) {
            batchJobsStatus =  jobInstances.parallelStream()
                                        .map(jobInstance ->
                                                 jobExplorer.getJobExecutions(jobInstance))
                                        .map(jobExecutions ->
                                                jobExecutions.parallelStream()
                                                                              .map(jobExecution -> new BatchJobStatus.BatchStatusBuilder()
                                                                                                    .id(jobExecution.getId())
                                                                                                    .version(jobExecution.getVersion())
                                                                                                    .batchStatus(jobExecution.getStatus().name())
                                                                                                    .startTime(jobExecution.getStartTime())
                                                                                                    .endTime(jobExecution.getEndTime())
                                                                                                    .createdTime(jobExecution.getCreateTime())
                                                                                                    .lastUpdated(jobExecution.getLastUpdated())
                                                                                                    .exitStatus(jobExecution.getExitStatus())
                                                                                                    .jobParameters(jobExecution.getJobParameters())
                                                                                                    .build()).collect(Collectors.toMap(x -> x.getId(), x -> x))).collect(Collectors.toList());
        }
        return batchJobsStatus == null ? Flux.empty() : Flux.just(batchJobsStatus);
    }

}
