package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.helpers.TypeOwnership;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "companies")
public class Company {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "company name don't be null")
    @NotEmpty(message = "company name don't be empty")
    @Size(min = 1, max = 350, message = "company name length should be long than 0 and less than 351")
    private String name;
    @Column(name = "full_address")
    @Size(max = 750, message = "company full address length should be less than 751")
    private String fullAddress;
    @Column(name = "phone_number")
    @Size(max = 40, message = "company phone number length should be less than 41")
    private String phoneNumber;
    @Column(name = "email")
    @Email(message = "company email is invalid")
    @Size(max = 75, message = "company email length should be less than 76")
    private String email;
    @Column(name = "fax")
    @Size(max = 50, message = "company fax length should be less than 51")
    private String fax;
    @Column(name = "web_site")
//    @URL(message = "company web site is invalid")
    @Size(max = 250, message = "company web site length should be less than 251")
    private String webSite;
    @Column(name = "type_ownership")
    @Enumerated(EnumType.STRING)
    private TypeOwnership typeOwnership;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "country_uuid", referencedColumnName = "uuid")
    private Country country;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "region_uuid", referencedColumnName = "uuid")
    private Region region;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "director_uuid", referencedColumnName = "uuid")
    private Person director;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "organization_uuid", referencedColumnName = "uuid")
    private Organization organization;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "membership_application_uuid", referencedColumnName = "uuid")
    private File membershipApplication;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "extract_from_usreo_uuid", referencedColumnName = "uuid")
    private File extractFromUsreo;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "charter_of_the_enterprise_uuid", referencedColumnName = "uuid")
    private File charterOfTheEnterprise;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "certificate_of_foreign_economic_relations_uuid", referencedColumnName = "uuid")
    private File certificateOfForeignEconomicRelations;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "certificate_of_state_registration_uuid", referencedColumnName = "uuid")
    private File certificateOfStateRegistration;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "payment_of_the_entrance_membership_fee_uuid", referencedColumnName = "uuid")
    private File paymentOfTheEntranceMembershipFee;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "founders_companies",
            joinColumns = @JoinColumn(name = "company_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "founder_uuid", referencedColumnName = "uuid")
    )
    private List<Person>founders;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "type_activities_companies",
            joinColumns = @JoinColumn(name = "company_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "type_activity_uuid", referencedColumnName = "uuid")
    )
    private List<TypeActivity>typeActivities;
    @OneToOne(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private CocaiotMember cocaiotMember;
    @ManyToMany(mappedBy = "companies", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Mailing>mailings;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "logo", referencedColumnName = "uuid")
    private File logo;

//    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<MailingCompany>mailingCompanies;

    public Company(final UUID uuid, final String name){
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

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public TypeOwnership getTypeOwnership() {
        return typeOwnership;
    }

    public void setTypeOwnership(TypeOwnership typeOwnership) {
        this.typeOwnership = typeOwnership;
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

    public boolean isCocaiotMember() {
        return isCocaiotMember;
    }

    public void setCocaiotMember(boolean cocaiotMember) {
        isCocaiotMember = cocaiotMember;
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

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public File getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(File membershipApplication) {
        this.membershipApplication = membershipApplication;
    }

    public File getExtractFromUsreo() {
        return extractFromUsreo;
    }

    public void setExtractFromUsreo(File extractFromUsreo) {
        this.extractFromUsreo = extractFromUsreo;
    }

    public File getCharterOfTheEnterprise() {
        return charterOfTheEnterprise;
    }

    public void setCharterOfTheEnterprise(File charterOfTheEnterprise) {
        this.charterOfTheEnterprise = charterOfTheEnterprise;
    }

    public File getCertificateOfForeignEconomicRelations() {
        return certificateOfForeignEconomicRelations;
    }

    public void setCertificateOfForeignEconomicRelations(File certificateOfForeignEconomicRelations) {
        this.certificateOfForeignEconomicRelations = certificateOfForeignEconomicRelations;
    }

    public File getCertificateOfStateRegistration() {
        return certificateOfStateRegistration;
    }

    public void setCertificateOfStateRegistration(File certificateOfStateRegistration) {
        this.certificateOfStateRegistration = certificateOfStateRegistration;
    }

    public File getPaymentOfTheEntranceMembershipFee() {
        return paymentOfTheEntranceMembershipFee;
    }

    public void setPaymentOfTheEntranceMembershipFee(File paymentOfTheEntranceMembershipFee) {
        this.paymentOfTheEntranceMembershipFee = paymentOfTheEntranceMembershipFee;
    }

    public List<Person> getFounders() {
        return founders;
    }

    public void setFounders(List<Person> founders) {
        this.founders = founders;
    }

    public List<TypeActivity> getTypeActivities() {
        return typeActivities;
    }

    public void setTypeActivities(List<TypeActivity> typeActivities) {
        this.typeActivities = typeActivities;
    }

    public CocaiotMember getCocaiotMember() {
        return cocaiotMember;
    }

    public void setCocaiotMember(CocaiotMember cocaiotMember) {
        this.cocaiotMember = cocaiotMember;
    }

    public List<Mailing> getMailings() {
        return mailings;
    }

    public void setMailings(List<Mailing> mailings) {
        this.mailings = mailings;
    }

    public void setLogo(File logo){
        this.logo=logo;
    }

    public File getLogo(){
        return this.logo;
    }

    public void setTypeWork(String typeWork){
        this.typeWork=typeWork;
    }

    public String getTypeWork(){
        return this.typeWork;
    }

    public CompanyDTO toCompanyDTOOnlyUuidAndName(){

        CompanyDTO companyDTO=CompanyDTO.builder()
                .uuid(this.getUuid())
                .name(this.name)
                .logo(this.getLogo()!=null ? this.getLogo().toFileDTO() : null)
                .build();

        return companyDTO;
    }

    public CompanyDTO toCompanyDTOOnlyGeneral() {

        CompanyDTO companyDTO=CompanyDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .fullAddress(this.getFullAddress())
                .phoneNumber(this.getPhoneNumber())
                .email(this.getEmail())
                .fax(this.getFax())
                .webSite(this.getWebSite())
                .countryDTO(this.getCountry()!=null ? this.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(this.getRegion()!=null ? this.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .director(this.getDirector()!=null ? this.getDirector().toPersonDTOOnlyFullName() : null)
                .founders(this.getFounders() != null ? this.getFounders().stream().
                        map(Person::toPersonDTOOnlyFullName).collect(Collectors.toList()) : null)
                .organizationDTO(this.getOrganization() != null ?
                        this.getOrganization().toOrganizationDTOOnlyUuidAndName() : null)
                .typeActivityDTOS(this.getTypeActivities()!=null ? this.getTypeActivities().stream()
                        .map(TypeActivity::toTypeActivityDTOOnlyUuidAndName).collect(Collectors.toList()) : null)
                .typeOwnership(this.getTypeOwnership())
                .logo(this.getLogo()!=null ? this.getLogo().toFileDTO() : null)
                .typeWork(this.getTypeWork())
                .build();

        return companyDTO;
    }

    public CompanyDTO toCompanyDTOWithTypeActivities(){

        CompanyDTO companyDTO=CompanyDTO.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .typeActivityDTOS(this.getTypeActivities()!=null ? this.getTypeActivities().stream()
                        .map(TypeActivity::toTypeActivityDTOOnlyUuidAndName).collect(Collectors.toList()) : null)
                .typeOwnership(this.getTypeOwnership())
                .logo(this.getLogo()!=null ? this.getLogo().toFileDTO() : null)
                .build();

        return companyDTO;
    }

}
