package com.gwideal.base.manager;

import com.gwideal.base.entity.BusinessEntity;
import com.gwideal.core.entity.SysUser;

/**
 * Created by farno on 2016/5/17.
 */
public interface BusinessMng<T extends BusinessEntity> extends BaseMng<T> {
  void logicDel (String id, SysUser currentUser);

  void saveOrUpdate (T t, SysUser currentUser);
}
