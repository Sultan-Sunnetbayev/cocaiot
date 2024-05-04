package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.RoleRepository;
import tm.salam.cocaiot.dtoes.RoleDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Role;
import tm.salam.cocaiot.models.RoleCategory;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;
    final FilterBuilder filterBuilder;

    public RoleServiceImpl(RoleRepository roleRepository, FilterBuilder filterBuilder) {
        this.roleRepository = roleRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addRole(Role role, final List<UUID>categoryUuids, List<Boolean> privilages){

        final ResponseTransfer responseTransfer;

        role.setName(removeDoubleWhiteSpace(role.getName()));
        if(isRoleNameExists(role.getName())){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        final UUID savedRoleUuid=roleRepository.addRole(role.getName());

        if(savedRoleUuid==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            for(int i=0; i<categoryUuids.size() && i<privilages.size(); i++){
                roleRepository.addRoleCategoryPrivilage(savedRoleUuid, categoryUuids.get(i), privilages.get(i));
            }
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept role successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isRoleNameExists(String roleName){

        return roleRepository.isRoleNameExists(roleName.toLowerCase(Locale.ROOT));
    }

    private String removeDoubleWhiteSpace(String str){

        str=str.trim();
        char lastch=str.charAt(0);
        StringBuilder str1=new StringBuilder();

        str1.append(lastch);
        for(int i=1; i<str.length(); i++) {
            if(lastch==' ' && str.charAt(i)==lastch) {
                continue;
            }
            str1.append(str.charAt(i));
            lastch=str.charAt(i);
        }

        return str1.toString();
    }

    @Override
    public ResponseTransfer editRole(Role role, final List<UUID> categoryUuids, List<Boolean> privilages){

        final ResponseTransfer responseTransfer;

        if(role.getUuid()==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role uuid invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        role.setName(removeDoubleWhiteSpace(role.getName()));
        if(isRoleExists(role)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error this role exists")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();

            return responseTransfer;
        }
        Boolean isEdited=roleRepository.editRole(role.getUuid(), role.getName());

        if(isEdited==null || !isEdited){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            for(int i=0; i<categoryUuids.size() && i<privilages.size(); i++){
                roleRepository.editPrivilageCategoryByRoleUuid(role.getUuid(), categoryUuids.get(i), privilages.get(i));
            }
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept role successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isRoleExists(final Role role){

        return roleRepository.isRoleExists(role.getUuid(),role.getName().toLowerCase(Locale.ROOT));
    }

    @Override
    public ResponseTransfer removeRole(final UUID roleUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=roleRepository.removeRoleByUuid(roleUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept role successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public int getAmountRolesBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        int amountRoles=roleRepository.getAmountRolesBySearchKey(searchKey);

        return amountRoles;
    }

    @Override
    public ResponseTransfer getRoleDTOSBySearchKey(String searchKey, final int page, final int size, List<String> sortBy,
                                                   List<SortType> sortTypes){

        final ResponseTransfer responseTransfer;
        List<RoleDTO>roleDTOS=new LinkedList<>();
        sortBy=parseRoleColumns(sortBy);
        if(sortBy!=null && !isColumnsExistsInRole(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role column not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        final Pageable pageable=filterBuilder.buildFilter(page, size, sortBy, sortTypes);

        if(pageable==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error with filter")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();

            return responseTransfer;
        }
        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Role>roles=roleRepository.getRolesBySearchKey(searchKey, pageable);

        if(roles==null){
            roles=new LinkedList<>();
        }
        for(Role role:roles){
            roleDTOS.add(toGeneralRoleDTO(role));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept all founded roles successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(roleDTOS)
                .build();

        return responseTransfer;
    }

    private List<String>parseRoleColumns(final List<String>columns){

        if(columns==null){

            return null;
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String column:columns){
            switch (column){
                case "roleName":
                    parsedColumns.add("name");
                    break;
                default:
                    parsedColumns.add(column);
            }
        }

        return parsedColumns;
    }

    private boolean isColumnsExistsInRole(List<String>columns){

        List<String>roleColumns=roleRepository.getRoleColumns();

        if(columns==null || roleColumns==null){

            return false;
        }

        return roleColumns.containsAll(columns);
    }

    private RoleDTO toGeneralRoleDTO(final Role role){

        if(role==null){

            return null;
        }
        RoleDTO roleDTO=RoleDTO.builder()
                .uuid(role.getUuid())
                .name(role.getName())
                .build();

        return roleDTO;
    }

    @Override
    public ResponseTransfer getRoleDTOByUuid(final UUID roleUuid){

        final ResponseTransfer responseTransfer;
        final Role role=roleRepository.getRoleByUuid(roleUuid);

        if(role==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept role successful founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toRoleDTO(role))
                    .build();
        }

        return responseTransfer;
    }

    private RoleDTO toRoleDTO(final Role role){

        if(role==null){

            return null;
        }
        RoleDTO roleDTO=RoleDTO.builder()
                .uuid(role.getUuid())
                .name(role.getName())
                .roleCategoryDTOS(role.getRoleCategories() != null ?
                        role.getRoleCategories().stream()
                                .map(RoleCategory::toPrivilageCategoryDTO)
                                .collect(Collectors.toList()) :
                        null)
                .build();

        return roleDTO;
    }

    @Override
    public ResponseTransfer getRoleDTOSByName(String searchKey){

        final ResponseTransfer responseTransfer;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Role>roles=roleRepository.getRolesByName(searchKey);
        List<RoleDTO>roleDTOS=new LinkedList<>();

        if(roles==null){
            roles=new LinkedList<>();
        }
        for(Role role:roles){
            roleDTOS.add(role.toRoleDTOOnlyUuidAndName());
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept all founded roles successful returned")
                .data(roleDTOS)
                .httpStatus(HttpStatus.ACCEPTED)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer isRoleExistsByUuid(final UUID roleUuid){

        final ResponseTransfer responseTransfer;
        final boolean isExists=roleRepository.isRoleExistsByUuid(roleUuid);

        if(isExists){
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error role not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public boolean getPrivilageByRoleNameAndCategoryName(final String roleName, final String categoryName){

        Boolean privilage=roleRepository.getPrivilageByRoleNameAndCategoryName(roleName, categoryName);

        return privilage==null ? false : privilage;
    }

}
