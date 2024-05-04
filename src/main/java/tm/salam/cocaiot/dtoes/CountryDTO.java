package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDTO {

    private UUID uuid;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer amountCompany;

    @Override
    public String toString() {
        return "CountryDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", amountCompany=" + amountCompany +
                '}';
    }

}
