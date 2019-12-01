package com.cheemcheem.springprojects.energyusage.repository;

import com.cheemcheem.springprojects.energyusage.model.SpendingRange;
import java.util.Collection;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class SpendingRangeRepository {

  @NonNull
  private Collection<SpendingRange> spendingRanges;
}
