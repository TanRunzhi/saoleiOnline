package com.gwideal.util.codegen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by li_hongyu on 14-7-21.
 */
public class CodeGenerator {

  private static final Logger log = LogManager.getLogger();

  private static final String[] excludeFields = new String[]{"pid", "flag", "update_time", "updater"};
  private static final String model = "com.gwideal.biz.entity.Envelop";

  private static final String manager = "com.gwideal.biz.manager.EnvelopMng";

  private static final String dao = "com.gwideal.biz.dao.EnvelopDao";

  private static final String action = "com.gwideal.biz.action.EnvelopAct";

  private static final String table = "tbl_envelop";

  private static final String accessUrl = "/envelop";

  public static void main(String[] args) throws Exception {
    log.debug("代码生成开始");

    EntityGen.gen(model, table, excludeFields);

    //DaoGen.gen(dao, model);

    ManagerGen.gen(manager, model);

    ActionGen.gen(action, manager, accessUrl, "archives/envelop");

    //System.out.println(Base64.getEncoder().encodeToString("sfzc-admin".getBytes("utf-8")));

    log.debug("代码生成结束");

  }
}
