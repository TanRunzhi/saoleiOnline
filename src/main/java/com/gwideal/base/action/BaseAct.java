package com.gwideal.base.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.base.entity.PageParams;
import com.gwideal.core.entity.*;
import com.gwideal.core.excel.PoiManager;
import com.gwideal.core.manager.*;
import com.gwideal.util.io.PropertiesReader;
import com.gwideal.util.json.JSONHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangyinghu on 15/6/30.
 */
public class BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @ExceptionHandler(Exception.class)
  public String handle(Exception e, HttpServletRequest request) {
    String msg = "出现错误,请联系开发人员!";
    if (e instanceof NoSuchMethodException) {
      msg = "资源不存在!";
    } else if (e instanceof HttpMessageNotWritableException) {
      msg = "服务器错误,请联系开发人员!";
    }
    e.printStackTrace();
    request.setAttribute("msg", msg);
    request.setAttribute("errordetails", e.getMessage());
    return "syserror";
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public String handle() {
    return JSONHelper.formatObject(new BaseJsonResult(false, PropertiesReader.getPropertiesValue("msg.restful.invalidParams")));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseBody
  public String doException(Exception e, HttpServletRequest request) throws Exception {
    Map<String, String> map = new HashMap<>();
    if (e instanceof MaxUploadSizeExceededException) {
      long maxSize = ((MaxUploadSizeExceededException) e).getMaxUploadSize();
      map.put("message", "上传文件太大，不能超过" + maxSize / 1024 / 1024 + "M");
    } else if (e instanceof RuntimeException) {
      map.put("message", "未选中文件");
    } else {
      map.put("message", "上传失败");
    }
    return JSON.toJSONString(map);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    //binder.setAutoGrowCollectionLimit(2048);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    //true:允许输入空值，false:不能为空值
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

  protected Map<String, Object> getKeyMap(HttpSession s) {
    return (Map<String, Object>) s.getAttribute("keyMap");
  }

  public SysUser getCurrentUser(HttpSession s) {
    return (SysUser) s.getAttribute("currentUser");
  }

  protected SysDepart getCurrentDept(HttpSession s) {
    return getCurrentUser(s).getDepart();
  }

  protected boolean isAdminDepart(HttpSession s) {
    Boolean bool = (Boolean) s.getAttribute("isAdminDepart");
    return bool != null && bool;
  }

  protected boolean isAdmin(HttpSession s) {
    Boolean bool = (Boolean) s.getAttribute("isAdmin");
    return bool != null && bool;
  }

  public Menu getCurrentMenu(HttpSession s) {
    return (Menu) s.getAttribute("crtMenu");
  }

  public Module getCurrentModule(HttpSession s) {
    return (Module) s.getAttribute("crtModule");
  }

  public List<String> getUserAcls(HttpSession s) {
    if (s.getAttribute("userAcls") != null)
      return (List<String>) s.getAttribute("userAcls");
    else {
      List<String> acls = sysUserMng.getAclKeyByUserId(getCurrentUser(s).getId());
      s.setAttribute("userAcls", acls);
      return acls;
    }
  }

  protected String getIp(HttpSession s) {
    return s.getAttribute("ip") == null ? "" : s.getAttribute("ip").toString();
  }

  protected void downloadFile(String objId, String group, String attId, boolean preview, HttpServletResponse response, HttpServletRequest request) throws IOException {
    logger.debug("Attachment download fired");
    Attachment att;
    if (StringUtils.isNotEmpty(objId)) {
      att = attachmentMng.findByObjectId(objId, group).get(0);
    } else {
      att = attachmentMng.get(attId);
    }
    att.setCount(att.getCount() + 1);
    attachmentMng.saveOrUpdate(att);
    OutputStream os = response.getOutputStream();
    try {
      response.reset();
      if (preview) {
        response.setHeader("Content-Disposition", "inline; filename="
            + getFileNameByBrowser(att.getDisplayName(), request));
        response.setContentType("application/pdf; charset=utf-8");
      } else {
        response.setHeader("Content-Disposition", "attachment; filename="
            + getFileNameByBrowser(att.getDisplayName(), request));
        response.setContentType("application/octet-stream; charset=utf-8");
      }
      os.write(FileUtils.readFileToByteArray(new File(sysConfigMng.getCacheValue("sys.upload.tempFolder") + att.getFilePath())));
      os.flush();
    } finally {
      if (os != null) {
        os.close();
      }
    }
  }

  protected void responseFile(String fileName, File file , HttpServletResponse response, HttpServletRequest request){
    OutputStream os = null;
    try {
      os = response.getOutputStream();
      response.reset();
      response.setHeader("Content-Disposition", "attachment; filename="
          + getFileNameByBrowser(fileName, request));
      response.setContentType("application/octet-stream; charset=utf-8");
      os.write(FileUtils.readFileToByteArray(file));
      os.flush();
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      if (os != null) {
        try{
          os.close();
        } catch (Exception ee){
          ee.printStackTrace();
        }
      }
    }
  }

  private Boolean isFireFox(HttpServletRequest request) {
    logger.debug(request.getHeader("user-agent"));
    return request.getHeader("user-agent").contains("Firefox");

  }

  private Boolean isIE(HttpServletRequest request) {
    String userAgent = request.getHeader("user-agent");
    return userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edge");
  }

  protected String getFileNameByBrowser(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
    return isFireFox(request) ?
        new String(fileName.getBytes(PropertiesReader.getPropertiesValue("sys.file.getCode")), PropertiesReader.getPropertiesValue("sys.file.code"))
        : URLEncoder.encode(fileName, "utf-8");
  }

  protected String getFileMB(long byteFile) {
    if (byteFile == 0) {
      return "0MB";
    }
    long mb = 1024 * 1024;
    return "" + byteFile / mb + "MB";
  }

  public List<Menu> getMenuTree(HttpSession s) {
    return (List<Menu>) s.getAttribute("menuTree");
  }

  public boolean validCaptcha(String keyInput, String phone, HttpSession s) {
    if (Boolean.parseBoolean(PropertiesReader.getPropertiesValue("website.kaptcha.enable"))) {
      Object phoneCaptcha = s.getAttribute("phoneCaptcha");
      if (phoneCaptcha != null) {
        String[] str = phoneCaptcha.toString().split("-");
        return keyInput.equals(str[0]) && phone.equals(str[2]) && ((System.currentTimeMillis() - Long.parseLong(str[1])) / (1000 * 60) < 30);
      }
      return false;
    }
    return true;
  }

  protected String getBasePath(HttpServletRequest request) {
    return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
  }

  public ModelAndView error(String msg) {
    return new ModelAndView("sipa/commons/error")
        .addObject("error", msg);
  }

  protected String rtnJson(List list, PageParams pm){
    BaseJsonResult json = new BaseJsonResult();
    json.put("data", list);
    json.put("pm", pm);
    return JSON.toJSONString(json, SerializerFeature.WriteMapNullValue);
  }


  public String getUUID() {
    return UUID.randomUUID().toString().replace("-","");
  }


  @Resource
  protected SysUserMng sysUserMng;
  @Resource
  protected ActionLogMng actionLogMng;
  @Resource
  protected LookupsMng lookupsMng;
  @Resource
  protected AttachmentMng attachmentMng;
  @Resource
  protected SysConfigMng sysConfigMng;
  @Resource
  protected MenuMng menuMng;
  @Resource
  protected PoiManager poiManager;
  @Resource
  protected HttpServletRequest request;
}
