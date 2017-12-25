package com.oseasy.initiate.modules.actyw.tool.process.vo;

public enum ProjectMarkType {
	 S1("gcontest","gcontest")
	,S2("project","project")
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

	private ProjectMarkType(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(ProjectMarkType e: ProjectMarkType.values()) {
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
