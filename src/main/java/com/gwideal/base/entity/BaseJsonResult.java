package com.gwideal.base.entity;

import com.alibaba.fastjson.JSON;
import com.gwideal.util.io.PropertiesReader;

import java.util.HashMap;

/**
 * Created by li_hongyu on 2015/07/20.
 */
public class BaseJsonResult extends HashMap<String, Object> {
  public BaseJsonResult() {
    put("success", true);
    put("message", PropertiesReader.getPropertiesValue("msg.restful.success"));
  }

  public BaseJsonResult(boolean success, Object message) {
    put("success", success);
    put("message", message);
  }

  public String toJSONString(){
    return JSON.toJSONString(this);
  }

  public BaseJsonResult setData(Object data){
    this.put("data",data);
    return this;
  }
}
