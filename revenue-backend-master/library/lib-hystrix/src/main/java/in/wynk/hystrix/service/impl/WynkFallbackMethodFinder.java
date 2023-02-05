package in.wynk.hystrix.service.impl;

import in.wynk.hystrix.dto.MethodInvocationData;
import in.wynk.hystrix.dto.method.FallbackMethod;
import in.wynk.hystrix.dto.method.impl.WynkFallbackMethod;
import in.wynk.hystrix.service.IMethodFinder;
import in.wynk.hystrix.utils.HystrixUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class WynkFallbackMethodFinder implements IMethodFinder {

    @Override
    public FallbackMethod find(String methodName, MethodInvocationData invocationData, Throwable th) throws NoSuchMethodException {
        Method method = getFallbackMethod(methodName, invocationData);
        WynkFallbackMethod.WynkFallbackMethodBuilder builder = WynkFallbackMethod.builder().method(method).object(invocationData.getCallingInstance());
        if (HystrixUtils.checkIfFallbackMethodIsExtended(invocationData, method)) {
            builder.args(getExtendedArgs(invocationData, th));
        } else {
            builder.args(invocationData.getArgs());
        }
        return builder.build();
    }

    private Object[] getExtendedArgs(MethodInvocationData invocationData, Throwable t) {
        Object[] extendedArgs = Arrays.copyOf(invocationData.getArgs(), invocationData.getArgs().length + 1);
        extendedArgs[invocationData.getArgs().length] = t;
        return extendedArgs;
    }

    private Method getFallbackMethod(String methodName, MethodInvocationData invocationData) throws NoSuchMethodException {
        final Optional<Method> matchedMethod = Arrays.stream(invocationData.getCallingClass().getDeclaredMethods()).filter(method ->
                HystrixUtils.matchMethod(method, methodName, invocationData.getParamTypes())).findAny();
        if (!matchedMethod.isPresent()) {
            throw new NoSuchMethodException(methodName);
        }
        return matchedMethod.get();
    }

}
