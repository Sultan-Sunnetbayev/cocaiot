package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO users(name, surname, patronomic_name, email, password, role_uuid) " +
            "VALUES(:name, :surname, :patronomicName, :email, :password, :roleUuid) ON CONFLICT DO NOTHING " +
            "RETURNING TRUE")
    Boolean addUser(@Param("name")String name, @Param("surname")String surname,
                    @Param("patronomicName")String patronomicName, @Param("email")String email,
                    @Param("password")String password, @Param("roleUuid")UUID roleUuid);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE users userr SET name = :name, surname = :surname, " +
            "patronomic_name = :patronomicName, email = :email, password = :password, role_uuid = :roleUuid " +
            "WHERE userr.uuid = :uuid RETURNING TRUE")
    Boolean editUserByUuid(@Param("uuid")UUID uuid, @Param("name")String name, @Param("surname")String surname,
                           @Param("patronomicName")String patronomicName, @Param("email")String email,
                           @Param("password")String password, @Param("roleUuid")UUID roleUuid);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM users userr WHERE userr.uuid = :userUuid RETURNING TRUE")
    Boolean removeUserByUuid(@Param("userUuid")UUID userUuid);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM users " +
            "INNER JOIN roles role ON (role.uuid = users.role_uuid) " +
            "WHERE (LOWER(users.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(users.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(users.patronomic_name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(role.name) LIKE CONCAT('%', :searchKey, '%'))")
    int getAmountUsersBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT * FROM users userr " +
            "INNER JOIN roles role ON (role.uuid = userr.role_uuid) " +
            "WHERE (LOWER(userr.name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(userr.surname) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(userr.patronomic_name) LIKE CONCAT(:searchKey, '%')) OR " +
            "(LOWER(role.name) LIKE CONCAT('%', :searchKey, '%'))")
    List<User>getUsersBySearchKey(Pageable pageable, @Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns " +
            "WHERE (table_schema = 'public') AND (table_name = 'users') AND (data_type <> 'uuid')")
    List<String>getUserColumns();

    @Query("SELECT user FROM User user WHERE user.uuid = :userUuid")
    User getUserByUuid(@Param("userUuid")UUID userUuid);

    @Query("SELECT user FROM User user WHERE user.email = :userEmail")
    User getUserByEmail(@Param("userEmail")String email);

}
