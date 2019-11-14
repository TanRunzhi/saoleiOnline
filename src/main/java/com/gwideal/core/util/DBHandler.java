package com.gwideal.core.util;

import com.gwideal.util.io.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li_hongyu on 2014/11/28.
 */
@Service
public class DBHandler {

  private final static Logger logger = LogManager.getLogger();

  private final static String className = PropertiesReader.getPropertiesValue("db.driverClass");

  private final static String url = PropertiesReader.getPropertiesValue("db.url");

  private final static String username = PropertiesReader.getPropertiesValue("db.username");

  private final static String pwd = PropertiesReader.getPropertiesValue("db.pwd");

  private Connection conn = null;

  public Connection initDB() {
    try {
      Class.forName(className);
      conn = DriverManager.getConnection(url, username, pwd);
      conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      conn.setAutoCommit(true);
      return conn;
    } catch (Exception ex) {
      logger.error(ex.getStackTrace());
    }
    return null;
  }

  public void closeDB() {
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<String> getTableNamesOracle() {
    List<String> l = new ArrayList<>();
    try {
      Long t = System.currentTimeMillis();
      String SQL = "SELECT t1.table_name,t2.comments " +
          "FROM user_tables t1 JOIN user_tab_comments t2 " +
          "ON t1.table_name=t2.table_name " +
          "WHERE t1.table_name NOT LIKE 'T_CORE_%'";
      PreparedStatement pstmt = initDB().prepareStatement(SQL);
      ResultSet rs = pstmt.executeQuery();
      logger.info("Done, Total Cost:" + (System.currentTimeMillis() - t) / 1000.0 + " s");
      while (rs.next()) {
        l.add(rs.getString(1) + ":" + rs.getString(2));
      }
      pstmt.close();
      return l;
    } catch (SQLException e) {
      logger.error(e.getStackTrace());
    } catch (Exception ex) {
      logger.error(ex.getStackTrace());
    } finally {
      closeDB();
    }
    return null;
  }

  public List<String> getTableNamesSQLServer() {
    List<String> l = new ArrayList<String>();
    try {
      Long t = System.currentTimeMillis();
      String SQL = "SELECT t.name AS t_name," +
          "cast(isnull(c.[value], '') AS VARCHAR(100)) AS t_comment " +
          "FROM " + username + "..SysObjects t " +
          "LEFT JOIN " + username + ".sys.extended_properties c " +
          "ON t.id = c.major_id " +
          "WHERE t.xtype='U' " +
          "AND t.name NOT LIKE 't_core_%' " +
          "AND t.name NOT LIKE 't_jbpm_%' " +
          "ORDER BY t.name";
      PreparedStatement pstmt = initDB().prepareStatement(SQL);
      ResultSet rs = pstmt.executeQuery();
      logger.info("Done, Total Cost:" + (System.currentTimeMillis() - t) / 1000.0 + " s");
      while (rs.next()) {
        l.add(rs.getString(1) + ":" + rs.getString(2));
      }
      pstmt.close();
      return l;
    } catch (Exception ex) {
      logger.error(ex.getStackTrace());
    } finally {
      closeDB();
    }
    return null;
  }

  public int cleanData(String tableNames) {
    int i = 0;
    try {
      Long t = System.currentTimeMillis();

      for (String tableName : tableNames.split(",")) {
        assert tableName.indexOf(":") > 0;
        String tName = tableName.split(":")[0];
        PreparedStatement pstmt = initDB().prepareStatement("delete from ?");
        pstmt.setString(1, tName);
        pstmt.execute();
        pstmt.close();
        i++;
      }
      logger.info("Done, Total Cost:" + (System.currentTimeMillis() - t) / 1000.0 + " s");
      return i;
    } catch (SQLException ex) {
      logger.error(ex.getStackTrace());
    } finally {
      closeDB();
    }
    return 0;
  }
}
