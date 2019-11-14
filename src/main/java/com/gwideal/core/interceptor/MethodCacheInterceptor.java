package com.gwideal.core.interceptor;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

/**
 * Created by li_hongyu on 2015/06/03.
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean {

  private final static Logger logger = LogManager.getLogger();

  private Cache cache;

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public void afterPropertiesSet() throws Exception {
    logger.debug(cache + " A cache is required. Use setCache(Cache) to provide one.");
  }

  public Object invoke(MethodInvocation invocation) throws Throwable {
    String targetName = invocation.getThis().getClass().getName();
    String methodName = invocation.getMethod().getName();
    Object[] arguments = invocation.getArguments();
    Object result;
    String cacheKey = getCacheKey(targetName, methodName, arguments);
    Element element;
    synchronized (this) {
      element = cache.get(cacheKey);
      if (element == null) {
        logger.debug(cacheKey + "加入到缓存： " + cache.getName());
        // 调用实际的方法
        result = invocation.proceed();
        element = new Element(cacheKey, (Serializable) result);
        cache.put(element);
      } else {
        logger.debug(cacheKey + "使用缓存： " + cache.getName());
      }
    }
    return element.getObjectValue();
  }

  /**
   * <b>function:</b> 返回具体的方法全路径名称 参数
   *
   * @param targetName 全路径
   * @param methodName 方法名称
   * @param arguments  参数
   * @return 完整方法名称
   */
  private String getCacheKey(String targetName, String methodName, Object[] arguments) {
    StringBuilder sb = new StringBuilder();
    sb.append(targetName).append(".").append(methodName);
    if ((arguments != null) && (arguments.length != 0)) {
      for (Object argument : arguments) {
        sb.append(".").append(argument);
      }
    }
    return sb.toString();
  }
}
