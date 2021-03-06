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
public class Person {

  @Id
  @GeneratedValue(generator = "hibernate-uuid")
  @GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
  private String id;

  private String sessionId;


  public Integer getIntegerId(){
    return Integer.parseInt(this.getId());
  }

  public Person setId(Integer id){
    this.id = id + "";
    return this;
  }

  public Person setSessionId(String sessionId){
    this.sessionId = sessionId;
    return this;
  }

}