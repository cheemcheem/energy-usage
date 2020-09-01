package com.cheemcheem.projects.energyusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.repository.SpendingRangeRepository;
import com.cheemcheem.projects.energyusage.tests.util.LocalDateTimeHelper;
import com.cheemcheem.projects.energyusage.util.Calculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CalculatorTest {


  private final List<SpendingRange> spendingRanges = new ArrayList<>();
  private final SpendingRangeRepository spendingRangeRepository = new SpendingRangeRepository(
      spendingRanges);
  private final Calculator calculator = new Calculator(
      spendingRangeRepository);

  @BeforeEach
  void setup() {
    // 1 -( £10 )-> 2, 2 -( £10 )-> 3, 3 -( £10 )-> 4
    spendingRanges.clear();
    spendingRanges.addAll(List.of(
        new SpendingRange(
            LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY), LocalDateTimeHelper
            .toLocalDateTime(2 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
        new SpendingRange(
            LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
            .toLocalDateTime(3 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
        new SpendingRange(
            LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
            .toLocalDateTime(4 * LocalDateTimeHelper.DAY), BigDecimal.TEN)
    ));

  }

  @Test
  void contextLoads() {
    assertThat(calculator).isNotNull();
    assertThat(calculator.calculateAllSpending()).isNotNull();
  }

  @Nested
  class RequestsThatDoNotStartOrEndWithARange {

    @Test
    void requestIsWithinRange() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.QUARTER_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2Ranges() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(2 * LocalDateTimeHelper.DAY - LocalDateTimeHelper.QUARTER_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(2 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsInsideFirstRange() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsInsideSecondRange() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(2 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(15.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
              LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(4 * LocalDateTimeHelper.DAY), BigDecimal.valueOf(30.00))
      ));

      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(4 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);

    }

    @Test
    void rangesAreWithinRequest() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(4 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestEndsAfterLastRange() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(3 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(4 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = LocalDateTimeHelper.toLocalDateTime(2);
      var endDate = LocalDateTimeHelper.toLocalDateTime(1);

      assertThrows(InvalidDateException.class,
          () -> calculator.calculateSpendingBetweenDates(startDate, endDate));
    }

    @Test
    void requestStartsAndEndsAfterEndDate() {
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(4 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(45 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);

      AtomicReference<SpendingRange> resultHolder = new AtomicReference<>();
      assertDoesNotThrow(() -> resultHolder.set(calculator
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
      assertDoesNotThrow(calculator::calculateAllSpending);
      Assertions.assertDoesNotThrow(
          () -> calculator.calculateSpendingBetweenDates(
              LocalDateTimeHelper.toLocalDateTime(1), LocalDateTimeHelper.toLocalDateTime(2)));
    }

  }

  @Nested
  class RequestWhenRangeStarts {

    @Test
    void requestIsWithinRangeStartingWhenFirstRangeStarts() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesStartingWhenFirstRangeStarts() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(2 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsWhenSecondLastRangeStartsAndEndsAfterLastRange() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(4 * LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.QUARTER_DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(7.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesEndingWhenSecondRangeEnds() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper
          .toLocalDateTime(2 * LocalDateTimeHelper.DAY - LocalDateTimeHelper.QUARTER_DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenFirstRangeEnds() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateSpendingBetweenDates(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenSecondRangeEnds() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.HALF_DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
    void dailyAverageOverNoDays() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateAverageDailySpending(requestStartDate, requestEndDate);
      var resultUsage = result.stream()
          .map(SpendingRange::getUsage)
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO)
          .setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result).isEmpty();
    }

    @Test
    void dailyAverageOverHalfDay() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper
          .toLocalDateTime(LocalDateTimeHelper.DAY + LocalDateTimeHelper.HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
          .calculateAverageDailySpending(requestStartDate, requestEndDate);
      var resultUsage = result.stream()
          .map(SpendingRange::getUsage)
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO)
          .setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result).isEmpty();
    }

    @Test
    void dailyAverageOver1Day() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
    void dailyAverageOver2Days() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
    void dailyAverageOver3Days() throws InvalidDateException {
      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(4 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
    void dailyAverageOver10Days() throws InvalidDateException {

      // extend initial condition
      spendingRanges.addAll(List.of(
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(4 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(5 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(5 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(6 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(6 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(7 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(7 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(8 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(8 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(9 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(9 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(10 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
          new SpendingRange(
              LocalDateTimeHelper.toLocalDateTime(10 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
              .toLocalDateTime(11 * LocalDateTimeHelper.DAY), BigDecimal.TEN)
      ));

      var requestStartDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);
      var requestEndDate = LocalDateTimeHelper.toLocalDateTime(11 * LocalDateTimeHelper.DAY);
      var expectedUsage = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculator
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
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY);
      var endDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);

      assertThrows(InvalidDateException.class,
          () -> calculator.calculateAverageDailySpending(startDate, endDate));
    }
  }

  @Nested
  class WeeklyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = LocalDateTimeHelper.toLocalDateTime(8 * LocalDateTimeHelper.DAY);
      var endDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);

      assertThrows(InvalidDateException.class,
          () -> calculator.calculateAverageWeeklySpending(startDate, endDate));
    }


    /**
     * Ensure that it will calculate average daily spending per week for a range that goes over a
     * new year. Over 4 weeks, 90 was spent, making the average daily value for each range = 90 / (4
     * * 7) ~= 3.21
     */
    @Test
    void worksOverMultipleYears() {
      var startRange = LocalDateTime.of(1970, 12, 22, 0, 0, 0, 0);
      var endRange = LocalDateTime.of(1971, 1, 19, 0, 0, 0, 0);
      var valRange = BigDecimal.valueOf(90);

      spendingRanges.clear();
      spendingRanges.addAll(List.of(
          new SpendingRange(startRange, endRange, valRange)
      ));

      var startOne = LocalDateTime.of(1970, 12, 22, 0, 0, 0, 0);
      var endOne = LocalDateTime.of(1970, 12, 29, 0, 0, 0, 0);
      var valOne = BigDecimal.valueOf(3.21);

      var startTwo = LocalDateTime.of(1970, 12, 29, 0, 0, 0, 0);
      var endTwo = LocalDateTime.of(1971, 1, 5, 0, 0, 0, 0);
      var valTwo = BigDecimal.valueOf(3.21);

      var startThree = LocalDateTime.of(1971, 1, 5, 0, 0, 0, 0);
      var endThree = LocalDateTime.of(1971, 1, 12, 0, 0, 0, 0);
      var valThree = BigDecimal.valueOf(3.21);

      var startFour = LocalDateTime.of(1971, 1, 12, 0, 0, 0, 0);
      var endFour = LocalDateTime.of(1971, 1, 19, 0, 0, 0, 0);
      var valFour = BigDecimal.valueOf(3.21);

      var expectedWeekOne = new SpendingRange(startOne, endOne, valOne);
      var expectedWeekTwo = new SpendingRange(startTwo, endTwo, valTwo);
      var expectedWeekThree = new SpendingRange(startThree, endThree, valThree);
      var expectedWeekFour = new SpendingRange(startFour, endFour, valFour);

      var results = calculator.calculateAverageWeeklySpending();

      assertThat(results)
          .containsExactlyInAnyOrder(expectedWeekOne, expectedWeekTwo, expectedWeekThree,
              expectedWeekFour);
    }
  }

  @Nested
  class MonthlyAverageTests {

    @Test
    void knowsThatDatesShouldBeCorrectWayAround() {
      var startDate = LocalDateTimeHelper.toLocalDateTime(40 * LocalDateTimeHelper.DAY);
      var endDate = LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY);

      assertThrows(InvalidDateException.class,
          () -> calculator.calculateAverageMonthlySpending(startDate, endDate));
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
      var results = calculator.calculateAverageMonthlySpending();

      assertThat(results).containsExactlyInAnyOrder(expectedMonthOne, expectedMonthTwo);

    }
  }

}