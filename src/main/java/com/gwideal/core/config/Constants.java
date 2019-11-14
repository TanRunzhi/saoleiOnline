package com.gwideal.core.config;


/**
 * Created by li_hongyu on 2015/06/01.
 * 定义系统中用到的一些常量
 */
public class Constants {

  /**
   * 上传文件临时文件路径
   */
  public final static String LOCAL_FILE_UPLOAD_TEMP_BASE = "sys.upload.tempFolder";

  /**
   * 系统管理白名单
   */
  public final static String SYS_WHITE_LIST = "sys.manager.whitelist";

  public final static String SPLIT_CHAR = ",";

  public final static String Role_SystemAdmin = "info";

  /**
   * 密级权限_内部
   */
  public final static String Role_Secret_NeiBu = "cnarchives.others.secretInternal";
  /**
   * 密级权限_控制
   */
  public final static String Role_Secret_KongZhi = "cnarchives.others.secretUnderControl";
  /**
   * 密级权限_秘密
   */
  public final static String Role_Secret_MiMi = "cnarchives.others.secretSecret";
  /**
   * 密级权限_机密
   */
  public final static String Role_Secret_JiMi = "cnarchives.others.secretTopSecret";
  /**
   * 密级权限_绝密
   */
  public final static String Role_Secret_JueMi = "cnarchives.others.secretSuperSecret";


  public final static String ArchStatus_Archived = "Archived";

  public static String TemplateType_Archives = "Archives";

  /**
   * 集团本部人员的权限点
   * */
  public static String CN_DEPART_ACLKEY = "cnarchives.cndepart";

  /**
   * 集团管理员的权限点
   * */
  public static String CN_DEPARTADMIN_ACLKEY ="cnarchives.cndepartAdmin";

  /**
   * 公司人员的权限点
   * */
  public static String CN_CORP_ACLKEY = "cnarchives.corpdepart";

  /**
   * 模块code
   * */
  public static String biz_Module_code = "cnarchives";


  /**
   * 模块code
   * */
  public static String BIZ_MODULE_USER_CODE = "userManageLookups";

  /**
   * 档案借阅管理员权限
   * */
  public static String ACL_BORROW_MANAGE = "cnarchives.manage.borrow.manage";

  /**
   * 档案借阅领导权限
   * */
  public static String ACL_BORROW_LEADER = "cnarchives.manage.borrow.leader";


}

