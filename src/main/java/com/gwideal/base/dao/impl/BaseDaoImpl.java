package com.gwideal.base.dao.impl;

import com.gwideal.base.dao.BaseDao;
import com.gwideal.base.entity.BaseEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by li_hongyu on 14-7-11.
 */
@Repository("baseDao")
public class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {

  @Resource
  private SessionFactory sessionFactory;

  @Resource
  private JdbcTemplate jdbcTemplate;

  /**
   * 这个通常也是hibernate的取得子类class的方法
   *
   * @author "yangk"
   * @date 2010-4-11 下午01:51:28
   */
   /* private Class getEntityClass () {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = (( ParameterizedType ) genType).getActualTypeArguments();
        return ( Class ) params[0];
    }*/
  public Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public T load(Class clazz, Serializable id) {
    return (T) getCurrentSession().load(clazz, id);
  }

  public T save(T o) {
    escapeHtml(o);
    getCurrentSession().save(o);
    return o;
  }

  public void delete(T o) {
    getCurrentSession().delete(o);
  }

  public void update(T o) {
    getCurrentSession().update(o);
  }

  private void escapeHtml(T o) {
    for (Field f : o.getClass().getDeclaredFields()) {
      if (f.getType().getTypeName().contains("String")) {
        f.setAccessible(true);
        try {
          f.set(o, StringEscapeUtils.unescapeHtml((String) f.get(o)));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void saveOrUpdate(T o) {
    escapeHtml(o);
    getCurrentSession().saveOrUpdate(o);
  }

  public void merge(T o) {
    getCurrentSession().merge(o);
  }

  public List<T> find(String hql) {
    return getCurrentSession().createQuery(hql).list();
  }

  public List<T> find(String hql, Object[] param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.length > 0) {
      for (int i = 0; i < param.length; i++) {
        q.setParameter(i, param[i]);
      }
    }
    return q.list();
  }

  public List<T> find(String hql, List<Object> param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.
        size() > 0) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return q.list();
  }

  public List<T> find(String hql, Map<String, Object> params) {
    Query q = getCurrentSession().createQuery(hql);
    if (params != null && !params.isEmpty()) {
      for (Map.Entry<String, Object> entry : params.entrySet())
        if (String.class.equals(entry.getValue().getClass())) {
          if (!StringUtils.isEmpty((String) entry.getValue()))
            q.setParameter(entry.getKey(), entry.getValue());
        } else
          q.setParameter(entry.getKey(), entry.getValue());
    }
    return q.list();
  }

  @Override
  public List<T> topFind(String hql, Integer maxResult, Object[] values) {
    Query q = getCurrentSession().createQuery(hql);
    if (values != null && values.length > 0) {
      for (int i = 0; i < values.length; i++) {
        q.setParameter(i, values[i]);
      }
    }
    q.setMaxResults(maxResult);
    return q.list();
  }

  public List<T> find(String hql, Object[] param, Integer page, Integer rows) {
    if (page == null || page < 1) {
      page = 1;
    }
    if (rows == null || rows < 1) {
      rows = 10;
    }
    Query q = this.getCurrentSession().createQuery(hql);
    if (param != null && param.length > 0) {
      for (int i = 0; i < param.length; i++) {
        q.setParameter(i, param[i]);
      }
    }
    return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
  }

  public List<T> find(String hql, List<Object> param, Integer page, Integer pageSize) {
    if (page == null || page < 1) {
      page = 1;
    }
    if (pageSize == null || pageSize < 1) {
      pageSize = 10;
    }
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.size() > 0) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return q.setFirstResult((page - 1) * pageSize).setMaxResults(pageSize).list();
  }

  public T get(Class<T> c, Serializable id) {
    return (T) getCurrentSession().get(c, id);
  }

  public T get(String hql, Object[] param) {
    List<T> l = find(hql, param);
    if (l != null && l.size() > 0) {
      return l.get(0);
    } else {
      return null;
    }
  }

  public T get(String hql, List<Object> param) {
    List<T> l = find(hql, param);
    if (l != null && l.size() > 0) {
      return l.get(0);
    } else {
      return null;
    }
  }

  public Integer count(String hql) {
    return Integer.parseInt(String.valueOf(getCurrentSession().createQuery(hql).uniqueResult()));
  }

  public Long count(String hql, Object[] param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.length > 0) {
      for (int i = 0; i < param.length; i++) {
        q.setParameter(i, param[i]);
      }
    }
    return (Long) q.uniqueResult();
  }

  public Integer count(String hql, List<Object> param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.size() > 0) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return Integer.parseInt(String.valueOf(q.uniqueResult()));
  }

  public Integer count(String hql, Map<String, Object> params) {
    Query q = getCurrentSession().createQuery(hql);
    if (params != null && !params.isEmpty()) {
      for (Map.Entry<String, Object> entry : params.entrySet())
        if (String.class.equals(entry.getValue().getClass())) {
          if (!StringUtils.isEmpty((String) entry.getValue()))
            q.setParameter(entry.getKey(), entry.getValue());
        } else
          q.setParameter(entry.getKey(), entry.getValue());
    }
    return Integer.parseInt(String.valueOf(q.uniqueResult()));
  }

  public Integer executeHql(String hql) {
    return getCurrentSession().createQuery(hql).executeUpdate();
  }

  public Integer executeHql(String hql, Object[] param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.length > 0) {
      for (int i = 0; i < param.length; i++) {
        q.setParameter(i, param[i]);
      }
    }
    return q.executeUpdate();
  }

  public Integer executeHql(String hql, List<Object> param) {
    Query q = getCurrentSession().createQuery(hql);
    if (param != null && param.size() > 0) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return q.executeUpdate();
  }

  public List<T> createQuery(String hql, Map<String, String> params) {
    Query q = getCurrentSession().createQuery(hql);
    if (!params.isEmpty()) {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        q.setParameter(entry.getKey(), "%" + entry.getValue() + "%");
      }
    }
    return q.list();
  }

  public List<T> findEntityListBySql(String sql, Class entityClass, Object... args) {
    return jdbcTemplate.queryForList(sql, entityClass, args);
  }

  public List<T> findEntityListBySqlPage(String sql, Class entityClass, int pageIndex, int page, Object... args) {
    return jdbcTemplate.queryForList(sql, entityClass, args);
  }

  public List<Map<String, Object>> findMapListBySql(String sql, Object... args) {
    return jdbcTemplate.queryForList(sql, args);
  }

  @Override
  public List<Object[]> findPageObjArray(String hql, List<Object> param, Integer page, Integer rows) {
    if (page == null || page < 1) {
      page = 1;
    }
    if (rows == null || rows < 1) {
      rows = 10;
    }
    Query q = this.getCurrentSession().createQuery(hql);
    if (param != null && param.size() > 0) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
  }

  @Override
  public List<Object[]> findObjArray(String hql, List<Object> param) {
    Query q = getCurrentSession().createQuery(hql);
    if (!param.isEmpty()) {
      for (int i = 0; i < param.size(); i++) {
        q.setParameter(i, param.get(i));
      }
    }
    return q.list();
  }
}
