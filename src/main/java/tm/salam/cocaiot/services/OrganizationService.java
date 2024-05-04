package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Organization;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

    @Transactional
    ResponseTransfer addOrganization(Organization organization);

    @Transactional
    ResponseTransfer editOrganization(Organization organization);

    @Transactional
    ResponseTransfer removeOrganization(UUID organizationUuid);

    ResponseTransfer getAllOrganizationDTOS(String searchKey, int page, int size, List<String> sortBy,
                                            List<SortType> sortTypes);

    int getAmountOrganizationsBySearchKey(String searchKey);

    ResponseTransfer getOrganizationDTOByUuid(UUID organizationUuid);

    ResponseTransfer getColumnNameOrganizationById(int id);

    ResponseTransfer getOrganizationDTOSByName(String searchKey);

    ResponseTransfer isOrganizationExistsByUuid(UUID organizationUuid);
}
