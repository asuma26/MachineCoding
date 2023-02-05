package in.wynk.queue.consumer;

public interface ISQSMessageConsumer<T> {

    void consume(T message);

    Class<T> messageType();

}
