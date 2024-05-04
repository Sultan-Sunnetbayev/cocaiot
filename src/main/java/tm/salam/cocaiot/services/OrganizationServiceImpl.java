package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.OrganizationRepository;
import tm.salam.cocaiot.dtoes.OrganizationDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Organization;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final FilterBuilder filterBuilder;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, FilterBuilder filterBuilder) {
        this.organizationRepository = organizationRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addOrganization(final Organization organization){

        final ResponseTransfer responseTransfer;
        final Boolean isAdded=organizationRepository.addOrganization(organization.getName());

        if(isAdded==null || !isAdded){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00033")
                    .message("error organization don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00023")
                    .message("accept organization successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editOrganization(final Organization organization){

        final ResponseTransfer responseTransfer;

        if(organization.getUuid()==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00034")
                    .message("error edited organization uuid invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        if(isOrganizationExistsByName(organization.getUuid(), organization.getName())){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00035")
                    .message("error edited organization name already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final Boolean isEdited=organizationRepository.editOrganization(organization.getUuid(), organization.getName());

        if(isEdited==null || !isEdited){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00036")
                    .message("error organization don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00024")
                    .message("accept organization successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isOrganizationExistsByName(final UUID organizationUuid, final String organizationName){

        return organizationRepository.isOrganizationExistsByName(organizationUuid, organizationName.toLowerCase(Locale.ROOT));
    }

    @Override
    @Transactional
    public ResponseTransfer removeOrganization(final UUID organizationUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=organizationRepository.removeOrganizationByUuid(organizationUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00037")
                    .message("error organization don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00025")
                    .message("accept organization successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAllOrganizationDTOS(String searchKey, final int page, final int size, List<String> sortBy,
                                                   List<SortType> sortTypes){

        List<OrganizationDTO>organizationDTOS=new LinkedList<>();
        final ResponseTransfer responseTransfer;

        if(sortBy!=null && !isOrganizationColumnsExists(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00038")
                    .message("error column not found in entity organization")
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
        List<Organization>organizations=organizationRepository.getOrganizationsBySearchKey(searchKey, pageable);

        if(organizations==null){
            organizations=new LinkedList<>();
        }
        for(Organization organization:organizations){
            organizationDTOS.add(toOrganizationDTO(organization));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00026")
                .message("accept all founded organizations successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(organizationDTOS)
                .build();

        return responseTransfer;
    }

    private OrganizationDTO toOrganizationDTO(final Organization organization){

        if(organization==null){

            return null;
        }
        OrganizationDTO organizationDTO= OrganizationDTO.builder()
                .uuid(organization.getUuid())
                .name(organization.getName())
                .amountCompany(organization.getAmountCompany())
                .build();

        return organizationDTO;
    }

    @Override
    public int getAmountOrganizationsBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        int amountOrganization=organizationRepository.getAmountOrganizationsBySearchKey(searchKey);

        return amountOrganization;
    }

    @Override
    public ResponseTransfer getOrganizationDTOByUuid(final UUID organizationUuid){

        final ResponseTransfer responseTransfer;
        final Organization organization=organizationRepository.getOrganizationByUuid(organizationUuid);

        if(organization==null){
            responseTransfer = ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00039")
                    .message("error organization not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer = ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00027")
                    .message("accept organization successful founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toOrganizationDTO(organization))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getColumnNameOrganizationById(final int id){

        final ResponseTransfer responseTransfer;
        final List<String>organizationColumns=organizationRepository.getOrganizationColumns();

        if(organizationColumns==null || id<0 || id>=organizationColumns.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00040")
                    .message("error column not found in entity organization")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00028")
                    .message("accept column successful founded in entity organization")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(organizationColumns.get(id))
                    .build();
        }

        return responseTransfer;
    }

    private boolean isOrganizationColumnsExists(final List<String>columnNames){

        List<String>organizationColumns=organizationRepository.getOrganizationColumns();

        if(organizationColumns==null || columnNames==null){

            return false;
        }
        int i=-1;

        for(String columnName:columnNames){
            i++;
            switch (columnName){
                case "amountCompany":
                    columnNames.set(i, "amount_company");
            }
        }

        return organizationColumns.containsAll(columnNames);
    }

    @Override
    public ResponseTransfer getOrganizationDTOSByName(String searchKey){

        final ResponseTransfer responseTransfer;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Organization>organizations=organizationRepository.getOrganizationsByName(searchKey);
        List<OrganizationDTO>organizationDTOS=new LinkedList<>();

        if(organizations==null){
            organizations=new LinkedList<>();
        }
        for(Organization organization:organizations){
            organizationDTOS.add(organization.toOrganizationDTOOnlyUuidAndName());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00060")
                .message("accept all found organization successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(organizationDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer isOrganizationExistsByUuid(final UUID organizationUuid){

        final ResponseTransfer responseTransfer;
        final boolean isOrganizationExists=organizationRepository.isOrganizationExistsByUuid(organizationUuid);

        if(!isOrganizationExists && organizationUuid!=null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00063")
                    .message("error organization not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .build();
        }

        return responseTransfer;
    }

}
