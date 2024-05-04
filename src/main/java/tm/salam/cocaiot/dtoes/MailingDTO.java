package tm.salam.cocaiot.dtoes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tm.salam.cocaiot.helpers.TypeMailing;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailingDTO {

    private UUID uuid;
    private String name;
    private String text;
    private FileDTO fileDTO;
    private TypeMailing typeMailing;
    private List<CompanyDTO> companyDTOS;
    private List<EntrepreneurDTO>entrepreneurDTOS;

    @Override
    public String toString() {
        return "MailingDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", fileDTO=" + fileDTO +
                ", typeMailing=" + typeMailing +
                ", companyDTOS=" + companyDTOS +
                ", entrepreneurDTOS=" + entrepreneurDTOS +
                '}';
    }

}
