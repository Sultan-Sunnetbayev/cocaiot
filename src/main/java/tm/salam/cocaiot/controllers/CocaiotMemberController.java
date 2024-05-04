package tm.salam.cocaiot.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.dtoes.CocaiotMemberDTO;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.helpers.StatusPayment;
import tm.salam.cocaiot.helpers.MemberType;
import tm.salam.cocaiot.services.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/cocaiot-member")
public class CocaiotMemberController {

    private final CocaiotMemberService cocaiotMemberService;
    private final CompanyService companyService;
    private final EntrepreneurService entrepreneurService;
    private final FileService fileService;
    private final LocalizationService localizationService;
    private final TypeActivityService typeActivityService;
    private final OrganizationService organizationService;

    public CocaiotMemberController(CocaiotMemberService cocaiotMemberService, CompanyService companyService,
                                   EntrepreneurService entrepreneurService, FileService fileService,
                                   LocalizationService localizationService, TypeActivityService typeActivityService,
                                   OrganizationService organizationService) {
        this.cocaiotMemberService = cocaiotMemberService;
        this.companyService = companyService;
        this.entrepreneurService = entrepreneurService;
        this.fileService = fileService;
        this.localizationService = localizationService;
        this.typeActivityService = typeActivityService;
        this.organizationService = organizationService;
    }

