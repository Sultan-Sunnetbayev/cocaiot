package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import tm.salam.cocaiot.dtoes.RegionDTO;

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
@Table(name = "regions")
public class Region {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "region name don't be null")
    @NotEmpty(message = "region name don't be empty")
    @Size(min = 1, max = 150, message = "region name length should be long than 0 and less than 151")
    private String name;
    @Column(name = "amount_company")
    private int amountCompany;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "country_uuid", referencedColumnName = "uuid")
    private Country country;
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Person> persons;
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Company>companies;

    public Region(final UUID uuid, final String name){
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public RegionDTO toDTO(){

        final RegionDTO regionDTO= RegionDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .amountCompany(this.amountCompany)
                .build();

        return regionDTO;
    }

    public RegionDTO toRegionDTOOnlyUuidAndName(){

        final RegionDTO regionDTO= RegionDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .build();

        return regionDTO;
    }

}
