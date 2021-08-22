package com.learn.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shiro权限标签(Velocity版)
 * 
 */
public class VelocityShiro {
	  private Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 是否拥有该权限
	 * @param permission  权限标识
	 * @return   true：是     false：否
	 */
	public boolean hasPermission(String permission) {
		logger.info(permission);
		//获取当前的用户对象 subject
		Subject subject = SecurityUtils.getSubject();
		//该当前用户不为空且是被授权（有这个权限才能操作）
		return subject != null && subject.isPermitted(permission);
	}

}
