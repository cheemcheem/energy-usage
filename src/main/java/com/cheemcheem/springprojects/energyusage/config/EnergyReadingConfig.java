package com.cheemcheem.springprojects.energyusage.config;

import com.cheemcheem.springprojects.energyusage.exception.EmptyRepositoryException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.importer.EnergyReadingsFileReader;
import com.cheemcheem.springprojects.energyusage.util.mapper.SpendingRangeMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
  public Collection<SpendingRange> readCSVRange() throws IOException, EmptyRepositoryException {
    var csvBeanCreator = new EnergyReadingsFileReader(this.csvPath);
    csvBeanCreator.initialise();
    return new HashSet<>(csvBeanCreator.getEnergyReadingsRange());
  }

  @Bean
  public SpendingRangeMapper makeMapper() {
    return new SpendingRangeMapper();
  }

}
