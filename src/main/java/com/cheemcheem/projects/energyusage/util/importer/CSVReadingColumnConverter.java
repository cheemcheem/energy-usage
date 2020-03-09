package com.cheemcheem.projects.energyusage.util.importer;

import com.cheemcheem.projects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.util.converter.BigDecimalConverter;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.math.BigDecimal;

public class CSVReadingColumnConverter extends AbstractBeanField<EnergyReading> {

  @Override
  protected Object convert(String value) throws CsvDataTypeMismatchException {
    try {
      return BigDecimalConverter.parse(value);
    } catch (InvalidBigDecimalException e) {
      throw new CsvDataTypeMismatchException(value, BigDecimal.class, e.getMessage());
    }
  }

}
