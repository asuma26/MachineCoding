package in.wynk.utils.response;

@Deprecated
public class BaseResponse<T> {

    private T data;
    private boolean success;

    public BaseResponse() {
        super();
    }

    public BaseResponse(T data, boolean success) {
        super();
        this.data = data;
        this.success = success;
    }

    public static <T> BaseResponse<T> build(T data, boolean success) {
        return new BaseResponse<>(data, success);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
