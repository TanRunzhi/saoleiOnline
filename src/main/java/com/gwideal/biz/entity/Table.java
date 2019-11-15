package com.gwideal.biz.entity;

import com.gwideal.base.entity.BusinessEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Table  {

  @Id
  @GeneratedValue(generator = "hibernate-uuid")
  @GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
  private String id;

  private Cell[][] cells;


}