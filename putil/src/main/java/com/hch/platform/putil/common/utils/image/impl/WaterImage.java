package com.hch.platform.putil.common.utils.image.impl;

import java.io.File;

import com.hch.platform.putil.common.config.Rstatus;
import com.hch.platform.putil.common.config.RstatusGroup;
import com.hch.platform.putil.common.utils.image.IWater;

/**
 * Created by Administrator on 2017/10/26 0026.
 */
public class WaterImage extends Water<File>{
    public File resource;

    public WaterImage() {
      super();
      this.isShow = false;
    }

    public WaterImage(File resource) {
      super();
      this.isShow = false;
      this.resource = resource;
    }

    @Override
    public RstatusGroup validate() {
        RstatusGroup rsgroup = super.validate();
        if((resource == null)){
            rsgroup.getFails().add(new Rstatus(false, "资源文件不存在！"));
        }else{
            rsgroup.getSuccesss().add(new Rstatus(true, "资源文件合法！"));
        }
        return rsgroup;
    }

    @Override
    public IWater<File> getWater() {
        return this;
    }

    @Override
    public File getResource() {
        return resource;
    }

    public void setResource(File resource) {
        this.resource = resource;
    }
}
