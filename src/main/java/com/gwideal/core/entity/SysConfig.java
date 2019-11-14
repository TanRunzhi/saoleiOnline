package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by li_hongyu on 2015/05/29.
 */
@Entity
@Table (name = "T_CORE_CONFIG")
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE, region="com.gwideal.core.entity.SysConfig")
public class SysConfig extends BaseEntity {

  @Column (name = "cfg_key")
  private String cfgKey;//配置KEY

  @Column (name = "cfg_value")
  private String cfgValue;//配置VALUE

  @Column (name = "last_value")
  private String lastValue;//前一次配置的VALUE

  private String remark;

  public SysConfig() {
  }

  public SysConfig(String key, String value, String remark) {
    this.cfgKey = key;
    this.cfgValue = value;
    this.remark = remark;
  }

  public String getCfgKey() {
    return cfgKey;
  }

  public void setCfgKey(String cfgKey) {
    this.cfgKey = cfgKey;
  }

  public String getCfgValue() {
    return cfgValue;
  }

  public void setCfgValue(String cfgValue) {
    this.cfgValue = cfgValue;
  }

  public String getLastValue() {
    return lastValue;
  }

  public void setLastValue(String lastValue) {
    this.lastValue = lastValue;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
}
