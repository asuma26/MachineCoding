package in.wynk.thanks.kinesis;

public interface IKinesisRecordHandler extends Comparable<String> {

    /**
     * Listen
     * @param data
     */
    void handle(String data);

    /**
     * register kinesis listener using this in {@link KinesisRecordHandlerFactory}
     */
    void register();

    /**
     * return the application stream name for the listener.
     * @return
     */
    String getApplicationName();

    @Override default int compareTo(String o) {
        if(this.getApplicationName() == null) return -1;
        if(o == null) return 1;
        return this.getApplicationName().compareTo(o);
    }
}
