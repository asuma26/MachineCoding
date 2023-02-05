package in.wynk.queue.dto;

public interface FIFOQueueMessageMarker {

    String getMessageGroupId();

    String getMessageDeDuplicationId();

}
