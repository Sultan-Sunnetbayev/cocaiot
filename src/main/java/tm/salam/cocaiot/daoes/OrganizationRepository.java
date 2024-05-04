package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Organization;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {


    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO organizations(name) VALUES(:organizationName) ON CONFLICT " +
            "DO NOTHING RETURNING TRUE")
    Boolean addOrganization(@Param("organizationName")String organizationName);

    @Query("SELECT CASE WHEN COUNT(organization)>0 THEN TRUE ELSE FALSE END FROM Organization organization " +
            "WHERE (LOWER(organization.name) = :organizationName) AND (organization.uuid <> :organizationUuid)")
    boolean isOrganizationExistsByName(@Param("organizationUuid")UUID organizationUuid,
                                       @Param("organizationName")String organizationName);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE organizations organization SET name = :organizationName " +
            "WHERE organization.uuid = :organizationUuid RETURNING TRUE")
    Boolean editOrganization(@Param("organizationUuid")UUID organizationUuid,
                             @Param("organizationName")String organizationName);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM organizations organization WHERE organization.uuid = :organizationUuid " +
            "RETURNING TRUE")
    Boolean removeOrganizationByUuid(@Param("organizationUuid")UUID organizationUuid);

    @Query(nativeQuery = true, value = "SELECT * FROM organizations organization " +
            "WHERE (LOWER(organization.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(CAST(organization.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    List<Organization> getOrganizationsBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM organizations organization " +
            "WHERE (LOWER(organization.name) LIKE CONCAT('%',:searchKey, '%')) OR " +
            "(CAST(organization.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    int getAmountOrganizationsBySearchKey(@Param("searchKey")String searchKey);

    @Query("SELECT organization FROM Organization organization WHERE organization.uuid = :organizationUuid")
    Organization getOrganizationByUuid(@Param("organizationUuid")UUID organizationUuid);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'organizations') AND (data_type <> 'uuid')")
    List<String>getOrganizationColumns();

    @Query("SELECT NEW Organization(organization.uuid AS uuid, organization.name AS name) FROM Organization organization " +
            "WHERE LOWER(organization.name) LIKE CONCAT('%', :searchKey, '%') ORDER BY organization.name")
    List<Organization>getOrganizationsByName(@Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(organization)>0 THEN TRUE ELSE FALSE END FROM Organization organization " +
            "WHERE organization.uuid = :organizationUuid")
    boolean isOrganizationExistsByUuid(@Param("organizationUuid")UUID organizationUuid);

}
