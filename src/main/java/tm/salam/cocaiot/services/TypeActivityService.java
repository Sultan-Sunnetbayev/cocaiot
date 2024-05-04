package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.List;
import java.util.UUID;

public interface TypeActivityService {

    @Transactional
    ResponseTransfer addTypeActivity(TypeActivity typeActivity);

    @Transactional
    ResponseTransfer editTypeActivityByUuid(TypeActivity typeActivity);

    @Transactional
    ResponseTransfer removeTypeActivityByUuid(UUID typeActivityUuid);

    ResponseTransfer getAllTypeActivityDTOS(String searchKey, int page, int size, List<String>sortBy,
                                            List<SortType>sortTypes);

    ResponseTransfer getTypeActivityDTOByUuid(UUID typeActivityUuid);

    int getAmountTypeActivityBySearchKey(String searchKey);

    ResponseTransfer getColumnNameTypeActivityById(int id);

    ResponseTransfer getTypeActivityDTOSByName(String searchKey);

    ResponseTransfer isTypeActivitiesExistsByUuids(List<UUID> typeActivityUuids);

    ResponseTransfer getTypeActivityDTOSByCompanyUuids(List<UUID> companyUuids);

    @Transactional
    void incrementAmountCompanyByTypeActivityUuids(List<UUID> typeActivityUuids, int value);

    @Transactional
    void decrementAmountCompanyByTypeActivityUuids(List<UUID> typeActivityUuids, int value);

    List<UUID> getTypeActivityUuidsByEntrepreneurUuid(UUID entrepreneurUuid);

}
