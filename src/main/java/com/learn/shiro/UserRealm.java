package com.learn.shiro;

import com.learn.entity.SysMenuEntity;
import com.learn.entity.SysUserEntity;
import com.learn.service.SysMenuService;
import com.learn.service.SysUserService;
import com.learn.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 认证
 * 自定义的UserRealm
 */
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    
    /**
     * 授权(验证权限时调用)
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了=》授权doGetAuthorizationInfo");
		 // 从 principals获取主身份信息
        // 将getPrimaryPrincipal方法返回值转为真实身份类型（在上边的doGetAuthenticationInfo认证通过填充到SimpleAuthenticationInfo中身份类型）
		SysUserEntity user = (SysUserEntity)principals.getPrimaryPrincipal();
		Long userId = user.getUserId();

        // 根据身份信息获取权限信息   根据身份信息从数据库中查询权限数据
        // 从数据库获取到权限数据
		List<String> permsList = null;
		/*
		Shiro 支持三种方式的授权：
        编程式：通过写if/else 授权代码块完成：
        注解式：通过在执行的Java方法上放置相应的注解完成：@RequiresRoles("admin")
        JSP/GSP 标签：在JSP/GSP 页面通过相应的标签完成：
        <shiro:hasRole name="admin">    <!— 有权限—>
        </shiro:hasRole>
		* */
		//系统管理员，拥有最高权限
		if(userId == Constant.SUPER_ADMIN){
			List<SysMenuEntity> menuList = sysMenuService.queryList(new HashMap<String, Object>());
			permsList = new ArrayList<>(menuList.size());
			for(SysMenuEntity menu : menuList){
				permsList.add(menu.getPerms());
			}
		}else{
			permsList = sysUserService.queryAllPerms(userId);
		}

		//用户权限列表
		Set<String> permsSet = new HashSet<String>();
		for(String perms : permsList){
			if(StringUtils.isBlank(perms)){
				continue;
			}
			// 将数据库中的权限标签 符放入集合
			permsSet.addAll(Arrays.asList(perms.trim().split(",")));
		}
		 // 查到权限数据，返回授权信息(要包括 上边的permissions)
        //返回认证信息由父类AuthorizingRealm进行认证
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		 // 将上边查询到授权信息填充到simpleAuthorizationInfo对象中
		info.setStringPermissions(permsSet);
		return info;
	}

	/**
	 * 认证(登录时调用)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了=》认证doGetAuthenticationInfo");
        //从封装的令牌token中取
       /* UsernamePasswordToken userToken  = (UsernamePasswordToken) token;
        userToken.getUsername();
        userToken.getPassword();*/
		String username = (String) token.getPrincipal();
		//获取shiro加密后的密码
        String password = new String((char[]) token.getCredentials());
        System.out.println(password);
        
        //查询用户信息
        SysUserEntity user = sysUserService.queryByUserName(username);
        
        //账号不存在
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }
        
        //密码错误
        if(!password.equals(user.getPassword())) {
            throw new IncorrectCredentialsException("账号或密码不正确");
        }

        //账号锁定
        if(user.getStatus() == 0){
        	throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        //密码认证 shiro做
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        return info;
	}

}
