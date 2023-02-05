package in.wynk.queue.dto;

public interface MessageToEventMapper<E extends MessageThresholdExceedEvent> {

    static <T> MessageThresholdExceedEvent map(T message) {
        return DefaultMessageThresholdExceedEvent.builder()
                .type(message.getClass().getCanonicalName())
                .payload(message)
                .build();
    }

    E map();

}
