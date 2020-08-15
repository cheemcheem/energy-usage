package com.cheemcheem.projects.energyusage.service;

import com.cheemcheem.projects.energyusage.dto.EnergyReadingDTO;
import com.cheemcheem.projects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.repository.EnergyReadingRepository;
import com.cheemcheem.projects.energyusage.util.mapper.DTOMapper;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EnergyReadingService {

  private final EnergyReadingRepository energyReadingRepository;

  public boolean addReading(User user, EnergyReadingDTO energyReadingDTO) {
    log.debug("EnergyReadingService.addReading");
    log.debug("Creating energy reading from request body {}.", energyReadingDTO);

    EnergyReading energyReading;
    try {
      energyReading = DTOMapper.toEnergyReadingModel(energyReadingDTO);
    } catch (InvalidDateException | InvalidBigDecimalException e) {
      log.warn("Failed to parse request body. Reason: {}", e.getMessage(), e);
      return false;
    }
    log.debug("Created energy reading {}. Adding user {}.", energyReading, user);

    energyReading.setUser(user);
    log.debug("Added user to energy reading {}. Saving in repository.", energyReading);

    var saved = this.energyReadingRepository.save(energyReading);
    log.debug("Saved energy reading in repository {}.", saved);

    return true;
  }

  public Collection<EnergyReadingDTO> convertReadingsForUser(User user) {
    log.info("EnergyReadingService.convertReadingsForUser");
    log.debug("Converting energy readings in user {}.", user);

    var converted = user.getEnergyReadings()
        .stream()
        .sorted()
        .map(DTOMapper::toEnergyReadingDTO)
        .collect(Collectors.toList());
    log.debug("Converted energy readings: {}.", converted);

    return converted;
  }

}
