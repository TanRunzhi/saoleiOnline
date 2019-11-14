package com.gwideal.core.excel;

import com.gwideal.base.entity.BaseEntity;
import com.gwideal.core.config.Constants;
import com.gwideal.core.excel.annotations.Excel;
import com.gwideal.core.excel.annotations.ExcelTemplate;
import com.gwideal.core.manager.LookupsMng;
import com.gwideal.core.manager.SysConfigMng;
import com.gwideal.util.common.UtilEmpty;
import com.gwideal.util.io.FileHelper;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Service("poiManger")
public class PoiManager<T extends BaseEntity> {

  private final static Logger logger = LogManager.getLogger();
  @Resource
  private SysConfigMng sysConfigMng;

  public List excelImpt(Integer sheetNum, InputStream is, Class entityClass) throws Exception {
    if (!entityClass.isAnnotationPresent(ExcelTemplate.class)) {
      return Collections.EMPTY_LIST;
    }
    List excelList = new ArrayList();
    ExcelTemplate excelTemplate = (ExcelTemplate) entityClass
        .getAnnotation(ExcelTemplate.class);
    Workbook wb = WorkbookFactory.create(is);
    Sheet sheet = wb.getSheetAt(sheetNum);
    int rows = sheet.getPhysicalNumberOfRows();
    int rowStartNum = excelTemplate.rowStartNum();
    for (int r = rowStartNum; r < rows; r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      Object obj = entityClass.newInstance();
      for (Field field : entityClass.getDeclaredFields()) {
        if (field.isAnnotationPresent(Excel.class)) {
          Excel excel = field.getAnnotation(Excel.class);
          Cell cell = row.getCell(excel.cell());
          field.setAccessible(true);
          if (cell == null) {
            continue;
          }
          switch (cell.getCellType()) {
            case FORMULA:
              field.set(obj, cell.getNumericCellValue() + "");
              break;
            case NUMERIC:
              if (DateUtil.isCellDateFormatted(cell)) {
                field.set(obj, cell.getDateCellValue());
              } else {
                field.set(obj, cell.getNumericCellValue() + "");
              }
              break;
            case STRING:
              try {
                field.set(obj, cell.getStringCellValue());
              } catch (Exception e) {
                field.set(obj, null);
              }
              break;
            default:
          }
        }
      }
      excelList.add(obj);
    }
    is.close();
    return excelList;
  }

  public List<Map<String, Object>> excelImp(Integer rowStart, String[] fields, Integer keyIndex, InputStream is) throws IOException {
    List<Map<String, Object>> list = new ArrayList<>();
    Workbook wb = WorkbookFactory.create(is);
    Sheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();
    for (int r = rowStart; r < rows; r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      Map<String, Object> item = new HashMap<>();
      //对应excel表格中的第几行
      item.put("#rowNum",r+1);
      for (int i = 0; i < fields.length; i++) {
        Cell cell = row.getCell(i);
        if (cell == null) {
          continue;
        }
        switch (cell.getCellType()) {
          case FORMULA:
            try {
              item.put(fields[i], cell.getNumericCellValue() + "");
            } catch (Exception e) {
              item.put(fields[i], null);
            }
            break;
          case NUMERIC:
            try {
              if (DateUtil.isCellDateFormatted(cell)) {
                item.put(fields[i], cell.getDateCellValue());
              } else {
//                item.put(fields[i], cell.getNumericCellValue() + "");
                cell.setCellType(CellType.STRING);
                item.put(fields[i], cell.getStringCellValue());
              }
            } catch (Exception e) {
              item.put(fields[i], null);
            }
            break;
          case STRING:
            try {
              item.put(fields[i], cell.getStringCellValue());
            } catch (Exception e) {
              item.put(fields[i], null);
            }
            break;
          default:
        }
      }
      try {
        if (item.get(fields[keyIndex]) != null && StringUtils.isNotEmpty(item.get(fields[keyIndex]).toString())) {
          list.add(item);
        }
      } catch (Exception ignored) {
        logger.error(ignored.getMessage());
      }
    }
    is.close();
    return list;
  }

