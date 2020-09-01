package com.cheemcheem.projects.energyusage.config.csv;

import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.projects.energyusage.repository.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("csv")
public class CSVUserConfiguration implements org.springframework.boot.CommandLineRunner {

  private final UserRepository userRepository;
  private final EnergyReadingRepository energyReadingRepository;
  private final User defaultUser;

  @Override
  public void run(String... args) {
    log.debug("RegisterDefaultUser.run");

    log.debug("Converting default user {}.", this.defaultUser);
    var user = User.builder().userId(this.defaultUser.getUserId()).build();

    log.debug("Saving converted user {}.", user);
    var savedUser = this.userRepository.save(user);
    log.info("Saved converted user {}.", savedUser);

    log.debug("Saving default readings {}.", this.defaultUser.getEnergyReadings());
    var savedEnergy = this.energyReadingRepository.saveAll(this.defaultUser.getEnergyReadings()
        .stream()
        .peek(energyReading -> energyReading.setUser(savedUser))
        .collect(Collectors.toList())
    );
    log.debug("Saved energy readings {}.", savedEnergy);
  }
}
