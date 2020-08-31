package com.cheemcheem.projects.energyusage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class EnergyUsageApplication
    extends WebSecurityConfigurerAdapter {

  public static void main(String[] args) {
    SpringApplication.run(EnergyUsageApplication.class, args);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        .authorizeRequests(a -> a
            .antMatchers("/**/*.{js,html,css}").permitAll()
            .antMatchers("/", "/error").permitAll()
            .anyRequest().authenticated()
        )
        .logout(l -> l
            .logoutSuccessUrl("/").permitAll()
        )
        .csrf(c -> c
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
//        .exceptionHandling(e -> e
//            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//        )
        .oauth2Login();
//    http
//        .oauth2Login()
//          .and()
//        .csrf()
//          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//          .and()
//        .authorizeRequests()
//          .antMatchers("/**/*.{js,html,css}").permitAll()
//          .antMatchers("/", "/api/user").permitAll()
//          .anyRequest().authenticated();
    // @formatter:on
  }

  @Profile("local")
  @Bean
  public RequestCache refererRequestCache() {
    return new HttpSessionRequestCache() {
      @Override
      public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String referrer = request.getHeader("referer");
        if (referrer != null) {
          request.getSession()
              .setAttribute("SPRING_SECURITY_SAVED_REQUEST", new SimpleSavedRequest(referrer));
        }
      }
    };
  }
}
