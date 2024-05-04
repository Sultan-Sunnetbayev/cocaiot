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
public class UserDTO {

    private UUID uuid;
    private String name;
    private String surname;
    private String patronomicName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    private RoleDTO roleDTO;

    @Override
    public String toString() {
        return "UserDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronomicName='" + patronomicName + '\'' +
                ", email='" + email + '\'' +
                ", roleDTO=" + roleDTO +
                '}';
    }
}
