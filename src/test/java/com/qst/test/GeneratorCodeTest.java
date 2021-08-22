package com.qst.test;

import com.learn.utils.GenUtils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class GeneratorCodeTest {
    @Test
    public void test() throws Exception {
        //构建数据
        Map<String,Object> data = new HashMap<>();
        data.put("package","com.learn");
        data.put("className","Account");
        data.put("classname","account");

        //构建模板列表
        List<String> templates = new ArrayList<>();
        templates.add("vms/Controller.java.vm");

        //构建输出流对象
        File file  = new File("c:\\upload\\code.zip");
        //FileOutputStream outputStream = new FileOutputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        System.out.println(outputStream);
        //ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        //调用方法生成代码
        GenUtils.generatorCode(data,templates,zipOutputStream);
        zipOutputStream.close();


    }
}
