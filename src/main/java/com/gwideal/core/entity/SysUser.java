package com.gwideal.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gwideal.base.entity.BusinessEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_USER")
public class SysUser extends BusinessEntity {

  /**
   * 登陆账号
   */
  @Getter
  @Setter
  private String account;

  /**
   * 真实姓名
   */
  @Getter
  @Setter
  @Column(name = "real_name")
  private String realName;

  /**
   * 密码
   */
  @Getter
  @Setter
  private String pwd;

  /**
   *
   */
  @Getter
  @Setter
  private String phone;

  /**
   * 电子邮件
   */
  @Getter
  @Setter
  private String mail;

  /**
   * 账号类型
   */
  @Getter
  @Setter
  private String type;

  @Column(name = "unlock_time")
  @Getter
  @Setter
  private Date unlockTime;

  @Column(name = "login_failed_times")
  @Getter
  @Setter
  private Integer loginFailedTimes = 0;

  @Column(name = "last_login_time")
  @Getter
  @Setter
  private Date lastLoginTime;

  @Column(name = "last_login_ip")
  @Getter
  @Setter
  private String lastLoginIp;

  /**
   * 操作意见
   */
  @Transient
  @Getter
  @Setter
  private String option;


  @Column(name = "create_time")
  @Getter
  @Setter
  private String createTime;


  @Getter
  @Setter
  private String state;


  /**
   * 所属单位
   */
  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "depart_id")
  private SysDepart depart;


  @Getter
  @Setter
  @ManyToMany(mappedBy = "users",fetch=FetchType.EAGER)
  @JsonIgnore
  private Set<SysRole> roles = new HashSet<>();

  @Getter
  @Setter
  @ManyToMany(targetEntity = SysAcl.class, cascade = {CascadeType.PERSIST,
      CascadeType.REFRESH, CascadeType.MERGE},fetch=FetchType.EAGER)
  @JoinTable(name = "t_core_acl_user", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "acl_id")})
  @OrderBy("seq asc")
  @JsonIgnore
  private Set<SysAcl> acls = new HashSet<>();


  public SysUser() {
  }

  public SysUser(String id) {
    super.setId(id);
  }

  public SysUser(String id, String account, String realName, String type) {
    super.setId(id);
    this.account = account;
    this.realName = realName;
    this.type = type;
  }

  public SysUser(String account, String realName, String pwd, String phone, String mail, String type, Integer loginFailedTimes) {
    this.account = account;
    this.realName = realName;
    this.pwd = pwd;
    this.phone = phone;
    this.mail = mail;
    this.type = type;
    this.loginFailedTimes = loginFailedTimes;
  }

  public SysUser(String account, String realName, String pwd, SysDepart depart, String type) {
    this.account = account;
    this.realName = realName;
    this.pwd = pwd;
    this.depart = depart;
    this.type = type;
  }

  public String getDepartId(){
    return depart == null ? "" : depart.getId();
  }

  public String getDepartName(){
    return depart == null ? "" : depart.getName();
  }

}