package in.wynk.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.List;

import static in.wynk.auth.constant.AuthConstant.SECURITY_PROPS_PREFIX;

@ConfigurationProperties(SECURITY_PROPS_PREFIX)
public class SecurityProperties {

    private OAuth oauth;
    private Exempt exempt;

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public Exempt getExempt() {
        return exempt;
    }

    public void setExempt(Exempt exempt) {
        this.exempt = exempt;
    }

    public static final class Exempt {

        private List<String> paths;

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }
    }

    public static final class OAuth {

        private JwtProperties jwt;
        private KeyProperties secrets;

        public JwtProperties getJwt() {
            return jwt;
        }

        public void setJwt(JwtProperties jwt) {
            this.jwt = jwt;
        }

        public KeyProperties getSecrets() {
            return secrets;
        }

        public void setSecrets(KeyProperties secrets) {
            this.secrets = secrets;
        }

        public static final class JwtProperties {
            private String issuer;
            private long expiry;

            public String getIssuer() {
                return issuer;
            }

            public void setIssuer(String issuer) {
                this.issuer = issuer;
            }

            public long getExpiry() {
                return expiry;
            }

            public void setExpiry(long expiry) {
                this.expiry = expiry;
            }
        }

        public static final class KeyProperties {
            private String keyStorePassword;
            private String keyPairAlias;
            private String keyPairPassword;
            private Resource keyStore;

            public Resource getKeyStore() {
                return this.keyStore;
            }

            public void setKeyStore(Resource keyStore) {
                this.keyStore = keyStore;
            }

            public String getKeyStorePassword() {
                return this.keyStorePassword;
            }

            public void setKeyStorePassword(String keyStorePassword) {
                this.keyStorePassword = keyStorePassword;
            }

            public String getKeyPairAlias() {
                return this.keyPairAlias;
            }

            public void setKeyPairAlias(String keyPairAlias) {
                this.keyPairAlias = keyPairAlias;
            }

            public String getKeyPairPassword() {
                return this.keyPairPassword;
            }

            public void setKeyPairPassword(String keyPairPassword) {
                this.keyPairPassword = keyPairPassword;
            }
        }
    }
}