package com.hch.platform.pcore.modules.pw.vo;

/**
 * 入驻审核状态.
 * pw_enter_shstatus
 * @author chenhao
 */
public enum PwEnterShStatus {
  PESS_DSH("0", "待审核")
  ,PESS_DFP("1", "待分配")
  ,PESS_YFP("2", "已分配")
  ,PESS_DXQ("3", "即将到期")
  ,PESS_YTC("4", "已退出")
  ,PESS_FAIL("9", "入驻失败");

 private String key;
 private String name;

 private PwEnterShStatus(String key, String name) {
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
 public static PwEnterShStatus getByKey(String key) {
   if ((key != null)) {
     PwEnterShStatus[] entitys = PwEnterShStatus.values();
     for (PwEnterShStatus entity : entitys) {
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