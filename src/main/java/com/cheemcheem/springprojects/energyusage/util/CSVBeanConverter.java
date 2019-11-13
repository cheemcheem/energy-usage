package com.cheemcheem.springprojects.energyusage.util;

import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVBeanConverter extends AbstractBeanField<EnergyReading> {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  private synchronized Date format(String value) throws ParseException {
    return DATE_FORMAT.parse(value);
  }

  @Override
  protected Object convert(String value) throws CsvDataTypeMismatchException {
    try {
      return format(value);
    } catch (ParseException e) {
      throw new CsvDataTypeMismatchException("Failed to parse date, " + e.getMessage());
    }
  }
}
