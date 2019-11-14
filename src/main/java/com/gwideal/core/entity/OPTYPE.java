package com.gwideal.core.entity;

import java.util.EnumSet;

/**
 * 操作日志、操作类型枚举
 *
 * @author Administrator
 */
public enum OPTYPE {
  ADD("新增"), DEL("删除"), EDIT("修改"), SAVE("保存"), IMPORT("导入"), PUBLISH("发布"),RESET("重置"),
  EXPORT("导出"), AUDIT("审核"), DOWN("下载"), BATCH_SUCC("批量通过"),ANSWER("答复"),STICK("置顶"),UNSTICK("取消置顶"),
  FAILED("退回"), REVOKE("撤销"), SUCCESS("通过"), LOGIN("登录"), LOGOUT("登出"),LOOK("查看"),SEND("发送")
  ,REMIND("催报"),OPEN("开启"),ABOLISH("废除"),AJAX_CANCEL("REVOKE");

  private String name;

  OPTYPE(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public static void main(String[] args) {
    EnumSet<OPTYPE> currEnumSet = EnumSet.allOf(OPTYPE.class);
    for (OPTYPE aLightSetElement : currEnumSet) {
      System.out.println("当前EnumSet中数据为：" + aLightSetElement);
    }
    System.out.println(OPTYPE.DEL.getName());
  }
}
