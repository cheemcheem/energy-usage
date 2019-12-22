package com.cheemcheem.springprojects.energyusage.util.mapper;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.converter.BigDecimalConverter;
import com.cheemcheem.springprojects.energyusage.util.converter.LocalDateTimeConverter;
import java.math.RoundingMode;

public class SpendingRangeMapper {

  public SpendingRangeDTO toDTO(SpendingRange source) {

    return new SpendingRangeDTO(
        LocalDateTimeConverter.format(source.getStartDate()),
        LocalDateTimeConverter.format(source.getEndDate()),
        BigDecimalConverter.format(source.getUsage().setScale(2, RoundingMode.HALF_UP))
    );
  }

  public SpendingRange toModel(SpendingRangeDTO source)
      throws InvalidDateException, InvalidBigDecimalException {
    return new SpendingRange(
        LocalDateTimeConverter.parse(source.getStartDate()),
        LocalDateTimeConverter.parse(source.getEndDate()),
        BigDecimalConverter.parse(source.getUsage())
    );
  }


}
