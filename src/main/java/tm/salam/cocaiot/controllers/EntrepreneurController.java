package tm.salam.cocaiot.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.helpers.MemberType;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.services.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/entrepreneur")
public class EntrepreneurController {

    private final EntrepreneurService entrepreneurService;
    private final PersonService personService;
    private final FileService fileService;
    private final TypeActivityService typeActivityService;
    private final CocaiotMemberService cocaiotMemberService;

    public EntrepreneurController(EntrepreneurService entrepreneurService, PersonService personService,
                                  FileService fileService, TypeActivityService typeActivityService,
                                  CocaiotMemberService cocaiotMemberService) {
        this.entrepreneurService = entrepreneurService;
        this.personService = personService;
        this.fileService = fileService;
        this.typeActivityService = typeActivityService;
        this.cocaiotMemberService = cocaiotMemberService;
    }

    @GetMapping(path = "/get-persons", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getPersons(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=personService.getIsNotEntrepreneurPersonsBySearchKey(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-activities", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getTypeActivity(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=typeActivityService.getTypeActivityDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-entrepreneur", produces = "application/json")
    public ResponseEntity addEntrepreneur(@RequestParam("personUuid")UUID personUuid,
                                          @RequestParam(required = false, value = "webSite")String webSite,
                                          @RequestParam(required = false, value = "typeWork")String typeWork,
                                          @RequestParam("typeActivityUuids")List<UUID>typeActivityUuids,
                                          @RequestParam(required = false, value = "logoUuid")UUID logoUuid,
                                          @RequestParam(value = "membershipApplicationUuid", required = false)
                                                  UUID membershipApplicationUuid,
                                          @RequestParam(value = "patentCertifyingPaymentUuid", required = false)
                                                      UUID patentCertifyingPaymentUuid,
                                          @RequestParam(value = "entrepreneurStatisticalCodesUuid", required = false)
                                                      UUID entrepreneurStatisticalCodesUuid,
                                          @RequestParam(value = "certificateOfForeignEconomicRelationsUuid", required = false)
                                                      UUID certificateOfForeignEconomicRelationsUuid,
                                          @RequestParam(value = "registrationCertificateOfEntrepreneurUuid", required = false)
                                                      UUID registrationCertificateOfEntrepreneurUuid,
                                          @RequestParam(value = "certificateOfTaxRegistrationUuid", required = false)
                                                      UUID certificateOfTaxRegistrationUuid){

        Map<String, Object> response=new LinkedHashMap<>();

        ResponseTransfer responseTransfer=personService.isPersonsExistsByUuid(personUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=typeActivityService.isTypeActivitiesExistsByUuids(typeActivityUuids);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=fileService.isFilesExistsByUuids(membershipApplicationUuid, patentCertifyingPaymentUuid,
                entrepreneurStatisticalCodesUuid, certificateOfForeignEconomicRelationsUuid,
                registrationCertificateOfEntrepreneurUuid, certificateOfTaxRegistrationUuid, logoUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=entrepreneurService.addEntrepreneur(personUuid, webSite, typeWork, typeActivityUuids, logoUuid,
                membershipApplicationUuid, patentCertifyingPaymentUuid, entrepreneurStatisticalCodesUuid,
                certificateOfForeignEconomicRelationsUuid, registrationCertificateOfEntrepreneurUuid,
                certificateOfTaxRegistrationUuid);

        if(responseTransfer.isStatus()){
            personService.changeValueIsEntrepreneurPersonByUuid(personUuid, true);
            fileService.changeStatusConfirmFilesByUuid(true, membershipApplicationUuid,
                    patentCertifyingPaymentUuid, entrepreneurStatisticalCodesUuid,
                    certificateOfForeignEconomicRelationsUuid, registrationCertificateOfEntrepreneurUuid,
                    certificateOfTaxRegistrationUuid, logoUuid);
            typeActivityService.incrementAmountCompanyByTypeActivityUuids(typeActivityUuids, 1);
            cocaiotMemberService.addMember((UUID) responseTransfer.getData(), MemberType.IS_ENTREPRENEUR, null,
                    null, null);
        }

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return  ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-entrepreneur", produces = "application/json")
    ResponseEntity editEntrepreneur(@RequestParam("entrepreneurUuid")UUID entrepreneurUuid,
                                    @RequestParam(value = "personUuid")UUID personUuid,
                                    @RequestParam(required = false, value = "webSite")String webSite,
                                    @RequestParam(required = false, value = "typeWork")String typeWork,
                                    @RequestParam("typeActivityUuids")List<UUID>typeActivityUuids,
                                    @RequestParam(required = false, value = "logoUuid")UUID logoUuid,
                                    @RequestParam(value = "membershipApplicationUuid", required = false)
                                            UUID membershipApplicationUuid,
                                    @RequestParam(value = "patentCertifyingPaymentUuid", required = false)
                                            UUID patentCertifyingPaymentUuid,
                                    @RequestParam(value = "entrepreneurStatisticalCodesUuid", required = false)
                                            UUID entrepreneurStatisticalCodesUuid,
                                    @RequestParam(value = "certificateOfForeignEconomicRelationsUuid", required = false)
                                            UUID certificateOfForeignEconomicRelationsUuid,
                                    @RequestParam(value = "registrationCertificateOfEntrepreneurUuid", required = false)
                                            UUID registrationCertificateOfEntrepreneurUuid,
                                    @RequestParam(value = "certificateOfTaxRegistrationUuid", required = false)
                                            UUID certificateOfTaxRegistrationUuid){

        Map<String, Object>response=new LinkedHashMap<>();

        ResponseTransfer responseTransfer=personService.isPersonsExistsByUuid(personUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=typeActivityService.isTypeActivitiesExistsByUuids(typeActivityUuids);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=fileService.isFilesExistsByUuids(membershipApplicationUuid, patentCertifyingPaymentUuid,
                entrepreneurStatisticalCodesUuid, certificateOfForeignEconomicRelationsUuid,
                registrationCertificateOfEntrepreneurUuid, certificateOfTaxRegistrationUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=entrepreneurService.editEntrepreneur(entrepreneurUuid, personUuid, webSite, typeWork,
                typeActivityUuids, logoUuid, membershipApplicationUuid, patentCertifyingPaymentUuid,
                entrepreneurStatisticalCodesUuid, certificateOfForeignEconomicRelationsUuid,
                registrationCertificateOfEntrepreneurUuid, certificateOfTaxRegistrationUuid);
        if(responseTransfer.isStatus()){
            typeActivityService.incrementAmountCompanyByTypeActivityUuids(typeActivityUuids, 1);
            fileService.changeStatusConfirmFilesByUuid(true, logoUuid);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/entrepreneurs", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountEntreprenurs(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        int amountEntrepreneurs=entrepreneurService.getAmountEntrepreneursBySearchKey(searchKey);

        response.put("status",true);
        response.put("code", "SS-00036");
        response.put("message","accept amount persons successful returned");
        response.put("data", amountEntrepreneurs);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/entrepreneurs", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllEntrepreneurs(@RequestParam("page")int page,
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
        final ResponseTransfer responseTransfer= entrepreneurService.getAllEntrepreneurDTOS(searchKey, page, size,
                sortBy, sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-entrepreneur", params = {"entrepreneurUuid"}, produces = "application/json")
    public ResponseEntity getEntrepreneur(@RequestParam("entrepreneurUuid")UUID entrepreneurUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=entrepreneurService.getEntrepreneurDTOByUuid(entrepreneurUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-entrepreneur", params = {"entrepreneurUuid"}, produces = "application/json")
    public ResponseEntity<?> removeEntrepreneur(@RequestParam("entrepreneurUuid")UUID entrepreneurUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer<?> responseTransfer=entrepreneurService.removeEntrepreneurByUuid(entrepreneurUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
