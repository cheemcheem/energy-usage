package com.cheemcheem.projects.energyusage.config.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("csv")
@EnableWebMvc
@RequiredArgsConstructor
@Configuration
public class CSVInterceptorConfiguration implements WebMvcConfigurer {

  private final CSVInterceptor csvInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(csvInterceptor);
  }
}
