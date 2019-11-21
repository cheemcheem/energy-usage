package com.cheemcheem.springprojects.energyusage.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.service.CalculatorService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CalculatorServiceTest {

  @Autowired
  private CalculatorService calculatorService;

  @Test
  void contextLoads() {
    assertThat(calculatorService).isNotNull();
  }

  @Nested
  class FilterTests {

    @Test
    void filtersEmptyList() {
      var startDate = new Date();
      var endDate = new Date();
      var readings = new ArrayList<EnergyReading>();
      assertDoesNotThrow(
          () -> calculatorService.filterForRange(readings, startDate, endDate));
    }

    @Test
    void filtersDateParametersInclusivelyAcrossDifferentYears() {
      var startDate = Date.from(
          LocalDate.of(2010, 5, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          startReading,
          endReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .contains(startReading, endReading);
    }

    @Test
    void filtersDateParametersInclusivelyWithinSameYear() {
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 6, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          startReading,
          endReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .contains(startReading, endReading);
    }

    @Test
    void filtersDateParametersInclusivelyWithinSameMonth() {
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 6).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          startReading,
          endReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .contains(startReading, endReading);
    }

    @Test
    void filtersDateParametersInclusivelyWithinSameDay() {
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(10, 0).toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(15, 0).toInstant(ZoneOffset.UTC)
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          startReading,
          endReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .contains(startReading, endReading);
    }

    @Test
    void filtersDateParametersInclusivelyWithinSameHour() {
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(5, 15).toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(5, 45).toInstant(ZoneOffset.UTC)
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          startReading,
          endReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .contains(startReading, endReading);
    }

    @Test
    void filtersListCorrectlyAcrossDifferentYears() {
      var preStartDate = Date.from(
          LocalDate.of(2010, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startDate = Date.from(
          LocalDate.of(2011, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2012, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var postEndDate = Date.from(
          LocalDate.of(2013, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var preStartReading = new EnergyReading(
          preStartDate,
          BigDecimal.ZERO
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var postEndReading = new EnergyReading(
          postEndDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          preStartReading,
          startReading,
          endReading,
          postEndReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .containsOnly(startReading, endReading)
          .doesNotContain(preStartReading, postEndReading);
    }

    @Test
    void filtersListCorrectlyWithinSameYear() {
      var preStartDate = Date.from(
          LocalDate.of(2019, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startDate = Date.from(
          LocalDate.of(2019, 4, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var postEndDate = Date.from(
          LocalDate.of(2019, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var preStartReading = new EnergyReading(
          preStartDate,
          BigDecimal.ZERO
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var postEndReading = new EnergyReading(
          postEndDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          preStartReading,
          startReading,
          endReading,
          postEndReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .containsOnly(startReading, endReading)
          .doesNotContain(preStartReading, postEndReading);
    }

    @Test
    void filtersListCorrectlyWithinSameMonth() {
      var preStartDate = Date.from(
          LocalDate.of(2019, 5, 4).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 6).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var postEndDate = Date.from(
          LocalDate.of(2019, 5, 7).atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      var preStartReading = new EnergyReading(
          preStartDate,
          BigDecimal.ZERO
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var postEndReading = new EnergyReading(
          postEndDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          preStartReading,
          startReading,
          endReading,
          postEndReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .containsOnly(startReading, endReading)
          .doesNotContain(preStartReading, postEndReading);
    }

    @Test
    void filtersListCorrectlyWithinSameDay() {
      var preStartDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(1, 0).toInstant(ZoneOffset.UTC)
      );
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(10, 0).toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(20, 0).toInstant(ZoneOffset.UTC)
      );
      var postEndDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(23, 0).toInstant(ZoneOffset.UTC)
      );
      var preStartReading = new EnergyReading(
          preStartDate,
          BigDecimal.ZERO
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var postEndReading = new EnergyReading(
          postEndDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          preStartReading,
          startReading,
          endReading,
          postEndReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .containsOnly(startReading, endReading)
          .doesNotContain(preStartReading, postEndReading);
    }

    @Test
    void filtersListCorrectlyWithinSameHour() {
      var preStartDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(15, 10).toInstant(ZoneOffset.UTC)
      );
      var startDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(15, 15).toInstant(ZoneOffset.UTC)
      );
      var endDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(15, 45).toInstant(ZoneOffset.UTC)
      );
      var postEndDate = Date.from(
          LocalDate.of(2019, 5, 5).atTime(15, 50).toInstant(ZoneOffset.UTC)
      );
      var preStartReading = new EnergyReading(
          preStartDate,
          BigDecimal.ZERO
      );
      var startReading = new EnergyReading(
          startDate,
          BigDecimal.ZERO
      );
      var endReading = new EnergyReading(
          endDate,
          BigDecimal.ZERO
      );
      var postEndReading = new EnergyReading(
          postEndDate,
          BigDecimal.ZERO
      );
      var readings = List.of(
          preStartReading,
          startReading,
          endReading,
          postEndReading
      );
      var filteredReadings = calculatorService.filterForRange(readings, startDate, endDate);

      assertThat(filteredReadings)
          .containsOnly(startReading, endReading)
          .doesNotContain(preStartReading, postEndReading);
    }
  }

  @Nested
  class GenerateTests {

    @Test
    void generateFailsCorrectlyWhenNoReadings() {
      List<EnergyReading> readings = List.of();
      assertThrows(EmptyRepositoryException.class,
          () -> calculatorService.generateForReadings(readings));
    }

    @Test
    void generateCorrectValueWithOneEmptyReading() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.ZERO
          )
      );

      var spendingRange = calculatorService.generateForReadings(readings);

      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.ZERO);

    }

    @Test
    void generateCorrectlyValueWithTwoEmptyReadings() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.ZERO
          ),
          new EnergyReading(
              new Date(2), BigDecimal.ZERO
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.ZERO);
    }


    @Test
    void generateCorrectValueWithOneReading() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(), BigDecimal.valueOf(10)
          )
      );

      var spendingRange = calculatorService.generateForReadings(readings);

      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(0));

    }

    @Test
    void generateCorrectlyValueWithTwoReadings() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(9)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    void generateCorrectlyValueWithResettingReadings() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(9)
          ),
          new EnergyReading(
              new Date(3), BigDecimal.valueOf(20)
          ),
          new EnergyReading(
              new Date(4), BigDecimal.valueOf(19)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(2));
    }


    @Test
    void generateCorrectValueWithOneNegativeReading() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(), BigDecimal.valueOf(-10)
          )
      );

      var spendingRange = calculatorService.generateForReadings(readings);

      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(0));

    }

    @Test
    void generateCorrectValueWithTwoNegativeReadings() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(-10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(-11)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    void generateCorrectValueWithResettingNegativeReadings() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(-10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(-11)
          ),
          new EnergyReading(
              new Date(3), BigDecimal.valueOf(-5)
          ),
          new EnergyReading(
              new Date(4), BigDecimal.valueOf(-6)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(2));
    }


    @Test
    void generateCorrectValueWithNegativeThenPositiveReading() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(-10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(11)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void generateCorrectValueWithPositiveThenNegativeReading() throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(10)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(-11)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(21));
    }

    @Test
    void generateCorrectValueWithResettingNegativeAndPositiveReadings()
        throws EmptyRepositoryException {
      List<EnergyReading> readings = List.of(
          new EnergyReading(
              new Date(1), BigDecimal.valueOf(5)
          ),
          new EnergyReading(
              new Date(2), BigDecimal.valueOf(-11)
          ),
          new EnergyReading(
              new Date(3), BigDecimal.valueOf(10)
          ),
          new EnergyReading(
              new Date(4), BigDecimal.valueOf(-6)
          )
      );
      var spendingRange = calculatorService.generateForReadings(readings);
      assertThat(spendingRange.getUsage()).isEqualTo(BigDecimal.valueOf(32));
    }

  }
}