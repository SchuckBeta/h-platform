package com.oseasy.initiate.modules.attachment.enums;

/*文件来源对应sys_attachment表type字段*/
public enum FileSourceEnum {
	 S0("0","国创项目")
	,S1("1","国创项目通告")
	,S2("2","双创大赛")
	,S3("3","双创大赛通告")
	;

	//(0国创项目，1 国创项目通告，2 双创大赛，3双创大赛通告）
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

	private FileSourceEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(FileSourceEnum e:FileSourceEnum.values()) {
				if (e.value.equals(value)) {
					return e.name;
				}
			}
		}
		return "";
	}
	
}
