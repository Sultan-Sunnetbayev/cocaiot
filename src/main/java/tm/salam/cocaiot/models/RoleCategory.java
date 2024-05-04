package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import tm.salam.cocaiot.dtoes.RoleCategoryDTO;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles_categories")
public class RoleCategory {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "privilage")
    private boolean privilage;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_uuid", referencedColumnName = "uuid")
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_uuid", referencedColumnName = "uuid")
    private Category category;

    public RoleCategoryDTO toPrivilageCategoryDTO(){

        RoleCategoryDTO roleCategoryDTO=RoleCategoryDTO.builder()
                .categoryDTO(this.getCategory().toCategoryDTO())
                .privilage(this.isPrivilage())
                .build();

        return roleCategoryDTO;
    }

}
