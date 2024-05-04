package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import tm.salam.cocaiot.dtoes.CategoryDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "category name don't be null")
    @NotEmpty(message = "category name don't be empty")
    @Size(min = 1, max = 250, message = "category name length should be long than 0 and less than 251")
    private String name;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    private Date updated;

//    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Role> roles;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoleCategory>roleCategories;

    public CategoryDTO toCategoryDTO(){

        CategoryDTO categoryDTO=CategoryDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .build();

        return categoryDTO;
    }

}