  public String excelExp(int rowStartNum, List<?> list, String proPath, String tmplatePath, String fileName) throws Exception {
    if (UtilEmpty.isArrayEmpty(list)) {
      return null;
    }
    Map keyFields = PropertiesReader.getProperties(proPath);
    Class clz = list.get(0).getClass();
    Workbook wb = WorkbookFactory.create(new FileInputStream(tmplatePath));
    Sheet sheet = wb.getSheetAt(0);
    CellStyle style = getCellStyle(wb);
    for (Object obj : list) {
      Row row = sheet.getRow(rowStartNum);
      if (row == null) {
        row = sheet.createRow(rowStartNum);
      }
      for (Field field : clz.getDeclaredFields()) {
        field.setAccessible(true);
        Object idx = keyFields.get(field.getName());
        if (idx == null) {
          continue;
        }
        int r = Integer.parseInt(idx.toString());
        Cell cell = row.getCell(r);
        if (cell == null) {
          cell = row.createCell(r);
        }
        cell.setCellStyle(style);
        Object cellValue = field.get(obj);
        if (cellValue != null) {
          if (cellValue instanceof Boolean) {
            cellValue = ((boolean) cellValue) ? "是" : "否";
          }
          cell.setCellValue(cellValue.toString());
        }
      }
      rowStartNum++;
    }
    OutputStream os = new FileOutputStream(fileName);
    wb.write(os);
    os.flush();
    os.close();
    return tmplatePath;
  }

  public void excelExp(int rowStartNum, List<Map<String, Object>> list, Object[] keys,
                       String templatePath, String fileName, HttpServletResponse response)
      throws Exception {
    if (UtilEmpty.isArrayEmpty(list)) {
      return;
    }
    Workbook wb = getWookBookToExcelExp(rowStartNum,  list,  keys, templatePath, fileName, response);
    responseFile(response, fileName, wb, null);
  }

  public Workbook getWookBookToExcelExp(int rowStartNum, List<Map<String, Object>> list, Object[] keys,
                                        String templatePath, String fileName, HttpServletResponse response)
      throws Exception {
    if (UtilEmpty.isArrayEmpty(list)) {
      return null;
    }
    Workbook wb = WorkbookFactory.create(new FileInputStream(new File(templatePath)));
    Sheet sheet = wb.getSheetAt(0);
    sheet.shiftRows(rowStartNum + 1, sheet.getLastRowNum(), list.size());
    CellStyle style = getCellStyle(wb);
    int rowIdx = 0;
    for (Map<String, Object> map : list) {
      Row row = sheet.getRow(rowStartNum + rowIdx);
      if (row == null) {
        row = sheet.createRow(rowStartNum + rowIdx);
      }

      for (int i = 0; i < keys.length; i++) {
        Cell cell = row.getCell(i);
        if (cell == null) {
          cell = row.createCell(i);
        }
        cell.setCellStyle(style);
        Object cellValue = map.get(keys[i]);
        if (keys[i].equals("seq") && cellValue == null) {
          cellValue = rowIdx + 1;
        }
        if (cellValue != null) {
          if (cellValue instanceof Boolean) {
            cellValue = ((boolean) cellValue) ? "是" : "否";
          }
          cell.setCellValue(cellValue.toString());
        }
      }
      rowIdx++;
    }
   return wb;
  }

