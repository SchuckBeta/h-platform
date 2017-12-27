/**
 *
 */
package com.hch.platform.pcore.modules.sys.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 字典工具类

 * @version 2014-11-7
 */
public abstract class AbsLogUtils {
	/**
	 * 保存日志
	 */
	public abstract void saveLog(HttpServletRequest request, String title);

	/**
	 * 保存日志
	 */
	public abstract void saveLog(HttpServletRequest request, Object handler, Exception ex, String title);

	/**
	 * 获取菜单名称路径（如：系统设置-机构用户-用户管理-编辑）
	 */
	public abstract String getMenuNamePath(String requestUri, String permission);
}
