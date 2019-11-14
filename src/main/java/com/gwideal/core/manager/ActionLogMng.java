package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.ActionLog;

public interface ActionLogMng extends BaseMng<ActionLog> {

  /**
   * @param sys
   * @param ip
   * @param operator
   * @param opType
   * @param operNum
   * @param entity
   * @param entityId
   * @param bz
   * @param act
   */
  void saveLog(String sys, String ip, String operator, String opType, String operNum, String entity, String entityId, String bz, String act);

}
