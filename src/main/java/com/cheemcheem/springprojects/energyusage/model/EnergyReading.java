package com.cheemcheem.springprojects.energyusage.model;

import com.cheemcheem.springprojects.energyusage.util.CSVBeanConverter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

@Data
public class EnergyReading {

  @CsvCustomBindByPosition(converter = CSVBeanConverter.class, position = 0)
  @NonNull
  private Date date;

  @CsvBindByPosition(position = 1)
  @NonNull
  private BigDecimal reading;

  public EnergyReading() {
  }
}
