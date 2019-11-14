package com.gwideal.core.interceptor;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class FileMaxInterceptor implements HandlerInterceptor {
  private long maxSize;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


    if (request != null && ServletFileUpload.isMultipartContent(request)) {
      ServletRequestContext ctx = new ServletRequestContext(request);
      long requestSize = ctx.contentLength();
      if (requestSize > maxSize) {
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
          PrintWriter out = response.getWriter();
          out.print("{\"success\":false,\"message\":\"文件大小超过最大限制"+(maxSize/1024/1024)+"MB!\"}");//session失效
          out.flush();
          return false;
        }
        throw new MaxUploadSizeExceededException(maxSize);
      }
    }
    return true;
  }


  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
  }

  public void setMaxSize(long maxSize) {
    this.maxSize = maxSize;
  }
}
