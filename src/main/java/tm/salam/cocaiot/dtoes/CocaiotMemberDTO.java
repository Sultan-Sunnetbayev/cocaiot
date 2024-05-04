package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tm.salam.cocaiot.helpers.StatusPayment;
import tm.salam.cocaiot.helpers.MemberType;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocaiotMemberDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID uuid;
    private CompanyDTO companyDTO;
    private EntrepreneurDTO entrepreneurDTO;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatusPayment statusPayment;
    private Date initialDate;
    private Date finalDate;
    private Date initialDateLastPayment;
    private FileDTO fileDTO;
    private MemberType typeCocaiotMember;

    @Override
    public String toString() {
        return "CocaiotMemberDTO{" +
                "uuid=" + uuid +
                ", companyDTO=" + companyDTO +
                ", entrepreneurDTO=" + entrepreneurDTO +
                ", statusPayment=" + statusPayment +
                ", initialDate=" + initialDate +
                ", finalDate=" + finalDate +
                ", initialDateLastPayment=" + initialDateLastPayment +
                ", fileDTO=" + fileDTO +
                ", typeCocaiotMember=" + typeCocaiotMember +
                '}';
    }

}
