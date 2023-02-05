package in.wynk.order.core.utils;

import com.fasterxml.uuid.Generators;

public class OrderIdentityGenerator {

    public static String generate() {
        return Generators.timeBasedGenerator().generate().toString();
    }

}
