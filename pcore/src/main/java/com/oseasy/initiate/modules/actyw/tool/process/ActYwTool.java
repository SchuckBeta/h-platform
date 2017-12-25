/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process
 * @Description [[_ActYwTool_]]文件
 * @date 2017年6月2日 下午4:35:49
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBounds;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBoundsX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtDocker;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRight;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRightX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtModel;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtOutgoing;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtProperties;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtPropertiesX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtStencil;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtStencilX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtStencilset;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtTarget;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeft;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeftX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;

import net.sf.json.JSONObject;



/**
 * 自定义流程工具类.
 *
 * @author chenhao
 * @date 2017年6月2日 下午4:35:49
 *
 */
public class ActYwTool {
  private static Logger logger = LoggerFactory.getLogger(ActYwTool.class);

  public static void main(String[] args) {
    getRtJson();

  }

  private static void getRtJson() {
    ActYwResult rt = initRtMin(new ActYwResult(), "ea90727d34c24285802473744a9893e8", "process119",
        "大赛流程119", "chenhao", "1.0");
    // RtChildShapes rtcs = initRtChildShapes(new RtChildShapes(),
    // "sid-E76260EF-151A-49A4-B235-825E54BAFF5B");
    // rtcs = initRtcsPropertiesX(rt, rtcs, "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", "学生（项目负责人）");
    // rtcs = initRtcsStencilX(rt, rtcs, StenType.ST_START_NONE_EVENT);
    // rtcs = initRtcsBoundsX(rt, rtcs, 0);
    // rtcs = initRtcsOutgoings(rt, rtcs, new String[]{"sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
    // "sid-E76260EF-151A-49A4-B235-825E54BAFF5B"});
    // rtcs = initRtcsTarget(rt, rtcs, "sid-E76260EF-151A-49A4-B235-825E54BAFF5B");
    // rtcs = initRtcsDockers(rt, rtcs, Lists.newArrayList(Arrays.asList(new Double[][]{{1.0, 1.0},
    // {2.0, 2.0}})));
    // addRtChildShapes(rt, rtcs);

    addRtChildShapes(rt,
        initRtChildShapesByName(rt, new RtChildShapes(), "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
            "sid-E76260EF-151A-49A4-B235-825E54BAFF5B", "学生（项目负责人）", StenType.ST_START_EVENT_NONE,
            0,
            new String[] { "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
                "sid-E76260EF-151A-49A4-B235-825E54BAFF5B" },
            "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
            Lists.newArrayList(Arrays.asList(new Double[][] { { 1.0, 1.0 }, { 2.0, 2.0 } }))));

    // RtChildShapes rtcs2 = initRtChildShapes(new RtChildShapes(),
    // "sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46");
    // rtcs2 = initRtcsPropertiesX(rtcs2, "sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46", "学院审核（教学秘书）");
    // rtcs2 = initRtcsStencilX(rtcs2, StenType.ST_USER_TASK);
    // rtcs2 = initRtcsBoundsX(rtcs2, rt.getChildShapes().size());
    // rtcs2 = initRtcsOutgoings(rtcs2, new String[]{"sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
    // "sid-E76260EF-151A-49A4-B235-825E54BAFF5B"});
    // rtcs2 = initRtcsTarget(rtcs2, "sid-E76260EF-151A-49A4-B235-825E54BAFF5B");
    // rtcs2 = initRtcsDockers(rtcs2, Lists.newArrayList(Arrays.asList(new Double[][]{{10.0, 10.0},
    // {20.0, 20.0}})));
    // addRtChildShapes(rt, rtcs2);

    addRtChildShapes(rt,
        initRtChildShapesByName(rt, new RtChildShapes(), "sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46",
            "sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46", "学院审核（教学秘书）", StenType.ST_TASK_USER,
            rt.getChildShapes().size(),
            new String[] { "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
                "sid-E76260EF-151A-49A4-B235-825E54BAFF5B" },
            "sid-E76260EF-151A-49A4-B235-825E54BAFF5B",
            Lists.newArrayList(Arrays.asList(new Double[][] { { 10.0, 10.0 }, { 20.0, 20.0 } }))));

    addRtChildShapes(rt,
        initRtChildShapesByName(rt, new RtChildShapes(), "sid-5A95E338-ACE9-4666-BF72-A6FD49E40E8F",
            "sid-5A95E338-ACE9-4666-BF72-A6FD49E40E8F", "学校审核（教务处）", StenType.ST_TASK_USER,
            rt.getChildShapes().size(),
            new String[] { "sid-B3D94D40-AB2D-49D5-81A4-C0B04AE49A19" }));

    addRtChildShapes(rt,
        initRtChildShapesByName(rt, new RtChildShapes(), "sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17",
            "sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17", "审核结束（项目评级）", StenType.ST_TASK_USER,
            rt.getChildShapes().size()));

    System.out.println(JSONObject.fromObject(rt).toString());
  }

