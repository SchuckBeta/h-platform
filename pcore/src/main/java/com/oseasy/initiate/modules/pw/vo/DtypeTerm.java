package com.hch.platform.pcore.modules.pw.vo;

import java.util.Date;

import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.DateUtil.Dtype;

public enum DtypeTerm {
  YEAR_10(3653, Dtype.YEAR, 10, "十年"),
  YEAR_5(1826, Dtype.YEAR, 5, "五年"),
  YEAR_4(1461, Dtype.YEAR, 4, "四年"),
  YEAR_3(1095, Dtype.YEAR, 3, "三年"),
  YEAR_2(730, Dtype.YEAR, 2, "二年"),
  YEAR_1(365, Dtype.YEAR, 1, "一年"),
  YEAR_A_HALF(183, Dtype.MONTH, 6, "半年"),
  QUARTER(92, Dtype.MONTH, 3, "季度"),
  MONTH(31, Dtype.MONTH, 1, "月"),
  DAY(1, Dtype.DAY, 1, "天");

  private Integer num;
  private Dtype type;
  private Integer tnum;
  private String remarks;
  private DtypeTerm(Integer num, Dtype type, Integer tnum, String remarks) {
    this.num = num;
    this.tnum = tnum;
    this.type = type;
    this.remarks = remarks;
  }

  public Integer getTnum() {
    return tnum;
  }

  public void setTnum(Integer tnum) {
    this.tnum = tnum;
  }

  public Integer getNum() {
    return num;
  }
  public Dtype getType() {
    return type;
  }
  public void setType(Dtype type) {
    this.type = type;
  }
  public String getRemarks() {
    return remarks;
  }

  /**
   * 根据num获取枚举 .
   * @author chenhao
   * @param num 枚举标识
   * @return DtypeTerm
   */
  public static DtypeTerm getByNum(Integer num) {
    if ((num != null)) {
      DtypeTerm[] entitys = DtypeTerm.values();
      for (DtypeTerm entity : entitys) {
        if ((entity.getNum()).equals(num)) {
          return entity;
        }
      }
    }
    return null;
  }

  /**
   * 根据类型添加天数.
   * @param type Dtype年、半年、月、日、季度
   * @param num 添加数量
   * @param date
   * @return Integer
   */
  public static Integer addDayByType(Integer num, Date date) {
    return addDayByType(getByNum(num), date);
  }

  public static Integer addDayByType(DtypeTerm dtypeTerm, Date date) {
    if(dtypeTerm == null){
      return null;
    }
    return DateUtil.addDayByType(dtypeTerm.getType(), dtypeTerm.getTnum(), date, false);
  }
}
