package in.wynk.auth.factory;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.exception.WynkRuntimeException;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

public class KeyPairFactory {

    private final Resource resource;

    private final char[] password;

    private KeyStore store;

    private final Object lock = new Object();

    public KeyPairFactory(Resource resource, char[] password) {
        this.resource = resource;
        this.password = password;
    }

    public KeyPair getKeyPair(String alias, char[] password) {
        try {
            synchronized (lock) {
                if (store == null) {
                    synchronized (lock) {
                        store = KeyStore.getInstance(AuthConstant.KEY_TYPE);
                        store.load(resource.getInputStream(), this.password);
                    }
                }
            }
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, password);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance(AuthConstant.ENCRYPTION_ALGORITHM_TYPE).generatePublic(spec);
            return new KeyPair(publicKey, key);
        } catch (Exception ex) {
            throw new WynkRuntimeException(WynkAuthErrorType.AUTH001, ex);
        }
    }
}
