package com.cheemcheem.projects.energyusage.config;

import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.util.importer.EnergyReadingsFileReader;
import com.cheemcheem.projects.energyusage.util.mapper.SpendingRangeMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EnergyReadingConfig {

  @NonNull
  private final String csvPath;

  @Bean
  public Collection<SpendingRange> readCSVRange() throws IOException {
    log.info("Reading CSV from input csv file.");
    log.debug("CSV path {}", csvPath);
    var csvBeanCreator = new EnergyReadingsFileReader(this.csvPath);
    csvBeanCreator.initialise();
    return new HashSet<>(csvBeanCreator.getEnergyReadingsRange());
  }

  @Bean
  public SpendingRangeMapper makeMapper() {
    log.info("Generating new Spending Range Mapper.");
    return new SpendingRangeMapper();
  }

}
