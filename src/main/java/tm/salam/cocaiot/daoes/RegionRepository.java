package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Region;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO regions(name, country_uuid) VALUES(:regionName, :countryUuid) " +
            "ON CONFLICT DO NOTHING RETURNING TRUE")
    Boolean addRegion(@Param("regionName")String regionName,
                      @Param("countryUuid")UUID countryUuid);

    @Query("SELECT CASE WHEN COUNT(region)>0 THEN TRUE ELSE FALSE END FROM Region region " +
            "WHERE (LOWER(region.name) = :regionName) AND (region.uuid <> :regionUuid)")
    boolean isRegionExistsByName(@Param("regionName")String regionName,
                                 @Param("regionUuid")UUID regionUuid);

    @Query("SELECT CASE WHEN COUNT(region)>0 THEN TRUE ELSE FALSE END FROM Region region WHERE region.uuid = :regionUuid")
    boolean isRegionExistsByUuid(@Param("regionUuid")UUID regionUuid);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE regions region SET name = :regionName, country_uuid = :countryUuid " +
            "WHERE region.uuid = :regionUuid RETURNING TRUE")
    Boolean editRegion(@Param("regionUuid")UUID regionUuid,
                       @Param("regionName")String regionName,
                       @Param("countryUuid")UUID countryUuid);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM regions region WHERE region.uuid = :regionUuid RETURNING TRUE")
    Boolean removeRegionByUuid(@Param("regionUuid")UUID regionUuid);

    @Query(nativeQuery = true, value = "SELECT * FROM regions region " +
            "INNER JOIN countries country ON region.country_uuid = country.uuid " +
            "WHERE (LOWER(region.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(CAST(region.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(country.name) LIKE CONCAT('%', :searchKey, '%'))")
    List<Region>getRegionsBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT COUNT(region) FROM regions region " +
            "INNER JOIN countries country ON region.country_uuid = country.uuid " +
            "WHERE (LOWER(region.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(CAST(region.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(country.name) LIKE CONCAT('%', :searchKey, '%'))")
    int getAmountRegionsBySearchKey(@Param("searchKey")String searchKey);

    @Query("SELECT region FROM Region region WHERE region.uuid = :regionUuid")
    Region getRegionByUuid(@Param("regionUuid")UUID regionUuid);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'regions') AND (data_type <> 'uuid')")
    List<String>getRegionColumns();

    @Query("SELECT NEW Region(region.uuid AS uuid, region.name AS name) FROM Region region " +
            "WHERE (region.country.uuid = :countryUuid) AND " +
            "(LOWER(region.name) LIKE CONCAT('%', :searchKey, '%')) ORDER BY region.name")
    List<Region>getRegionsByCountryUuidAndName(@Param("countryUuid")UUID countryUuid,
                                               @Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(region)>0 THEN TRUE ELSE FALSE END FROM Region region " +
            "WHERE (region.uuid = :regionUuid) AND (region.country.uuid = :countryUuid)")
    boolean checkTheCorrectnessCountryAndRegion(@Param("countryUuid")UUID countryUuid,
                                                @Param("regionUuid")UUID regionUuid);

}
