package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import tm.salam.cocaiot.dtoes.MailingDTO;
import tm.salam.cocaiot.helpers.TypeMailing;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
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
@Table(name = "mailings")
public class Mailing {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "mailing name don't be null")
    @NotEmpty(message = "mailing name don't be empty")
    @Size(min = 1, max = 500, message = "mailing name length should be long than 0 and less than 501")
    private String name;
    @Column(name = "text")
    private String text;
    @Column(name = "type_mailing")
    @Enumerated(EnumType.STRING)
    private TypeMailing typeMailing;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "file_uuid", referencedColumnName = "uuid")
    private File file;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "mailings_companies",
            joinColumns = @JoinColumn(name = "mailing_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "company_uuid", referencedColumnName = "uuid")
    )
    private List<Company>companies;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "mailings_entrepreneurs",
            joinColumns = @JoinColumn(name = "mailing_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "entrepreneur_uuid", referencedColumnName = "uuid")
    )
    private List<Entrepreneur>entrepreneurs;
//    @OneToMany(mappedBy = "mailing", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<MailingCompany>mailingCompanies;

    public MailingDTO toMailingDTOOnlyGeneral() {

        MailingDTO mailingDTO=MailingDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .typeMailing(this.getTypeMailing())
                .build();

        return mailingDTO;
    }

    public MailingDTO toMailingDTO() {

        MailingDTO mailingDTO=MailingDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .text(this.getText())
                .fileDTO(this.getFile() != null ? this.getFile().toFileDTO() : null)
                .typeMailing(this.getTypeMailing())
                .build();

        return mailingDTO;
    }

}
