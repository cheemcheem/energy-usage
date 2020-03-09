package com.cheemcheem.projects.energyusage.config;

import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.util.importer.EnergyReadingsFileReader;
import com.cheemcheem.projects.energyusage.util.mapper.SpendingRangeMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EnergyReadingConfig {

  private final Logger logger = LoggerFactory.getLogger(EnergyReadingConfig.class);
  @NonNull
  private final String csvPath;

  @Bean
  public Collection<SpendingRange> readCSVRange() throws IOException {
    logger.info("Reading CSV from input csv file.");
    logger.debug("CSV path {}", csvPath);
    var csvBeanCreator = new EnergyReadingsFileReader(this.csvPath);
    csvBeanCreator.initialise();
    return new HashSet<>(csvBeanCreator.getEnergyReadingsRange());
  }

  @Bean
  public SpendingRangeMapper makeMapper() {
    logger.info("Generating new Spending Range Mapper.");
    return new SpendingRangeMapper();
  }

}
