package com.oseasy.initiate.modules.sys.utils;

public class RegexUtils {
	public static  String mobileRegex = "^0?(13[0-9]|15[012356789]|18[0-9]|17[0-9])[0-9]{8}$";
	public static  String emailRegex = "^[a-zA-Z0-9_-][\\.a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
	public static  String grantRegex="^0|0.0|0.00|[1-9][0-9]*(\\.[0-9]{1,2})?$";
}
