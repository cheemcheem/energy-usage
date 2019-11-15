package com.cheemcheem.springprojects.energyusage.config;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.util.importers.CSVBeanCreator;
import com.cheemcheem.springprojects.energyusage.util.mappers.SpendingRangeMapper;
import java.io.IOException;
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
    return CSVBeanCreator.getEnergyReadings(csvPath);
  }

  @Bean(name = "spendingRangeMapper")
  public SpendingRangeMapper makeMapper() {
    return new SpendingRangeMapper();
  }
}
