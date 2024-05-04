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
import tm.salam.cocaiot.models.Role;
import tm.salam.cocaiot.models.User;
import tm.salam.cocaiot.services.CategoryService;
import tm.salam.cocaiot.services.RoleService;
import tm.salam.cocaiot.services.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final RoleService roleService;
    private final CategoryService categoryService;
    private final UserService userService;

    public SettingController(RoleService roleService, CategoryService categoryService, UserService userService) {
        this.roleService = roleService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping(path = "/get-all/categories", produces = "application/json")
    public ResponseEntity getCategories(){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=categoryService.getAllCategoryDTOS();

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-role", produces = "application/json")
    public ResponseEntity addRole(@Validated @ModelAttribute Role role,
                                  @RequestParam(value = "categoryUuids", required = false) List<UUID> categoryUuids,
                                  @RequestParam(value = "privilages", required = false)List<Boolean>privilages){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=roleService.addRole(role, categoryUuids, privilages);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-role", produces = "application/json")
    public ResponseEntity editRole(@Validated @ModelAttribute Role role,
                                   @RequestParam("categoryUuids")List<UUID>categoryUuids,
                                   @RequestParam("privilages")List<Boolean>privilages){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=roleService.editRole(role, categoryUuids, privilages);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-role", params = {"roleUuid"}, produces = "application/json")
    public ResponseEntity removeRole(@RequestParam("roleUuid")UUID roleUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=roleService.removeRole(roleUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/roles", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountRoles(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountRoles=roleService.getAmountRolesBySearchKey(searchKey);

        response.put("status", true);
        response.put("code", "");
        response.put("message", "");
        response.put("data", amountRoles);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/roles", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllRoles(@RequestParam("page")int page,
                                      @RequestParam("size")int size,
                                      @RequestBody(required = false)String requestBody){

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
        final ResponseTransfer responseTransfer=roleService.getRoleDTOSBySearchKey(searchKey, page, size, sortBy,
                sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-role", params = {"roleUuid"}, produces = "application/json")
    public ResponseEntity getRole(@RequestParam("roleUuid")UUID roleUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=roleService.getRoleDTOByUuid(roleUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-roles", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getRoles(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=roleService.getRoleDTOSByName(searchKey);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/add-user", produces = "application/json")
    public ResponseEntity addUser(@Validated @ModelAttribute User user,
                                  @RequestParam("roleUuid")UUID roleUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        ResponseTransfer responseTransfer=roleService.isRoleExistsByUuid(roleUuid);

        if(!responseTransfer.isStatus()){
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer=userService.addUser(user, roleUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-user", produces = "application/json")
    public ResponseEntity editUser(@Validated @ModelAttribute User user,
                                   @RequestParam("roleUuid")UUID roleUuid) {

        Map<String, Object> response = new LinkedHashMap<>();
        ResponseTransfer responseTransfer = roleService.isRoleExistsByUuid(roleUuid);

        if (!responseTransfer.isStatus()) {
            response.put("status", responseTransfer.isStatus());
            response.put("code", responseTransfer.getCode());
            response.put("message", responseTransfer.getMessage());

            return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
        }
        responseTransfer = userService.editUser(user, roleUuid);
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-user", params = {"userUuid"}, produces = "application/json")
    public ResponseEntity removeUser(@RequestParam("userUuid")UUID userUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=userService.removeUserByUuid(userUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/users", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountUsers(@RequestParam(value = "searchKey", required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountUsers=userService.getAmountUsersBySearchKey(searchKey);

        response.put("status", true);
        response.put("code", "");
        response.put("message", "accept user amount sucessful returned");
        response.put("data", amountUsers);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/get-all/users", params = {"page", "size"},produces = "application/json")
    public ResponseEntity getAllUsers(@RequestParam("page")int page,
                                      @RequestParam("size")int size,
                                      @RequestBody(required = false)String requestBody){

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
        final ResponseTransfer responseTransfer=userService.getUserDTOSBySearchKey(searchKey, page, size, sortBy,
                sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);

    }

    @GetMapping(path = "/get-user", params = {"userUuid"}, produces = "application/json")
    public ResponseEntity getUser(@RequestParam("userUuid")UUID userUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=userService.getUserDTOByUuid(userUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
