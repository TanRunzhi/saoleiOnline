package com.gwideal.core.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by farno on 2016/9/23.
 */
public class XssFilter implements Filter {
  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
    chain.doFilter(new XssHttpServletRequestWrapper(
        (HttpServletRequest) req), resp);
  }

  public void init(FilterConfig config) throws ServletException {

  }
}
