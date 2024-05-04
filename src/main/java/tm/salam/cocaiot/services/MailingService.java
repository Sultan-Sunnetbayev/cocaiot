package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Mailing;

import java.util.List;
import java.util.UUID;

public interface MailingService {

    @Transactional
    ResponseTransfer addMailing(Mailing mailing, UUID fileUuid, List<UUID> companyUuids, List<UUID>entrepreneurUuids);

    int getAmountMailingsBySearchKey(String searchKey);

    ResponseTransfer getMailingDTOSBySearchKey(String searchKey, int page, int size, List<String> sortBy,
                                               List<SortType> sortTypes);

    ResponseTransfer getMailingDTOByUuid(UUID mailingUuid);

    @Transactional
    ResponseTransfer removeMailingByUuid(UUID mailingUuid);
}