  public void excelExp(int rowStartNum, List<Map<String, Object>> list, Object[] keys,
                       String templatePath, String fileName, HttpServletResponse response, Map<String, String> titles)
      throws Exception {
    if (UtilEmpty.isArrayEmpty(list)) {
      return;
    }
    Workbook wb = WorkbookFactory.create(this.getClass().getResourceAsStream(templatePath));
    Sheet sheet = wb.getSheetAt(0);
    if (titles != null && !titles.isEmpty()) {
      for (int i = 0; i <= rowStartNum; i++) {
        Cell cell = sheet.getRow(i).getCell(0);
        String val = cell.getStringCellValue();
        for (String key : titles.keySet()) {
          val = val.replaceAll("#" + key + "#", titles.get(key));
        }
        cell.setCellValue(val);
      }
    }
    if (rowStartNum + 1 < sheet.getLastRowNum()) {
      sheet.shiftRows(rowStartNum + 1, sheet.getLastRowNum(), list.size());
    }
    CellStyle style = getCellStyle(wb);
    int rowIdx = 0;
    for (Map<String, Object> map : list) {
      Row row = sheet.getRow(rowStartNum + rowIdx);
      if (row == null) {
        row = sheet.createRow(rowStartNum + rowIdx);
      }

      for (int i = 0; i < keys.length; i++) {
        Cell cell = row.getCell(i);
        if (cell == null) {
          cell = row.createCell(i);
        }
        cell.setCellStyle(style);
        Object cellValue = map.get(keys[i]);
        if (keys[i].equals("seq") && cellValue == null) {
          cellValue = rowIdx + 1;
        }
        if (cellValue != null) {
          if (cellValue instanceof Boolean) {
            cellValue = ((boolean) cellValue) ? "是" : "否";
          }
          cell.setCellValue(cellValue.toString());
        }
      }
      rowIdx++;
    }
    responseFile(response, fileName, wb, null);
  }

  /**
   * 导出Excel,自动生成Excel(简单的)
   *
   * @param fileName 文件名
   * @param data     数据
   * @param titles   标题集合
   * @param response
   */
  public void expExcel(String fileName, List<Object[]> data, Object[] titles, HttpServletResponse response) {
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet(fileName);
    HSSFRow row = sheet.createRow(0);
    HSSFCellStyle cellStyle = getCellStyle(wb, true);
    HSSFCellStyle cellStyle2 = getCellStyle(wb, false);
    for (int i = 0; i < titles.length; i++) {
      createCell(row, i, titles[i], cellStyle);
    }
    // 设置单元格
    for (int i = 0; i < data.size(); i++) {
      HSSFRow nrow = sheet.createRow(i + 1);
      Object[] obj = data.get(i);
      for (int j = 0; j < obj.length; j++) {
        createCell(nrow, j, obj[j], cellStyle2);
      }
    }

    for (int i = 0; i < titles.length; i++) {
      sheet.autoSizeColumn(i, true);
    }
    responseFile(response, fileName + ".xls", wb, null);
  }

  /**
   * 按照模板导出单个对象
   *
   * @param fileds   字段集合 0、字段名 1、注释 2、行数 3、列数
   * @param objs     数据集合
   * @param key      模板
   * @param fileName 文件名
   * @param response
   */
  public void expSingleObject(List<Object[]> fileds, Object[] objs, String key, String fileName, HttpServletResponse response) {
    try {
      Workbook wb = WorkbookFactory.create(this.getClass().getResourceAsStream(PropertiesReader.getPropertiesValue(key)));
      Sheet sheet = wb.getSheetAt(0);
      for (int i = 0; i < fileds.size(); i++) {
        Object[] field = fileds.get(i);
        int rows = Integer.parseInt(field[2].toString());
        if (rows >= 0) {
          sheet.getRow(rows).getCell(Integer.parseInt(field[3].toString()))
              .setCellValue(getValue(objs[i]));
        }
      }
      responseFile(response, fileName, wb, null);
    } catch (IOException e) {
      logger.debug(e.getMessage());
    }

  }

  /**
   * 导出Excel,自动生成Excel(简单的)
   *
   * @param fileName 文件名
   * @param data     数据
   * @param titles   标题集合
   * @param response
   */
  public void expMapExcel(String fileName, List<Map<String, Object>> data, Object[] titles, HttpServletResponse response) {
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet(fileName);
    int len = titles.length;
    HSSFCellStyle cellStyle = getCellStyle(wb, true);
    HSSFCellStyle cellStyle2 = getCellStyle(wb, false);

    HSSFRow row = sheet.createRow(0);
    for (int i = 0; i < len; i++) {
      createCell(row, i, titles[i], cellStyle);
    }
    // 设置单元格
    for (int i = 0; i < data.size(); i++) {
      HSSFRow nrow = sheet.createRow(i + 1);
      Object[] obj = data.get(i).values().toArray();
      for (int j = 0; j < obj.length; j++) {
        if (j < len) {
          createCell(nrow, j, obj[j], cellStyle2);
        }
      }
    }
    for (int i = 0; i < titles.length; i++) {
      sheet.autoSizeColumn(i, true);
    }
    responseFile(response, fileName + ".xls", wb, null);
  }

