/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl
 * @Description [[_ActYwCroot_]]文件
 * @date 2017年6月18日 上午11:20:02
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwResult;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommand;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwRchildShapes;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;

/**
 * 业务流程根节点操作类-起始节点.
 * @author chenhao
 * @date 2017年6月18日 上午11:20:02
 *
 */
public class ActYwCroot implements ActYwCommand<ActYwResult>{

  @Override
  public ActYwRchildShapes execute(ActYwResult tpl) {
    if (tpl == null) {
      return null;
    }

    List<RtChildShapes> rtcShapess = Lists.newArrayList();

//    rtcShapess.add(ActYwTool.initRtChildShapesByName(tpl, new RtChildShapes(), "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//            "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", "学生（项目负责人）", "", new RtPropertiesX(), StenType.ST_START_EVENT_NONE,
//            0,
//            new String[] { "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//                "sid-E76260EF-151A-49A4-B235-825E54BAFF5B" },
//            "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", null));
//
//    rtcShapess.add(ActYwTool.initRtChildShapesByName(tpl, new RtChildShapes(), "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//        "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", "学生（项目负责人）", null, new RtPropertiesX(), StenType.ST_FLOW_SEQUENCE,
//        0,
//        new String[] { "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//    "sid-E76260EF-151A-49A4-B235-825E54BAFF5B" },
//        "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//        Lists.newArrayList(Arrays.asList(new Double[][] { { 1.0, 1.0 }, { 2.0, 2.0 } }))));
//
//    rtcShapess.add(ActYwTool.initRtChildShapesByName(tpl, new RtChildShapes(), "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//        "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", "学生（项目负责人）", null, new RtPropertiesX(), StenType.ST_START_EVENT_NONE,
//        0,
//        new String[] { "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//    "sid-E76260EF-151A-49A4-B235-825E54BAFF5B" },
//        "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
//        Lists.newArrayList(Arrays.asList(new Double[][] { { 1.0, 1.0 }, { 2.0, 2.0 } }))));

    return new ActYwRchildShapes(rtcShapess);
  }
}
