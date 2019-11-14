package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_ROLE_GROUP")
public class RoleGroup extends BaseEntity {

  @Column(name = "group_code")
  private String groupCode;//角色组代码

  private String name;//角色组名称

  private String remark;//备注

  private String seq;//排序号

  public RoleGroup() {
  }

  public RoleGroup(String code, String name, String seq) {
    this.groupCode = code;
    this.name = name;
    this.seq = seq;
  }

  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = groupCode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getRemark() {
    return remark;
  }

  public String getSeq() {
    return seq;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }


}