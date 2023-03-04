package MaintenanceManager.MaintenanceManager.services.utiltities;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerateDatesService {

    @Loggable
    public List<LocalDate> getIntervalSchedulingDatesByQuarter(
            Integer interval, Integer year,
            QuarterlySchedulingEnum quarter) {
        List<LocalDate> dates = new ArrayList<>();
        System.out.println("Quarter: " + quarter + "; Interval: " + interval + "; Year + " + year);
        // gets the starting date (randomly generated according to interval task) and
        // then adds a date to the ArrayList every n-th day (interval)
        LocalDate dateInQuarter = getFirstDateForIntervalTaskByYearAndQuarter(
                interval, year, quarter);
        switch (quarter) {
            case Q1:
                int Q21stMonth = 4; // month value when dates cease to be generated
                while (dateInQuarter.getMonthValue() < Q21stMonth) {
                    //System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusDays(interval);
                }
                break;
            case Q2:
                System.out.println("Quarter 2");
                int Q31stMonth = 7; // month value when dates cease to be generated
                while (dateInQuarter.getMonthValue() < Q31stMonth) {
                    //System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusDays(interval);
                }
                break;
            case Q3:
                System.out.println("Quarter 3");
                int Q41stMonth = 10; // month value when dates cease to be generated
                while (dateInQuarter.getMonthValue() < Q41stMonth) {
                    //System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusDays(interval);
                }
                break;
            default:
                System.out.println("Quarter 4");
                int nextYearValue = year + 1; // year value when dates cease to be generated
                while (dateInQuarter.getYear() < nextYearValue) {
                    //System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusDays(interval);
                }
                break;
        }
        return dates;
    }

    @Loggable
   public List<LocalDate> getMonthlySchedulingDatesByQuarter(
           Integer year, QuarterlySchedulingEnum quarter, Integer dayOfMonth) {
        // this adds the dates to an ArrayList (one per month at the desired day of month)
       List<LocalDate> dates = new ArrayList<>();
       switch (quarter) {
           case Q1:
               System.out.println("Quarter 1");
               dates.add(LocalDate.of(year, 1, dayOfMonth));
               dates.add(LocalDate.of(year, 2, dayOfMonth));
               dates.add(LocalDate.of(year, 3, dayOfMonth));
               break;
           case Q2:
               System.out.println("Quarter 2");
               dates.add(LocalDate.of(year, 4, dayOfMonth));
               dates.add(LocalDate.of(year, 5, dayOfMonth));
               dates.add(LocalDate.of(year, 6, dayOfMonth));
               break;
           case Q3:
               System.out.println("Quarter 3");
               dates.add(LocalDate.of(year, 7, dayOfMonth));
               dates.add(LocalDate.of(year, 8, dayOfMonth));
               dates.add(LocalDate.of(year, 9, dayOfMonth));
               break;
           default:
               System.out.println("Quarter 4");
               dates.add(LocalDate.of(year, 10, dayOfMonth));
               dates.add(LocalDate.of(year, 11, dayOfMonth));
               dates.add(LocalDate.of(year, 12, dayOfMonth));
               break;
       }
       return dates;
   }
    @Loggable
    public List<LocalDate> getWeeklySchedulingDatesByQuarter(
            DayOfWeek dayofWeek, Integer year,
            QuarterlySchedulingEnum quarter) {
        List<LocalDate> dates = new ArrayList<>();
        System.out.println("Quarter: " + quarter + "; Day: " + dayofWeek + "; Year + " + year);
        // dateInQuarter first gets the starting point LocalDate, and then is continually modified
        // to be one week later and added to the ArrayList until the date is in the next quarter
        LocalDate dateInQuarter = getFirstDayOfWeekByYearAndQuarter(
                dayofWeek, year, quarter);
        switch (quarter) {
            case Q1:
                int Q21stMonth = 4;
                while (dateInQuarter.getMonthValue() < Q21stMonth) {
                    System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusWeeks(1);
                }
                break;
            case Q2:
                System.out.println("Quarter 2");
                int Q31stMonth = 7;
                while (dateInQuarter.getMonthValue() < Q31stMonth) {
                    System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusWeeks(1);
                }
                break;
            case Q3:
                System.out.println("Quarter 3");
                int Q41stMonth = 10;
                while (dateInQuarter.getMonthValue() < Q41stMonth) {
                    System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusWeeks(1);
                }
                break;
            default:
                System.out.println("Quarter 4");
                int nextYearValue = year + 1;
                while (dateInQuarter.getYear() < nextYearValue) {
                    System.out.println(dateInQuarter);
                    dates.add(dateInQuarter);
                    dateInQuarter = dateInQuarter.plusWeeks(1);
                }
                break;
        }
        return dates;
    }

    @Loggable
    public LocalDate getFirstDayOfWeekByYearAndQuarter(
            DayOfWeek dayofWeek, Integer year,
            QuarterlySchedulingEnum quarter
    ) { // for each quarter getting the LocalDate by day of week often produces a date from
        // the prior month, so each quarter has an if block to determine if this has happened
        // and if so calibrates the date to be one week later -- in the desired year/quarter
       LocalDate firstDayOfQuarter;
        switch (quarter) {
            case Q1:
                //System.out.println("Quarter 1");
                LocalDate ldQ1 = LocalDate.of(year, 1, 1);
                ldQ1 = ldQ1.with(dayofWeek);
                //System.out.println("Date before adjustment check: " + ldQ1);
                if (ldQ1.getMonthValue() == 12
                        && ldQ1.getYear() < year) {
                    ldQ1 =  ldQ1.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ1;
                break;
            case Q2:
                //System.out.println("Quarter 2");
                LocalDate ldQ2 = LocalDate.of(year, 4, 1);
                ldQ2 = ldQ2.with(dayofWeek);
                //System.out.println("Date before adjustment check: " + ldQ2);
                if (ldQ2.getMonthValue() < 4) {
                    ldQ2 = ldQ2.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ2;
                break;
            case Q3:
                //System.out.println("Quarter 3");
                LocalDate ldQ3 = LocalDate.of(year, 7, 1);
                ldQ3 = ldQ3.with(dayofWeek);
                //System.out.println("Date before adjustment check: " + ldQ3);
                if (ldQ3.getMonthValue() < 7) {
                    ldQ3 = ldQ3.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ3;
                break;
            default:
                //System.out.println("Quarter 4");
                LocalDate ldQ4 = LocalDate.of(year, 10, 1);
                ldQ4 = ldQ4.with(dayofWeek);
                //System.out.println("Date before adjustment check: " + ldQ4);
                if (ldQ4.with(dayofWeek).getMonthValue() < 10) {
                    ldQ4 = ldQ4.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ4;
                break;

        }
        return firstDayOfQuarter;
    }

    @Loggable
    public LocalDate getFirstDateForIntervalTaskByYearAndQuarter(
            Integer interval, Integer year, QuarterlySchedulingEnum quarter
    ) { // this gets a random day of the week depending on how many days the interval
        // if the interval is below 7, it gets a random day within the nth day of the week
        // corresponding to the interval. If the interval is over 7, it just picks a random day
        // from 1 to 7
        DayOfWeek[] possibleDaysToBegin = new DayOfWeek[]{
                DayOfWeek.SUNDAY, DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        int dailyIndex = interval;
        if (interval > 7) {
            dailyIndex = 7;
        }
        int random = (int)(Math.random() * dailyIndex + 1) - 1;
        //System.out.println("The random number is: " + random);
        DayOfWeek beginningDayOfWeek = possibleDaysToBegin[random];
        //System.out.println("The beginning day of the week is: " + beginningDayOfWeek);
        return getFirstDayOfWeekByYearAndQuarter(
                beginningDayOfWeek, year, quarter);
    }

}
