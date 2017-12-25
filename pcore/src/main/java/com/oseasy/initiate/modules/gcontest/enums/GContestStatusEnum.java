package com.oseasy.initiate.modules.gcontest.enums;

public enum GContestStatusEnum {
	 S0("0","未提交")
	,S1("1","待学院专家打分")
	,S2("2","待学院秘书审核")
	,S3("3","待学校专家打分")
	,S4("4","待学校管理员审核")
	,S5("5","待学校管理员路演审核")
	,S6("6","待学校管理员评级")
	,S7("7","校赛评级结束")
	,S8("8","校赛未通过")
	,S9("9","院赛未通过")
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

	private GContestStatusEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(GContestStatusEnum e:GContestStatusEnum.values()) {
				if (e.value.equals(value)) {
					return e.name;
				}
			}
		}
		return "";
	}
	
}
