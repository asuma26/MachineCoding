package in.wynk.hystrix.factory;

import in.wynk.hystrix.service.IMethodFinder;
import in.wynk.hystrix.service.impl.WynkFallbackMethodFinder;

public class MethodFinderFactory {

    private static final IMethodFinder METHOD_FINDER = new WynkFallbackMethodFinder();

    public static IMethodFinder getMethodFinder() {
        return METHOD_FINDER;
    }

}
