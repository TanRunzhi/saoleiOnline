package com.gwideal.biz.manager;

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

@Service("CellMng")
public class CellMng {

  private final static Logger logger = LogManager.getLogger();




}