  /**
   * 导出Excel,自动生成Excel(简单的)
   *
   * @param fileName 文件名
   * @param data     数据
   * @param titles   标题集合
   * @param response
   */
  public void expMapZipExcel(String fileName, List<Map<String, Object>> data, Object[] titles, HttpServletResponse response) {
    if (data.isEmpty()) {
      return;
    }
    //每个Excel行数和数据量
    int rowOnly = Integer.parseInt(PropertiesReader.getPropertiesValue("export.excel.rowOnly")); //每个文件的记录数
    int size = data.size();

    //小于行数只导出一个文件
    if (size <= rowOnly) {
      expMapExcel(fileName, data, titles, response);
      return;
    }

    //要压缩的源文件
    List<File> zipSrc = new ArrayList<File>();
    File filePath = new File(sysConfigMng.getCacheValue(Constants.LOCAL_FILE_UPLOAD_TEMP_BASE)
        + "/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/"); //文件路径
    if (!filePath.exists()) {
      filePath.mkdirs();
    }

    int fileNum = (size % rowOnly == 0 ? (size / rowOnly) : (size / rowOnly + 1));         //生成的文件数
    int num = 0;
    int len = titles.length;

    while (fileNum > num) {
      num++;
      HSSFWorkbook wb = new HSSFWorkbook();
      HSSFSheet sheet = wb.createSheet(fileName);
      HSSFCellStyle cellStyle = getCellStyle(wb, true);
      HSSFCellStyle cellStyle2 = getCellStyle(wb, false);

      HSSFRow row = sheet.createRow(0);
      for (int i = 0; i < len; i++) {
        createCell(row, i, titles[i], cellStyle);
      }
      for (int i = 0; i < rowOnly; i++) {
        int k = i + (num - 1) * rowOnly;
        if (k > size - 1) {
          break;
        }
        HSSFRow nRow = sheet.createRow(i + 1);
        Object[] obj = data.get(k).values().toArray();
        for (int j = 0; j < obj.length; j++) {
          if (j < len) {
            createCell(nRow, j, obj[j], cellStyle2);
          }
        }
      }
      File excelFile = new File(filePath, fileName + num + ".xls");
      zipSrc.add(workBookToFile(wb, excelFile));
    }
    String zipName = fileName + ".zip";
    File zipFile = new File(filePath, zipName);
    FileHelper.zipFiles(zipSrc, zipFile);
    try {
      responseFile(response, zipName, null, FileUtils.readFileToByteArray(zipFile));
    } catch (IOException e) {
      logger.debug(e.getMessage());
    }
  }

  /**
   * 文件内容写入workbook
   *
   * @param wb
   * @param file
   * @return
   */
  private File workBookToFile(Workbook wb, File file) {
    try {
      OutputStream os = new FileOutputStream(file);
      wb.write(os);
      os.close();
      wb.close();
      return file;
    } catch (FileNotFoundException e) {
      logger.debug(e.getMessage());
    } catch (IOException e) {
      logger.debug(e.getMessage());
    }
    return null;
  }

