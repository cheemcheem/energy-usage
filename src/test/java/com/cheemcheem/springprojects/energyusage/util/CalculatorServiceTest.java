package com.cheemcheem.springprojects.energyusage.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheemcheem.springprojects.energyusage.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CalculatorServiceTest {

  @Autowired
  private CalculatorService calculatorService;

  @Test
  void contextLoads() {
    assertThat(calculatorService).isNotNull();
  }

}