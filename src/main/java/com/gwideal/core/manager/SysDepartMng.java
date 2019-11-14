package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.SysDepart;
import com.gwideal.core.entity.SysUser;

public interface SysDepartMng extends BaseMng<SysDepart> {
  String getCacheAjaxRoot (String scope, String q, SysUser currentUser);

  String getCacheAjaxChild (String id);

  String getJSONUser (String id);

  String getJSONDept (String q);
  
  /**
   * 根据编码获取部门对象
   * @param code
   * @return
   */
  SysDepart getDeptByCode (String code);
  
  /**
   * 获取部门树
   * @param deptid
   * @param type 
   * @return
   */
  String getDeptTree (String deptid, String type);
  
  /**
   * 验证编码是否存在
   * @param code
   * @return
   */
  boolean isExistsCode (String code);

  void logicDel (String id, SysUser currentUser);
}
