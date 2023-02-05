package in.wynk.hystrix.service;

import com.netflix.hystrix.HystrixCommand;
import in.wynk.hystrix.advice.WynkHystrixCommand;
import in.wynk.hystrix.dto.MethodInvocationData;

public interface ICacheCommandManager {

    HystrixCommand.Setter get(MethodInvocationData invocationData, WynkHystrixCommand commandAnnotation);

}
