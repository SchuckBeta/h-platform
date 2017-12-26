package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.io.Serializable;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;

import net.sf.json.JSONObject;

public class GnodePvo implements Serializable {
    private static final long serialVersionUID = 1L;
    private GnodePvo parent;
    private String id;
    private String name;
    private Boolean hasGroup;
    private String iconUrl;
    private Boolean hasGateway;
    private String formId;
    private String groupId;
    private String flowGroup;
    private String preFunId;
    private String nextFunId;
    private String nodeId;
    private String remarks;
    private JSONObject datas;

    private String posLux; // 左上坐标X
    private String posLuy; // 左上坐标Y
    private Float width; // 宽
    private Float height; // 高

    private String posAlux; // 左上坐标X
    private String posAluy; // 左上坐标Y
    private Float widtha; // 宽
    private Float heighta; // 高

    public GnodePvo getParent() {
        return parent;
    }

    public void setParent(GnodePvo parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getHasGateway() {
        return hasGateway;
    }

    public void setHasGateway(Boolean hasGateway) {
        this.hasGateway = hasGateway;
    }

    public String getFlowGroup() {
        return flowGroup;
    }

    public void setFlowGroup(String flowGroup) {
        this.flowGroup = flowGroup;
    }

    public String getPreFunId() {
        return preFunId;
    }

    public void setPreFunId(String preFunId) {
        this.preFunId = preFunId;
    }

    public String getNextFunId() {
        return nextFunId;
    }

    public void setNextFunId(String nextFunId) {
        this.nextFunId = nextFunId;
    }

    public Float getWidth() {
      return width;
    }

    public void setWidth(Float width) {
      this.width = width;
    }

    public Float getHeight() {
      return height;
    }

    public void setHeight(Float height) {
      this.height = height;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public static ActYwGnode convert(GnodePvo pvo) {
        return convert(pvo, new ActYwGnode(), true);
    }

    public String getPosLux() {
        return posLux;
    }

    public void setPosLux(String posLux) {
        this.posLux = posLux;
    }

    public String getPosLuy() {
        return posLuy;
    }

    public void setPosLuy(String posLuy) {
        this.posLuy = posLuy;
    }

    public String getPosAlux() {
      return posAlux;
    }

    public void setPosAlux(String posAlux) {
      this.posAlux = posAlux;
    }

    public String getPosAluy() {
      return posAluy;
    }

    public void setPosAluy(String posAluy) {
      this.posAluy = posAluy;
    }

    public Float getWidtha() {
      return widtha;
    }

    public void setWidtha(Float widtha) {
      this.widtha = widtha;
    }

    public Float getHeighta() {
      return heighta;
    }

    public void setHeighta(Float heighta) {
      this.heighta = heighta;
    }

    public JSONObject getDatas() {
        return datas;
    }

    public void setDatas(JSONObject datas) {
        this.datas = datas;
    }

    public Boolean getHasGroup() {
        return hasGroup;
    }

    public void setHasGroup(Boolean hasGroup) {
        this.hasGroup = hasGroup;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public static ActYwGnode convert(GnodePvo pvo, ActYwGnode actYwGnode, Boolean isSave) {
        if(actYwGnode == null){
            actYwGnode = new ActYwGnode();
        }

        if(isSave){
            actYwGnode.setId(pvo.getId());
            actYwGnode.setHasGateway(pvo.getHasGateway());
            actYwGnode.setParent(new ActYwGnode(pvo.getParent().getId()));
            actYwGnode.setNodeId(pvo.getNodeId());
            actYwGnode.setPreFunId(pvo.getPreFunId());
            actYwGnode.setNextFunId(pvo.getNextFunId());
        }

        actYwGnode.setGroupId(pvo.getGroupId());
        ActYwGroup group = new ActYwGroup(pvo.getGroupId());
        group.setDatas((pvo.getDatas() != null)?pvo.getDatas().toString():"{}");
        actYwGnode.setGroup(group);
        actYwGnode.setName(pvo.getName());
        actYwGnode.setHasGroup(pvo.getHasGroup());
        actYwGnode.setIconUrl(pvo.getIconUrl());
        actYwGnode.setFormId(pvo.getFormId());
        actYwGnode.setFlowGroup(pvo.getFlowGroup());
        actYwGnode.setRemarks(pvo.getRemarks());

        if((pvo.getWidth() != null)){
            actYwGnode.setWidth(pvo.getWidth());
        }

        if((pvo.getHeight() != null)){
            actYwGnode.setHeight(pvo.getHeight());
        }

        if(StringUtil.isNotEmpty(pvo.getPosLux())){
            actYwGnode.setPosLux(pvo.getPosLux());
        }else{
          actYwGnode.setPosLux(ActYwGnode.DEFAULT_PLUX);
        }

        if(StringUtil.isNotEmpty(pvo.getPosLuy())){
            actYwGnode.setPosLuy(pvo.getPosLuy());
        }else{
          actYwGnode.setPosLuy(ActYwGnode.DEFAULT_PLUY);
        }

        if((pvo.getWidtha() != null)){
            actYwGnode.setWidtha(pvo.getWidtha());
        }

        if((pvo.getHeighta() != null)){
            actYwGnode.setHeighta(pvo.getHeighta());
        }

        if(StringUtil.isNotEmpty(pvo.getPosAlux())){
            actYwGnode.setPosAlux(pvo.getPosAlux());
        }else{
          actYwGnode.setPosAlux(ActYwGnode.DEFAULT_PLUX);
        }

        if(StringUtil.isNotEmpty(pvo.getPosAluy())){
            actYwGnode.setPosAluy(pvo.getPosAluy());
        }else{
          actYwGnode.setPosAluy(ActYwGnode.DEFAULT_PLUY);
        }
        return actYwGnode;
    }
}
