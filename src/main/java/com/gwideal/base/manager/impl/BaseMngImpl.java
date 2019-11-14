package com.gwideal.base.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.dao.BaseDao;
import com.gwideal.base.entity.BaseEntity;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.excel.PoiManager;
import com.gwideal.core.excel.annotations.DynamicExcel;
import com.gwideal.core.manager.LookupsMng;
import com.gwideal.util.codeHelper.CustomerCoder;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by li_Hongyu on 14-7-17.
 */
@Transactional
public class BaseMngImpl<T extends BaseEntity> implements BaseMng<T> {

  private final static Logger logger = LogManager.getLogger();

  private Class getEntityClass() {
    return (Class) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
  }

  private String getEntityName() {
    String className = getEntityClass().getName();
    return className.substring(className.lastIndexOf(".") + 1);
  }

  @Override
  public void saveOrUpdate(T t) {
    if (StringUtils.isEmpty(t.getId()))
      t.setId(null);
    baseDao.saveOrUpdate(t);
  }

  @Override
  public void merge(T t) {
    if (StringUtils.isEmpty(t.getId()))
      t.setId(null);
    baseDao.merge(t);
  }

  public T save(T t) {
    return baseDao.save(t);
  }

  public T load(Serializable id) {
    return baseDao.load(getEntityClass(), id);
  }

  public T get(Serializable id) {
    return (T) baseDao.get(getEntityClass(), id);
  }

  public List<T> getAll() {
    return baseDao.find("from " + getEntityName() + " where 1 = 1");
  }

  public List<T> getAllOrderBy(String orderBy) {
    return baseDao.find("from " + getEntityName() + " where 1 = 1 order by " + (StringUtils.isEmpty(orderBy) ? " id desc" : orderBy));
  }

  public List<T> findOrderBy(String hql, String orderBy) {
    return baseDao.find(hql + " order by " + (StringUtils.isEmpty(orderBy) ? " id desc" : orderBy));
  }


  /**
   * 整合hql语句查询
   *
   * @param t   对象
   * @param hql 原查询语句
   * @return 对象列表的查询结果
   * tips: 查询参数加载说明
   * 1、所有查询参数封装在t.params的map中，遍历map.keySet()获得查询参数的键值对，包括order by
   * 2、如果有ign_开头，则忽略该结果
   * 3、如果key中包含Date,表示日期范围，根据是Date1还是Date2,查询出大于等于Date1小于Date2的结果，否则就直接等于
   * 4、如果key中包含逗号，表示多个值，用hql做in范围查询
   * 5、如果key中包含以eq_开头，用等于查询
   * 6、否则都用like查询
   * 7、最后拼接t.orderBy中的排序参数，为空则按ID排序
   */
  @Override
  public List<T> mergeHQL(T t, String hql) {
    return mergeHQL(t, null, hql, true);
  }


  @Override
  public List<Object[]> mergeObjHQL(String fieldHql, T t, String hql) {
    StringBuilder sb = new StringBuilder(" from " + getEntityName() + " as t ");
    List<Object> params = new ArrayList<>();
    mergeHQL(t, sb, params, hql);
    int totalRow = count("select count(*) " + sb.toString(), params);
    sb.append(" order by ");
    sb.append(StringUtils.isEmpty(t.getOrderBy()) ? "id desc" : t.getOrderBy());
    t.getPm().setTotalRows(totalRow);
    return baseDao.findPageObjArray(fieldHql + sb.toString(), params, t.getPm().getPage(), t.getPm().getPageSize());
  }


