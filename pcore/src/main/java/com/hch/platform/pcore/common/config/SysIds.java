package com.hch.platform.pcore.common.config;

/**
 * 系统固定ID
 *
 * @author Administrator
 */
public enum SysIds {
    SYS_USER_SUPER("1", "系统超级管理员用户"),
    SYS_ROLE_SUPER("1", "系统超级管理员角色"),
    SYS_ROLE_ADMIN("10", "系统管理员角色"),
    SYS_ROLE_USER("13757518f4da45ecaa32a3b582e8396a", "学生/导师角色"),
    SYS_ADMIN_ROLE("f9c12c05add2409dac0fcdec9387e63c", "学校管理员角色Id"),
    SYS_OFFICE_TOP("1", "系统顶级机构"),
    SYS_NO_ROOT("0", "系统序号最大值记录"),
    SYS_TREE_ROOT("1", "系统树根节点"),
    SYS_TREE_PROOT("0", "系统树根节点父节点"),
    SITE_TOP("1", "系统官方站点"),
    SITE_CATEGORYS_SYS_ROOT("1", "系统顶级栏目"),
    SITE_CATEGORYS_TOP_ROOT("ca46923a84ef4754b58ae89e21e97d69", "系统网站顶级栏目"),
    SITE_CATEGORYS_TOP_INDEX("3817dff6b23a408b8fe131595dfffcbc", "系统官方网站首页栏目"),
    SITE_CATEGORYS_GCONTEST_ROOT("448e7bc14f3c477fa31a7c47fe016c12", "系统大赛栏目"),
    SITE_MENU_GCONTEST_ROOT("85c6095f275540b9980dde2b06d77382", "系统大赛菜单"),
    SITE_CATEGORYS_PROJECT_ROOT("c5c65c9a80a849cfbe4a05741b78d902", "系统项目栏目"),
    SITE_MENU_PROJECT_ROOT("5474e38a3c8a46f590939df6a453d5f8", "系统项目菜单"),
    SITE_PW_ENTER_ROOT("7cff6f6b9b494e25a38877fc634a613a", "入驻管理"),
    SITE_PW_APPOINTMENT_ROOT("5b65b4e07abf4827b7d7d5f0a65f5b50", "预约管理"),
    ISMS("0001", "秘书"),
    ISCOLLEGE("0002", "院");

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
