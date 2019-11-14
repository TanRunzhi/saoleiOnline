package com.gwideal.base.manager;

import com.gwideal.base.entity.BaseEntity;
import com.gwideal.core.entity.SysUser;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by li_hongyu on 14-7-17.
 */
public interface BaseMng<T extends BaseEntity> {
  void saveOrUpdate(T t);

  void merge(T t);

  T save(T t);

  T get(Serializable id);

  T load(Serializable id);

  void del(String ids);

  List<T> getAll();

  List<T> getAllOrderBy(String orderBy);

  List<T> find(String hql, Object... values);

  List<T> topFind(String hql, Integer maxResult, Object... values);

  List<T> find(String hql, Map<String, Object> params);

  List<T> findBy(String key, String value, String orderBy);

  List<T> findByEqKey(String key, String value, String orderBy);

  List<T> find(String hql, Integer page, Integer rows, Object... values);

  List<T> findOrderBy(String hql, String orderBy);

  List<T> mergeHQL(T t, String hql);

  List<T> mergeHQL(T t, String columns, String hql, boolean isPage);

  List<Object[]> mergeObjHQL(String fieldHql, T t, String hql);

  List<T> mergeHQLWithOutPage(T t, String hql);

  List<Object> createHql(String hql);

  List<Object[]> findObjs(T t, String hql, String appendSql);

  String batchOpr(char op, String val, String ids);

  String ajaxUpdate(String id, String fieldName, Object fieldValue);

  Integer executeHql(String hql, Object[] param);

  boolean checkDuplicated(String id, String key, String value, String hql);

  Integer count(String hql, List<Object> param);

  Integer count(String hql, Object... param);

  T initBean(T queryBean);

  void refresh(T t);

  List<Map<String, Object>> sqlQuery(String sql, Map<String, Object> params, String orderBy);

  List<Map<String, Object>> sqlQuery(String sql, T t, String appendSql);

  List<Map<String, Object>> sqlQueryByPage(String sql, T t, String appendSql);

  String sqlQueryByPage(String sql, Integer page, Integer pageSize, String orderBy, boolean encodeId, Object... params);

  String ajaxUpdate(String tableName, String id, String fieldName, Boolean fieldValue, String userId);

  String ajaxUpdate(String id, String fieldName, Object fieldValue, SysUser user);

  String ajaxAudit(String id, String fieldName, Object fieldValue, String fieldName2, Object fieldValue2, SysUser user);

  String ajaxUpdateBatch(String tableName, String fieldName, String fieldValue, String conditionName, String conditionValue);

  String ajaxUpdateById(String className, String pid, String fieldName, String fieldValue, SysUser user);

  /**
   * 动态导出实体的属性成Excel
   *
   * @param t        实体
   * @param columns  列集合
   * @param hql      其余的条件
   * @param fileName 文件名
   * @param response
   */
  void expDynamicFieldInfo(T t, List<String> columns, String hql, String fileName, HttpServletResponse response);

  void expSingleDynamicFieldInfo(String id, String key, String fileName, HttpServletResponse response);

  /**
   * 获取需要导出Excel的列
   *
   * @return Map<String, List < Object [ ]>>集合，key为数据类型  value为数组：其中 0、字段名 1、注释 2、行数 3、列数
   */
  Map<String, List<Object[]>> getDynamicExcelField();

}