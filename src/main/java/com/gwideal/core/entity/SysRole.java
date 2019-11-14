package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_ROLE")
public class SysRole extends BaseEntity {

  /**
   * 角色名称
   */
  private String name;

  /**
   * 角色代码
   */
  private String code;

  /**
   * 描述
   */
  private String remark;

  /**
   * 排序
   */
  private String seq;

  @ManyToOne
  @JoinColumn(name = "group_id")
  private RoleGroup roleGroup;

  @ManyToMany(targetEntity = SysUser.class, cascade = {CascadeType.PERSIST,
      CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(name = "t_core_user_role", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
  @OrderBy("account asc")
  private Set<SysUser> users = new HashSet<>();

  @ManyToMany(targetEntity = SysAcl.class, cascade = {CascadeType.PERSIST,
      CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(name = "t_core_role_acl", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "acl_id")})
  @OrderBy("seq asc")
  private Set<SysAcl> acls = new HashSet<>();

  public SysRole() {
  }

  public SysRole(String code, String name, String seq, RoleGroup group) {
    this.name = name;
    this.code = code;
    this.seq = seq;
    this.roleGroup = group;
  }

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

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getSeq() {
    return seq;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }

  @OrderBy("id asc")
  public Set<SysUser> getUsers() {
    return users;
  }

  public void setUsers(Set<SysUser> users) {
    this.users = users;
  }

  @OrderBy("id asc")
  public Set<SysAcl> getAcls() {
    return acls;
  }

  public void setAcls(Set<SysAcl> acls) {
    this.acls = acls;
  }

  public RoleGroup getRoleGroup() {
    return roleGroup;
  }

  public void setRoleGroup(RoleGroup roleGroup) {
    this.roleGroup = roleGroup;
  }

}