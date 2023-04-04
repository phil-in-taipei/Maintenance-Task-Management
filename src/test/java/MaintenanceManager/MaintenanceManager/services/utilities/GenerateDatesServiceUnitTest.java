package MaintenanceManager.MaintenanceManager.services.utilities;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateDatesService;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
public class GenerateDatesServiceUnitTest {

    @Autowired
    GenerateDatesService generateDatesService;

    @Test
    public void testGetFirstDateForIntervalTaskByYearAndQuarter() {
        assertThat(
                generateDatesService.getFirstDateForIntervalTaskByYearAndQuarter(
                                4, 2023, QuarterlySchedulingEnum.Q2)
                        .getYear())
                .isEqualTo(2023);
        // April is the first month of Q2
        assertThat(
                generateDatesService.getFirstDateForIntervalTaskByYearAndQuarter(
                                4, 2023, QuarterlySchedulingEnum.Q2)
                        .getMonthValue())
                .isEqualTo(4);
        // this tests that the day of the week is in the proper range
        // it should be one of the first four days of the week
        assertThat(
                generateDatesService.getFirstDateForIntervalTaskByYearAndQuarter(
                                4, 2023, QuarterlySchedulingEnum.Q2)
                        .getDayOfWeek())
                .isNotEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    public void testGetFirstDayOfWeekByYearAndQuarter() {
        // first Wednesday of Q2 2023 is 4/5
        assertThat(
                generateDatesService.getFirstDayOfWeekByYearAndQuarter(
                        DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q2).getYear())
                .isEqualTo(2023);
        assertThat(
                generateDatesService.getFirstDayOfWeekByYearAndQuarter(
                        DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q2).getDayOfWeek())
                .isEqualTo(DayOfWeek.WEDNESDAY);
        assertThat(
                generateDatesService.getFirstDayOfWeekByYearAndQuarter(
                        DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q2).getDayOfMonth())
                .isEqualTo(5);
    }

    @Test
    public void testGetIntervalSchedulingDatesByQuarter() {
        // ask mentor about mocking getFirstDateForIntervalTaskByYearAndQuarter
        // method (belongs to same class) inside test for this method
        // in order to get a set reproducible list of dates to test
        assertThat(
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                                25, 2023, QuarterlySchedulingEnum.Q2)
                        .size())
                .isEqualTo(4);
        assertThat(
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                                5, 2023, QuarterlySchedulingEnum.Q2).get(0).getMonthValue()
                        )
                .isEqualTo(4);
        assertThat(
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                        5, 2023, QuarterlySchedulingEnum.Q2).get(7).getMonthValue()
        )
                .isEqualTo(5);
        assertThat(
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                        5, 2023, QuarterlySchedulingEnum.Q2).get(7).getYear()
        )
                .isEqualTo(2023);
    }

    @Test
    public void testGetMonthlySchedulingDatesByQuarter() {
        // 10th day of month in Q2 of 2023 should be the following:
        // 4/10, 5/10, 6/10
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2023));

        assertThat(
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                                2023, QuarterlySchedulingEnum.Q2, 10)
                        .size())
                .isEqualTo(3);
        assertThat(
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                        2023, QuarterlySchedulingEnum.Q2, 10)
        ).isEqualTo(dates);

    }


    @Test
    public void testGetWeeklySchedulingDatesByQuarter() {
        // Wednesdays in Q2 of 2023 should be the following:
        // 4/5, 4/12, 4/19, 4/26, 5/3, 5/10, 5/17, 5/24, 5/31, 6/7, 6/14, 6/21, 6/28
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(5).withYear(2023));
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(12).withYear(2023));
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(19).withYear(2023));
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(26).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(3).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(17).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(24).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(31).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(7).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(14).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(21).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(28).withYear(2023));

        assertThat(
                generateDatesService.getWeeklySchedulingDatesByQuarter(
                        DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q2)
                        .size())
                .isEqualTo(13);
        assertThat(
                generateDatesService.getWeeklySchedulingDatesByQuarter(
                        DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q2)
        ).isEqualTo(dates);
    }

}
