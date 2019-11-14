package com.gwideal.quartz;

import lombok.Data;

/**
 * @author zzy
 * 动态任务类
 */
@Data
public class AutoJobDTO {
  /**
   * 任务id
   */
  private String job_id;
  /**
   * 任务名称
   */
  private String job_name;
  /**
   * 任务分组
   */
  private String job_group;
  /**
   * cron 表达式
   */
  private String job_time;
  /**
   * 定时 业务类
   */
  private String className;
  /**
   * 定时 业务方法
   */
  private String methodName;
  /**
   * 定时 任务状态 0：启用；1：禁用；2：已删除
   */
  private Integer jobStatus;

}
