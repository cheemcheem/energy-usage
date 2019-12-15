package com.cheemcheem.springprojects.energyusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.SpendingRangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CalculatorServiceTest {


  public static final long DAY = 86400000L;
  public static final long HALF_DAY = DAY / 2;
  public static final long QUARTER_DAY = HALF_DAY / 2;

  private final List<SpendingRange> spendingRanges = new ArrayList<>();
  private final SpendingRangeRepository spendingRangeRepository = new SpendingRangeRepository(
      spendingRanges);
  private final CalculatorService calculatorService = new CalculatorService(
      spendingRangeRepository);

  private static Date getGmtDate(long date) {
    return new Date(LocalDate.ofInstant(new Date(date).toInstant(), ZoneId.of("GMT")).toEpochDay());
  }

  @BeforeEach
  void setup() {
    // 1 -( £10 )-> 2, 2 -( £10 )-> 3, 3 -( £10 )-> 4
    spendingRanges.clear();
    spendingRanges.addAll(List.of(
        new SpendingRange(new Date(DAY), new Date(2 * DAY), BigDecimal.TEN),
        new SpendingRange(new Date(2 * DAY), new Date(3 * DAY), BigDecimal.TEN),
        new SpendingRange(new Date(3 * DAY), new Date(4 * DAY), BigDecimal.TEN)
    ));

  }

  @Test
  void contextLoads() {
    assertThat(calculatorService).isNotNull();
    assertThat(calculatorService.calculateAllSpending()).isNotNull();
  }

  @Nested
  class RequestsThatDoNotStartOrEndWithARange {

    @Test
    void requestIsWithinRange() throws InvalidDateException {
      var requestStartDate = new Date(DAY + QUARTER_DAY);
      var requestEndDate = new Date(DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2Ranges() throws InvalidDateException {
      var requestStartDate = new Date(2 * DAY - QUARTER_DAY);
      var requestEndDate = new Date(2 * DAY + QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsInsideFirstRange() throws InvalidDateException {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsInsideSecondRange() throws InvalidDateException {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(2 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(15.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void rangeIsWithinRequest() throws InvalidDateException {
      // use DAY -> 4*DAY to keep earliest and latest dates same as others
      spendingRanges.clear();
      spendingRanges.addAll(List.of(
          new SpendingRange(new Date(DAY), new Date(4 * DAY), BigDecimal.valueOf(30.00))
      ));

      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(4 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);

    }

    @Test
    void rangesAreWithinRequest() throws InvalidDateException {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(4 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestEndsAfterLastRange() throws InvalidDateException {
      var requestStartDate = new Date(3 * DAY + HALF_DAY);
      var requestEndDate = new Date(4 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = new Date(2);
      var endDate = new Date(1);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateSpendingBetweenDates(startDate, endDate));
    }

    @Test
    void requestStartsAndEndsAfterEndDate() {
      var requestStartDate = new Date(4 * DAY + HALF_DAY);
      var requestEndDate = new Date(45 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);

      AtomicReference<SpendingRange> resultHolder = new AtomicReference<>();
      assertDoesNotThrow(() -> resultHolder.set(calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate)));

      var result = resultHolder.get();
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void handlesEmptyRepository() {
      spendingRanges.clear();
      assertDoesNotThrow(calculatorService::calculateAllSpending);
      assertDoesNotThrow(
          () -> calculatorService.calculateSpendingBetweenDates(new Date(1), new Date(2)));
    }

  }

  @Nested
  class RequestWhenRangeStarts {

    @Test
    void requestIsWithinRangeStartingWhenFirstRangeStarts() throws InvalidDateException {
      var requestStartDate = new Date(DAY);
      var requestEndDate = new Date(DAY + QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesStartingWhenFirstRangeStarts() throws InvalidDateException {
      var requestStartDate = new Date(DAY);
      var requestEndDate = new Date(2 * DAY + QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsWhenSecondLastRangeStartsAndEndsAfterLastRange() throws InvalidDateException {
      var requestStartDate = new Date(3 * DAY);
      var requestEndDate = new Date(4 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

  }

  @Nested
  class RequestWhenRangeEnds {

    @Test
    void requestIsWithinRangeEndingWhenRangeEnds() throws InvalidDateException {
      var requestStartDate = new Date(DAY + QUARTER_DAY);
      var requestEndDate = new Date(2 * DAY);
      var expectedUsage = BigDecimal.valueOf(7.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesEndingWhenSecondRangeEnds() throws InvalidDateException {
      var requestStartDate = new Date(2 * DAY - QUARTER_DAY);
      var requestEndDate = new Date(3 * DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenFirstRangeEnds() throws InvalidDateException {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(2 * DAY);
      var expectedUsage = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenSecondRangeEnds() throws InvalidDateException {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(3 * DAY);
      var expectedUsage = BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

  }

  @Nested
  class DailyAverageTests {


    @Test
    void dailyAverageOverNoDays() {

    }

    @Test
    void dailyAverageOverHalfDay() {

    }

    @Test
    void dailyAverageOver1Day() throws InvalidDateException {
      var requestStartDate = new Date(DAY);
      var requestEndDate = new Date(2 * DAY);
      var expectedUsage = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService
          .calculateAverageDailySpending(requestStartDate, requestEndDate);
      var resultUsage = result.stream()
          .map(SpendingRange::getUsage)
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO)
          .setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.get(0).getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.get(result.size() - 1).getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void dailyAverageOver2Days() {

    }

    @Test
    void dailyAverageOver3Days() {

    }

    @Test
    void dailyAverageOver10Days() {

    }

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = new Date(2 * DAY);
      var endDate = new Date(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageDailySpending(startDate, endDate));
    }
  }

  @Nested
  class WeeklyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = new Date(8 * DAY);
      var endDate = new Date(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageWeeklySpending(startDate, endDate));
    }
  }

  @Nested
  class MonthlyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = new Date(40 * DAY);
      var endDate = new Date(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageMonthlySpending(startDate, endDate));
    }
  }

}