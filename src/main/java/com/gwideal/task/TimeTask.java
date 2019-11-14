package com.gwideal.task;

import com.gwideal.core.manager.SysConfigMng;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhao_zhiyuan on 2016/12/19.
 */
public class TimeTask {
  private final static Logger logger = LogManager.getLogger();

  public void timeJob() {
    logger.info("定时任务");
  }

  @Resource
  private SysConfigMng sysConfigMng;

}
