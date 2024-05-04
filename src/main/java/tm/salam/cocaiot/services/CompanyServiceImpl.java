package tm.salam.cocaiot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.CompanyRepository;
import tm.salam.cocaiot.daoes.FileRepository;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.helpers.*;
import tm.salam.cocaiot.models.Company;
import tm.salam.cocaiot.models.Person;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final FileRepository fileRepository;
    private final FilterBuilder filterBuilder;

    @Value("${default.sort.column}")
    private String defaultSortColumn;
    @Value("${default.sort.type}")
    private String defaultSortType;

    public CompanyServiceImpl(CompanyRepository companyRepository, FileRepository fileRepository,
                              FilterBuilder filterBuilder) {
        this.companyRepository = companyRepository;
        this.fileRepository = fileRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer<UUID> addCompany(final Company company, final UUID organizationUuid, final List<UUID> typeActivityUuids,
                                       final TypeOwnership typeOwnership, final UUID countryUuid, final UUID regionUuid,
                                       final UUID directorUuid, List<UUID>founderUuids, final UUID logoUuid,
                                       final UUID... fileUuids){

        final ResponseTransfer<UUID> responseTransfer;
        final UUID savedCompanyUuid=companyRepository.addCompany(company.getName(), company.getFullAddress(),
                company.getPhoneNumber(), company.getEmail(), company.getFax(), company.getWebSite(),
                company.getTypeWork(), countryUuid, regionUuid, directorUuid, organizationUuid, typeOwnership.name(),
                logoUuid, fileUuids[0], fileUuids[1], fileUuids[2], fileUuids[3], fileUuids[4], fileUuids[5]);
        if(savedCompanyUuid==null){
            responseTransfer= ResponseTransfer.<UUID>builder()
                    .status(false)
                    .code("SR-00056")
                    .message("error company don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        if(typeActivityUuids!=null) {
            for (UUID typeActivityUuid : typeActivityUuids) {
                companyRepository.addTypeActivityToCompany(savedCompanyUuid, typeActivityUuid);
            }
        }
        if(founderUuids!=null) {
            if (founderUuids != null) {
                for (UUID founderUuid : founderUuids) {
                    companyRepository.addFounderToCompany(savedCompanyUuid, founderUuid);
                }
            }
        }
        responseTransfer= ResponseTransfer.<UUID>builder()
                .status(true)
                .code("SS-00055")
                .message("accept company successful added")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(savedCompanyUuid)
                .build();

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editCompany(final Company company, final UUID organizationUuid, final List<UUID> typeActivityUuids,
                                        final TypeOwnership typeOwnership, final UUID countryUuid, final UUID regionUuid,
                                        final UUID directorUuid, List<UUID> founderUuids, final UUID logoUuid,
                                        final UUID... fileUuids){

        final ResponseTransfer responseTransfer;

//        if(company.getUuid()==null){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("")
//                    .message("error company uuid is invalid")
//                    .httpStatus(HttpStatus.BAD_REQUEST)
//                    .build();
//
//            return responseTransfer;
//        }
        final Company editedCompany=companyRepository.getCompanyByUuid(company.getUuid());
        if(editedCompany==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00057")
                    .message("error company not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }

//        if(isCompanyExists(company, typeOwnership)){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("SR-00058")
//                    .message("error company unique parameter already exists")
//                    .httpStatus(HttpStatus.CONFLICT)
//                    .build();
//
//            return responseTransfer;
//        }
        final UUID editedCompanyUuid=companyRepository.editCompany(company.getUuid(), company.getName(),
                company.getFullAddress(), company.getPhoneNumber(), company.getEmail(), company.getFax(),
                company.getWebSite(), company.getTypeWork(), countryUuid, regionUuid, directorUuid, organizationUuid,
                typeOwnership.name(), logoUuid, fileUuids[0], fileUuids[1], fileUuids[2], fileUuids[3], fileUuids[4],
                fileUuids[5]);
        if(editedCompanyUuid==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00059")
                    .message("error company don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();

            return responseTransfer;
        }
        if(typeActivityUuids!=null) {
            companyRepository.removeActivityCompanyByUuid(company.getUuid());
            for (UUID typeActivityUuid : typeActivityUuids) {
                companyRepository.addTypeActivityToCompany(editedCompanyUuid, typeActivityUuid);
            }
        }
        if(founderUuids!=null){
            companyRepository.removeFoundersByCompanyUuid(editedCompanyUuid);
            for(UUID founderUuid:founderUuids){
                companyRepository.addFounderToCompany(editedCompanyUuid, founderUuid);
            }
        }

        changeStatusConfirmFilesByCompany(editedCompany, false);
        List<UUID>companyFileUuids = new LinkedList<>();
        for (UUID fileUuid : fileUuids) {
            if (fileUuid != null) {
                companyFileUuids.add(fileUuid);
            }
        }
        fileRepository.changeStatusConfirmFileByUuid(true, companyFileUuids);

        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00056")
                .message("accept company successful edited")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();

        return responseTransfer;
    }

    private boolean isCompanyExists(final Company company, final TypeOwnership typeOwnership) {

        return companyRepository.isCompanyExists(company.getUuid(), company.getName().toLowerCase(Locale.ROOT),
                company.getEmail(), company.getFax(), company.getWebSite(), typeOwnership.name());
    }

    private void changeStatusConfirmFilesByCompany(final Company company, final boolean valueStatusConfirm){

        List<UUID> companyFileUuids = new LinkedList<>();
        if (company.getMembershipApplication() != null) {
            companyFileUuids.add(company.getMembershipApplication().getUuid());
        }
        if (company.getExtractFromUsreo() != null) {
            companyFileUuids.add(company.getExtractFromUsreo().getUuid());
        }
        if (company.getCharterOfTheEnterprise() != null) {
            companyFileUuids.add(company.getCharterOfTheEnterprise().getUuid());
        }
        if (company.getCertificateOfForeignEconomicRelations() != null) {
            companyFileUuids.add(company.getCertificateOfForeignEconomicRelations().getUuid());
        }
        if (company.getCertificateOfStateRegistration() != null) {
            companyFileUuids.add(company.getCertificateOfStateRegistration().getUuid());
        }
        if (company.getPaymentOfTheEntranceMembershipFee() != null) {
            companyFileUuids.add(company.getPaymentOfTheEntranceMembershipFee().getUuid());
        }
        fileRepository.changeStatusConfirmFileByUuid(valueStatusConfirm, companyFileUuids);

        return;
    }

    @Override
    @Transactional
    public ResponseTransfer removeCompanyByUuid(final UUID companyUuid){

        final ResponseTransfer responseTransfer;
        final Company company=companyRepository.getCompanyByUuid(companyUuid);
        final Boolean isRemoved=companyRepository.removeCompanyByUuid(companyUuid);

        if(isRemoved==null || company==null || !isRemoved){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00060")
                    .message("error company don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            changeStatusConfirmFilesByCompany(company, false);
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00057")
                    .message("accept company successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAllCompanyDTOS(String searchKey, final int page, final int size, List<String> sortBy,
                                              List<SortType> sortTypes, TypeOwnership typeOwnership) {

        List<CompanyDTO> companyDTOS = new LinkedList<>();
        final ResponseTransfer responseTransfer;
//        sortBy = parseCompanyColumns(null, sortBy);
//        if (sortBy != null && !isCompanyColumnsExists(null, sortBy)) {
//            responseTransfer = ResponseTransfer.builder()
//                    .status(false)
//                    .code("SR-00061")
//                    .message("error column not found in entity company")
//                    .httpStatus(HttpStatus.NOT_FOUND)
//                    .data(companyDTOS)
//                    .build();
//
//            return responseTransfer;
//
//        }
        final Pageable pageable= PageRequest.of(page, size);
//        final Pageable pageable = filterBuilder.buildFilter(page, size, sortBy, sortTypes);
//
//        if (pageable == null) {
//            responseTransfer = ResponseTransfer.builder()
//                    .status(false)
//                    .code("SR-00010")
//                    .message("error with sort type")
//                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
//                    .build();
//
//            return responseTransfer;
//        }
        if (searchKey == null) {
            searchKey = "";
        } else {
            searchKey = searchKey.toLowerCase(Locale.ROOT);
        }
        List<Company> companies = companyRepository.getCompaniesBySearchKey(pageable, searchKey, typeOwnership.name());

        if(companies==null){
            companies=new LinkedList<>();
        }
        if(sortBy==null){
            sortBy=new LinkedList<>();
            sortBy.add(defaultSortColumn);
        }
        if(sortTypes==null){
            sortTypes=new LinkedList<>();
            sortTypes.add(SortType.valueOf(defaultSortType));
        }
        sortCompanies(companies, sortBy, sortTypes);
        for(Company company:companies){
            companyDTOS.add(toCompanyDTOOnlyGeneral(company));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00058")
                .message("accept all founded company successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(companyDTOS)
                .build();

        return responseTransfer;
    }

    private void sortCompanies(List<Company>companies, final List<String>sortBy, List<SortType>sortTypes){
        if(sortBy==null || sortTypes==null){

            return;
        }
        Collections.sort(companies, new Comparator<Company>() {
            @Override
            public int compare(Company company, Company t1) {
                int resultComparisons=0;

                for(int i=0; i<sortBy.size() && i<sortTypes.size() && resultComparisons==0; i++){
                    switch (sortBy.get(i)) {
                        case "name":
                            resultComparisons = company.getName().compareTo(t1.getName());
                            break;
                        case "organizationName":
                            if(company.getOrganization()!=null){
                                resultComparisons=company.getOrganization().getName()
                                        .compareTo(t1.getOrganization().getName());
                            }
                            break;
                        case "countryName":
                            resultComparisons=company.getCountry().getName().compareTo(t1.getCountry().getName());
                            break;
                        case "regionName":
                            resultComparisons=company.getRegion().getName().compareTo(t1.getRegion().getName());
                            break;
                        case "typeOwnership":
                            resultComparisons=company.getTypeOwnership().name().compareTo(t1.getTypeOwnership().name());
                            break;
                        case "typeActivityName":
                            Iterator<TypeActivity>iteratorCompanyTypeActivity=company.getTypeActivities().iterator();
                            Iterator<TypeActivity>iteratorT1TypeActivity=t1.getTypeActivities().iterator();
                            String companyTypeActivity, t1TypeActivity;
                            while(resultComparisons==0 && iteratorCompanyTypeActivity.hasNext() && iteratorT1TypeActivity.hasNext()){
                                companyTypeActivity=iteratorCompanyTypeActivity.next().getName();
                                t1TypeActivity=iteratorT1TypeActivity.next().getName();
                                resultComparisons=companyTypeActivity.compareTo(t1TypeActivity);
                            }
                            if(resultComparisons==0){
                                resultComparisons=company.getTypeActivities().size()-t1.getTypeActivities().size();
                            }
                            break;
                        case "phoneNumber":
                            resultComparisons=company.getPhoneNumber().compareTo(t1.getPhoneNumber());
                            break;
                        case "email":
                            resultComparisons=company.getEmail().compareTo(t1.getEmail());
                            break;
                        case "fullAddress":
                            resultComparisons=company.getFullAddress().compareTo(t1.getFullAddress());
                            break;
                        case "fax":
                            resultComparisons=company.getFax().compareTo(t1.getFax());
                            break;
                        case "webSite":
                            resultComparisons=company.getWebSite().compareTo(t1.getWebSite());
                            break;
                        case "created":
                            resultComparisons=company.getCreated().compareTo(t1.getCreated());
                            break;
                    }
                    if(resultComparisons!=0){
                        switch (sortTypes.get(i)){
                            case ASCENDING:
                                return resultComparisons;
                            case DESCENDING:
                                return -resultComparisons;
                        }
                    }
                }
                return resultComparisons;
            }
        });
    }

    @Override
    public int getAmountCompaniesBySearchKey(String searchKey, TypeOwnership typeOwnership){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        int amountCompany=companyRepository.getAmountCompaniesBySearchKey(searchKey, typeOwnership.name());

        return amountCompany;
    }

    @Override
    public boolean isCompanyColumnsExists(String withStart, final List<String> columns) {

        List<String>companyColumns=companyRepository.getCompanyColumns();

        if(columns==null || companyColumns==null){

            return false;
        }
        if(withStart!=null){
            for(int i=0; i<companyColumns.size(); i++){
                companyColumns.set(i, withStart+companyColumns.get(i));
            }
        }
        companyColumns.add("country.name");
        companyColumns.add("region.name");
        companyColumns.addAll(Arrays.asList("person.surname", "person.name", "person.patronomic_name"));
        companyColumns.add("type_activity.name");
        companyColumns.add("organization.name");

        return companyColumns.containsAll(columns);
    }

    @Override
    public List<String> parseCompanyColumns(String withStart, final List<String> columns) {

        if(columns==null){
            return null;
        }
        if(withStart==null){
            withStart="";
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String column:columns){
            switch (column){
                case "fullAddress":
                    parsedColumns.add(withStart+"full_address");
                    break;
                case "phoneNumber":
                    parsedColumns.add(withStart+"phone_number");
                    break;
                case "webSite":
                    parsedColumns.add(withStart+"web_site");
                    break;
                case "typeWork":
                    parsedColumns.add(withStart+"type_work");
                    break;
                case "typeOwnership":
                    parsedColumns.add(withStart+"type_ownership");
                    break;
                case "countryName":
                    parsedColumns.add("country.name");
                    break;
                case "regionName":
                    parsedColumns.add("region.name");
                    break;
                case "directorFullName":
                    parsedColumns.addAll(Arrays.asList("person.surname", "person.name", "person.patronomic_name"));
                    break;
                case "organizationName":
                    parsedColumns.add("organization.name");
                    break;
                case "typeActivityName":
                    parsedColumns.add("type_activity.name");
                    break;
                default:
                    parsedColumns.add(withStart+column);
                    break;
            }
        }

        return parsedColumns;
    }

    private CompanyDTO toCompanyDTOOnlyGeneral(final Company company){

        if(company==null){

            return null;
        }
        CompanyDTO companyDTO=CompanyDTO.builder()
                .uuid(company.getUuid())
                .name(company.getName())
                .fullAddress(company.getFullAddress())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .fax(company.getFax())
                .webSite(company.getWebSite())
                .typeWork(company.getTypeWork())
                .countryDTO(company.getCountry()!=null ? company.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(company.getRegion()!=null ? company.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .director(company.getDirector()!=null ? company.getDirector().toPersonDTOOnlyFullName() : null)
                .founders(company.getFounders() != null ? company.getFounders().stream().
                        map(Person::toPersonDTOOnlyFullName).collect(Collectors.toList()) : null)
                .organizationDTO(company.getOrganization() != null ?
                        company.getOrganization().toOrganizationDTOOnlyUuidAndName() : null)
                .typeActivityDTOS(company.getTypeActivities()!=null ? company.getTypeActivities().stream()
                        .map(TypeActivity::toTypeActivityDTOOnlyUuidAndName).collect(Collectors.toList()) : null)
                .typeOwnership(company.getTypeOwnership())
                .logo(company.getLogo()!=null ? company.getLogo().toFileDTO() : null)
                .build();

        return companyDTO;
    }

    @Override
    public ResponseTransfer getCompanyDTOByUuid(final UUID companyUuid){

        final ResponseTransfer responseTransfer;
        Company company=companyRepository.getCompanyByUuid(companyUuid);

        if(company==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00057")
                    .message("error company not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00054")
                    .message("accept company founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toCompanyDTO(company))
                    .build();
        }

        return responseTransfer;
    }

    private CompanyDTO toCompanyDTO(final Company company) {

        if(company==null){

            return null;
        }
        CompanyDTO companyDTO=CompanyDTO.builder()
                .uuid(company.getUuid())
                .name(company.getName())
                .fullAddress(company.getFullAddress())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .fax(company.getFax())
                .webSite(company.getWebSite())
                .typeWork(company.getTypeWork())
                .countryDTO(company.getCountry()!=null ? company.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(company.getRegion()!=null ? company.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .director(company.getDirector()!=null ? company.getDirector().toPersonDTOOnlyFullName() : null)
                .founders(company.getFounders() != null ? company.getFounders().stream().
                        map(Person::toPersonDTOOnlyFullName).collect(Collectors.toList()) : null)
                .organizationDTO(company.getOrganization() != null ?
                        company.getOrganization().toOrganizationDTOOnlyUuidAndName() : null)
                .typeActivityDTOS(company.getTypeActivities()!=null ? company.getTypeActivities().stream()
                        .map(TypeActivity::toTypeActivityDTOOnlyUuidAndName).collect(Collectors.toList()) : null)
                .logo(company.getLogo()!=null ? company.getLogo().toFileDTO() : null)
                .membershipApplication(company.getMembershipApplication() != null ?
                        company.getMembershipApplication().toFileDTO() : null)
                .extractFromUsreo(company.getExtractFromUsreo() != null ?
                        company.getExtractFromUsreo().toFileDTO() : null)
                .charterOfTheEnterprise(company.getCharterOfTheEnterprise() != null ?
                        company.getCharterOfTheEnterprise().toFileDTO() : null)
                .certificateOfForeignEconomicRelations(company.getCertificateOfForeignEconomicRelations() != null ?
                        company.getCertificateOfForeignEconomicRelations().toFileDTO() : null)
                .certificateOfStateRegistration(company.getCertificateOfStateRegistration() != null ?
                        company.getCertificateOfStateRegistration().toFileDTO() : null)
                .paymentOfTheEntranceMembershipFee(company.getPaymentOfTheEntranceMembershipFee() != null ?
                        company.getPaymentOfTheEntranceMembershipFee().toFileDTO() : null)
                .build();

        return companyDTO;
    }

    @Override
    public List<CompanyDTO>getIsNotCocaiotMemberCompanyDTOSByName(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Company>companies=companyRepository.getIsNotCocaiotMemberCompaniesByName(searchKey);
        List<CompanyDTO>companyDTOS=new LinkedList<>();

        if(companies==null){
            companies=new LinkedList<>();
        }
        for(Company company:companies){
            companyDTOS.add(company.toCompanyDTOOnlyUuidAndName());
        }

        return companyDTOS;
    }

    @Override
    public ResponseTransfer getCompanyDTOOnlyGeneralByUuid(final UUID companyUuid){

        final ResponseTransfer responseTransfer;
        final Company company=companyRepository.getCompanyByUuid(companyUuid);

        if(company==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00057")
                    .message("error company not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00054")
                    .message("accept company founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toCompanyDTOOnlyGeneral(company))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer isCompanyExistsByUuid(final UUID companyUuid){

        final ResponseTransfer responseTransfer;
        final boolean isCompanyExists=companyRepository.isCompanyExistsByUuid(companyUuid);

        if (isCompanyExists){
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00057")
                    .message("error company not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getCocaiotMemberCompanyDTOSByName(String searchKey){

        final ResponseTransfer responseTransfer;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Company>companies=companyRepository.getCocaiotMemberCompaniesByName(searchKey);
        List<CompanyDTO>companyDTOS=new LinkedList<>();

        if(companies==null){
            companies=new LinkedList<>();
        }
        for(Company company:companies){
            companyDTOS.add(company.toCompanyDTOOnlyUuidAndName());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(companyDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public List<CompanyDTO> getCompanyDTOSByName(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Company>companies=companyRepository.getCompaniesByName(searchKey);
        List<CompanyDTO>companyDTOS=new LinkedList<>();

        if(companies==null){
            companies=new LinkedList<>();
        }
        for(Company company:companies){
            companyDTOS.add(company.toCompanyDTOOnlyUuidAndName());
        }

        return companyDTOS;
    }

    @Override
    public List<CompanyDTO> getCompanyDTOSByTypeActivityUuids(List<UUID> typeActivityUuids){

        List<Company>companies=companyRepository.getCompaniesByTypeActivityUuids(typeActivityUuids);
        List<CompanyDTO>companyDTOS=new LinkedList<>();

        if(companies==null){
            companies=new LinkedList<>();
        }
        for(Company company:companies){
            companyDTOS.add(company.toCompanyDTOOnlyUuidAndName());
        }

        return companyDTOS;
    }

    @Override
    public List<String>getCompanyEmailsByUuids(List<UUID>companyUuids){

        if(companyUuids==null){
            companyUuids=new LinkedList<>();
        }
        List<String>companyEmails=companyRepository.getCompanyEmailsByUuids(companyUuids);

        if(companyEmails==null){
            companyEmails=new LinkedList<>();
        }

        return companyEmails;
    }

    @Override
    public ResponseTransfer isCompaniesExistsByUuids(final List<UUID> companyUuids){

        final ResponseTransfer responseTransfer;
        int amountCompany=companyRepository.getAmountCompanyByUuids(companyUuids);

        if(amountCompany==companyUuids.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00071")
                    .message("error companies not found with this uuids")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer<?> getCompanyNamesByTypeActivityUuid(final UUID typeActivityUuid){

        ResponseTransfer<?>responseTransfer;
        List<Company>companies=companyRepository.getCompanyNamesByTypeActivityUuid(typeActivityUuid);

        if(companies==null){
            companies=new LinkedList<>();
        }
        List<CompanyDTO>companyDTOS=new LinkedList<>();

        for(Company company:companies){
            companyDTOS.add(company.toCompanyDTOOnlyGeneral());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .data(companyDTOS)
                .build();

        return responseTransfer;
    }

}
