/**
 * 
 */
package com.oseasy.initiate.common.utils.excel.fieldtype;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.entity.Area;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 字段类型转换


 */
public class AreaType {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		for (Area e : UserUtils.getAreaList()) {
			if (StringUtil.trimToEmpty(val).equals(e.getName())) {
				return e;
			}
		}
		return null;
	}

	/**
	 * 获取对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((Area)val).getName() != null) {
			return ((Area)val).getName();
		}
		return "";
	}
}
