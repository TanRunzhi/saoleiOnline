package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.SysConfig;

public interface SysConfigMng extends BaseMng<SysConfig> {
  String getCacheValue (String key);

  void setValue (String key, String value, String remark);

  String getValue (String key);
}
