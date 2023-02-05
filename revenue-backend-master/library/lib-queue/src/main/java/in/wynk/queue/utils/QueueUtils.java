package in.wynk.queue.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN;

public class QueueUtils {

    public static final ObjectMapper mapper = new ObjectMapper().configure(IGNORE_UNKNOWN, true);

}
