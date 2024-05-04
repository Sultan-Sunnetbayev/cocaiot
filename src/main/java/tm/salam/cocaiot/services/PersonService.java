package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Person;

import java.util.List;
import java.util.UUID;

public interface PersonService {

    @Transactional
    ResponseTransfer addPerson(Person person, UUID imageUuid, UUID copyPassportUuid, UUID countryUuid, UUID regionUuid);

    @Transactional
    ResponseTransfer editPerson(Person person, UUID imageUuid, UUID copyPassportUuid, UUID countryUuid, UUID regionUuid);

    @Transactional
    ResponseTransfer removePerson(UUID personUuid);

    int getAmountPersonsBySearchKey(String searchKey);

    ResponseTransfer getAllPersonDTOS(String searchKey, int page, int size, List<String> sortBy,
                                      List<SortType> sortTypes);

    boolean isPersonColumnsExists(String withStart, List<String> columnNames);

    List<String> parsePersonColumns(String withStart, List<String> columnNames);

    ResponseTransfer getPersonDTOByUuid(UUID personUuid);

    ResponseTransfer getColumnNamePersonById(int id);

    ResponseTransfer getIsNotEntrepreneurPersonsBySearchKey(String searchKey);

    ResponseTransfer isPersonsExistsByUuid(UUID... personUuid);

    @Transactional
    void changeValueIsEntrepreneurPersonByUuid(UUID personUuid, boolean isEntrepreneur);

    ResponseTransfer getPersonDTOSByFullName(String searchKey);
}
