package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    @Transactional
    ResponseTransfer addUser(User user, UUID roleUuid);

    @Transactional
    ResponseTransfer editUser(User user, UUID roleUuid);

    @Transactional
    ResponseTransfer removeUserByUuid(UUID userUuid);

    int getAmountUsersBySearchKey(String searchKey);

    ResponseTransfer getUserDTOSBySearchKey(String searchKey, int page, int size, List<String> sortBy,
                                            List<SortType> sortTypes);

    ResponseTransfer getUserDTOByUuid(UUID userUuid);

    User getUserByEmail(String email);

    ResponseTransfer getUserDTOByEmail(String email);
}
