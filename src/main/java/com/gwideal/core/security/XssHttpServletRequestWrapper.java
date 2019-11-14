package com.gwideal.core.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by farno on 2016/9/23.
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final static Logger logger = LogManager.getLogger();
  private String excludeParams = "content";

  public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
    super(servletRequest);
  }

  public String[] getParameterValues(String parameter) {
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
      return null;
    }
    if(excludeParams.contains(parameter)){
      return  values;
    }
    int count = values.length;
    String[] encodedValues = new String[count];
    for (int i = 0; i < count; i++) {
      encodedValues[i] = cleanXSS(values[i]);
    }
    return encodedValues;
  }

  /*public String getServletPath() {
    //super.getServletPath()
    String value = super.getServletPath();
    if (value == null) {
      return null;
    }
    return cleanXSS(value);
  }*/

  public String getQueryString() {
    String value = super.getQueryString();
    if (value == null) {
      return null;
    }
    return cleanXSS(value);
  }

  public String getParameter(String parameter) {
    String value = super.getParameter(parameter);
    if (value == null) {
      return null;
    }
    return cleanXSS(value);
  }

  public String getHeader(String name) {
    String value = super.getHeader(name);
    if (value == null)
      return null;
    return cleanXSS(value);
  }

  public static String cleanXSS(String value) {
    //You'll need to remove the spaces from the html entities below
    //logger.debug("before sql inject interceptor :{}", value);
    value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
    value = value.replaceAll("'", "&#39;");
    value = value.replaceAll("eval\\((.*)\\)", "");
    value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
    value = value.replaceAll("script", "");
    value = value.replaceAll("(?i)(char|mid|union|from|truncate|select|insert"
        + "|drop|call|script|focus|document|cookie|write|frame|iframe|javascript|window|open|classLoader|" +
        "docBase|prompt|alert|confirm|onmouse|onerror|onclick|onload)+", "");
    //logger.debug("after Xss filter fired :{}", value);
    StringBuilder sb = new StringBuilder(value.length() + 16);
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '>':
          sb.append("＞");// 转义大于号
          break;
        case '<':
          sb.append("＜");// 转义小于号
          break;
        case '\'':
          sb.append("＇");// 转义单引号
          break;
        case '\"':
          sb.append("＂");// 转义双引号
          break;
        default:
          sb.append(c);
          break;
      }
    }

    return sb.toString();
  }
}
