package tm.salam.cocaiot.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporalMailing {

    private UUID uuid;
    private String name;
    private String text;
    private List<String> recipients;
    private Set<String> typeActivityNames;
    private TypeMailing typeMailing;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date created;

    @Override
    public String toString() {
        return "TemporalMailing{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", recipients=" + recipients +
                ", typeActivityNames=" + typeActivityNames +
                ", typeMailing=" + typeMailing +
                '}';
    }

}
