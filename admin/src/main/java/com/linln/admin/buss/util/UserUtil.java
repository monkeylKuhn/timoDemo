package com.linln.admin.buss.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.linln.modules.system.domain.User;

public class UserUtil {
	
	public static User getUserInfo(){
    	Subject subject = SecurityUtils.getSubject();
    	User principal = (User)subject.getPrincipal();
		return principal;
	}
	
}