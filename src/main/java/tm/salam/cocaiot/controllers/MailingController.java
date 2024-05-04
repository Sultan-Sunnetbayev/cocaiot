package tm.salam.cocaiot.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.dtoes.CompanyDTO;
import tm.salam.cocaiot.dtoes.EntrepreneurDTO;
import tm.salam.cocaiot.dtoes.FileDTO;
import tm.salam.cocaiot.helpers.*;
import tm.salam.cocaiot.models.Mailing;
import tm.salam.cocaiot.services.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/mailing")
public class MailingController {

    private final MailingService mailingService;
    private final CompanyService companyService;
    private final FileService fileService;
    private final TypeActivityService typeActivityService;
    private final EmailService emailService;
    private final EntrepreneurService entrepreneurService;
//    private final MailingCompanyService mailingCompanyService;

    public MailingController(MailingService mailingService, CompanyService companyService, FileService fileService,
                             TypeActivityService typeActivityService, EmailService emailService,
                             EntrepreneurService entrepreneurService) {
        this.mailingService = mailingService;
        this.companyService = companyService;
        this.fileService = fileService;
        this.typeActivityService = typeActivityService;
        this.emailService = emailService;
        this.entrepreneurService = entrepreneurService;
    }

    @PostMapping(path = "/get-activities", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getActivities(@RequestParam(value = "searchKey", required = false)String searchKey,
                                        @RequestParam(value = "companyUuids", required = false)List<UUID>companyUuids,
                                        @RequestParam(value = "entrepreneurUuids", required = false)List<UUID>entrepreneurUuids){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer;

        if(companyUuids==null || companyUuids.isEmpty()) {
            responseTransfer = typeActivityService.getTypeActivityDTOSByName(searchKey);
        }else{
            responseTransfer=typeActivityService.getTypeActivityDTOSByCompanyUuids(companyUuids);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-recipients", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getCompanies(@RequestParam(value = "searchKey", required = false)String searchKey,
                                       @RequestParam(value = "typeActivityUuids", required = false) List<UUID> typeActivityUuids){

        Map<String, Object>response=new LinkedHashMap<>();
        List<TemporalMailingRecipients>mailingRecipients=new LinkedList<>();
        List<CompanyDTO>companyDTOS;
        List<EntrepreneurDTO>entrepreneurDTOS;

        if(typeActivityUuids==null || typeActivityUuids.isEmpty()){
            companyDTOS=companyService.getCompanyDTOSByName(searchKey);
            entrepreneurDTOS = entrepreneurService.getEntrepreneurDTOSByFullName(searchKey);
        }else{
            companyDTOS=companyService.getCompanyDTOSByTypeActivityUuids(typeActivityUuids);
            entrepreneurDTOS=entrepreneurService.getEntrepreneurDTOSByTypeActivityUuids(typeActivityUuids);
        }
        for(CompanyDTO companyDTO:companyDTOS){
            mailingRecipients.add(TemporalMailingRecipients.builder()
                    .uuid(companyDTO.getUuid())
                    .recipientName(companyDTO.getName())
                    .memberType(MemberType.IS_COMPANY)
                    .build());
        }
        for(EntrepreneurDTO entrepreneurDTO:entrepreneurDTOS){
            mailingRecipients.add(TemporalMailingRecipients.builder()
                    .uuid(entrepreneurDTO.getUuid())
                    .recipientName(entrepreneurDTO.getPersonDTO().getPersonDTOFullName())
                    .memberType(MemberType.IS_ENTREPRENEUR)
                    .build());
        }
        response.put("status", true);
        response.put("code", "");
        response.put("message", "accept recipients successful returned");
        response.put("data", mailingRecipients);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/send-mailing", produces = "application/json")
    public ResponseEntity sendMailing(@Validated @ModelAttribute Mailing mailing,
                                      @RequestParam(value = "fileUuid", required = false)UUID fileUuid,
                                      @RequestParam(value = "companyUuids", required = false)List<UUID>companyUuids,
                                      @RequestParam(value = "entrepreneurUuids", required = false)List<UUID>entrepreneurUuids){

        Map<String, Object>response=new LinkedHashMap<>();
        ResponseTransfer responseTransfer;
        FileDTO fileDTO=null;

        if(fileUuid!=null && mailing.getTypeMailing().equals(TypeMailing.EMAIL)){
            responseTransfer=fileService.getFileDTOByUuid(fileUuid);
            if(!responseTransfer.isStatus()){
                response.put("status", responseTransfer.isStatus());
                response.put("code", responseTransfer.getCode());
                response.put("message", responseTransfer.getMessage());

                return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
            }
            fileDTO=(FileDTO) responseTransfer.getData();
        }
        if(companyUuids!=null) {
            responseTransfer = companyService.isCompaniesExistsByUuids(companyUuids);
            if (!responseTransfer.isStatus()) {
                response.put("status", responseTransfer.isStatus());
                response.put("code", responseTransfer.getCode());
                response.put("message", responseTransfer.getMessage());

                return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
            }
        }
        if(entrepreneurUuids!=null){
            responseTransfer=entrepreneurService.isEntrepreneursExistsByUuids(entrepreneurUuids);
            if (!responseTransfer.isStatus()) {
                response.put("status", responseTransfer.isStatus());
                response.put("code", responseTransfer.getCode());
                response.put("message", responseTransfer.getMessage());

                return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
            }
        }
        switch (mailing.getTypeMailing()) {
            case EMAIL:
                List<String> emails = companyService.getCompanyEmailsByUuids(companyUuids);

                emails.addAll(entrepreneurService.getEntrepreneurEmailsByUuids(entrepreneurUuids));
                responseTransfer=emailService.sendMessageWithAttachment(mailing, fileDTO, emails);
                if(!responseTransfer.isStatus()){
                    break;
                }
                responseTransfer = mailingService.addMailing(mailing, fileUuid, companyUuids, entrepreneurUuids);
                if(responseTransfer.isStatus()){
                    fileService.changeStatusConfirmFilesByUuid(true, fileUuid);
                }
                break;
            default:
                responseTransfer= ResponseTransfer.builder()
                        .status(false)
                        .code("SR-00072")
                        .message("error with type send mailing")
                        .httpStatus(HttpStatus.FAILED_DEPENDENCY)
                        .build();
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-mailing", params = {"mailingCompanyUuid"}, produces = "application/json")
    public ResponseEntity removeMailing(@RequestParam("mailingCompanyUuid")UUID mailingUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=mailingService.removeMailingByUuid(mailingUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/mailings", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountMailings(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountMailings=mailingService.getAmountMailingsBySearchKey(searchKey);

        response.put("status", true);
        response.put("code", "");
        response.put("message", "accept amount mailings successful returned by searchKey");
        response.put("data", amountMailings);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/mailings", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllMailings(@RequestParam("page")int page,
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
        final ResponseTransfer responseTransfer=mailingService.getMailingDTOSBySearchKey(searchKey, page, size, sortBy,
                sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-mailing", params = {"mailingUuid"}, produces = "application/json")
    public ResponseEntity getMailingByUuid(@RequestParam("mailingUuid")UUID mailingUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=mailingService.getMailingDTOByUuid(mailingUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}