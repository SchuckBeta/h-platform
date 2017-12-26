package com.oseasy.initiate.modules.actyw.tool.process.vo;

public enum FormClientType {
  FST_FRONT("1", "前台"), FST_ADMIN("2", "后台");

  private String key;
  private String name;

  private FormClientType(String key, String name) {
    this.key = key;
    this.name = name;
  }

  /**
   * 根据key获取枚举 .
   *
   * @author chenhao
   * @param key 枚举标识
   * @return FormClientType
   */
  public static FormClientType getByKey(String key) {
    if ((key != null)) {
      FormClientType[] entitys = FormClientType.values();
      for (FormClientType entity : entitys) {
        if ((key).equals(entity.getKey())) {
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

  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }
}
