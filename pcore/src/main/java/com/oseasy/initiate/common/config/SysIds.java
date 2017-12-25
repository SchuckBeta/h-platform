package com.oseasy.initiate.common.config;

/**
 * 系统固定ID
 * @author Administrator
 *
 */
public enum SysIds {
	SYS_OFFICE_TOP("1", "系统顶级机构"),
	SYS_NO_ROOT("0", "系统序号最大值记录"),
  SYS_TREE_ROOT("1", "系统树根节点"),
  SYS_TREE_PROOT("0", "系统树根节点父节点"),
	SITE_TOP("1", "系统官方站点"),
	SITE_CATEGORYS_SYS_ROOT("1", "系统顶级栏目"),
	SITE_CATEGORYS_TOP_ROOT("ca46923a84ef4754b58ae89e21e97d69", "系统网站顶级栏目"),
  SITE_CATEGORYS_TOP_INDEX("3817dff6b23a408b8fe131595dfffcbc", "系统官方网站首页栏目");



	private String id;
	private String remark;
	private SysIds(String id, String remark) {
		this.id = id;
		this.remark = remark;
	}

	public String getId() {
		return id;
	}


	public String getRemark() {
		return remark;
	}

	public static SysIds getById(String id) {
		SysIds[] entitys = SysIds.values();
		for (SysIds entity : entitys) {
			if ((id).equals(entity.getId())) {
				return entity;
			}
		}
		return null;
	}
}
