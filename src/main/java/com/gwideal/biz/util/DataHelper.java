package com.gwideal.biz.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwideal.util.io.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataHelper {

  private final static Logger logger = LogManager.getLogger();

  /**
   * 获取token
   *
   * @return
   */
  public static String getToken() {
    JSONObject json = HttpUtil.loadJsonByPostContent(PropertiesReader.getPropertiesValue("auth.base")
        + PropertiesReader.getPropertiesValue("auth.token"));
    return json == null ? "" : json.getString("access_token");
  }

  /**
   * 生成统一事项编码
   *
   * @return
   */
  public static String getCode() {
    Map<String, String> param = new HashMap<>();
    param.put("accessToken", getToken());
    param.put("itemCode", PropertiesReader.getPropertiesValue("auth.union.code"));
    JSONObject json = loadJson(PropertiesReader.getPropertiesValue("auth.code"), param);
    if (json != null) {
      return json.getString("applyNo");
    }
    return "123456";
  }

  /**
   * 获取用户信息
   *
   * @param token
   * @return
   */
  public static JSONObject getUser(String token) {
    Map<String, String> param = new HashMap<>();
    param.put("accessToken",getToken());
    param.put("portalToken", token);
    return loadJson(PropertiesReader.getPropertiesValue("auth.user"), param);
  }

  public static JSONObject loadJson(String url, Map<String, String> params) {
    JSONObject json = HttpUtil.loadJsonByJsonTypePost(PropertiesReader.getPropertiesValue("auth.base") + url, JSON.toJSONString(params));
    logger.debug(JSON.toJSONString(json));
    if (json != null && json.getBoolean("isSuccess")) {
      return (JSONObject) json.get("data");
    }
    return null;
  }

  public static void main(String[] args) {
    //System.out.println(DataHelper.getToken());
    DataHelper.getUser("d74b2897-04b6-4f52-8164-d8d58a7cc459");
  }
}
