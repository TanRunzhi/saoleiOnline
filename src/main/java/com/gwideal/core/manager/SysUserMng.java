package com.gwideal.core.manager;

import com.gwideal.base.manager.BusinessMng;
import com.gwideal.core.entity.SysUser;

import java.util.List;
import java.util.Set;

/**
 * Created by li_hongyu on 14-7-11.
 */

public interface SysUserMng extends BusinessMng<SysUser> {
  SysUser authLogin(String account, String pwd);

  String getAjaxRoot(String q);

  String getAjaxLeaf(String id);

  String ajSetRoles(SysUser user, String roleIds);

  String getCacheUserByDept(String deptId);

  String ajSearchUser(String loginId, String realName, String scope, SysUser currentUser);

  SysUser valid(String userId, String loginId);

  String loadCacheUserTree();

  String getCacheUserChildren(String roleId, String id);

  List<String> getAclKeyByUserId(String userId);

  Set<String> getAclUrlByUserId(String userId);

  String pwSetting(SysUser user);

  String getUserByDeptId(String deptid);

  List<SysUser> getUserByType(String type);

  List<SysUser> getExistsRoleUserByType(String type, String roleCode);

  String ajSetPwd(SysUser currentUser, String pwd);

  String userLoginFailed(String account);

  String batchGrant();

  SysUser getUserByAccount(String account);
}
