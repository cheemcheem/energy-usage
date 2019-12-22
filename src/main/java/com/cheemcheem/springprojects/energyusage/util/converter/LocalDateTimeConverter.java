package com.cheemcheem.springprojects.energyusage.util.converter;

import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter {

  public static final String PATTERN = "dd/MM/yyyy HH:mm";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(PATTERN);

  public synchronized static LocalDateTime parse(String value) throws InvalidDateException {
    try {
      return LocalDateTime.parse(value, DATE_FORMAT);
    } catch (DateTimeParseException cause) {
      throw new InvalidDateException(value, cause);
    }
  }

  public synchronized static String format(LocalDateTime value) {
    return DATE_FORMAT.format(value);
  }
}
