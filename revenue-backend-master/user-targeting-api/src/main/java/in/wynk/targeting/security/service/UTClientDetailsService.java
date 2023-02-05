package in.wynk.targeting.security.service;

import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static in.wynk.auth.constant.AuthConstant.S2S_READ_ACCESS_ROLE;
import static in.wynk.auth.constant.AuthConstant.S2S_WRITE_ACCESS_ROLE;

@Deprecated
public class UTClientDetailsService implements IClientDetailsService<Client> {
    @Override
    public Optional<Client> getClientDetails(String clientId) {
        UTClient clientDetails = new UTClient();
        return Optional.of(clientDetails);
    }

    @Override
    public Optional<Client> getClientDetailsByAlias(String clientAlias) {
        return Optional.empty();
    }

    @Getter
    public static class UTClient implements Client {

        @Override

        public String getClientId() {
            return "543fbd6f96644406567079c00d8f33dc";
        }

        @Override
        public String getAlias() {
            return null;
        }

        @Override
        public String getClientSecret() {
            return "50de5a601c133a29c8db434fa9bf2db4";
        }

        @Override
        public List<String> getAuthorities() {
            return Arrays.asList(S2S_READ_ACCESS_ROLE,
                    S2S_WRITE_ACCESS_ROLE);
        }
    }
}
