package com.cheemcheem.springprojects.energyusage.util.importer;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EnergyReadingsFileReader {

  @NonNull
  private final String csvPath;
  private String systemResource;

  public void initialise() {
    this.systemResource = ClassLoader.getSystemResource(this.csvPath).getPath();
  }

  public Collection<EnergyReading> getEnergyReadings() throws IOException {
    if (this.systemResource == null) {
      throw new IllegalStateException(
          "This instance has not been initialised successfully with initialise().");
    }
    var mappingStrategy = new ColumnPositionMappingStrategy<EnergyReading>();
    mappingStrategy.setType(EnergyReading.class);

    var reader = Files.newBufferedReader(Path.of(this.systemResource));
    var csvToBean = new CsvToBeanBuilder<EnergyReading>(reader)
        .withType(EnergyReading.class)
        .withMappingStrategy(mappingStrategy)
        .build();

    var list = csvToBean.parse();
    reader.close();
    return list;
  }

  public Collection<SpendingRange> getEnergyReadingsRange()
      throws IOException, EmptyRepositoryException {
    // use set to remove duplicates, if this is not done spending ranges with start = end
    // and usage = 0 will occur and will cause errors
    var energyReadings = new ArrayList<>(new HashSet<>(getEnergyReadings()));

    if (energyReadings.size() < 2) {
      throw new EmptyRepositoryException("Not enough readings to do analysis with.");
    }

    energyReadings.sort(Comparator.comparing(EnergyReading::getDate));

    var spendingRanges = new ArrayList<SpendingRange>();
    var lastReading = energyReadings.get(0);
    for (int i = 1; i < energyReadings.size(); i++) {
      var currentReading = energyReadings.get(i);
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
