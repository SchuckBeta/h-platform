/**
 * 
 */
package com.hch.platform.pcore.modules.sys.security;

/**
 * 用户和密码（包含验证码）令牌类

 * @version 2013-5-19
 */
public class MyUsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

	private static final long serialVersionUID = 1L;

	private String captcha;
	private boolean mobileLogin;
	private String loginType;//1-短信登录,2-微信扫码
	private boolean adopt;//短信验证通过或者微信扫码成功
	public MyUsernamePasswordToken() {
		super();
	}

	public MyUsernamePasswordToken(String username, char[] password,
			boolean rememberMe, String host, String captcha, boolean mobileLogin) {
		super(username, password, rememberMe, host);
		this.captcha = captcha;
		this.mobileLogin = mobileLogin;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public boolean isMobileLogin() {
		return mobileLogin;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public boolean isAdopt() {
		return adopt;
	}

	public void setAdopt(boolean adopt) {
		this.adopt = adopt;
	}
	
	
}