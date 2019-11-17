package com.cheemcheem.springprojects.energyusage.controller;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.service.EnergyReadingService;
import com.cheemcheem.springprojects.energyusage.util.converters.DateConverter;
import java.util.Date;
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
  private final EnergyReadingService energyReadingService;

  @GetMapping("/spending/all")
  public SpendingRangeDTO getAllSpending() {
    logger.info("EnergyUsageController.getAllSpending");
    return energyReadingService.getAllSpending();
  }

  @GetMapping("/spending/from")
  public ResponseEntity getSpendingFrom(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate
  ) {
    logger.info("EnergyUsageController.getSpendingFrom");
    return ResponseEntity.ok(energyReadingService.getSpendingFrom(startDate));

  }

  @GetMapping("/spending/to")
  public ResponseEntity getSpendingTo(
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("EnergyUsageController.getSpendingTo");
    return ResponseEntity.ok(energyReadingService.getSpendingTo(endDate));

  }


  @GetMapping("/spending/between")
  public ResponseEntity getSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("EnergyUsageController.getSpendingTo");
    return ResponseEntity.ok(energyReadingService.getSpendingBetween(startDate, endDate));

  }

  @GetMapping("/average/between")
  public ResponseEntity getAverageSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = DateConverter.PATTERN) Date endDate
  ) {
    logger.info("EnergyUsageController.getAverageSpendingBetween");
    return ResponseEntity.ok(energyReadingService.getAverageSpending(startDate, endDate, 7));

  }


}
