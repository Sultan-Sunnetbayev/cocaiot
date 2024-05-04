package tm.salam.cocaiot.daoes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tm.salam.cocaiot.models.MailingCompany;

import java.util.UUID;

@Repository
public interface MailingCompanyRepository extends JpaRepository<MailingCompany, UUID> {

//    @Transactional
//    @Query(nativeQuery = true, value = "DELETE FROM mailings_companies mailing_company " +
//            "WHERE (mailing_company.mailing_uuid = :mailingUuid) AND (mailing_company.company_uuid = :companyUuid) " +
//            "RETURNING TRUE")
//    Boolean removeMailingCompanyByMailingCompanyUuid(@Param("mailingUuid")UUID mailingUuid,
//                                                     @Param("companyUuid")UUID companyUuid);

//    @Query(nativeQuery = true, value = "SELECT DISTINCT(mailing_company.uuid), mailing_company.mailing_uuid, " +
//            "mailing_company.company_uuid, mailing_company.created, mailing_company.updated FROM mailings_companies mailing_company " +
//            "INNER JOIN mailings mailing ON (mailing.uuid = mailing_company.mailing_uuid) " +
//            "INNER JOIN companies company ON (company.uuid = mailing_company.company_uuid) " +
//            "INNER JOIN type_activities_companies type_activity_company ON (type_activity_company.company_uuid = company.uuid) " +
//            "INNER JOIN type_activities type_activity ON (type_activity.uuid = type_activity_company.type_activity_uuid) " +
//            "WHERE (mailing.amount_company > 0) AND " +
//            "((LOWER(mailing.name) LIKE CONCAT('%', :searchKey, '%')) " +
//            "OR (LOWER(CAST(mailing.type_mailing AS VARCHAR)) LIKE CONCAT('%', :searchKey, '%')) " +
//            "OR (LOWER(company.name) LIKE CONCAT('%', :searchKey, '%')) " +
//            "OR (LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')))"
//    )
//    List<MailingCompany> getMailingCompaniesBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

//    @Query("SELECT mailingCompany FROM MailingCompany mailingCompany WHERE mailingCompany.uuid = :mailingCompanyUuid")
//    MailingCompany getMailingCompanyByUuid(@Param("mailingCompanyUuid")UUID mailingCompanyUuid);

}
