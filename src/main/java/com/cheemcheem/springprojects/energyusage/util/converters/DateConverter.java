package com.cheemcheem.springprojects.energyusage.util.converters;

import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

  public static final String PATTERN = "dd/MM/yyyy HH:mm";
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

  public synchronized static Date parse(String value) {
    try {
      return DateConverter.DATE_FORMAT.parse(value);
    } catch (ParseException cause) {
      throw new InvalidDateException(value, cause);
    }
  }

  public synchronized static String format(Date value) {
    return DateConverter.DATE_FORMAT.format(value);
  }
}
