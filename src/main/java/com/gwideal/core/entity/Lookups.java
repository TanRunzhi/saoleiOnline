package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by li_hongyu on 14-7-21.
 */
@Entity
@Table(name = "T_CORE_LOOKUPS")
public class Lookups extends BaseEntity {
  @Column(name = "category_name")
  private String cName;

  @Column(name = "category_code")
  private String cCode;

  @Column(name = "lookups_name")
  private String lName;

  @Column(name = "lookups_code")
  private String lCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Lookups parent;

  private String description;

  private String seq;//排序号

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "module_id")
  private Module module;

  @OneToMany(targetEntity = Lookups.class, mappedBy = "parent", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @OrderBy(value = "lCode")
  private Set<Lookups> children = new HashSet<Lookups>();

  /** * 长宁档案系统  -  部分用户可以修改的字典项  -  归属部门 * **/
  @Getter
  @Setter
  private String departCode;

  @Getter
  @Setter
  private String departName;


  public String getcName() {
    return cName;
  }

  public void setcName(String cName) {
    this.cName = cName;
  }

  public String getcCode() {
    return cCode;
  }

  public void setcCode(String cCode) {
    this.cCode = cCode;
  }

  public String getlName() {
    return lName;
  }

  public void setlName(String lName) {
    this.lName = lName;
  }

  public String getlCode() {
    return lCode;
  }

  public void setlCode(String lCode) {
    this.lCode = lCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Lookups getParent() {
    return parent;
  }

  public void setParent(Lookups parent) {
    this.parent = parent;
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public Set<Lookups> getChildren() {
    return children;
  }

  public void setChildren(Set<Lookups> children) {
    this.children = children;
  }

  public String getSeq() {
    return seq;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }


}
