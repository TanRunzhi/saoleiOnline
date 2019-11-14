package com.gwideal.core.interceptor;

import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.SysConfigMng;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by li_hongyu on 14-7-22.
 */
public class AdminInterceptor implements HandlerInterceptor {

  private static final Logger logger = LogManager.getLogger();

  @Resource
  private SysConfigMng sysConfigMng;

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception {
    logger.debug("admin interceptor fired");
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
    SysUser user = (SysUser) httpServletRequest.getSession().getAttribute("currentUser");
    String whitelist = sysConfigMng.getCacheValue("sys.manager.whitelist");
    if (user == null) {
      logger.debug("login timeout...");
      response.sendRedirect("/login.htm");
    } else if (StringUtils.isEmpty(whitelist) || !whitelist.contains(user.getAccount())) {
      response.sendRedirect("/notAdmin.htm");
    } else {
      logger.debug("[" + user.getRealName() + "] fired " + httpServletRequest.getRequestURI());
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) throws Exception {

  }
}
