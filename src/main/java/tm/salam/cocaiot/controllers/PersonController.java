package tm.salam.cocaiot.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.Person;
import tm.salam.cocaiot.services.FileService;
import tm.salam.cocaiot.services.LocalizationService;
import tm.salam.cocaiot.services.PersonService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService personService;
    private final LocalizationService localizationService;
    private final FileService fileService;

    public PersonController(PersonService personService, LocalizationService localizationService,
                            FileService fileService) {
        this.personService = personService;
        this.localizationService = localizationService;
        this.fileService = fileService;
    }

    @GetMapping(path = "/get-countries", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getCountries(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.getCountryDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-regions", params = {"countryUuid","searchKey"}, produces = "application/json")
    public ResponseEntity getRegions(@RequestParam("countryUuid")UUID countryUuid,
                                     @RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.getRegionDTOSByCountryUuidAndName(countryUuid, searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-person", produces = "application/json")
    public ResponseEntity addPerson(@RequestParam(value = "countryUuid", required = false)UUID countryUuid,
                                    @RequestParam(value = "regionUuid", required = false)UUID regionUuid,
                                    @ModelAttribute Person person,
                                    @RequestParam(required = false, value = "imageUuid")UUID imageUuid,
                                    @RequestParam(required = false, value = "copyPassportUuid")UUID copyPassportUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        ResponseTransfer responseTransfer=localizationService.checkTheCorrectnessCountryAndRegion(countryUuid, regionUuid);

        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=fileService.isFilesExistsByUuids(imageUuid, copyPassportUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=personService.addPerson(person, imageUuid, copyPassportUuid, countryUuid, regionUuid);
        if(responseTransfer.isStatus()){
            fileService.changeStatusConfirmFilesByUuid(true, imageUuid, copyPassportUuid);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-person", produces = "application/json")
    public ResponseEntity editPerson(@RequestParam(value = "countryUuid", required = false)UUID countryUuid,
                                     @RequestParam(value = "regionUuid", required = false)UUID regionUuid,
                                     @ModelAttribute Person person,
                                     @RequestParam(required = false, value = "imageUuid")UUID imageUuid,
                                     @RequestParam(required = false, value = "copyPassportUuid")UUID copyPassportUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        ResponseTransfer responseTransfer=localizationService.checkTheCorrectnessCountryAndRegion(countryUuid, regionUuid);

        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=fileService.isFilesExistsByUuids(imageUuid, copyPassportUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=personService.editPerson(person, imageUuid, copyPassportUuid, countryUuid, regionUuid);
        if(responseTransfer.isStatus()){
            fileService.changeStatusConfirmFilesByUuid(true, imageUuid, copyPassportUuid);
        }
        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-person", params = {"personUuid"}, produces = "application/json")
    public ResponseEntity removePerson(@RequestParam("personUuid")UUID personUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=personService.removePerson(personUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/persons", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountPersons(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        int amountPerson=personService.getAmountPersonsBySearchKey(searchKey);

        response.put("status",true);
        response.put("code", "SS-00036");
        response.put("message","accept amount persons successful returned");
        response.put("data", amountPerson);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/persons", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllPersons(@RequestParam("page")int page,
                                        @RequestParam("size")int size,
                                        @RequestBody(required = false) String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        String searchKey=null;
        List<String> sortBy=null;
        List<SortType>sortTypes=null;

        try {
            final JSONObject jsonObject;
            if(requestBody==null){
                jsonObject=new JSONObject();
            }else {
                jsonObject = new JSONObject(requestBody);
            }
            if(jsonObject.has("searchKey")){
                searchKey=jsonObject.getString("searchKey");
            }
            if(jsonObject.has("sort")) {
                sortBy=new LinkedList<>();
                sortTypes=new LinkedList<>();
                final JSONArray jsonArray=jsonObject.getJSONArray("sort");
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject sort=jsonArray.getJSONObject(i);
                    sortBy.add(sort.getString("sortBy"));
                    sortTypes.add(SortType.valueOf(sort.getString("sortType")));
                }
            }
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
            response.put("status",false);
            response.put("code","SR-00006");
            response.put("message",jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
            response.put("status", false);
            response.put("code", "SR-00007");
            response.put("message", illegalArgumentException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        final ResponseTransfer responseTransfer= personService.getAllPersonDTOS(searchKey, page, size, sortBy, sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-person", params = {"personUuid"}, produces = "application/json")
    public ResponseEntity getPerson(@RequestParam("personUuid")UUID personUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=personService.getPersonDTOByUuid(personUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-column/person", params = {"id"}, produces = "application/json")
    public ResponseEntity getColumnPerson(@RequestParam("id")int id){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=personService.getColumnNamePersonById(id);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
