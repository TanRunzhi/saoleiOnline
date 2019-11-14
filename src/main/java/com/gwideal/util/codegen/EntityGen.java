package com.gwideal.util.codegen;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.util.*;

/**
 * Created by li_hongyu on 14-7-21.
 */
public class EntityGen {

  private static final Logger log = LogManager.getLogger();
  private static final String[] CONTEXT_CONFIG_LOCATION = {"classpath*:config-spring/spring-core.xml"};

  public static void gen(String entityClassName, String tableName, String[] excludeFields) throws IOException, SQLException {
    StringBuilder code = new StringBuilder();
    code.append(EntityGenUtil.packageCode(entityClassName)).append(";\n\n");
    code.append("import com.gwideal.base.entity.BaseEntity;\n");
    code.append("import lombok.Getter;\n");
    code.append("import lombok.Setter;\n");
    /*code.append("import org.hibernate.annotations.Where;\n");*/
    List<ColumnBean> list = getColumns(tableName, excludeFields);
    Set<String> set = EntityGenUtil.importsCode(list);
    for (String aSet : set) {
      code.append(aSet);
    }
    code.append("/**\n").append(" * @author \n").append(" */ \n");
    code.append("@Entity\n");
    code.append("@Table(name=\"").append(tableName).append("\")\n");
    code.append("public class ")
        .append(EntityGenUtil.simpleClassName(entityClassName))
        .append(" extends BaseEntity{\n\n");
    for (ColumnBean cb : list) {
      code.append(EntityGenUtil.propertyCode(cb)).append("\n");
    }
    code.append("\n");
    /*for (ColumnBean cb : list) {
      code.append(EntityGenUtil.setterCode(cb)).append("\n\n");
      code.append(EntityGenUtil.getterCode(cb)).append("\n\n");
    }*/
    code.append("}");
    String filePath = System.getProperty("user.dir")
        + "\\src\\main\\java\\"
        + entityClassName.replaceAll("\\.", "\\\\")
        + ".java";
    File file = new File(filePath);
    boolean r = file.getParentFile().mkdirs();
    if (r) {
      log.info("folder to create success...");
    }
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
    fw.write(code.toString());
    fw.flush();
    fw.close();
  }

  public static List<ColumnBean> getColumns(
      String tableName,
      String[] filterProperty) throws HibernateException, SQLException {
    ApplicationContext mContext = new ClassPathXmlApplicationContext(CONTEXT_CONFIG_LOCATION);
    DruidDataSource ds = (DruidDataSource) mContext.getBean("dataSource");
    /* DriverManagerDataSource ds = (DriverManagerDataSource) mContext.getBean("dataSource1");*/
    Connection connection = ds.getConnection();
    Map<String, String> comments = getComment(tableName, connection, "sqlserver");
    List<ColumnBean> list = new ArrayList<ColumnBean>();
    String sql = "select * from " + tableName;
    connection = ds.getConnection();
    Statement st = connection.createStatement();
    ResultSet rs = st.executeQuery(sql);
    ResultSetMetaData rsmd = rs.getMetaData();
    int n = rsmd.getColumnCount();
    DatabaseMetaData dbTable = connection.getMetaData();
    ResultSet rsColumn = null;
    for (int i = 1; i <= n; i++) {
      String name = rsmd.getColumnName(i);
      log.info("列名=" + name + "|");
      if (EntityGenUtil.contains(filterProperty, name)) {
        continue;
      }
      ColumnBean bean = new ColumnBean();
      bean.setName(name);
      bean.setType(rsmd.getColumnClassName(i));
      rsColumn = dbTable.getColumns(null, null, tableName, null);

      bean.setComment(comments.get(name));
      //bean.setComment(getCommentForSqlserver(tableName, name, connection));
      list.add(bean);
    }
    assert rsColumn != null;
    rsColumn.close();
    rs.close();
    st.close();
    connection.close();
    return list;
  }

  private static Map<String, String> getComment(
      String tableName,
      Connection conn, String dataSourceType) throws SQLException {
    Map<String, String> map = new HashMap<String, String>();
    String sql = "select column_name,comments from user_col_comments where table_name = ?";
    if ("sqlserver".equalsIgnoreCase(dataSourceType)) {
      sql = "SELECT cast( B.name as varchar(500)) AS column_name,"
          + " cast( C.value as varchar(500)) AS comments FROM sys.tables A"
          + " INNER JOIN sys.columns B ON B.object_id = A.object_id "
          + " LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id "
          + " AND C.minor_id = B.column_id"
          + " WHERE A.name = ?  ";
    }
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setString(1, tableName.toUpperCase());
      rs = pst.executeQuery();
      while (rs.next()) {
        map.put(rs.getString("column_name"), rs.getString("comments"));
      }
    } catch (SQLException e) {
      log.debug(e.getMessage());
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (pst != null) {
        pst.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
    return map;
  }

  private static String getCommentForSqlserver(
      String tableName, String columnName,
      Connection conn) throws SQLException {
    String sql = " SELECT  cast( A.name as varchar(500)) AS table_name,"
        + " cast( B.name as varchar(500)) AS column_name,"
        + " cast( C.value as varchar(500)) AS column_comment FROM sys.tables A"
        + " INNER JOIN sys.columns B ON B.object_id = A.object_id "
        + " LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id "
        + " AND C.minor_id = B.column_id"
        + " WHERE A.name = ? and B.name= ? ";
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setString(1, tableName);
      pst.setString(2, columnName);
      rs = pst.executeQuery();
      while (rs.next()) {
        return rs.getString("column_comment");
      }
    } catch (SQLException e) {
      log.debug(e.getMessage());
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (pst != null) {
        pst.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
    return null;
  }
}
