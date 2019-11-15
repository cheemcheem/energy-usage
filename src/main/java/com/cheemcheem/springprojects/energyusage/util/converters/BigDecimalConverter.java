package com.cheemcheem.springprojects.energyusage.util.converters;

import java.math.BigDecimal;

public class BigDecimalConverter {

  public static BigDecimal parse(String value) {
    return new BigDecimal(value);
  }

  public static String format(BigDecimal value) {
    return value.toString();
  }
}
