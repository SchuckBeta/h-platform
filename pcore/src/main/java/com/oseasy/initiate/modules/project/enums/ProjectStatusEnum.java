package com.oseasy.initiate.modules.project.enums;

public enum ProjectStatusEnum {
	 S0("0","未提交")
	,S1("1","待学院立项审核")
	,S2("2","待学校立项审核")
	,S3("3","待提交中期报告")
	,S4("4","待修改中期报告")
	,S5("5","待中期检查")
	,S6("6","待提交结项报告")
	,S7("7","待结项审核")
	,S8("8","项目终止")
	,S9("9","项目结项")
	;

	//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
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

	private ProjectStatusEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(ProjectStatusEnum e:ProjectStatusEnum.values()) {
				if (e.value.equals(value)) {
					return e.name;
				}
			}
		}
		return "";
	}
	
}