  /**
   * 初始化结果集属性数据(自定义画布大小) .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param resourceId 资源标识
   * @param pcsId
   *          流程ID
   * @param name
   *          流程名称
   * @param pcsAuthor
   *          流程作者
   * @param pcsVersion
   *          流程版本
   * @param lrx
   *          右下X坐标
   * @param lry
   *          右下Y坐标
   * @param ulx
   *          左上X坐标
   * @param uly
   *          左上Y坐标
   * @param childShapess
   *          节点列表
   * @return ActYwResult
   */
  public static ActYwResult initRtMax(ActYwResult rt, String resourceId, String pcsId, String name,
      String pcsAuthor, String pcsVersion, Double lrx, Double lry, Double ulx, Double uly,
      List<RtChildShapes> childShapess) {
    if (resourceId == null) {
      return null;
    }
    rt.setResourceId(resourceId);

    rt = initRtProperties(rt, pcsId, name, pcsAuthor, pcsVersion);
    rt = initRtStencli(rt);
    rt = initRtBounds(rt, lrx, lry, ulx, uly);
    rt = initRtStencliset(rt);
    if ((childShapess != null) && !childShapess.isEmpty()) {
      rt = addRtChildShapes(rt, childShapess);
    }
    return rt;
  }

  /**
   * 初始化结果集属性数据(自定义画布大小-需要调用addRtChildShapes初始化流程节点) .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param resourceId 资源标识
   * @param pcsId
   *          流程ID
   * @param name
   *          流程名称
   * @param pcsAuthor
   *          流程作者
   * @param pcsVersion
   *          流程版本
   * @param lrx
   *          右下X坐标
   * @param lry
   *          右下Y坐标
   * @param ulx
   *          左上X坐标
   * @param uly
   *          左上Y坐标
   * @return ActYwResult
   */
  public static ActYwResult initRtMax(ActYwResult rt, String resourceId, String pcsId, String name,
      String pcsAuthor, String pcsVersion, Double lrx, Double lry, Double ulx, Double uly) {
    return initRtMax(rt, resourceId, pcsId, name, pcsAuthor, pcsVersion, lrx, lry, ulx, uly, null);
  }

  /**
   * 初始化结果集属性数据(默认画布大小) .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param resourceId 资源标识
   * @param pcsId
   *          流程ID
   * @param name
   *          流程名称
   * @param pcsAuthor
   *          流程作者
   * @param pcsVersion
   *          流程版本
   * @param childShapess
   *          节点列表
   * @return ActYwResult
   */
  public static ActYwResult initRtMin(ActYwResult rt, String resourceId, String pcsId, String name,
      String pcsAuthor, String pcsVersion, List<RtChildShapes> childShapess) {
    if (resourceId == null) {
      return null;
    }
    rt.setResourceId(resourceId);

    rt = initRtProperties(rt, pcsId, name, pcsAuthor, pcsVersion);
    rt = initRtStencli(rt);
    rt = initRtBounds(rt);
    rt = initRtStencliset(rt);

    if ((childShapess != null) && !childShapess.isEmpty()) {
      rt = addRtChildShapes(rt, childShapess);
    }
    return rt;
  }

  /**
   * 初始化结果集属性数据(默认画布大小-需要调用addRtChildShapes初始化流程节点) .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param pcsId
   *          流程ID
   * @param name
   *          流程名称
   * @param pcsAuthor
   *          流程作者
   * @param pcsVersion
   *          流程版本
   * @return ActYwResult
   */
  public static ActYwResult initRtMin(ActYwResult rt, String resourceId, String pcsId, String name,
      String pcsAuthor, String pcsVersion) {
    return initRtMin(rt, resourceId, pcsId, name, pcsAuthor, pcsVersion, null);
  }

  /**
   * 初始化结果集属性数据 .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param pcsId
   *          流程ID
   * @param name
   *          流程名称
   * @param documentation
   *          相关文档
   * @param pcsAuthor
   *          流程作者
   * @param pcsVersion
   *          流程版本
   * @param pcsNamespace
   *          流程命名空间
   * @param executionlisteners
   *          执行监听
   * @param eventlisteners
   *          事件监听
   * @param signaldefinitions
   *          xx定义
   * @param messagedefinitions
   *          消息定义
   * @return ActYwResult
   */
  public static ActYwResult initRtProperties(ActYwResult rt, String pcsId, String name,
      String pcsAuthor, String pcsVersion, String pcsNamespace, String documentation,
      String executionlisteners, String eventlisteners, String signaldefinitions,
      String messagedefinitions) {
    RtProperties rtp = rt.getProperties();
    if (rtp == null) {
      rtp = new RtProperties();
    }

    rtp.setProcess_id(pcsId);
    rtp.setName(name);
    rtp.setProcess_author(pcsAuthor);
    rtp.setProcess_version(pcsVersion);
    rtp.setProcess_namespace(pcsNamespace);

    if (pcsVersion == null) {
      rtp.setProcess_version(RtSvl.RtPropertiesVal.RT_PROCESS_VERSION);
    } else {
      rtp.setProcess_version(pcsVersion);
    }

    if (pcsNamespace == null) {
      rtp.setProcess_namespace(RtSvl.RtPropertiesVal.RT_PROCESS_NAMESPACE);
    } else {
      rtp.setProcess_namespace(pcsNamespace);
    }

    if (documentation == null) {
      rtp.setDocumentation("");
    } else {
      rtp.setDocumentation(documentation);
    }

    if (executionlisteners == null) {
      rtp.setExecutionlisteners("{\"executionListeners\":\"[]\"}");
    } else {
      rtp.setExecutionlisteners(executionlisteners);
    }

    if (eventlisteners == null) {
      rtp.setEventlisteners("{\"eventlisteners\":\"[]\"}");
    } else {
      rtp.setEventlisteners(eventlisteners);
    }

    if (signaldefinitions == null) {
      rtp.setSignaldefinitions("[]");
    } else {
      rtp.setSignaldefinitions(signaldefinitions);
    }

    if (messagedefinitions == null) {
      rtp.setMessagedefinitions("[]");
    } else {
      rtp.setMessagedefinitions(messagedefinitions);
    }

    rt.setProperties(rtp);
    return rt;
  }

