package com.gwideal.core.interceptor;

import com.gwideal.core.entity.SysUser;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by li_hongyu on 14-7-22.
 */
public class AuthInterceptor implements HandlerInterceptor {

  private static final Logger logger = LogManager.getLogger();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
    SysUser user = (SysUser) request.getSession().getAttribute("currentUser");
    if (user == null) {
      logger.info("Interceptor：跳转到login页面！");
      response.sendRedirect("/welcome.htm");
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) throws Exception {

  }
}
