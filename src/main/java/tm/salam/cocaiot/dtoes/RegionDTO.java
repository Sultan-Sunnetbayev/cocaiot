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
public class RegionDTO {

    private UUID uuid;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer amountCompany;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CountryDTO countryDTO;

    @Override
    public String toString() {
        return "RegionDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", amountCompany=" + amountCompany +
                ", countryDTO=" + countryDTO +
                '}';
    }

}