  public static ActYwResult initRtProperties(ActYwResult rt, String pcsId, String name,
      String pcsAuthor, String pcsVersion) {
    return initRtProperties(rt, pcsId, name, pcsAuthor, pcsVersion, null, null, null, null, null,
        null);
  }

  /**
   * 初始化结果集Stencli数据 .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param id
   *          流程ID
   * @return ActYwResult
   */
  public static ActYwResult initRtStencli(ActYwResult rt, String id) {
    RtStencil rtstencil = rt.getStencil();
    if (rtstencil == null) {
      rtstencil = new RtStencil();
    }

    if (id == null) {
      rtstencil.setId(RtSvl.RtStencilVal.RT_STENCIL_ID);
    } else {
      rtstencil.setId(id);
    }

    rt.setStencil(rtstencil);
    return rt;
  }

  public static ActYwResult initRtStencli(ActYwResult rt) {
    return initRtStencli(rt, null);
  }

  /**
   * 初始化结果集Bounds画布大小 .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param lrx
   *          右下X坐标
   * @param lry
   *          右下Y坐标
   * @param ulx
   *          左上X坐标
   * @param uly
   *          左上Y坐标
   * @return ActYwResult
   */
  public static ActYwResult initRtBounds(ActYwResult rt, Double lrx, Double lry, Double ulx,
      Double uly) {
    RtBounds rtb = rt.getBounds();
    if (rtb == null) {
      rtb = new RtBounds();
    }

    RtLowerRight rtbLr = rtb.getLowerRight();
    if (rtbLr == null) {
      rtbLr = new RtLowerRight();
    }

    if (lrx == null) {
      rtbLr.setX(RtSvl.RtBoundsVal.RT_BOUNDS_LOWERRIGHT_X);
    } else {
      rtbLr.setX(lrx);
    }

    if (lry == null) {
      rtbLr.setY(RtSvl.RtBoundsVal.RT_BOUNDS_LOWERRIGHT_Y);
    } else {
      rtbLr.setY(lry);
    }

    RtUpperLeft rtbUl = rtb.getUpperLeft();
    if (rtbUl == null) {
      rtbUl = new RtUpperLeft();
    }

    if (ulx == null) {
      rtbUl.setX(RtSvl.RtBoundsVal.RT_BOUNDS_UPPERLEFT_X);
    } else {
      rtbUl.setX(ulx);
    }

    if (uly == null) {
      rtbUl.setY(RtSvl.RtBoundsVal.RT_BOUNDS_UPPERLEFT_Y);
    } else {
      rtbUl.setY(uly);
    }

    rtb.setLowerRight(rtbLr);
    rtb.setUpperLeft(rtbUl);
    rt.setBounds(rtb);
    return rt;
  }

  public static ActYwResult initRtBounds(ActYwResult rt) {
    return initRtBounds(rt, null, null, null, null);
  }

  /**
   * 初始化结果集StencliSet数据 .
   *
   * @author chenhao
   * @param rt 需初始化对象
   * @param url xml地址
   * @param namespace 命名空间
   * @return ActYwResult
   */
  public static ActYwResult initRtStencliset(ActYwResult rt, String url, String namespace) {
    RtStencilset rtstencilset = rt.getStencilset();
    if (rtstencilset == null) {
      rtstencilset = new RtStencilset();
    }

    if (url == null) {
      rtstencilset.setUrl(RtSvl.RtStencilsetVal.RT_URL);
    } else {
      rtstencilset.setUrl(url);
    }

    if (namespace == null) {
      rtstencilset.setNamespace(RtSvl.RtStencilsetVal.RT_NAMESPACE);
    } else {
      rtstencilset.setNamespace(namespace);
    }

    rt.setStencilset(rtstencilset);
    return rt;
  }

  public static ActYwResult initRtStencliset(ActYwResult rt) {
    return initRtStencliset(rt, null, null);
  }

  /**
   * 添加结果集多个节点数据 .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param rtcShapess
   *          节点集合
   * @return ActYwResult
   */
  public static ActYwResult addRtChildShapes(ActYwResult rt, List<RtChildShapes> rtcShapess) {
    List<RtChildShapes> rshapess = rt.getChildShapes();
    rshapess.addAll(rtcShapess);
    rt.setChildShapes(rshapess);
    return rt;
  }

