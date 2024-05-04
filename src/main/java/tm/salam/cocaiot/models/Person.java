package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import tm.salam.cocaiot.dtoes.PersonDTO;

import javax.persistence.*;
import javax.validation.constraints.Email;
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
@Table(name = "persons")
public class Person {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "person name don't be null")
    @NotEmpty(message = "person name don't be empty")
    @Size(min = 1, max = 50, message = "person name length should be long than 0 and less than 51")
    private String name;
    @Column(name = "surname")
    @NotNull(message = "person surname don't be null")
    @NotEmpty(message = "person surname don't be empty")
    @Size(min = 1, max = 65, message = "person surname length should be long than 0 and less than 66")
    private String surname;
    @Column(name = "patronomic_name")
    @Size(max = 75, message = "person patronomic name length should be less than 76")
    private String patronomicName;
    @Column(name = "birth_place")
    @Size(max = 600, message = "person birth place length should be less than 601")
    private String birthPlace;
    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    @Column(name = "full_address_of_residence")
    @Size(max = 750, message = "person full address of residence length should be less than 751")
    private String fullAddressOfResidence;
    @Column(name = "phone_number")
    @Size(max = 40, message = "person phone number length should be less than 41")
    private String phoneNumber;
    @Column(name = "fax")
    @Size(max = 50, message = "person fax length should be less than 51")
    private String fax;
    @Column(name = "email")
    @Email(message = "person email is invalid")
    @Size(max = 75, message = "person email length should be less than 76")
    private String email;
    @Column(name = "education")
    @Size(max = 750, message = "person education length should be less than 751")
    private String education;
    @Column(name = "experience")
    @Size(max = 750, message = "person experience length should be less than 751")
    private String experience;
    @Column(name = "knowledge_of_languages")
    @Size(max = 300, message = "person knowledge of languages length should be less than 301")
    private String knowledgeOfLanguages;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;
    @Column(name = "is_entrepreneur")
    private boolean isEntrepreneur;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "country_uuid", referencedColumnName = "uuid")
    private Country country;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "region_uuid", referencedColumnName = "uuid")
    private Region region;
    @OneToMany(mappedBy = "director", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Company> companies;
    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Entrepreneur entrepreneur;
    @ManyToMany(mappedBy = "founders", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Company>founderCompanies;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image", referencedColumnName = "uuid")
    private File image;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "copy_passport", referencedColumnName = "uuid")
    private File copyPassport;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronomicName() {
        return patronomicName;
    }

    public void setPatronomicName(String patronomicName) {
        this.patronomicName = patronomicName;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullAddressOfResidence() {
        return fullAddressOfResidence;
    }

    public void setFullAddressOfResidence(String fullAddressOfResidence) {
        this.fullAddressOfResidence = fullAddressOfResidence;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getKnowledgeOfLanguages() {
        return knowledgeOfLanguages;
    }

    public void setKnowledgeOfLanguages(String knowledgeOfLanguages) {
        this.knowledgeOfLanguages = knowledgeOfLanguages;
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

    public boolean isEntrepreneur() {
        return isEntrepreneur;
    }

    public void setEntrepreneur(boolean entrepreneur) {
        isEntrepreneur = entrepreneur;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Entrepreneur getEntrepreneur() {
        return entrepreneur;
    }

    public void setEntrepreneur(Entrepreneur entrepreneur) {
        this.entrepreneur = entrepreneur;
    }

    public List<Company> getFounderCompanies() {
        return founderCompanies;
    }

    public void setFounderCompanies(List<Company> founderCompanies) {
        this.founderCompanies = founderCompanies;
    }

    public void setImage(File image){
        this.image=image;

    }

    public File getCopyPassport() {
        return copyPassport;
    }

    public void setCopyPassport(File copyPassport) {
        this.copyPassport = copyPassport;
    }

    public File getImage(){
        return this.image;
    }

    public PersonDTO toPersonDTOOnlyGeneral(){

        PersonDTO personDTO=PersonDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .surname(this.getSurname())
                .patronomicName(this.getPatronomicName())
                .fullAddressOfResidence(this.getFullAddressOfResidence())
                .phoneNumber(this.getPhoneNumber())
                .email(this.getEmail())
                .knowledgeOfLanguages(this.getKnowledgeOfLanguages())
                .image(this.getImage()==null ? null : this.getImage().toFileDTO())
                .copyPassport(this.getCopyPassport()!=null ? this.getCopyPassport().toFileDTO() : null)
                .build();

        return personDTO;
    }

    public PersonDTO toPersonDTO(){

        PersonDTO personDTO= PersonDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .surname(this.getSurname())
                .patronomicName(this.getPatronomicName())
                .birthPlace(this.getBirthPlace())
                .birthDate(this.getBirthDate())
                .countryDTO(this.getCountry()!=null ? this.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(this.getRegion() !=null ? this.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .fullAddressOfResidence(this.getFullAddressOfResidence())
                .phoneNumber(this.getPhoneNumber())
                .fax(this.getFax())
                .email(this.getEmail())
                .education(this.getEducation())
                .experience(this.getExperience())
                .knowledgeOfLanguages(this.getKnowledgeOfLanguages())
                .image(this.getImage()!=null ? this.getImage().toFileDTO() : null)
                .copyPassport(this.getCopyPassport() ==null ? null : this.getCopyPassport().toFileDTO())
                .build();

        return personDTO;
    }

    public Person(final UUID uuid, final String name, final String surname, final String patronomicName){
        this.uuid=uuid;
        this.name=name;
        this.surname=surname;
        this.patronomicName=patronomicName;
    }

    public PersonDTO toPersonDTOOnlyFullName(){

        PersonDTO personDTO=PersonDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .surname(this.getSurname())
                .patronomicName(this.getPatronomicName())
                .image(this.getImage()!=null ? this.getImage().toFileDTO() : null)
                .build();

        return personDTO;
    }

    public PersonDTO toPersonDTOWithFullNameAndPhoneNumber(){

        PersonDTO personDTO=PersonDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .surname(this.getSurname())
                .patronomicName(this.getPatronomicName())
                .phoneNumber(this.getPhoneNumber())
                .image(this.getImage()!=null ? this.getImage().toFileDTO() : null)
                .build();

        return personDTO;
    }

    public String getFullName(){

        String fullName= this.getSurname() + " " + this.getName();
        if(this.getPatronomicName()!=null){
            fullName+=" " + this.getPatronomicName();
        }

        return fullName;
    }

}
