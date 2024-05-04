package tm.salam.cocaiot.dtoes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailingCompanyDTO {

    private UUID uuid;
    private MailingDTO mailingDTO;
    private CompanyDTO companyDTO;

    @Override
    public String toString() {
        return "MailingCompanyDTO{" +
                "uuid=" + uuid +
                ", mailingDTO=" + mailingDTO +
                ", companyDTO=" + companyDTO +
                '}';
    }

}
