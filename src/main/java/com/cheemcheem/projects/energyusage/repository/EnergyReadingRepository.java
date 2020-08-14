package com.cheemcheem.projects.energyusage.repository;

import com.cheemcheem.projects.energyusage.model.EnergyReading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyReadingRepository extends JpaRepository<EnergyReading, Integer> {

}
