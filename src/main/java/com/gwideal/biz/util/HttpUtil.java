package com.gwideal.biz.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {

  public static String post(String urlStr) {
    try {
      return post(urlStr, null, null, null);
    } catch (Exception e) {
      return null;
    }
  }

  public static JSONObject loadJsonByPostContent(String urlStr) {
    try {
      Map<String, String> header = new HashMap<>();
      header.put("Content-Type", "application/x-www-form-urlencoded");
      String msg = post(urlStr, header, null, null);
      return JSON.parseObject(msg);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static JSONObject loadJsonByJsonTypePost(String urlStr, String json) {
    try {
      Map<String, String> header = new HashMap<>();
      header.put("Content-Type", "application/json");
      String msg = post(urlStr, header, null, new StringEntity(json, ContentType.APPLICATION_JSON));
      return JSON.parseObject(msg);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * httpClient post请求
   *
   * @param url    请求url
   * @param header 头部信息
   * @param param  请求参数 form提交适用
   * @param entity 请求实体 json/xml提交适用
   * @return 可能为空 需要处理
   * @throws Exception
   */
  public static String post(String url, Map<String, String> header, Map<String, String> param, HttpEntity entity) throws Exception {
    String result = "";
    CloseableHttpClient httpClient = null;
    try {
      httpClient = getHttpClient();
      HttpPost httpPost = new HttpPost(url);
      // 设置头信息
      if (MapUtils.isNotEmpty(header)) {
        for (Map.Entry<String, String> entry : header.entrySet()) {
          httpPost.addHeader(entry.getKey(), entry.getValue());
        }
      }
      // 设置请求参数
      if (MapUtils.isNotEmpty(param)) {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
          //给参数赋值
          formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
      }
      // 设置实体 优先级高
      if (entity != null) {
        httpPost.setEntity(entity);
      }
      HttpResponse httpResponse = httpClient.execute(httpPost);
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        HttpEntity resEntity = httpResponse.getEntity();
        result = EntityUtils.toString(resEntity, "utf-8");
      } else {
        readHttpResponse(httpResponse);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      if (httpClient != null) {
        httpClient.close();
      }
    }
    return result;
  }

  public static CloseableHttpClient getHttpClient() throws Exception {
    CloseableHttpClient httpClient = HttpClients.custom()
        .build();
    return httpClient;
  }

  public static String readHttpResponse(HttpResponse httpResponse)
      throws ParseException, IOException {
    StringBuilder builder = new StringBuilder();
    // 获取响应消息实体
    HttpEntity entity = httpResponse.getEntity();
    // 响应状态
    builder.append("status:" + httpResponse.getStatusLine());
    builder.append("headers:");
    HeaderIterator iterator = httpResponse.headerIterator();
    while (iterator.hasNext()) {
      builder.append("\t" + iterator.next());
    }
    // 判断响应实体是否为空
    if (entity != null) {
      String responseString = EntityUtils.toString(entity);
      builder.append("response length:" + responseString.length());
      builder.append("response content:" + responseString.replace("\r\n", ""));
    }
    return builder.toString();
  }
}
