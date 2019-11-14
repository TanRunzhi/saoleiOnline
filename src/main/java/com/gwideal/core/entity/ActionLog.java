package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "T_CORE_ACTION_LOG")
public class ActionLog extends BaseEntity {


  @Getter
  @Setter
  private Boolean flag = true;

  /**
   * 操作人
   */
  @Getter
  @Setter
  private String operator;

  /**
   * 时间
   */
  @Getter
  @Setter
  @Column(name = "operator_date")
  private String operatorDate;

  /**
   * 类型
   */
  @Getter
  @Setter
  @Column(name = "oper_type")
  private String operType;

  /**
   * 条数
   */
  @Getter
  @Setter
  @Column(name = "oper_num")
  private String operNum = "0";

  @Getter
  @Setter
  private String entity;

  @Getter
  @Setter
  private String entityId;

  @Getter
  @Setter
  private String bz;

  @Getter
  @Setter
  private String act;

  @Getter
  @Setter
  private String ip;

  @Getter
  @Setter
  private String sys;


  public ActionLog() {
  }


  public ActionLog(String sys, String ip, String operator, String operatorDate, String operType, String operNum, String entity, String entityId, String bz) {
    this.sys = sys;
    this.ip = ip;
    this.operator = operator;
    this.operatorDate = operatorDate;
    this.operType = operType;
    this.operNum = operNum;
    this.entity = entity;
    this.entityId = entityId;
    this.bz = bz;
  }

  public ActionLog(String sys, String ip, Boolean flag, String operator, String operatorDate, String operType, String operNum, String entity, String entityId, String bz, String act) {
    this.sys = sys;
    this.ip = ip;
    this.flag = flag;
    this.operator = operator;
    this.operatorDate = operatorDate;
    this.operType = operType;
    this.operNum = operNum;
    this.entity = entity;
    this.entityId = entityId;
    this.bz = bz;
    this.act = act;
  }
}

