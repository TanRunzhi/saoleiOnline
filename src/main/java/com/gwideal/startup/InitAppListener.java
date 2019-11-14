package com.gwideal.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

@Component
public class InitAppListener implements InitializingBean, ServletContextAware {

  private final static Logger logger = LogManager.getLogger();

  @Override
  public void afterPropertiesSet() throws Exception {
    logger.info("startup 1 fired");
  }

  @Override
  public void setServletContext(ServletContext servletContext) {
    logger.info("startup 2 fired");
  }
}
