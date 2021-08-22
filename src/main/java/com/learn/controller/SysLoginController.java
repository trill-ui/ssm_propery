package com.learn.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.learn.entity.SysUserEntity;
import com.learn.service.SysUserService;
import com.learn.utils.R;
import com.learn.utils.ShiroUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录相关
 */
@Controller
public class SysLoginController {
    @Autowired
    private Producer producer;

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("captcha.jpg")
    public void captcha(HttpServletResponse response) throws ServletException, IOException {
        // 不需要浏览器进行图片缓存(浪费空间，因为只用一次)
        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        System.out.println("验证码："+text);
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);

        // 使用生成的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }

    /**
     * 登录
     */
    @ResponseBody
    @RequestMapping(value = "/sys/login", method = RequestMethod.POST)
    public R login(String username, String password, String captcha) throws IOException {
        //从session中取出kaptcha生成的验证码text值
		String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
		System.out.println("==验证码=="+kaptcha);
		System.out.println("==password=="+password);
		System.out.println("==username=="+username);
        //获取用户页面输入的验证码
		if(!captcha.equalsIgnoreCase(kaptcha)){
			return R.error("验证码不正确");
		}
		if(password==null){
			return R.error("密码不能为空");
		}

        try {
            //获取当前用户
            Subject subject = ShiroUtils.getSubject();
            //sha256加密（安全散列算法）
            //password = new Sha256Hash(password).toHex();
            //System.out.println("==sha256加密==="+password);
            //md5加密，不加盐
            password = new Md5Hash(password).toHex();
            System.out.println("md5加密，不加盐："+password);
            //md5加密，加盐
            //password = new Md5Hash(password,username,1).toHex();
            //System.out.println("md5加密，加盐："+password);
            //使用simpleHash
            //password = new SimpleHash("MD5",password,username,1).toHex();
            //System.out.println("simpleHash:"+password);
            //封装到令牌（Token）
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            //执行登录的方法，如果没有一次就说明OK了
            subject.login(token);
        } catch (UnknownAccountException e) {  //用户名不存在
            return R.error(e.getMessage());
        } catch (IncorrectCredentialsException e) { //密码不存在
            return R.error(e.getMessage());
        } catch (LockedAccountException e) {
            return R.error(e.getMessage());
        } catch (AuthenticationException e) {
            return R.error("账户验证失败");
        }

        return R.ok();
    }


    /**
     */
    @ResponseBody
    @RequestMapping(value = "/sys/reg", method = RequestMethod.POST)
    public R reg(String username, String password, String captcha) throws IOException {
		String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
		if(!captcha.equalsIgnoreCase(kaptcha)){
			return R.error("验证码不正确");
		}

        System.out.println("密码："+password);
        //md5加密，不加盐
        SysUserEntity user = new SysUserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(1);

        List<Long> roles = new ArrayList<>();
        roles.add(1L); //默认新用户为普通用户

        user.setRoleIdList(roles);

        this.sysUserService.save(user);

        return R.ok();
    }

    /**
     * 退出
     */
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        ShiroUtils.logout();
        return "redirect:login.html";
    }

}
