package com.cheemcheem.projects.energyusage.util.importer;

import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequiredArgsConstructor
public class EnergyReadingsFileReader {

  private final Logger logger = LoggerFactory.getLogger(EnergyReadingsFileReader.class);

  @NonNull
  private final String csvPath;
  private String systemResource;

  public void initialise() {
    this.systemResource = ClassLoader.getSystemResource(this.csvPath).getPath();
    logger.debug("Initialised with {} to get {}.", this.csvPath, this.systemResource);
  }

  public Collection<EnergyReading> getEnergyReadings() throws IOException {
    if (!new File(this.systemResource).exists()) {
      logger.warn("No input.csv file found!");
      return Collections.emptySet();
    }
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

  public Collection<SpendingRange> getEnergyReadingsRange() throws IOException {
    // use set to remove duplicates, if this is not done spending ranges with start = end
    // and usage = 0 will occur and will cause errors
    var energyReadings = new ArrayList<>(new HashSet<>(getEnergyReadings()));

    if (energyReadings.size() < 2) {
      logger.warn("Not enough readings to do analysis with.");
      return Collections.emptySet();
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
