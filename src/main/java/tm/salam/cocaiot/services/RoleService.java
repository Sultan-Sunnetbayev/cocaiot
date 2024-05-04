package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    @Transactional
    ResponseTransfer addRole(Role role, List<UUID> categoryUuids, List<Boolean> privilages);

    ResponseTransfer editRole(Role role, List<UUID> categoryUuids, List<Boolean> privilages);

    ResponseTransfer removeRole(UUID roleUuid);

    int getAmountRolesBySearchKey(String searchKey);

    ResponseTransfer getRoleDTOSBySearchKey(String searchKey, int page, int size, List<String> sortBy,
                                           List<SortType> sortTypes);

    ResponseTransfer getRoleDTOByUuid(UUID roleUuid);

    ResponseTransfer getRoleDTOSByName(String searchKey);

    ResponseTransfer isRoleExistsByUuid(UUID roleUuid);

    boolean getPrivilageByRoleNameAndCategoryName(String roleName, String categoryName);
}
