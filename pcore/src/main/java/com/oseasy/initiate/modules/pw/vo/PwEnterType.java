package com.oseasy.initiate.modules.pw.vo;

/**
 * 入驻类型.
 * pw_enter_type
 * @author chenhao
 */
public enum PwEnterType {
  PET_TEAM("0", "团队")
 ,PET_XM("1", "项目")
 ,PET_QY("2", "企业");

 private String key;
 private String name;

 private PwEnterType(String key, String name) {
   this.key = key;
   this.name = name;
 }

 /**
  * 根据key获取枚举 .
  *
  * @author chenhao
  * @param key 枚举标识
  * @return PwEnterType
  */
 public static PwEnterType getByKey(String key) {
   if ((key != null)) {
     PwEnterType[] entitys = PwEnterType.values();
     for (PwEnterType entity : entitys) {
       if ((entity.getKey() != null) && (key).equals(entity.getKey())) {
         return entity;
       }
     }
   }
   return null;
 }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }
}