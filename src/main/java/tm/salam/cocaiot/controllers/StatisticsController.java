package tm.salam.cocaiot.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.services.StatisticsService;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping(path = "/get-countries/statistics", produces = "application/json")
    public ResponseEntity getCountryStatistics(@RequestParam(value = "initialDate", required = false)
                                                   @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                               @RequestParam(value = "finalDate", required = false)
                                                    @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getStatisticsForCountries(initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-regions/statistics", produces = "application/json")
    public ResponseEntity getRegionStatistics(@RequestParam(value = "initialDate", required = false)
                                                   @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                              @RequestParam(value = "finalDate", required = false)
                                                    @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate,
                                              @RequestParam(value = "countryUuid", required = false)UUID countryUuid){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getStatisticsForRegions(initialDate, finalDate, countryUuid);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-activities/statistics", produces = "application/json")
    public ResponseEntity getTypeActivityStatistics(@RequestParam(value = "initialDate", required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                                    @RequestParam(value = "finalDate", required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getStatisticsForTypeActivities(initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-payment/statistics", produces = "application/json")
    public ResponseEntity getStatusPaymentStatistics(@RequestParam(value = "initialDate", required = false)
                                                         @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                                     @RequestParam(value = "finalDate", required = false)
                                                         @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getStatisticsByStatusPayment(initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-ownership/statistics", produces = "application/json")
    public ResponseEntity getTypeOwnershipStatistics(@RequestParam(value = "initialDate", required = false)
                                                     @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                                     @RequestParam(value = "finalDate", required = false)
                                                     @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getStatisticsByTypeOwnership(initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/get-amount/members", produces = "application/json")
    public ResponseEntity getAmountMemberStatistics(@RequestParam(value = "initialDate", required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy")Date initialDate,
                                                    @RequestParam(value = "finalDate", required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy")Date finalDate){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getAmountMemberStatistics(initialDate, finalDate);

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

    @GetMapping(path = "/amount/last-members", produces = "application/json")
    public ResponseEntity getAmountMemberStatisticsByDayWeekMonthYear(){

        Map<String, Object> response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer=statisticsService.getAmountMembersByDayWeekMonthYear();

        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
