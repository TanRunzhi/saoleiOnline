package com.gwideal.util.codegen;

import com.gwideal.util.codeHelper.StringHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class ActionGen {

  /**
   * Action类生成器
   *
   * @param actionClassName
   * @param managerClassName
   * @param accessUrl
   * @throws Exception
   */
  public static void gen(String actionClassName, String managerClassName,
                         String accessUrl, String path) throws Exception {

    String[] type = actionClassName.split("\\.");
    if (type.length < 2) {
      throw new Exception("param 'className' is a class's full name!");
    }

    String simpleClassName = type[type.length - 1];

    String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\"
        + actionClassName.replaceAll("\\.", "\\\\") + ".java";
    File file = new File(filePath);
    file.getParentFile().mkdirs();
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(
        file), "utf-8");
    fw.write(packageCode(actionClassName));
    fw.write(importsCode(managerClassName));
    fw.write(classCode(simpleClassName, accessUrl, path));
    fw.flush();
    fw.close();
  }

  private static String packageCode(String actionClassName) {

    if (!actionClassName.contains(".")) {
      return "package " + actionClassName + ";\n\n";
    }
    return "package "
        + actionClassName
        .substring(0, actionClassName.lastIndexOf("."))
        + ";\n\n";
  }

  private static String importsCode(String managerClassName) {
    StringBuilder code = new StringBuilder();
    code.append("import ").append(managerClassName).append(";\n");
    code.append("import ").append(managerClassName.replace("manager", "entity").replace("Mng", "")).append(";\n");
    code.append("import org.apache.logging.log4j.Logger;\n");
    code.append("import org.apache.logging.log4j.LogManager;\n");
    code.append("import org.springframework.stereotype.Controller;\n");
    code.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
    code.append("import com.gwideal.base.action.BaseAct;\n");
    code.append("import com.gwideal.util.io.PropertiesReader;\n");
    code.append("import javax.annotation.Resource;\n");
    code.append("import org.springframework.web.bind.annotation.ResponseBody;\n" +
        "import org.springframework.web.servlet.ModelAndView;");
    return code.toString();
  }

  private static String classCode(String actionSimpleClassName, String accessUrl, String path) {
    StringBuilder code = new StringBuilder();
    String className = actionSimpleClassName.replace("Act", "");
    String managerName = actionSimpleClassName.replace("Act", "Mng");
    String managerImp = StringHelper.lowCastFirstChar(managerName);

    code.append("\n");
    code.append("/**\n").append(" * @author \n").append(" */ \n");
    code.append("@Controller\n");
    code.append("@RequestMapping(\"").append(accessUrl).append("\")\n");
    code.append("public class ").append(actionSimpleClassName).append(" extends BaseAct ").append("{\n\n");
    code.append("\tprivate final static Logger logger = LogManager.getLogger();");
    // list 生成
    code.append("\n@RequestMapping(\"list\")\n" +
        "  public ModelAndView list(" + className + " queryBean) {\n" +
        "    logger.debug(\"" + className + " list fired\");\n" +
        "    return new ModelAndView(\"" + path + "/list\")\n" +
        "        .addObject(\"queryBean\", queryBean)\n" +
        "        .addObject(\"list\", " + managerImp + ".mergeHQL(queryBean, \" and 1 = 1 \"));\n}\n");
    //input 生成
    code.append("\n@RequestMapping(\"input\")\n" +
        "  public ModelAndView input(" + className + " queryBean) {\n" +
        "    return new ModelAndView(\"" + path + "/input\")\n" +
        "        .addObject(\"bean\", " + managerImp + ".initBean(queryBean));\n}");

    code.append("\n@RequestMapping(\"save\")\n" +
        "  public ModelAndView save(" + className + " bean) {\n" +
        " " + managerImp + ".saveOrUpdate(bean);\n" +
        "    return new ModelAndView(\"redirect:list.htm\");\n}");

    code.append("\n@RequestMapping(\"ajDel\")\n" +
        "@ResponseBody\n" +
        "  public String ajDel(String id) {\n" +
        "   " + managerImp + ".del(id);\n" +
        "   return PropertiesReader.getPropertiesValue(\"msg.delete.success\");\n}");

    code.append("\n");
    code.append("\t@Resource\n");
    code.append("\tprivate ").append(managerName).append(" ").append(managerImp).append(";\n\n");
    code.append("}\n");
    return code.toString();
  }


}
