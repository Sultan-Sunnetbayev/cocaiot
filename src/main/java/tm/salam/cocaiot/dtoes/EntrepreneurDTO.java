package tm.salam.cocaiot.dtoes;

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
public class EntrepreneurDTO {

    private UUID uuid;
    private PersonDTO personDTO;
    private String webSite;
    private List<TypeActivityDTO> typeActivityDTOS;
    private FileDTO logo;
    private String typeWork;
    private FileDTO membershipApplication;
    private FileDTO patentCertifyingPayment;
    private FileDTO entrepreneurStatisticalCodes;
    private FileDTO certificateOfForeignEconomicRelations;
    private FileDTO registrationCertificateOfEntrepreneur;
    private FileDTO certificateOfTaxRegistration;

    @Override
    public String toString() {
        return "EntrepreneurDTO{" +
                "uuid=" + uuid +
                ", personDTO=" + personDTO +
                ", webSite='" + webSite + '\'' +
                ", typeActivityDTOS=" + typeActivityDTOS +
                ", logo=" + logo +
                ", typeWork='" + typeWork + '\'' +
                ", membershipApplication=" + membershipApplication +
                ", patentCertifyingPayment=" + patentCertifyingPayment +
                ", entrepreneurStatisticalCodes=" + entrepreneurStatisticalCodes +
                ", certificateOfForeignEconomicRelations=" + certificateOfForeignEconomicRelations +
                ", registrationCertificateOfEntrepreneur=" + registrationCertificateOfEntrepreneur +
                ", certificateOfTaxRegistration=" + certificateOfTaxRegistration +
                '}';
    }

}