    @GetMapping(path = "/get-candidates", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getCandidates(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object> response=new LinkedHashMap<>();
        final List<CompanyDTO> companyDTOS=companyService.getIsNotCocaiotMemberCompanyDTOSByName(searchKey);
        final List<EntrepreneurDTO>entrepreneurDTOS=entrepreneurService.getIsNotCocaiotMemberEntrepreneurDTOSByFullName(searchKey);
        List<CocaiotMemberDTO>cocaiotMemberDTOS=new LinkedList<>();

        for(CompanyDTO companyDTO:companyDTOS){
            cocaiotMemberDTOS.add(CocaiotMemberDTO.builder()
                    .companyDTO(companyDTO)
                    .typeCocaiotMember(MemberType.IS_COMPANY)
                    .build());
        }
        for(EntrepreneurDTO entrepreneurDTO:entrepreneurDTOS){
            cocaiotMemberDTOS.add(CocaiotMemberDTO.builder()
                    .entrepreneurDTO(entrepreneurDTO)
                    .typeCocaiotMember(MemberType.IS_ENTREPRENEUR)
                    .build());
        }
        response.put("status", true);
        response.put("code", "SS-00063");
        response.put("message", "accept cocaiot member candidates successful returned");
        response.put("data", cocaiotMemberDTOS);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-candidate", produces = "application/json")
    public ResponseEntity getCandidateByUuid(@RequestBody String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer;
        final MemberType memberType;
        CocaiotMemberDTO cocaiotMemberDTO=null;

        try {
            final JSONObject jsonObject=new JSONObject(requestBody);
            final UUID candidateUuid=UUID.fromString(jsonObject.getString("candidateUuid"));
            memberType = MemberType.valueOf(jsonObject.getString("typeCocaiotMember"));
            if(Objects.equals(MemberType.IS_COMPANY, memberType)){
                responseTransfer=companyService.getCompanyDTOOnlyGeneralByUuid(candidateUuid);
                if(responseTransfer.isStatus()){
                    cocaiotMemberDTO=CocaiotMemberDTO.builder()
                            .companyDTO((CompanyDTO) responseTransfer.getData())
                            .typeCocaiotMember(MemberType.IS_COMPANY)
                            .build();
                }
            }else {
                responseTransfer = entrepreneurService.getEntrepreneurDTOOnlyGeneralByUuid(candidateUuid);
                if (responseTransfer.isStatus()) {
                    cocaiotMemberDTO = CocaiotMemberDTO.builder()
                            .entrepreneurDTO((EntrepreneurDTO) responseTransfer.getData())
                            .typeCocaiotMember(MemberType.IS_ENTREPRENEUR)
                            .build();
                }
            }
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
            response.put("status", false);
            response.put("code", "SR-00006");
            response.put("message", jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (IllegalArgumentException illegalArgumentException){
            response.put("status", false);
            response.put("code", "SR-00007");
            response.put("message", illegalArgumentException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", cocaiotMemberDTO);

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-member", produces = "application/json")
    public ResponseEntity addMember(@RequestBody String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        final UUID memberUuid, fileUuid;
        final MemberType memberType;
        final Date initialDate, finalDate;

        try {
            JSONObject jsonObject=new JSONObject(requestBody);
            memberUuid=UUID.fromString(jsonObject.getString("memberUuid"));
            memberType = MemberType.valueOf(jsonObject.getString("typeCocaiotMember"));
            if(jsonObject.has("fileUuid")){
                fileUuid=UUID.fromString(jsonObject.getString("fileUuid"));
            }else{
                fileUuid=null;
            }
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            initialDate=new Date(simpleDateFormat.parse(jsonObject.getString("initialDate")).getTime());
            finalDate=new Date(simpleDateFormat.parse(jsonObject.getString("finalDate")).getTime());

            if(initialDate.after(finalDate)){
                throw new JSONException("error wrong initial date after final date");
            }
        }catch (JSONException jsonException){
            response.put("status", false);
            response.put("code", "SR-00006");
            response.put("message", jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (IllegalArgumentException illegalArgumentException){
            response.put("status", false);
            response.put("code", "SR-00007");
            response.put("message", illegalArgumentException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (ParseException parseException){
            response.put("status", false);
            response.put("code", "SR-00065");
            response.put("message", parseException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ResponseTransfer responseTransfer=fileService.isFilesExistsByUuids(fileUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        switch (memberType){
            case IS_COMPANY:
                responseTransfer=companyService.isCompanyExistsByUuid(memberUuid);
                break;
            case IS_ENTREPRENEUR:
                responseTransfer=entrepreneurService.isEntrepreneurExistsByUuid(memberUuid);
                break;
        }
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=cocaiotMemberService.addMember(memberUuid, memberType, fileUuid, initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-member", params = {"memberUuid"}, produces = "application/json")
    public ResponseEntity editMember(@RequestParam("memberUuid")UUID memberUuid,
                                     @RequestBody String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        final UUID newMemberUuid, fileUuid;
        final MemberType memberType;
        final Date initialDate, finalDate;

        try {
            JSONObject jsonObject=new JSONObject(requestBody);
            newMemberUuid=UUID.fromString(jsonObject.getString("newMemberUuid"));
            memberType = MemberType.valueOf(jsonObject.getString("typeCocaiotMember"));
            fileUuid=UUID.fromString(jsonObject.getString("fileUuid"));
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            initialDate=new Date(simpleDateFormat.parse(jsonObject.getString("initialDate")).getTime());
            finalDate=new Date(simpleDateFormat.parse(jsonObject.getString("finalDate")).getTime());
        }catch (JSONException jsonException){
            response.put("status", false);
            response.put("code", "SR-00006");
            response.put("message", jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (IllegalArgumentException illegalArgumentException){
            response.put("status", false);
            response.put("code", "SR-00007");
            response.put("message", illegalArgumentException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (ParseException parseException){
            response.put("status", false);
            response.put("code", "SR-00065");
            response.put("message", parseException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ResponseTransfer responseTransfer=fileService.isFilesExistsByUuids(fileUuid);
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        switch (memberType){
            case IS_COMPANY:
                responseTransfer=companyService.isCompanyExistsByUuid(newMemberUuid);
                break;
            case IS_ENTREPRENEUR:
                responseTransfer=entrepreneurService.isEntrepreneurExistsByUuid(newMemberUuid);
                break;
        }
        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=cocaiotMemberService.editMember(memberUuid, newMemberUuid, memberType, fileUuid,
                initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-member", params = {"memberUuid"}, produces = "application/json")
    public ResponseEntity removeMember(@RequestParam("memberUuid")UUID memberUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=cocaiotMemberService.removeMemberByUuid(memberUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-activities", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getActivities(@RequestParam(value = "searchKey", required = false)String searchKey){

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

    @GetMapping(path = "/get-regions", params = {"countryUuid", "searchKey"}, produces = "application/json")
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

    @GetMapping(path = "/get-organizations", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getOrganizations(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.getOrganizationDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-status/payments", produces = "application/json")
    public ResponseEntity getStatusPayments(){

        Map<String, Object>response=new LinkedHashMap<>();
        List<StatusPayment>statusPayments=new LinkedList<>();

        for(StatusPayment statusPayment:StatusPayment.values()){
            statusPayments.add(statusPayment);
        }
        response.put("status", true);
        response.put("code", "");
        response.put("message", "");
        response.put("data", statusPayments);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping(path = "/get-member/companies", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getMemberCompanies(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=companyService.getCocaiotMemberCompanyDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/members", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountMembers(@RequestParam("searchKey")String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
//        final TypeCocaiotMember typeCocaiotMember;
//
//        try {
//            JSONObject jsonObject=new JSONObject(requestBody);
//
//            if(jsonObject.has("searchKey")) {
//                searchKey = jsonObject.getString("searchKey");
//            }
//            typeCocaiotMember=TypeCocaiotMember.valueOf(jsonObject.getString("typeCocaiotMember"));
//        }catch (JSONException jsonException){
//            jsonException.printStackTrace();
//            response.put("status", false);
//            response.put("code", "SR-00006");
//            response.put("message", jsonException.getMessage());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }catch (IllegalArgumentException illegalArgumentException){
//            illegalArgumentException.printStackTrace();
//            response.put("status", false);
//            response.put("code", "SR-00007");
//            response.put("message", illegalArgumentException.getMessage());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
        final int amountMembers=cocaiotMemberService.getAmountMembersBySearchKey(searchKey, null);

        response.put("status", true);
        response.put("code", "SS-00064");
        response.put("message", "accept amount member by search key successful returned");
        response.put("data", amountMembers);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-members", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getMembers(@RequestParam("page")int page,
                                     @RequestParam("size")int size,
                                     @RequestBody(required = false) String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        String searchKey=null;
        List<String> sortBy=null;
        List<SortType>sortTypes=null;
        MemberType memberType =null;

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
//            typeCocaiotMember=TypeCocaiotMember.valueOf(jsonObject.getString("typeCocaiotMember"));
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
//        if(sortBy!=null) {
//            switch (typeCocaiotMember) {
//                case IS_COMPANY:
//                    sortBy = companyService.parseCompanyColumns("company.", sortBy);
//                    if (!companyService.isCompanyColumnsExists("company.", sortBy)) {
//                        response.put("status", false);
//                        response.put("code", "SR-00061");
//                        response.put("message", "error column not found in entity company");
//
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//                    }
//                    break;
//                case IS_ENTREPRENEUR:
//                    sortBy = personService.parsePersonColumns("person.", sortBy);
//                    if (!personService.isPersonColumnsExists("person.",sortBy)) {
//                        response.put("status", false);
//                        response.put("code", "SR-00046");
//                        response.put("message", "error column not found in entity person");
//
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//                    }
//                    break;
//            }
//        }
        final ResponseTransfer responseTransfer=cocaiotMemberService.getAllCocaiotMemberDTOSBySearchKey(searchKey, page,
                size, sortBy, sortTypes, memberType);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-member", params = {"memberUuid"}, produces = "application/json")
    public ResponseEntity getMember(@RequestParam("memberUuid")UUID memberUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=cocaiotMemberService.getCocaiotMemberDTOByUuid(memberUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
