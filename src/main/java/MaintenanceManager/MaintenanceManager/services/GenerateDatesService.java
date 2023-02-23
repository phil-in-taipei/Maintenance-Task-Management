package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
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

    public void getWeeklySchedulingDatesByQuarter() { // List<LocalDate>
        //int quarter = 2;
        //DayOfWeek dayOfWeek = DayOfWeek.SUNDAY;
        LocalDate ldQ1 = LocalDate.of(2023, 1, 1);
        System.out.println("*********************************First Sunday of Quarter 1: " +
                ldQ1.with(DayOfWeek.SUNDAY) + "******************************");
        System.out.println("*********************************First Saturday of Quarter 1: " +
                ldQ1.with(DayOfWeek.SATURDAY) + "******************************");

        if (ldQ1.with(DayOfWeek.SATURDAY).getMonthValue() == 12
                && ldQ1.with(DayOfWeek.SATURDAY).getYear() < ldQ1.getYear()) {
            System.out.println("********************That value is for the previous month/year******************************");
            System.out.println("********************Should be: " + ldQ1.with(DayOfWeek.SATURDAY).plusWeeks(1) +
                    "****************************");
        }

        LocalDate ldQ2 = LocalDate.of(2023, 4, 1);
        System.out.println("*********************************First Sunday of Quarter 2: " +
                ldQ2.with(DayOfWeek.SUNDAY) + "******************************");
        System.out.println("*********************************First Monday of Quarter 2: " +
                ldQ2.with(DayOfWeek.MONDAY) + "******************************");

        if (ldQ2.with(DayOfWeek.MONDAY).getMonthValue() < 4) {
            System.out.println("********************That value is for the previous month******************************");
            System.out.println("********************Should be: " + ldQ2.with(DayOfWeek.MONDAY).plusWeeks(1) +
                    "****************************");
        }


        LocalDate ldQ3 = LocalDate.of(2023, 7, 1);
        System.out.println("*********************************First Sunday of Quarter 3: " +
                ldQ3.with(DayOfWeek.SUNDAY) + "******************************");
        System.out.println("*********************************First Wednesday of Quarter 3: " +
                ldQ3.with(DayOfWeek.WEDNESDAY) + "******************************");

        if (ldQ3.with(DayOfWeek.WEDNESDAY).getMonthValue() < 7) {
            System.out.println("********************That value is for the previous month******************************");
            System.out.println("********************Should be: " + ldQ3.with(DayOfWeek.WEDNESDAY).plusWeeks(1) +
                    "****************************");
        }


        LocalDate ldQ4 = LocalDate.of(2023, 10, 1);
        System.out.println("*********************************First Sunday of Quarter 4: " +
                ldQ4.with(DayOfWeek.SUNDAY) + "******************************");
        System.out.println("*********************************First Friday of  Quarter 4: " +
                ldQ4.with(DayOfWeek.FRIDAY) + "******************************");

        if (ldQ4.with(DayOfWeek.FRIDAY).getMonthValue() < 10) {
            System.out.println("********************That value is for the previous month******************************");
            System.out.println("********************Should be: " + ldQ4.with(DayOfWeek.FRIDAY).plusWeeks(1) +
                    "****************************");
        }
    }

    /*

    		LocalDate ld = LocalDate.of(2023, 1, 1);
		System.out.println("*************First day of the year: " + ld + "***************************");
		System.out.println("*********************************First Sunday of the year: " +
				ld.with(DayOfWeek.SUNDAY) + "******************************");
		ld = LocalDate.of(2023, 1, 2);
		System.out.println("*********************************First Monday of the year: " +
				ld.with(DayOfWeek.MONDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Tuesday of the year: " +
				ld.with(DayOfWeek.TUESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Wednesday of the year: " +
				ld.with(DayOfWeek.WEDNESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Thursday of the year: " +
				ld.with(DayOfWeek.THURSDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Friday of the year: " +
				ld.with(DayOfWeek.FRIDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Saturday of the year: " +
				ld.with(DayOfWeek.SATURDAY) + "******************************");
		System.out.println("*************************************************************");
		LocalDate ldQ2 = LocalDate.of(2023, 4, 1);
		System.out.println("*************First day of the year: " + ld + "***************************");
		System.out.println("*********************************First Sunday of the year: " +
				ldQ2.with(DayOfWeek.SUNDAY) + "******************************");
		ldQ2 = LocalDate.of(2023, 4, 2);
		System.out.println("*********************************First Monday of the Q: " +
				ldQ2.with(DayOfWeek.MONDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Tuesday of the Q: " +
				ldQ2.with(DayOfWeek.TUESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Wednesday of the Q: " +
				ldQ2.with(DayOfWeek.WEDNESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Thursday of the Q: " +
				ldQ2.with(DayOfWeek.THURSDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Friday of the Q: " +
				ldQ2.with(DayOfWeek.FRIDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Saturday of the Q: " +
				ldQ2.with(DayOfWeek.SATURDAY) + "******************************");
     */

}
