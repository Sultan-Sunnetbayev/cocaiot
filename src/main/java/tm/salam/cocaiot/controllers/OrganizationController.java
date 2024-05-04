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
import tm.salam.cocaiot.models.Organization;
import tm.salam.cocaiot.services.OrganizationService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping(path = "/add-organization", produces = "application/json")
    public ResponseEntity addOrganization(@Validated @RequestBody Organization organization){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.addOrganization(organization);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-organization", produces = "application/json")
    public ResponseEntity editOrganization(@Validated @RequestBody Organization organization){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.editOrganization(organization);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-organization", params = {"organizationUuid"}, produces = "application/json")
    public ResponseEntity removeOrganization(@RequestParam("organizationUuid")UUID organizationUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.removeOrganization(organizationUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-all/organizations", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllOrganizationDTOS(@RequestParam("page") int page,
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
        final ResponseTransfer responseTransfer = organizationService.getAllOrganizationDTOS(searchKey, page, size,
                sortBy, sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-organization", params = {"organizationUuid"}, produces = "application/json")
    public ResponseEntity getOrganizationDTOByUuid(@RequestParam("organizationUuid")UUID organizationUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.getOrganizationDTOByUuid(organizationUuid);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/organizations", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountOrganizations(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountOrganizations=organizationService.getAmountOrganizationsBySearchKey(searchKey);

        response.put("status",true);
        response.put("code","SS-00029");
        response.put("message","accept amount organizations successful returned");
        response.put("data",amountOrganizations);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-column/organization", params = {"id"}, produces = "application/json")
    public ResponseEntity getColumnOrganization(@RequestParam("id")int id){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=organizationService.getColumnNameOrganizationById(id);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
