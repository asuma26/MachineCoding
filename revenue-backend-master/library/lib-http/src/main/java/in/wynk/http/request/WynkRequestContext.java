package in.wynk.http.request;

public class WynkRequestContext {

    private static final ThreadLocal<CachedBodyHttpServletRequest> local = new ThreadLocal<>();

    public static CachedBodyHttpServletRequest getRequest() {
        return local.get();
    }

    public static void setRequest(CachedBodyHttpServletRequest request) {
        local.set(request);
    }
}
