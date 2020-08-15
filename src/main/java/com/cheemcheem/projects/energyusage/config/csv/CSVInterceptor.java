package com.cheemcheem.projects.energyusage.config.csv;

import com.cheemcheem.projects.energyusage.repository.UserRepository;
import com.cheemcheem.projects.energyusage.util.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class CSVInterceptor implements HandlerInterceptor {

  private final UserRepository userRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    log.debug("CSVInterceptor.preHandle");
    if (Constants.DO_NOT_INTERCEPT.test(request.getServletPath())) {
      return true;
    }

    var defaultUserId = 1;
    var optionalUser = userRepository.findById(defaultUserId);
    if (optionalUser.isEmpty()) {
      log.debug("Failed to get default user.");
      return false;
    }
    request.setAttribute(Constants.USER_ID_ATTRIBUTE_KEY, optionalUser.get());
    return true;
  }
}