  /**
   * 导出Excel,自动生成Excel(简单的)
   *
   * @param fileName 文件名
   * @param data     list数据
   * @param titles   标题集合
   * @param response
   */
  public void expZipExcel(String fileName, List<Object[]> data, Object[] titles, HttpServletResponse response) {
    if (data.isEmpty()) {
      return;
    }
    //每个Excel行数和数据量
    int rowOnly = Integer.parseInt(PropertiesReader.getPropertiesValue("export.excel.rowOnly")); //每个文件的记录数
    int size = data.size();

    //小于行数只导出一个文件
    if (size <= rowOnly) {
      expExcel(fileName, data, titles, response);
      return;
    }

    //要压缩的源文件
    List<File> zipSrc = new ArrayList<File>();
    File filePath = new File(sysConfigMng.getCacheValue(Constants.LOCAL_FILE_UPLOAD_TEMP_BASE)
        + "/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/"); //文件路径
    if (!filePath.exists()) {
      filePath.mkdirs();
    }


    int fileNum = (size % rowOnly == 0 ? (size / rowOnly) : (size / rowOnly + 1));         //生成的文件数
    int num = 0;
    int len = titles.length;

    while (fileNum > num) {
      num++;
      HSSFWorkbook wb = new HSSFWorkbook();
      HSSFSheet sheet = wb.createSheet(fileName);
      HSSFCellStyle cellStyle = getCellStyle(wb, true);
      HSSFCellStyle cellStyle2 = getCellStyle(wb, false);

      HSSFRow row = sheet.createRow(0);
      for (int i = 0; i < len; i++) {
        createCell(row, i, titles[i], cellStyle);
      }
      for (int i = 0; i < rowOnly; i++) {
        int k = i + (num - 1) * rowOnly;
        if (k > size - 1) {
          break;
        }
        HSSFRow nRow = sheet.createRow(i + 1);
        Object[] obj = data.get(k);
        for (int j = 0; j < obj.length; j++) {
          if (j < len) {
            createCell(nRow, j, obj[j], cellStyle2);
          }
        }
      }
      File excelFile = new File(filePath, fileName + num + ".xls");
      zipSrc.add(workBookToFile(wb, excelFile));
    }
    String zipName = fileName + ".zip";
    File zipFile = new File(filePath, zipName);
    FileHelper.zipFiles(zipSrc, zipFile);
    try {
      responseFile(response, zipName, null, FileUtils.readFileToByteArray(zipFile));
    } catch (IOException e) {
      logger.debug(e.getMessage());
    }
  }

