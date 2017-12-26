package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.common.utils.json.JsonAliUtils;

public class RtPxFormproperties {
  private List<RtPxFormPropertie> formProperties;

  public RtPxFormproperties() {
    super();
    // TODO Auto-generated constructor stub
  }

  public RtPxFormproperties(List<RtPxFormPropertie> formProperties) {
    super();
    this.formProperties = formProperties;
  }

  public RtPxFormproperties(String formProperties) {
    super();
    this.formProperties = JsonAliUtils.toBean(formProperties, RtPxFormPropertie.class);
  }

  public List<RtPxFormPropertie> getFormProperties() {
    return formProperties;
  }

  public void setFormProperties(List<RtPxFormPropertie> formProperties) {
    this.formProperties = formProperties;
  }
}
