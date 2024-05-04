package tm.salam.cocaiot.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tm.salam.cocaiot.dtoes.CountryDTO;
import tm.salam.cocaiot.dtoes.FileDTO;
import tm.salam.cocaiot.dtoes.RegionDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporalCocaiotMember {

    private UUID uuid;
    private String memberName;
    private TypeOwnership typeOwnership;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> typeActivityNames;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullAddress;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatusPayment statusPayment;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CountryDTO countryDTO;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RegionDTO regionDTO;
    private FileDTO logo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date created;


    @Override
    public String toString() {
        return "TemporalCocaiotMember{" +
                "uuid=" + uuid +
                ", memberName='" + memberName + '\'' +
                ", typeOwnership=" + typeOwnership +
                ", typeActivityNames=" + typeActivityNames +
                ", fullAddress='" + fullAddress + '\'' +
                ", statusPayment=" + statusPayment +
                ", created=" + created +
                '}';
    }

}
