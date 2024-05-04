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
public class CategoryDTO {

    private UUID uuid;
    private String name;

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
