package com.gwideal.core.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.SysDepart;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.SysDepartMng;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("sysDepartMng")
@Transactional
public class SysDepartMngImpl extends BaseMngImpl<SysDepart> implements
    SysDepartMng {

  @Override
  public List<SysDepart> getAll() {
    return find("from SysDepart order by code asc");
  }

  public SysDepart getDeptByCode(String code) {
    List<SysDepart> list = findBy("code", code, null);
    return (list == null || list.size() == 0) ? null : list.get(0);
  }

  public boolean isExistsCode(String code) {
    List<Object> objs = new ArrayList<Object>();
    objs.add(code);
    return count("select count(1) from SysDepart where code=?", objs) > 0;
  }

  @Override
  public void logicDel(String id, SysUser currentUser) {
    boolean flag = false;
    String sql ="update t_core_depart set flag = 'false' where pid = '"+id+"'";
    jdbcTemplate.execute(sql);
  }

  /**
   * 获得缓存部门列表（JSON）
   *
   * @param scope       查询范围（corp:本单位，dept:本部门）
   * @param q           查询参数
   * @param currentUser 当前登录人
   * @return 部门JSON数据（部门ID，名称，级别）
   */
  @Override
  public String getCacheAjaxRoot(String scope, String q, SysUser currentUser) {
    Map<String, Object> m = new HashMap<String, Object>();
    List<Map<String, Object>> list;
    if (StringUtils.isNotEmpty(scope)) {
      if ("corp".equalsIgnoreCase(scope))
        m.put("eq_pid", currentUser.getDepart().getId());
      list = sqlQuery(
          "select pid as id,c_name as name,N_Level as level from T_CORE_DEPART where n_valid = 1 ",
          m, "N_order");
    } else {
      list = sqlQuery(
          "select pid as id,c_name as name,N_Level as level from T_CORE_DEPART where n_valid = 1 and c_pid is null",
          m, "N_order");
    }
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> d : list) {
      ss.append("{");
      Set<SysDepart> cs = load((String) d.get("id")).getChildren(); // 判断当前节点是否还有子节点
      if (cs.size() == 0) { // 没有子节点 设置 state 为空
        ss.append(String
            .format("\"id\": \"%s\",\"level\": \"%s\", \"text\": \"%s\", \"iconCls\": \"\", \"state\": \"\"",
                d.get("id"), d.get("level"), d.get("name")));
      } else { // 还有子节点 设置 state为closed
        ss.append(String
            .format("\"id\": \"%s\",\"level\": \"%s\", \"text\": \"%s\", \"iconCls\": \"\", \"state\": \"closed\"",
                d.get("id"), d.get("level"), d.get("name")));
      }
      ss.append("},");
    }
    ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  /**
   * 获得子节点
   *
   * @param id 父节点ID
   * @return 子节点部门列表（部门ID，名称，级别）
   */
  @Override
  public String getCacheAjaxChild(String id) {
    Map<String, Object> m = new HashMap<>();
    m.put("eq_c_pid", id);
    List<Map<String, Object>> list = sqlQuery(
        "select pid as id,name from T_CORE_DEPART where flag=1 ", m,
        "seq");
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> d : list) {
      ss.append("{");
      Set<SysDepart> cs = load((String) d.get("id")).getChildren(); // 判断当前节点是否还有子节点
      if (cs.size() == 0) { // 没有子节点 设置 state 为空
        ss.append(String
            .format("\"id\": \"%s\", \"level\": \"%s\", \"text\": \"%s\", \"iconCls\": \"\", \"state\": \"\"",
                d.get("id"), d.get("level"), d.get("name")));
      } else { // 还有子节点 设置 state为closed
        ss.append(String
            .format("\"id\": \"%s\", \"level\": \"%s\",\"text\": \"%s\", \"iconCls\": \"\", \"state\": \"closed\"",
                d.get("id"), d.get("level"), d.get("name")));
      }
      ss.append("},");
    }
    ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString(); // 字符编码转换
  }

  @Override
  public String getJSONUser(String id) {
    return "123";
  }

  /**
   * 获得部门JSON对象
   *
   * @param q 部门名称查询字段
   * @return 部门JSON对象（部门ID，名称，级别）
   */
  @Override
  public String getJSONDept(String q) {
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("c_name", q);
    List<Map<String, Object>> list = sqlQuery(
        "select pid as id,c_name as name,N_Level as level from T_CORE_DEPART where n_valid=1 ",
        m, "N_order");
    StringBuilder ss = new StringBuilder();
    ss.append("[");
    for (Map<String, Object> d : list) {
      ss.append("{");
      ss.append(String
          .format("\"id\": \"%s\",\"level\": \"%s\", \"text\": \"%s\", \"iconCls\": \"\", \"state\": \"\"",
              d.get("id"), d.get("level"), d.get("name")));
      ss.append("},");
    }
    ss = ss.deleteCharAt(ss.lastIndexOf(",")).append("]");
    return ss.toString();
  }

  public void saveOrUpdate(SysDepart dept) {
    if (dept.getParent() == null
        || StringUtils.isEmpty(dept.getParent().getId()))
      dept.setParent(null);
    super.saveOrUpdate(dept);
  }

  @Override
  public String getDeptTree(String deptid, String type) {
    String sql = "SELECT pid AS id,name as [text],'' iconCls,"
        + " (select (case when count(*)>0 then 'closed' else '' end ) "
        + " from t_core_depart h where h.PARENT_ID=t.pid) [state] "
        + " FROM t_core_depart t where pid!='-1'  ";
    if ("root".equals(type)) {
      if (deptid == null || "-1".equals(deptid)) {
        deptid = "1";
      }
      sql += " and pid='" + deptid + "'";
    } else {
      if (deptid != null && !"-1".equals(deptid)) {
        sql += " and parent_id='" + deptid + "'";
      } else {
        sql += " and parent_id is null ";
      }
    }
    return JSON.toJSONString(jdbcTemplate.queryForList(sql));
  }

}
