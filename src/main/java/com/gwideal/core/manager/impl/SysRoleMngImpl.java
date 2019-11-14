package com.gwideal.core.manager.impl;

import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.SysAcl;
import com.gwideal.core.entity.SysRole;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.SysAclMng;
import com.gwideal.core.manager.SysRoleMng;
import com.gwideal.core.manager.SysUserMng;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("sysRoleMng")
@Transactional
public class SysRoleMngImpl extends BaseMngImpl<SysRole> implements SysRoleMng {

  private final static Logger logger = LogManager.getLogger();

  /**
   * 加载权限树根节点 找出所有分组
   *
   * @return easyui tree string
   */
  @Override
  public String getCacheRoleTree() {
    List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT pid AS id,name FROM t_core_role_group ORDER BY seq");
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> roleGroup : list) {
      ss.append("{");
      if (jdbcTemplate.queryForList("SELECT pid FROM t_core_role WHERE group_id = ?", roleGroup.get("id")).isEmpty())
        ss.append(String.format("\"id\": \"%s\", \"text\": \"%s\", \"isGroup\":%s,\"iconCls\": \"\", \"state\": \"\"", roleGroup.get("id"), roleGroup.get("name"), true));
      else
        ss.append(String.format("\"id\": \"%s\", \"text\": \"%s\", \"isGroup\":%s,\"iconCls\": \"\", \"state\": \"closed\"", roleGroup.get("id"), roleGroup.get("name"), true));
      ss.append("},");
    }
    if (!list.isEmpty())
      ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  /**
   * 加载权限树子节点
   *
   * @param groupId 权限分组ID
   * @return easyui tree string
   */
  @Override
  public String getCacheRoleChildren(String groupId) {
    List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT pid AS id,name FROM t_core_role WHERE group_id = ? ORDER BY seq", groupId);
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> role : list) {
      ss.append("{");
      ss.append(String.format("\"id\": \"%s\", \"text\": \"%s\",\"isGroup\":%s,\"iconCls\": \"\", \"state\": \"\"", role.get("id"), role.get("name"), false));
      ss.append("},");
    }
    ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  /**
   * 加载权限树子节点
   *
   * @param groupId 权限分组ID
   * @param userid  用户ID
   * @return easyui tree string
   */
  public String getRoleChildren(String groupId, String userid) {
    List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT pid AS id,name FROM t_core_role WHERE group_id = ? ORDER BY seq", groupId);
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> role : list) {
      ss.append("{");
      ss.append(String.format("\"id\": \"%s\", \"text\": \"%s\",\"isGroup\":%s,\"iconCls\": \"\", \"state\": \"\"", role.get("id"), role.get("name"), false));
      if (StringUtils.isNotEmpty(userid) && jdbcTemplate.queryForList("SELECT ROLE_ID FROM T_CORE_USER_ROLE WHERE USER_ID=? AND ROLE_ID = ?", userid, role.get("id")).size() == 1) {
        ss.append(",\"checked\":true");
      }
      ss.append("},");
    }
    ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  /**
   * 自动保存角色-权限关系
   *
   * @param roleId 角色ID
   * @param aclKey 权限KEY
   * @param op     操作（a:添加；r:移除）
   */
  @Override
  public void saveRoleAcl(String roleId, String aclKey, String op) {
    SysRole role = load(roleId);
    SysAcl acl = sysAclMng.loadCacheAclByKey(aclKey);
    //
    if ("a".equals(op)) {
      role.getAcls().add(acl);
    } else if ("r".equals(op)) {
      role.getAcls().remove(acl);
    }
    saveOrUpdate(role);
    //
    if ("a".equals(op)) {
      if (!role.getUsers().isEmpty()) {
        for (SysUser u : role.getUsers()) {
          jdbcTemplate.update("insert into T_CORE_ACL_USER(user_id,acl_id) values(?,?)", u.getId(), acl.getId());
        }
      }
    } else if ("r".equals(op)) {
      if (!role.getUsers().isEmpty()) {
        for (SysUser user : role.getUsers()) {
//          jdbcTemplate.update("delete from T_CORE_ACL_USER where user_id = ? and acl_id = ? ", u.getId(), acl.getId());
          Set<SysAcl> acls = new HashSet<>();
          for(SysRole r : user.getRoles()){
            acls.addAll(r.getAcls());
          }
          user.setAcls(acls);
          sysUserMng.saveOrUpdate(user);
        }
      }
    }
  }

  /**
   * 自动保存用户-权限关系
   *
   * @param userId 角色ID
   * @param aclKey 权限KEY
   * @param op     操作（a:添加；r:移除）
   */
  @Override
  public void saveUserAcl(String userId, String aclKey, String op) {
    SysAcl acl = sysAclMng.loadCacheAclByKey(aclKey);
    int row = 0;
    if ("a".equals(op)) {
      row = jdbcTemplate.update("insert into T_CORE_ACL_USER(user_id,acl_id) values(?,?)", userId, acl.getId());
    } else if ("r".equals(op)) {
      row = jdbcTemplate.update("delete from T_CORE_ACL_USER where user_id = ? and acl_id = ? ", userId, acl.getId());
    }
    logger.info("{} rows has been change({}) in T_CORE_ACL_USER , where user_id = {} , aclkey = {}",row,op,userId,aclKey);
  }

  @Override
  public void saveOrUpdate(SysRole role) {
    String op = "update";
    if (StringUtils.isEmpty(role.getId())) {
      op = "add";
    }

    super.saveOrUpdate(role);

    if ("add".equals(op)) {
      //identityService.saveGroup(new GroupEntity(role.getId()));
    }
  }

  /**
   * 自动保存角色-权限关系
   *
   * @param roleId 角色ID
   * @param userId 用户ID
   * @param op     操作（a:添加；r:移除）
   */
  @Override
  public void saveRoleUser(String roleId, String userId, String op) {
    SysRole role = load(roleId);
    SysUser user = sysUserMng.load(userId);
    //
    if ("a".equals(op)) {
      role.getUsers().add(user);
//      identityService.createMembership(userId, roleId);
    } else if ("r".equals(op)) {
      role.getUsers().remove(user);
      //identityService.deleteMembership(userId, roleId);
    }
    super.saveOrUpdate(role);
    //
    if ("a".equals(op)) {
      user.getAcls().addAll(role.getAcls());
    } else if ("r".equals(op)) {
      //这行代码有问题  不该移除此角色的全部权限  如 u1 有角色 r1 r2 ,r1有权限 a1 a2 a3 ，r2有权限 a3 a4 a5 ，若移除r2时，a3权限不该被移除
      // 原代码  →  user.getAcls().removeAll(role.getAcls());
      // 改良后
      Set<SysAcl> acls = new HashSet<>();
      for(SysRole r : user.getRoles()){
        if(!r.getId().equals(roleId)){
          acls.addAll(r.getAcls());
        }
      }
      user.setAcls(acls);
    }
    sysUserMng.saveOrUpdate(user);
  }


  @Autowired
  private SysAclMng sysAclMng;

  @Autowired
  private SysUserMng sysUserMng;


}
