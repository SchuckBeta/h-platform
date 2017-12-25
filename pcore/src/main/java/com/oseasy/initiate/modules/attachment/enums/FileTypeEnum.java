package com.oseasy.initiate.modules.attachment.enums;

/*文件类别对应sys_attachment表file_step字段*/
public enum FileTypeEnum {
	 S100("100","国创项目申报")
	,S101("101","国创项目周报")
	,S102("102","国创项目中期检查")
	,S103("103","国创项目结项")
	,S200("200","国创项目通告")
	;

	//(100国创项目申报，101国创项目周报，102国创项目中期检查，103国创项目结项，200国创项目通告,300双创大赛。。。,301,400双创大赛通告。。。,401）自己加
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

	private FileTypeEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}
	public static String getNameByValue(String value) {
		if (value!=null) {
			for(FileTypeEnum e:FileTypeEnum.values()) {
				if (e.value.equals(value)) {
					return e.name;
				}
			}
		}
		return "";
	}
	
}
