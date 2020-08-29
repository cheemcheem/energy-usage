package com.cheemcheem.projects.energyusage.util.importer;

import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.util.converter.LocalDateTimeConverter;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.util.Date;

public class CSVDateColumnConverter extends AbstractBeanField<EnergyReading> {

  @Override
  protected Object convert(String value) throws CsvDataTypeMismatchException {
    try {
      return LocalDateTimeConverter.parseCSV(value);
    } catch (InvalidDateException e) {
      throw new CsvDataTypeMismatchException(value, Date.class, e.getMessage());
    }
  }
}
