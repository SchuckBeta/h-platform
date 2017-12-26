package com.hch.platform.putil.common.utils.image.impl;

import com.hch.platform.putil.common.config.Rstatus;
import com.hch.platform.putil.common.config.RstatusGroup;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.putil.common.utils.image.IWater;

/**
 * Created by Administrator on 2017/10/26 0026.
 */
public class WaterText extends Water<String> {
    private String resource;

    public WaterText() {
      super();
      this.isShow = false;
    }

    public WaterText(String resource) {
      super();
      this.isShow = false;
      this.resource = resource;
    }

    @Override
    public RstatusGroup validate() {
        RstatusGroup rsgroup = super.validate();
        if(StringUtil.isEmpty(this.resource)){
            rsgroup.getFails().add(new Rstatus(false, "资源不存在！"));
        }else{
            rsgroup.getSuccesss().add(new Rstatus(true, "资源合法！"));
        }
        return rsgroup;
    }

    @Override
    public IWater<String> getWater() {
        return this;
    }

    @Override
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
