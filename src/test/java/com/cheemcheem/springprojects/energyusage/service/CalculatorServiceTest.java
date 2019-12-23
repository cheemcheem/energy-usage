package com.cheemcheem.springprojects.energyusage.service;

import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.DAY;
import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.HALF_DAY;
import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.QUARTER_DAY;
import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.toLocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.SpendingRangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CalculatorServiceTest {


  private final List<SpendingRange> spendingRanges = new ArrayList<>();
  private final SpendingRangeRepository spendingRangeRepository = new SpendingRangeRepository(
      spendingRanges);
  private final CalculatorService calculatorService = new CalculatorService(
      spendingRangeRepository);

  @BeforeEach
  void setup() {
    // 1 -( £10 )-> 2, 2 -( £10 )-> 3, 3 -( £10 )-> 4
    spendingRanges.clear();
    spendingRanges.addAll(List.of(
        new SpendingRange(
            toLocalDateTime(DAY), toLocalDateTime(2 * DAY), BigDecimal.TEN),
        new SpendingRange(
            toLocalDateTime(2 * DAY), toLocalDateTime(3 * DAY), BigDecimal.TEN),
        new SpendingRange(
            toLocalDateTime(3 * DAY), toLocalDateTime(4 * DAY), BigDecimal.TEN)
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
      var requestStartDate = toLocalDateTime(DAY + QUARTER_DAY);
      var requestEndDate = toLocalDateTime(DAY + HALF_DAY);
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
      var requestStartDate = toLocalDateTime(2 * DAY - QUARTER_DAY);
      var requestEndDate = toLocalDateTime(2 * DAY + QUARTER_DAY);
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
      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(DAY + HALF_DAY);
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
      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(2 * DAY + HALF_DAY);
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
          new SpendingRange(
              toLocalDateTime(DAY), toLocalDateTime(4 * DAY), BigDecimal.valueOf(30.00))
      ));

      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(4 * DAY + HALF_DAY);
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
      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(4 * DAY + HALF_DAY);
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
      var requestStartDate = toLocalDateTime(3 * DAY + HALF_DAY);
      var requestEndDate = toLocalDateTime(4 * DAY + HALF_DAY);
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
      var startDate = toLocalDateTime(2);
      var endDate = toLocalDateTime(1);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateSpendingBetweenDates(startDate, endDate));
    }

    @Test
    void requestStartsAndEndsAfterEndDate() {
      var requestStartDate = toLocalDateTime(4 * DAY + HALF_DAY);
      var requestEndDate = toLocalDateTime(45 * DAY + HALF_DAY);
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
          () -> calculatorService.calculateSpendingBetweenDates(
              toLocalDateTime(1), toLocalDateTime(2)));
    }

  }

  @Nested
  class RequestWhenRangeStarts {

    @Test
    void requestIsWithinRangeStartingWhenFirstRangeStarts() throws InvalidDateException {
      var requestStartDate = toLocalDateTime(DAY);
      var requestEndDate = toLocalDateTime(DAY + QUARTER_DAY);
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
      var requestStartDate = toLocalDateTime(DAY);
      var requestEndDate = toLocalDateTime(2 * DAY + QUARTER_DAY);
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
      var requestStartDate = toLocalDateTime(3 * DAY);
      var requestEndDate = toLocalDateTime(4 * DAY + HALF_DAY);
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
      var requestStartDate = toLocalDateTime(DAY + QUARTER_DAY);
      var requestEndDate = toLocalDateTime(2 * DAY);
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
      var requestStartDate = toLocalDateTime(2 * DAY - QUARTER_DAY);
      var requestEndDate = toLocalDateTime(3 * DAY);
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
      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(2 * DAY);
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
      var requestStartDate = toLocalDateTime(HALF_DAY);
      var requestEndDate = toLocalDateTime(3 * DAY);
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
      var requestStartDate = toLocalDateTime(DAY);
      var requestEndDate = toLocalDateTime(2 * DAY);
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
      var startDate = toLocalDateTime(2 * DAY);
      var endDate = toLocalDateTime(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageDailySpending(startDate, endDate));
    }
  }

  @Nested
  class WeeklyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = toLocalDateTime(8 * DAY);
      var endDate = toLocalDateTime(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageWeeklySpending(startDate, endDate));
    }
  }

  @Nested
  class MonthlyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = toLocalDateTime(40 * DAY);
      var endDate = toLocalDateTime(DAY);

      assertThrows(InvalidDateException.class,
          () -> calculatorService.calculateAverageMonthlySpending(startDate, endDate));
    }

    @Test
    void worksOverMultipleYears() {
      var startRange = LocalDateTime.of(1970, 12, 22, 0, 0, 0, 0);
      var endRange = LocalDateTime.of(1971, 1, 21, 0, 0, 0, 0);
      var valRange = BigDecimal.valueOf(90);
      spendingRanges.clear();
      spendingRanges.addAll(List.of(
          new SpendingRange(startRange, endRange, valRange)
      ));

      var startOne = LocalDateTime.of(1970, 12, 22, 0, 0, 0, 0);
      var endOne = LocalDateTime.of(1970, 12, 31, 23, 59, 59, 999999999);
      var valOne = BigDecimal.valueOf(3).setScale(2, RoundingMode.HALF_UP);

      var startTwo = LocalDateTime.of(1971, 1, 1, 0, 0, 0, 0);
      var endTwo = LocalDateTime.of(1971, 1, 21, 0, 0, 0, 0);
      var valTwo = BigDecimal.valueOf(3).setScale(2, RoundingMode.HALF_UP);

      var expectedMonthOne = new SpendingRange(startOne, endOne, valOne);
      var expectedMonthTwo = new SpendingRange(startTwo, endTwo, valTwo);
      var results = calculatorService.calculateAverageMonthlySpending();

      assertThat(results).containsExactlyInAnyOrder(expectedMonthOne, expectedMonthTwo);

    }
  }

}