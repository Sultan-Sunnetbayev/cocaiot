package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TypeActivityRepository extends JpaRepository<TypeActivity, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO type_activities(name) VALUES(:name) ON CONFLICT DO NOTHING " +
            "RETURNING TRUE")
    Boolean addTypeActivity(@Param("name")String name);

    @Query("SELECT CASE WHEN COUNT(typeActivity)>0 THEN TRUE ELSE FALSE END FROM TypeActivity typeActivity " +
            "WHERE (LOWER(typeActivity.name) = :typeActivityName) AND (typeActivity.uuid <> :typeActivityUuid)")
    boolean isTypeActivityExistsByName(@Param("typeActivityName")String typeActivityName,
                                       @Param("typeActivityUuid")UUID typeActivityUuid);

    @Query("SELECT CASE WHEN COUNT(typeActivity)>0 THEN TRUE ELSE FALSE END FROM TypeActivity typeActivity " +
            "WHERE typeActivity.uuid = :typeActivityUuid")
    boolean isTypeActivityExistsByUuid(@Param("typeActivityUuid")UUID typeActivityUuid);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE type_activities type_activity SET name = :typeActivityName " +
            "WHERE type_activity.uuid = :typeActivityUuid RETURNING TRUE")
    Boolean editTypeActivityByUuid(@Param("typeActivityUuid")UUID typeActivityUuid,
                                   @Param("typeActivityName")String typeActivityName);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM type_activities type_activity " +
            "WHERE type_activity.uuid = :typeActivityUuid RETURNING TRUE")
    Boolean removeTypeActivityByUuid(@Param("typeActivityUuid")UUID typeActivityUuid);

    @Query(nativeQuery = true, value = "SELECT * FROM type_activities type_activity " +
            "WHERE (LOWER(type_activity.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(CAST(amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    List<TypeActivity> getTypeActivitiesBySearchKey(@Param("searchKey") String searchKey, Pageable pageable);

    @Query("SELECT typeActivity FROM TypeActivity typeActivity WHERE typeActivity.uuid = :typeActivityUuid")
    TypeActivity getTypeActivityByUuid(@Param("typeActivityUuid")UUID typeActivityUuid);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM type_activities type_activity " +
            "WHERE (LOWER(type_activity.name) LIKE CONCAT('%',:searchKey,'%')) OR " +
            "(CAST(amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    int getAmountTypeActivityBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'type_activities') AND (data_type <> 'uuid')")
    List<String>getTypeActivityColumns();

    @Query("SELECT NEW TypeActivity(typeActivity.uuid AS uuid, typeActivity.name AS name) FROM TypeActivity typeActivity " +
            "WHERE LOWER(typeActivity.name) LIKE CONCAT('%', :searchKey, '%') ORDER BY typeActivity.name")
    List<TypeActivity>getTypeActivitiesByName(@Param("searchKey")String searchKey);

    @Query("SELECT COUNT(typeActivity) FROM TypeActivity typeActivity WHERE typeActivity.uuid IN :typeActivityUuids")
    int getAmountTypeActivityByUuids(@Param("typeActivityUuids")List<UUID>typeAcitivtyUuids);

    @Query("SELECT NEW TypeActivity(typeActivity.uuid AS uuid, typeActivity.name AS name) FROM TypeActivity typeActivity " +
            "INNER JOIN typeActivity.companies company ON (company.uuid IN :companyUuids) " +
            "GROUP BY typeActivity.uuid ORDER BY typeActivity.name")
    List<TypeActivity>getTypeActivitiesByCompanyUuids(@Param("companyUuids")List<UUID>companyUuids);

    @Transactional
    @Modifying
    @Query("UPDATE TypeActivity  typeActivity SET typeActivity.amountCompany=typeActivity.amountCompany + :value " +
            "WHERE typeActivity.uuid IN  :typeActivityUuids")
    void incrementAmountCompanyByTypeActivityUuids(@Param("typeActivityUuids")List<UUID>typeActivityUuids,
                                                   @Param("value")int value);

    @Transactional
    @Modifying
    @Query("UPDATE TypeActivity typeActivity SET typeActivity.amountCompany=typeActivity.amountCompany - :value " +
            "WHERE typeActivity.uuid IN :typeActivityUuids")
    void decrementAmountCompanyByTypeActivityUuids(@Param("typeActivityUuids")List<UUID>typeActivityUuids,
                                                   @Param("value")int value);

    @Query(nativeQuery = true, value = "SELECT CAST(type_activity_uuid AS VARCHAR) FROM entrepreneurs_type_activities " +
            "WHERE entrepreneur_uuid = :entrepreneurUuid")
    List<UUID>getTypeActivityUuidsByEntrepreneurUuid(@Param("entrepreneurUuid")UUID entrepreneurUuid);

}
