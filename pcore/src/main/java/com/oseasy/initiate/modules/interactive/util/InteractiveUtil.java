package com.oseasy.initiate.modules.interactive.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.interactive.entity.SysViews;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * Created by zhangzheng on 2017/7/5.
 */
public class InteractiveUtil {
    public static void updateViews(String foreignId,HttpServletRequest request,String queue){
    	User user= UserUtils.getUser();
        SysViews sc=new SysViews();
        sc.setId(IdGen.uuid());
        sc.setCreateDate(new Date());
        sc.setUserId(user.getId());
        sc.setForeignId(foreignId);
        sc.setDelFlag("0");
        sc.setIp(getIpAddr(request));
        CacheUtils.lpush(queue, sc);
    }
    public static String getIpAddr(HttpServletRequest request) {     
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}   
        if(StringUtil.isEmpty(ip)){
        	ip="unknown";
        }
        return ip;     

    } 
}
