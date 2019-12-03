package com.cheemcheem.springprojects.energyusage.model;

import com.cheemcheem.springprojects.energyusage.util.importer.CSVDateColumnConverter;
import com.cheemcheem.springprojects.energyusage.util.importer.CSVReadingColumnConverter;
import com.opencsv.bean.CsvCustomBindByPosition;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class EnergyReading implements Comparable<EnergyReading> {

  @CsvCustomBindByPosition(converter = CSVDateColumnConverter.class, position = 0)
  @NonNull
  private Date date;

  @CsvCustomBindByPosition(converter = CSVReadingColumnConverter.class, position = 1)
  @NonNull
  private BigDecimal reading;

  @Override
  public int compareTo(EnergyReading o) {
    return getDate().compareTo(o.getDate());
  }
}
