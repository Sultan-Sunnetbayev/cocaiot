package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.UserRepository;
import tm.salam.cocaiot.dtoes.UserDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.User;

import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilterBuilder filterBuilder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           FilterBuilder filterBuilder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addUser(final User user, final UUID roleUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isAdded=userRepository.addUser(user.getName(), user.getSurname(), user.getPatronomicName(),
                user.getEmail(), passwordEncoder.encode(user.getPassword()), roleUuid);

        if(isAdded==null || !isAdded){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept user successful added")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editUser(final User user, final UUID roleUuid){

        final ResponseTransfer responseTransfer;

        if(user.getUuid()==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user uuid invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
        final Boolean isEdited=userRepository.editUserByUuid(user.getUuid(), user.getName(), user.getSurname(),
                user.getPatronomicName(), user.getEmail(), passwordEncoder.encode(user.getPassword()), roleUuid);

        if(isEdited==null || !isEdited){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept user successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer removeUserByUuid(final UUID userUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=userRepository.removeUserByUuid(userUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept user successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public int getAmountUsersBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        final int amountUsers=userRepository.getAmountUsersBySearchKey(searchKey);

        return amountUsers;
    }

    @Override
    public ResponseTransfer getUserDTOSBySearchKey(String searchKey, final int page, final int size, List<String> sortBy,
                                                   List<SortType> sortTypes){

        final ResponseTransfer responseTransfer;

        sortBy=parseUserColumns(sortBy);
        if(sortBy!=null && isColumnsExistsInUser(sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error column not found in user")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        final Pageable pageable=filterBuilder.buildFilter(page, size, sortBy, sortTypes);

        if(pageable==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error with sorting")
                    .httpStatus(HttpStatus.FAILED_DEPENDENCY)
                    .build();

            return responseTransfer;
        }
        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<User>users=userRepository.getUsersBySearchKey(pageable, searchKey);
        List<UserDTO>userDTOS=new LinkedList<>();

        if(users==null){
            users=new LinkedList<>();
        }
        for(User user:users){
            userDTOS.add(toGeneralUserDTO(user));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept founded users successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(userDTOS)
                .build();

        return responseTransfer;
    }

    private List<String>parseUserColumns(final List<String>columns){

        if(columns==null){
            return null;
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String column:columns){
            switch (column){
                case "fullName":
                    parsedColumns.addAll(Arrays.asList("surname", "name", "patronomic_name"));
                    break;
                case "roleName":
                    parsedColumns.add("role.name");
                    break;
                default:
                    parsedColumns.add(column);
            }
        }

        return parsedColumns;
    }

    private boolean isColumnsExistsInUser(final List<String>columns){

        List<String>userColumns=userRepository.getUserColumns();

        if(userColumns==null || columns==null){

            return false;
        }
        userColumns.add("role.name");

        return userColumns.containsAll(columns);
    }

    private UserDTO toGeneralUserDTO(final User user){

        if(user==null){

            return null;
        }
        UserDTO userDTO=UserDTO.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .surname(user.getSurname())
                .patronomicName(user.getPatronomicName())
                .roleDTO(user.getRole().toRoleDTOOnlyUuidAndName())
                .build();

        return userDTO;
    }

    @Override
    public ResponseTransfer getUserDTOByUuid(final UUID userUuid){

        final ResponseTransfer responseTransfer;
        final User user=userRepository.getUserByUuid(userUuid);

        if(user==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("")
                    .message("accept user successful founded with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toUserDTO(user))
                    .build();
        }

        return responseTransfer;
    }

    private UserDTO toUserDTO(final User user){

        if(user==null){

            return null;
        }
        UserDTO userDTO= UserDTO.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .surname(user.getSurname())
                .patronomicName(user.getPatronomicName())
                .email(user.getEmail())
                .roleDTO(user.getRole().toRoleDTOOnlyUuidAndName())
                .build();

        return userDTO;
    }

    private UserDTO toUserDTOForProfile(final User user){

        if(user==null){

            return null;
        }
        UserDTO userDTO= UserDTO.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .surname(user.getSurname())
                .patronomicName(user.getPatronomicName())
                .email(user.getEmail())
                .roleDTO(user.getRole().toRoleDTO())
                .build();

        return userDTO;
    }

    @Override
    public User getUserByEmail(final String email){

        return userRepository.getUserByEmail(email);
    }

    @Override
    public ResponseTransfer getUserDTOByEmail(final String email){

        final ResponseTransfer responseTransfer;
        final User user=userRepository.getUserByEmail(email);

        if(user==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("")
                    .message("error user not found with this email")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();

            return responseTransfer;
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept user successful founded with this email")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(toUserDTOForProfile(user))
                .build();

        return responseTransfer;
    }

}
