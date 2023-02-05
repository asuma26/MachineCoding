package in.wynk.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import com.github.annotation.analytic.core.service.AnalyticService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AnalysedEntity
public class SessionDTO {
    @Analysed
    private Map<String, Object> sessionPayload;

    public <T> T get(String key) {
        T value = (T) sessionPayload.get(key);
        if(Objects.nonNull(key) && Objects.nonNull(value)){
            AnalyticService.update(key, value.toString());
        }
        return value;
    }

    public <T> void put(String key, T value) {
        if(Objects.nonNull(key) && Objects.nonNull(value)){
            AnalyticService.update(key, value.toString());
        }
        sessionPayload.put(key, value);
    }
}
