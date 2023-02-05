package in.wynk.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class TimeItAspect {

    @Around(value = "execution(@in.wynk.advice.TimeIt * *.*(..))")
    public Object timeIt(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) pjp.getStaticPart().getSignature();
        Method method = methodSignature.getMethod();
        try {
            Object[] args = pjp.getArgs();
            return pjp.proceed(args);
        }
        finally {
            AnalyticService.update(method.getName() + "-time", System.currentTimeMillis() - startTime);
        }
    }
}
