package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCategoryDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoleDTO roleDTO;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CategoryDTO categoryDTO;
    private boolean privilage;


}
