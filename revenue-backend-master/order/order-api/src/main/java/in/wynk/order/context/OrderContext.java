package in.wynk.order.context;

import in.wynk.order.core.dao.entity.Order;

import java.util.Optional;

public class OrderContext {

    private static final ThreadLocal<Order> LOCAL = new ThreadLocal<>();

    public static Optional<Order> getOrder() {
        return Optional.ofNullable(LOCAL.get());
    }

    public static void setOrder(Order order) {
        LOCAL.set(order);
    }

    public static void clear() {
        LOCAL.remove();
    }

}
