package tm.salam.cocaiot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.MailingRepository;
import tm.salam.cocaiot.dtoes.MailingDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.helpers.TemporalMailing;
import tm.salam.cocaiot.models.Company;
import tm.salam.cocaiot.models.Entrepreneur;
import tm.salam.cocaiot.models.Mailing;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailingServiceImpl implements MailingService {

    private final MailingRepository mailingRepository;
    private final FilterBuilder filterBuilder;

    @Value("${default.sort.column}")
    private String defaultSortColumn;
    @Value("${default.sort.type}")
    private String defaultSortType;

    public MailingServiceImpl(MailingRepository mailingRepository, FilterBuilder filterBuilder) {
        this.mailingRepository = mailingRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addMailing(final Mailing mailing, final UUID fileUuid, final List<UUID> companyUuids,
                                       List<UUID>entrepreneurUuids){

        final ResponseTransfer responseTransfer;
        final UUID savedMailingUuid=mailingRepository.addMailing(mailing.getName(), mailing.getText(), fileUuid,
                mailing.getTypeMailing().name());

        if(savedMailingUuid==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00073")
                    .message("error mailing don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        if(companyUuids!=null) {
            for (UUID companyUuid : companyUuids) {
                mailingRepository.addCompanyToMailing(savedMailingUuid, companyUuid);
            }
        }
        if(entrepreneurUuids!=null) {
            for (UUID entrepreneurUuid : entrepreneurUuids) {
                mailingRepository.addEntrepreneurToMailing(savedMailingUuid, entrepreneurUuid);
            }
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept mailing successful added")
                .httpStatus(HttpStatus.ACCEPTED)
                .build();

        return responseTransfer;
    }

    @Override
    public int getAmountMailingsBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        final int amountMailings=mailingRepository.getAmountMailingsBySearchKey(searchKey);

        return amountMailings;
    }

    @Override
    public ResponseTransfer getMailingDTOSBySearchKey(String searchKey, final int page, final int size, List<String> sortBy,
                                                      List<SortType> sortTypes){

        final ResponseTransfer responseTransfer;

//        List<MailingDTO>mailingDTOS=new LinkedList<>();
//        sortBy=parseMailingColumns(sortBy);
//        if(sortBy!=null && !isMailingColumnsExists(sortBy)){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("")
//                    .message("error mailing company columns not found")
//                    .httpStatus(HttpStatus.NOT_FOUND)
//                    .build();
//
//            return responseTransfer;
//        }
//        final Pageable pageable= filterBuilder.buildFilter(page, size, sortBy, sortTypes);

        final Pageable pageable=PageRequest.of(page, size);

        if (searchKey == null) {
            searchKey = "";
        } else {
            searchKey = searchKey.toLowerCase(Locale.ROOT);
        }
        List<Mailing>mailings=mailingRepository.getMailingsBySearchKey(searchKey, pageable);

        if(mailings==null){
            mailings=new LinkedList<>();
        }
        if(sortBy==null){
            sortBy=new LinkedList<>();
            sortBy.add(defaultSortColumn);
        }
        if(sortTypes==null){
            sortTypes=new LinkedList<>();
            sortTypes.add(SortType.valueOf(defaultSortType));
        }
        List<TemporalMailing>temporalMailings=new LinkedList<>();

        for(Mailing mailing:mailings){
            temporalMailings.add(toTemporalMailing(mailing));
        }
        sortMailings(temporalMailings, sortBy, sortTypes);
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept all founded mailings successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(temporalMailings)
                .build();

        return responseTransfer;
    }

    private TemporalMailing toTemporalMailing(final Mailing mailing){

        if(mailing==null){

            return null;
        }
        TemporalMailing temporalMailing= TemporalMailing.builder()
                .uuid(mailing.getUuid())
                .name(mailing.getName())
                .text(mailing.getText())
                .typeMailing(mailing.getTypeMailing())
                .created(mailing.getCreated())
                .build();
        List<String>recipientNames=new LinkedList<>();
        Set<String>typeActivityNames=new TreeSet<>();

        if(mailing.getCompanies()!=null) {
            mailing.getCompanies().stream().map(Company::getName)
                    .collect(Collectors.toCollection(() -> recipientNames));
            for(Company company:mailing.getCompanies()){
                company.getTypeActivities().stream().map(TypeActivity::getName)
                        .collect(Collectors.toCollection(() -> typeActivityNames));
            }
        }
        if(mailing.getEntrepreneurs()!=null) {
            for(Entrepreneur entrepreneur:mailing.getEntrepreneurs()){
                recipientNames.add(entrepreneur.getPerson().getFullName());
                entrepreneur.getTypeActivities().stream().map(TypeActivity::getName)
                        .collect(Collectors.toCollection(() -> typeActivityNames));
            }
        }
        temporalMailing.setRecipients(recipientNames);
        temporalMailing.setTypeActivityNames(typeActivityNames);

        return temporalMailing;
    }

    private List<String>parseMailingColumns(List<String>columns){

        if(columns==null){
            return null;
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String column:columns){
            switch (column){
                case "typeMailing":
                    parsedColumns.add("type_mailing");
                default:
                    parsedColumns.add(column);
            }
        }

        return parsedColumns;
    }

    private boolean isMailingColumnsExists(List<String>columns){

        List<String>mailingColumns=mailingRepository.getMailingColumns();

        if(columns==null || mailingColumns==null){
            return false;
        }

        return mailingColumns.containsAll(columns);
    }

    private void sortMailings(List<TemporalMailing>temporalMailings, final List<String>sortBy, final List<SortType>sortTypes){

        if(sortBy==null || sortTypes==null){

            return;
        }
        Collections.sort(temporalMailings, new Comparator<TemporalMailing>() {
            @Override
            public int compare(TemporalMailing temporalMailing, TemporalMailing t1) {
                int resultComparisons=0;

                for(int i=0; i<sortBy.size() && i<sortTypes.size() && resultComparisons==0; i++){
                    switch (sortBy.get(i)) {
                        case "name":
                            resultComparisons = temporalMailing.getName().compareTo(t1.getName());
                            break;
                        case "typeMailing":
                            resultComparisons=temporalMailing.getTypeMailing().name().compareTo(t1.getTypeMailing().name());
                            break;
                        case "text":
                            resultComparisons=temporalMailing.getText().compareTo(t1.getText());
                            break;
                        case "recipientNames":
                            Iterator<String>iteratorTemporalMailing=temporalMailing.getRecipients().iterator();
                            Iterator<String>iteratorT1=t1.getRecipients().iterator();
                            String temporalMailingRecipientName;
                            String t1RecipientName;
                            while(resultComparisons==0 && iteratorTemporalMailing.hasNext() && iteratorT1.hasNext()){
                                temporalMailingRecipientName=iteratorTemporalMailing.next();
                                t1RecipientName=iteratorT1.next();
                                resultComparisons=temporalMailingRecipientName.compareTo(t1RecipientName);
                            }
                            if(resultComparisons==0){
                                resultComparisons=temporalMailing.getRecipients().size()-t1.getRecipients().size();
                            }
                            break;
                        case "typeActivityNames":
                            iteratorTemporalMailing=temporalMailing.getTypeActivityNames().iterator();
                            iteratorT1=t1.getTypeActivityNames().iterator();
                            String temporalMailingTypeActivityName;
                            String t1TypeActivityName;
                            while(resultComparisons==0 && iteratorTemporalMailing.hasNext() && iteratorT1.hasNext()){
                                temporalMailingTypeActivityName=iteratorTemporalMailing.next();
                                t1TypeActivityName=iteratorT1.next();
                                resultComparisons=temporalMailingTypeActivityName.compareTo(t1TypeActivityName);
                            }
                            if(resultComparisons==0){
                                resultComparisons=temporalMailing.getTypeActivityNames().size()-t1.getTypeActivityNames().size();
                            }
                            break;
                        case "created":
                            resultComparisons=temporalMailing.getCreated().compareTo(t1.getCreated());
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

    private MailingDTO toGeneralMailingDTO(final Mailing mailing){

        if(mailing==null){

            return null;
        }
        MailingDTO mailingDTO=MailingDTO.builder()
                .uuid(mailing.getUuid())
                .name(mailing.getName())
                .text(mailing.getText())
                .companyDTOS(mailing.getCompanies().stream().map(Company::toCompanyDTOOnlyUuidAndName)
                        .collect(Collectors.toList()))
                .entrepreneurDTOS(mailing.getEntrepreneurs().stream()
                        .map(Entrepreneur::toEntrepreneurDTOWithFullNameAndTypeActivities)
                        .collect(Collectors.toList()))
                .typeMailing(mailing.getTypeMailing())
                .build();

        return mailingDTO;
    }

    @Override
    public ResponseTransfer getMailingDTOByUuid(final UUID mailingUuid){

        final ResponseTransfer responseTransfer;
        final Mailing mailing=mailingRepository.getMailingByUuid(mailingUuid);

        if(mailing==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error mailing not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept mailing successful returned")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toMailingDTO(mailing))
                    .build();
        }

        return responseTransfer;
    }

    private MailingDTO toMailingDTO(final Mailing mailing){

        if(mailing==null){

            return null;
        }
        MailingDTO mailingDTO=MailingDTO.builder()
                .uuid(mailing.getUuid())
                .name(mailing.getName())
                .text(mailing.getText())
                .fileDTO(mailing.getFile() != null ? mailing.getFile().toFileDTO() : null)
                .companyDTOS(mailing.getCompanies().stream().map(Company::toCompanyDTOWithTypeActivities)
                        .collect(Collectors.toList()))
                .entrepreneurDTOS(mailing.getEntrepreneurs().stream()
                        .map(Entrepreneur::toEntrepreneurDTOWithFullNameAndTypeActivities)
                        .collect(Collectors.toList()))
                .typeMailing(mailing.getTypeMailing())
                .build();

        return mailingDTO;
    }

    @Override
    @Transactional
    public ResponseTransfer removeMailingByUuid(final UUID mailingUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=mailingRepository.removeMailingByUuid(mailingUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error mailing don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept mailing successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

}
