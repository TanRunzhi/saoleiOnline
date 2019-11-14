package com.gwideal.core.manager.impl;

import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.base.util.DateUtil;
import com.gwideal.core.entity.ActionLog;
import com.gwideal.core.manager.ActionLogMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("actionLogMng")
@Transactional
public class ActionLogMngImpl extends BaseMngImpl<ActionLog> implements
    ActionLogMng {

  private final static Logger logger = LogManager.getLogger();

  @Override
  public void saveLog(String sys, String ip, String operator, String opType, String operNum, String entity, String entityId, String bz, String act) {
    if (!Boolean.parseBoolean(PropertiesReader.getPropertiesValue("sys.log.open"))) {
      return;
    }

    ActionLog log = new ActionLog(sys, ip, true, operator, DateUtil.getNow("yyyy-MM-dd HH:mm"), opType, operNum + "", entity, entityId, bz, act);
    baseDao.save(log);
    logger.debug(opType + entity + "日志成功!");
  }


}
