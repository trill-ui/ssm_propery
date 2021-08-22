package com.learn.utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类

 */
public class GenUtils {


    /**
     * 生成java代码
     * @param data  填充到模板中的数据
     * @param templates 模板名称
     * @param zip 输出流
     */
    public static void generatorCode(Map<String, Object> data,
                                     List<String> templates, ZipOutputStream zip) throws IOException {


        //1.设置velocity的资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //2.初始化velocity引擎
        Velocity.init(prop);
        //3.创建velocity模板文件
        VelocityContext context = new VelocityContext(data);
        //遍历模板列表，获取每一个模板，基于每一个模板生成一个代码
        for(String template : templates){
            //4.加载velocity模板文件
            Template tpl = Velocity.getTemplate(template, "utf-8");
            //5.合并数据到模板
            StringWriter sw = new StringWriter();
            tpl.merge(context,sw);

            //把代码数据输出到zip文件中
            String fileName = getFileName(template, data.get("className").toString(), data.get("package").toString());
            zip.putNextEntry(new ZipEntry(fileName));

            IOUtils.write(sw.toString(),zip,"UTF-8");
            //6.释放资源
            IOUtils.closeQuietly(sw);
        }
    }

    /**
     * 获取文件名
     * 根据模板名称，类名称，包名称拼接一个完成的文件路径和名称
     * @param template 模板名称 Controller.java.vm
     * @param className 实体类名称 User
     * @param packageName 包名称  com.learn
     * @return main/java/com/learn/controller/UserController.java
     * main/java/com/learn/service/UserService.java
     */
    public static String getFileName(String template, String className, String packageName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        /*判断参数：是否不为空，长度是否不为0，值是否不包含空白字符*/
        if (StringUtils.isNotBlank(packageName)) {
            /*把com.learn中的点换成斜杠*/
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        /*实体类层*/
        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        /*dao层*/
        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }


        if (template.contains("Dao.xml.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.xml";
        }

        /*服务层*/
        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        /*控制层*/
        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator + "page"
                    + File.separator + "admin" + File.separator + className.toLowerCase() + ".html";
        }

        if (template.contains("list.js.vm")) {
            return "main" + File.separator + "webapp" + File.separator + "js" + File.separator + "admin" + File.separator + className.toLowerCase() + ".js";
        }

        if (template.contains("menu.sql.vm")) {
            return className.toLowerCase() + "_menu.sql";
        }

        return null;
    }

    public static void main(String[] args){
        String fileName = getFileName("Controller.java.vm", "User", "com.learn");
        System.out.println(fileName); //main\java\com\learn\controller\UserController.java

    }
}
