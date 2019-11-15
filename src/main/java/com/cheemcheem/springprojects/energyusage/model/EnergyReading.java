package com.cheemcheem.springprojects.energyusage.model;

import com.cheemcheem.springprojects.energyusage.util.importers.CSVDateColumnConverter;
import com.cheemcheem.springprojects.energyusage.util.importers.CSVReadingColumnConverter;
import com.opencsv.bean.CsvCustomBindByPosition;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

@Data
public class EnergyReading {

  @CsvCustomBindByPosition(converter = CSVDateColumnConverter.class, position = 0)
  @NonNull
  private Date date;

  @CsvCustomBindByPosition(converter = CSVReadingColumnConverter.class, position = 1)
  @NonNull
  private BigDecimal reading;

  public EnergyReading() {
  }
}
