package com.oseasy.initiate.modules.actyw.tool.project;

import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.proproject.entity.ProProject;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class ActProParamVo {

	private ActYw actYw;
	private ProProject proProject;

	public ProProject getProProject() {
		return proProject;
	}

	public void setProProject(ProProject proProject) {
		this.proProject = proProject;
	}

	public ActYw getActYw() {
		return actYw;
	}

	public void setActYw(ActYw actYw) {
		this.actYw = actYw;
	}
}
