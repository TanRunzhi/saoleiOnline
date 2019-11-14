package com.gwideal.core.entity;

import com.gwideal.base.entity.BaseEntity;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CORE_ATTACHMENT")
public class Attachment extends BaseEntity {


  /**
   * 显示的文件名
   */
  @Column(name = "display_name")
  private String displayName;

  /**
   * 文件名
   */
  private String fileName;

  /**
   * 文件路径
   */
  private String filePath;

  /**
   * 文件类型
   */
  private String fileType;

  /**
   * 文件大小
   */
  private String fileSize;

  /**
   * 对象ID
   */
  private String objectId;

  /**
   * 序列
   */
  private String category;

  /**
   * 下载次数,默认0
   */
  private Integer count = 0;


  /**
   * 上传时间
   */
  @Column(name = "upload_date")
  private Date uploadDate = new Date();


  /**
   * 上传人
   */
  @ManyToOne
  @JoinColumn(name = "uploader")
  private SysUser uploader;

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getFileType() {
    return fileType;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getFileSize() {
    return fileSize;
  }

  public void setFileSize(String fileSize) {
    this.fileSize = fileSize;
  }

  public Date getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(Date uploadDate) {
    this.uploadDate = uploadDate;
  }


  public String getFileNameOnServer() {
    return StringUtils.isNotEmpty(getFilePath()) ? getFilePath().substring(getFilePath().lastIndexOf("/") + 1) : null;
  }

  public SysUser getUploader() {
    return uploader;
  }

  public void setUploader(SysUser uploader) {
    this.uploader = uploader;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getFixedSize(Long fileSize) {
    if (fileSize == null) {
      return "";
    } else {
      if (fileSize > (1024d * 1024)) {
        return String.format("%.2f", fileSize / (1024d * 1024)) + "MB";
      } else if (fileSize > 1024d) {
        return String.format("%.2f", fileSize / 1024d) + "KB";
      } else {
        return fileSize + "B";
      }
    }
  }

  public Attachment() {
  }

  public Attachment(String id){
    setId(id);
  }

  public Attachment(String displayName, String filename, String filepath, String filetype,
                    String fileSize, String objectId, String category, Integer count,
                    Date uploadDate, SysUser uploader) {
    this.displayName = displayName;
    this.fileName = filename;
    this.filePath = filepath;
    this.fileType = filetype;
    this.fileSize = fileSize;
    this.objectId = objectId;
    this.category = category;
    this.count = count;
    this.uploadDate = uploadDate;
    this.uploader = uploader;
  }
}