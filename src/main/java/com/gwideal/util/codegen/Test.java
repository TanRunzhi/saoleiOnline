package com.gwideal.util.codegen;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {


  public static void main(String[] args) throws Exception {
    Map<String,Object> data = new HashMap<>();

    //组织input元素,  {}包含用 map组织
    Map<String,Object> input = new HashMap<>();

    //组织input下LISTFROZ,[]:集合,用list组织
    List<Map> list  = new ArrayList<>();

    //[]下元素为{},所以用map组织
    Map<String,Object> m = new HashMap<>();
    m.put("HOLD_STOP_DATE","20400630");

    //构造元素放进[]内
    list.add(m);

    //input 添加属性
    input.put("CHECK_PASSWORD_FLAG","0");
    input.put("LISTFROZ",list);

    //comm_req
    Map<String,Object> comm_req = new HashMap<>();
    comm_req.put("RECON_CODE","");

    //放input、comm_req
    data.put("input",input);
    data.put("comm_req",comm_req);
    System.out.println(JSON.toJSONString(data));
  }

}

