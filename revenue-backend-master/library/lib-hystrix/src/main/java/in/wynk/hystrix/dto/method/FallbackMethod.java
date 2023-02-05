package in.wynk.hystrix.dto.method;

import java.lang.reflect.InvocationTargetException;

public interface FallbackMethod {
    Object invoke() throws InvocationTargetException, IllegalAccessException;
}
