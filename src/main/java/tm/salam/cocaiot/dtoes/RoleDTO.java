package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    private UUID uuid;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RoleCategoryDTO> roleCategoryDTOS;

    @Override
    public String toString() {
        return "RoleDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", roleCategoryDTOS=" + roleCategoryDTOS +
                '}';
    }

}
