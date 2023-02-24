package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GenerateDatesService {

   public List<LocalDate> getMonthlySchedulingDatesByQuarter(
           Integer year, QuarterlySchedulingEnum quarter, Integer dayOfMonth) {
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

    public List<LocalDate> getWeeklySchedulingDatesByQuarter(
            DayOfWeek dayofWeek, Integer year,
            QuarterlySchedulingEnum quarter) {
        List<LocalDate> dates = new ArrayList<>();
        System.out.println("Quarter: " + quarter + "; Day: " + dayofWeek + "; Year + " + year);
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

    public LocalDate getFirstDayOfWeekByYearAndQuarter(
            DayOfWeek dayofWeek, Integer year,
            QuarterlySchedulingEnum quarter
    ) {
       LocalDate firstDayOfQuarter;
        switch (quarter) {
            case Q1:
                System.out.println("Quarter 1");
                LocalDate ldQ1 = LocalDate.of(year, 1, 1);
                ldQ1 = ldQ1.with(dayofWeek);
                System.out.println("Date before adjustment check: " + ldQ1);
                if (ldQ1.getMonthValue() == 12
                        && ldQ1.getYear() < year) {
                    ldQ1 =  ldQ1.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ1;
                break;
            case Q2:
                System.out.println("Quarter 2");
                LocalDate ldQ2 = LocalDate.of(year, 4, 1);
                ldQ2 = ldQ2.with(dayofWeek);
                System.out.println("Date before adjustment check: " + ldQ2);
                if (ldQ2.getMonthValue() < 4) {
                    ldQ2 = ldQ2.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ2;
                break;
            case Q3:
                System.out.println("Quarter 3");
                LocalDate ldQ3 = LocalDate.of(year, 7, 1);
                ldQ3 = ldQ3.with(dayofWeek);
                System.out.println("Date before adjustment check: " + ldQ3);
                if (ldQ3.getMonthValue() < 7) {
                    ldQ3 = ldQ3.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ3;
                break;
            default:
                System.out.println("Quarter 4");
                LocalDate ldQ4 = LocalDate.of(year, 10, 1);
                ldQ4 = ldQ4.with(dayofWeek);
                System.out.println("Date before adjustment check: " + ldQ4);
                if (ldQ4.with(dayofWeek).getMonthValue() < 10) {
                    ldQ4 = ldQ4.plusWeeks(1);
                }
                firstDayOfQuarter = ldQ4;
                break;

        }
        return firstDayOfQuarter;
    }

}
