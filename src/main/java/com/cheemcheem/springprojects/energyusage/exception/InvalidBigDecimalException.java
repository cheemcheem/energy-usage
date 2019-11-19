package com.cheemcheem.springprojects.energyusage.exception;

public class InvalidBigDecimalException extends Exception {

  public InvalidBigDecimalException(String invalidBigDecimal, Throwable cause) {
    super("Failed to parse '" + invalidBigDecimal + "' as BigDecimal. Reason: " + cause.getMessage()
        + ".");
  }
}
