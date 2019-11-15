package com.cheemcheem.springprojects.energyusage.exception;

public class InvalidBigDecimalException extends RuntimeException {

  public InvalidBigDecimalException(String invalidBigDecimal, Throwable cause) {
    super("Failed to parse big decimal '" + invalidBigDecimal + "'. Reason: " + cause.getMessage()
        + ".");
  }
}
