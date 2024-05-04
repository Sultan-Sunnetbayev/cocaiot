package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tm.salam.cocaiot.helpers.TypeOwnership;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {

    private UUID uuid;
    private String name;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullAddress;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    private String fax;
    private String webSite;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CountryDTO countryDTO;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RegionDTO regionDTO;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonDTO director;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PersonDTO>founders;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TypeOwnership typeOwnership;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TypeActivityDTO> typeActivityDTOS;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrganizationDTO organizationDTO;
    private FileDTO logo;
    private String typeWork;
    private FileDTO membershipApplication;
    private FileDTO extractFromUsreo;
    private FileDTO charterOfTheEnterprise;
    private FileDTO certificateOfForeignEconomicRelations;
    private FileDTO certificateOfStateRegistration;
    private FileDTO paymentOfTheEntranceMembershipFee;

    @Override
    public String toString() {
        return "CompanyDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", webSite='" + webSite + '\'' +
                ", countryDTO=" + countryDTO +
                ", regionDTO=" + regionDTO +
                ", director=" + director +
                ", founders=" + founders +
                ", typeOwnership=" + typeOwnership +
                ", typeActivityDTOS=" + typeActivityDTOS +
                ", organizationDTO=" + organizationDTO +
                ", logo=" + logo +
                ", typeWork='" + typeWork + '\'' +
                ", membershipApplication=" + membershipApplication +
                ", extractFromUsreo=" + extractFromUsreo +
                ", charterOfTheEnterprise=" + charterOfTheEnterprise +
                ", certificateOfForeignEconomicRelations=" + certificateOfForeignEconomicRelations +
                ", certificateOfStateRegistration=" + certificateOfStateRegistration +
                ", paymentOfTheEntranceMembershipFee=" + paymentOfTheEntranceMembershipFee +
                '}';
    }

}
