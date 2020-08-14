package com.cheemcheem.projects.energyusage.util.converter;

import com.cheemcheem.projects.energyusage.exception.InvalidBigDecimalException;
import java.math.BigDecimal;

public class BigDecimalConverter {

  public static BigDecimal parse(String value) throws InvalidBigDecimalException {
    try {
      return new BigDecimal(value);
    } catch (NumberFormatException cause) {
      throw new InvalidBigDecimalException(value, cause);
    }
  }

  public static String format(BigDecimal value) {
    return value.toString();
  }
}
