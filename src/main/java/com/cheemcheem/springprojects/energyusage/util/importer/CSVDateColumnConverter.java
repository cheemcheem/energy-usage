package com.cheemcheem.springprojects.energyusage.util.importer;

import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.model.EnergyReading;
import com.cheemcheem.springprojects.energyusage.util.converter.LocalDateTimeConverter;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.util.Date;

public class CSVDateColumnConverter extends AbstractBeanField<EnergyReading> {

  @Override
  protected Object convert(String value) throws CsvDataTypeMismatchException {
    try {
      return LocalDateTimeConverter.parse(value);
    } catch (InvalidDateException e) {
      throw new CsvDataTypeMismatchException(value, Date.class, e.getMessage());
    }
  }
}
