package com.gwideal.biz.entity;

import com.gwideal.base.entity.BusinessEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
public class Person extends BusinessEntity {

  @Getter
  @Setter
  private String sessionId;

  public Integer getIntegerId(){
    return Integer.parseInt(this.getId());
  }

  public Person setId(Integer id){
    super.setId(id + "");
    return this;
  }

  public Person setSessionId(String sessionId){
    this.sessionId = sessionId;
    return this;
  }

}