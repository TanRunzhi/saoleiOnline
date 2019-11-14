package com.gwideal.core.manager.impl;

import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.config.Constants;
import com.gwideal.core.entity.Attachment;
import com.gwideal.core.manager.AttachmentMng;
import com.gwideal.core.manager.SysConfigMng;
import com.gwideal.util.codeHelper.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("attachmentMng")
@Transactional
public class AttachmentMngImpl extends BaseMngImpl<Attachment> implements AttachmentMng {

  @Override
  public List<Attachment> findByObjectId(String objectId, String group) {
    if (StringUtils.isNotEmpty(objectId)) {
      String hql = String.format("from Attachment where objectId in %s ", StringHelper.formatStringToSqlInQuery(objectId));
      if (StringUtils.isNotEmpty(group)) {
        hql += String.format(" and category = '%s'", group);
      }
      return find(hql);
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<Attachment> findByGroupName(String group) {
    if (StringUtils.isNotEmpty(group)) {
      return find("from Attachment where category = ?0  order by uploadDate desc", group);
    } else {
      return null;
    }
  }

  @Override
  public List<Attachment> getFileSendToFTP() {
    return find("from Attachment where sentToFTP = 0");
  }

  @Override
  public String getIdByDisplayName(String fileDisplayName) {
    return (String) jdbcTemplate.queryForList("SELECT pid FROM T_CORE_ATTACHMENT WHERE FILE_NAME_DISPLAY = ?", fileDisplayName).get(0).get("pid");
  }

  /**
   * 获取附件物理文件路径
   *
   * @param attId attachmentId
   * @return 路径
   */
  @Override
  public String getFilePath(String attId) {
    if (StringUtils.isEmpty(attId))
      return "";
    else {
      Attachment a = load(attId);
      return sysConfigMng.getCacheValue(Constants.LOCAL_FILE_UPLOAD_TEMP_BASE) + a.getFilePath();
    }
  }

  @Override
  public void clearAttachment(String attId) {
    Attachment a = load(attId);
    boolean r = new File(sysConfigMng.getCacheValue(Constants.LOCAL_FILE_UPLOAD_TEMP_BASE) + a.getFilePath()).delete();
    if (r)
      del(a.getId());
  }

  @Override
  public void updateObjectId(String objectId, String fid) {
    if (StringUtils.isNotEmpty(fid)) {
      for (String attrId : fid.split(",")) {
        if (StringUtils.isNotEmpty(attrId)) {
          jdbcTemplate.update("update T_CORE_ATTACHMENT SET OBJECTID=? where PID=?",
              objectId, attrId);
        }
      }
    }
  }


  @Override
  public Map<String, Object> getImageAttByObjId(String id) {
    return jdbcTemplate.queryForList("SELECT FILETYPE,FILEPATH FROM t_core_attachment WHERE OBJECTID = ?", id).get(0);
  }

  @Override
  public String getObjSize(String objectId) {
    String fileSize = jdbcTemplate.queryForMap("SELECT ISNULL(sum(FILESIZE),0) num FROM t_core_attachment WHERE OBJECTID = ?", objectId)
        .get("num").toString();
    DecimalFormat format = new DecimalFormat("0.00");
    double size = Double.parseDouble(fileSize) / 1024;
    if (size < 2048) {
      return Math.ceil(size) + "KB";
    }
    return format.format(size / 1024) + "MB";
  }

  @Resource
  private SysConfigMng sysConfigMng;
}
