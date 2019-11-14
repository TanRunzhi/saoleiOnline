package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_CORE_DEPART")
public class SysDepart extends BaseEntity {

  /**
   * 部门名称
   */
  @Getter
  @Setter
  private String name;


  /**
   * 上级部门
   */
  @ManyToOne
  @JoinColumn(name = "parent_id")
  @Getter
  @Setter
  private SysDepart parent;

  /**
   * 单位代码
   */
  @Getter
  @Setter
  private String code;

  /**
   * 是否有效
   */
  @Getter
  @Setter
  private Boolean flag = true;

  /**
   * 排序
   */
  @Getter
  @Setter
  private String seq = "00";

  @Getter
  @Setter
  @OneToMany(targetEntity = SysDepart.class, mappedBy = "parent", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @OrderBy(value = "seq")
  private Set<SysDepart> children = new HashSet<>();


}