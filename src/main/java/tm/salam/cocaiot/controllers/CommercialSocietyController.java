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
import tm.salam.cocaiot.helpers.TypeOwnership;
import tm.salam.cocaiot.models.Company;
import tm.salam.cocaiot.services.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/commercial-society/")
public class CommercialSocietyController {

    private final CompanyService companyService;
    private final TypeActivityService typeActivityService;
    private final LocalizationService localizationService;
    private final PersonService personService;
    private final FileService fileService;
    private final CocaiotMemberService cocaiotMemberService;

    private final TypeOwnership typeOwnership=TypeOwnership.COMMERCIAL_SOCIETY;

    public CommercialSocietyController(CompanyService companyService, TypeActivityService typeActivityService,
                                       LocalizationService localizationService, PersonService personService,
                                       FileService fileService, CocaiotMemberService cocaiotMemberService) {
        this.companyService = companyService;
        this.typeActivityService = typeActivityService;
        this.localizationService = localizationService;
        this.personService = personService;
        this.fileService = fileService;
        this.cocaiotMemberService = cocaiotMemberService;
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

    @GetMapping(path = "/get-countries", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getCountries(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.getCountryDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-regions", params = {"countryUuid","searchKey"}, produces = "application/json")
    public ResponseEntity getRegions(@RequestParam("countryUuid") UUID countryUuid,
                                     @RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.getRegionDTOSByCountryUuidAndName(countryUuid, searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-persons", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getPersons(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=personService.getPersonDTOSByFullName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-company", produces = "application/json")
    public ResponseEntity addCompany(@ModelAttribute Company company,
                                     @RequestParam(value = "founderUuids", required = false)List<UUID> founderUuids,
                                     @RequestParam(value = "typeActivityUuids", required = false) List<UUID> typeActivityUuids,
                                     @RequestParam(value = "countryUuid", required = false)UUID countryUuid,
                                     @RequestParam(value = "regionUuid", required = false)UUID regionUuid,
                                     @RequestParam(value = "directorUuid", required = false)UUID directorUuid,
                                     @RequestParam(value = "logoUuid", required = false)UUID logoUuid,
                                     @RequestParam(value = "membershipApplicationUuid", required = false)
                                             UUID membershipApplicationUuid,
                                     @RequestParam(value = "extractFromUsreoUuid", required = false)
                                             UUID extractFromUsreoUuid,
                                     @RequestParam(value = "charterOfTheEnterpriseUuid", required = false)
                                             UUID charterOfTheEnterpriseUuid,
                                     @RequestParam(value = "certificateOfForeignEconomicRelationsUuid", required = false)
                                             UUID certificateOfForeignEconomicRelationsUuid,
                                     @RequestParam(value = "certificateOfStateRegistrationUuid", required = false)
                                             UUID certificateOfStateRegistrationUuid,
                                     @RequestParam(value = "paymentOfTheEntranceMembershipFeeUuid", required = false)
                                             UUID paymentOfTheEntranceMembershipFeeUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        if(founderUuids==null){
            founderUuids=new LinkedList<>();
        }
        ResponseTransfer responseTransfer=personService.isPersonsExistsByUuid(founderUuids.stream().toArray(UUID[] ::new));
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

        responseTransfer=localizationService.checkTheCorrectnessCountryAndRegion(countryUuid, regionUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=personService.isPersonsExistsByUuid(directorUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=fileService.isFilesExistsByUuids(membershipApplicationUuid, extractFromUsreoUuid,
                charterOfTheEnterpriseUuid, certificateOfForeignEconomicRelationsUuid, certificateOfStateRegistrationUuid,
                paymentOfTheEntranceMembershipFeeUuid, logoUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=companyService.addCompany(company, null, typeActivityUuids, typeOwnership,
                countryUuid, regionUuid, directorUuid, founderUuids, logoUuid, membershipApplicationUuid,
                extractFromUsreoUuid, charterOfTheEnterpriseUuid, certificateOfForeignEconomicRelationsUuid,
                certificateOfStateRegistrationUuid, paymentOfTheEntranceMembershipFeeUuid);
        if(responseTransfer.isStatus()){
            fileService.changeStatusConfirmFilesByUuid(true, membershipApplicationUuid, extractFromUsreoUuid,
                    charterOfTheEnterpriseUuid, certificateOfForeignEconomicRelationsUuid, certificateOfStateRegistrationUuid,
                    paymentOfTheEntranceMembershipFeeUuid, logoUuid);
            cocaiotMemberService.addMember((UUID) responseTransfer.getData(), MemberType.IS_COMPANY, null,
                    null, null);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-company", produces = "application/json")
    public ResponseEntity editCompany(@ModelAttribute Company company,
                                      @RequestParam(value = "founderUuids", required = false)List<UUID> founderUuids,
                                      @RequestParam(value = "typeActivityUuids", required = false) List<UUID> typeActivityUuids,
                                      @RequestParam(value = "countryUuid", required = false)UUID countryUuid,
                                      @RequestParam(value = "regionUuid", required = false)UUID regionUuid,
                                      @RequestParam(value = "directorUuid", required = false)UUID directorUuid,
                                      @RequestParam(value = "logoUuid", required = false)UUID logoUuid,
                                      @RequestParam(value = "membershipApplicationUuid", required = false)
                                              UUID membershipApplicationUuid,
                                      @RequestParam(value = "extractFromUsreoUuid", required = false)
                                              UUID extractFromUsreoUuid,
                                      @RequestParam(value = "charterOfTheEnterpriseUuid", required = false)
                                              UUID charterOfTheEnterpriseUuid,
                                      @RequestParam(value = "certificateOfForeignEconomicRelationsUuid", required = false)
                                              UUID certificateOfForeignEconomicRelationsUuid,
                                      @RequestParam(value = "certificateOfStateRegistrationUuid", required = false)
                                              UUID certificateOfStateRegistrationUuid,
                                      @RequestParam(value = "paymentOfTheEntranceMembershipFeeUuid", required = false)
                                              UUID paymentOfTheEntranceMembershipFeeUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        if(founderUuids==null){
            founderUuids=new LinkedList<>();
        }
        ResponseTransfer responseTransfer=personService.isPersonsExistsByUuid(founderUuids.stream().toArray(UUID[] ::new));
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

        responseTransfer=localizationService.checkTheCorrectnessCountryAndRegion(countryUuid, regionUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=personService.isPersonsExistsByUuid(directorUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=fileService.isFilesExistsByUuids(membershipApplicationUuid, extractFromUsreoUuid,
                charterOfTheEnterpriseUuid, certificateOfForeignEconomicRelationsUuid, certificateOfStateRegistrationUuid,
                paymentOfTheEntranceMembershipFeeUuid, logoUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }

        responseTransfer=companyService.editCompany(company, null, typeActivityUuids, typeOwnership,
                countryUuid, regionUuid, directorUuid, founderUuids, logoUuid, membershipApplicationUuid,
                extractFromUsreoUuid, charterOfTheEnterpriseUuid, certificateOfForeignEconomicRelationsUuid,
                certificateOfStateRegistrationUuid, paymentOfTheEntranceMembershipFeeUuid);
        if(responseTransfer.isStatus()){
            fileService.changeStatusConfirmFilesByUuid(true, logoUuid);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-company", params = {"companyUuid"}, produces = "application/json")
    public ResponseEntity removeCompany(@RequestParam(value = "companyUuid")UUID companyUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=companyService.removeCompanyByUuid(companyUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/companies", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountCompanies(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        int amountEntrepreneurs=companyService.getAmountCompaniesBySearchKey(searchKey, typeOwnership);

        response.put("status",true);
        response.put("code", "SS-00036");
        response.put("message","accept amount persons successful returned");
        response.put("data", amountEntrepreneurs);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/companies", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllCompanies(@RequestParam("page")int page,
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
        final ResponseTransfer responseTransfer= companyService.getAllCompanyDTOS(searchKey, page, size, sortBy,
                sortTypes, typeOwnership);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-company", params = {"companyUuid"}, produces = "application/json")
    public ResponseEntity getCompany(@RequestParam("companyUuid")UUID companyUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=companyService.getCompanyDTOByUuid(companyUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