  /**
   * 输出导出文件到web
   *
   * @param response
   * @param fileName 文件名
   * @param wb       workbook 不为空即导出单个文件
   * @param bytes    不为空即导出文件
   */
  public void responseFile(HttpServletResponse response, String fileName, Workbook wb, byte[] bytes) {
    try {
      response.setContentType("application/octet-stream;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment;filename=" +
          new String(fileName.getBytes(PropertiesReader.getPropertiesValue("sys.file.getCode")), PropertiesReader.getPropertiesValue("sys.file.code")));
      // 客户端不缓存
      response.addHeader("Pargam", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
      OutputStream out = response.getOutputStream();
      if (wb != null) {
        wb.write(out);
      } else {
        out.write(bytes);
      }
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public Cell createCell(HSSFRow row, int col, Object val, CellStyle cellStyle) {
    // 创建一个celll单元格
    HSSFCell cell = row.createCell(col);
    cell.setCellValue(getValue(val));
    if (cellStyle != null) {
      cell.setCellStyle(cellStyle);// 给单元格设置样式
    }
    return cell;
  }

  private String getValue(Object val) {
    String value = (val == null ? "" : val.toString());
    if (val instanceof BigDecimal) {
      BigDecimal b = (BigDecimal) val;
      return b.setScale(2).toString();
    } else if (val instanceof Double) {
      return new DecimalFormat("##.00").format(val);
    } else if (val instanceof Date) {
      Date t = (Timestamp) val;
      return DateFormatUtils.format(t, "yyyy-MM-dd");
    } else if (val instanceof Boolean) {
      value = Boolean.parseBoolean(value) ? "是" : "否";
    } else if (value.indexOf("look-") >= 0) {
      String[] cols = StringUtils.split(value, "-");
      value = lookupsMng.getCacheLookupsNameByCCodeLCode(cols[1], cols.length > 2 ? cols[2] : "");
    }
    return value;
  }

  public HSSFCellStyle getCellStyle(HSSFWorkbook wb, Boolean isTitle) {
    HSSFCellStyle cellStyle = wb.createCellStyle();
    cellStyle.setWrapText(true);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    HSSFFont font2 = wb.createFont();
    font2.setFontName("Arial");
    font2.setFontHeightInPoints((short) 10);


    if (isTitle) {
      cellStyle.setBorderBottom(BorderStyle.THIN);//带边框
      cellStyle.setBorderTop(BorderStyle.THIN);//带边框
      cellStyle.setBorderRight(BorderStyle.THIN);//带边框
      cellStyle.setBorderLeft(BorderStyle.THIN);//带边框
      font2.setBold(true);
    }
    cellStyle.setFont(font2);
    return cellStyle;
  }

  public CellStyle getCellStyle(Workbook wb) {
    CellStyle cellStyle = wb.createCellStyle();
    //cellStyle.setWrapText(true);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    Font font2 = wb.createFont();
    font2.setFontName("Arial");
    font2.setFontHeightInPoints((short) 10);

    cellStyle.setBorderBottom(BorderStyle.THIN);//带边框
    cellStyle.setBorderTop(BorderStyle.THIN);//带边框
    cellStyle.setBorderRight(BorderStyle.THIN);//带边框
    cellStyle.setBorderLeft(BorderStyle.THIN);//带边框

    cellStyle.setFont(font2);
    return cellStyle;
  }


  public List importExcel(MultipartFile file, String propertyPath, Class cls, int startNum) {
    List list = new ArrayList<>();
    Map keyFields = PropertiesReader.getProperties(propertyPath);
    try {
      Workbook hwb = getWorkBook(file);
      Sheet hst = hwb.getSheetAt(0);
      FormulaEvaluator evaluator = hwb.getCreationHelper().createFormulaEvaluator();
      for (int r = startNum; r <= hst.getLastRowNum(); r++) {
        Row row = hst.getRow(r);
        if (row == null || isRowEmpty(row)) {
          continue;
        }
        Object obj = cls.newInstance();
        for (Field field : cls.getDeclaredFields()) {
          field.setAccessible(true);
          Object idx = keyFields.get(field.getName());
          if (idx == null) {
            continue;
          }
          try {
            Cell cell = row.getCell(Integer.parseInt(idx.toString()));
            if (field.getType() == Date.class) {
              if (cell.getCellType() == CellType.NUMERIC) {
                field.set(obj, DateUtil.getJavaDate(cell.getNumericCellValue()));
              } else {
                field.set(obj, DateUtil.parseYYYYMMDDDate(cell.getStringCellValue()));
              }
            } else if (field.getType() == Boolean.class) {
              field.set(obj, "是".equals(cell.getStringCellValue()));
            } else if (field.getType() == Double.class) {
              if (cell.getCellType() == CellType.NUMERIC) {
                field.set(obj, cell.getNumericCellValue());
              } else {
                String value = cell.getStringCellValue();
                field.set(obj, UtilEmpty.isStringEmpty(value) ? 0.0 : Double.parseDouble(value));
              }
            } else if (field.getType() == Integer.class) {
              if (cell.getCellType() == CellType.NUMERIC) {
                field.set(obj, (int) cell.getNumericCellValue());
              } else {
                String value = cell.getStringCellValue();
                field.set(obj, (UtilEmpty.isStringEmpty(value) ? 0 : Integer.parseInt(value)));
              }
            } else {
              if (cell.getCellType() == CellType.NUMERIC) {
                field.set(obj, NumberFormat.getNumberInstance().format(cell.getNumericCellValue()).replace(",", ""));
              } else if (cell.getCellType() == CellType.FORMULA) {
                field.set(obj, NumberFormat.getNumberInstance().format(evaluator.evaluate(cell).getNumberValue()).replace(",", ""));
              } else {
                field.set(obj, cell.getStringCellValue());
              }
            }
          } catch (Exception e) {
            logger.debug(e.getMessage());
          }
        }
        list.add(obj);
      }
      hwb.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  private Workbook getWorkBook(MultipartFile file) {
    //获得文件名
    String fileName = file.getOriginalFilename().toLowerCase();
    //创建Workbook工作薄对象，表示整个excel
    Workbook workbook = null;
    try {
      //获取excel文件的io流
      InputStream is = file.getInputStream();
      //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
      if (fileName.endsWith("xls")) {
        //2003
        workbook = new HSSFWorkbook(is);
      } else if (fileName.endsWith("xlsx")) {
        //2007
        workbook = new XSSFWorkbook(is);
      }
    } catch (IOException e) {
      logger.info(e.getMessage());
    }
    return workbook;
  }

  public boolean isRowEmpty(Row row) {
    if (row == null) {
      return true;
    }
    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
      Cell cell = row.getCell(c);
      if (cell != null && cell.getCellType() != CellType.BLANK)
        return false;
    }
    return true;
  }

  private String clearEmpty(String str) {
    //str.replaceAll(" |\\s","")
    return str == null ? "" : str.replaceAll("[\\u00A0]|\\s", "");
  }

  public String getCellValue(HSSFCell cell) {
    String str = "";
    if (cell != null) {
      switch (cell.getCellType()) {
        case FORMULA:
          str = cell.getCellFormula();
          break;
        case NUMERIC:
          if (HSSFDateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            str = DateFormatUtils.format(date, "yyyyMMdd");
          } else {
            HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
            str = dataFormatter.formatCellValue(cell);
          }
          break;
        case STRING:
          str = cell.getStringCellValue();
          break;
        default:
          str = cell.getStringCellValue();
          break;
      }
    }
    return str;
  }


  /**
   * 在word中添加表格
   * @param list 数据的集合
   * @param titles 第一行标题的名字的集合
   * @param keys list中的map的key的顺序集合，作用用于排序
   * @param templatePath 模版路径
   * @return  doc  可通过doc.write(OutputStream)进行其他处理
   * */
  public XWPFDocument setTableInWord(List<Map<String, String>> list, String[] titles, String[] keys,String templatePath){
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(templatePath);
      XWPFDocument doc = new XWPFDocument(in);
      List<XWPFParagraph> allXWPFParagraphs = doc.getParagraphs();
      for (XWPFParagraph xwpfParagraph : allXWPFParagraphs) {
        List<XWPFRun> runs = xwpfParagraph.getRuns();
        for (XWPFRun run : runs) {
          String text = run.getText(0);
          if (StringUtils.isNotBlank(text)) {
            if (text.equals("table")) {
              int cols = keys.length;
              int rows = list.size();
              XmlCursor cursor = xwpfParagraph.getCTP().newCursor();
              XWPFTable tableOne = doc.insertNewTbl(cursor);
              //样式控制
              CTTbl ttbl = tableOne.getCTTbl();
              CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();
              CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
              CTJc cTJc = tblPr.addNewJc();
              cTJc.setVal(STJc.Enum.forString("center"));//表格居中
              tblWidth.setW(new BigInteger("9000"));//每个表格宽度
              tblWidth.setType(STTblWidth.DXA);
              //表格创建
              XWPFTableRow tableRowTitle = tableOne.getRow(0);
              tableRowTitle.getCell(0).setText(titles[0]);
              for(int i = 1 ; i <= titles.length - 1 ;i++){
                tableRowTitle.addNewTableCell().setText(titles[i]);
              }
              for (int i = 0; i < rows; i++) {
                XWPFTableRow createRow = tableOne.createRow();
                for (int j = 0; j <= keys.length - 1; j++) {
                  createRow.getCell(j).setText(list.get(i).get(keys[j]));
                }
              }
              run.setText("", 0);
            }
          }
        }
      }
      return doc;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static void setValidationData(Sheet sheet, int firstRow,  int lastRow,
                                       int firstCol, int lastCol,String[] explicitListValues) throws IllegalArgumentException{
    if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || lastRow < firstRow || lastCol < firstCol) {
      throw new IllegalArgumentException("Wrong Row or Column index : " + firstRow+":"+lastRow+":"+firstCol+":" +lastCol);
    }
    if (sheet instanceof XSSFSheet) {
      XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet)sheet);
      XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
          .createExplicitListConstraint(explicitListValues);
      CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
      XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
      validation.setSuppressDropDownArrow(true);
      validation.setShowErrorBox(true);
      sheet.addValidationData(validation);
    } else if(sheet instanceof HSSFSheet){
      CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
      DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(explicitListValues);
      DataValidation validation = new HSSFDataValidation(addressList, dvConstraint);
      validation.setSuppressDropDownArrow(true);
      validation.setShowErrorBox(true);
      sheet.addValidationData(validation);
    }
  }

  @Resource
  private LookupsMng lookupsMng;

}
