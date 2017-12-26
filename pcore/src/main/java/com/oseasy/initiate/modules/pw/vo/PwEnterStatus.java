package com.hch.platform.pcore.modules.pw.vo;

import com.hch.platform.putil.common.utils.StringUtil;

/**
 * 入驻类型.
 * pw_enter_status
 * @author chenhao
*/
public enum PwEnterStatus {
 PES_DSH("0", "待审核")
 ,PES_RZCG("1", "入驻成功（待分配）")
 ,PES_RZSB("2", "入驻失败")
 ,PES_DXQ("3", "即将到期")
 ,PES_YXQ("4", "已续期")
 ,PES_YTF("5", "已退孵")
 ,PES_RZCGYFP("6", "入驻成功（已分配）");

 private String key;
 private String name;

 private PwEnterStatus(String key, String name) {
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
 public static PwEnterStatus getByKey(String key) {
   if ((key != null)) {
     PwEnterStatus[] entitys = PwEnterStatus.values();
     for (PwEnterStatus entity : entitys) {
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

  /**
   * 取消入驻.
   */
  public static String getKeyByQXRZ() {
    return PwEnterStatus.PES_RZCG.getKey() + StringUtil.DOTH + PwEnterStatus.PES_DXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_YXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_RZCGYFP.getKey() + StringUtil.DOTH + PwEnterStatus.PES_YTF.getKey();
  }


  /**
   * 待分配.
   */
  public static String getKeyByDFP() {
    return PwEnterStatus.PES_RZCG.getKey();
  }

  /**
   * 已分配.
   */
  public static String getKeyByYFP() {
    return PwEnterStatus.PES_DXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_YXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_RZCGYFP.getKey();
  }

  /**
   * 续期入驻.
   */
  public static String getKeyByXQRZ() {
    return PwEnterStatus.PES_YXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_RZCGYFP.getKey();
//    return PwEnterStatus.PES_RZCG.getKey() + StringUtil.DOTH + PwEnterStatus.PES_DXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_YXQ.getKey() + StringUtil.DOTH + PwEnterStatus.PES_RZCGYFP.getKey();
  }
}