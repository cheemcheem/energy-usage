package com.cheemcheem.projects.energyusage.controller;

import com.cheemcheem.projects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.User;
import com.cheemcheem.projects.energyusage.service.EnergyUsageService;
import com.cheemcheem.projects.energyusage.util.Constants;
import com.cheemcheem.projects.energyusage.util.converter.LocalDateTimeConverter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EnergyUsageController {

  private final EnergyUsageService energyUsageService;

  @GetMapping("/spending/all")
  public SpendingRangeDTO getAllSpending(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get all spending.");
    return energyUsageService.getAllSpending(user);
  }

  @GetMapping("/spending/from")
  public ResponseEntity<SpendingRangeDTO> getSpendingFrom(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get spending from '" + startDate + "'.");
    return ResponseEntity.ok(energyUsageService.getSpendingFrom(startDate, user));
  }

  @GetMapping("/spending/to")
  public ResponseEntity<SpendingRangeDTO> getSpendingTo(
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get spending to '" + endDate + "'.");
    return ResponseEntity.ok(energyUsageService.getSpendingTo(endDate, user));
  }


  @GetMapping("/spending/between")
  public ResponseEntity<SpendingRangeDTO> getSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get spending from '" + startDate + "' to '" + endDate + "'.");
    try {
      return ResponseEntity
          .ok(energyUsageService.getSpendingBetween(startDate, endDate, user));
    } catch (InvalidDateException e) {
      log.warn(e.getMessage());
      return ResponseEntity.badRequest().build();
    }

  }

  @GetMapping("/average/between")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingBetween(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get average spending from '" + startDate + "' to '" + endDate + "' over '" + 7
        + "' day periods.");
    try {
      return ResponseEntity
          .ok(energyUsageService.getAverageSpending(startDate, endDate, 7, user));
    } catch (InvalidDateException e) {
      log.warn(e.getMessage());
      return ResponseEntity.badRequest().build();
    }

  }

  @GetMapping("/average/all")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageAll(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get average spending over all days over '" + 7 + "' day periods.");
    return ResponseEntity.ok(energyUsageService.getAverageSpending(7, user));

  }


  @GetMapping("/average/daily")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingDaily(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get average daily spending from '" + startDate + "' to '" + endDate + "'.");
    try {
      return ResponseEntity
          .ok(energyUsageService.getAverageSpendingBetweenDaily(startDate, endDate, user));
    } catch (InvalidDateException e) {
      log.warn(e.getMessage());
      return ResponseEntity.badRequest().build();
    }

  }

  @GetMapping("/average/daily/all")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingDailyAll(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get average daily spending over all days.");
    return ResponseEntity.ok(energyUsageService.getAverageSpendingBetweenDaily(user));
  }

  @GetMapping("/average/weekly")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingWeekly(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get average weekly spending from '" + startDate + "' to '" + endDate + "'.");
    try {
      return ResponseEntity
          .ok(energyUsageService.getAverageSpendingBetweenWeekly(startDate, endDate, user));
    } catch (InvalidDateException e) {
      log.warn(e.getMessage());
      return ResponseEntity.badRequest().build();
    }

  }

  @GetMapping("/average/weekly/all")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingWeeklyAll(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get average weekly spending over all days.");
    return ResponseEntity.ok(energyUsageService.getAverageSpendingBetweenWeekly(user));
  }

  @GetMapping("/average/monthly")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingMonthly(
      @RequestParam("startDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime startDate,
      @RequestParam("endDate") @DateTimeFormat(pattern = LocalDateTimeConverter.PATTERN) LocalDateTime endDate,
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user
  ) {
    log.info("Get average monthly spending from '" + startDate + "' to '" + endDate + "'.");
    try {
      return ResponseEntity
          .ok(energyUsageService.getAverageSpendingBetweenMonthly(startDate, endDate, user));
    } catch (InvalidDateException e) {
      log.warn(e.getMessage());
      return ResponseEntity.badRequest().build();
    }

  }

  @GetMapping("/average/monthly/all")
  public ResponseEntity<List<SpendingRangeDTO>> getAverageSpendingMonthlyAll(
      @RequestAttribute(Constants.USER_ID_ATTRIBUTE_KEY) User user) {
    log.info("Get average monthly spending over all days.");
    return ResponseEntity.ok(energyUsageService.getAverageSpendingBetweenMonthly(user));
  }


}
