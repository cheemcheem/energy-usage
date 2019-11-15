package com.cheemcheem.springprojects.energyusage.util.importers;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVBeanCreator {

  public static List<EnergyReading> getEnergyReadings(String csvPath) throws IOException {
    var systemResource = ClassLoader.getSystemResource(csvPath).getPath();
    var mappingStrategy = new ColumnPositionMappingStrategy<EnergyReading>();
    mappingStrategy.setType(EnergyReading.class);

    var reader = Files.newBufferedReader(Path.of(systemResource));
    var csvToBean = new CsvToBeanBuilder<EnergyReading>(reader)
        .withType(EnergyReading.class)
        .withMappingStrategy(mappingStrategy)
        .build();

    var list = csvToBean.parse();
    reader.close();
    return list;
  }
}
