package com.cheemcheem.projects.energyusage.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.projects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.tests.util.LocalDateTimeHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpendingRangeRepositoryTest {


  private final List<SpendingRange> spendingRanges = new ArrayList<>();
  private final SpendingRangeRepository spendingRangeRepository = new SpendingRangeRepository(
      spendingRanges);


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
  void getBetweenDatesWorksNormally() {
    assertThat(spendingRangeRepository.getBetweenDates(
        LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY + 1),
        LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY + 1)
    )).containsExactlyInAnyOrder(
        new SpendingRange(
            LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
            .toLocalDateTime(3 * LocalDateTimeHelper.DAY), BigDecimal.TEN),
        new SpendingRange(
            LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY), LocalDateTimeHelper
            .toLocalDateTime(4 * LocalDateTimeHelper.DAY), BigDecimal.TEN)
    );
  }

  @Test
  void getBetweenDatesNotThrowsWhenEmpty() {
    spendingRanges.clear();
    assertDoesNotThrow(() -> spendingRangeRepository.getBetweenDates(
        LocalDateTimeHelper.toLocalDateTime(2 * LocalDateTimeHelper.DAY + 1),
        LocalDateTimeHelper.toLocalDateTime(3 * LocalDateTimeHelper.DAY + 1)
    ));
  }

  @Test
  void earliestWorksNormally() throws EmptyRepositoryException {
    assertThat(spendingRangeRepository.earliest()).isEqualTo(
        LocalDateTimeHelper.toLocalDateTime(LocalDateTimeHelper.DAY));
  }

  @Test
  void earliestThrowsWhenEmpty() {
    spendingRanges.clear();
    assertThrows(EmptyRepositoryException.class, spendingRangeRepository::earliest);
  }

  @Test
  void latestWorksNormally() throws EmptyRepositoryException {
    assertThat(spendingRangeRepository.latest()).isEqualTo(
        LocalDateTimeHelper.toLocalDateTime(4 * LocalDateTimeHelper.DAY));
  }

  @Test
  void latestThrowsWhenEmpty() {
    spendingRanges.clear();
    assertThrows(EmptyRepositoryException.class, spendingRangeRepository::latest);
  }
}