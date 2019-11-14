package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.Attachment;

import java.util.List;
import java.util.Map;

public interface AttachmentMng extends BaseMng<Attachment> {

  /**
   * 根据实体id、实体名，查找该实体的附件集合
   *
   * @param objectId 实体id
   * @return 附件列表
   */
  List<Attachment> findByObjectId (String objectId,String group);

  List<Attachment> findByGroupName (String group);

  /**
   * 获取待上传至ftp的附件
   *
   * @return 附件列表
   */
  List<Attachment> getFileSendToFTP ();

  String getIdByDisplayName (String fileDisplayName);

  String getFilePath (String attId);

  void clearAttachment (String attId);

  void updateObjectId (String objectId, String fid);

  Map<String,Object> getImageAttByObjId (String id);

  String getObjSize(String objectId);
}
