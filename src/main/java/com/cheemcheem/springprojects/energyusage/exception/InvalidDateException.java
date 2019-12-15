package com.cheemcheem.springprojects.energyusage.exception;

import java.util.Date;

public class InvalidDateException extends Exception {

  private InvalidDateException(String message) {
    super(message);
  }

  public InvalidDateException(String invalidDate, Throwable cause) {
    super("Failed to parse '" + invalidDate + "' as Date. Reason: " + cause.getMessage() + ".");
  }

  public static InvalidDateException wrongWayAround(Date invalidStartDate, Date invalidEndDate) {
    return new InvalidDateException(
        "Start date '" + invalidStartDate + "' occurs after end date '" + invalidEndDate + "'."
    );
  }
}
