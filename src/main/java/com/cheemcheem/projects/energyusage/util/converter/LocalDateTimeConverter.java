package com.cheemcheem.projects.energyusage.util.converter;

import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter {

  public static final String CSV_PATTERN = "dd/MM/yyyy HH:mm";
  public static final String ISO_PATTERN = "yyyy-MM-dd HH:mm:ss";
  private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern(CSV_PATTERN);
  private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern(ISO_PATTERN);

  public synchronized static LocalDateTime parseCSV(String value) throws InvalidDateException {
    try {
      return LocalDateTime.parse(value, CSV_DATE_FORMAT);
    } catch (DateTimeParseException cause) {
      throw new InvalidDateException(value, cause);
    }
  }

  public synchronized static String formatCSV(LocalDateTime value) {
    return CSV_DATE_FORMAT.format(value);
  }

  public synchronized static LocalDateTime parseISO(String value) throws InvalidDateException {
    try {
      return LocalDateTime.parse(value, ISO_DATE_FORMAT);
    } catch (DateTimeParseException cause) {
      throw new InvalidDateException(value, cause);
    }
  }

  public synchronized static String formatISO(LocalDateTime value) {
    return ISO_DATE_FORMAT.format(value);
  }
}
