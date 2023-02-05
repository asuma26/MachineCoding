package in.wynk.hystrix.dto;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Getter
public class MethodInvocationData {
    private final Class<?> callingClass;
    private final Object callingInstance;
    private final MethodSignature signature;
    private final Method method;
    private final Object[] args;
    private final Class<?>[] paramTypes;

    private MethodInvocationData(ProceedingJoinPoint pjp) {
        this.signature = (MethodSignature) pjp.getSignature();
        this.callingClass = pjp.getThis().getClass();
        this.callingInstance = pjp.getThis();
        this.args = pjp.getArgs();
        this.paramTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        this.method = ((MethodSignature) pjp.getSignature()).getMethod();
    }

    public static MethodInvocationData from(ProceedingJoinPoint pjp) {
        return new MethodInvocationData(pjp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInvocationData that = (MethodInvocationData) o;
        return Objects.equals(callingClass, that.callingClass) &&
                Objects.equals(method, that.method) &&
                Arrays.equals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(callingClass, method);
        result = 31 * result + Arrays.hashCode(paramTypes);
        return result;
    }
}
