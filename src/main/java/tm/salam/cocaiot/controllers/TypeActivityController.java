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
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.SortType;
import tm.salam.cocaiot.models.TypeActivity;
import tm.salam.cocaiot.services.CompanyService;
import tm.salam.cocaiot.services.EntrepreneurService;
import tm.salam.cocaiot.services.TypeActivityService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/type_activity")
public class TypeActivityController {

    private final TypeActivityService typeActivityService;
    private final CompanyService companyService;
    private final EntrepreneurService entrepreneurService;

    public TypeActivityController(TypeActivityService typeActivityService, CompanyService companyService,
                                  EntrepreneurService entrepreneurService) {
        this.typeActivityService = typeActivityService;
        this.companyService = companyService;
        this.entrepreneurService = entrepreneurService;
    }

    @PostMapping(path = "/add-activity", produces = "application/json")
    public ResponseEntity addTypeActivity(@Validated @RequestBody TypeActivity typeActivity) {

        Map<String, Object> response = new LinkedHashMap<>();
        final ResponseTransfer responseTransfer = typeActivityService.addTypeActivity(typeActivity);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-activity", produces = "application/json")
    public ResponseEntity editActivityByUuid(@Validated @RequestBody TypeActivity typeActivity) {

        Map<String, Object> response = new LinkedHashMap<>();
        final ResponseTransfer responseTransfer = typeActivityService.editTypeActivityByUuid(typeActivity);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-activity", params = {"activityUuid"}, produces = "application/json")
    public ResponseEntity removeActivityByUuid(@RequestParam("activityUuid") UUID typeActivityUuid) {

        Map<String, Object> response = new LinkedHashMap<>();
        final ResponseTransfer responseTransfer = typeActivityService.removeTypeActivityByUuid(typeActivityUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-all/activity", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllTypeActivityDTOS(@RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestBody(required = false) String requestBody) {

        Map<String, Object> response = new LinkedHashMap<>();
        String searchKey = null;
        List<String> sortBy = null;
        List<SortType> sortTypes = null;

        try {
            final JSONObject jsonObject;
            if (requestBody == null) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(requestBody);
            }
            if (jsonObject.has("searchKey")) {
                searchKey = jsonObject.getString("searchKey");
            }
            if (jsonObject.has("sort")) {
                sortBy = new LinkedList<>();
                sortTypes = new LinkedList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("sort");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject sort = jsonArray.getJSONObject(i);
                    sortBy.add(sort.getString("sortBy"));
                    sortTypes.add(SortType.valueOf(sort.getString("sortType")));
                }
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            response.put("status", false);
            response.put("code", "SR-00006");
            response.put("message", jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
            response.put("status", false);
            response.put("code", "SR-00007");
            response.put("message", illegalArgumentException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        final ResponseTransfer responseTransfer = typeActivityService.getAllTypeActivityDTOS(searchKey, page, size,
                sortBy, sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-activity", params = {"activityUuid"}, produces = "application/json")
    public ResponseEntity getActivity(@RequestParam("activityUuid") UUID typeActivityUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=typeActivityService.getTypeActivityDTOByUuid(typeActivityUuid);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/activity", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountTypeActivity(@RequestParam(value = "searchKey", required = false) String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountTypeActivity=typeActivityService.getAmountTypeActivityBySearchKey(searchKey);

        response.put("status",true);
        response.put("code","SS-00006");
        response.put("message","accept amount type activity successful returned");
        response.put("data",amountTypeActivity);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-column/activity", params = {"id"}, produces = "application/json")
    public ResponseEntity getColumnTypeActivity(@RequestParam("id")int id){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=typeActivityService.getColumnNameTypeActivityById(id);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-members", params = {"typeActivityUuid"}, produces = "application/json")
    public ResponseEntity<?>get(@RequestParam("typeActivityUuid")UUID typeActivityUuid){

        ResponseTransfer<Map<String, Object>>responseTransfer;
        ResponseTransfer<?>membercompanies=companyService.getCompanyNamesByTypeActivityUuid(typeActivityUuid);
        ResponseTransfer<?>memberentrepreneurs=entrepreneurService.getEntrepreneurNamesByTypeActivityUuid(typeActivityUuid);
        Map<String, Object>data=new HashMap<>();

        data.put("companies", membercompanies.getData());
        data.put("entrepreneurs", memberentrepreneurs.getData());
        responseTransfer=ResponseTransfer.<Map<String, Object>>builder()
                .status(true)
                .code("")
                .message("members successful returned")
                .data(data)
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseTransfer);
    }

}
