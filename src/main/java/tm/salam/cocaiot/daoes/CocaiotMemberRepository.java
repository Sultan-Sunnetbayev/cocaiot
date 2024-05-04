package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.CocaiotMember;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface CocaiotMemberRepository extends JpaRepository<CocaiotMember, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO cocaiot_members(company_uuid, entrepreneur_uuid, file_uuid, " +
            "status_payment, initial_date, final_date,  initial_date_last_payment) " +
            "VALUES(:companyUuid, :entrepreneurUuid, :fileUuid, CAST(:statusPayment AS status_payment), " +
            ":initialDateLastPayment, :finalDateLastPayment, :initialDateLastPayment) " +
            "ON CONFLICT DO NOTHING RETURNING TRUE")
    Boolean addMember(@Param("companyUuid")UUID companyUuid, @Param("entrepreneurUuid")UUID entrepreneurUuid,
                      @Param("fileUuid")UUID fileUuid, @Param("statusPayment")String statusPayment,
                      @Param("initialDateLastPayment")Date initialDateLastPayment,
                      @Param("finalDateLastPayment")Date finalDateLastPayment);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE cocaiot_members cocaiot_member SET company_uuid = :companyUuid, " +
            "entrepreneur_uuid = :entrepreneurUuid, file_uuid = :fileUuid, " +
            "status_payment = CAST(:statusPayment AS status_payment), final_date = :finalDateLastPayment, " +
            "initial_date_last_payment = :initialDateLastPayment " +
            "WHERE cocaiot_member.uuid = :memberUuid RETURNING TRUE")
    Boolean editMember(@Param("memberUuid")UUID memberUuid, @Param("companyUuid")UUID companyUuid,
                       @Param("entrepreneurUuid")UUID entrepreneurUuid, @Param("fileUuid")UUID fileUuid,
                       @Param("statusPayment")String statusPayment,
                       @Param("initialDateLastPayment")Date initialDateLastPayment,
                       @Param("finalDateLastPayment")Date finalDateLastPayment);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN COUNT(cocaiotMember)>0 THEN TRUE ELSE FALSE END " +
            "FROM cocaiot_members cocaiotMember WHERE (cocaiotMember.uuid <> :memberUuid) AND " +
            "(cocaiotMember.company_uuid = :companyUuid)")
    boolean isCompanyExists(@Param("memberUuid")UUID memberUuid,
                            @Param("companyUuid")UUID companyUuid);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN COUNT(cocaiotMember)>0 THEN TRUE ELSE FALSE END " +
            "FROM cocaiot_members cocaiotMember WHERE (cocaiotMember.uuid <> :memberUuid) AND " +
            "(cocaiotMember.entrepreneur_uuid = :entrepreneurUuid)")
    boolean isEntrepreneurExists(@Param("memberUuid")UUID memberUuid,
                                 @Param("entrepreneurUuid")UUID entrepreneurUuid);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM cocaiot_members cocaiot_member " +
            "WHERE cocaiot_member.uuid = :memberUuid RETURNING TRUE")
    Boolean removeMemberByUuid(@Param("memberUuid")UUID memberUuid);

//    @Query(nativeQuery = true, value = "SELECT * FROM cocaiot_members cocaiotMember " +
//            "INNER JOIN companies company ON (cocaiotMember.company_uuid IS NOT NULL AND company.uuid = cocaiotMember.company_uuid) " +
//            "LEFT JOIN countries country ON (country.uuid = company.country_uuid) " +
//            "LEFT JOIN regions region ON (region.uuid = company.region_uuid) " +
//            "LEFT JOIN persons person ON (person.uuid = company.director_uuid) " +
//            "LEFT JOIN type_activities_companies type_activity_company ON (type_activity_company.company_uuid = company.uuid) " +
//            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = type_activity_company.type_activity_uuid) " +
//            "WHERE (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
//            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
//            "((person.patronomic_name IS NOT NULL) AND (LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')))")
//    List<CocaiotMember>getCocaiotMemberCompaniesBySearchKey(@Param("searchKey")String searchKey,
//                                                            Pageable pageable);

//    @Query(nativeQuery = true, value = "SELECT * FROM cocaiot_members cocaiotMember " +
//            "INNER JOIN entrepreneurs entrepreneur ON (cocaiotMember.entrepreneur_uuid IS NOT NULL AND " +
//            "entrepreneur.uuid = cocaiotMember.entrepreneur_uuid) " +
//            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
//            "LEFT JOIN countries country ON (country.uuid = person.country_uuid) " +
//            "LEFT JOIN regions region ON (region.uuid = person.region_uuid) " +
//            "WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
//            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
//            "((person.patronomic_name IS NOT NULL) AND (LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%'))) OR " +
//            "(LOWER(country.name) LIKE CONCAT(:searchKey, '%')) OR (LOWER(region.name) LIKE CONCAT(:searchKey, '%')) OR " +
//            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%'))")
//    List<CocaiotMember>getCocaiotMemberEntrepreneursBySearchKey(@Param("searchKey")String searchKey,
//                                                                Pageable pageable);

