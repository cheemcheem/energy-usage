package com.cheemcheem.springprojects.energyusage.controller;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.service.EnergyUsageService;
import com.cheemcheem.springprojects.energyusage.util.converters.DateConverter;
import java.util.Date;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EnergyUsageController {

  private final Logger logger = LoggerFactory.getLogger(EnergyUsageController.class);
  @NonNull
  private final EnergyUsageService energyUsageService;

  @GetMapping("/spending/all")
  public SpendingRangeDTO getAllSpending() {
    logger.info("Get all spending.");
    return energyUsageService.getAllSpending();
  }

  @GetMapping("/spending/from")
  public ResponseEntity<SpendingRangeDTO> getSpendingFrom(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate
  ) {
    logger.info("Get spending from '" + startDate + "'.");
    return ResponseEntity.ok(energyUsageService.getSpendingFrom(startDate));

  }

  @GetMapping("/spending/to")
  public ResponseEntity<SpendingRangeDTO> getSpendingTo(
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("Get spending to '" + endDate + "'.");
    return ResponseEntity.ok(energyUsageService.getSpendingTo(endDate));

  }


  @GetMapping("/spending/between")
  public ResponseEntity<SpendingRangeDTO> getSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("Get spending from '" + startDate + "' to '" + endDate + "'.");
    return ResponseEntity.ok(energyUsageService.getSpendingBetween(startDate, endDate));

  }

  @GetMapping("/average/between")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + 7
        + "' day periods.");
    return ResponseEntity.ok(energyUsageService.getAverageSpending(startDate, endDate, 7));

  }

  @GetMapping("/average/all")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageAll() {
    logger.info("Get average spending over all days over " + 7 + "' day periods.");
    return ResponseEntity.ok(energyUsageService.getAverageSpending(7));

  }


}
