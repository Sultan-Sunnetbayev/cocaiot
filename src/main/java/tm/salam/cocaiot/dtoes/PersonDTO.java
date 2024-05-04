package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {

    private UUID uuid;
    private String name;
    private String surname;
    private String patronomicName;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String birthPlace;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date birthDate;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CountryDTO countryDTO;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RegionDTO regionDTO;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullAddressOfResidence;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
    private String fax;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String education;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String experience;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String knowledgeOfLanguages;
    private FileDTO image;
    private FileDTO copyPassport;
    private List<CompanyDTO> directorCompanies;
    private List<CompanyDTO>founderCompanies;
    private Boolean isEntrepreneur;

    @Override
    public String toString() {
        return "PersonDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronomicName='" + patronomicName + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", birthDate=" + birthDate +
                ", countryDTO=" + countryDTO +
                ", regionDTO=" + regionDTO +
                ", fullAddressOfResidence='" + fullAddressOfResidence + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fax='" + fax + '\'' +
                ", email='" + email + '\'' +
                ", education='" + education + '\'' +
                ", experience='" + experience + '\'' +
                ", knowledgeOfLanguages='" + knowledgeOfLanguages + '\'' +
                ", image=" + image +
                ", passport=" + copyPassport +
                ", directorCompanies=" + directorCompanies +
                ", founderCompanies=" + founderCompanies +
                ", isEntrepreneur=" + isEntrepreneur +
                '}';
    }

    public String getPersonDTOFullName(){

        String fullName=this.getSurname() + " " + this.getName();
        if(this.getPatronomicName()!=null){
            fullName+=" " + this.getPatronomicName();
        }

        return fullName;
    }

}
