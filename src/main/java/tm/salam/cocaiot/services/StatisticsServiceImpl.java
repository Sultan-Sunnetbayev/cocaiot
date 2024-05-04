package tm.salam.cocaiot.services;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import tm.salam.cocaiot.daoes.CocaiotMemberRepository;
import tm.salam.cocaiot.daoes.CountryRepository;
import tm.salam.cocaiot.daoes.RegionRepository;
import tm.salam.cocaiot.daoes.TypeActivityRepository;
import tm.salam.cocaiot.dtoes.StatisticsDTO;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.helpers.StatusPayment;
import tm.salam.cocaiot.helpers.TypeOwnership;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService{

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final TypeActivityRepository typeActivityRepository;
    private final CocaiotMemberRepository cocaiotMemberRepository;
    private final JdbcTemplate jbJdbcTemplate;

    private static int amountCocaiotMembers=0;

    public StatisticsServiceImpl(CountryRepository countryRepository, RegionRepository regionRepository,
                                 TypeActivityRepository typeActivityRepository,
                                 CocaiotMemberRepository cocaiotMemberRepository, JdbcTemplate jbJdbcTemplate) {
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.typeActivityRepository = typeActivityRepository;
        this.cocaiotMemberRepository = cocaiotMemberRepository;
        this.jbJdbcTemplate = jbJdbcTemplate;
    }

    @Override
    public ResponseTransfer getStatisticsForCountries(Date initialDate, Date finalDate){

        final ResponseTransfer responseTransfer;

        if(initialDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            try {
                initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        if(finalDate==null){
            finalDate=new Date();
        }
        amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        final String sqlQuery="SELECT country.uuid, country.name, COUNT(DISTINCT(cocaiot_member.uuid)) FROM countries country " +
                "LEFT JOIN persons person ON person.country_uuid = country.uuid " +
                "LEFT JOIN companies company ON (company.is_cocaiot_member AND company.country_uuid = country.uuid) " +
                "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.is_cocaiot_member AND  entrepreneur.person_uuid = person.uuid) " +
                "LEFT JOIN cocaiot_members cocaiot_member ON ((cocaiot_member.company_uuid = company.uuid OR " +
                "cocaiot_member.entrepreneur_uuid = entrepreneur.uuid) AND (cocaiot_member.created::DATE BETWEEN ? AND ?)) " +
                "GROUP BY country.uuid ORDER BY country.name";
        List<StatisticsDTO> statisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate}, new StatisticsDTORowMapper());

        if(statisticsDTOS==null){
            statisticsDTOS=new LinkedList<>();
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept statistics for country successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getStatisticsForRegions(Date initialDate, Date finalDate, final UUID countryUuid){

        final ResponseTransfer responseTransfer;

        if(initialDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            try {
                initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        if(finalDate==null){
            finalDate=new Date();
        }
        final String sqlQuery;
        List<StatisticsDTO> statisticsDTOS;
        if(countryUuid==null){
            amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
            sqlQuery="SELECT region.uuid, region.name, COUNT(DISTINCT(cocaiot_member.uuid)) FROM regions region " +
                    "LEFT JOIN persons person ON person.region_uuid = region.uuid " +
                    "LEFT JOIN companies company ON (company.is_cocaiot_member AND company.region_uuid = region.uuid) " +
                    "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.is_cocaiot_member AND entrepreneur.person_uuid = person.uuid) " +
                    "LEFT JOIN cocaiot_members cocaiot_member ON ((cocaiot_member.company_uuid = company.uuid OR " +
                    "cocaiot_member.entrepreneur_uuid = entrepreneur.uuid) AND (cocaiot_member.created::DATE BETWEEN ? AND ?)) " +
                    "GROUP BY region.uuid ORDER BY region.name";
            statisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate},
                    new StatisticsDTORowMapper());
        }else{
            amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersByCountryUuidAndBetweenDates(countryUuid,
                    initialDate, finalDate);
            sqlQuery="SELECT region.uuid, region.name, COUNT(DISTINCT(cocaiot_member.uuid)) FROM regions region " +
                    "LEFT JOIN persons person ON person.region_uuid = region.uuid " +
                    "LEFT JOIN companies company ON (company.is_cocaiot_member AND company.region_uuid = region.uuid) " +
                    "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.is_cocaiot_member AND entrepreneur.person_uuid = person.uuid) " +
                    "LEFT JOIN cocaiot_members cocaiot_member ON ((cocaiot_member.company_uuid = company.uuid OR " +
                    "cocaiot_member.entrepreneur_uuid = entrepreneur.uuid) AND (cocaiot_member.created::DATE BETWEEN ? AND ?)) " +
                    "WHERE region.country_uuid = ? GROUP BY region.uuid ORDER BY region.name";
            statisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate, countryUuid},
                    new StatisticsDTORowMapper());
        }
        if(statisticsDTOS==null){
            statisticsDTOS=new LinkedList<>();
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept statistics for regions successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getStatisticsForTypeActivities(Date initialDate, Date finalDate){

        final ResponseTransfer responseTransfer;

        if(initialDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            try {
                initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        if(finalDate==null){
            finalDate=new Date();
        }
        amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        String sqlQuery="SELECT type_activity.uuid, type_activity.name, COUNT(DISTINCT(cocaiot_member.uuid)) FROM type_activities type_activity " +
                "LEFT JOIN type_activities_companies type_activity_company ON type_activity_company.type_activity_uuid = type_activity.uuid " +
                "LEFT JOIN companies company ON (company.is_cocaiot_member AND company.uuid = type_activity_company.company_uuid) " +
                "LEFT JOIN cocaiot_members cocaiot_member ON (cocaiot_member.company_uuid = company.uuid AND " +
                "(cocaiot_member.created::DATE BETWEEN ? AND ?)) " +
                "GROUP BY type_activity.uuid ORDER BY type_activity.name";
        List<StatisticsDTO> companyStatisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate},
                new StatisticsDTORowMapper());
        sqlQuery="SELECT type_activity.uuid, type_activity.name, COUNT(DISTINCT(cocaiot_member.uuid)) FROM type_activities type_activity " +
                "LEFT JOIN entrepreneurs_type_activities entrepreneur_type_activity ON " +
                "entrepreneur_type_activity.type_activity_uuid = type_activity.uuid " +
                "LEFT JOIN entrepreneurs entrepreneur ON (entrepreneur.is_cocaiot_member AND " +
                "entrepreneur.uuid = entrepreneur_type_activity.entrepreneur_uuid) " +
                "LEFT JOIN cocaiot_members cocaiot_member ON (cocaiot_member.entrepreneur_uuid = entrepreneur.uuid AND " +
                "(cocaiot_member.created::DATE BETWEEN ? AND ?)) " +
                "GROUP BY type_activity.uuid ORDER BY type_activity.name";
        List<StatisticsDTO> entrepreneurStatisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate},
                new StatisticsDTORowMapper());
        Map<String, StatisticsDTO>statisticsDTOS=new HashMap<>();

        if(companyStatisticsDTOS!=null){
            for(StatisticsDTO companyStatisticsDTO:companyStatisticsDTOS){
                statisticsDTOS.put(companyStatisticsDTO.getUuid(), companyStatisticsDTO);
            }
        }
        if(entrepreneurStatisticsDTOS!=null){
            for(StatisticsDTO entrepreneurStatisticsDTO:entrepreneurStatisticsDTOS) {
                if(!statisticsDTOS.containsKey(entrepreneurStatisticsDTO.getUuid())) {
                    statisticsDTOS.put(entrepreneurStatisticsDTO.getUuid(), entrepreneurStatisticsDTO);
                }{
                    entrepreneurStatisticsDTO.setAmountMember(entrepreneurStatisticsDTO.getAmountMember() +
                            statisticsDTOS.get(entrepreneurStatisticsDTO.getUuid()).getAmountMember());
                    statisticsDTOS.put(entrepreneurStatisticsDTO.getUuid(), entrepreneurStatisticsDTO);
                }
            }
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept statistics for type activities successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS.values())
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getStatisticsByStatusPayment(Date initialDate, Date finalDate){

        final ResponseTransfer responseTransfer;

        if(initialDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            try {
                initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        if(finalDate==null){
            finalDate=new Date();
        }
        amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        final String sqlQuery="SELECT NULL AS uuid, cocaiot_member.status_payment AS name, COUNT(cocaiot_member) " +
                "FROM cocaiot_members cocaiot_member WHERE cocaiot_member.created::DATE BETWEEN ? AND ? " +
                "GROUP BY cocaiot_member.status_payment ORDER BY cocaiot_member.status_payment";
        List<StatisticsDTO> statisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate}, new StatisticsDTORowMapper());

        if(statisticsDTOS==null){
            statisticsDTOS=new LinkedList<>();
        }
        for(StatusPayment statusPayment:StatusPayment.values()){
            boolean isFounded=false;

            for(int i=0; i<statisticsDTOS.size() && !isFounded; i++){
                if(Objects.equals(statisticsDTOS.get(i).getName(), statusPayment.name())){
                    isFounded=true;
                }
            }
            if(!isFounded){
                statisticsDTOS.add(StatisticsDTO.builder()
                        .name(statusPayment.name())
                        .amountMember(0)
                        .procent(0.0)
                        .build());
            }
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept statistics by status payment successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getStatisticsByTypeOwnership(Date initialDate, Date finalDate){

        final ResponseTransfer responseTransfer;

        if(initialDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
            try {
                initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        if(finalDate==null){
            finalDate=new Date();
        }
        amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        String sqlQuery="SELECT NULL AS uuid, company.type_ownership AS name, COUNT(cocaiot_member) FROM cocaiot_members cocaiot_member " +
                "INNER JOIN companies company ON company.uuid = cocaiot_member.company_uuid " +
                "WHERE cocaiot_member.created::DATE BETWEEN ? AND ? GROUP BY company.type_ownership";
        List<StatisticsDTO> statisticsDTOS=jbJdbcTemplate.query(sqlQuery, new Object[]{initialDate, finalDate}, new StatisticsDTORowMapper());

        if(statisticsDTOS==null){
            statisticsDTOS=new LinkedList<>();
        }
        sqlQuery="SELECT COUNT(cocaiot_member) FROM cocaiot_members cocaiot_member " +
                "INNER JOIN entrepreneurs entrepreneur ON entrepreneur.uuid = cocaiot_member.entrepreneur_uuid " +
                "WHERE cocaiot_member.created BETWEEN ? AND ?";
        final int amountEntrepreneurMember=jbJdbcTemplate.queryForObject(sqlQuery, new Object[]{initialDate, finalDate}, Integer.class);

        statisticsDTOS.add(StatisticsDTO.builder()
                .name(TypeOwnership.ENTREPRENEUR.name())
                .amountMember(amountEntrepreneurMember)
                .procent(amountCocaiotMembers>0 ? amountEntrepreneurMember*100.0/amountCocaiotMembers : 0.0)
                .build());
        for(TypeOwnership typeOwnership:TypeOwnership.values()){
            boolean isFounded=false;

            for(int i=0; i<statisticsDTOS.size() && !isFounded; i++){
                if(Objects.equals(statisticsDTOS.get(i).getName(), typeOwnership.name())){
                    isFounded=true;
                }
            }
            if(!isFounded){
                statisticsDTOS.add(StatisticsDTO.builder()
                        .name(typeOwnership.name())
                        .amountMember(0)
                        .procent(0.0)
                        .build());
            }
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept statistics by status payment successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAmountMemberStatistics(Date initialDate, Date finalDate){

        final ResponseTransfer responseTransfer;
        StatisticsDTO statisticsDTO;
        if(initialDate==null && finalDate==null){
            statisticsDTO=StatisticsDTO.builder()
                    .amountMember(cocaiotMemberRepository.getAmountCocaiotMembers())
                    .procent(100.0)
                    .build();
        }else{
            if(initialDate==null){
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                try {
                    initialDate=new Date(simpleDateFormat.parse("01-01-1900").getTime());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
            if(finalDate==null){
                finalDate=new Date();
            }
            amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembers();
            final int amountMembers=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
            statisticsDTO=StatisticsDTO.builder()
                    .amountMember(amountMembers)
                    .procent(amountMembers*100.0/amountCocaiotMembers)
                    .build();
        }
        responseTransfer=ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept amount member successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTO)
                .build();

        return responseTransfer;
    }

    @Override
    public ResponseTransfer getAmountMembersByDayWeekMonthYear(){

        final ResponseTransfer responseTransfer;
        final Date currentDate=new Date();
        Date initialDate;
        Date finalDate;
        Calendar cal=Calendar.getInstance();
        List<StatisticsDTO>statisticsDTOS=new LinkedList<>();
        amountCocaiotMembers=cocaiotMemberRepository.getAmountCocaiotMembers();

        int amountMember=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(currentDate, currentDate);

        statisticsDTOS.add(StatisticsDTO.builder()
                .amountMember(amountMember)
                .procent(amountMember*100.0/amountCocaiotMembers)
                .build());
        cal.set(Calendar.DAY_OF_WEEK, 1);
        initialDate=cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        finalDate=cal.getTime();
        amountMember=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        statisticsDTOS.add(StatisticsDTO.builder()
                .amountMember(amountMember)
                .procent(amountMember*100.0/amountCocaiotMembers)
                .build());
        cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        initialDate=cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        finalDate=cal.getTime();
        amountMember=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        statisticsDTOS.add(StatisticsDTO.builder()
                .amountMember(amountMember)
                .procent(amountMember*100.0/amountCocaiotMembers)
                .build());
        cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        initialDate=cal.getTime();
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        finalDate=cal.getTime();
        amountMember=cocaiotMemberRepository.getAmountCocaiotMembersBetweenDates(initialDate, finalDate);
        statisticsDTOS.add(StatisticsDTO.builder()
                .amountMember(amountMember)
                .procent(amountMember*100.0/amountCocaiotMembers)
                .build());

        responseTransfer= ResponseTransfer.builder()
                .status(true)
                .code("")
                .message("accept amount member successful returned")
                .httpStatus(HttpStatus.ACCEPTED)
                .data(statisticsDTOS)
                .build();

        return responseTransfer;
    }

    private class StatisticsDTORowMapper implements RowMapper<StatisticsDTO> {
        @Override
        public StatisticsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

            StatisticsDTO statisticsDTO=StatisticsDTO.builder()
                    .uuid(rs.getString("uuid"))
                    .name(rs.getString("name"))
                    .amountMember(rs.getInt("count"))
                    .build();
            if(amountCocaiotMembers>0) {
                statisticsDTO.setProcent(statisticsDTO.getAmountMember() * 100.0 / amountCocaiotMembers);
            }

            return statisticsDTO;
        }

    }

}
