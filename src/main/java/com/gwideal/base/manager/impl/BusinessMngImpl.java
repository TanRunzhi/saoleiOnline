package com.gwideal.base.manager.impl;

import com.gwideal.base.entity.BusinessEntity;
import com.gwideal.core.config.Constants;
import com.gwideal.core.entity.SysUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by farno on 2015/11/17.
 */
@Transactional
public class BusinessMngImpl<T extends BusinessEntity> extends BaseMngImpl<T> {

  public void logicDel(String ids, SysUser currentUser) {
    if(StringUtils.isNotEmpty(ids)) {
      for (String id : ids.split(Constants.SPLIT_CHAR)) {
        if (StringUtils.isNotEmpty(id)) {
          ajaxUpdate(id, "flag", false, currentUser);
          /*T t = get(id);
          t.setFlag(false);
          saveOrUpdate(t,currentUser);*/
        }
      }
    }
  }

  public void saveOrUpdate(T t, SysUser currentUser) {
    t.setUpdateTime(new Date());
    t.setUpdater(currentUser);
    super.saveOrUpdate(t);
  }

}
