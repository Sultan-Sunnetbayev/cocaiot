package tm.salam.cocaiot.services;

import org.springframework.stereotype.Service;
import tm.salam.cocaiot.daoes.MailingCompanyRepository;

@Service
public class MailingCompanyServiceImpl implements MailingCompanyService {

    private final MailingCompanyRepository mailingCompanyRepository;
//    private final FilterBuilder filterBuilder;

//    @Value("${default.sort.column}")
//    private String defaultSortColumn;
//    @Value("${default.sort.type}")
//    private String defaultSortType;

    public MailingCompanyServiceImpl(MailingCompanyRepository mailingCompanyRepository) {
        this.mailingCompanyRepository = mailingCompanyRepository;
    }

//    @Override
//    @Transactional
//    public ResponseTransfer removeMailingCompanyByUuid(final UUID mailingUuid, final UUID companyUuid){
//
//        final ResponseTransfer responseTransfer;
//        final Boolean isRemoved=mailingCompanyRepository.removeMailingCompanyByMailingCompanyUuid(mailingUuid, companyUuid);
//
//        if(isRemoved==null || !isRemoved){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("")
//                    .message("error mailing company don't removed")
//                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
//                    .build();
//        }else{
//            responseTransfer=ResponseTransfer.builder()
//                    .status(true)
//                    .code("")
//                    .message("accept mailing company successful removed")
//                    .httpStatus(HttpStatus.ACCEPTED)
//                    .build();
//        }
//
//        return responseTransfer;
//    }

//    @Override
//    public ResponseTransfer getAllMailingCompanyDTOS(String searchKey, final int page, final int size, List<String> sortBy,
//                                                     List<SortType> sortTypes){
//
//        final ResponseTransfer responseTransfer;
//        List<MailingCompanyDTO>mailingCompanyDTOS=new LinkedList<>();
//        sortBy=parseMailingCompanyColumns(sortBy);
//        if(sortBy!=null && !isMailingCompanyColumnsExists(sortBy)){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("")
//                    .message("error mailing company columns not found")
//                    .httpStatus(HttpStatus.NOT_FOUND)
//                    .data(mailingCompanyDTOS)
//                    .build();
//
//            return responseTransfer;
//        }
//        final Pageable pageable= filterBuilder.buildFilter(page, size, sortBy, sortTypes);
//        final Pageable pageable=PageRequest.of(page, size);
//
//        if (searchKey == null) {
//            searchKey = "";
//        } else {
//            searchKey = searchKey.toLowerCase(Locale.ROOT);
//        }
//        List<MailingCompany>mailingCompanies=mailingCompanyRepository.getMailingCompaniesBySearchKey(searchKey, pageable);
//
//        if(mailingCompanies==null){
//            mailingCompanies=new LinkedList<>();
//        }
//        if(sortBy==null){
//            sortBy=new LinkedList<>();
//            sortBy.add(defaultSortColumn);
//        }
//        if(sortTypes==null){
//            sortTypes=new LinkedList<>();
//            sortTypes.add(SortType.valueOf(defaultSortType));
//        }
//        sortMailingCompanies(mailingCompanies, sortBy, sortTypes);
//        for(MailingCompany mailingCompany:mailingCompanies){
//            mailingCompanyDTOS.add(toGeneralMailingCompanyDTO(mailingCompany));
//        }
//        responseTransfer=ResponseTransfer.builder()
//                .status(true)
//                .code("")
//                .message("accept all founded mailings companies successful returned")
//                .httpStatus(HttpStatus.ACCEPTED)
//                .data(mailingCompanyDTOS)
//                .build();
//
//        return responseTransfer;
//    }

//    private List<String>parseMailingCompanyColumns(List<String>columns){
//
//        if(columns==null){
//            return null;
//        }
//        List<String>parsedColumns=new LinkedList<>();
//
//        for(String column:columns){
//            switch (column){
//                case "mailingName":
//                    parsedColumns.add("mailing.name");
//                    break;
//                case "typeMailing":
//                    parsedColumns.add("mailing.type_mailing");
//                case "companyName":
//                    parsedColumns.add("company.name");
//                    break;
//                case "companyTypeOwnership":
//                    parsedColumns.add("company.type_ownership");
//                    break;
//                case "typeActivityName":
//                    parsedColumns.add("type_activity.name");
//                    break;
//                default:
//                    parsedColumns.add(column);
//            }
//        }
//
//        return parsedColumns;
//    }

//    private boolean isMailingCompanyColumnsExists(List<String>columns){
//
//        final List<String>mailingCompanyColumns=new LinkedList<>(Arrays.asList("mailing.name", "mailing.type_mailing",
//                "company.name", "company.type_ownership", "type_activity.name"));
//
//        if(columns==null || mailingCompanyColumns==null){
//            return false;
//        }
//
//        return mailingCompanyColumns.containsAll(columns);
//    }

//    private MailingCompanyDTO toGeneralMailingCompanyDTO(final MailingCompany mailingCompany){
//
//        if (mailingCompany == null) {
//            return null;
//        }
//        MailingCompanyDTO mailingCompanyDTO=MailingCompanyDTO.builder()
//                .uuid(mailingCompany.getUuid())
//                .mailingDTO(mailingCompany.getMailing().toMailingDTOOnlyGeneral())
//                .companyDTO(mailingCompany.getCompany().toCompanyDTOWithTypeActivities())
//                .build();
//
//        return mailingCompanyDTO;
//    }

//    private void sortMailingCompanies(List<MailingCompany>mailingCompanies, final List<String>sortBy,
//                                      final List<SortType>sortTypes){
//
//        if(sortBy==null || sortTypes==null){
//
//            return;
//        }
//        Collections.sort(mailingCompanies, new Comparator<MailingCompany>() {
//            @Override
//            public int compare(MailingCompany mailingCompany, MailingCompany t1) {
//                int resultComparisons=0;
//
//                for(int i=0; i<sortBy.size() && i<sortTypes.size(); i++){
//                    switch (sortBy.get(i)) {
//                        case "mailingName":
//                            resultComparisons = mailingCompany.getMailing().getName()
//                                    .compareTo(t1.getMailing().getName());
//                            break;
//                        case "typeMailing":
//                            resultComparisons=mailingCompany.getMailing().getTypeMailing().name()
//                                    .compareTo(t1.getMailing().getTypeMailing().name());
//                            break;
//                        case "companyName":
//                            resultComparisons=mailingCompany.getCompany().getName().
//                                    compareTo(t1.getCompany().getName());
//                            break;
//                        case "companyTypeOwnership":
//                            resultComparisons=mailingCompany.getCompany().getTypeOwnership().name()
//                                    .compareTo(t1.getCompany().getTypeOwnership().name());
//                            break;
//                        case "typeActivityName":
//                            final int sz=Math.max(mailingCompany.getCompany().getTypeActivities().size(),
//                                    t1.getCompany().getTypeActivities().size());
//                            String mailingCompanyTypeActivity, t1TypeActivity;
//                            for(int e=0; e<sz && resultComparisons==0; e++){
//                                if(e<mailingCompany.getCompany().getTypeActivities().size()){
//                                    mailingCompanyTypeActivity=mailingCompany.getCompany().getTypeActivities().get(e).getName();
//                                }else{
//                                    mailingCompanyTypeActivity="";
//                                }
//                                if(e<t1.getCompany().getTypeActivities().size()){
//                                    t1TypeActivity=t1.getCompany().getTypeActivities().get(e).getName();
//                                }else{
//                                    t1TypeActivity="";
//                                }
//                                resultComparisons=mailingCompanyTypeActivity.compareTo(t1TypeActivity);
//                            }
//                            break;
//                        case "created":
//                            resultComparisons=mailingCompany.getCreated().compareTo(t1.getCreated());
//                            break;
//                    }
//                    if(resultComparisons!=0){
//                        switch (sortTypes.get(i)){
//                            case ASCENDING:
//                                return resultComparisons;
//                            case DESCENDING:
//                                return -resultComparisons;
//                        }
//                    }
//                }
//                return resultComparisons;
//            }
//        });
//    }

//    @Override
//    public ResponseTransfer getMailingCompanyDTOByUuid(final UUID mailingCompanyUuid){
//
//        final ResponseTransfer responseTransfer;
//        final MailingCompany mailingCompany=mailingCompanyRepository.getMailingCompanyByUuid(mailingCompanyUuid);
//
//        if(mailingCompany==null){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("")
//                    .message("error mailing company not found with this uuid")
//                    .httpStatus(HttpStatus.NOT_FOUND)
//                    .build();
//        }else{
//            responseTransfer=ResponseTransfer.builder()
//                    .status(true)
//                    .code("")
//                    .message("accept mailing company successful returned")
//                    .httpStatus(HttpStatus.ACCEPTED)
//                    .data(toMailingCompanyDTO(mailingCompany))
//                    .build();
//        }
//
//        return responseTransfer;
//    }

//    private MailingCompanyDTO toMailingCompanyDTO(final MailingCompany mailingCompany){
//
//        if(mailingCompany==null){
//
//            return null;
//        }
//        MailingCompanyDTO mailingCompanyDTO=MailingCompanyDTO.builder()
//                .uuid(mailingCompany.getUuid())
//                .mailingDTO(mailingCompany.getMailing().toMailingDTO())
//                .companyDTO(mailingCompany.getCompany().toCompanyDTOWithTypeActivities())
//                .build();
//
//        return mailingCompanyDTO;
//    }

}
