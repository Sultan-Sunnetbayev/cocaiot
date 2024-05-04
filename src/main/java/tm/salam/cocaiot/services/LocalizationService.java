package tm.salam.cocaiot.services;

import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Country;
import tm.salam.cocaiot.models.Region;

import java.util.List;
import java.util.UUID;

public interface LocalizationService {

    @Transactional
    ResponseTransfer addCountry(Country country);

    @Transactional
    ResponseTransfer editCountryByUuid(Country country);

    @Transactional
    ResponseTransfer removeCountryByUuid(UUID countryUuid);

    ResponseTransfer getAllCountryDTOS(String searchKey, int page, int size, List<String> sortBy, List<SortType>sortTypes);

    ResponseTransfer getCountryDTOByUuid(UUID countryUuid);

    int getAmountCountriesBySearchKey(String searchKey);

    ResponseTransfer getColumnNameCountryById(int id);

    @Transactional
    ResponseTransfer addRegion(Region region, UUID countryUuid);

    @Transactional
    ResponseTransfer editRegion(Region region, UUID countryUuid);

    @Transactional
    ResponseTransfer removeRegionByUuid(UUID regionUuid);

    ResponseTransfer getAllRegionDTOS(String searchKey, int page, int size, List<String> sortBy,
                                      List<SortType> sortTypes);

    ResponseTransfer getRegionDTOByUuid(UUID countryUuid);

    int getAmountRegionsBySearchKey(String searchKey);

    ResponseTransfer getColumnNameRegionById(int id);

    ResponseTransfer getCountryDTOSByName(String searchKey);

    ResponseTransfer getRegionDTOSByCountryUuidAndName(UUID countryUuid, String searchKey);

    ResponseTransfer checkTheCorrectnessCountryAndRegion(UUID countryUuid, UUID regionUuid);
}
