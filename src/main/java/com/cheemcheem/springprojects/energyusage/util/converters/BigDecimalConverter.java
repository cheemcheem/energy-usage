package com.cheemcheem.springprojects.energyusage.util.converters;

import com.cheemcheem.springprojects.energyusage.exception.InvalidBigDecimalException;
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
