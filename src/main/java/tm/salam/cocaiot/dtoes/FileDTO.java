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
public class FileDTO {

    private UUID uuid;
    private String name;
    private String path;
    private String extension;
    private long size;

    @Override
    public String toString() {
        return "FileDTO{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", extension='" + extension + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

}
