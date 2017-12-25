/**
 * 
 */
package com.oseasy.initiate.modules.sys.utils;

import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.sys.dao.OfficeDao;
import com.oseasy.initiate.modules.sys.entity.Office;

/**
 * 工具类


 */
public class OfficeUtils {
	
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static String schoolName="schoolName";
	public static Office getOrgByName(String name) {
		Office o = (Office)CacheUtils.get(UserUtils.CACHE_OFFICE,name);
		if (o==null) {
			o=officeDao.getOrgByName(name);
			if (o==null) {
				return o;
			}
			CacheUtils.put(UserUtils.CACHE_OFFICE, name,o);
		}
		return o;
	}
	public static Office getSchool() {
		Office o = (Office)CacheUtils.get(UserUtils.CACHE_OFFICE,schoolName);
		if (o==null) {
			o = officeDao.getSchool();
			if (o==null) {
				return o;
			}
			CacheUtils.put(UserUtils.CACHE_OFFICE, schoolName,o);
		}
		return o;
	}
	public static Office getOfficeByName(String officename) {
		Office o = (Office)CacheUtils.get(UserUtils.CACHE_OFFICE,officename);
		if (o==null) {
			o=officeDao.getOfficeByName(officename);
			if (o==null) {
				return o;
			}
			CacheUtils.put(UserUtils.CACHE_OFFICE, officename,o);
		}
		return o;
	}
	public static Office getProfessionalByName(String officename,String pname) {
		Office o = (Office)CacheUtils.get(UserUtils.CACHE_OFFICE,officename+"_"+pname);
		if (o==null) {
			o=officeDao.getProfessionalByName(officename,pname);
			if (o==null) {
				return o;
			}
			CacheUtils.put(UserUtils.CACHE_OFFICE, officename+"_"+pname,o);
		}
		return o;
	}
	public static void clearCache() {
		CacheUtils.removeAll(UserUtils.CACHE_OFFICE);;
	}
}
