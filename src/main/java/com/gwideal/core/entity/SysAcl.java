package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_ACL")
public class SysAcl extends BaseEntity {

  /**
   * 资源名称
   **/
  @Getter
  @Setter
  private String name;


  /**
   * 描述
   **/
  @Getter
  @Setter
  private String remark;

  /**
   * 权限key
   **/
  @Getter
  @Setter
  private String aclKey;


  /**
   * 权限类型
   **/
  @Getter
  @Setter
  @Column(name = "acl_type")
  private String aclType;


  /**创建时间**/
  @Getter
  @Setter
  @Column(name = "create_date")
  private Date createDate;

  /**
   * 排序号
   **/
  @Getter
  @Setter
  private String seq;

  @ManyToMany(mappedBy = "acls", fetch = FetchType.EAGER)
  @Getter
  @Setter
  private Set<SysRole> roles = new HashSet<>();

  @ManyToMany(mappedBy = "acls", fetch = FetchType.EAGER)
  @Getter
  @Setter
  private Set<SysUser> users = new HashSet<>();


  public SysAcl() {
  }

  public SysAcl(String key, String name, String aclType) {
    this.aclKey = key;
    this.name = name;
    this.aclType = aclType;
  }

}