//    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(cocaiotMember.uuid)) FROM cocaiot_members cocaiotMember " +
//            "INNER JOIN companies company ON (cocaiotMember.company_uuid IS NOT NULL AND company.uuid = cocaiotMember.company_uuid) " +
//            "LEFT JOIN countries country ON (country.uuid = company.country_uuid) " +
//            "LEFT JOIN regions region ON (region.uuid = company.region_uuid) " +
//            "LEFT JOIN persons person ON (person.uuid = company.director_uuid) " +
//            "LEFT JOIN type_activities_companies type_activity_company ON (type_activity_company.company_uuid = company.uuid) " +
//            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = type_activity_company.type_activity_uuid) " +
//            "WHERE (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
//            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
//            "((person.patronomic_name IS NOT NULL) AND (LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')))")
//    int getAmountCompanyMembersBySearchKey(@Param("searchKey")String searchKey);

//    @Query(nativeQuery = true, value = "SELECT COUNT(cocaiotMember.uuid) FROM cocaiot_members cocaiotMember " +
//            "INNER JOIN entrepreneurs entrepreneur ON (cocaiotMember.entrepreneur_uuid IS NOT NULL AND " +
//            "entrepreneur.uuid = cocaiotMember.entrepreneur_uuid) " +
//            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
//            "LEFT JOIN countries country ON (country.uuid = person.country_uuid) " +
//            "LEFT JOIN regions region ON (region.uuid = person.region_uuid) " +
//            "WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
//            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
//            "((person.patronomic_name IS NOT NULL) AND (LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%'))) OR " +
//            "(LOWER(country.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(region.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
//            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%'))")
//    int getAmountEntrepreneurMembersBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(cocaiot_member.uuid)) FROM cocaiot_members cocaiot_member " +
            "LEFT JOIN companies company ON (company.uuid = cocaiot_member.company_uuid) " +
            "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.uuid = cocaiot_member.entrepreneur_uuid) " +
            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
            "LEFT JOIN type_activities_companies type_activity_company ON (type_activity_company.company_uuid = company.uuid) " +
            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = type_activity_company.type_activity_uuid) " +
            "WHERE (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%'))")
    int getAmountCocaiotMembersBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT DISTINCT(cocaiot_member.uuid), cocaiot_member.company_uuid, " +
            "cocaiot_member.entrepreneur_uuid, cocaiot_member.file_uuid, cocaiot_member.status_payment, " +
            "cocaiot_member.initial_date, cocaiot_member.final_date, cocaiot_member.initial_date_last_payment, " +
            "cocaiot_member.created, cocaiot_member.updated " +
            "FROM cocaiot_members cocaiot_member " +
            "LEFT JOIN companies company ON (company.uuid = cocaiot_member.company_uuid) " +
            "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.uuid = cocaiot_member.entrepreneur_uuid) " +
            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
            "LEFT JOIN type_activities_companies type_activity_company ON (type_activity_company.company_uuid = company.uuid) " +
            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = type_activity_company.type_activity_uuid) " +
            "WHERE (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')) ORDER BY cocaiot_member.created DESC")
    List<CocaiotMember>getCocaiotMembersBySearchKey(Pageable pageable, @Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT * FROM cocaiot_members cocaiotMember " +
            "WHERE cocaiotMember.uuid = :memberUuid")
    CocaiotMember getCocaiotMemberByUuid(@Param("memberUuid")UUID memberUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE cocaiot_members SET " +
            "status_payment = CAST('PAYMENT_TIME_HAS_EXPIRED' AS status_payment) " +
            "WHERE final_date < CURRENT_DATE ")
    void checkStatusPaymentCocaiotMembers();

    @Query("SELECT COUNT(cocaiotMember) FROM CocaiotMember cocaiotMember")
    int getAmountCocaiotMembers();

    @Query(nativeQuery = true, value = "SELECT COUNT(cocaiot_member) FROM cocaiot_members cocaiot_member " +
            "WHERE CAST(cocaiot_member.created AS DATE) BETWEEN :initialDate AND :finalDate")
    int getAmountCocaiotMembersBetweenDates(@Param("initialDate") Date initialDate,
                                            @Param("finalDate")Date finalDate);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(cocaiot_member)) FROM cocaiot_members cocaiot_member " +
            "LEFT JOIN companies company ON (company.country_uuid = :countryUuid AND company_uuid = cocaiot_member.company_uuid) " +
            "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.uuid = cocaiot_member.entrepreneur_uuid) " +
            "LEFT JOIN persons person ON (person.country_uuid = :countryUuid AND person.uuid = entrepreneur.person_uuid) " +
            "WHERE ((company.country_uuid = :countryUuid) OR (person.country_uuid = :countryUuid)) AND " +
            "(CAST(cocaiot_member.created AS DATE) BETWEEN :initialDate AND :finalDate)")
    int getAmountCocaiotMembersByCountryUuidAndBetweenDates(@Param("countryUuid")UUID countryUuid,
                                                            @Param("initialDate") Date initialDate,
                                                            @Param("finalDate")Date finalDate);

}