  private void mergeHQL(T t, StringBuilder sb, List<Object> params, String hql) {
    sb.append(" where 1=1 ");
    if (!t.getParams().isEmpty()) {
      Map<String, String> paramMap = t.getParams();
      int i = 0;
      for (String key : paramMap.keySet()) {
        if (!StringUtils.isEmpty(paramMap.get(key)) && !key.startsWith("ign_"))
          if (key.contains("Date") || key.contains("Time")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ("1".equals(key.substring(key.length() - 1))) {
              sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" >=?").append(i);
              try {
                params.add(i, sdf.parse(paramMap.get(key)));
              } catch (ParseException e) {
                logger.error(e.getMessage(), e);
              }
            } else if ("2".equals(key.substring(key.length() - 1))) {
              sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" <=?").append(i);
              try {
                params.add(i, sdf.parse(paramMap.get(key)));
              } catch (ParseException e) {
                logger.error(e.getMessage(), e);
              }
            } else {
              sb.append(" and ").append(key).append(" =?").append(i);
              try {
                params.add(i, sdf.parse(paramMap.get(key)));
              } catch (ParseException e) {
                logger.error(e.getMessage(), e);
              }
            }
            i++;
          } else {
            if (StringUtils.isEmpty(paramMap.get(key)) && ",".equals(paramMap.get(key))) {
              continue;
            }
            if(key.startsWith("lik_")){
              //此初目的用于处理多选框做查询时的参数
              // 如数据库字段存的为 01,03 ， 若此时查询的多选框勾选了01和02 , 则参数为 ("lik_字段","01,02")
              // 则HQL应为 ：and ( 字段 like '%01%' or 字段 like '%02%' )
              String value = paramMap.get(key);
              key = key.substring(4);
              sb.append(" and ( 1=2 ");
              for(String val : value.split(",")){
                sb.append(" or ").append(key).append(" like ?").append(i);
                params.add(i, "%" + val + "%");
                i++;
              }
              sb.append(" ) ");
            } else if (key.startsWith("isnull_")) {
              if("1".equals(paramMap.get(key))){
                sb.append(" and ").append(key.substring(7)).append("  is null ");
              }else if("0".equals(paramMap.get(key))){
                sb.append(" and ").append(key.substring(7)).append("  is not null ");
              }
            } else if (key.startsWith("eq_")) {
              sb.append(" and ").append(key.substring(3)).append(" =?").append(i);
              params.add(i, paramMap.get(key));
              i++;
            } else if (key.startsWith("start_")) {
              sb.append(" and ").append(key.substring(6)).append(" like ?").append(i);
              params.add(i, paramMap.get(key) + "%");
              i++;
            } else if (key.startsWith("end_")) {
              sb.append(" and ").append(key.substring(4)).append(" like ?").append(i);
              params.add(i, "%" + paramMap.get(key));
              i++;
            } else if (key.startsWith("bool_")) {
              sb.append(" and ").append(key.substring(5)).append(" = ?").append(i);
              params.add(i, "1".equals(paramMap.get(key)));
              i++;
            } else if (key.startsWith("lob_")) {
              sb.append(" and dbms_lob.instr(").append(key.substring(4)).append(",?").append(i).append(") >0");
              params.add(i, paramMap.get(key));
              i++;
            } else if (key.startsWith("int_")) {
              sb.append(" and ").append(key.substring(4)).append(" = ?").append(i);
              params.add(i, Integer.valueOf(paramMap.get(key)));
              i++;
            } else if (key.startsWith("or_")) {
              if (sb.toString().indexOf("or_") > 1) {
                sb.replace(sb.toString().indexOf("or_"), sb.toString().indexOf("or_") + 3, "or " + key.substring(3) + " =?" + i);
                params.add(i, paramMap.get(key));
              } else {
                sb.append(" and (").append(key.substring(3)).append(" = ?").append(i).append(" or_").append(" )");
                params.add(i, paramMap.get(key));
              }
              i++;
            } else if (paramMap.get(key).contains(",")) {
              sb.append(" and ").append(key).append(" in (");
              for (String subValue : paramMap.get(key).split(",")) {
                sb.append("?").append(i).append(",");
                params.add(i, subValue);
                i++;
              }
              sb.deleteCharAt(sb.length() - 1).append(")");
            } else {
              sb.append(" and ").append(key).append(" like ?").append(i);
              params.add(i, "%" + paramMap.get(key) + "%");
              i++;
            }
          }
      }
    }
    if (!StringUtils.isEmpty(hql))
      sb.append(hql);
  }

  @Override
  public List<T> mergeHQLWithOutPage(T t, String hql) {
    return mergeHQL(t, null, hql, false);
  }

  @Override
  public List<T> mergeHQL(T t, String columns, String hql, boolean isPage) {
    StringBuilder sb = new StringBuilder(" from " + getEntityName() + " as t ");
    List<Object> params = new ArrayList<>();
    mergeHQL(t, sb, params, hql);
    StringBuilder sql = new StringBuilder((StringUtils.isEmpty(columns) ? "" : "select " + columns)).append(sb);
    sql.append(" order by ");
    sql.append(StringUtils.isEmpty(t.getOrderBy()) ? " t.id desc" : t.getOrderBy() + ",t.id desc");
    if (isPage) {
      int totalRow = count("select count(*) " + sb.toString(), params);
      t.getPm().setTotalRows(totalRow);
      if ((t.getPm().getPage() - 1) * t.getPm().getPageSize() > totalRow) {
        t.getPm().setPage(1);
      }
      return baseDao.find(sql.toString(), params, t.getPm().getPage(), t.getPm().getPageSize());
    } else {
      return baseDao.find(sql.toString(), params);
    }
  }

  @Override
  public List<Object> createHql(String hql) {
    return baseDao.getCurrentSession().createQuery(hql).list();
  }

  @Override
  public List<Object[]> findObjs(T t, String hql, String appendSql) {
    StringBuilder sb = new StringBuilder(hql);
    List<Object> params = new ArrayList<Object>();
    mergeHQL(t, sb, params, appendSql);
    if (StringUtils.isNotEmpty(t.getOrderBy())) {
      sb.append(" order by " + t.getOrderBy());
    }
    return baseDao.findObjArray(sb.toString(), params);
  }

  public List<T> findBy(String key, String value, String orderBy) {
    StringBuilder sb = new StringBuilder("from ")
        .append(getEntityName())
        .append(" where 1 = 1 and ")
        .append(key)
        .append(" like ?0 order by ")
        .append(StringUtils.isEmpty(orderBy) ? "id" : orderBy);
    return find(sb.toString(), value);
  }

  @Override
  public List<T> findByEqKey(String key, String value, String orderBy) {
    StringBuilder sb = new StringBuilder("from ")
        .append(getEntityName())
        .append(" where 1 = 1 and ")
        .append(key)
        .append(" = ?0 order by ")
        .append(StringUtils.isEmpty(orderBy) ? "id" : orderBy);
    return find(sb.toString(), value);
  }

  @Override
  public void del(String ids) {
    for (String id : ids.split(","))
      if (StringUtils.isNotEmpty(id)) {
        baseDao.delete(load(id));
      }
  }

  @Override
  public String batchOpr(char op, String val, String ids) {
    int count = 0;
    switch (op) {
      case 'i':
        for (String id : ids.split(",")) {
          if (!StringUtils.isBlank(id)) {
            T t = load(id);
            saveOrUpdate(t);
            count++;
          }
        }
    }
    return PropertiesReader.getValueWithPH("msg." + getEntityName() + ".batchOpr"
        , ("0".equals(val) ? "禁用" : "发布") + count);
  }

  @Override
  public String ajaxUpdate(String id, String fieldName, Object fieldValue) {
    String hql = " update " + getEntityClass().getName() + " set " + fieldName + "=? where id = ?";
    return executeHql(hql, new Object[]{fieldValue, id}) + "";
  }

  public List<T> find(String hql, Object... values) {
    return baseDao.find(hql, values);
  }

  @Override
  public List<T> topFind(String hql, Integer maxResult, Object... values) {
    return baseDao.topFind(hql, maxResult, values);
  }

  public List<T> find(String hql, Map<String, Object> values) {
    return baseDao.find(hql, values);
  }

  @Override
  public List<T> find(String hql, Integer page, Integer rows, Object... values) {
    return baseDao.find(hql, values, page, rows);
  }

  @Override
  public Integer executeHql(String hql, Object[] param) {
    return baseDao.executeHql(hql, param);
  }

  @Override
  public boolean checkDuplicated(String id, String key, String value, String hql) {
    if (StringUtils.isBlank(id)) {//新增
      return find("select " + key + " from " + getEntityName() + " where " + key + " = ?0 " + (StringUtils.isEmpty(hql) ? "" : hql), value).size() > 0;
    } else {//修改
      return find("select " + key + ",id " + "from " + getEntityName() + " where " + key + " = ?0 and id != ?1 " + (StringUtils.isEmpty(hql) ? "" : hql), value, id).size() > 0;
    }
  }

  @Override
  public Integer count(String hql, List<Object> param) {
    return baseDao.count(hql, param);
  }

  @Override
  public Integer count(String hql, Object... param) {
    return baseDao.count(hql, param).intValue();
  }

  @Override
  public T initBean(T queryBean) {
    T t;
    if (StringUtils.isEmpty(queryBean.getId())) {
      try {
        t = (T) queryBean.getClass().newInstance();
        t.setParams(queryBean.getParams());
        t.setPm(queryBean.getPm());
        t.setOp(queryBean.getOp());
        return t;
      } catch (InstantiationException e) {
        logger.error(e.getMessage(), e);
      } catch (IllegalAccessException e) {
        logger.error(e.getMessage(), e);
      }
    } else {
      t = load(queryBean.getId());
      t.setParams(queryBean.getParams());
      t.setPm(queryBean.getPm());
      t.setOp(queryBean.getOp());
      return t;
    }
    return null;
  }

  public void refresh(T t) {
    baseDao.getCurrentSession().flush();
    baseDao.getCurrentSession().refresh(t);
  }

  /**
   * sql脚本查询
   *
   * @param sql     原查询条件
   * @param params  参数：按key：value的形式加载
   * @param orderBy 排序参数
   * @return 查询结果
   * @tips: 参数key说明：
   * 1、以str_开头，字符串，like查询；
   * 2、以eq_开头，对象，=查询
   * 3、其他的类型继续添加
   */
  @Override
  public List<Map<String, Object>> sqlQuery(String sql, Map<String, Object> params, String orderBy) {
    List<Object> l = new ArrayList<Object>();
    StringBuilder sb = new StringBuilder(sql);
    for (String key : params.keySet()) {
      if (params.get(key) != null)
        if (key.contains("time") && (StringUtils.isNotEmpty((String) params.get(key)))) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          if ("1".equals(key.substring(key.length() - 1))) {
            sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" >=?");
            try {
              l.add(sdf.parse((String) params.get(key)));
            } catch (ParseException e) {
              logger.error(e.getMessage(), e);
            }
          } else if ("2".equals(key.substring(key.length() - 1))) {
            sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" <=?");
            try {
              l.add(sdf.parse((String) params.get(key)));
            } catch (ParseException e) {
              logger.error(e.getMessage(), e);
            }
          }
        } else {
          if (StringUtils.isNotEmpty((String) params.get(key)))
            if (key.contains("eq_")) {
              sb.append(" and ").append(key.substring(3)).append(" = ?");
              l.add(params.get(key));
            } else {
              sb.append(" and ").append(key).append(" like ?");
              l.add("%" + params.get(key) + "%");
            }
        }
    }
    sb.append(" order by ").append(StringUtils.isEmpty(orderBy) ? " pid" : orderBy);
    return jdbcTemplate.queryForList(sb.toString(), l.toArray());
  }

  private void mergeSql(StringBuilder sb, T t, List<Object> l) {
    Map<String, String> params = t.getParams();
    for (String key : params.keySet()) {
      if (StringUtils.isNotEmpty(params.get(key)) && !key.startsWith("ign_")) {
        if (key.contains("Date")) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          if ("1".equals(key.substring(key.length() - 1))) {
            sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" >=?");
            try {
              l.add(sdf.parse(t.getParams().get(key)));
            } catch (ParseException e) {
              e.printStackTrace();
            }
          } else if ("2".equals(key.substring(key.length() - 1))) {
            sb.append(" and ").append(key.substring(0, key.length() - 1)).append(" <=?");
            try {
              l.add(sdf.parse(t.getParams().get(key)));
            } catch (ParseException e) {
              e.printStackTrace();
            }
          } else {
            sb.append(" and ").append(key).append(" =?");
            try {
              l.add(sdf.parse(t.getParams().get(key)));
            } catch (ParseException e) {
              logger.error(e.getMessage(), e);
            }
          }
        } else {
          if (params.get(key).contains(",")) {
            sb.append(" and ").append(key).append(" in (");
            for (String subValue : params.get(key).split(",")) {
              sb.append("?").append(",");
              l.add(subValue);
            }
            sb.deleteCharAt(sb.length() - 1).append(")");
          } else if (key.startsWith("eq_")) {
            sb.append(" and ").append(key.substring(3)).append(" =?");
            l.add(params.get(key));
          } else if (key.startsWith("int_")) {
            sb.append(" and ").append(key.substring(4)).append(" =?");
            l.add(Integer.parseInt(params.get(key)));
          } else if (key.startsWith("ge_")) {
            sb.append(" and ").append(key.substring(3)).append(" >=?");
            l.add(params.get(key));
          } else if (key.startsWith("le_")) {
            sb.append(" and ").append(key.substring(3)).append(" <=?");
            l.add(params.get(key));
          } else if (key.startsWith("bool_")) {
            sb.append(" and ").append(key.substring(5)).append(" =?");
            l.add(params.get(key));
          } else if (key.startsWith("or_")) {
            if (sb.toString().indexOf("or_") > 1) {
              sb.replace(sb.toString().indexOf("or_"), sb.toString().indexOf("or_") + 3, "or " + key.substring(3) + " =?");
              l.add(params.get(key));
            } else {
              sb.append(" and (").append(key.substring(3)).append(" = ?").append(" or_").append(" )");
              l.add(params.get(key));
            }
          } else {
            sb.append(" and ").append(key).append(" like ?");
            l.add("%" + params.get(key) + "%");
          }
        }
      }
    }
  }

  @Override
  public List<Map<String, Object>> sqlQuery(String sql, T t, String appendSql) {
    List<Object> l = new ArrayList<Object>();
    StringBuilder sb = new StringBuilder(sql);
    mergeSql(sb, t, l);
    if (!StringUtils.isEmpty(appendSql)) {
      sb.append(appendSql);
    }
    return jdbcTemplate.queryForList(sb.toString(), l.toArray());
  }

  @Override
  public List<Map<String, Object>> sqlQueryByPage(String sql, T t, String appendSql) {
    List<Object> l = new ArrayList<Object>();
    StringBuilder sb = new StringBuilder(sql);
    mergeSql(sb, t, l);
    if (!StringUtils.isEmpty(appendSql)) {
      sb.append(appendSql);
    }
    int totalRow = Integer.parseInt(jdbcTemplate.queryForList("select count(*) as totalRow from (" + sb.toString() + ") t", l.toArray()).get(0).get("totalRow").toString());
    t.getPm().setTotalRows(totalRow);
    int orderIdx = sql.toLowerCase().indexOf("from");
    sb.insert(orderIdx, ",ROW_NUMBER() OVER(ORDER BY " + t.getOrderBy() + ") AS ROWNUM");
    String sqlCore = "select * from (" + sb.toString() + ") temp where ROWNUM between " + t.getPm().getStart(t.getPm().getPage()) + " and " + t.getPm().getEnd(totalRow, t.getPm().getPage());
    return jdbcTemplate.queryForList(sqlCore, l.toArray());
  }

  @Override
  public String sqlQueryByPage(String sql, Integer page, Integer pageSize, String orderBy, boolean encodeId, Object... params) {
    StringBuilder sb = new StringBuilder(sql);
    int totalRow = Integer.parseInt(jdbcTemplate.queryForList("select count(*) as totalRow from (" + sb.toString() + ") t", params).get(0).get("totalRow").toString());
    int orderIdx = sql.toLowerCase().indexOf("from");
    sb.insert(orderIdx, ",ROW_NUMBER() OVER(" + orderBy + ") AS ROWNUM ");
    String sqlCore = "select * from (" + sb.toString() + ") temp where ROWNUM between " + getStart(page, pageSize) + " and " + getEnd(totalRow, page, pageSize);
    List<Map<String, Object>> data = jdbcTemplate.queryForList(sqlCore, params);
    if (encodeId) {
      for (Map<String, Object> item : data) {
        item.put("id", CustomerCoder.getBASE64((String) item.get("id")));
      }
    }
    Map<String, Object> result = new BaseJsonResult();
    result.put("data", data);
    result.put("totalRow", totalRow);
    result.put("page", page);
    result.put("totalPage", totalRow % pageSize == 0 ? totalRow / pageSize : totalRow / pageSize + 1);
    return JSON.toJSONString(result);
  }

  @Override
  public String ajaxUpdate(String tableName, String id, String fieldName, Boolean fieldValue, String userId) {
    String handlePub = "";
    if ("published".equals(fieldName)) {
      if (fieldValue) {
        handlePub = ",publish_date = SYSDATE()";
      } else {
        handlePub = ",publish_date = null ";
      }
    }
    String sql = String.format("update " + tableName + " set " + fieldName + " = " + (fieldValue ? 1 : 0) + "," +
        "update_time=SYSDATE(),updater=? %s where pid = ?", handlePub);
    jdbcTemplate.update(sql, userId, id);

    return JSON.toJSONString(new BaseJsonResult());
  }

  @Override
  public String ajaxUpdate(String id, String fieldName, Object fieldValue, SysUser user) {
    if (user != null) {
      String hql = " update " + getEntityClass().getName() + " set " + fieldName + "=?0,updateTime=sysdate(),updater=?1 where id = ?2";
      return executeHql(hql, new Object[]{fieldValue, user, id}) + "";
    } else {
      String hql = " update " + getEntityClass().getName() + " set " + fieldName + "=?0 where id = ?1";
      return executeHql(hql, new Object[]{fieldValue, id}) + "";
    }
  }

  @Override
  public String ajaxAudit(String id, String fieldName, Object fieldValue, String fieldName2, Object fieldValue2, SysUser user) {
    String hql = " update " + getEntityClass().getName() + " set " + fieldName + "=?0," + fieldName2 + "=?1 ,updateTime=CURRENT_DATE(),updater=?2 where id3 = ?";
    return executeHql(hql, new Object[]{fieldValue, fieldValue2, user, id}) + "";
  }

  @Override
  public String ajaxUpdateBatch(String tableName, String fieldName,
                                String fieldValue, String conditionName, String conditionValue) {
    jdbcTemplate.update("update " + tableName + " set " + fieldName + " = '" + fieldValue + "' where " + conditionName + "  in  " + conditionValue);
    return JSON.toJSONString(new BaseJsonResult());
  }

  @Override
  public String ajaxUpdateById(String className, String pid, String fieldName, String fieldValue, SysUser user) {
    String hql = " update " + className + " set " + fieldName + " = ?0,updateTime=sysdate(),updater=?1 where id = ?2";
    return executeHql(hql, new Object[]{fieldValue, user, pid}) + "";
  }

  public Integer getStart(Integer page, Integer pageSize) {
    return (page - 1) * pageSize + 1;
  }

  public Integer getEnd(int totalRow, Integer page, Integer pageSize) {
    if (totalRow > 0)
      return ((page - 1) * pageSize + pageSize) < totalRow
          ? ((page - 1) * pageSize + pageSize)
          : totalRow;
    else
      return 0;
  }

  @Override
  public void expDynamicFieldInfo(T t, List<String> columns, String hql, String fileName, HttpServletResponse response) {
    List<String> titles = new ArrayList<>();
    List<String[]> objFields = new ArrayList<>();
    for (int i = 0; i < columns.size(); i++) {
      String str = columns.get(i);
      if (StringUtils.isNotEmpty(str)) {
        String[] cols = str.split("-");
        titles.add(cols[cols.length - 1]);
        objFields.add(cols);
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append(" select ");
    for (int i = 0; i < objFields.size(); i++) {
      String[] cols = objFields.get(i);
      if (cols[0].equals("look")) {
        sb.append("concat('look-" + cols[2] + "-'," + cols[1] + "),");
      } else if (cols[0].equals("join")) {
        String column = StringUtils.split(cols[1], ".")[0];
        sb.append("concat('" + column + "-',t." + column + ".id ), ");
      } else {
        sb.append(cols[0] + ",");
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(" from " + t.getClass().getName() + " t ");
    List<Object[]> data = findObjs(t, sb.toString(), hql);
    poiManager.expZipExcel(fileName, data, titles.toArray(), response);
  }

  @Override
  public void expSingleDynamicFieldInfo(String id, String key, String fileName, HttpServletResponse response) {
    List<Object[]> columns = new ArrayList<>();
    for (List<Object[]> objs : getDynamicExcelField().values()) {
      columns.addAll(objs);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(" select ");
    for (Object[] obj : columns) {
      String str = (obj[0] == null ? "" : obj[0].toString());
      if (StringUtils.isNotEmpty(str)) {
        String[] cols = str.split("-");
        if ("look".equals(cols[0])) {
          sb.append("concat('look-" + cols[2] + "-'," + cols[1] + "),");
        } else {
          sb.append(cols[0] + ",");
        }
      }
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(" from " + getEntityClass().getName() + " t where id = ? ");
    Object[] data = baseDao.findObjArray(sb.toString(), Arrays.asList(id)).get(0);
    poiManager.expSingleObject(columns, data, key, fileName, response);
  }

  @Override
  public Map<String, List<Object[]>> getDynamicExcelField() {
    Class clz = getEntityClass();
    Map<String, List<Object[]>> map = new HashMap<>();
    for (Field field : clz.getDeclaredFields()) {
      if (field.isAnnotationPresent(DynamicExcel.class)) {
        DynamicExcel excel = field.getAnnotation(DynamicExcel.class);
        if (!excel.exclude()) {
          if (!excel.fetch()) {
            List<Object[]> list = map.get(excel.scope());
            if (list == null) {
              list = new ArrayList<>();
              map.put(excel.scope(), list);
            }
            if (excel.index() == -1) {
              list.add(new Object[]{excel.value(), excel.comment(), excel.rows(), excel.cell()});
            } else {
              int r = excel.index();
              if (r < list.size()) {
                list.add(r, new Object[]{excel.value(), excel.comment(), excel.rows(), excel.cell()});
              } else {
                list.add(new Object[]{excel.value(), excel.comment(), excel.rows(), excel.cell()});
              }
            }
          } else {
            getChildrenField(field, map);
          }
        }
      }
    }
    return map;
  }

  private void getChildrenField(Field field, Map<String, List<Object[]>> map) {
    DynamicExcel excel = field.getAnnotation(DynamicExcel.class);
    if (excel.fetch()) {
      Class clz = null;
      try {
        clz = Class.forName(field.getGenericType().getTypeName());
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      if (clz != null) {
        for (Field f : clz.getDeclaredFields()) {
          if (f.isAnnotationPresent(DynamicExcel.class)) {
            DynamicExcel e = f.getAnnotation(DynamicExcel.class);
            if (!StringUtils.isEmpty(e.parent())) {
              List<Object[]> list = map.get(excel.scope());
              if (list == null) {
                list = new ArrayList<>();
                map.put(excel.scope(), list);
              }
              list.add(new Object[]{e.parent(), e.comment(), excel.rows(), excel.cell()});
              getChildrenField(f, map);
            }
          }
        }
      }
    }
  }


  public void setChildren(List<Map<String, Object>> root, List<Map<String, Object>> list) {

    if (!root.isEmpty()) {
      for (Map<String, Object> map : root) {
        String parentId = map.get("id").toString();
        List<Map<String, Object>> children = new ArrayList<>();
        for (Map<String, Object> m : list) {
          if (parentId != null && parentId.equals(m.get("parentId"))) {
            if (map.get("children") == null) {
              map.put("children", children);
            }
            children = (List<Map<String, Object>>) map.get("children");
            children.add(m);
          }
        }
        if (!children.isEmpty()) {
          setChildren(children, list);
        }
      }
    }
  }

  protected String getUUID() {
    return UUID.randomUUID().toString().replace("-","");
  }


  @Resource
  public BaseDao<T> baseDao;

  @Resource
  public JdbcTemplate jdbcTemplate;

  @Resource
  public PoiManager poiManager;

}
