package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Country;

import java.util.List;
import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO countries(name) VALUES(:countryName) ON CONFLICT DO NOTHING " +
            "RETURNING TRUE")
    Boolean addCountry(@Param("countryName")String countryName);

    @Query("SELECT CASE WHEN COUNT(country)>0 THEN TRUE ELSE FALSE END FROM Country country " +
            "WHERE country.uuid = :countryUuid")
    boolean isCountryExistsByUuid(@Param("countryUuid")UUID countryUuid);

    @Query("SELECT CASE WHEN COUNT(country)>0 THEN TRUE ELSE FALSE END FROM Country country " +
            "WHERE (LOWER(country.name) = :countryName) AND (country.uuid <> :countryUuid)")
    boolean isCountryExistsByName(@Param("countryName")String countryName,
                                  @Param("countryUuid")UUID countryUuid);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE countries SET name = :countryName WHERE uuid = :countryUuid RETURNING TRUE")
    Boolean editCountryByUuid(@Param("countryUuid")UUID countryUuid,
                              @Param("countryName")String countryName);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM countries country WHERE country.uuid = :countryUuid RETURNING TRUE")
    Boolean removeCountryByUuid(@Param("countryUuid")UUID countryUuid);

    @Query(nativeQuery = true, value = "SELECT * FROM countries country " +
            "WHERE (LOWER(country.name) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(CAST(country.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    List<Country>getCountriesBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM countries country " +
            "WHERE (LOWER(country.name) LIKE CONCAT('%',:searchKey, '%')) OR " +
            "(CAST(country.amount_company AS VARCHAR) LIKE CONCAT(:searchKey, '%'))")
    int getAmountCountriesBySearchKey(@Param("searchKey")String searchKey);

    @Query("SELECT country FROM Country country WHERE country.uuid = :countryUuid")
    Country getCountryByUuid(@Param("countryUuid")UUID countryUuid);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'countries') AND (data_type <> 'uuid')")
    List<String>getCountryColumns();

    @Query("SELECT NEW Country(country.uuid AS uuid, country.name AS name) FROM Country country " +
            "WHERE LOWER(country.name) LIKE CONCAT('%', :searchKey, '%') ORDER BY country.name")
    List<Country>getCountriesByName(@Param("searchKey")String searchKey);

}
