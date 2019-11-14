package com.gwideal.core.manager.impl;

import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.SysConfig;
import com.gwideal.core.manager.SysConfigMng;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service ("sysConfigMng")
@Transactional
public class SysConfigMngImpl extends BaseMngImpl<SysConfig> implements SysConfigMng {


  /**
   * 通过传入键获得数据库存储的配置值
   *
   * @param key 键
   * @return 值
   */
  @Override
  public String getCacheValue(String key) {
    List<Map<String, Object>> l = jdbcTemplate.queryForList("select cfg_Value as val from t_core_config where cfg_key = ?", key);
    if (l.size() > 0)
      return (String) l.get(0).get("val");
    return null;
  }

  /**
   * 修改参数
   *
   * @param key    要修改参数的key
   * @param value  要修改的值
   * @param remark 修改原因
   */
  @Override
  public void setValue(String key, String value, String remark) {
    List<SysConfig> l = findBy("cfgKey", key, "");
    if (l.size() == 0) {
      saveOrUpdate(new SysConfig(key, value, remark));
    } else {
      SysConfig s = findBy("cfgKey", key, "").get(0);
      s.setLastValue(s.getCfgValue());
      s.setCfgValue(value);
      s.setRemark(remark);
      saveOrUpdate(s);
    }
  }

  @Override
  public String getValue(String key) {
    List<Map<String, Object>> l = jdbcTemplate.queryForList("select cfg_Value as val from t_core_config where cfg_key = ?", key);
    if (l.size() > 0)
      return (String) l.get(0).get("val");
    return null;
  }
}
