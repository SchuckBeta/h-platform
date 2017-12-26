package com.hch.platform.pcore.modules.sys.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

public class SmsCredentialsMatcher extends SimpleCredentialsMatcher {  
    @Override  
       public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {  
    	MyUsernamePasswordToken token = (MyUsernamePasswordToken) authcToken;  
           if (!token.isAdopt()) {
        	   throw new AuthenticationException("msg:短信验证码错误, 请重试.");
           }
           return true;
       }  
}  
