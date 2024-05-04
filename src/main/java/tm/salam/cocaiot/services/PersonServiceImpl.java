package tm.salam.cocaiot.services;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.salam.cocaiot.daoes.PersonRepository;
import tm.salam.cocaiot.dtoes.PersonDTO;
import tm.salam.cocaiot.helpers.FilterBuilder;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Company;
import tm.salam.cocaiot.models.Person;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final FilterBuilder filterBuilder;

    public PersonServiceImpl(PersonRepository personRepository, FilterBuilder filterBuilder) {
        this.personRepository = personRepository;
        this.filterBuilder = filterBuilder;
    }

    @Override
    @Transactional
    public ResponseTransfer addPerson(final Person person, final UUID imageUuid, final UUID copyPassportUuid,
                                      final UUID countryUuid, final UUID regionUuid){

        final ResponseTransfer responseTransfer;
        final UUID savedPersonUuid=personRepository.addPerson(person.getName(), person.getSurname(), person.getPatronomicName(),
                person.getBirthPlace(), person.getBirthDate(), imageUuid, copyPassportUuid, countryUuid, regionUuid,
                person.getFullAddressOfResidence(), person.getPhoneNumber(), person.getFax(), person.getEmail(),
                person.getEducation(), person.getExperience(), person.getKnowledgeOfLanguages());

        if(savedPersonUuid==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00041")
                    .message("error person don't added")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }else{
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00030")
                    .message("accept person successful added")
                    .data(savedPersonUuid)
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    @Transactional
    public ResponseTransfer editPerson(final Person person, final UUID imageUuid, final UUID copyPassportUuid,
                                       final UUID countryUuid, final UUID regionUuid){

        final ResponseTransfer responseTransfer;

        if(person.getUuid()==null){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00042")
                    .message("error edited person uuid invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();

            return responseTransfer;
        }
//        if(isPersonExistsByEmail(person.getUuid(), person.getEmail())){
//            responseTransfer=ResponseTransfer.builder()
//                    .status(false)
//                    .code("SR-00043")
//                    .message("error edited person email already exists")
//                    .httpStatus(HttpStatus.CONFLICT)
//                    .build();
//
//            return responseTransfer;
//        }
        final Boolean isEdited=personRepository.editPerson(person.getUuid(),person.getName(), person.getSurname(),
                person.getPatronomicName(), person.getBirthPlace(), person.getBirthDate(), imageUuid,
                copyPassportUuid, countryUuid, regionUuid, person.getFullAddressOfResidence(), person.getPhoneNumber(),
                person.getFax(), person.getEmail(), person.getEducation(), person.getExperience(),
                person.getKnowledgeOfLanguages());

        if(isEdited==null || !isEdited){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00044")
                    .message("error person don't edited")
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00031")
                    .message("accept person successful edited")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    private boolean isPersonExistsByEmail(final UUID personUuid, final String personEmail){

        return personRepository.isPersonExistsByEmail(personUuid, personEmail);
    }

    @Override
    @Transactional
    public ResponseTransfer removePerson(final UUID personUuid){

        final ResponseTransfer responseTransfer;
        final Boolean isRemoved=personRepository.removePersonByUuid(personUuid);

        if(isRemoved==null || !isRemoved){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00045")
                    .message("error person don't removed")
                    .httpStatus(HttpStatus.EXPECTATION_FAILED)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00032")
                    .message("accept person successful removed")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public int getAmountPersonsBySearchKey(String searchKey){

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        int amountPerson=personRepository.getAmountPersonsBySearchKey(searchKey);

        return amountPerson;
    }

    @Override
    public ResponseTransfer getAllPersonDTOS(String searchKey, final int page, final int size, List<String> sortBy,
                                             List<SortType> sortTypes){

        final ResponseTransfer responseTransfer;

        sortBy=parsePersonColumns(null, sortBy);
        if(sortBy!=null && !isPersonColumnsExists(null, sortBy)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00046")
                    .message("error column not found in entity person")
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
        List<Person>persons=personRepository.getPersonsBySearchKey(searchKey, pageable);
        List<PersonDTO>personDTOS=new LinkedList<>();

        if(persons==null){
            persons=new LinkedList<>();
        }
        for(Person person:persons){
            personDTOS.add(toPersonDTOOnlyGeneral(person));
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("SS-00033")
                .message("accept founded all persons successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(personDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public boolean isPersonColumnsExists(String withStart, List<String> columnNames) {

        List<String>personColumns=getPersonColumns();

        if(personColumns==null || columnNames==null){

            return false;
        }
        if(withStart!=null){
            for(int i=0; i<personColumns.size(); i++){
                personColumns.set(i, withStart+personColumns.get(i));
            }
        }
        personColumns.add("region.name");
        personColumns.add("country.name");

        return personColumns.containsAll(columnNames);
    }

    @Override
    public List<String> parsePersonColumns(String withStart, final List<String> columnNames){

        if(columnNames==null){

            return null;
        }
        if(withStart==null){
            withStart="";
        }
        List<String>parsedColumns=new LinkedList<>();

        for(String columnName:columnNames) {
            switch (columnName){
                case "fullName":
                    parsedColumns.addAll(Arrays.asList(withStart+"surname", withStart+"name", withStart+"patronomic_name"));
                    break;
                case "birthPlace":
                    parsedColumns.add(withStart+"birth_place");
                    break;
                case "birthDate":
                    parsedColumns.add(withStart+"birth_date");
                    break;
                case "fullAddressOfResidence":
                    parsedColumns.add(withStart+"full_address_of_residence");
                    break;
                case "phoneNumber":
                    parsedColumns.add(withStart+"phone_number");
                    break;
                case "knowledgeOfLanguages":
                    parsedColumns.add(withStart+"knowledge_of_languages");
                    break;
                case "countryName":
                    parsedColumns.add("country.name");
                    break;
                case "regionName":
                    parsedColumns.add("region.name");
                    break;
                default:
                    parsedColumns.add(withStart+columnName);
                    break;
            }
        }

        return parsedColumns;
    }

    private List<String> getPersonColumns(){

        List<String>personColumns=personRepository.getPersonColumns();

        if(personColumns==null){

            return null;
        }

        return personColumns;
    }

    private PersonDTO toPersonDTOOnlyGeneral(final Person person){

        if(person==null){

            return null;
        }
        PersonDTO personDTO= PersonDTO.builder()
                .uuid(person.getUuid())
                .name(person.getName())
                .surname(person.getSurname())
                .patronomicName(person.getPatronomicName())
                .birthPlace(person.getBirthPlace())
                .birthDate(person.getBirthDate())
                .countryDTO(person.getCountry()!=null ? person.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(person.getRegion() !=null ? person.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .fullAddressOfResidence(person.getFullAddressOfResidence())
                .phoneNumber(person.getPhoneNumber())
                .fax(person.getFax())
                .email(person.getEmail())
                .education(person.getEducation())
                .experience(person.getExperience())
                .knowledgeOfLanguages(person.getKnowledgeOfLanguages())
                .image(person.getImage()!=null ? person.getImage().toFileDTO() : null)
                .copyPassport(person.getCopyPassport()!=null ? person.getCopyPassport().toFileDTO() : null)
                .build();

        return personDTO;
    }

    @Override
    public ResponseTransfer getPersonDTOByUuid(final UUID personUuid){

        final ResponseTransfer responseTransfer;
        Person person=personRepository.getPersonByUuid(personUuid);

        if(person==null){
            responseTransfer= ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00047")
                    .message("error person not found with this uuid")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            responseTransfer= ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00034")
                    .message("accept person successful returned with this uuid")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(toPersonDTO(person))
                    .build();
        }

        return responseTransfer;
    }

    private PersonDTO toPersonDTO(final Person person) {

        if(person==null){

            return null;
        }
        PersonDTO personDTO= PersonDTO.builder()
                .uuid(person.getUuid())
                .name(person.getName())
                .surname(person.getSurname())
                .patronomicName(person.getPatronomicName())
                .birthPlace(person.getBirthPlace())
                .birthDate(person.getBirthDate())
                .countryDTO(person.getCountry()!=null ? person.getCountry().toCountryDTOOnlyUuidAndName() : null)
                .regionDTO(person.getRegion() !=null ? person.getRegion().toRegionDTOOnlyUuidAndName() : null)
                .fullAddressOfResidence(person.getFullAddressOfResidence())
                .phoneNumber(person.getPhoneNumber())
                .fax(person.getFax())
                .email(person.getEmail())
                .education(person.getEducation())
                .experience(person.getExperience())
                .knowledgeOfLanguages(person.getKnowledgeOfLanguages())
                .image(person.getImage()!=null ? person.getImage().toFileDTO() :  null)
                .copyPassport(person.getCopyPassport()!=null ? person.getCopyPassport().toFileDTO() : null)
                .directorCompanies(person.getCompanies() != null ?
                        person.getCompanies().stream().map(Company::toCompanyDTOOnlyUuidAndName).
                                collect(Collectors.toList()) : null)
                .founderCompanies(person.getFounderCompanies() != null ?
                        person.getFounderCompanies().stream().map(Company::toCompanyDTOOnlyUuidAndName).
                                collect(Collectors.toList()) : null)
                .isEntrepreneur(person.isEntrepreneur())
                .build();

        return personDTO;
    }

    @Override
    public ResponseTransfer getColumnNamePersonById(final int id){

        final ResponseTransfer responseTransfer;
        List<String>personColumns=personRepository.getPersonColumns();

        if(personColumns==null || id<0 || id>=personColumns.size()-3){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00048")
                    .message("error column not found in entity person with this id")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else{
            personColumns.removeAll(Arrays.asList("uuid", "surname", "patronomic_name"));
            int i=-1;

            for(String personColumn:personColumns){
                i++;
                switch (personColumn){
                    case "name":
                        personColumns.set(i, "full_name");
                        break;
                }
            }
            responseTransfer=ResponseTransfer.builder()
                    .status(true)
                    .code("SS-00035")
                    .message("accept column name successful returned with this id")
                    .httpStatus(HttpStatus.ACCEPTED)
                    .data(personColumns.get(id))
                    .build();
        }

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getIsNotEntrepreneurPersonsBySearchKey(String searchKey){

        final ResponseTransfer responseTransfer;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Person>persons=personRepository.getIsNotEntrepreneurPersonsBySearchKey(searchKey);
        List<PersonDTO>personDTOS=new LinkedList<>();

        for(Person person:persons){
            personDTOS.add(toPersonDTOOnlyUuidAndFullName(person));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00061")
                .message("accept all founded don't entrepreneur person successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(personDTOS)
                .build();

        return responseTransfer;
    }

    private PersonDTO toPersonDTOOnlyUuidAndFullName(final Person person) {

        if(person==null){

            return null;
        }
        PersonDTO personDTO=PersonDTO.builder()
                .uuid(person.getUuid())
                .name(person.getName())
                .surname(person.getSurname())
                .patronomicName(person.getPatronomicName())
                .image(person.getImage()!=null ? person.getImage().toFileDTO() : null)
                .build();

        return personDTO;
    }

    @Override
    public ResponseTransfer isPersonsExistsByUuid(UUID... personUuids){

        final ResponseTransfer responseTransfer;
        List<UUID> persons=Arrays.stream(personUuids).filter(Objects::nonNull).collect(Collectors.toList());

        if(persons.size()!=personRepository.getCountPersonsByUuids(persons)){
            responseTransfer=ResponseTransfer.builder()
                    .status(false)
                    .code("SR-00053")
                    .message("error person not found with this uuid")
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
    @Transactional
    public void changeValueIsEntrepreneurPersonByUuid(final UUID personUuid, final boolean isEntrepreneur){

        personRepository.changeValueIsEntrepreneurPersonByUuid(personUuid, isEntrepreneur);

        return;
    }

    @Override
    public ResponseTransfer getPersonDTOSByFullName(String searchKey){

        final ResponseTransfer responseTransfer;

        if(searchKey==null){
            searchKey="";
        }else{
            searchKey=searchKey.toLowerCase(Locale.ROOT);
        }
        List<Person>persons=personRepository.getPersonsByNameOrSurnameOrPatronomicName(searchKey);
        List<PersonDTO>personDTOS=new LinkedList<>();

        if(persons==null){
            persons=new LinkedList<>();
        }
        for(Person person:persons){
            personDTOS.add(toPersonDTOOnlyUuidAndFullName(person));
        }
        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("SS-00062")
                .message("accept all founded person successfulr returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(personDTOS)
                .build();

        return responseTransfer;
    }

}
