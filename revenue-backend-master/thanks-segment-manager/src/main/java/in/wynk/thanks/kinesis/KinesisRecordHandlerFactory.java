package in.wynk.thanks.kinesis;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KinesisRecordHandlerFactory {

    private static Map<String, Set<IKinesisRecordHandler>> appHandlerMap = new HashMap<>();

    private KinesisRecordHandlerFactory() { }

    public static Set<IKinesisRecordHandler> getAppRecordHandlers(String applicationName) {
        if(applicationName.contains(applicationName)) {
            return appHandlerMap.get(applicationName);
        }
        return null;
    }

    public static void registerListener(IKinesisRecordHandler handler) {
        if(!appHandlerMap.containsKey(handler.getApplicationName()))
            appHandlerMap.put(handler.getApplicationName(), new HashSet<>());

        Set<IKinesisRecordHandler> handlers = appHandlerMap.get(handler.getApplicationName());
        handlers.add(handler);
        appHandlerMap.put(handler.getApplicationName(), handlers);
    }
}