  /**
   * 添加结果集节点数据 .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param rtcShapes
   *          节点
   * @return ActYwResult
   */
  public static ActYwResult addRtChildShapes(ActYwResult rt, RtChildShapes rtcShapes) {
    List<RtChildShapes> rtcShapess = rt.getChildShapes();
    if ((rtcShapess == null) || rtcShapess.isEmpty()) {
      rtcShapess = Lists.newArrayList();
    }
    rtcShapess.add(rtcShapes);
    rt.setChildShapes(rtcShapess);
    return rt;
  }

  /**
   * 初始化节点 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param resourceId
   *          节点资源Id
   * @param properties
   *          节点属性
   * @param stencil
   *          节点类型
   * @param bounds
   *          节点边界
   * @param outgoings
   *          关联资源
   * @param target
   *          目标资源
   * @param dockers
   *          目标资源坐标
   * @param childShapes
   *          子节点
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapes(RtChildShapes rtcs, String resourceId,
      RtPropertiesX properties, RtStencilX stencil, RtBoundsX bounds, List<RtOutgoing> outgoings,
      RtTarget target, List<?> dockers, List<?> childShapes) {
    if (rtcs == null) {
      return null;
    }

    if (resourceId == null) {
      return null;
    }
    rtcs.setResourceId(resourceId);

    if (properties != null) {
      rtcs.setProperties(properties);
    } else {
      rtcs.setProperties(new RtPropertiesX());
    }

    if (stencil != null) {
      rtcs.setStencil(stencil);
    } else {
      rtcs.setStencil(new RtStencilX());
    }

    if (bounds != null) {
      rtcs.setBounds(bounds);
    } else {
      rtcs.setBounds(new RtBoundsX());
    }

    if (outgoings != null) {
      rtcs.setOutgoing(outgoings);
    } else {
      rtcs.setOutgoing(new ArrayList<RtOutgoing>());
    }

    return rtcs;
  }

  public static RtChildShapes initRtChildShapes(RtChildShapes rtcs, String resourceId) {
    return initRtChildShapes(rtcs, resourceId, null, null, null, null, null, null, null);
  }

  public static RtChildShapes initRtChildShapes(RtChildShapes rtcs, String resourceId,
      RtPropertiesX properties, RtStencilX stencil, RtBoundsX bounds, List<RtOutgoing> outgoings) {
    return initRtChildShapes(rtcs, resourceId, properties, stencil, bounds, outgoings, null, null,
        null);
  }

  public static RtChildShapes initRtChildShapes(RtChildShapes rtcs, String resourceId,
      RtPropertiesX properties, RtStencilX stencil, RtBoundsX bounds, List<RtOutgoing> outgoings,
      RtTarget target, List<?> dockers) {
    return initRtChildShapes(rtcs, resourceId, properties, stencil, bounds, outgoings, target,
        dockers, null);
  }

  /**
   * 初始化节点属性(全属性=名称+id+表单+监听) .
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param rtcs
   *          需初始化节点对象
   * @param rtcSresourceId
   *          资源Id
   * @param prXoverrideid
   *          节点Id
   * @param prXname
   *          节点名称
   * @param prXdocumentation
   *          节点文档
   * @param prXexecutionlisteners
   *          节点监听事件
   * @param prXformkeydefinition
   *          节点关联表单
   * @param prXformproperties
   *          节点关联表单属性
   * @param prXinitiator
   *          节点发起人
   * @param stXstenType
   *          节点标识类型
   * @param boXsort
   *          节点排序位置
   * @param ouSresourceId
   *          节点关联Outgoings资源ID
   * @param taTresourceId
   *          节点关联Target资源ID
   * @param doSxys
   *          节点关联资源坐标
   * @param suSsubs
   *          节点关联子节点
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapes(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, String prXdocumentation, String prXexecutionlisteners,
      String prXformkeydefinition, String prXformproperties, String prXinitiator, StenType stXstenType,
      Integer boXsort, String[] ouSresourceId, String taTresourceId, List<Double[]> doSxys,
      List<RtChildShapes> suSsubs) {

    rtcs = initRtChildShapes(rtcs, rtcSresourceId);

    rtcs = initRtcsPropertiesX(rtcs, prXoverrideid, prXname, prXdocumentation, prXexecutionlisteners,
        prXformkeydefinition, prXformproperties, prXinitiator);

    rtcs = initRtcsStencilX(rtcs, stXstenType);

    if(suSsubs != null){
      rtcs = initRtcsSubs(rtcs, suSsubs);
    }

    if(doSxys == null){
      rtcs = initRtcsBoundsX(rt, rtcs, boXsort);
    }else{
      rtcs = initRtcsBoundsX(rtcs, doSxys.get(0)[0], doSxys.get(0)[1], doSxys.get(1)[0], doSxys.get(1)[1]);
    }

    rtcs = initRtcsOutgoings(rtcs, ouSresourceId);

    rtcs = initRtcsTarget(rtcs, taTresourceId);
    /**
     * 需要先执行 initRtcsBoundsX 方法获取BoundsX
     */
    rtcs = initRtcsDockers(rtcs);
//    rtcs = initRtcsDockers(rtcs, doSxys, false);

//    rtcs = initRtcsSubs(rtcs, suSsubs);

