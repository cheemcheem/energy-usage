package com.cheemcheem.projects.energyusage.config;

import com.cheemcheem.projects.energyusage.util.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    log.debug("AuthInterceptor.preHandle");
    log.debug(request.getServletPath());
    return Constants.DO_NOT_INTERCEPT.test(request.getServletPath());

    // code that adds user id to request attributes from request session
  }
}
