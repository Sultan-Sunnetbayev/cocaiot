package tm.salam.cocaiot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.CocaiotMemberRepository;
import tm.salam.cocaiot.dtoes.CocaiotMemberDTO;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;
import tm.salam.cocaiot.helpers.*;
import tm.salam.cocaiot.models.CocaiotMember;
import tm.salam.cocaiot.models.TypeActivity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CocaiotMemberServiceImpl implements CocaiotMemberService{

    private final CocaiotMemberRepository cocaiotMemberRepository;
//    private final FilterBuilder filterBuilder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${default.sort.column}")
    private String defaultSortColumn;
    @Value("${default.sort.type}")
    private String defaultSortType;

    public CocaiotMemberServiceImpl(CocaiotMemberRepository cocaiotMemberRepository, JdbcTemplate jdbcTemplate) {
        this.cocaiotMemberRepository = cocaiotMemberRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public ResponseTransfer addMember(final UUID memberUuid, final MemberType memberType,
                                      final UUID fileUuid, final Date initialDateLastPayment,
                                      final Date finalDateLastPayment){

        final ResponseTransfer responseTransfer;
        Boolean isAdded=null;
        final StatusPayment statusPayment=getStatusPaymentByDate(finalDateLastPayment);
        switch (memberType) {
            case IS_COMPANY:
                 isAdded = cocaiotMemberRepository.addMember(memberUuid, null, fileUuid, statusPayment.name(),
                         initialDateLastPayment, finalDateLastPayment);
                break;
            case IS_ENTREPRENEUR:
                isAdded=cocaiotMemberRepository.addMember(null, memberUuid, fileUuid, statusPayment.name(),
                        initialDateLastPayment, finalDateLastPayment);
                break;
        }
        if(isAdded==null || !isAdded){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00066")
                    .message("error member don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00065")
                    .message("accept member successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private StatusPayment getStatusPaymentByDate(Date finalDate){

        if(finalDate==null){
            return StatusPayment.PAYMENT_TIME_HAS_EXPIRED;
        }
        final Date currentDate=new Date();
        final StatusPayment statusPayment;

        Instant finalDatePlusOne=finalDate.toInstant().plus(1, ChronoUnit.DAYS);
        finalDate=Date.from(finalDatePlusOne);
        if(currentDate.getTime()>finalDate.getTime()){
            statusPayment=StatusPayment.PAYMENT_TIME_HAS_EXPIRED;
        }else {
            statusPayment=StatusPayment.PAID;
        }

        return statusPayment;
    }

    @Override
    @Transactional
    public ResponseTransfer editMember(final UUID memberUuid, final UUID newMemberUuid,
                                       final MemberType memberType, final UUID fileUuid,
                                       final Date initialDateLastPayment, final Date finalDateLastPayment){

        final ResponseTransfer responseTransfer;
        if(isMemberExists(memberUuid, newMemberUuid, memberType)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00067")
                    .message("error this member already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        Boolean isEdited=null;
        final StatusPayment statusPayment=getStatusPaymentByDate(finalDateLastPayment);
        switch (memberType) {
            case IS_COMPANY:
                isEdited = cocaiotMemberRepository.editMember(memberUuid, newMemberUuid, null, fileUuid,
                        statusPayment.name(), initialDateLastPayment, finalDateLastPayment);
                break;
            case IS_ENTREPRENEUR:
                isEdited=cocaiotMemberRepository.editMember(memberUuid, null, newMemberUuid, fileUuid,
                        statusPayment.name(), initialDateLastPayment, finalDateLastPayment);
                break;
        }
        if(isEdited==null || !isEdited){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00068")
                    .message("error cocaiot member don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00066")
                    .message("accept cocaiot member successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isMemberExists(final UUID memberUuid, final UUID newMemberUuid,
                                   final MemberType memberType) {

        boolean isExists=false;

        switch (memberType){
            case IS_COMPANY:
                isExists=cocaiotMemberRepository.isCompanyExists(memberUuid, newMemberUuid);
                break;
            case IS_ENTREPRENEUR:
                isExists=cocaiotMemberRepository.isEntrepreneurExists(memberUuid, newMemberUuid);
        }

        return isExists;
    }

    @Override
    @Transactional
    public ResponseTransfer removeMemberByUuid(final UUID memberUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=cocaiotMemberRepository.removeMemberByUuid(memberUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00069")
                    .message("error cocaiot member don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00067")
                    .message("accept cocaiot member successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAllCocaiotMemberDTOSBySearchKey(String searchKey, final int page, final int size,
                                                               List<String> sortBy, List<SortType> sortTypes,
                                                               final MemberType memberType){

        final ResponseTransfer responseTransfer;
//        List<CocaiotMemberDTO> cocaiotMemberDTOS = new LinkedList<>();
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

        Pageable pageable= PageRequest.of(page, size);
        List<CocaiotMember>cocaiotMembers=cocaiotMemberRepository.getCocaiotMembersBySearchKey(pageable, searchKey);

        if(cocaiotMembers==null){
            cocaiotMembers=new LinkedList<>();
        }
        if(sortBy==null){
            sortBy=new LinkedList<>();
            sortBy.add(defaultSortColumn);
        }
        if(sortTypes==null){
            sortTypes=new LinkedList<>();
            sortTypes.add(SortType.valueOf(defaultSortType));
        }
        List<TemporalCocaiotMember>temporalCocaiotMembers=new LinkedList<>();

        for(CocaiotMember cocaiotMember:cocaiotMembers){
            temporalCocaiotMembers.add(toTemporalCocaiotMember(cocaiotMember));
        }
        sortTemporalCocaiotMembers(temporalCocaiotMembers, sortBy, sortTypes);
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00068")
                .message("accept founded cocaiot member successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(temporalCocaiotMembers)
                .build();

        return responseTransfer;
    }

    private TemporalCocaiotMember toTemporalCocaiotMember(final CocaiotMember cocaiotMember){

        if(cocaiotMember==null){

            return null;
        }
        TemporalCocaiotMember temporalCocaiotMember=TemporalCocaiotMember.builder()
                .uuid(cocaiotMember.getUuid())
                .statusPayment(cocaiotMember.getStatusPayment())
                .created(cocaiotMember.getCreated())
                .build();
        if(cocaiotMember.getCompany()!=null){
            temporalCocaiotMember.setMemberName(cocaiotMember.getCompany().getName());
            temporalCocaiotMember.setTypeActivityNames(cocaiotMember.getCompany().getTypeActivities()!=null ?
                    cocaiotMember.getCompany().getTypeActivities().stream()
                            .map(TypeActivity::getName).collect(Collectors.toList()) : null);
            temporalCocaiotMember.setFullAddress(cocaiotMember.getCompany().getFullAddress());
            temporalCocaiotMember.setTypeOwnership(cocaiotMember.getCompany().getTypeOwnership());
            temporalCocaiotMember.setCountryDTO(cocaiotMember.getCompany().getCountry()!=null ?
                    cocaiotMember.getCompany().getCountry().toCountryDTOOnlyUuidAndName() : null);
            temporalCocaiotMember.setRegionDTO(cocaiotMember.getCompany().getRegion()!=null ?
                    cocaiotMember.getCompany().getRegion().toRegionDTOOnlyUuidAndName() : null);
            temporalCocaiotMember.setLogo(cocaiotMember.getCompany().getLogo()!=null ?
                    cocaiotMember.getCompany().getLogo().toFileDTO() : null);
        }else{
            temporalCocaiotMember.setMemberName(cocaiotMember.getEntrepreneur().getPerson().getFullName());
            temporalCocaiotMember.setTypeOwnership(TypeOwnership.ENTREPRENEUR);
            temporalCocaiotMember.setFullAddress(cocaiotMember.getEntrepreneur().getPerson().getFullAddressOfResidence());
            temporalCocaiotMember.setTypeActivityNames(cocaiotMember.getEntrepreneur().getTypeActivities()!=null ?
                    cocaiotMember.getEntrepreneur().getTypeActivities().stream()
                            .map(TypeActivity::getName).collect(Collectors.toList()) : null);
            temporalCocaiotMember.setCountryDTO(cocaiotMember.getEntrepreneur().getPerson().getCountry()!=null ?
                    cocaiotMember.getEntrepreneur().getPerson().getCountry().toCountryDTOOnlyUuidAndName() : null);
            temporalCocaiotMember.setRegionDTO(cocaiotMember.getEntrepreneur().getPerson().getRegion()!=null ?
                    cocaiotMember.getEntrepreneur().getPerson().getRegion().toRegionDTOOnlyUuidAndName() : null);
            temporalCocaiotMember.setLogo(cocaiotMember.getEntrepreneur().getLogo()!=null ?
                    cocaiotMember.getEntrepreneur().getLogo().toFileDTO() : null);
        }

        return temporalCocaiotMember;
    }

    private void sortTemporalCocaiotMembers(final List<TemporalCocaiotMember> temporalCocaiotMembers,
                                            final List<String> sortBy, final List<SortType> sortTypes) {

        Collections.sort(temporalCocaiotMembers, new Comparator<TemporalCocaiotMember>() {
            @Override
            public int compare(TemporalCocaiotMember temporalCocaiotMember, TemporalCocaiotMember t1) {

                int resultComparisons=0;
                for(int i=0; i<sortBy.size() && i<sortTypes.size() && resultComparisons==0; i++){
                    switch (sortBy.get(i)) {
                        case "memberName":
                            resultComparisons = temporalCocaiotMember.getMemberName().compareTo(t1.getMemberName());
                            break;
                        case "typeOwnership":
                            resultComparisons=temporalCocaiotMember.getTypeOwnership().name().
                                    compareTo(t1.getTypeOwnership().name());
                            break;
                        case "typeActivityNames":
                            Iterator<String>iteratorTempTypeActivity=temporalCocaiotMember.getTypeActivityNames().iterator();
                            Iterator<String>iteratorT1TypeActivity=t1.getTypeActivityNames().iterator();
                            String tempTypeActivityName;
                            String t1TypeActivityName;
                            while(resultComparisons==0 && iteratorTempTypeActivity.hasNext() && iteratorT1TypeActivity.hasNext()){
                                tempTypeActivityName=iteratorTempTypeActivity.next();
                                t1TypeActivityName=iteratorT1TypeActivity.next();
                                resultComparisons=tempTypeActivityName.compareTo(t1TypeActivityName);
                            }
                            if(resultComparisons==0){
                                resultComparisons=temporalCocaiotMember.getTypeActivityNames().size()-t1.getTypeActivityNames().size();
                            }
                            break;
                        case "statusPayment":
                            resultComparisons=temporalCocaiotMember.getStatusPayment().name().
                                    compareTo(t1.getStatusPayment().name());
                            break;
                        case "created":
                            resultComparisons=temporalCocaiotMember.getCreated().compareTo(t1.getCreated());
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

    private CocaiotMemberDTO toCocaiotMemberDTOOnlyGeneral(final CocaiotMember cocaiotMember,
                                                           final MemberType memberType) {

        CocaiotMemberDTO cocaiotMemberDTO=null;

        switch (memberType){
            case IS_COMPANY:
                cocaiotMemberDTO=CocaiotMemberDTO.builder()
                        .uuid(cocaiotMember.getUuid())
                        .companyDTO(cocaiotMember.getCompany().toCompanyDTOOnlyGeneral())
                        .typeCocaiotMember(memberType)
                        .build();
                break;
            case IS_ENTREPRENEUR:
                cocaiotMemberDTO=CocaiotMemberDTO.builder()
                        .uuid(cocaiotMember.getUuid())
                        .entrepreneurDTO(cocaiotMember.getEntrepreneur().toEntrepreneurDTOOnlyGeneral())
                        .typeCocaiotMember(memberType)
                        .build();
                break;
        }

        return cocaiotMemberDTO;
    }

    @Override
    public int getAmountMembersBySearchKey(String searchKey, final MemberType memberType){

        int amountMembers=0;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
//        switch (typeCocaiotMember){
//            case IS_COMPANY:
//                amountMembers=cocaiotMemberRepository.getAmountCompanyMembersBySearchKey(searchKey);
//                break;
//            case IS_ENTREPRENEUR:
//                amountMembers=cocaiotMemberRepository.getAmountEntrepreneurMembersBySearchKey(searchKey);
//                break;
//        }
        amountMembers=cocaiotMemberRepository.getAmountCocaiotMembersBySearchKey(searchKey);

        return amountMembers;
    }

    @Override
    public ResponseTransfer getCocaiotMemberDTOByUuid(final UUID cocaiotMemberUuid){

        final ResponseTransfer responseTransfer;
        final CocaiotMember cocaiotMember=cocaiotMemberRepository.getCocaiotMemberByUuid(cocaiotMemberUuid);

        if(cocaiotMember==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00070")
                    .message("error cocaiot member not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00069")
                    .message("accept cocaiot member successful founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toCocaiotMemberDTO(cocaiotMember))
                    .build();
        }

        return responseTransfer;
    }

    private CocaiotMemberDTO toCocaiotMemberDTO(final CocaiotMember cocaiotMember) {

        if(cocaiotMember==null){

            return null;
        }
        CompanyDTO companyDTO=null;
        EntrepreneurDTO entrepreneurDTO=null;
        MemberType memberType;

        if(cocaiotMember.getCompany()!=null){
            companyDTO=cocaiotMember.getCompany().toCompanyDTOOnlyGeneral();
            memberType = MemberType.IS_COMPANY;
        }else{
            entrepreneurDTO=cocaiotMember.getEntrepreneur().toEntrepreneurDTOOnlyGeneral();
            memberType = MemberType.IS_ENTREPRENEUR;
        }
        CocaiotMemberDTO cocaiotMemberDTO=CocaiotMemberDTO.builder()
                .uuid(cocaiotMember.getUuid())
                .companyDTO(companyDTO)
                .entrepreneurDTO(entrepreneurDTO)
                .statusPayment(cocaiotMember.getStatusPayment())
                .initialDate(cocaiotMember.getInitialDate())
                .finalDate(cocaiotMember.getFinalDate())
                .initialDateLastPayment(cocaiotMember.getInitialDateLastPayment())
                .fileDTO(cocaiotMember.getFile() != null ? cocaiotMember.getFile().toFileDTO() : null)
                .typeCocaiotMember(memberType)
                .build();

        return cocaiotMemberDTO;
    }

    @Transactional
    @Scheduled(cron = "1 0 0 * * *")
    void checkStatusPaymentCocaiotMembers(){

        cocaiotMemberRepository.checkStatusPaymentCocaiotMembers();
    }

}
