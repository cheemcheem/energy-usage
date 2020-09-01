package com.cheemcheem.projects.energyusage.util.importer;

import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnergyReadingsFileReader {

  @NonNull
  private final String csvPath;
  private String systemResource;

  public void initialise() {
    this.systemResource = ClassLoader.getSystemResource(this.csvPath).getPath();
    log.debug("Initialised with {} to get {}.", this.csvPath, this.systemResource);
  }

  public List<EnergyReading> getEnergyReadings() throws IOException {
    if (!new File(this.systemResource).exists()) {
      log.warn("No input.csv file found!");
      return Collections.emptyList();
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

    var list = List.copyOf(csvToBean.parse());
    reader.close();
    return list;
  }

}
