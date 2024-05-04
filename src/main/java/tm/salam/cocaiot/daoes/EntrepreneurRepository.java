package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Entrepreneur;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntrepreneurRepository extends JpaRepository<Entrepreneur, UUID> {


    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO entrepreneurs(person_uuid, web_site, type_work, logo, " +
            "membership_application_uuid, patent_certifying_payment_uuid, " +
            "entrepreneur_statistical_codes_uuid, certificate_of_foreign_economic_relations_uuid, " +
            "registration_certificate_of_entrepreneur_uuid, certificate_of_tax_registration_uuid) " +
            "VALUES(:personUuid, :webSite, :typeWork, :logoUuid, :membershipApplicationUuid, :patentCertifyingPaymentUuid, " +
            ":entrepreneurStatisticalCodesUuid, :certificateOfForeignEconomicRelationsUuid, " +
            ":registrationCertificateOfEntrepreneurUuid, :certificateOfTaxRegistrationUuid) " +
            "ON CONFLICT DO NOTHING RETURNING CAST(uuid AS VARCHAR)")
    UUID addEntrepreneur(@Param("personUuid")UUID personUuid,
                         @Param("webSite")String webSite,
                         @Param("typeWork")String typeWork,
                         @Param("logoUuid")UUID logoUuid,
                         @Param("membershipApplicationUuid")UUID membershipApplicationUuid,
                         @Param("patentCertifyingPaymentUuid")UUID patentCertifyingPaymentUuid,
                         @Param("entrepreneurStatisticalCodesUuid")UUID entrepreneurStatisticalCodesUuid,
                         @Param("certificateOfForeignEconomicRelationsUuid")UUID certificateOfForeignEconomicRelationsUuid,
                         @Param("registrationCertificateOfEntrepreneurUuid")UUID registrationCertificateOfEntrepreneurUuid,
                         @Param("certificateOfTaxRegistrationUuid")UUID certificateOfTaxRegistrationUuid);

    @Query("SELECT entrepreneur FROM Entrepreneur entrepreneur WHERE entrepreneur.uuid = :entrepreneurUuid")
    Entrepreneur getEntrepreneurByUuid(@Param("entrepreneurUuid")UUID entrepreneurUuid);

    @Query("SELECT CASE WHEN COUNT(entrepreneur)>0 THEN TRUE ELSE FALSE END FROM Entrepreneur entrepreneur " +
            "WHERE entrepreneur.person.uuid = :personUuid AND entrepreneur.uuid <> :entrepreneurUuid")
    boolean isPersonEntrepreneur(@Param("entrepreneurUuid")UUID entrepreneurUuid,
                                 @Param("personUuid")UUID personUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO entrepreneurs_type_activities(entrepreneur_uuid, type_activity_uuid) " +
            "VALUES(:entrepreneurUuid, :typeActivityUuid) ON CONFLICT DO NOTHING")
    void addTypeActivityToEntrepreneur(@Param("entrepreneurUuid")UUID entrepreneurUuid,
                                       @Param("typeActivityUuid")UUID typeActivityUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM entrepreneurs_type_activities entrepreneur_type_activity " +
            "WHERE entrepreneur_type_Activity.entrepreneur_uuid = :entrepreneurUuid")
    void removeTypeActivitiesByEntrepreneurUuid(@Param("entrepreneurUuid")UUID entrepreneurUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE entrepreneurs entrepreneur SET person_uuid = :personUuid, " +
            "web_site = :webSite, type_work = :typeWork, logo = :logoUuid, " +
            "membership_application_uuid = :membershipApplicationUuid, " +
            "patent_certifying_payment_uuid = :patentCertifyingPaymentUuid, " +
            "entrepreneur_statistical_codes_uuid = :entrepreneurStatisticalCodesUuid, " +
            "certificate_of_foreign_economic_relations_uuid = :certificateOfForeignEconomicRelationsUuid, " +
            "registration_certificate_of_entrepreneur_uuid = :registrationCertificateOfEntrepreneurUuid, " +
            "certificate_of_tax_registration_uuid = :certificateOfTaxRegistrationUuid " +
            "WHERE entrepreneur.uuid = :entrepreneurUuid")
    void editEntrepreneur(@Param("entrepreneurUuid")UUID entrepreneurUuid,
                          @Param("personUuid")UUID personUuid,
                          @Param("webSite")String webSite,
                          @Param("typeWork")String typeWork,
                          @Param("logoUuid")UUID logoUuid,
                          @Param("membershipApplicationUuid")UUID membershipApplicationUuid,
                          @Param("patentCertifyingPaymentUuid")UUID patentCertifyingPaymentUuid,
                          @Param("entrepreneurStatisticalCodesUuid")UUID entrepreneurStatisticalCodesUuid,
                          @Param("certificateOfForeignEconomicRelationsUuid")UUID certificateOfForeignEconomicRelationsUuid,
                          @Param("registrationCertificateOfEntrepreneurUuid")UUID registrationCertificateOfEntrepreneurUuid,
                          @Param("certificateOfTaxRegistrationUuid")UUID certificateOfTaxRegistrationUuid);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(entrepreneur.uuid)) FROM entrepreneurs entrepreneur " +
            "LEFT JOIN persons person ON entrepreneur.person_uuid = person.uuid " +
            "LEFT JOIN countries country ON country.uuid = person.country_uuid " +
            "LEFT JOIN regions region ON region.uuid= person.region_uuid " +
            "LEFT JOIN entrepreneurs_type_activities entrepreneur_type_activity " +
            "ON (entrepreneur_type_activity.entrepreneur_uuid = entrepreneur.uuid) " +
            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = entrepreneur_type_activity.type_activity_uuid) "+
            "WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.full_address_of_residence) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.education) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%'))")
    int getAmountEntrepreneursBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT DISTINCT(entrepreneur.uuid), entrepreneur.person_uuid, entrepreneur.web_site, " +
            "entrepreneur.type_work, entrepreneur.logo, entrepreneur.membership_application_uuid, " +
            "entrepreneur.patent_certifying_payment_uuid, entrepreneur.entrepreneur_statistical_codes_uuid, " +
            "entrepreneur.certificate_of_foreign_economic_relations_uuid," +
            "entrepreneur.registration_certificate_of_entrepreneur_uuid, " +
            "entrepreneur.certificate_of_tax_registration_uuid, " +
            "entrepreneur.is_cocaiot_member, entrepreneur.created, entrepreneur.updated FROM entrepreneurs entrepreneur " +
            "LEFT JOIN persons person ON entrepreneur.person_uuid = person.uuid " +
            "LEFT JOIN countries country ON country.uuid = person.country_uuid " +
            "LEFT JOIN regions region ON region.uuid= person.region_uuid " +
            "LEFT JOIN entrepreneurs_type_activities entrepreneur_type_activity " +
            "ON (entrepreneur_type_activity.entrepreneur_uuid = entrepreneur.uuid) " +
            "LEFT JOIN type_activities type_activity ON (type_activity.uuid = entrepreneur_type_activity.type_activity_uuid) "+
            "WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.full_address_of_residence) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.education) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) ORDER BY entrepreneur.created DESC")
    List<Entrepreneur> getEntrepreneursBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Transactional
    @Query(nativeQuery = true, value ="DELETE FROM entrepreneurs entrepreneur " +
            "WHERE entrepreneur.uuid = :entrepreneurUuid RETURNING TRUE")
    Boolean removeEntrepreneurByUuid(@Param("entrepreneurUuid")UUID entrepreneurUuid);

    @Query("SELECT NEW Entrepreneur(entrepreneur.uuid AS uuid, entrepreneur.person AS person) FROM Entrepreneur entrepreneur " +
            "WHERE (entrepreneur.isCocaiotMember = FALSE ) AND " +
            "((LOWER(entrepreneur.person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(entrepreneur.person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(entrepreneur.person.patronomicName) LIKE CONCAT(:searchKey, '%'))) " +
            "ORDER BY entrepreneur.person.surname, entrepreneur.person.name, entrepreneur.person.patronomicName")
    List<Entrepreneur>getIsNotCocaiotMemberEntrepreneursByFullName(@Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(entrepreneur)>0 THEN TRUE ELSE FALSE END FROM Entrepreneur entrepreneur " +
            "WHERE entrepreneur.uuid = :entrepreneurUuid")
    boolean isEntrepreneurExistsByUuid(@Param("entrepreneurUuid")UUID entrepreneurUuid);

    @Query("SELECT NEW Entrepreneur(entrepreneur.uuid AS uuid, entrepreneur.person AS person) FROM Entrepreneur entrepreneur " +
            "WHERE (LOWER(entrepreneur.person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(entrepreneur.person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(entrepreneur.person.patronomicName) LIKE CONCAT(:searchKey, '%')) " +
            "ORDER BY entrepreneur.person.surname, entrepreneur.person.name, entrepreneur.person.patronomicName")
    List<Entrepreneur>getEntrepreneursByFullName(@Param("searchKey")String searchKey);

    @Query("SELECT NEW Entrepreneur(entrepreneur.uuid AS uuid, entrepreneur.person AS person) FROM Entrepreneur entrepreneur " +
            "INNER JOIN entrepreneur.typeActivities typeActivity ON typeActivity.uuid IN :typeActivityUuids " +
            "GROUP BY entrepreneur.uuid")
    List<Entrepreneur>getEntrepreneursByTypeActivityUuids(@Param("typeActivityUuids")List<UUID>typeActivityUuids);

    @Query(nativeQuery = true, value = "SELECT person.email FROM entrepreneurs entrepreneur " +
            "INNER JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
            "WHERE (person.email IS NOT NULL) AND (entrepreneur.uuid IN :entrepreneurUuids)")
    List<String>getEntrepreneurEmailsByUuids(@Param("entrepreneurUuids")List<UUID>entrepreneurUuids);

    @Query("SELECT COUNT(entrepreneur) FROM Entrepreneur entrepreneur WHERE entrepreneur.uuid IN :entrepreneurUuids")
    int getAmountEntrepreneursByUuids(@Param("entrepreneurUuids")List<UUID>entrepreneurUuids);

    @Query(nativeQuery = true, value = "SELECT * FROM entrepreneurs entrepreneur " +
                "INNER JOIN entrepreneurs_type_activities entrepreneur_type_activity " +
                    "ON ((entrepreneur_type_activity.type_activity_uuid=:typeActivityUuid) AND " +
                        "(entrepreneur_type_activity.entrepreneur_uuid=entrepreneur.uuid)) " +
                "INNER JOIN persons person ON (person.uuid = entrepreneur.person_uuid)")
    List<Entrepreneur>getEntrepreneurNamesByTypeActivityUuid(@Param("typeActivityUuid")UUID typeActivityUuid);

}
