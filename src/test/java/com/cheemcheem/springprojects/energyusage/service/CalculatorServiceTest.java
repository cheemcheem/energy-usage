package com.cheemcheem.springprojects.energyusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.springprojects.energyusage.repository.SpendingRangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CalculatorServiceTest {

  private CalculatorService calculatorService;

  public static final long DAY = 86400000L / 2;
  public static final long HALF_DAY = DAY / 2;
  public static final long QUARTER_DAY = HALF_DAY / 2;
  @MockBean
  private EnergyReadingRepository energyReadingRepository;
  @MockBean
  private SpendingRangeRepository spendingRangeRepository;

  @BeforeEach
  void setup() {
    var earliest = new Date(2 * HALF_DAY);
    var latest = new Date(8 * HALF_DAY);
    calculatorService = new CalculatorService(spendingRangeRepository, energyReadingRepository);

    // 1 -( £10 )-> 2, 2 -( £10 )-> 3, 3 -( £10 )-> 4
    when(spendingRangeRepository.getSpendingRanges()).thenReturn(List.of(
        new SpendingRange(new Date(DAY), new Date(2 * DAY), BigDecimal.TEN),
        new SpendingRange(new Date(2 * DAY), new Date(3 * DAY), BigDecimal.TEN),
        new SpendingRange(new Date(3 * DAY), new Date(4 * DAY), BigDecimal.TEN)
    ));
    when(energyReadingRepository.earliest()).thenReturn(earliest);
    when(energyReadingRepository.latest()).thenReturn(latest);
  }

  @Test
  void contextLoads() {
    assertThat(calculatorService).isNotNull();
    assertThat(calculatorService.generateForAllReadings()).isNotNull();
  }

  @Test
  void requestIsWithinRange() {
    var requestStartDate = new Date(DAY + QUARTER_DAY);
    var requestEndDate = new Date(DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Test
  void requestOver2Ranges() {
    var requestStartDate = new Date(2 * DAY - QUARTER_DAY);
    var requestEndDate = new Date(2 * DAY + QUARTER_DAY);
    var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Test
  void requestStartsBeforeFirstRangeEndsInsideFirstRange() {
    var requestStartDate = new Date(HALF_DAY);
    var requestEndDate = new Date(DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Test
  void requestStartsBeforeFirstRangeEndsInsideSecondRange() {
    var requestStartDate = new Date(HALF_DAY);
    var requestEndDate = new Date(2 * DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(15.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Test
  void rangeIsWithinRequest() {
    // use DAY -> 4*DAY to keep earliest and latest dates same as others
    when(spendingRangeRepository.getSpendingRanges()).thenReturn(List.of(
        new SpendingRange(new Date(DAY), new Date(4 * DAY), BigDecimal.valueOf(30.00))
    ));

    var requestStartDate = new Date(HALF_DAY);
    var requestEndDate = new Date(4 * DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);

  }

  @Test
  void rangesAreWithinRequest() {
    var requestStartDate = new Date(HALF_DAY);
    var requestEndDate = new Date(4 * DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Test
  void requestEndsAfterLastRange() {
    var requestStartDate = new Date(3 * DAY + HALF_DAY);
    var requestEndDate = new Date(4 * DAY + HALF_DAY);
    var expectedUsage = BigDecimal.valueOf(5.00).setScale(2, RoundingMode.HALF_UP);

    var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
    var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
    assertThat(resultUsage).isEqualTo(expectedUsage);
    assertThat(result.getStartDate()).isEqualTo(requestStartDate);
    assertThat(result.getEndDate()).isEqualTo(requestEndDate);
  }

  @Nested
  class RequestWhenRangeStarts {

    @Test
    void requestIsWithinRangeStartingWhenFirstRangeStarts() {
      var requestStartDate = new Date(DAY);
      var requestEndDate = new Date(DAY + QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesStartingWhenFirstRangeStarts() {
      var requestStartDate = new Date(DAY);
      var requestEndDate = new Date(2 * DAY + QUARTER_DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsWhenSecondLastRangeStartsAndEndsAfterLastRange() {
      var requestStartDate = new Date(3 * DAY);
      var requestEndDate = new Date(4 * DAY + HALF_DAY);
      var expectedUsage = BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

  }

  @Nested
  class RequestWhenRangeEnds {

    @Test
    void requestIsWithinRangeEndingWhenRangeEnds() {
      var requestStartDate = new Date(DAY + QUARTER_DAY);
      var requestEndDate = new Date(2 * DAY);
      var expectedUsage = BigDecimal.valueOf(7.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestOver2RangesEndingWhenSecondRangeEnds() {
      var requestStartDate = new Date(2 * DAY - QUARTER_DAY);
      var requestEndDate = new Date(3 * DAY);
      var expectedUsage = BigDecimal.valueOf(12.50).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenFirstRangeEnds() {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(2 * DAY);
      var expectedUsage = BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

    @Test
    void requestStartsBeforeFirstRangeEndsWhenSecondRangeEnds() {
      var requestStartDate = new Date(HALF_DAY);
      var requestEndDate = new Date(3 * DAY);
      var expectedUsage = BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_UP);

      var result = calculatorService.generateForReadingsBetween(requestStartDate, requestEndDate);
      var resultUsage = result.getUsage().setScale(2, RoundingMode.HALF_UP);
      assertThat(resultUsage).isEqualTo(expectedUsage);
      assertThat(result.getStartDate()).isEqualTo(requestStartDate);
      assertThat(result.getEndDate()).isEqualTo(requestEndDate);
    }

  }
}