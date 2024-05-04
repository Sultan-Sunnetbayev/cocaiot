package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import tm.salam.cocaiot.dtoes.FileDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files")
public class File {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @Size(max = 350, message = "file's name length should be less than 351")
    private String name;
    @Column(name = "path")
    @NotNull(message = "file's path don't be null")
    @NotEmpty(message = "file's path don't be empty")
    @Size(min = 1, max = 500, message = "file's path length should be less than 501")
    private String path;
    @Column(name = "extension")
    @Size(max = 150, message = "file's extension length should be less than 151")
    private String extension;
    @Column(name = "size")
    private long size;
    @Column(name = "is_confirmed")
    private boolean isConfirmed;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

    public FileDTO toFileDTO(){

        FileDTO fileDTO= FileDTO.builder()
                .uuid(this.uuid)
                .name(this.name)
                .path(this.path)
                .extension(this.extension)
                .size(this.size)
                .build();

        return fileDTO;
    }

}
