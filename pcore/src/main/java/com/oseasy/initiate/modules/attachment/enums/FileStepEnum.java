package com.oseasy.initiate.modules.attachment.enums;

import java.util.List;

import com.google.common.collect.Lists;

/*文件类别对应sys_attachment表file_step字段*/
public enum FileStepEnum implements StringEnum<FileStepEnum>{
	/** 民大项目. */
	S2000("2000", FileTypeEnum.S10, "民大项目申报"),
	S2100("2100", FileTypeEnum.S10, "民大项目立项"),
	S2200("2200", FileTypeEnum.S10, "民大项目中期"),
	S2300("2300", FileTypeEnum.S10, "民大项目结项"),

	/** 双创项目. */
	S100("100", FileTypeEnum.S0, "双创项目申报"),
	S101("101", FileTypeEnum.S0, "双创项目周报"),
	S102("102", FileTypeEnum.S0, "双创项目中期检查"),
	S103("103", FileTypeEnum.S0, "双创项目结项"),

	 /** 双创项目通告. */
	S200("200", FileTypeEnum.S1, "双创项目通告"),

	/** 大赛申报. */
	S300("300", FileTypeEnum.S2, "大赛申报"),

	/** 优秀展示. */
	S601("601", FileTypeEnum.S6, "优秀展示封面"),
	S602("602", FileTypeEnum.S6, "优秀展示内容"),

	/** 大赛热点内容. */
	S701("701", FileTypeEnum.S7, "大赛热点内容"),

	/** 通告发布内容. */
	S801("801", FileTypeEnum.S8,"通告发布内容"),

	/** 课程. */
	S900("900", FileTypeEnum.S9, "课程课件"),

	/** 入驻. */
  S_ENTER_TEAM("31000", FileTypeEnum.S_PW_ENTER, "入驻团队附件"),
  S_ENTER_PROJECT("32000", FileTypeEnum.S_PW_ENTER, "入驻项目附件"),
  S_ENTER_COMPANY("33000", FileTypeEnum.S_PW_ENTER, "入驻企业附件"),

  /** 流程图. */
  S_FLOW_NODELV1("20000", FileTypeEnum.S_FLOW_ICON, "节点一级图标"),
  S_FLOW_NODELV2("20001", FileTypeEnum.S_FLOW_ICON, "节点二级图标"),

  /** 自定义. */
	S1100("1100", FileTypeEnum.S11, "自定义")
	;

	//(100双创项目申报，101双创项目周报，102双创项目中期检查，103双创项目结项，200双创项目通告,300双创大赛。。。,301,400双创大赛通告。。。,401）自己加
	private String value;
	private String name;
	private FileTypeEnum type;

	private FileStepEnum(String value, FileTypeEnum type, String name) {
    this.value = value;
    this.name = name;
    this.type = type;
  }

  public FileTypeEnum getType() {
    return type;
  }

  public void setType(FileTypeEnum type) {
    this.type = type;
  }

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

	private FileStepEnum(String value, String name) {
		this.value=value;
		this.name=name;
	}

	 /**
	  * 根据类型获取 .
	  * @author chenhao
	  * @param key 枚举标识
	  * @return List
	  */
	 public static List<FileStepEnum> getByType(FileTypeEnum type) {
	   if (type != null) {
	     List<FileStepEnum> fileSteps = Lists.newArrayList();
	     FileStepEnum[] entitys = FileStepEnum.values();
	     for (FileStepEnum entity : entitys) {
	       if ((type).equals(entity.getType())) {
	         fileSteps.add(entity);
	       }
	     }
	     return fileSteps;
	   }
	   return null;
	 }

	 /**
	  * 根据key获取枚举 .
	  *
	  * @author chenhao
	  * @param key 枚举标识
	  * @return FileStepEnum
	  */
	 public static FileStepEnum getByValue(String value) {
	   if ((value != null)) {
	     FileStepEnum[] entitys = FileStepEnum.values();
	     for (FileStepEnum entity : entitys) {
	       if ((value).equals(entity.getValue())) {
	         return entity;
	       }
	     }
	   }
	   return null;
	 }

	public static String getNameByValue(String value) {
		if (value!=null) {
			for(FileStepEnum e:FileStepEnum.values()) {
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
