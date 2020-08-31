package com.cheemcheem.projects.energyusage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("!csv")
@RequiredArgsConstructor
@Configuration
public class AuthInterceptorConfiguration implements WebMvcConfigurer {

  private final AuthInterceptor authInterceptor;
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor);
  }

}
