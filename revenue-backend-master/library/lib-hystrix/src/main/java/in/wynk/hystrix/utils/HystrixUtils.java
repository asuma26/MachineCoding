package in.wynk.hystrix.utils;

import in.wynk.hystrix.dto.MethodInvocationData;

import java.lang.reflect.Method;
import java.util.Arrays;

public class HystrixUtils {

    public static boolean checkIfFallbackMethodIsExtended(MethodInvocationData invocationData, Method method) {
        int pjpParamLength = invocationData.getParamTypes().length;
        int fallbackParamLength = method.getParameterTypes().length;
        return fallbackParamLength == pjpParamLength + 1 && method.getParameterTypes()[fallbackParamLength - 1] == Throwable.class;
    }

    public static boolean matchMethod(Method method, String name, Class<?>[] paramTypes) {
        if (method.getName().equalsIgnoreCase(name)) {
            if (Arrays.equals(method.getParameterTypes(), paramTypes)) {
                return true;
            }
            Object[] extendedParams = Arrays.copyOf(paramTypes, paramTypes.length + 1);
            extendedParams[paramTypes.length] = Throwable.class;
            return Arrays.equals(method.getParameterTypes(), extendedParams);
        }
        return false;
    }

}
