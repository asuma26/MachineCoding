package in.wynk.session.context;

import in.wynk.session.dto.Session;

public class SessionContextHolder<T> {

    private static final ThreadLocal<Session> local = new ThreadLocal<>();

    public static <T> void set(Session<T> session) {
        local.set(session);
    }

    public static <T> Session<T> get() {
        return local.get();
    }

    public static <T> T getBody(){
        return (T) get().getBody();
    }

    public static String getId(){
       return local.get().getId().toString();
    }
}
