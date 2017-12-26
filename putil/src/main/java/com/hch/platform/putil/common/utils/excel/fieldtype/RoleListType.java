/**
 * 
 */
package com.oseasy.initiate.common.utils.excel.fieldtype;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.Collections3;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;

/**
 * 字段类型转换


 */
public class RoleListType {

	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);
	
	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		List<Role> roleList = Lists.newArrayList();
		List<Role> allRoleList = systemService.findAllRole();
		for (String s : StringUtil.split(val, ",")) {
			for (Role e : allRoleList) {
				if (StringUtil.trimToEmpty(s).equals(e.getName())) {
					roleList.add(e);
				}
			}
		}
		return roleList.size()>0?roleList:null;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null) {
			@SuppressWarnings("unchecked")
			List<Role> roleList = (List<Role>)val;
			return Collections3.extractToString(roleList, "name", ", ");
		}
		return "";
	}
	
}
