package com.cheemcheem.projects.energyusage.util.mapper;

import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnergyReadingsMapper {

  private final List<EnergyReading> energyReadings;

  public Collection<SpendingRange> getEnergyReadingsRange() {
    // use set to remove duplicates, if this is not done spending ranges with start = end
    // and usage = 0 will occur and will cause errors

    if (this.energyReadings.size() < 2) {
      log.warn("Not enough readings to do analysis with.");
      return Collections.emptySet();
    }

    this.energyReadings.sort(Comparator.comparing(EnergyReading::getDate));

    var spendingRanges = new ArrayList<SpendingRange>();
    var lastReading = this.energyReadings.get(0);
    for (int i = 1; i < this.energyReadings.size(); i++) {
      var currentReading = this.energyReadings.get(i);
      if (currentReading.getReading().compareTo(lastReading.getReading()) > 0) {
        lastReading = currentReading;
        continue;
      }
      spendingRanges.add(
          new SpendingRange(
              lastReading.getDate(),
              currentReading.getDate(),
              (lastReading.getReading().subtract(currentReading.getReading())).abs()
          )
      );
      lastReading = currentReading;

    }
    return spendingRanges;
  }

}
