package com.gwideal.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import javax.annotation.Resource;

/**
 * @author zzy
 * 任务管理
 */

public class JobManager {

  @Resource
  private Scheduler scheduler;

  @Autowired
  private AutowireCapableBeanFactory capableBeanFactory;


  /**
   * 添加一个定时任务
   */
  public void addJob(AutoJobDTO job) throws SchedulerException {

    //这里获取任务信息数据
    TriggerKey triggerKey = TriggerKey.triggerKey(job.getJob_name(), job.getJob_group());
    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
    if (trigger == null) {
      //不存在，创建一个
      JobDetail jobDetail = null;
      //动态job生成,指定业务类和业务方法,即可生成对象
      try {
        //生成目标对象
        Class clz2 = Class.forName(job.getClassName());
        Object obj = clz2.newInstance();
        capableBeanFactory.autowireBean(obj);
        //装配对应属性
        MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        methodInvokingJobDetailFactoryBean.setTargetMethod(job.getMethodName());
        methodInvokingJobDetailFactoryBean.setGroup(job.getJob_group());
        methodInvokingJobDetailFactoryBean.setName(job.getJob_name());
        methodInvokingJobDetailFactoryBean.setTargetObject(obj);
        methodInvokingJobDetailFactoryBean.afterPropertiesSet();
        jobDetail = methodInvokingJobDetailFactoryBean.getObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }

      jobDetail.getJobDataMap().put("scheduleJob", job);
      //表达式调度构建器
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getJob_time());
      //按新的cronExpression表达式构建一个新的trigger
      trigger = TriggerBuilder.newTrigger().withIdentity(job.getJob_name(), job.getJob_group()).withSchedule(scheduleBuilder).build();
      scheduler.scheduleJob(jobDetail, trigger);
    } else {
      // Trigger已存在，那么更新相应的定时设置
      //表达式调度构建器
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getJob_time());
      //按新的cronExpression表达式重新构建trigger
      trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
      //按新的trigger重新设置job执行
      scheduler.rescheduleJob(triggerKey, trigger);
    }
  }

  /**
   * 禁用定时任务
   *
   * @param job
   */
  public void disabled(AutoJobDTO job) {
    try {
      Trigger trigger = this.scheduler.getTrigger(TriggerKey.triggerKey(job.getJob_name(), job.getJob_group()));
      if (null != trigger) {
        scheduler.deleteJob(JobKey.jobKey(job.getJob_name(), job.getJob_group()));
      }
    } catch (SchedulerException e) {
      e.printStackTrace();
      // TODO
    }
  }

  /**
   * 启动所有定时任务
   */
  public void startJobs() {
    try {
      scheduler.start();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }


  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * 初始化定时任务
   *
   * @throws SchedulerException
   */
  public void loadJobInit() throws SchedulerException {
    System.out.println("init---");
    AutoJobDTO job = new AutoJobDTO();
   /* job.setClassName("com.gwideal.task.TaskJob");
    job.setMethodName("executeJob");
    job.setJob_id("Id1");
    job.setJob_name("Name1");
    job.setJob_group("linGroup");
    job.setJob_time("0/30 * * * * ?");*/
    addJob(job);
    startJobs();
  }

}
