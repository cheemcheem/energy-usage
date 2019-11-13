package com.cheemcheem.springprojects.energyusage.service;

import com.cheemcheem.springprojects.energyusage.repository.EnergyReadingRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnergyReadingService {

  @NonNull
  private final EnergyReadingRepository energyReadingRepository;
}
