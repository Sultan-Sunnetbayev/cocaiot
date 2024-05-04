package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Company;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO companies(name, full_address, phone_number, email, fax, web_site, " +
            "type_work, country_uuid, region_uuid, director_uuid, organization_uuid, type_ownership, logo, " +
            "membership_application_uuid, extract_from_usreo_uuid, charter_of_the_enterprise_uuid, " +
            "certificate_of_foreign_economic_relations_uuid, certificate_of_state_registration_uuid, " +
            "payment_of_the_entrance_membership_fee_uuid) " +
            "VALUES(:name, :fullAddress, :phoneNumber, :email, :fax, :webSite, :typeWork, :countryUuid, :regionUuid, " +
            ":directorUuid, :organizationUuid, CAST(:typeOwnership AS type_ownership), :logoUuid, " +
            ":membershipApplicationUuid, :extractFromUsreoUuid, :charterOfTheEnterpriseUuid, " +
            ":certificateOfForeignEconomicRelationsUuid, :certificateOfStateRegistrationUuid, " +
            ":paymentOfTheEntranceMembershipFeeUuid) ON CONFLICT DO NOTHING RETURNING CAST(uuid AS VARCHAR)")
    UUID addCompany(@Param("name")String name, @Param("fullAddress")String fullAddress,
                    @Param("phoneNumber")String phoneNumber, @Param("email")String email, @Param("fax")String fax,
                    @Param("webSite")String webSite, @Param("typeWork")String typeWork, @Param("countryUuid")UUID countryUuid,
                    @Param("regionUuid")UUID regionUuid, @Param("directorUuid")UUID directorUuid,
                    @Param("organizationUuid")UUID organizationUuid, @Param("typeOwnership") String typeOwnership,
                    @Param("logoUuid")UUID logoUuid, @Param("membershipApplicationUuid")UUID membershipApplicationUuid,
                    @Param("extractFromUsreoUuid")UUID extractFromUsreoUuid,
                    @Param("charterOfTheEnterpriseUuid")UUID charterOfTheEnterpriseUuid,
                    @Param("certificateOfForeignEconomicRelationsUuid")UUID certificateOfForeignEconomicRelationsUuid,
                    @Param("certificateOfStateRegistrationUuid")UUID certificateOfStateRegistrationUuid,
                    @Param("paymentOfTheEntranceMembershipFeeUuid")UUID paymentOfTheEntranceMembershipFeeUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO type_activities_companies(company_uuid, type_activity_uuid) " +
            "VALUES(:companyUuid, :typeActivityUuid) ON CONFLICT DO NOTHING")
    void addTypeActivityToCompany(@Param("companyUuid")UUID companyUuid,
                                  @Param("typeActivityUuid")UUID typeActivityUuid);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN COUNT(company)>0 THEN TRUE ELSE FALSE END FROM companies company " +
            "WHERE (company.type_ownership = CAST(:typeOwnership AS type_ownership)) AND (company.uuid <> :companyUuid) AND " +
            "((LOWER(company.name) = :companyName) OR (company.email = :companyEmail) OR " +
            "(company.fax = :companyFax) OR (company.web_site = :companyWebSite))")
    boolean isCompanyExists(@Param("companyUuid")UUID companyUuid, @Param("companyName")String companyName,
                            @Param("companyEmail")String companyEmail, @Param("companyFax")String companyFax,
                            @Param("companyWebSite")String companyWebSite, @Param("typeOwnership")String typeOwnership);

    @Query("SELECT company FROM Company company WHERE company.uuid = :companyUuid")
    Company getCompanyByUuid(@Param("companyUuid")UUID companyUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM type_activities_companies WHERE company_uuid = :companyUuid")
    void removeActivityCompanyByUuid(@Param("companyUuid")UUID companyUuid);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE companies company SET name = :name, " +
            "full_address = :fullAddress, phone_number = :phoneNumber, email = :email, " +
            "fax = :fax, web_site = :webSite, type_work = :typeWork, country_uuid = :countryUuid, " +
            "region_uuid = :regionUuid, director_uuid = :directorUuid, organization_uuid = :organizationUuid, " +
            "type_ownership = CAST(:typeOwnership AS type_ownership), logo = :logoUuid, " +
            "membership_application_uuid = :membershipApplicationUuid, extract_from_usreo_uuid = :extractFromUsreoUuid, " +
            "charter_of_the_enterprise_uuid = :charterOfTheEnterpriseUuid, " +
            "certificate_of_foreign_economic_relations_uuid = :certificateOfForeignEconomicRelationsUuid, " +
            "certificate_of_state_registration_uuid = :certificateOfStateRegistrationUuid, " +
            "payment_of_the_entrance_membership_fee_uuid = :paymentOfTheEntranceMembershipFeeUuid " +
            "WHERE company.uuid = :companyUuid RETURNING CAST(company.uuid AS VARCHAR)")
    UUID editCompany(@Param("companyUuid")UUID companyUuid, @Param("name")String name, @Param("fullAddress")String fullAddress,
                     @Param("phoneNumber")String phoneNumber, @Param("email")String email, @Param("fax")String fax,
                     @Param("webSite")String webSite,@Param("typeWork")String typeWork, @Param("countryUuid")UUID countryUuid,
                     @Param("regionUuid")UUID regionUuid, @Param("directorUuid")UUID directorUuid,
                     @Param("organizationUuid")UUID organizationUuid, @Param("typeOwnership")String typeOwnership,
                     @Param("logoUuid")UUID logoUuid, @Param("membershipApplicationUuid")UUID membershipApplicationUuid,
                     @Param("extractFromUsreoUuid")UUID extractFromUsreoUuid,
                     @Param("charterOfTheEnterpriseUuid")UUID charterOfTheEnterpriseUuid,
                     @Param("certificateOfForeignEconomicRelationsUuid")UUID certificateOfForeignEconomicRelationsUuid,
                     @Param("certificateOfStateRegistrationUuid")UUID certificateOfStateRegistrationUuid,
                     @Param("paymentOfTheEntranceMembershipFeeUuid")UUID paymentOfTheEntranceMembershipFeeUuid);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM companies company WHERE company.uuid = :companyUuid RETURNING TRUE")
    Boolean removeCompanyByUuid(@Param("companyUuid")UUID companyUuid);

    @Query(nativeQuery = true, value = "SELECT DISTINCT(company.uuid), company.name, company.full_address, " +
            "company.phone_number, company.email, company.fax, company.web_site,company.type_work, company.country_uuid, " +
            "company.region_uuid, company.director_uuid, company.organization_uuid, company.type_ownership, company.logo, " +
            "company.membership_application_uuid, company.extract_from_usreo_uuid, company.charter_of_the_enterprise_uuid, " +
            "company.certificate_of_foreign_economic_relations_uuid, certificate_of_state_registration_uuid, " +
            "company.payment_of_the_entrance_membership_fee_uuid, company.is_cocaiot_member, company.created, " +
            "company.updated FROM companies company " +
            "LEFT JOIN countries country ON country.uuid = company.country_uuid " +
            "LEFT JOIN regions region ON region.uuid = company.region_uuid " +
            "LEFT JOIN persons person ON person.uuid = company.director_uuid " +
            "LEFT JOIN type_activities_companies type_activity_company ON type_activity_company.company_uuid = company.uuid " +
            "LEFT JOIN type_activities type_activity ON type_activity.uuid = type_activity_company.type_activity_uuid " +
            "LEFT JOIN organizations organization ON organization.uuid = company.organization_uuid " +
            "WHERE (company.type_ownership = CAST(:typeOwnership AS type_ownership)) AND " +
            "((LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "((organization IS NOT NULL) AND (LOWER(organization.name) LIKE CONCAT('%', :searchKey, '%'))) OR " +
            "(LOWER(country.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(region.name) LIKE CONCAT('%', :searchKey, '%'))) ORDER BY company.created DESC")
    List<Company> getCompaniesBySearchKey(Pageable pageable,
                                          @Param("searchKey")String searchKey,
                                          @Param("typeOwnership")String typeOwnership);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(company.uuid)) FROM companies company " +
            "LEFT JOIN organizations organization ON organization.uuid = company.organization_uuid " +
            "LEFT JOIN countries country ON country.uuid = company.country_uuid " +
            "LEFT JOIN regions region ON region.uuid = company.region_uuid " +
            "LEFT JOIN persons person ON person.uuid = company.director_uuid " +
            "LEFT JOIN type_activities_companies type_activity_company ON type_activity_company.company_uuid = company.uuid " +
            "LEFT JOIN type_activities type_activity ON type_activity.uuid = type_activity_company.type_activity_uuid " +
            "WHERE (company.type_ownership = CAST(:typeOwnership AS type_ownership)) AND " +
            "((LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "((organization IS NOT NULL) AND (LOWER(organization.name) LIKE CONCAT('%', :searchKey, '%'))) OR " +
            "(LOWER(country.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(region.name) LIKE CONCAT('%', :searchKey, '%')))")
    int getAmountCompaniesBySearchKey(@Param("searchKey")String searchKey,
                                      @Param("typeOwnership")String typeOwnership);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'companies') AND (data_type <> 'uuid')")
    List<String>getCompanyColumns();

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO founders_companies(company_uuid, founder_uuid) " +
            "VALUES(:companyUuid, :founderUuid) ON CONFLICT DO NOTHING")
    void addFounderToCompany(@Param("companyUuid")UUID companyUuid,
                             @Param("founderUuid")UUID founderUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM founders_companies WHERE company_uuid = :companyUuid")
    void removeFoundersByCompanyUuid(@Param("companyUuid")UUID companyUuid);

    @Query("SELECT NEW Company(company.uuid AS uuid, company.name AS name) FROM Company company " +
            "WHERE (company.isCocaiotMember = FALSE) AND (company.name LIKE CONCAT('%', :searchKey, '%')) " +
            "ORDER BY company.name")
    List<Company>getIsNotCocaiotMemberCompaniesByName(@Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(company)>0 THEN TRUE ELSE FALSE END FROM Company company " +
            "WHERE company.uuid = :companyUuid")
    boolean isCompanyExistsByUuid(@Param("companyUuid")UUID companyUuid);

    @Query("SELECT NEW Company(company.uuid AS uuid, company.name AS name) FROM Company company " +
            "WHERE (company.isCocaiotMember = TRUE) AND (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) " +
            "ORDER BY company.name")
    List<Company>getCocaiotMemberCompaniesByName(@Param("searchKey")String searchKey);

    @Query("SELECT NEW Company(company.uuid AS uuid, company.name AS name) FROM Company company " +
            "WHERE (company.name LIKE CONCAT('%', :searchKey, '%')) ORDER BY company.name")
    List<Company>getCompaniesByName(@Param("searchKey")String searchKey);

    @Query("SELECT NEW Company(company.uuid AS uuid, company.name AS name) FROM Company company " +
            "INNER JOIN company.typeActivities typeActivity ON typeActivity.uuid IN :typeActivityUuids " +
            "GROUP BY company.uuid ORDER BY company.name")
    List<Company>getCompaniesByTypeActivityUuids(@Param("typeActivityUuids")List<UUID>typeActivityUuids);

    @Query("SELECT company.email FROM Company company " +
            "WHERE (company.email IS NOT NULL) AND (company.uuid IN :companyUuids)")
    List<String>getCompanyEmailsByUuids(@Param("companyUuids")List<UUID>companyUuids);

    @Query("SELECT COUNT(company) FROM Company company WHERE company.uuid IN :companyUuids")
    int getAmountCompanyByUuids(@Param("companyUuids")List<UUID> companyUuids);

    @Query(nativeQuery = true, value = "SELECT * FROM companies company " +
            "INNER JOIN type_activities_companies type_activity_company " +
            "ON ((type_activity_company.type_activity_uuid = :typeActivityUuid) AND " +
            "(type_activity_company.company_uuid=company.uuid))" +
            "ORDER BY company.name")
    List<Company>getCompanyNamesByTypeActivityUuid(@Param("typeActivityUuid")UUID typeActivityUuid);

}
