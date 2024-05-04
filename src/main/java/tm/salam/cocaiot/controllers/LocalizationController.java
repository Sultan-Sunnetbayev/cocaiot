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
import tm.salam.cocaiot.models.Country;
import tm.salam.cocaiot.models.Region;
import tm.salam.cocaiot.services.LocalizationService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/localization")
public class LocalizationController {

    private final LocalizationService localizationService;

    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @PostMapping(path = "/add-country", produces = "application/json")
    public ResponseEntity addCountry(@Validated @RequestBody Country country){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.addCountry(country);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-country", produces = "application/json")
    public ResponseEntity editCountry(@Validated @RequestBody Country country){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.editCountryByUuid(country);

        response.put("status",responseTransfer.isStatus());
        response.put("code",responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-country", params = {"countryUuid"}, produces = "application/json")
    public ResponseEntity removeCountry(@RequestParam("countryUuid")UUID countryUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.removeCountryByUuid(countryUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-all/countries", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getAllCountries(@RequestParam("page")int page,
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
        final ResponseTransfer responseTransfer= localizationService.getAllCountryDTOS(searchKey, page, size,
                sortBy, sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-country", params = {"countryUuid"}, produces = "application/json")
    public ResponseEntity getCountry(@RequestParam("countryUuid")UUID countryUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.getCountryDTOByUuid(countryUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/countries", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountCountries(@RequestParam(required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        final int amountCountries= localizationService.getAmountCountriesBySearchKey(searchKey);

        response.put("status",true);
        response.put("code","SS-00006");
        response.put("message","accept amount type activity successful returned");
        response.put("data",amountCountries);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-column/country", params = {"id"}, produces = "application/json")
    public ResponseEntity getColumnCountry(@RequestParam("id")int id){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.getColumnNameCountryById(id);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
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

    @PostMapping(path = "/add-region", params = {"countryUuid"}, produces = "application/json")
    public ResponseEntity addRegion(@Validated @RequestBody Region region,
                                    @RequestParam("countryUuid")UUID countryUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.addRegion(region, countryUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/edit-region", params = {"countryUuid"}, produces = "application/json")
    public ResponseEntity editRegion(@Validated @RequestBody Region region,
                                     @RequestParam("countryUuid")UUID countryUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=localizationService.editRegion(region, countryUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/remove-region", params = {"regionUuid"}, produces = "application/json")
    public ResponseEntity removeRegion(@RequestParam("regionUuid")UUID regionUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.removeRegionByUuid(regionUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-all/regions", params = {"page", "size"}, produces = "application/json")
    public ResponseEntity getRegions(@RequestParam("page")int page,
                                     @RequestParam("size")int size,
                                     @RequestBody(required = false) String requestBody){

        Map<String, Object>response=new LinkedHashMap<>();
        String searchKey=null;
        List<String> sortBy=null;
        List<SortType>sortTypes=null;

        try {
            JSONObject jsonObject;
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
                JSONArray jsonArray=jsonObject.getJSONArray("sort");
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
        final ResponseTransfer responseTransfer= localizationService.getAllRegionDTOS(searchKey, page, size, sortBy,
                sortTypes);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-region", params = {"regionUuid"}, produces = "application/json")
    public ResponseEntity getRegion(@RequestParam("regionUuid")UUID regionUuid){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer= localizationService.getRegionDTOByUuid(regionUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/get-amount/regions", params = {"searchKey"}, produces = "application/json")
    public ResponseEntity getAmountRegions(@RequestParam(required = false)String searchKey){

        Map<String, Object>response=new LinkedHashMap<>();
        int amountCountries= localizationService.getAmountRegionsBySearchKey(searchKey);

        response.put("status",true);
        response.put("code","SS-00006");
        response.put("message","accept amount type activity successful returned");
        response.put("data",amountCountries);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-column/region", params = {"id"}, produces = "application/json")
    public ResponseEntity getColumnRegion(@RequestParam("id")int id){

        Map<String, Object>response=new LinkedHashMap<>();
        ResponseTransfer responseTransfer= localizationService.getColumnNameRegionById(id);

        response.put("status",responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message",responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