    return rtcs;
  }

  /**
   * 初始化节点属性(全属性=名称+id+表单+监听-需要调用addRtChildShapes初始化流程节点) .
   * @author chenhao
   * @param rt 需初始化对象
   * @param rtcs 需初始化节点对象
   * @param rtcSresourceId 资源Id
   * @param prXoverrideid 节点Id
   * @param prXname 节点名称
   * @param prXdocumentation 节点文档
   * @param prXexecutionlisteners 节点监听事件
   * @param prXformkeydefinition 节点关联表单
   * @param prXformproperties 节点关联表单属性
   * @param prXinitiator 节点发起人
   * @param stXstenType 节点标识类型
   * @param boXsort 节点排序位置
   * @param ouSresourceId 节点关联Outgoings资源ID
   * @param taTresourceId 节点关联Target资源ID
   * @param doSxys 节点关联资源坐标
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapes(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, String prXdocumentation, String prXexecutionlisteners,
      String prXformkeydefinition, String prXformproperties, String prXinitiator, StenType stXstenType,
      Integer boXsort, String[] ouSresourceId, String taTresourceId, List<Double[]> doSxys) {
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, prXdocumentation,
        prXexecutionlisteners, prXformkeydefinition, prXformproperties, prXinitiator, stXstenType,
        boXsort, ouSresourceId, taTresourceId, doSxys, null);
  }

  /**
   * 初始化节点属性(名称+id) .
   *
   * @author chenhao
   * @param rt
   *          结果对象
   * @param rtcs
   *          需初始化节点对象
   * @param rtcSresourceId
   *          资源Id
   * @param prXoverrideid
   *          节点Id
   * @param prXname
   *          节点名称
   * @param stXstenType
   *          节点标识类型
   * @param boXsort
   *          节点排序位置
   * @param ouSresourceId
   *          节点关联Outgoings资源ID
   * @param taTresourceId
   *          节点关联Target资源ID
   * @param doSxys
   *          节点关联资源坐标
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapesByName(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, StenType stXstenType, Integer boXsort,
      String[] ouSresourceId, String taTresourceId, List<Double[]> doSxys) {
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, null, null, null, null,
        null, stXstenType, boXsort, ouSresourceId, taTresourceId, doSxys, null);
  }

  public static RtChildShapes initRtChildShapesByName(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, StenType stXstenType, Integer boXsort,
      String[] ouSresourceId) {
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, null, null, null, null,
        null, stXstenType, boXsort, ouSresourceId, null, null, null);
  }

  public static RtChildShapes initRtChildShapesByName(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, StenType stXstenType, Integer boXsort) {
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, null, null, null, null,
        null, stXstenType, boXsort, null, null, null, null);
  }

  /**
   * 初始化节点属性(名称+id+表单) .
   *
   * @author chenhao
   * @param rt
   *          结果对象
   * @param rtcs
   *          需初始化节点对象
   * @param rtcSresourceId
   *          资源Id
   * @param prXoverrideid
   *          节点Id
   * @param prXname
   *          节点名称
   * @param prXformkeydefinition
   *          节点关联表单
   * @param prXformproperties
   *          节点关联表单属性
   * @param stXstenType
   *          节点标识类型
   * @param boXsort
   *          节点排序位置
   * @param ouSresourceId
   *          节点关联Outgoings资源ID
   * @param taTresourceId
   *          节点关联Target资源ID
   * @param doSxys
   *          节点关联资源坐标
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapesByForm(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, String prXformkeydefinition, String prXformproperties,
      StenType stXstenType, Integer boXsort, String[] ouSresourceId, String taTresourceId,
      List<Double[]> doSxys) {
    // , String prXdocumentation, String prXexecutionlisteners, String prXinitiator
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, null, null,
        prXformkeydefinition, prXformproperties, null, stXstenType, boXsort, ouSresourceId, taTresourceId,
        doSxys, null);
  }

  /**
   * 初始化节点属性(名称+id+监听) .
   *
   * @author chenhao
   * @param rt
   *          结果对象
   * @param rtcs
   *          需初始化节点对象
   * @param rtcSresourceId
   *          资源Id
   * @param prXoverrideid
   *          节点Id
   * @param prXname
   *          节点名称
   * @param prXexecutionlisteners
   *          节点监听事件
   * @param stXstenType
   *          节点标识类型
   * @param boXsort
   *          节点排序位置
   * @param ouSresourceId
   *          节点关联Outgoings资源ID
   * @param taTresourceId
   *          节点关联Target资源ID
   * @param doSxys
   *          节点关联资源坐标
   * @return RtChildShapes
   */
  public static RtChildShapes initRtChildShapesByListener(ActYwResult rt, RtChildShapes rtcs, String rtcSresourceId,
      String prXoverrideid, String prXname, String prXexecutionlisteners, StenType stXstenType,
      Integer boXsort, String[] ouSresourceId, String taTresourceId, List<Double[]> doSxys) {
    // , String prXdocumentation, String prXformkeydefinition, String prXformproperties, String
    // prXinitiator
    return initRtChildShapes(rt, rtcs, rtcSresourceId, prXoverrideid, prXname, null, prXexecutionlisteners,
        null, null, null, stXstenType, boXsort, ouSresourceId, taTresourceId, doSxys, null);
  }

  /**
   * 初始化节点属性 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param overrideid
   *          节点Id
   * @param name
   *          节点名称
   * @param documentation
   *          节点文档
   * @param executionlisteners
   *          节点监听事件
   * @param formproperties
   *          节点关联表单属性
   * @param initiator
   *          节点发起人
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsPropertiesX(RtChildShapes rtcs, String overrideid,
      String name, String documentation, String executionlisteners, String formkeydefinition,
      String formproperties, String initiator) {
    if (rtcs == null) {
      return null;
    }

    if (rtcs.getProperties() != null) {
      RtPropertiesX rtpx = rtcs.getProperties();

      if (StringUtil.isNotEmpty(overrideid)) {
        rtpx.setOverrideid(overrideid);
      } else {
        logger.warn("RtChildShapes.overrideid is undefind");
      }

      if (StringUtil.isNotEmpty(name)) {
        rtpx.setName(name);
      } else {
        logger.warn("RtChildShapes.name is undefind");
      }

      if (StringUtil.isNotEmpty(documentation)) {
        rtpx.setDocumentation(documentation);
      } else {
        rtpx.setDocumentation("");
      }

      if (StringUtil.isNotEmpty(executionlisteners)) {
        rtpx.setExecutionlisteners(executionlisteners);
      } else {
        rtpx.setExecutionlisteners("");
      }

      if (StringUtil.isNotEmpty(formkeydefinition)) {
        rtpx.setFormkeydefinition(formkeydefinition);
      } else {
        rtpx.setFormkeydefinition("");
      }

      if (StringUtil.isNotEmpty(formproperties)) {
        rtpx.setFormproperties(formproperties);
      } else {
        rtpx.setFormproperties("");
      }

      if (StringUtil.isNotEmpty(initiator)) {
        rtpx.setInitiator(initiator);
      } else {
        rtpx.setInitiator("");
      }
    } else {
      logger.warn("RtChildShapes.properties is undefind");
    }

    return rtcs;
  }

  public static RtChildShapes initRtcsPropertiesX(RtChildShapes rtcs, String overrideid,
      String name) {
    return initRtcsPropertiesX(rtcs, overrideid, name, null, null, null, null, null);
  }

  /**
   * 初始化节点属性 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param properties 节点属性
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsPropertiesX(RtChildShapes rtcs, RtPropertiesX properties) {
    if (properties == null) {
      return rtcs;
    }
    return initRtcsPropertiesX(rtcs, properties.getOverrideid(), properties.getName(),
        properties.getDocumentation(), properties.getExecutionlisteners(),
        properties.getFormkeydefinition(), properties.getFormproperties(),
        properties.getInitiator());
  }

  /**
   * 初始化节点属性 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param id
   *          节点类型标识
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsStencilX(RtChildShapes rtcs, String id) {
    if (rtcs.getStencil() != null) {
      RtStencilX rtsx = rtcs.getStencil();

      if (StringUtil.isNotEmpty(id)) {
        rtsx.setId(id);
      } else {
        logger.warn("RtChildShapes.stencil.id is undefind");
      }
    } else {
      logger.warn("RtChildShapes.stencil is undefind");
    }
    return rtcs;
  }

  public static RtChildShapes initRtcsStencilX(RtChildShapes rtcs, StenType stenType) {
    return initRtcsStencilX(rtcs, stenType.getKey());
  }

  /**
   * 初始化节点属性 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param stencil 节点类型标识
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsStencilX(RtChildShapes rtcs, RtStencilX stencil) {
    if (stencil == null) {
      return rtcs;
    }
    return initRtcsStencilX(rtcs, stencil.getId());
  }

  /**
   * 初始化节点Bounds画布大小 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param lrx
   *          右下X坐标
   * @param lry
   *          右下Y坐标
   * @param ulx
   *          左上X坐标
   * @param uly
   *          左上Y坐标
   * @return ActYwResult
   */
  public static RtChildShapes initRtcsBoundsX(RtChildShapes rtcs, Double lrx, Double lry,
      Double ulx, Double uly) {
    RtBoundsX rtbx = rtcs.getBounds();
    if (rtbx == null) {
      rtbx = new RtBoundsX();
    }

    RtLowerRightX rtbxLr = rtbx.getLowerRight();
    if (rtbxLr == null) {
      rtbxLr = new RtLowerRightX();
    }

    if (lrx == null) {
      rtbxLr.setX(RtSvl.RtBoundsVal.RT_BOUNDS_LOWERRIGHT_X);
    } else {
      rtbxLr.setX(lrx);
    }

    if (lry == null) {
      rtbxLr.setY(RtSvl.RtBoundsVal.RT_BOUNDS_LOWERRIGHT_Y);
    } else {
      rtbxLr.setY(lry);
    }

    RtUpperLeftX rtbxUl = rtbx.getUpperLeft();
    if (rtbxUl == null) {
      rtbxUl = new RtUpperLeftX();
    }

    if (ulx == null) {
      rtbxUl.setX(RtSvl.RtBoundsVal.RT_BOUNDS_UPPERLEFT_X);
    } else {
      rtbxUl.setX(ulx);
    }

    if (uly == null) {
      rtbxUl.setY(RtSvl.RtBoundsVal.RT_BOUNDS_UPPERLEFT_Y);
    } else {
      rtbxUl.setY(uly);
    }

    rtbx.setLowerRight(rtbxLr);
    rtbx.setUpperLeft(rtbxUl);
    rtcs.setBounds(rtbx);
    return rtcs;
  }

  /**
   * 根据排序生成定位.
   *
   * @author chenhao
   * @param rt
   *          需初始化对象
   * @param rtcs
   *          需初始化节点对象
   * @param sort
   *          节点排序位置
   * @return ActYwResult
   */
  public static RtChildShapes initRtcsBoundsX(ActYwResult rt, RtChildShapes rtcs, Integer sort) {
    RtBounds rtb = rt.getBounds();
    RtUpperLeft rtbul = rtb.getUpperLeft();
    RtLowerRight rtblr = rtb.getLowerRight();
    List<RtChildShapes> rtcscshapes = rtcs.getChildShapes();

    System.out.println(rtcs);

    Double centerx = (double) ((rtblr.getX() - rtbul.getX()) / 2);
    Double zeroX = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
    Double zeroY = (double) 50.0;
    Double rate = 3.0;
    Double area = 500.0;
    Double ulx, uly, lrx, lry;
    /**
     * 判断是否为连接节点.
     */
    StenType stype = StenType.getByKey(rtcs.getStencil().getId());
    if ((stype.getSubtype()).equals(StenEsubType.SES_FLOW)) {
      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT - (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * 4));

      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * 4));
    }else if ((stype.getSubtype()).equals(StenEsubType.SES_JG)) {
      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT - (RtSvl.RtBoundsVal.RT_COLS_HEIGHT + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2));

      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2));
    }else{
      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT);
      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT);
    }

