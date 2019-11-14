package com.gwideal.core.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.base.manager.impl.BusinessMngImpl;
import com.gwideal.core.entity.SysDepart;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.SysDepartMng;
import com.gwideal.core.manager.SysRoleMng;
import com.gwideal.core.manager.SysUserMng;
import com.gwideal.util.codeHelper.CustomerCoder;
import com.gwideal.util.io.PropertiesReader;
import com.gwideal.util.json.JSONHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by li_hongyu on 14-7-11.
 */
@Service("sysUserMng")
@Transactional
public class SysUserMngImpl extends BusinessMngImpl<SysUser> implements SysUserMng {

  private final static Logger logger = LogManager.getLogger();
  private static final Integer LOCKED_MINUTE = Integer.parseInt(PropertiesReader.getPropertiesValue("msg.account.lockedMinute"));
  private static final int FAILED_TIMES = Integer.parseInt(PropertiesReader.getPropertiesValue("msg.account.allowFailed"));

  @Override
  public void saveOrUpdate(SysUser user) {
    if (StringUtils.isEmpty(user.getId())
        && StringUtils.isEmpty(user.getPwd()))
      user.setPwd(codeGenerator.encoderMD5(PropertiesReader.getPropertiesValue("sys.init.pwd")));
    super.saveOrUpdate(user);
  }

  /**
   * 验证登陆
   *
   * @param account    登录名
   * @param pwd        密码
   * @return 返回用户，如没有返回空
   */
  @Override
  public SysUser authLogin(String account, String pwd) {
    String sql = "SELECT pid AS id,unlock_time unlockTime FROM t_core_user " +
        "WHERE flag = 1 and  account = ? AND pwd = ? AND flag = 1 ";
    List<Map<String, Object>> l = jdbcTemplate.queryForList(sql, account, codeGenerator.encoderMD5(pwd));
    if (l.size() > 0) {
      Map<String, Object> user = l.get(0);
      String id = (String) user.get("id");
      jdbcTemplate.update("UPDATE t_core_user SET LAST_LOGIN_TIME = ? WHERE pid = ?", new Date(), id);
      if (user.get("unlockTime") == null) {
        jdbcTemplate.update("UPDATE t_core_user SET login_failed_times = 0 WHERE pid = ?",id);
        return load(id);
      } else if (user.get("unlockTime") != null && ((Date) user.get("unlockTime")).getTime() < System.currentTimeMillis()) {
        logger.info("userId: [{}] account unlocked", id);
        jdbcTemplate.update("UPDATE t_core_user SET login_failed_times = 0,unlock_time = NULL WHERE pid = ?", id);
        return load(id);
      }
    }
    return null;
  }



  @Override
  public String ajSetRoles(SysUser user, String roleIds) {
    user.getRoles().clear();
    if (!StringUtils.isEmpty(roleIds.replace(" ", ""))) {
      for (String roleId : roleIds.split(",")) {
        user.getRoles().add(sysRoleMng.load(roleId));
      }
    }
    saveOrUpdate(user);
    return "ok";
  }

  /**
   * 通过单位或部门ID获得人员
   *
   * @param deptId 部门或单位ID
   * @return 人员JSON数据(登录表示及用户姓名 ）
   */
  @Override
  public String getCacheUserByDept(String deptId) {
    return JSONHelper
        .formatObject(jdbcTemplate
            .queryForList(
                "SELECT pid AS id,real_name AS realName FROM t_core_user WHERE flag = 1 AND dept_id=? ORDER BY N_Order",
                deptId));
  }

  /**
   * 获得人员JSON列表
   *
   * @param loginId     人员登录标识
   * @param realName    人员姓名
   * @param scope       查询范围（corp:本单位；dept:本部门）
   * @param currentUser 当前登陆人
   * @return JSON格式人员数据（登录标识及人员姓名）
   */
  @Override
  public String ajSearchUser(String loginId, String realName, String scope,
                             SysUser currentUser) {
    Map<String, Object> m = new HashMap<>();
    m.put("c_loginid", loginId);
    m.put("c_name", realName);
    if (StringUtils.isNotEmpty(scope)) {
      if ("corp".equalsIgnoreCase(scope))
        m.put("c_corp", currentUser.getDepart().getId());
    }
    return JSONHelper
        .formatObject(sqlQuery(
            "select pid as id,real_name as realName from t_core_user where flag = 1 ",
            m, null));
  }

  @Override
  public SysUser valid(String userId, String loginId) {
    return null;
  }

