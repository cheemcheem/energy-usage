package com.cheemcheem.projects.energyusage.util.mapper;

import com.cheemcheem.projects.energyusage.dto.EnergyReadingDTO;
import com.cheemcheem.projects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.projects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.EnergyReading;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.util.converter.BigDecimalConverter;
import com.cheemcheem.projects.energyusage.util.converter.LocalDateTimeConverter;
import java.math.RoundingMode;

public class DTOMapper {

  public static SpendingRangeDTO toSpendingRangeDTO(SpendingRange source) {
    return new SpendingRangeDTO(
        LocalDateTimeConverter.formatISO(source.getStartDate()),
        LocalDateTimeConverter.formatISO(source.getEndDate()),
        BigDecimalConverter.format(source.getUsage().setScale(2, RoundingMode.HALF_UP))
    );
  }

  public static EnergyReading toEnergyReadingModel(EnergyReadingDTO source)
      throws InvalidDateException, InvalidBigDecimalException {
    return new EnergyReading(
        LocalDateTimeConverter.parseISO(source.getDateISO()),
        BigDecimalConverter.parse(source.getReading())
    );
  }

  public static EnergyReadingDTO toEnergyReadingDTO(EnergyReading source) {
    return new EnergyReadingDTO(
        LocalDateTimeConverter.formatISO(source.getDate()),
        BigDecimalConverter.format(source.getReading())
    );
  }


}
