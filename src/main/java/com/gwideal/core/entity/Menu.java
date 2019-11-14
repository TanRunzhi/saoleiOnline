package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_MENU")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.gwideal.core.entity.Menu")
public class Menu extends BaseEntity {

  private boolean flag = true;

  private String name;//栏目名称

  private String url;//栏目链接

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Menu parent;//父栏目

  @OneToMany(targetEntity = Menu.class, mappedBy = "parent", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @OrderBy(value = "seq")
  private Set<Menu> children = new HashSet<Menu>();//子栏目

  private String icon;//图标

  private String seq;//排序号

  private String code;//栏目代码

  private String remark;//备注

  @ManyToOne
  @JoinColumn(name = "module_id")
  private Module module;//所属模块

  @Column(name = "acl_key")
  private String aclKey;//权限Key

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public Menu getParent() {
    return parent;
  }

  public void setParent(Menu parent) {
    this.parent = parent;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getIcon() {
    return icon;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }

  public String getSeq() {
    return seq;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Set<Menu> getChildren() {
    return children;
  }

  public void setChildren(Set<Menu> children) {
    this.children = children;
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public String getAclKey() {
    return aclKey;
  }

  public void setAclKey(String aclKey) {
    this.aclKey = aclKey;
  }
}