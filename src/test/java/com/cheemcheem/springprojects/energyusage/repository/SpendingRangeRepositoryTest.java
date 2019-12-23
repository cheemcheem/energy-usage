package com.cheemcheem.springprojects.energyusage.repository;

import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.DAY;
import static com.cheemcheem.springprojects.energyusage.tests.util.LocalDateTimeHelper.toLocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
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
            toLocalDateTime(DAY), toLocalDateTime(2 * DAY), BigDecimal.TEN),
        new SpendingRange(
            toLocalDateTime(2 * DAY), toLocalDateTime(3 * DAY), BigDecimal.TEN),
        new SpendingRange(
            toLocalDateTime(3 * DAY), toLocalDateTime(4 * DAY), BigDecimal.TEN)
    ));

  }

  @Test
  void getBetweenDatesWorksNormally() {
    assertThat(spendingRangeRepository.getBetweenDates(
        toLocalDateTime(2 * DAY + 1),
        toLocalDateTime(3 * DAY + 1)
    )).containsExactlyInAnyOrder(
        new SpendingRange(
            toLocalDateTime(2 * DAY), toLocalDateTime(3 * DAY), BigDecimal.TEN),
        new SpendingRange(
            toLocalDateTime(3 * DAY), toLocalDateTime(4 * DAY), BigDecimal.TEN)
    );
  }

  @Test
  void getBetweenDatesNotThrowsWhenEmpty() {
    spendingRanges.clear();
    assertDoesNotThrow(() -> spendingRangeRepository.getBetweenDates(
        toLocalDateTime(2 * DAY + 1),
        toLocalDateTime(3 * DAY + 1)
    ));
  }

  @Test
  void earliestWorksNormally() throws EmptyRepositoryException {
    assertThat(spendingRangeRepository.earliest()).isEqualTo(toLocalDateTime(DAY));
  }

  @Test
  void earliestThrowsWhenEmpty() {
    spendingRanges.clear();
    assertThrows(EmptyRepositoryException.class, spendingRangeRepository::earliest);
  }

  @Test
  void latestWorksNormally() throws EmptyRepositoryException {
    assertThat(spendingRangeRepository.latest()).isEqualTo(toLocalDateTime(4 * DAY));
  }

  @Test
  void latestThrowsWhenEmpty() {
    spendingRanges.clear();
    assertThrows(EmptyRepositoryException.class, spendingRangeRepository::latest);
  }
}