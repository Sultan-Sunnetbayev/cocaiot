package tm.salam.cocaiot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.EntrepreneurRepository;
import tm.salam.cocaiot.daoes.FileRepository;
import tm.salam.cocaiot.daoes.PersonRepository;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Entrepreneur;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntrepreneurServiceImpl implements EntrepreneurService {

    private final EntrepreneurRepository entrepreneurRepository;
    private final PersonRepository personRepository;
    private final FileRepository fileRepository;
    private final FilterBuilder filterBuilder;

    @Value("${default.sort.column}")
    private String defaultSortColumn;
    @Value("${default.sort.type}")
    private String defaultSortType;

    public EntrepreneurServiceImpl(EntrepreneurRepository entrepreneurRepository, PersonRepository personRepository,
                                   FileRepository fileRepository, FilterBuilder filterBuilder) {
        this.entrepreneurRepository = entrepreneurRepository;
        this.personRepository = personRepository;
        this.fileRepository = fileRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer<UUID> addEntrepreneur(final UUID personUuid,
                                            final String webSite,
                                            final String typeWork,
                                            final List<UUID>typeActivityUuids,
                                            final UUID logoUuid,
                                            final UUID membershipApplicationUuid,
                                            final UUID patentCertifyingPaymentUuid,
                                            final UUID entrepreneurStatisticalCodes,
                                            final UUID certificateOfForeignEconomicRelationsUuid,
                                            final UUID registrationCertificateOfEntrepreneur,
                                            final UUID certificateOfTaxRegistration){

        final ResponseTransfer<UUID> responseTransfer;
        final UUID savedEntrepreneurUuid=entrepreneurRepository.addEntrepreneur(personUuid, webSite, typeWork, logoUuid,
                membershipApplicationUuid, patentCertifyingPaymentUuid, entrepreneurStatisticalCodes,
                certificateOfForeignEconomicRelationsUuid, registrationCertificateOfEntrepreneur,
                certificateOfTaxRegistration);

        if(savedEntrepreneurUuid==null){
            responseTransfer=ResponseTransfer.<UUID>builder()
                    .status(false)
                    .code("SR-00049")
                    .message("error this person already added to entrepreneur")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            for(UUID typeActivityUuid:typeActivityUuids){
                entrepreneurRepository.addTypeActivityToEntrepreneur(savedEntrepreneurUuid, typeActivityUuid);
            }
            responseTransfer=ResponseTransfer.<UUID>builder()
                    .status(true)
                    .code("SS-00039")
                    .message("accept person added to entrepreneur")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(savedEntrepreneurUuid)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer<?> editEntrepreneur(final UUID entrepreneurUuid, final UUID personUuid, final String webSite,
                                             final String typeWork, final List<UUID>typeActivityUuids,
                                             final UUID logoUuid, final UUID... fileUuids){

        final ResponseTransfer<?> responseTransfer;

        if(isPersonEntrepreneur(entrepreneurUuid, personUuid)){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00049")
                    .message("error this person already added to entrepreneur")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final Entrepreneur entrepreneur=entrepreneurRepository.getEntrepreneurByUuid(entrepreneurUuid);
        if(entrepreneur==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00050")
                    .message("error entrepreneur not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        entrepreneurRepository.editEntrepreneur(entrepreneurUuid, personUuid, webSite, typeWork, logoUuid, fileUuids[0],
                fileUuids[1], fileUuids[2], fileUuids[3], fileUuids[4], fileUuids[5]);
        if (!Objects.equals(entrepreneur.getPerson().getUuid(), personUuid)) {
            personRepository.changeValueIsEntrepreneurPersonByUuid(entrepreneur.getPerson().getUuid(), false);
            personRepository.changeValueIsEntrepreneurPersonByUuid(personUuid, true);
        }
        entrepreneurRepository.removeTypeActivitiesByEntrepreneurUuid(entrepreneurUuid);
        for(UUID typeActivityUuid:typeActivityUuids) {
            entrepreneurRepository.addTypeActivityToEntrepreneur(entrepreneurUuid, typeActivityUuid);
        }
        List<UUID>savedFileUuids=new LinkedList<>();

        changeStatusConfirmFilesByEntrepreneur(entrepreneur, false);
        for(UUID fileUuid:fileUuids){
            if(fileUuid!=null){
                savedFileUuids.add(fileUuid);
            }
        }
        fileRepository.changeStatusConfirmFileByUuid(true, savedFileUuids);
        responseTransfer = ResponseTransfer.builder()
                .status(true)
                .code("SS-00040")
                .message("accept entrepreneur successful edited")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();

        return responseTransfer;
    }

    private boolean isPersonEntrepreneur(final UUID entrepreneurUuid, final UUID personUuid) {

        return entrepreneurRepository.isPersonEntrepreneur(entrepreneurUuid, personUuid);
    }

    @Override
    public int getAmountEntrepreneursBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        int amountEntreprenurs=entrepreneurRepository.getAmountEntrepreneursBySearchKey(searchKey);

        return amountEntreprenurs;
    }

    @Override
    public ResponseTransfer<List<EntrepreneurDTO>> getAllEntrepreneurDTOS(String searchKey, final int page, final int size, List<String> sortBy,
                                                   List<SortType> sortTypes) {

        final ResponseTransfer<List<EntrepreneurDTO>> responseTransfer;

//        sortBy=parseEntrepreneurPersonColumns(sortBy);
//        if(sortBy!=null && !isEntrepreneurPersonColumnsExists(sortBy)){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("SR-00046")
//                    .message("error column not found in entity person")
//                    .httpStatus(HttpStatus.NOT_FOUND)
//                    .build();
//
//            return responseTransfer;
//
//        }
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
        final Pageable pageable= PageRequest.of(page, size);
        if (searchKey == null) {
            searchKey = "";
        } else {
            searchKey = searchKey.toLowerCase(Locale.ROOT);
        }
        List<Entrepreneur> entrepreneurs = entrepreneurRepository.getEntrepreneursBySearchKey(searchKey, pageable);
        List<EntrepreneurDTO> entrepreneurDTOS = new LinkedList<>();

        if (entrepreneurs == null) {
            entrepreneurs = new LinkedList<>();
        }
        if(sortBy==null){
            sortBy=new LinkedList<>();
            sortBy.add(defaultSortColumn);
        }
        if(sortTypes==null){
            sortTypes=new LinkedList<>();
            sortTypes.add(SortType.valueOf(defaultSortType));
        }
        sortEntrepreneurs(entrepreneurs, sortBy, sortTypes);
        for (Entrepreneur entrepreneur : entrepreneurs) {
            entrepreneurDTOS.add(toEntrepreneurDTOOnlyGeneral(entrepreneur));
        }
        responseTransfer = ResponseTransfer.<List<EntrepreneurDTO>>builder()
                .status(true)
                .code("SS-00041")
                .message("accept founded all entrepreneur persons successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(entrepreneurDTOS)
                .build();

        return responseTransfer;
    }

    private EntrepreneurDTO toEntrepreneurDTOOnlyGeneral(final Entrepreneur entrepreneur) {

        if(entrepreneur==null){

            return null;
        }
        EntrepreneurDTO entrepreneurDTO=EntrepreneurDTO.builder()
                .uuid(entrepreneur.getUuid())
                .personDTO(entrepreneur.getPerson().toPersonDTO())
                .webSite(entrepreneur.getWebSite())
                .typeWork(entrepreneur.getTypeWork())
                .typeActivityDTOS(entrepreneur.getTypeActivities().stream().map(TypeActivity::toTypeActivityDTOOnlyUuidAndName)
                        .collect(Collectors.toList()))
                .logo(entrepreneur.getLogo()!=null ? entrepreneur.getLogo().toFileDTO() : null)
                .build();

        return entrepreneurDTO;
    }

    private boolean isEntrepreneurPersonColumnsExists(List<String> columnNames) {

        List<String>personColumns=personRepository.getPersonColumns();

        if(personColumns==null || columnNames==null){

            return false;
        }
        for(int i=0; i<personColumns.size(); i++){
            personColumns.set(i, "person."+personColumns.get(i));
        }
        personColumns.add("country.name");
        personColumns.add("region.name");

        return personColumns.containsAll(columnNames);
    }

    private List<String> parseEntrepreneurPersonColumns(final List<String> columnNames){

        if(columnNames==null){

            return null;
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String columnName:columnNames) {
            switch (columnName){
                case "fullName":
                    parsedColumns.addAll(Arrays.asList("person.surname", "person.name", "person.patronomic_name"));
                    break;
                case "birthPlace":
                    parsedColumns.add("person.birth_place");
                    break;
                case "birthDate":
                    parsedColumns.add("person.birth_date");
                    break;
                case "fullAddressOfResidence":
                    parsedColumns.add("person.full_address_of_residence");
                    break;
                case "phoneNumber":
                    parsedColumns.add("person.phone_number");
                    break;
                case "knowledgeOfLanguages":
                    parsedColumns.add("person.knowledge_of_languages");
                    break;
                case "countryName":
                    parsedColumns.add("country.name");
                    break;
                case "regionName":
                    parsedColumns.add("region.name");
                    break;
                default:
                    parsedColumns.add("person." + columnName);
            }
        }

        return parsedColumns;
    }

    @Override
    public ResponseTransfer<EntrepreneurDTO> getEntrepreneurDTOByUuid(final UUID entrepreneurUuid){

        final ResponseTransfer<EntrepreneurDTO> responseTransfer;
        final Entrepreneur entrepreneur=entrepreneurRepository.getEntrepreneurByUuid(entrepreneurUuid);

        if(entrepreneur==null){
            responseTransfer=ResponseTransfer.<EntrepreneurDTO>builder()
                    .status(false)
                    .code("SR-00051")
                    .message("error entrepreneur not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        responseTransfer=ResponseTransfer.<EntrepreneurDTO>builder()
                .status(true)
                .code("SS-00042")
                .message("accept entrepreneur successful founded with this uuid")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(toEntrepreneurDTO(entrepreneur))
                .build();

        return responseTransfer;
    }

    private void sortEntrepreneurs(List<Entrepreneur>entrepreneurs, final List<String>sortBy, List<SortType>sortTypes){
        if(sortBy==null || sortTypes==null){

            return;
        }
        Collections.sort(entrepreneurs, new Comparator<Entrepreneur>() {
            @Override
            public int compare(Entrepreneur entrepreneur, Entrepreneur t1) {
                int resultComparisons=0;

                for(int i=0; i<sortBy.size() && i<sortTypes.size() && resultComparisons==0; i++){
                    switch (sortBy.get(i)) {
                        case "fullName":
                            String entrepreneurFullName=entrepreneur.getPerson().getFullName();
                            String t1FullName=t1.getPerson().getFullName();
                            resultComparisons = entrepreneurFullName.compareTo(t1FullName);
                            break;
                        case "typeActivityName":
                            Iterator<TypeActivity>iteratorEntreprenurTypeActivity=entrepreneur.getTypeActivities().iterator();
                            Iterator<TypeActivity>iteratorT1TypeActivity=t1.getTypeActivities().iterator();
                            String entrepreneurTypeActivity, t1TypeActivity;
                            while(resultComparisons==0 && iteratorEntreprenurTypeActivity.hasNext() && iteratorT1TypeActivity.hasNext()){
                                entrepreneurTypeActivity=iteratorEntreprenurTypeActivity.next().getName();
                                t1TypeActivity=iteratorT1TypeActivity.next().getName();
                                resultComparisons=entrepreneurTypeActivity.compareTo(t1TypeActivity);
                            }
                            if(resultComparisons==0){
                                resultComparisons=entrepreneur.getTypeActivities().size()-t1.getTypeActivities().size();
                            }
                            break;
                        case "fullAddressOfResidence":
                            resultComparisons=entrepreneur.getPerson().getFullAddressOfResidence()
                                    .compareTo(t1.getPerson().getFullAddressOfResidence());
                            break;
                        case "phoneNumber":
                            resultComparisons=entrepreneur.getPerson().getPhoneNumber()
                                    .compareTo(t1.getPerson().getPhoneNumber());
                            break;
                        case "email":
                            resultComparisons=entrepreneur.getPerson().getEmail()
                                    .compareTo(t1.getPerson().getEmail());
                            break;
                        case "knowledgeOfLanguages":
                            resultComparisons=entrepreneur.getPerson().getKnowledgeOfLanguages()
                                    .compareTo(t1.getPerson().getKnowledgeOfLanguages());
                            break;
                        case "birthPlace":
                            resultComparisons=entrepreneur.getPerson().getBirthPlace()
                                    .compareTo(t1.getPerson().getBirthPlace());
                            break;
                        case "birthDate":
                            resultComparisons=entrepreneur.getPerson().getBirthDate()
                                    .compareTo(t1.getPerson().getBirthDate());
                            break;
                        case "countryName":
                            resultComparisons=entrepreneur.getPerson().getCountry().getName()
                                    .compareTo(t1.getPerson().getCountry().getName());
                            break;
                        case "regionName":
                            resultComparisons=entrepreneur.getPerson().getRegion().getName()
                                    .compareTo(t1.getPerson().getRegion().getName());
                            break;
                        case "fax":
                            resultComparisons=entrepreneur.getPerson().getFax().compareTo(t1.getPerson().getFax());
                            break;
                        case "education":
                            resultComparisons=entrepreneur.getPerson().getEducation()
                                    .compareTo(t1.getPerson().getEducation());
                            break;
                        case "experience":
                            resultComparisons=entrepreneur.getPerson().getExperience()
                                    .compareTo(t1.getPerson().getExperience());
                            break;
                        case "created":
                            resultComparisons=entrepreneur.getCreated().compareTo(t1.getCreated());
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

    private EntrepreneurDTO toEntrepreneurDTO(final Entrepreneur entrepreneur) {

        if(entrepreneur==null){

            return null;
        }
        EntrepreneurDTO entrepreneurDTO=EntrepreneurDTO.builder()
                .uuid(entrepreneur.getUuid())
                .personDTO(entrepreneur.getPerson().toPersonDTO())
                .webSite(entrepreneur.getWebSite())
                .typeWork(entrepreneur.getTypeWork())
                .typeActivityDTOS(entrepreneur.getTypeActivities().stream().map(TypeActivity::toTypeActivityDTOOnlyUuidAndName)
                        .collect(Collectors.toList()))
                .logo(entrepreneur.getLogo()!=null ? entrepreneur.getLogo().toFileDTO() : null)
                .membershipApplication(entrepreneur.getMembershipApplication() != null ?
                        entrepreneur.getMembershipApplication().toFileDTO() : null)
                .patentCertifyingPayment(entrepreneur.getPatentCertifyingPayment() != null ?
                        entrepreneur.getPatentCertifyingPayment().toFileDTO() : null)
                .entrepreneurStatisticalCodes(entrepreneur.getEntrepreneurStatisticalCodes() != null ?
                        entrepreneur.getEntrepreneurStatisticalCodes().toFileDTO() : null)
                .certificateOfForeignEconomicRelations(entrepreneur.getCertificateOfForeignEconomicRelations() != null ?
                        entrepreneur.getCertificateOfForeignEconomicRelations().toFileDTO() : null)
                .registrationCertificateOfEntrepreneur(entrepreneur.getRegistrationCertificateOfEntrepreneur() != null ?
                        entrepreneur.getRegistrationCertificateOfEntrepreneur().toFileDTO() : null)
                .certificateOfTaxRegistration(entrepreneur.getCertificateOfTaxRegistration() != null ?
                        entrepreneur.getCertificateOfTaxRegistration().toFileDTO() : null)
                .build();

        return entrepreneurDTO;
    }

    @Override
    @Transactional
    public ResponseTransfer removeEntrepreneurByUuid(final UUID entrepreneurUuid){

        final ResponseTransfer responseTransfer;
        final Entrepreneur entrepreneur=entrepreneurRepository.getEntrepreneurByUuid(entrepreneurUuid);
        final Boolean isRemoved=entrepreneurRepository.removeEntrepreneurByUuid(entrepreneurUuid);

        if(isRemoved==null || entrepreneur==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00052")
                    .message("error entrepreneur don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            personRepository.changeValueIsEntrepreneurPersonByUuid(entrepreneur.getPerson().getUuid(), false);
            changeStatusConfirmFilesByEntrepreneur(entrepreneur, false);
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00053")
                    .message("accept entrepreneur successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private void changeStatusConfirmFilesByEntrepreneur(final Entrepreneur entrepreneur, final boolean valueStatusConfirm){

        List<UUID> entrepreneurFileUuids = new LinkedList<>();
        if (entrepreneur.getMembershipApplication() != null) {
            entrepreneurFileUuids.add(entrepreneur.getMembershipApplication().getUuid());
        }
        if (entrepreneur.getPatentCertifyingPayment() != null) {
            entrepreneurFileUuids.add(entrepreneur.getPatentCertifyingPayment().getUuid());
        }
        if (entrepreneur.getEntrepreneurStatisticalCodes() != null) {
            entrepreneurFileUuids.add(entrepreneur.getEntrepreneurStatisticalCodes().getUuid());
        }
        if (entrepreneur.getCertificateOfForeignEconomicRelations() != null) {
            entrepreneurFileUuids.add(entrepreneur.getCertificateOfForeignEconomicRelations().getUuid());
        }
        if (entrepreneur.getRegistrationCertificateOfEntrepreneur() != null) {
            entrepreneurFileUuids.add(entrepreneur.getRegistrationCertificateOfEntrepreneur().getUuid());
        }
        if (entrepreneur.getCertificateOfTaxRegistration() != null) {
            entrepreneurFileUuids.add(entrepreneur.getCertificateOfTaxRegistration().getUuid());
        }
        fileRepository.changeStatusConfirmFileByUuid(valueStatusConfirm, entrepreneurFileUuids);

        return;
    }

    @Override
    public List<EntrepreneurDTO>getIsNotCocaiotMemberEntrepreneurDTOSByFullName(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Entrepreneur>entrepreneurs=entrepreneurRepository.getIsNotCocaiotMemberEntrepreneursByFullName(searchKey);
        List<EntrepreneurDTO>entrepreneurDTOS=new LinkedList<>();

        if(entrepreneurs==null){
            entrepreneurs=new LinkedList<>();
        }
        for(Entrepreneur entrepreneur:entrepreneurs){
            entrepreneurDTOS.add(EntrepreneurDTO.builder()
                    .uuid(entrepreneur.getUuid())
                    .personDTO(entrepreneur.getPerson().toPersonDTOOnlyFullName())
                    .logo(entrepreneur.getLogo()!=null ? entrepreneur.getLogo().toFileDTO() : null)
                    .build());
        }

        return entrepreneurDTOS;
    }

    @Override
    public ResponseTransfer getEntrepreneurDTOOnlyGeneralByUuid(final UUID entrepreneurUuid){

        final ResponseTransfer responseTransfer;
        final Entrepreneur entrepreneur=entrepreneurRepository.getEntrepreneurByUuid(entrepreneurUuid);

        if(entrepreneur==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00051")
                    .message("error entrepreneur not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else {
            responseTransfer = ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00042")
                    .message("accept entrepreneur successful founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toEntrepreneurDTOOnlyGeneral(entrepreneur))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer isEntrepreneurExistsByUuid(final UUID entrepreneurUuid){

        final ResponseTransfer responseTransfer;
        final boolean isEntrepreneurExists=entrepreneurRepository.isEntrepreneurExistsByUuid(entrepreneurUuid);

        if(isEntrepreneurExists){
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00051")
                    .message("error entrepreneur not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public List<EntrepreneurDTO>getEntrepreneurDTOSByFullName(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Entrepreneur>entrepreneurs=entrepreneurRepository.getEntrepreneursByFullName(searchKey);
        List<EntrepreneurDTO>entrepreneurDTOS=new LinkedList<>();

        if(entrepreneurs==null){
            entrepreneurs=new LinkedList<>();
        }
        for(Entrepreneur entrepreneur:entrepreneurs){
            entrepreneurDTOS.add(EntrepreneurDTO.builder()
                    .uuid(entrepreneur.getUuid())
                    .personDTO(entrepreneur.getPerson().toPersonDTOOnlyFullName())
                    .logo(entrepreneur.getLogo()!=null ? entrepreneur.getLogo().toFileDTO() : null)
                    .typeWork(entrepreneur.getTypeWork())
                    .build());
        }

        return entrepreneurDTOS;
    }

    @Override
    public List<EntrepreneurDTO>getEntrepreneurDTOSByTypeActivityUuids(final List<UUID> typeActivityUuids){

        List<Entrepreneur>entrepreneurs=entrepreneurRepository.getEntrepreneursByTypeActivityUuids(typeActivityUuids);
        List<EntrepreneurDTO>entrepreneurDTOS=new LinkedList<>();

        if(entrepreneurs==null){
            entrepreneurs=new LinkedList<>();
        }
        for(Entrepreneur entrepreneur:entrepreneurs){
            entrepreneurDTOS.add(EntrepreneurDTO.builder()
                    .uuid(entrepreneur.getUuid())
                    .personDTO(entrepreneur.getPerson().toPersonDTOOnlyFullName())
                    .logo(entrepreneur.getLogo()!=null ? entrepreneur.getLogo().toFileDTO() : null)
                    .typeWork(entrepreneur.getTypeWork())
                    .build());
        }

        return entrepreneurDTOS;
    }

    @Override
    public List<String>getEntrepreneurEmailsByUuids(List<UUID> entrepreneurUuids){

        if(entrepreneurUuids==null){
            entrepreneurUuids=new LinkedList<>();
        }
        List<String>emails=entrepreneurRepository.getEntrepreneurEmailsByUuids(entrepreneurUuids);

        if(emails==null){
            emails=new LinkedList<>();
        }

        return emails;
    }

    @Override
    public ResponseTransfer isEntrepreneursExistsByUuids(final List<UUID>entrepreneurUuids){

        final ResponseTransfer responseTransfer;
        final int amountEntrepreneurs=entrepreneurRepository.getAmountEntrepreneursByUuids(entrepreneurUuids);

        if(amountEntrepreneurs!=entrepreneurUuids.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error any entrepreneur not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer<?>getEntrepreneurNamesByTypeActivityUuid(final UUID typeActivityUuid){

        ResponseTransfer<?>responseTransfer;
        List<Entrepreneur>entrepreneurs=entrepreneurRepository.getEntrepreneurNamesByTypeActivityUuid(typeActivityUuid);

        if(entrepreneurs==null){
            entrepreneurs=new LinkedList<>();
        }
        List<EntrepreneurDTO>entrepreneurDTOS=new LinkedList<>();

        for(Entrepreneur entrepreneur:entrepreneurs){
            entrepreneurDTOS.add(entrepreneur.toEntrepreneurDTOOnlyGeneral());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .data(entrepreneurDTOS)
                .build();

        return responseTransfer;
    }

}
