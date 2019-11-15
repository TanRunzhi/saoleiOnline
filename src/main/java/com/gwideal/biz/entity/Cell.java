package com.gwideal.biz.entity;

import com.gwideal.base.entity.BusinessEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Cell {

  @Id
  @GeneratedValue(generator = "hibernate-uuid")
  @GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
  private String id;

  private Integer row;

  private Integer col;

  private int num = 0;

  /**
   * 0 默认 1 被右键插旗  2 被左键点开  -1 左键点到炸弹
   * */
  private int state = 0;

  private boolean hasBoom = false;

  private int player = 0;

}