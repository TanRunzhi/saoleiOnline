package com.gwideal.util.codegen;


import com.gwideal.util.codeHelper.StringHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ManagerGen {

  private static final Logger log = LogManager.getLogger();
  /**
   * manager类生成器
   *
   * @param managerClassName
   * @param entityclassName
   * @throws Exception
   */
  public static void gen(String managerClassName, String entityclassName) throws Exception {

    String[] type = managerClassName.split("\\.");
    if (type.length < 2) {
      throw new Exception("param 'className' is a class's full name!");
    }

    String simpleClassName = type[type.length - 1];
    String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\" + managerClassName.replaceAll("\\.", "\\\\") + ".java";
    File file = new File(filePath);
    boolean r = file.getParentFile().mkdirs();
    if(r){
      log.info("folder to create success...");
    }
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
    fw.write(packageCode(managerClassName));
    fw.write(importsCode(entityclassName));
    fw.write(classCode(simpleClassName));
    fw.flush();
    fw.close();

    String implFilePath = getImplPath(filePath, simpleClassName);
    File file1 = new File(implFilePath);
    boolean rr = file1.getParentFile().mkdirs();
    if(rr){
        log.info("folder to create success");
    }
    fw = new OutputStreamWriter(new FileOutputStream(file1), "utf-8");
    fw.write(packageCodeImpl(managerClassName));
    fw.write(importsCodeImpl(entityclassName));
    fw.write(classCodeImpl(simpleClassName));
    fw.flush();
    fw.close();
  }


  private static String packageCode(String managerClassName) {

    if (!managerClassName.contains(".")) {
      return "package " + managerClassName + ";\n\n";
    }
    return "package " + managerClassName.substring(0, managerClassName.lastIndexOf(".")) + ";\n\n";
  }

  private static String packageCodeImpl(String managerClassName) {
    if (!managerClassName.contains(".")) {
      return "package " + managerClassName + ";\n\n";
    }
    return "package " + managerClassName.substring(0, managerClassName.lastIndexOf(".")) + ".impl;\n\n";
  }

  private static String importsCode(String entityClassName) {
    StringBuilder code = new StringBuilder();
    code.append("import com.gwideal.base.manager.BaseMng;\n");
    code.append("import ").append(entityClassName).append(";\n\n");
    return code.toString();
  }

  private static String importsCodeImpl(String entityClassName) {
    StringBuilder code = new StringBuilder();
    code.append("import com.gwideal.base.manager.impl.BaseMngImpl;\n");
    code.append("import ").append(entityClassName).append(";\n");
    code.append("import ").append(entityClassName.replace("entity", "dao")).append("Dao;\n");
    code.append("import ").append(entityClassName.replace("entity", "manager")).append("Mng;\n");
    code.append("import org.springframework.stereotype.Service;\n");
    code.append("import javax.annotation.Resource;\n");
    code.append("import org.springframework.transaction.annotation.Transactional;\n");
    code.append("import org.apache.logging.log4j.LogManager;\n");
    code.append("import org.apache.logging.log4j.Logger;\n");
    return code.toString();
  }

  private static String classCode(String simpleClassName) {
    StringBuilder code = new StringBuilder();
    code.append("/**\n").append(" * @author \n").append(" */ \n");
    code.append("public interface ").append(simpleClassName).append(" extends BaseMng<").append(simpleClassName.replaceAll("Mng", "")).append(">{\n\n");
    code.append("}\n");
    return code.toString();
  }

  private static String classCodeImpl(String simpleClassName) {
    StringBuilder code = new StringBuilder();
    String daoName = simpleClassName.replace("Mng", "Dao");
    code.append("\n");
    code.append("/**\n").append(" * @author \n").append(" */ \n");
    code.append("@Service(\"").append(StringHelper.lowCastFirstChar(simpleClassName)).append("\")\n");
    code.append("@Transactional\n");
    code.append("public class ").append(simpleClassName).append("Impl extends BaseMngImpl<").append(simpleClassName.replaceAll("Mng", "")).append("> implements ").append(simpleClassName).append("{\n\n");
    code.append("\tprivate final static Logger logger = LogManager.getLogger();\n\n");
    code.append("\t@Resource\n");
    code.append("\tprivate ").append(daoName).append(" ").append(StringHelper.lowCastFirstChar(daoName)).append(";\n");
    code.append("}\n");
    return code.toString();
  }

  private static String getImplPath(String path, String simpleClassName) {
    String head = path.substring(0, path.lastIndexOf("\\"));
    StringBuilder tail = new StringBuilder();
    tail.append("\\impl\\").append(simpleClassName).append("Impl.java");
    return head + tail.toString();
  }

  public static void main(String[] args) {
    System.out.println(getImplPath("F:\\project\\ece\\src\\com\\aqkk\\core\\manager\\SysDepartMng.java", "SysDepartMng"));
  }
}
