package com.gwideal.base.entity;

import com.gwideal.core.entity.SysUser;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by farno on 2015/10/19.
 */
@MappedSuperclass
public class BusinessEntity extends BaseEntity {
  private Boolean flag = true;

  @ManyToOne
  @JoinColumn(name = "updater")
  private SysUser updater;

  @Column(name = "update_time")
  private Date updateTime;

  public Boolean getFlag() {
    return flag;
  }

  public void setFlag(Boolean flag) {
    this.flag = flag;
  }

  public SysUser getUpdater() {
    return updater;
  }

  public void setUpdater(SysUser updater) {
    this.updater = updater;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

}
