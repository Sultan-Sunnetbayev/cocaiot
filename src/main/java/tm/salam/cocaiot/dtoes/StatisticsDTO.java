package tm.salam.cocaiot.dtoes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uuid;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private int amountMember;
    private double procent;

    @Override
    public String toString() {
        return "StatisticsDTO{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", amountMember=" + amountMember +
                ", procent=" + procent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatisticsDTO)) return false;
        StatisticsDTO that = (StatisticsDTO) o;
        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

}
