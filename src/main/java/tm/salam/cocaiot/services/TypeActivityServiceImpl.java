package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.TypeActivityRepository;
import tm.salam.cocaiot.dtoes.TypeActivityDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.TypeActivity;

import java.util.*;

@Service
public class TypeActivityServiceImpl implements TypeActivityService {

    private final TypeActivityRepository typeActivityRepository;
    private final FilterBuilder filterBuilder;

    public TypeActivityServiceImpl(TypeActivityRepository typeActivityRepository, FilterBuilder filterBuilder) {
        this.typeActivityRepository = typeActivityRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addTypeActivity(final TypeActivity typeActivity){

        final Boolean isAdded=typeActivityRepository.addTypeActivity(typeActivity.getName());
        final ResponseTransfer responseTransfer;

        if(isAdded==null || !isAdded){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00001")
                    .message("error this activity type already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else {
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00001")
                    .message("accept activity successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }


    private boolean isActivityExistsByName(final String name, final UUID typeActivityUuid){

        return typeActivityRepository.isTypeActivityExistsByName(name.toLowerCase(Locale.ROOT), typeActivityUuid);
    }

    private boolean isTypeActivityExistsByUuid(final UUID typeActivityUuid){

        return typeActivityRepository.isTypeActivityExistsByUuid(typeActivityUuid);
    }

    @Override
    @Transactional
    public ResponseTransfer editTypeActivityByUuid(final TypeActivity typeActivity){

        final ResponseTransfer responseTransfer;

        if(typeActivity.getUuid()==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00002")
                    .message("error invalid uuid edited type activity")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        if(isActivityExistsByName(typeActivity.getName(), typeActivity.getUuid())){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00004")
                    .message("error name edited type activity already exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final Boolean isEdited=typeActivityRepository.editTypeActivityByUuid(typeActivity.getUuid(),typeActivity.getName());

        if(isEdited==null || !isEdited){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00005")
                    .message("error type activity don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00002")
                    .message("accept type activity successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer removeTypeActivityByUuid(final UUID typeActivityUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=typeActivityRepository.removeTypeActivityByUuid(typeActivityUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer = ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00008")
                    .message("error type activity don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00003")
                    .message("accept type activity successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAllTypeActivityDTOS(String searchKey, final int page, int size, List<String>sortBy,
                                                   List<SortType>sortTypes){

        List<TypeActivityDTO>typeActivityDTOS=new LinkedList<>();
        final ResponseTransfer responseTransfer;

        if(sortBy!=null && !isTypeActivityColumnsExists(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00009")
                    .message("error column not found in type activity")
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
        List<TypeActivity>typeActivities=typeActivityRepository.getTypeActivitiesBySearchKey(searchKey, pageable);

        if(typeActivities==null){
            typeActivities=new LinkedList<>();
        }
        for(TypeActivity typeActivity:typeActivities){
            typeActivityDTOS.add(toTypeActivityDTO(typeActivity));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00004")
                .message("accept all type activity successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(typeActivityDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getTypeActivityDTOByUuid(final UUID typeActivityUuid){

        TypeActivity typeActivity=typeActivityRepository.getTypeActivityByUuid(typeActivityUuid);
        ResponseTransfer responseTransfer;

        if(typeActivity==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00003")
                    .message("error not found type activity with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00005")
                    .message("accept type activity successful return with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toTypeActivityDTO(typeActivity))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public int getAmountTypeActivityBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }

        return typeActivityRepository.getAmountTypeActivityBySearchKey(searchKey);
    }

    private TypeActivityDTO toTypeActivityDTO(final TypeActivity typeActivity){

        if(typeActivity==null){

            return null;
        }
        TypeActivityDTO typeActivityDTO=TypeActivityDTO.builder()
                .uuid(typeActivity.getUuid())
                .name(typeActivity.getName())
                .amountCompany(typeActivity.getAmountCompany())
                .build();

        return typeActivityDTO;
    }

    private boolean isTypeActivityColumnsExists(List<String>columnNames){

        final List<String>typeActivityColumns=typeActivityRepository.getTypeActivityColumns();

        if(columnNames==null || typeActivityColumns==null){

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

        return typeActivityColumns.containsAll(columnNames);
    }

    @Override
    public ResponseTransfer getColumnNameTypeActivityById(final int id){

        final ResponseTransfer responseTransfer;
        List<String>typeActivityColumns=typeActivityRepository.getTypeActivityColumns();

        if(typeActivityColumns==null || id<0 || id>=typeActivityColumns.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00011")
                    .message("error not found column type activity with this id")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else {
            responseTransfer = ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00007")
                    .message("accept type activity column name successful returned by id")
                    .data(typeActivityColumns.get(id))
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getTypeActivityDTOSByName(String searchKey){

        final ResponseTransfer responseTransfer;

        if (searchKey == null) {
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<TypeActivity>typeActivities=typeActivityRepository.getTypeActivitiesByName(searchKey);
        List<TypeActivityDTO>typeActivityDTOS=new LinkedList<>();

        if(typeActivities==null){
            typeActivities=new LinkedList<>();
        }
        for(TypeActivity typeActivity:typeActivities){
            typeActivityDTOS.add(typeActivity.toTypeActivityDTOOnlyUuidAndName());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00059")
                .message("accept all found type activity successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(typeActivityDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer isTypeActivitiesExistsByUuids(List<UUID>typeActivityUuids){

        final ResponseTransfer responseTransfer;

        if(typeActivityUuids==null){
            typeActivityUuids=new ArrayList<>();
        }
        int amountTypeActivity=typeActivityRepository.getAmountTypeActivityByUuids(typeActivityUuids);

        if(amountTypeActivity==typeActivityUuids.size()){
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00062")
                    .message("error type activity not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getTypeActivityDTOSByCompanyUuids(final List<UUID>companyUuids){

        final ResponseTransfer responseTransfer;
        List<TypeActivity>typeActivities=typeActivityRepository.getTypeActivitiesByCompanyUuids(companyUuids);
        List<TypeActivityDTO>typeActivityDTOS=new LinkedList<>();

        if(typeActivities==null){
            typeActivities=new LinkedList<>();
        }
        for(TypeActivity typeActivity:typeActivities){
            typeActivityDTOS.add(typeActivity.toTypeActivityDTOOnlyUuidAndName());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00070")
                .message("accept founded type activities successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(typeActivityDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    @Transactional
    public void incrementAmountCompanyByTypeActivityUuids(final List<UUID> typeActivityUuids, final int value){

        typeActivityRepository.incrementAmountCompanyByTypeActivityUuids(typeActivityUuids, value);
    }

    @Override
    @Transactional
    public void decrementAmountCompanyByTypeActivityUuids(final List<UUID> typeActivityUuids, final int value){

        typeActivityRepository.decrementAmountCompanyByTypeActivityUuids(typeActivityUuids, value);
    }

    @Override
    public List<UUID> getTypeActivityUuidsByEntrepreneurUuid(final UUID entrepreneurUuid){

        List<UUID>typeActivityUuids=typeActivityRepository.getTypeActivityUuidsByEntrepreneurUuid(entrepreneurUuid);

        if(typeActivityUuids==null){
            typeActivityUuids=new LinkedList<>();
        }

        return typeActivityUuids;
    }

}
