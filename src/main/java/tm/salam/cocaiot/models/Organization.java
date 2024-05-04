package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import tm.salam.cocaiot.dtoes.OrganizationDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "organizations")
public class Organization {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "organization name don't be null")
    @NotEmpty(message = "organization name don't be empty")
    @Size(min = 1, max = 400, message = "length organization name's should be long than 0 and less than 401")
    private String name;
    @Column(name = "amount_company")
    private int amountCompany;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Company>companies;

    public Organization(final UUID uuid, final String name){
        this.uuid=uuid;
        this.name=name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmountCompany() {
        return amountCompany;
    }

    public void setAmountCompany(int amountCompany) {
        this.amountCompany = amountCompany;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public OrganizationDTO toOrganizationDTOOnlyUuidAndName(){

        OrganizationDTO organizationDTO= OrganizationDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .build();

        return organizationDTO;
    }

}
