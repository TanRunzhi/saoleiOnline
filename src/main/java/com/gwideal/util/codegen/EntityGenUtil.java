package com.gwideal.util.codegen;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EntityGenUtil {

    public static String packageCode(String className) {

        if (!className.contains(".")) {
            return "package " + className;
        }
        return "package " + className.substring(0, className.lastIndexOf("."));
    }

    public static String simpleClassName(String className) {
        if (!className.contains(".")) {
            return className;
        } else {
            String[] temp = className.split("\\.");
            return temp[temp.length - 1];
        }
    }

    public static Set<String> importsCode(List<ColumnBean> list) {
        Set<String> set = new LinkedHashSet<String>();
        for (ColumnBean cb : list) {
            if ("java.sql.Timestamp".equals(cb.getType())) {
                set.add("import java.util.Date;\n");
            } else if (!cb.getType().contains("lang")) {
                set.add("import " + cb.getType() + ";\n");
            }
        }
        set.add("\n");
        set.add("import javax.persistence.*;\n");
        return set;
    }


    public static String propertyCode(ColumnBean cb) {
        String code = "";
        if ("java.sql.Timestamp".equals(cb.getType())) {
            cb.setType("java.util.Date");
        }
        if ("java.math.BigDecimal".equals(cb.getType())) {
            cb.setType("java.lang.Double");
        }

        String[] type = cb.getType().split("\\.");
        String name = cb.getName();
        name = name.toLowerCase();
        code = code + "\n\t/**\n\t * " + cb.getComment()+"\n\t */\n\t@Getter\n\t@Setter\n";
        if (name.contains("_")) {
            StringBuilder sb = new StringBuilder();
            String[] temp = name.split("_");
            sb.append(temp[0]);
            for (int i = 1; i < temp.length; i++) {
                sb.append(toUpperIndexChar(temp[i]));
            }
            if (cb.getName().indexOf("_ID") > 0) {
                code = code + "  @ManyToOne(fetch = FetchType.LAZY)\n";
                code = code + "	@JoinColumn(name=\"" + cb.getName().toLowerCase() + "\")\n";
            } else {
                code = code + "	@Column(name=\"" + cb.getName().toLowerCase() + "\")\n";
            }
            name = sb.toString();
        }
        if (cb.getName().indexOf("_ID") > 0) {
            code = code + "	private " + type[type.length - 1] + " " + StringUtils.replace(name,"Id","") + ";";
        }else {
            code = code + "	private " + type[type.length - 1] + " " + name + ";";
        }
        return code;
    }


    public static String getterCode(ColumnBean cb) {
        cb = init(cb);
        StringBuilder code = new StringBuilder();
        code.append("	public ").append(cb.getType()).append(" get").append(toUpperIndexChar(cb.getName())).append("(){\n");
        code.append("		return ").append(cb.getName()).append(";\n");
        code.append("	}");
        return code.toString();
    }

    public static String setterCode(ColumnBean cb) {
        cb = init(cb);
        StringBuilder code = new StringBuilder();
        code.append("	public void ").append("set").append(toUpperIndexChar(cb.getName())).append("(").append(cb.getType()).append(" ").append(cb.getName()).append("){\n");
        code.append("		this.").append(cb.getName()).append(" = ").append(cb.getName()).append(";\n");
        code.append("	}");
        return code.toString();
    }

    public static ColumnBean init(ColumnBean cb) {
        ColumnBean tempBean = new ColumnBean();
        if ("java.sql.Timestamp".equals(cb.getType())) {
            cb.setType("java.util.Date");
        }
        if ("java.math.BigDecimal".equals(cb.getType())) {
            cb.setType("java.lang.Integer");
        }
        String[] type = cb.getType().split("\\.");
        tempBean.setType(type[type.length - 1]);
        String name = cb.getName();
        name = name.toLowerCase();
        if (name.contains("_")) {
            StringBuilder sb = new StringBuilder();
            String[] temp = name.split("_");
            sb.append(temp[0]);
            for (int i = 1; i < temp.length; i++) {
                sb.append(toUpperIndexChar(temp[i]));
            }
            name = sb.toString();
        }
        tempBean.setName(name);
        tempBean.setComment(cb.getComment());
        return tempBean;
    }

    public static String toUpperIndexChar(String str) {
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1, str.length());
    }

    public static String toLowerIndexChar(String str) {
        return String.valueOf(str.charAt(0)).toLowerCase() + str.substring(1, str.length());
    }

    public static boolean contains(String[] arr, String str) {
        if (arr == null || arr.length < 1) {
            return false;
        } else {
            for (String anArr : arr) {
                if (str.equalsIgnoreCase(anArr)) {
                    return true;
                }
            }
        }
        return false;
    }

}
