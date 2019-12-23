package com.cheemcheem.springprojects.energyusage.config;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.util.importers.EnergyReadingsFileReaderService;
import com.cheemcheem.springprojects.energyusage.util.mappers.SpendingRangeMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EnergyReadingConfig {

  @NonNull
  private final String csvPath;

  @Bean
  public Collection<EnergyReading> readCSV() throws IOException {
    if (!new File(csvPath).exists()) {
      return Collections.emptySet();
    }
    var csvBeanCreator = new EnergyReadingsFileReaderService(this.csvPath);
    csvBeanCreator.initialise();
    return new TreeSet<>(csvBeanCreator.getEnergyReadings());
  }

  @Bean
  public SpendingRangeMapper makeMapper() {
    return new SpendingRangeMapper();
  }

}
