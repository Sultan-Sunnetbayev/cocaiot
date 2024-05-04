package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tm.salam.cocaiot.models.Person;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO persons(name, surname, patronomic_name, birth_place, birth_date, " +
            "image, copy_passport, country_uuid, region_uuid, full_address_of_residence, phone_number, fax, email, " +
            "education, experience, knowledge_of_languages) " +
            "VALUES(:name, :surname, :patronomicName, :birthPlace, :birthDate, :imageUuid, :copyPassport, :countryUuid, " +
            ":regionUuid, :fullAddressOfResidence, :phoneNumber, :fax, :email, :education, :experience, " +
            ":knowledgeOfLanguages) " +
            "ON CONFLICT DO NOTHING RETURNING CAST(uuid AS VARCHAR)")
    UUID addPerson(@Param("name")String name, @Param("surname")String surname, @Param("patronomicName")String patronomicName,
                   @Param("birthPlace")String birthPlace, @Param("birthDate") Date birthDate,
                   @Param("imageUuid")UUID imageUuid, @Param("copyPassport")UUID copyPassport,
                   @Param("countryUuid")UUID countryUuid, @Param("regionUuid")UUID regionUuid,
                   @Param("fullAddressOfResidence")String fullAddressOfResidence, @Param("phoneNumber")String phoneNumber,
                   @Param("fax")String fax, @Param("email")String email, @Param("education")String education,
                   @Param("experience")String experience, @Param("knowledgeOfLanguages")String knowledgeOfLanguages);

    @Query("SELECT CASE WHEN COUNT(person)>0 THEN TRUE ELSE FALSE END FROM Person person " +
            "WHERE (person.email = :personEmail) AND (person.uuid <> :personUuid)")
    boolean isPersonExistsByEmail(@Param("personUuid")UUID personUuid,
                                  @Param("personEmail")String personEmail);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE  persons person SET name = :name, surname = :surname, " +
            "patronomic_name = :patronomicName, birth_place = :birthPlace, birth_date = :birthDate, " +
            "image = :imageUuid, copy_passport = :copyPassport, country_uuid = :countryUuid, region_uuid = :regionUuid, " +
            "full_address_of_residence = :fullAddressOfResidence, phone_number = :phoneNumber, fax = :fax, " +
            "email = :email, education = :education, experience = :experience, " +
            "knowledge_of_languages = :knowledgeOfLanguages " +
            "WHERE person.uuid = :uuid RETURNING TRUE")
    Boolean editPerson(@Param("uuid")UUID uuid, @Param("name")String name, @Param("surname")String surname,
                       @Param("patronomicName")String patronomicName, @Param("birthPlace")String birthPlace,
                       @Param("birthDate") Date birthDate, @Param("imageUuid")UUID imageUuid,
                       @Param("copyPassport")UUID copyPassport, @Param("countryUuid")UUID countryUuid,
                       @Param("regionUuid")UUID regionUuid, @Param("fullAddressOfResidence")String fullAddressOfResidence,
                       @Param("phoneNumber")String phoneNumber, @Param("fax")String fax, @Param("email")String email,
                       @Param("education")String education, @Param("experience")String experience,
                       @Param("knowledgeOfLanguages")String knowledgeOfLanguages);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM persons person WHERE person.uuid = :personUuid RETURNING TRUE")
    Boolean removePersonByUuid(@Param("personUuid")UUID personUuid);

    @Query("SELECT COUNT(person) FROM Person person WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomicName) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.fullAddressOfResidence) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.education) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%'))")
    int getAmountPersonsBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT * FROM persons person " +
            "LEFT JOIN countries country ON country.uuid = person.country_uuid " +
            "LEFT JOIN regions region ON region.uuid= person.region_uuid " +
            "WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomic_name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.full_address_of_residence) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.education) LIKE CONCAT('%', :searchKey, '%')) OR " +
            "(LOWER(person.experience) LIKE CONCAT('%', :searchKey, '%'))")
    List<Person> getPersonsBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'persons') AND (data_type <> 'uuid')")
    List<String>getPersonColumns();

    @Query("SELECT person FROM Person person WHERE person.uuid = :personUuid")
    Person getPersonByUuid(@Param("personUuid")UUID personUuid);

    @Query("SELECT NEW Person(person.uuid AS uuid, person.name AS name, person.surname AS surname, person.patronomicName AS " +
            "patronomicName) FROM Person person WHERE (person.isEntrepreneur = FALSE) AND " +
            "((LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.patronomicName) LIKE CONCAT(:searchKey, '%'))) " +
            "ORDER BY person.surname, person.name, person.patronomicName")
    List<Person>getIsNotEntrepreneurPersonsBySearchKey(@Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(person)>0 THEN TRUE ELSE FALSE END FROM Person person WHERE person.uuid = :personUuid")
    boolean isPersonExistsByUuid(@Param("personUuid")UUID personUuid);

    @Query("SELECT COUNT(person) FROM Person person WHERE person.uuid IN :personUuids")
    int getCountPersonsByUuids(@Param("personUuids")List<UUID> personUuids);

    @Transactional
    @Modifying
    @Query("UPDATE Person person SET person.isEntrepreneur = :isEntrepreneur WHERE person.uuid = :personUuid")
    void changeValueIsEntrepreneurPersonByUuid(@RequestParam("personUuid")UUID personUuid,
                                               @RequestParam("isEntrepreneur")boolean isEntrepreneur);

    @Query("SELECT NEW Person(person.uuid AS uuid, person.name AS name, person.surname AS surname, person.patronomicName) " +
            "FROM Person person WHERE (LOWER(person.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(person.surname) LIKE CONCAT( :searchKey, '%')) OR " +
            "(LOWER(person.patronomicName) LIKE CONCAT(:searchKey, '%')) " +
            "ORDER BY person.surname, person.name, person.patronomicName")
    List<Person>getPersonsByNameOrSurnameOrPatronomicName(@Param("searchKey")String searchKey);

}
