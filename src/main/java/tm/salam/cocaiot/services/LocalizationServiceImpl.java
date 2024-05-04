package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.CountryRepository;
import tm.salam.cocaiot.daoes.RegionRepository;
import tm.salam.cocaiot.dtoes.CountryDTO;
import tm.salam.cocaiot.dtoes.RegionDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Country;
import tm.salam.cocaiot.models.Region;

import java.util.*;


@Service
public class LocalizationServiceImpl implements LocalizationService {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final FilterBuilder filterBuilder;

    public LocalizationServiceImpl(CountryRepository countryRepository, RegionRepository regionRepository,
                                   FilterBuilder filterBuilder) {
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addCountry(final Country country){

        final ResponseTransfer responseTransfer;
        final Boolean isAdded=countryRepository.addCountry(country.getName());

        if(isAdded==null || !isAdded){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00016")
                    .message("error country don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00011")
                    .message("accept country successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editCountryByUuid(final Country country){

        final ResponseTransfer responseTransfer;

        if(country.getUuid()==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00017")
                    .message("error edited country uuid invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        if(isCountryExistsByName(country.getName(), country.getUuid())){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00019")
                    .message("error edited country name already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final Boolean isEdited=countryRepository.editCountryByUuid(country.getUuid(), country.getName());

        if(isEdited==null || !isEdited){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00020")
                    .message("error country don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00012")
                    .message("accept country successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isCountryExistsByUuid(final UUID countryUuid){

        return countryRepository.isCountryExistsByUuid(countryUuid);
    }

    private boolean isCountryExistsByName(final String countryName, final UUID countryUuid){

        return countryRepository.isCountryExistsByName(countryName.toLowerCase(Locale.ROOT), countryUuid);
    }

    @Override
    @Transactional
    public ResponseTransfer removeCountryByUuid(final UUID countryUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=countryRepository.removeCountryByUuid(countryUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00021")
                    .message("error country don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00013")
                    .message("accept country successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAllCountryDTOS(String searchKey, final int page, final int size, List<String>sortBy,
                                              List<SortType>sortTypes){

        final ResponseTransfer responseTransfer;

        if(sortBy!=null && !isCountryColumnsExists(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00022")
                    .message("error column not found in country entity")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        final Pageable pageable=filterBuilder.buildFilter(page, size, sortBy, sortTypes);

        if(pageable==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00010")
                    .message("error with sort type")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();

            return responseTransfer;
        }
        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Country>countries=countryRepository.getCountriesBySearchKey(searchKey, pageable);
        List<CountryDTO>countryDTOS=new LinkedList<>();

        if(countries==null){
            countries=new LinkedList<>();
        }
        for(Country country:countries){
            countryDTOS.add(toCountryDTO(country));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00014")
                .message("accept all founded countries successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(countryDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getCountryDTOByUuid(final UUID countryUuid){

        final ResponseTransfer responseTransfer;
        final Country country=countryRepository.getCountryByUuid(countryUuid);

        if(country==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00023")
                    .message("error country not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00015")
                    .message("accept country successful returned with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toCountryDTO(country))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public int getAmountCountriesBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }

        return countryRepository.getAmountCountriesBySearchKey(searchKey);
    }

    private CountryDTO toCountryDTO(final Country country){

        if(country==null){

            return null;
        }
        final CountryDTO countryDTO=CountryDTO.builder()
                .uuid(country.getUuid())
                .name(country.getName())
                .amountCompany(country.getAmountCompany())
                .build();

        return countryDTO;
    }

    private boolean isCountryColumnsExists(List<String>columnNames){

        final List<String>countryColumns=countryRepository.getCountryColumns();

        if(columnNames==null || countryColumns==null){

            return false;
        }
        int i=-1;

        for(String columnName:columnNames){
            i++;
            switch (columnName){
                case "amountCompany":
                    columnNames.set(i, "amount_company");
                    break;
            }
        }

        return countryColumns.containsAll(columnNames);
    }

    @Override
    public ResponseTransfer getColumnNameCountryById(final int id){

        final ResponseTransfer responseTransfer;
        final List<String>countryColumns=countryRepository.getCountryColumns();

        if(countryColumns==null || id<0 || id>=countryColumns.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00024")
                    .message("error not found column in country entity with this id")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else {
            responseTransfer = ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00016")
                    .message("accept column name in country entity successful returned by id")
                    .data(countryColumns.get(id))
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer addRegion(final Region region, final UUID countryUuid){

        final ResponseTransfer responseTransfer;
        if(!isCountryExistsByUuid(countryUuid)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00023")
                    .message("error country not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        final Boolean isAdded=regionRepository.addRegion(region.getName(), countryUuid);

        if(isAdded==null || !isAdded){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00025")
                    .message("error region don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00017")
                    .message("accept region successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editRegion(final Region region, final UUID countryUuid){

        final ResponseTransfer responseTransfer;
        if(!isCountryExistsByUuid(countryUuid)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00023")
                    .message("error country not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        if(region.getUuid()==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00026")
                    .message("error edited region uuid is invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        if(isRegionExistsByName(region.getName(), region.getUuid())){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00027")
                    .message("error edited region name already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final Boolean isEdited=regionRepository.editRegion(region.getUuid(), region.getName(), countryUuid);

        if(isEdited==null || !isEdited){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00028")
                    .message("error region don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00018")
                    .message("accept region successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer removeRegionByUuid(final UUID regionUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=regionRepository.removeRegionByUuid(regionUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00029")
                    .message("error region don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00019")
                    .message("accept region successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isRegionExistsByName(final String regionName, final UUID regionUuid){

        return regionRepository.isRegionExistsByName(regionName.toLowerCase(Locale.ROOT), regionUuid);
    }

    private boolean isRegionExistsByUuid(final UUID regionUuid){

        return regionRepository.isRegionExistsByUuid(regionUuid);
    }

    @Override
    public ResponseTransfer getAllRegionDTOS(String searchKey, final int page, final int size, List<String> sortBy,
                                             List<SortType> sortTypes){

        final ResponseTransfer responseTransfer;
        List<RegionDTO>regionDTOS=new LinkedList<>();

        if(sortBy!=null && !isRegionColumnsExists(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00030")
                    .message("error column not found in region entity")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .data(regionDTOS)
                    .build();

            return responseTransfer;
        }
        final Pageable pageable=filterBuilder.buildFilter(page, size, sortBy, sortTypes);

        if(pageable==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00010")
                    .message("error with sort type")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();

            return responseTransfer;
        }
        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Region>regions=regionRepository.getRegionsBySearchKey(searchKey, pageable);

        if(regions==null){
            regions=new LinkedList<>();
        }
        for(Region region:regions){
            regionDTOS.add(toRegionDTO(region));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00020")
                .message("accept all founded regions successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(regionDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getRegionDTOByUuid(final UUID countryUuid){

        ResponseTransfer responseTransfer;
        Region region=regionRepository.getRegionByUuid(countryUuid);

        if(region==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00031")
                    .message("error region not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00021")
                    .message("accept region successful returned with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toRegionDTO(region))
                    .build();
        }

        return responseTransfer;
    }

    private RegionDTO toRegionDTO(final Region region){

        if(region==null){

            return null;
        }
        RegionDTO regionDTO= RegionDTO.builder()
                .uuid(region.getUuid())
                .name(region.getName())
                .amountCompany(region.getAmountCompany())
                .countryDTO(region.getCountry().toCountryDTOOnlyUuidAndName())
                .build();

        return regionDTO;
    }

    @Override
    public int getAmountRegionsBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }

        return regionRepository.getAmountRegionsBySearchKey(searchKey);
    }

    private boolean isRegionColumnsExists(List<String>columnNames){

        List<String>regionColumns=regionRepository.getRegionColumns();

        if(regionColumns==null || columnNames==null){

            return false;
        }
        regionColumns.add("country.name");
        int i=-1;

        for(String columnName:columnNames){
            i++;
            switch (columnName){
                case "amountCompany":
                    columnNames.set(i, "amount_company");
                    break;
                case "countryName":
                    columnNames.set(i, "country.name");
            }
        }

        return regionColumns.containsAll(columnNames);
    }

    @Override
    public ResponseTransfer getColumnNameRegionById(final int id){

        ResponseTransfer responseTransfer;
        List<String>regionColumns=regionRepository.getRegionColumns();

        if(regionColumns==null || id<0 || id>=regionColumns.size()+1){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00032")
                    .message("error not found column in region entity with this id")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else {
            regionColumns.add(2, "country.name");
            responseTransfer = ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00022")
                    .message("accept column name in region entity successful returned by id")
                    .data(regionColumns.get(id))
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getCountryDTOSByName(String searchKey){

        final ResponseTransfer responseTransfer;
        List<CountryDTO>countryDTOS=new LinkedList<>();

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Country>countries=countryRepository.getCountriesByName(searchKey);

        if(countries==null){
            countries=new LinkedList<>();
        }
        for(Country country:countries){
            countryDTOS.add(toCountryDTOOnlyUuidAndName(country));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00037")
                .message("accept all founded countries successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(countryDTOS)
                .build();

        return responseTransfer;
    }

    private CountryDTO toCountryDTOOnlyUuidAndName(final Country country) {

        if(country==null){

            return null;
        }
        CountryDTO countryDTO=CountryDTO.builder()
                .uuid(country.getUuid())
                .name(country.getName())
                .build();

        return countryDTO;
    }

    @Override
    public ResponseTransfer getRegionDTOSByCountryUuidAndName(final UUID countryUuid, String searchKey){

        final ResponseTransfer responseTransfer;
        List<RegionDTO>regionDTOS=new LinkedList<>();

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Region>regions=regionRepository.getRegionsByCountryUuidAndName(countryUuid, searchKey);

        if(regions==null){
            regions=new LinkedList<>();
        }
        for(Region region:regions){
            regionDTOS.add(toRegionDTOOnlyUuidAndName(region));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00038")
                .message("accept all founded regions successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(regionDTOS)
                .build();

        return responseTransfer;
    }

    private RegionDTO toRegionDTOOnlyUuidAndName(final Region region) {

        if(region==null){

            return null;
        }
        RegionDTO regionDTO=RegionDTO.builder()
                .uuid(region.getUuid())
                .name(region.getName())
                .build();

        return regionDTO;
    }

    @Override
    public ResponseTransfer checkTheCorrectnessCountryAndRegion(final UUID countryUuid, final UUID regionUuid){

        final ResponseTransfer responseTransfer;
        boolean isCorrect=regionRepository.checkTheCorrectnessCountryAndRegion(countryUuid, regionUuid);

        if(!isCorrect && countryUuid!=null && regionUuid!=null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00048")
                    .message("error conflict with country and region")
                    .httpStatus(HttpStatus.FAILED_DEPENDENCY)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .build();
        }

        return responseTransfer;
    }

}
