package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.SysAcl;
import com.gwideal.core.entity.SysUser;

import java.util.List;

public interface SysAclMng extends BaseMng<SysAcl> {

  List<String> getAclsByUser(SysUser currentUser);

  String getAclTree (String roleId);

  String getAclChildren (String roleId, String key);

  SysAcl loadCacheAclByKey (String aclKey);
}
