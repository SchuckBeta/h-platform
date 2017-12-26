package com.oseasy.initiate.modules.pw.vo;

import com.oseasy.initiate.common.config.Global;

/**
 * 创业基地模块系统配置常量类.
 * @author chenhao
 */
public class SvalPw {
  /**
   * 最大申报记录数.
   */
  public static final String IS_MAX = "isMax";
  public static final String MAXNUM = "maxNum";
  /**
   * 限制最大申请记录数.
   */
  public static final String ENTER_APPLY_MAXNUM = Global.getConfig("enter.apply.maxNum");
  /**
   * 设置续期提醒天数.
   */
  public static final String ENTER_EXPIRE_MAXDAY = Global.getConfig("enter.expire.maxDay");
  public static final String ENTER_EXPIRE_LOGFILE = Global.getConfig("enter.expire.logFile");

  public static Integer getEnterApplyMaxNum() {
    return Integer.parseInt(ENTER_APPLY_MAXNUM);
  }
  public static Integer getEnterExpireMaxDay() {
    return Integer.parseInt(ENTER_EXPIRE_MAXDAY);
  }
}
