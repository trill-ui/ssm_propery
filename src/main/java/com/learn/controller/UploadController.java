package com.learn.controller;

import com.learn.entity.RoomEntity;
import com.learn.utils.MultipartFileUtil;
import com.learn.utils.R;
import com.learn.utils.RRException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * 文件上传
 *
 */
@RestController
@RequestMapping("file")
public class UploadController {

    public static String[] suffixs = {"IMG", "PNG", "JPG", "JPEG", "GIF", "BPM"};

    /**
     * 上传文件
     */
    @RequestMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }
        Map<String,Object> map = MultipartFileUtil.uploadFile("upload", file, request);
      /*  String url = MultipartFileUtil.uploadPhoto(file);*/
        //roomService.save(room);
        return R.ok(map);
    }


    //显示图片
    @RequestMapping("/show")
    public void show(String path,HttpServletRequest request, HttpServletResponse response){
        //upload/1d461ea2-35a7-4ac1-a5ae-ae2641b1cb0f.jpg
        System.out.println("path:"+path);
        MultipartFileUtil.showPhoto(path,request,response);
    }

    /**
     * 上传资讯内容的图片
     *
     * @param upload   图片
     * @param response 响应
     */
    @ResponseBody
    @RequestMapping("ckEditorUpload")
    public void uploadFile(MultipartFile upload, String CKEditorFuncNum, HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            String path = null;

            if (upload != null && !upload.isEmpty()) {

                //String url = MultipartFileUtil.uploadFile("\\cdn", upload, request);
                //path = url;
            }


            // 返回“图像”选项卡并显示图片
            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ",'" + path + "','')");
            out.println("</script>");

        } catch (RuntimeException e) {
            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ",'','" + e.getMessage() + "');");
            out.println("</script>");
        }
    }


}
