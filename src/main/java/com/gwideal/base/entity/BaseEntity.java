package com.gwideal.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gwideal.util.codeHelper.CustomerCoder;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Resource;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by li_hongyu on 14-7-10.
 */
@MappedSuperclass
public class BaseEntity  implements Serializable{

  @Id
  @Column (name = "pid")
  @GeneratedValue (generator = "hibernate-uuid")
  @GenericGenerator (name = "hibernate-uuid", strategy = "uuid")
  private String id;

  //分页参数
  @Transient
  @JsonIgnore
  private PageParams pm = new PageParams();

  @Transient//查询参数
  @JsonIgnore
  private Map<String, String> params = new HashMap<String, String>();

  @Transient//临时传值参数
  @JsonIgnore
  private Map<String, String> tmpParams = new HashMap<String, String>();

  @Transient//支持多选查询
  @JsonIgnore
  private boolean ign_multiChk;

  @Transient
  @JsonIgnore
  private String op;//操作

  @Transient
  @JsonIgnore
  private String orderBy;//排序

  @Transient
  @JsonIgnore
  private String fid;//多附件Ids

  @Transient
  @JsonIgnore
  private String token;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PageParams getPm() {
    return pm;
  }

  public void setPm(PageParams pm) {
    this.pm = pm;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public boolean isIgn_multiChk() {
    return ign_multiChk;
  }

  public void setIgn_multiChk(boolean ign_multiChk) {
    this.ign_multiChk = ign_multiChk;
  }

  public String getOp() {
    return op;
  }

  public void setOp(String op) {
    this.op = op;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Map<String, String> getTmpParams() {
    return tmpParams;
  }

  public void setTmpParams(Map<String, String> tmpParams) {
    this.tmpParams = tmpParams;
  }

  public String getFid() {
    return fid;
  }

  public void setFid(String fid) {
    this.fid = fid;
  }

  public String getEncodedId() {
    return CustomerCoder.getBASE64(getId());
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
