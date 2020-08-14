package com.cheemcheem.projects.energyusage.config;

import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.util.importer.EnergyReadingsFileReader;
import java.io.IOException;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("csv")
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EnergyReadingConfig {

  @NonNull
  private final String csvPath;

  @Bean
  public User defaultUser() throws IOException {
    log.info("Reading CSV from input csv file.");
    log.debug("CSV path {}", csvPath);
    var csvBeanCreator = new EnergyReadingsFileReader(this.csvPath);
    csvBeanCreator.initialise();
    var energyReadings = csvBeanCreator.getEnergyReadings();

    return User.builder()
        .energyReading(new HashSet<>(energyReadings))
        .userId(0)
        .build();
  }

}
