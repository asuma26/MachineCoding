package in.wynk.hystrix.dto.method.impl;

import in.wynk.hystrix.dto.method.FallbackMethod;
import lombok.Builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Builder
public class WynkFallbackMethod implements FallbackMethod {

    private final Method method;
    private final Object object;
    private final Object[] args;

    @Override
    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(object, args);
    }

}
