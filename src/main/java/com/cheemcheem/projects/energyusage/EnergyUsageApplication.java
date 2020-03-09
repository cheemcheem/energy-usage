package com.cheemcheem.projects.energyusage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class EnergyUsageApplication {

  public static void main(String[] args) {
    SpringApplication.run(EnergyUsageApplication.class, args);
  }

}
