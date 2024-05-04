package tm.salam.cocaiot.services;

import tm.salam.cocaiot.helpers.ResponseTransfer;

import java.util.Date;
import java.util.UUID;

public interface StatisticsService {
    ResponseTransfer getStatisticsForCountries(Date initialDate, Date finalDate);

    ResponseTransfer getStatisticsForRegions(Date initialDate, Date finalDate, UUID countryUuid);

    ResponseTransfer getStatisticsForTypeActivities(Date initialDate, Date finalDate);

    ResponseTransfer getStatisticsByStatusPayment(Date initialDate, Date finalDate);

    ResponseTransfer getStatisticsByTypeOwnership(Date initialDate, Date finalDate);

    ResponseTransfer getAmountMemberStatistics(Date initialDate, Date finalDate);

    ResponseTransfer getAmountMembersByDayWeekMonthYear();
}
