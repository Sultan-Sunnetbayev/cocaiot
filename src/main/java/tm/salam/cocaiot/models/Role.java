package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import tm.salam.cocaiot.dtoes.RoleDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "role name don't be null")
    @NotEmpty(message = "role name don't be empty")
    @Size(min = 1, max = 250, message = "role name length should be long than 0 and less than 251")
    private String name;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "roles_categories",
//            joinColumns = @JoinColumn(name = "role_uuid", referencedColumnName = "uuid"),
//            inverseJoinColumns = @JoinColumn(name = "category_uuid", referencedColumnName = "uuid")
//    )
//    private List<Category> categories;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoleCategory>roleCategories;
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<User>users;

    public Role(final UUID uuid, final String name){
        this.setUuid(uuid);
        this.setName(name);
    }

    public RoleDTO toRoleDTOOnlyUuidAndName(){

        RoleDTO roleDTO=RoleDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .build();

        return roleDTO;
    }

    public RoleDTO toRoleDTO(){

        RoleDTO roleDTO=RoleDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .roleCategoryDTOS(this.getRoleCategories() != null ? this.getRoleCategories().stream()
                        .map(RoleCategory::toPrivilageCategoryDTO).collect(Collectors.toList()) : null)
                .build();

        return roleDTO;
    }

}
