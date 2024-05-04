package tm.salam.cocaiot.daoes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.models.Role;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO roles(name) VALUES(:name) ON CONFLICT DO NOTHING " +
            "RETURNING CAST(uuid AS VARCHAR)")
    UUID addRole(@Param("name")String name);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO roles_categories(role_uuid, category_uuid, privilage) " +
            "VALUES(:roleUuid, :categoryUuid, :privilage) ON CONFLICT DO NOTHING")
    void addRoleCategoryPrivilage(@Param("roleUuid")UUID roleUuid, @Param("categoryUuid")UUID categoryUuid,
                                  @Param("privilage")boolean privilage);

    @Query("SELECT CASE WHEN COUNT(role)>0 THEN TRUE ELSE FALSE END FROM Role role " +
            "WHERE (LOWER(role.name) = :roleName) AND (role.uuid <> :roleUuid)")
    boolean isRoleExists(@Param("roleUuid")UUID roleUuid, @Param("roleName")String roleName);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE roles role SET name = :roleName WHERE role.uuid = :roleUuid RETURNING TRUE")
    Boolean editRole(@Param("roleUuid")UUID roleUuid, @Param("roleName")String roleName);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE roles_categories role_category SET privilage = :privilage " +
            "WHERE (role_category.role_uuid = :roleUuid) AND (role_category.category_uuid = :categoryUuid)")
    void editPrivilageCategoryByRoleUuid(@Param("roleUuid")UUID roleUuid, @Param("categoryUuid")UUID categoryUuid,
                                         @Param("privilage")boolean privilage);

    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM roles role WHERE role.uuid = :roleUuid RETURNING TRUE")
    Boolean removeRoleByUuid(@Param("roleUuid")UUID roleUuid);

    @Query("SELECT COUNT(role) FROM Role role WHERE (LOWER(role.name) LIKE CONCAT('%', :searchKey, '%'))")
    int getAmountRolesBySearchKey(@Param("searchKey")String searchKey);

    @Query(nativeQuery = true, value = "SELECT column_name FROM information_schema.columns WHERE " +
            "(table_schema = 'public') AND (table_name = 'roles') AND (data_type <> 'uuid')")
    List<String> getRoleColumns();

    @Query(nativeQuery = true, value = "SELECT * FROM roles role " +
            "WHERE (LOWER(role.name) LIKE CONCAT('%', :searchKey, '%'))")
    List<Role>getRolesBySearchKey(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT role FROM Role role WHERE role.uuid = :roleUuid")
    Role getRoleByUuid(@Param("roleUuid")UUID roleUuid);

    @Query("SELECT NEW Role(role.uuid AS uuid, role.name AS name) FROM Role role " +
            "WHERE LOWER(role.name) LIKE CONCAT('%', :searchKey, '%') ORDER BY role.name")
    List<Role>getRolesByName(@Param("searchKey")String searchKey);

    @Query("SELECT CASE WHEN COUNT(role)>0 THEN TRUE ELSE FALSE END FROM Role role WHERE role.uuid = :roleUuid")
    boolean isRoleExistsByUuid(@Param("roleUuid")UUID roleUuid);

    @Query("SELECT CASE WHEN COUNT(role)>0 THEN TRUE ELSE FALSE END FROM Role role WHERE LOWER(role.name) = :roleName")
    boolean isRoleNameExists(@Param("roleName") String roleName);

    @Query(nativeQuery = true, value = "SELECT privilage FROM roles_categories role_category " +
            "INNER JOIN roles role ON (role.name = :roleName AND role.uuid = role_category.role_uuid) " +
            "INNER JOIN categories category ON (category.name = :categoryName AND category.uuid = role_category.category_uuid) " +
            "WHERE (role.name = :roleName) AND (category.name = :categoryName)")
    Boolean getPrivilageByRoleNameAndCategoryName(@Param("roleName")String roleName,
                                                  @Param("categoryName")String categoryName);

}
