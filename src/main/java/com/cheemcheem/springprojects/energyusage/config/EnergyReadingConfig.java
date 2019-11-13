package com.cheemcheem.springprojects.energyusage.config;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EnergyReadingConfig {

  private final String csvPath;

  @Bean(name = "csvList")
  public List<EnergyReading> readCSV() throws IOException {
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
