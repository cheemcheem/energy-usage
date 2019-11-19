package com.cheemcheem.springprojects.energyusage.exception;

public class InvalidDateException extends Exception {

  public InvalidDateException(String invalidDate, Throwable cause) {
    super("Failed to parse '" + invalidDate + "' as Date. Reason: " + cause.getMessage() + ".");
  }
}
