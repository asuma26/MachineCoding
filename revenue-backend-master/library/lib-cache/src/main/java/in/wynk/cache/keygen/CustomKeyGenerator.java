package in.wynk.cache.keygen;

import in.wynk.cache.constant.CacheConstant;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Objects;

public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getSimpleName())
                .append(CacheConstant.HYPHEN_DELIMITER)
                .append(method.getName());
        for (Object param : params) {
            if (Objects.nonNull(param)) {
                sb.append(CacheConstant.HYPHEN_DELIMITER)
                        .append(param.getClass().getSimpleName())
                        .append(CacheConstant.COLON_DELIMITER).append(param);
            }
        }
        return sb.toString();
    }
}
