package com.oseasy.initiate.modules.actyw.tool.process.vo;

import com.oseasy.initiate.modules.gcontest.vo.GContestNodeVo;
import com.oseasy.initiate.modules.project.vo.ProjectNodeVo;
/**
 * 固定流程ID.
 * @author chenhao
 *
 */
public enum FlowYwId {
  FY_P(ProjectNodeVo.YW_ID, ProjectNodeVo.YW_FID, FlowProjectType.PMT_XM, "双创项目固定流程"),
  FY_G(GContestNodeVo.YW_ID, GContestNodeVo.YW_FID, FlowProjectType.PMT_DASAI, "大赛固定流程"),
  FY_APPOINTMENT("1000", "1000", FlowProjectType.PMT_APPOINTMENT, "预约固定流程"),
  FY_ENTER("2000", "2000", FlowProjectType.PMT_ENTER, "入驻固定流程");

  private String id;
  private String gid;
  private FlowProjectType fpType;
  private String remark;
  private FlowYwId(String id, String gid, FlowProjectType fpType, String remark) {
    this.id = id;
    this.gid = gid;
    this.fpType = fpType;
    this.remark = remark;
  }

  public String getGid() {
    return gid;
  }

  public String getId() {
    return id;
  }

  public String getRemark() {
    return remark;
  }

  public FlowProjectType getFpType() {
    return fpType;
  }

  public static FlowYwId getById(String id) {
    FlowYwId[] entitys = FlowYwId.values();
    for (FlowYwId entity : entitys) {
      if ((id).equals(entity.getId())) {
        return entity;
      }
    }
    return null;
  }

  public static FlowYwId getByGid(String gid) {
    FlowYwId[] entitys = FlowYwId.values();
    for (FlowYwId entity : entitys) {
      if ((gid).equals(entity.getGid())) {
        return entity;
      }
    }
    return null;
  }

  public static FlowYwId getByFpType(FlowProjectType fpType) {
    FlowYwId[] entitys = FlowYwId.values();
    for (FlowYwId entity : entitys) {
      if ((fpType != null) && (fpType).equals(entity.getFpType())) {
        return entity;
      }
    }
    return null;
  }
}
