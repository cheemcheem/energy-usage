package com.cheemcheem.projects.energyusage.controller;

import com.cheemcheem.projects.energyusage.dto.EnergyReadingDTO;
import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.service.EnergyReadingService;
import com.cheemcheem.projects.energyusage.util.Constants;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reading")
public class ReadingController {

  private final EnergyReadingService energyReadingService;

  @PostMapping("/add")
  public ResponseEntity<Object> addReading(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user,
      @RequestBody EnergyReadingDTO energyReadingDTO
  ) {
    log.info("Adding new reading.");
    log.debug("Adding reading {} to user {}.", energyReadingDTO, user);

    var added = this.energyReadingService.addReading(user, energyReadingDTO);
    if (!added) {
      log.debug("Failed to save reading.");
      return ResponseEntity.badRequest().build();
    }

    log.debug("Saved reading.");
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/all")
  public ResponseEntity<Collection<EnergyReadingDTO>> getReadings(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get all readings.");
    log.info("Getting all readings for user {}.", user);
    return ResponseEntity.ok(this.energyReadingService.convertReadingsForUser(user));
  }

  @GetMapping("/last")
  public ResponseEntity<EnergyReadingDTO> getLastReading(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get last reading.");
    log.info("Getting last reading for user {}.", user);
    return ResponseEntity.ok(this.energyReadingService.getLastReadingForUser(user));
  }
}
