package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table (name = "T_CORE_MODULE")
public class Module extends BaseEntity {

  private String name;//模块名称

  private String code;//模块编码

  private String seq;//排序号

  private String remark;//备注说明

  @Column (name = "reg_date")
  private Date regDate;//注册时间

  @Column (name = "is_active")
  private boolean activated = false;//是否启用

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }

  public String getSeq() {
    return seq;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getRemark() {
    return remark;
  }

  public void setRegDate(Date regDate) {
    this.regDate = regDate;
  }

  public Date getRegDate() {
    return regDate;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }
}