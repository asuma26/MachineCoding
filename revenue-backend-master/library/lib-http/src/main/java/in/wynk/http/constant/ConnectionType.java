package in.wynk.http.constant;

public enum ConnectionType {

    HTTPS("https"), HTTP("http");

    private String type;

    ConnectionType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
