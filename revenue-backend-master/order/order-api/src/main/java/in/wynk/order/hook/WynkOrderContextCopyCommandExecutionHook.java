package in.wynk.order.hook;

import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.dao.entity.Order;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WynkOrderContextCopyCommandExecutionHook extends HystrixCommandExecutionHook {

    private final HystrixRequestVariableDefault<Order> hrv = new HystrixRequestVariableDefault<>();

    @Override
    public <T> void onStart(HystrixInvokable<T> commandInstance) {
        HystrixRequestContext.initializeContext();
        getThreadLocals();
    }

    @Override
    public <T> void onExecutionStart(HystrixInvokable<T> commandInstance) {
        setThreadLocals();
    }


    @Override
    public <T> void onFallbackStart(HystrixInvokable<T> commandInstance) {
        setThreadLocals();
    }


    @Override
    public <T> void onSuccess(HystrixInvokable<T> commandInstance) {
        HystrixRequestContext.getContextForCurrentThread().shutdown();
        super.onSuccess(commandInstance);
    }

    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, HystrixRuntimeException.FailureType failureType, Exception e) {
        HystrixRequestContext.getContextForCurrentThread().shutdown();
        return super.onError(commandInstance, failureType, e);
    }

    private void getThreadLocals() {
        OrderContext.getOrder().ifPresent(hrv::set);
    }

    private void setThreadLocals() {
        OrderContext.setOrder(hrv.get());
    }

}