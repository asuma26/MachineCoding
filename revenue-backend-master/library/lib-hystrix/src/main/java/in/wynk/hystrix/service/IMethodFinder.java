package in.wynk.hystrix.service;

import in.wynk.hystrix.dto.MethodInvocationData;
import in.wynk.hystrix.dto.method.FallbackMethod;

public interface IMethodFinder {
    FallbackMethod find(String methodName, MethodInvocationData invocationData, Throwable th) throws NoSuchMethodException;
}
