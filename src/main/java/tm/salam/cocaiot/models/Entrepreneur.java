package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;

import javax.persistence.*;
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
@Table(name = "entrepreneurs")
public class Entrepreneur {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "web_site")
//    @URL(message = "company web site is invalid")
    @Size(max = 250, message = "entrepreneur web site length should be less than 251")
    private String webSite;
    @Column(name = "type_work")
    private String typeWork;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;
    @Column(name = "is_cocaiot_member")
    private boolean isCocaiotMember;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "person_uuid", referencedColumnName = "uuid")
    private Person person;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "membership_application_uuid", referencedColumnName = "uuid")
    private File membershipApplication;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "patent_certifying_payment_uuid", referencedColumnName = "uuid")
    private File patentCertifyingPayment;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "entrepreneur_statistical_codes_uuid", referencedColumnName = "uuid")
    private File entrepreneurStatisticalCodes;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "certificate_of_foreign_economic_relations_uuid", referencedColumnName = "uuid")
    private File certificateOfForeignEconomicRelations;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "registration_certificate_of_entrepreneur_uuid", referencedColumnName = "uuid")
    private File registrationCertificateOfEntrepreneur;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "certificate_of_tax_registration_uuid", referencedColumnName = "uuid")
    private File certificateOfTaxRegistration;
    @OneToOne(mappedBy = "entrepreneur", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private CocaiotMember cocaiotMember;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "entrepreneurs_type_activities",
            joinColumns = @JoinColumn(name = "entrepreneur_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "type_activity_uuid", referencedColumnName = "uuid")
    )
    private List<TypeActivity> typeActivities;
    @ManyToMany(mappedBy = "entrepreneurs", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Mailing>mailings;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "logo", referencedColumnName = "uuid")
    private File logo;

    public Entrepreneur(final UUID uuid, final Person person){
        this.uuid=uuid;
        this.person=person;
    }

    public EntrepreneurDTO toEntrepreneurDTOOnlyGeneral() {

        EntrepreneurDTO entrepreneurDTO=EntrepreneurDTO.builder()
                .uuid(this.uuid)
                .personDTO(this.getPerson().toPersonDTO())
                .typeActivityDTOS(this.getTypeActivities()!=null ? this.getTypeActivities().stream()
                        .map(TypeActivity::toTypeActivityDTOOnlyUuidAndName).collect(Collectors.toList()) : null)
                .webSite(this.getWebSite())
                .logo(this.getLogo()!=null ? this.getLogo().toFileDTO() : null)
                .typeWork(this.getTypeWork())
                .build();

        return entrepreneurDTO;
    }

    public EntrepreneurDTO toEntrepreneurDTOWithFullNameAndTypeActivities(){

        EntrepreneurDTO entrepreneurDTO=EntrepreneurDTO.builder()
                .uuid(this.uuid)
                .personDTO(this.getPerson().toPersonDTOOnlyFullName())
                .typeActivityDTOS(this.getTypeActivities().stream().map(TypeActivity::toTypeActivityDTOOnlyUuidAndName)
                        .collect(Collectors.toList()))
                .build();

        return entrepreneurDTO;
    }

}
