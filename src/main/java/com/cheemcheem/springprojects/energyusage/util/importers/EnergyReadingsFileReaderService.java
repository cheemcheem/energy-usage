package com.cheemcheem.springprojects.energyusage.util.importers;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EnergyReadingsFileReaderService {

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
}