  /**
   * 加载用户树根节点，找出所有一级部门
   *
   * @return easyui tree string
   */
  @Override
  public String loadCacheUserTree() {
    List<Map<String, Object>> departs = jdbcTemplate
        .queryForList("SELECT pid AS id,name FROM t_core_depart WHERE FLAG = 1 AND parent_id IS NULL ORDER BY seq");
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> depart : departs) {
      ss.append("{");
      List<Map<String, Object>> subDeparts = jdbcTemplate.queryForList(
          "SELECT pid FROM t_core_depart WHERE parent_id = ?",
          depart.get("id"));
      List<Map<String, Object>> users = jdbcTemplate.queryForList(
          "SELECT pid FROM t_core_user WHERE depart_id = ?",
          depart.get("id"));
      if (subDeparts.isEmpty() && users.isEmpty())
        ss.append(String
            .format("\"id\": \"%s\",\"text\": \"%s\",\"isDepart\":%s,\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"\"",
                depart.get("id"), depart.get("name"), true));
      else
        ss.append(String
            .format("\"id\": \"%s\",\"text\": \"%s\",\"isDepart\":%s,\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"closed\"",
                depart.get("id"), depart.get("name"), true));
      ss.append("},");
    }
    if (ss.length() > 1) {
      ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    }
    return ss.toString();
  }

  /**
   * 获取用户树子节点
   *
   * @param roleId   角色ID
   * @param departId 父节点ID
   * @return easyui tree string
   */
  @Override
  public String getCacheUserChildren(String roleId, String departId) {
    List<Map<String, Object>> departs = jdbcTemplate
        .queryForList(
            "SELECT pid AS id,name FROM t_core_depart WHERE flag = 1 AND parent_id = ? ORDER BY seq",
            departId);
    List<Map<String, Object>> users = jdbcTemplate
        .queryForList(
            "SELECT pid AS id,real_name AS name FROM t_core_user WHERE flag = 1 AND depart_id = ? ORDER BY account",
            departId);
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    if (!departs.isEmpty())
      for (Map<String, Object> depart : departs) {
        ss.append("{");
        List<Map<String, Object>> subDeparts = jdbcTemplate
            .queryForList(
                "SELECT pid FROM t_core_depart WHERE flag = 1 AND  parent_id = ?",
                depart.get("id"));
        List<Map<String, Object>> subUsers = jdbcTemplate.queryForList(
            "SELECT pid FROM t_core_user WHERE flag = 1 AND  depart_id = ?",
            depart.get("id"));
        if (subDeparts.isEmpty() && subUsers.isEmpty())
          ss.append(String
              .format("\"id\": \"%s\",\"text\": \"%s\",\"isDepart\":%s,\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"\"",
                  depart.get("id"), depart.get("name"), true));
        else
          ss.append(String
              .format("\"id\": \"%s\",\"text\": \"%s\",\"isDepart\":%s,\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"closed\"",
                  depart.get("id"), depart.get("name"), true));
        ss.append("},");
      }
    if (!users.isEmpty())
      for (Map<String, Object> user : users) {
        ss.append("{");
        ss.append(String
            .format("\"id\": \"%s\",\"text\": \"%s\",\"isDepart\":%s,\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"\"",
                user.get("id"), user.get("name"), false));
        if (StringUtils.isNotEmpty(roleId)
            && jdbcTemplate
            .queryForList(
                "SELECT * FROM t_core_user_role WHERE ROLE_ID=? AND user_id = ?",
                roleId, user.get("id")).size() == 1)
          ss.append(",\"checked\":true");
        ss.append("},");
      }
    ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  @Override
  public List<String> getAclKeyByUserId(String userId) {
    Set<String> result = new HashSet<>();
    for (Map<String, Object> acl : jdbcTemplate
        .queryForList(
            "SELECT aclKey FROM t_core_acl a WHERE a.pid IN (SELECT acl_id FROM t_core_acl_user WHERE user_id = ?)",
            userId)) {
      result.add((String) acl.get("aclKey"));
    }
    return new ArrayList<>(result);
  }

  @Override
  public Set<String> getAclUrlByUserId(String userId) {
    Set<String> aclSet = new HashSet<>();
    List<Map<String, Object>> acls = jdbcTemplate.queryForList(
        "SELECT a.remark url FROM t_core_acl a WHERE a.pid IN (SELECT acl_id FROM t_core_acl_user WHERE user_id = ?)",
        userId);
    if (!acls.isEmpty()) {
      for (Map<String, Object> acl : acls) {
        String des = (acl.get("url") == null ? "" : acl.get("url").toString());
        if (StringUtils.isNotBlank(des)) {
          for (String url : des.split("@")) {
            aclSet.add(url);
          }
        }
      }
    }
    return aclSet;
  }


  @Override
  public String getAjaxRoot(String q) {
    List<SysDepart> list = q == null ? sysDepartMng
        .find("from SysDepart where 1 = 1") : sysDepartMng.find(
        "from SysDepart where 1 = 1 and name like ?0", "%" + q + "%");
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (SysDepart d : list) {
      ss.append("{");
      ss.append(String
          .format("\"id\": \"%s\", \"text\": \"%s\",\"attributes\":{\"type\":\"depart\"}, \"iconCls\": \"\", \"state\": \"closed\"",
              d.getId(), d.getName()));
      ss.append("},");
    }
    ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  @Override
  public String getAjaxLeaf(String id) {
    List<SysUser> list = find(
        "from SysUser where 1 = 1 and depart.id = ?0", id);
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (SysUser u : list) {
      ss.append("{");
      ss.append(String
          .format("\"id\": \"%s\",\"checkbox\":\"false\", \"text\": \"%s\",\"attributes\":{\"type\":\"user\"}, \"iconCls\": \"\", \"state\": \"\"",
              u.getId(), u.getRealName()));
      ss.append("},");
    }
    ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString(); // 字符编码转换
  }

  @Override
  public String pwSetting(SysUser user) {
    try {
      SysUser pUser = this.get(user.getId());
      pUser.setPwd(codeGenerator.encoderMD5(user.getPwd()));
      this.saveOrUpdate(pUser);
      return "1";
    } catch (Exception e) {
      e.printStackTrace();
      return "密码修改出现错误";
    }
  }

  @Override
  public String getUserByDeptId(String deptId) {
    StringBuilder sql = new StringBuilder("select u.PID id,u.account,u.REAL_NAME realName,u.phone,u.mail,d.NAME deptname "
        + "from  T_CORE_USER u inner join T_CORE_DEPART d on u.DEPART_ID=d.PID where u.flag = 1 ");
    if (StringUtils.isNotEmpty(deptId) && !"-1".equals(deptId)) {
      sql.append(" and d.pid!='-1'");
    }
    sql.append("	and d.pid=?");
    return JSON.toJSONString(jdbcTemplate.queryForList(sql.toString(), deptId));
  }


  @Override
  public List<SysUser> getUserByType(String type) {
    return find(" from SysUser where flag = 1 and  type = ?0  ", type);
  }

  @Override
  public List<SysUser> getExistsRoleUserByType(String type, String roleCode) {
    return find(" from SysUser u where u.flag = 1 and u.type=?0 and ?1 in (select o.code from u.roles o) ", type, roleCode);
  }

  @Override
  public String ajSetPwd(SysUser currentUser, String pwd) {
    Map<String, Object> result = new BaseJsonResult();
    if (StringUtils.isEmpty(pwd)) {
      result.put("success", false);
      result.put("message", PropertiesReader.getPropertiesValue("sys.msg.pwdEmpty"));
    } else {
      currentUser = load(currentUser.getId());
      currentUser.setPwd(codeGenerator.encoderMD5(pwd));
      saveOrUpdate(currentUser);
    }
    return JSON.toJSONString(result);
  }

  @Override
  public String userLoginFailed(String account) {
    List<Map<String, Object>> result =
        jdbcTemplate.queryForList("SELECT pid,login_failed_times failedTimes,unlock_time unlockTime FROM t_core_user WHERE ACCOUNT = ?", account);
    if (result.size() > 0) {
      Map<String, Object> user = result.get(0);
      Integer failedTimes = Integer.parseInt(user.get("failedTimes").toString());
      if (failedTimes < FAILED_TIMES) {
        logger.info("user:[{}] login failed {} times", account, failedTimes + 1);
        jdbcTemplate.update("UPDATE t_core_user SET login_failed_times = ? WHERE pid = ?", failedTimes + 1, user.get("pid"));
        return PropertiesReader.getValueWithPH("sys.account.failedTimes", (failedTimes + 1) + "");
      } else if (failedTimes == FAILED_TIMES && user.get("unlockTime") == null) {
        logger.info("user:[{}] login failed {} times,account will be locked for {} minutes", FAILED_TIMES, account, LOCKED_MINUTE);
        jdbcTemplate.update("UPDATE t_core_user SET login_failed_times = ?," +
            "unlock_time = (interval " + LOCKED_MINUTE + " minute + sysdate()) WHERE pid = ?", FAILED_TIMES, user.get("pid"));
        return PropertiesReader.getValueWithPH("sys.account.locked");
      } else {
        logger.info("user:[{}] login failed {} times, account is still locking", FAILED_TIMES, account);
        return PropertiesReader.getValueWithPH("sys.account.locking");
      }
    } else {
      logger.info("user: [{}], which is not exist, tempt to login system", account);
      return PropertiesReader.getPropertiesValue("sys.account.notExist");
    }
  }

  /**
   * 批量给企业人员授权
   *
   * @return
   */
  @Override
  public String batchGrant() {
    List<SysUser> users = find("from SysUser where account like ?0 or account like ?1 or account like ?2 or account like ?3", "%-xx", "%-gl", "%-zs", "%-cw");
    List<Object[]> userRoles = new ArrayList<>();
    List<Object[]> userAcls = new ArrayList<>();
    List<Object[]> delUser = new ArrayList<>();
    List<Map<String, Object>> xxRoleAcls = jdbcTemplate.queryForList("select * from T_CORE_ROLE_ACL where ROLE_ID = (select pid from T_CORE_ROLE where code = ?)", "corpInfo");
    List<Map<String, Object>> glRoleAcls = jdbcTemplate.queryForList("select * from T_CORE_ROLE_ACL where ROLE_ID = (select pid from T_CORE_ROLE where code = ?)", "corpMag");
    List<Map<String, Object>> zsRoleAcls = jdbcTemplate.queryForList("select * from T_CORE_ROLE_ACL where ROLE_ID = (select pid from T_CORE_ROLE where code = ?)", "corpBusiness");
    List<Map<String, Object>> cwRoleAcls = jdbcTemplate.queryForList("select * from T_CORE_ROLE_ACL where ROLE_ID = (select pid from T_CORE_ROLE where code = ?)", "corpFinance");
    for (SysUser u : users) {
      delUser.add(new Object[]{u.getId()});
      if (u.getAccount().endsWith("zs")) {
        userRoles.add(new Object[]{u.getId(), zsRoleAcls.get(0).get("ROLE_ID")});
        for (Map<String, Object> roleAcl : zsRoleAcls) {
          userAcls.add(new Object[]{u.getId(), roleAcl.get("ACL_ID")});
        }
      } else if (u.getAccount().endsWith("cw")) {
        userRoles.add(new Object[]{u.getId(), cwRoleAcls.get(0).get("ROLE_ID")});
        for (Map<String, Object> roleAcl : cwRoleAcls) {
          userAcls.add(new Object[]{u.getId(), roleAcl.get("ACL_ID")});
        }
      } else if (u.getAccount().endsWith("gl")) {
        userRoles.add(new Object[]{u.getId(), glRoleAcls.get(0).get("ROLE_ID")});
        for (Map<String, Object> roleAcl : glRoleAcls) {
          userAcls.add(new Object[]{u.getId(), roleAcl.get("ACL_ID")});
        }
      } else if (u.getAccount().endsWith("xx")) {
        userRoles.add(new Object[]{u.getId(), xxRoleAcls.get(0).get("ROLE_ID")});
        for (Map<String, Object> roleAcl : xxRoleAcls) {
          userAcls.add(new Object[]{u.getId(), roleAcl.get("ACL_ID")});
        }
      }
    }
    int[] k = jdbcTemplate.batchUpdate("delete from T_CORE_ACL_USER where USER_ID = ?", delUser);
    logger.debug("del acl user :{}", k.length);
    int[] l = jdbcTemplate.batchUpdate("delete from T_CORE_USER_ROLE where USER_ID = ?", delUser);
    logger.debug("del user role :{}", l.length);
    int[] i = jdbcTemplate.batchUpdate("insert into t_core_user_role(USER_ID,ROLE_ID) values (?,?)", userRoles);
    logger.debug("insert user role :{}", i.length);
    int[] j = jdbcTemplate.batchUpdate("insert into T_CORE_ACL_USER(user_id,acl_id) values (?,?)", userAcls);
    logger.debug("insert acl user:{}", j.length);
    Map<String, Object> result = new BaseJsonResult();
    result.put("message", "insert userRole " + i.length + ", insert userAcl " + j.length);
    return JSON.toJSONString(result);
  }

  @Override
  public SysUser getUserByAccount(String account) {
    List<SysUser> list = findByEqKey("account", account, null);
    return list == null || list.isEmpty() ? null : list.get(0);
  }

  @Resource
  private CustomerCoder codeGenerator;

  @Resource
  private SysDepartMng sysDepartMng;

  @Resource
  private SysRoleMng sysRoleMng;

}
