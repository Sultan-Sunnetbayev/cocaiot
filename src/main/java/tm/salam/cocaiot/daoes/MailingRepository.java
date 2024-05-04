package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Mailing;

import java.util.List;
import java.util.UUID;

@Repository
public interface MailingRepository extends JpaRepository<Mailing, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO mailings(name, text, file_uuid, type_mailing) " +
            "VALUES(:name, :text, :fileUuid, CAST(:typeMailing AS type_mailing)) ON CONFLICT DO NOTHING " +
            "RETURNING CAST(uuid AS VARCHAR)")
    UUID addMailing(@Param("name")String name, @Param("text")String text, @Param("fileUuid")UUID fileUuid,
                    @Param("typeMailing")String typeMailing);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO mailings_companies(mailing_uuid, company_uuid) " +
            "VALUES(:mailingUuid, :companyUuid) ON CONFLICT DO NOTHING")
    void addCompanyToMailing(@Param("mailingUuid")UUID mailingUuid, @Param("companyUuid")UUID companyUuid);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT(mailing.uuid)) FROM mailings mailing " +
            "LEFT JOIN mailings_companies mailing_company ON (mailing_company.mailing_uuid = mailing.uuid) " +
            "LEFT JOIN companies company ON (company.uuid = mailing_company.company_uuid) " +
            "LEFT JOIN mailings_entrepreneurs mailing_entrepreneur ON (mailing_entrepreneur.mailing_uuid = mailing.uuid) " +
            "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.uuid = mailing_entrepreneur.entrepreneur_uuid) " +
            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
            "WHERE (LOWER(mailing.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(CAST(mailing.type_mailing AS VARCHAR)) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%'))")
    int getAmountMailingsBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT DISTINCT(mailing.uuid), mailing.name, SUBSTRING(mailing.text, 1, 100) AS text, " +
            "mailing.file_uuid, mailing.type_mailing, mailing.created, mailing.updated " +
            "FROM mailings mailing " +
            "LEFT JOIN mailings_companies mailing_company ON (mailing_company.mailing_uuid = mailing.uuid) " +
            "LEFT JOIN companies company ON (company.uuid = mailing_company.company_uuid) " +
            "LEFT JOIN mailings_entrepreneurs mailing_entrepreneur ON (mailing_entrepreneur.mailing_uuid = mailing.uuid) " +
            "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.uuid = mailing_entrepreneur.entrepreneur_uuid) " +
            "LEFT JOIN persons person ON (person.uuid = entrepreneur.person_uuid) " +
            "WHERE (LOWER(mailing.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(CAST(mailing.type_mailing AS VARCHAR)) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%'))")
    List<Mailing> getMailingsBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'mailings') AND (data_type <> 'uuid')")
    List<String>getMailingColumns();

    @Query("SELECT mailing FROM Mailing mailing WHERE mailing.uuid = :mailingUuid")
    Mailing getMailingByUuid(@Param("mailingUuid")UUID mailingUuid);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM mailings mailing WHERE mailing.uuid = :mailingUuid RETURNING TRUE")
    Boolean removeMailingByUuid(@Param("mailingUuid")UUID mailingUuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO mailings_entrepreneurs(mailing_uuid, entrepreneur_uuid) " +
            "VALUES(:mailingUuid, :entrepreneurUuid) ON CONFLICT DO NOTHING")
    void addEntrepreneurToMailing(@Param("mailingUuid")UUID mailingUuid,
                                  @Param("entrepreneurUuid")UUID entrepreneurUuid);

}
