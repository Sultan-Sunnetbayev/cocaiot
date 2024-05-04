package tm.salam.cocaiot.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporalMailingRecipients {

    private UUID uuid;
    private String recipientName;
    private MemberType memberType;

    @Override
    public String toString() {
        return "TemporalMailingRecipients{" +
                "uuid=" + uuid +
                ", recipientName='" + recipientName + '\'' +
                ", memberType=" + memberType +
                '}';
    }

}
