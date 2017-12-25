package com.oseasy.initiate.modules.actyw.tool.process.vo;

public enum FormType {
	 S1("applyFrom","项目申报")
	,S2("auditForm","项目审核")
	,S3("listFrontForm","项目前台列表")
	,S4("listAdminForm","项目后台变更列表")
	,S5("scoreForm","项目打分")
	,S6("changeForm","项目变更")
	,S7("auditWpListForm","项目网评列表")
	,S8("auditLyListForm","项目路演列表")
	,S9("auditEndListForm","项目评级列表")
/*	,S8("8","项目终止")
	,S9("9","项目结项")*/
	;

	private String value;
	private String name;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private FormType(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(FormType e: FormType.values()) {
				if (e.value.equals(value)) {
					return e.name;
				}
			}
		}
		return "";
	}

/*	public static Map getMap() {
		Map map =new HashMap<>();

		for(FormTypeEnum e: FormTypeEnum.values()) {
			if (e.value.equals(value)) {
				return e.name;
			}
		}

		return map;
	}*/

}
