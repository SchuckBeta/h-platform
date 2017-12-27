/**
 *
 */
package com.hch.platform.pcore.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hch.platform.pcore.common.persistence.AbsBaseEntity;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * Service基类

 * @version 2014-05-16
 */
//@Transactional(readOnly = true)
public abstract class AbsBaseService {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 数据范围过滤
	 * @param user 当前用户对象，通过“entity.getCurrentUser()”获取
	 * @param officeAlias 机构表别名，多个用“,”逗号隔开。
	 * @param userAlias 用户表别名，多个用“,”逗号隔开，传递空，忽略此参数
	 * @return 标准连接条件对象
	 */
	public abstract String dataScopeFilter(AbsUser user, String officeAlias, String userAlias);

	/**
	 * 数据范围过滤（符合业务表字段不同的时候使用，采用exists方法）
	 * @param entity 当前过滤的实体类
	 * @param sqlMapKey sqlMap的键值，例如设置“dsf”时，调用方法：${sqlMap.sdf}
	 * @param officeWheres office表条件，组成：部门表字段=业务表的部门字段
	 * @param userWheres user表条件，组成：用户表字段=业务表的用户字段
	 * @example
	 * 		dataScopeFilter(user, "dsf", "id=a.office_id", "id=a.create_by");
	 * 		dataScopeFilter(entity, "dsf", "code=a.jgdm", "no=a.cjr"); // 适应于业务表关联不同字段时使用，如果关联的不是机构id是code。
	 */
	public abstract void dataScopeFilter(AbsBaseEntity<?> entity, String sqlMapKey, String officeWheres, String userWheres);
}