//    if ((stype.getSubtype()).equals(StenEsubType.SES_FLOW)) {
//      ulx = (double) (zeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      uly = (double) (zeroY + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * (rate * sort - 2) / 2));
//
//      lrx = (double) (zeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      lry = (double) (uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * 2);
//    } else if ((stype.getSubtype()).equals(StenEsubType.SES_JG)) {
//      Integer subSize = rtcscshapes.size();
//      ulx = (double) (zeroX - area + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      uly = (double) (zeroY + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * rate * (sort - 1)) / 2);
//
//      lrx = (double) (zeroX + area  + RtSvl.RtBoundsVal.RT_COLS_WIDTH);
//      lry = (double) (uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * (subSize * (rate + 1) * sort) + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * rate);
//    } else {
//      ulx = (double) (zeroX);
//      uly = (double) (zeroY + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * rate * (sort - 1)) / 2);
//
//      lrx = (double) (ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH);
//      lry = (double) (uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT);
//    }
//    System.out.println(sort+"-"+rtcs.getProperties().getName()+"--> lrx="+lrx+" lry="+lry+" ulx="+ulx+" uly="+uly);

//    /**
//     * 判断是否为连接节点.
//     */
//    StenType stype = StenType.getByKey(rtcs.getStencil().getId());
//    if ((stype.getSubtype()).equals(StenEsubType.SES_FLOW)) {
//      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT - (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * 4));
//
//      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT * 4));
//    }else if ((stype.getSubtype()).equals(StenEsubType.SES_JG)) {
//      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT - (RtSvl.RtBoundsVal.RT_COLS_HEIGHT + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2));
//
//      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT + (RtSvl.RtBoundsVal.RT_COLS_HEIGHT + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2));
//    }else{
//      ulx = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      uly = (double) ((sort * 2 - 1) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT);
//      lrx = (double) (centerx + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//      lry = (double) ((sort * 2) * RtSvl.RtBoundsVal.RT_COLS_HEIGHT);
//    }
//    System.out.println(sort+"-"+rtcs.getProperties().getName()+"--> lrx="+lrx+" lry="+lry+" ulx="+ulx+" uly="+uly);
    return initRtcsBoundsX(rtcs, lrx, lry, ulx, uly);
  }

  /**
   * 初始化节点关联关系 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param resourceId
   *          节点关联资源ID
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsOutgoings(RtChildShapes rtcs, String... resourceId) {
    if (rtcs.getOutgoing() == null) {
      rtcs.setOutgoing(Lists.newArrayList());
    }

    List<RtOutgoing> rtogs = rtcs.getOutgoing();

    if ((resourceId != null) && (resourceId.length > 0)) {
      for (int i = 0; i < resourceId.length; i++) {
        rtogs.add(new RtOutgoing(resourceId[i]));
      }
    } else {
      logger.warn("RtChildShapes.outgoing.resourceId.. is undefind");
    }
    return rtcs;
  }

  /**
   * 初始化节点关联对象 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param resourceId
   *          节点关联资源ID
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsTarget(RtChildShapes rtcs, String resourceId) {
    if (StringUtil.isEmpty(resourceId)) {
      logger.warn("RtChildShapes.target.resourceId.. is undefind");
    } else {
      if (rtcs.getTarget() == null) {
        rtcs.setTarget(new RtTarget(resourceId));
      } else {
        rtcs.getTarget().setResourceId(resourceId);
      }
    }
    return rtcs;
  }

  /**
   * 初始化节点关联Dockers坐标对象 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param hasRtBounds
   *          是否已经初始化了RtBoundX对象
   * @param xys
   *          节点关联资源坐标
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsDockers(RtChildShapes rtcs, List<Double[]> xys, Boolean hasRtBounds) {
    if (hasRtBounds) {
      RtBoundsX rtbx = rtcs.getBounds();
      RtUpperLeftX rtbulX = rtbx.getUpperLeft();
      RtLowerRightX rtblrX = rtbx.getLowerRight();

      Double centerx = ((rtblrX.getX() + rtbulX.getX()) / 2);
      Double centery = ((rtblrX.getY() + rtbulX.getY()) / 2);

      Double[] preDocker = new Double[]{centerx, centery - RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2};
      Double[] nextDocker = new Double[]{centerx, centery + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2};
      xys = Lists.newArrayList(Arrays.asList(new Double[][] { preDocker, nextDocker }));

//      System.out.println("--------------------------------------------------------------------------------------------");
//      System.out.println("(centerx, centery) = (" + centerx + ", " + centery + ")");
//      System.out.println("xys = (" + preDocker[0] + "," + preDocker[1] + ") - (" + nextDocker[0] + "," + nextDocker[1] + ")");
//      System.out.println("--------------------------------------------------------------------------------------------");
    }

    if (xys == null || xys.isEmpty()) {
      return rtcs;
    }

    if (rtcs.getDockers() == null) {
      rtcs.setDockers(Lists.newArrayList());
    }

    List<RtDocker> rtds = rtcs.getDockers();

    for (Double[] xy : xys) {
      rtds.add(new RtDocker(xy[0], xy[1]));
    }

    return rtcs;
  }

  public static RtChildShapes initRtcsDockers(RtChildShapes rtcs) {
    return initRtcsDockers(rtcs, null, true);
  }

  /**
   * 初始化节点关联子节点对象 .
   *
   * @author chenhao
   * @param rtcs
   *          需初始化节点对象
   * @param subs
   *          节点关联子节点
   * @return RtChildShapes
   */
  public static RtChildShapes initRtcsSubs(RtChildShapes rtcs, List<RtChildShapes> subs) {
    rtcs.setChildShapes(subs);
    return rtcs;
  }

  /**
   *
   * 生成modelData数据.
   * @author chenhao
   * @param category
   *          类目
   * @param name
   *          流程名称
   * @param key
   *          流程标识
   * @param description
   *          流程描述
   * @param jsonXml
   *          流程json
   * @param repositoryService 流程服务
   * @return Model
   */
  public static Model genModelData(String category, String name, String key, String description, String jsonXml, RepositoryService repositoryService) {
    return genModelData(new RtModel(name, key, description, category, jsonXml, null), repositoryService);
  }

  /**
   * 生成modelData数据.
   * @author chenhao
   * @param rtModel 模型
   * @return  Model
   */
  public static Model genModelData(RtModel rtModel, RepositoryService repositoryService) {
    ObjectMapper objectMapper = new ObjectMapper();
//    ObjectNode editorNode = objectMapper.createObjectNode();
//    editorNode.put(RtSvl.RtModelVal.ON_ID, "canvas");
//    editorNode.put(RtSvl.RtModelVal.ON_RESOURCE_ID, "canvas");
//    ObjectNode properties = objectMapper.createObjectNode();
//    properties.put(RtSvl.RtModelVal.ON_PROCESS_AUTHOR, "initiate");
//    editorNode.put(RtSvl.RtModelVal.ON_PROPERTIES, properties);
//    ObjectNode stencilset = objectMapper.createObjectNode();
//    stencilset.put(RtSvl.RtModelVal.ON_NAMESPACE, RtSvl.RtStencilsetVal.RT_NAMESPACE);
//    editorNode.put(RtSvl.RtModelVal.ON_STENCILSET, stencilset);

    org.activiti.engine.repository.Model modelData = repositoryService.newModel();
    rtModel.setDescription(StringUtil.defaultString(rtModel.getDescription()));
    modelData.setKey(StringUtil.defaultString(rtModel.getKey()));
    modelData.setName(rtModel.getName());
    modelData.setCategory(rtModel.getCategory());
    modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count() + 1)));

    ObjectNode modelObjectNode = objectMapper.createObjectNode();
    modelObjectNode.put(RtSvl.RtModelVal.M_NAME, rtModel.getName());
    modelObjectNode.put(RtSvl.RtModelVal.M_REVISION, modelData.getVersion());
    modelObjectNode.put(RtSvl.RtModelVal.M_DESCRIPTION, rtModel.getDescription());
    modelData.setMetaInfo(modelObjectNode.toString());

    return modelData;
  }
}
