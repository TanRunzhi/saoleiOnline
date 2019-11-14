package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.SysRole;

public interface SysRoleMng extends BaseMng<SysRole> {
  String getCacheRoleTree();

  String getCacheRoleChildren(String id);

  String getRoleChildren(String id, String userid);

  void saveRoleAcl(String roleId, String aclKey, String op);

  void saveUserAcl(String userId, String aclKey, String op);

  void saveRoleUser(String roleId, String userId, String op);


}
