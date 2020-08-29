package com.cheemcheem.projects.energyusage.model;

import com.cheemcheem.projects.energyusage.util.importer.CSVDateColumnConverter;
import com.cheemcheem.projects.energyusage.util.importer.CSVReadingColumnConverter;
import com.opencsv.bean.CsvCustomBindByPosition;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"user"})
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class EnergyReading implements Comparable<EnergyReading> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int energyReadingId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @CsvCustomBindByPosition(converter = CSVDateColumnConverter.class, position = 0)
  @NonNull
  private LocalDateTime date;

  @CsvCustomBindByPosition(converter = CSVReadingColumnConverter.class, position = 1)
  @NonNull
  private BigDecimal reading;

  @Override
  public int compareTo(EnergyReading o) {
    return getDate().compareTo(o.getDate());
  }

}
