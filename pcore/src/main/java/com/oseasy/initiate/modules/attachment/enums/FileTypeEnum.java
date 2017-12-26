package com.hch.platform.pcore.modules.attachment.enums;

/*文件来源对应sys_attachment表type字段*/
public enum FileTypeEnum implements StringEnum<FileTypeEnum>{
  S_SYS_CERT("10000", "系统证书"),
  S_FLOW_ICON("20000", "流程图标"),
  S_PW_ENTER("30000", "入驻附件"),

  /** 民大项目. */
  S10("10","民大项目"),

	/** 国创项目. */
	S0("0","国创项目"),

	/** 国创项目通告. */
	S1("1", "国创项目通告"),

	/** 双创大赛. */
	S2("2","双创大赛"),

	/** 双创大赛通告. */
	S3("3", "双创大赛通告"),

	/** 内容管理. */
	S4("4","内容管理"),

	/** 课程学分认定. */
	S5("5","课程学分认定"),

	/** 优秀展示. */
	S6("6","优秀展示"),

	/** 大赛热点. */
	S7("7","大赛热点"),

	/** 通告发布. */
	S8("8","通告发布"),

	/** 课程管理. */
	S9("9","课程管理"),

  /** 自定义. */
	S11("11","自定义")
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

	private FileTypeEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}

  /**
   * 根据key获取枚举 .
   *
   * @author chenhao
   * @param key 枚举标识
   * @return FileTypeEnum
   */
  public static FileTypeEnum getByValue(String value) {
    if ((value != null)) {
      FileTypeEnum[] entitys = FileTypeEnum.values();
      for (FileTypeEnum entity : entitys) {
        if ((value).equals(entity.getValue())) {
          return entity;
        }
      }
    }
    return null;
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
	@Override
	public String getStringValue() {
		return this.value;
	}
}
