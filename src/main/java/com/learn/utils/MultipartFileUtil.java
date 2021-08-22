package com.learn.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 上传的文件处理工具类
 *
 */
public class MultipartFileUtil {

    public static final Logger logger = LoggerFactory.getLogger(MultipartFileUtil.class);

    /**
     * 根据文件名称获取后缀名
     *
     * @param fileName 文件名称
     * @return 结果
     */
    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取项目根路径
     *
     * @return 结果
     */
    public static String getRootPath(HttpServletRequest request) {
        String classPath =  request.getSession().getServletContext().getRealPath( "/" );
        String rootPath = "";
        //windows下
        if ("\\".equals(File.separator)) {
//            rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
            rootPath = classPath;
            rootPath = rootPath.replace("/", "\\");
            System.out.println("rootpath"+rootPath);
        }
        //linux下
        if ("/".equals(File.separator)) {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
            System.out.println("rootpath"+rootPath);
        }
        return rootPath;
    }

    /**
     * 判断文件是否未空，或者没有数据
     *
     * @param file 文件
     * @return 结果
     */
    public static boolean isEmpty(MultipartFile file) {
        return file == null || file.getSize() == 0 || "".equals(file.getOriginalFilename().trim());
    }


    /**
     * 上传文件
     *
     * @param prefix 存放文件的跟路径
     * @param file   文件 D:\eclipse02\apache-tomcat-9.0.22\webapps\ssm_property\cdn\cdn\10753fa4-3b89-4aad-9f3d-8f8fe828f419.jpg
     * @return 返回相对路径  cdn\cdn\10753fa4-3b89-4aad-9f3d-8f8fe828f419.jpg
     */
    public static Map<String, Object> uploadFile(String prefix, MultipartFile file, HttpServletRequest request) {
       /* prefix = "upload" + prefix; */ //   cdn\cdn
        //取得当前上传文件的文件名称
        String myFileName = file.getOriginalFilename();
        String suffix = myFileName.substring(myFileName.lastIndexOf(".") + 1); //jpg
        //重命名上传后的文件名
        String fileName = UUID.randomUUID().toString() + "." + suffix.toLowerCase();  //10753fa4-3b89-4aad-9f3d-8f8fe828f419.jpg

        //文件夹路径  D:\eclipse02\apache-tomcat-9.0.22\webapps\ssm_property
        //String filePath = getRootPath(request) + prefix + "\\"; // D:\eclipse02\apache-tomcat-9.0.22\webapps\ssm_property\cdn\cdn\

        //使用tomcat作为图片服务器，当然是有专门的图片服务器
        //截取到 webapps目录路径  D:\eclipse02\apache-tomcat-9.0.22\webapps
        String filePath = getRootPath(request);
        String subString = filePath.substring(0,filePath.indexOf("ssm_property"));

        //5.文件上传 D:\\eclipse02\\apache-tomcat-9.0.22\\webapps\\upload\\image
        String uploadPath = subString + prefix + "\\";
        File f = new File(uploadPath);
        // 目录已存在创建文件夹
        if (!f.exists()) {
            f.mkdirs();// 目录不存在的情况下，会抛出异常
            System.out.println("创建目录："+f);
        }

        //定义上传路径
        String path = uploadPath + fileName;
        System.out.println("prefix"+prefix+"===="+"suffix:"+suffix+"=====filename:"+fileName+"======uploadPath:"+uploadPath+"===path"+path);

        //创建文件
        File localFile = new File(path);
        try {
            file.transferTo(localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("imgUrl",path);
        map.put("url",prefix + "/" + fileName);

        return map;
       /* return path;*/
    }

    //上传图片
    public static String uploadPhoto(MultipartFile multipartFile){
        //获取绝对路径
        String path ="C:/upload";
        File f=new File(path);
        //如果不存在，直接创建
        if(!f.exists()){
            f.mkdirs();
        }
        //获取图片名称
        String filename = multipartFile.getOriginalFilename();
        //拼接的图片路径
        String filepath=path+"/"+filename;
        File file = new File(filepath);
        //上传图片
        try {
            multipartFile.transferTo(file);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(filepath);
        return filepath;
    }


    //回显图片
    public static void showPhoto(String photo,HttpServletRequest request, HttpServletResponse response){
        //使用tomcat作为图片服务器，当然是有专门的图片服务器
        //截取到 webapps目录路径  D:\eclipse02\apache-tomcat-9.0.22\webapps
        String filePath = getRootPath(request);
        String subString = filePath.substring(0,filePath.indexOf("ssm_property"));

        //获取图片的当前路径  放入读
        FileInputStream fis=null;
        //用response  获取一个写对象的流
        ServletOutputStream os=null;
        try {
            fis = new FileInputStream(photo);
            os = response.getOutputStream();
            //提高读写的速度
            byte[] b=new byte[1024];
            //边读边写
            while(fis.read(b)!=-1){
                os.write(b);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                if(os!=null){
                    os.close();
                }
                if(fis!=null){
                    fis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
}
