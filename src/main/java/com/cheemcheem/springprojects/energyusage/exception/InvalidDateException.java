package com.cheemcheem.springprojects.energyusage.exception;

public class InvalidDateException extends RuntimeException {

  public InvalidDateException(String invalidDate, Throwable cause) {
    super("Failed to parse date '" + invalidDate + "'. Reason: " + cause.getMessage() + ".");
  }
}
