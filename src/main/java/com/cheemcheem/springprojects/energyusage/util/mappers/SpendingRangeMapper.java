package com.cheemcheem.springprojects.energyusage.util.mappers;

import com.cheemcheem.springprojects.energyusage.dto.SpendingRangeDTO;
import com.cheemcheem.springprojects.energyusage.exception.InvalidBigDecimalException;
import com.cheemcheem.springprojects.energyusage.exception.InvalidDateException;
import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import com.cheemcheem.springprojects.energyusage.util.converters.BigDecimalConverter;
import com.cheemcheem.springprojects.energyusage.util.converters.DateConverter;
import java.math.RoundingMode;

public class SpendingRangeMapper {

  public SpendingRangeDTO toDTO(SpendingRange source) {

    return new SpendingRangeDTO(
        DateConverter.format(source.getStartDate()),
        DateConverter.format(source.getEndDate()),
        BigDecimalConverter.format(source.getUsage().setScale(2, RoundingMode.HALF_UP))
    );
  }

  public SpendingRange toModel(SpendingRangeDTO source)
      throws InvalidDateException, InvalidBigDecimalException {
    return new SpendingRange(
        DateConverter.parse(source.getStartDate()),
        DateConverter.parse(source.getEndDate()),
        BigDecimalConverter.parse(source.getUsage())
    );
  }


}
