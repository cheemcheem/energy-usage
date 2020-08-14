package com.cheemcheem.projects.energyusage.util.mapper;

import com.cheemcheem.projects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.projects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.projects.energyusage.exception.InvalidDateException;
import com.cheemcheem.projects.energyusage.model.SpendingRange;
import com.cheemcheem.projects.energyusage.util.converter.BigDecimalConverter;
import com.cheemcheem.projects.energyusage.util.converter.LocalDateTimeConverter;
import java.math.RoundingMode;

public class SpendingRangeMapper {

  public static SpendingRangeDTO toDTO(SpendingRange source) {

    return new SpendingRangeDTO(
        LocalDateTimeConverter.format(source.getStartDate()),
        LocalDateTimeConverter.format(source.getEndDate()),
        BigDecimalConverter.format(source.getUsage().setScale(2, RoundingMode.HALF_UP))
    );
  }

  public static SpendingRange toModel(SpendingRangeDTO source)
      throws InvalidDateException, InvalidBigDecimalException {
    return new SpendingRange(
        LocalDateTimeConverter.parse(source.getStartDate()),
        LocalDateTimeConverter.parse(source.getEndDate()),
        BigDecimalConverter.parse(source.getUsage())
    );
  }


}
