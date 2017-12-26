package com.oseasy.initiate.modules.pw.service;

import java.util.List;

import com.oseasy.initiate.modules.pw.entity.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.pw.dao.PwDesignerCanvasDao;

/**
 * 画布表Service.
 * @author zy
 * @version 2017-12-18
 */
@Service
@Transactional(readOnly = false)
public class PwDesignerCanvasService extends CrudService<PwDesignerCanvasDao, PwDesignerCanvas> {
	@Autowired
	PwDesignerRoomService pwDesignerRoomService;

	@Autowired
	PwDesignerRoomAttrService pwDesignerRoomAttrService;
	@Autowired
	PwRoomService pwRoomService;
	@Autowired
	PwSpaceService pwSpaceService;

	public PwDesignerCanvas get(String id) {
		return super.get(id);
	}

	public List<PwDesignerCanvas> findList(PwDesignerCanvas pwDesignerCanvas) {
		return super.findList(pwDesignerCanvas);
	}

	public Page<PwDesignerCanvas> findPage(Page<PwDesignerCanvas> page, PwDesignerCanvas pwDesignerCanvas) {
		return super.findPage(page, pwDesignerCanvas);
	}

	@Transactional(readOnly = false)
	public void save(PwDesignerCanvas pwDesignerCanvas) {
		super.save(pwDesignerCanvas);
	}

	@Transactional(readOnly = false)
	public void delete(PwDesignerCanvas pwDesignerCanvas) {
		super.delete(pwDesignerCanvas);
	}

	@Transactional(readOnly = false)
	public void deteleAll(String floorId) {
		//找到画布配置
		PwDesignerCanvas pwDesignerCanvas=getPwDesignerCanvasByFloorId(floorId);
		if(pwDesignerCanvas!=null){
			//找到画布房间配置
			List<PwDesignerRoom> pwDesignerRooms=pwDesignerRoomService.findListByCid(pwDesignerCanvas.getId());
			if(pwDesignerRooms!=null && pwDesignerRooms.size()>0){
				for(PwDesignerRoom pwDesignerRoom:pwDesignerRooms){
					//删除画布房间内物品
					if(pwDesignerRoom.getId()!=null){
						pwDesignerRoomAttrService.deleteAllByRoomId(pwDesignerRoom.getId());
					}
				}
			}
			//删除画布房间
			pwDesignerRoomService.deleteAllByCid(pwDesignerCanvas.getId());
			//删除画布
			this.deleteByFloorId(floorId);
		}

	}

	private PwDesignerCanvas getPwDesignerCanvasByFloorId(String floorId) {
		return dao.getPwDesignerCanvasByFloorId(floorId);
	}

	@Transactional(readOnly = false)
	private void deleteByFloorId(String floodId) {
		dao.deleteByFloorId(floodId);
	}

	@Transactional(readOnly = false)
	public boolean saveAll(JSONObject jsonData) {
		try {

			String floorId=(String)jsonData.get("floorId");//楼层id
			if(floorId!=null){
				deteleAll(floorId);
			}else{
				logger.error("楼层Id不存在");
				return false;
			}
			String paperSize=(String)jsonData.get("paperSize");//画布大小
			String picUrl=(String)jsonData.get("picUrl");//画布大小
			PwDesignerCanvas pwDesignerCanvas=new PwDesignerCanvas();
			pwDesignerCanvas.setFloorId(floorId);
			pwDesignerCanvas.setPapersize(paperSize);
			pwDesignerCanvas.setPicUrl(picUrl);
			this.save(pwDesignerCanvas);

			JSONObject data=(JSONObject)jsonData.get("data");//房间data
			JSONArray roomList=(JSONArray)data.get("list");//房间List
			for(int i=0;i<roomList.size();i++){
				JSONObject room =roomList.getJSONObject(i);
				PwDesignerRoom pwDesignerRoom=new PwDesignerRoom();
				String transform=(String)room.get("transform");//画布调整尺寸
				String shapeType=(String)room.get("shapeType");//形状类型
				String type=(String)room.get("type");//房间类型
				String name=(String)room.get("name");//房间名称
				String roomId=(String)room.get("roomId");//房间id
				String sort=(String)room.get("sort");//房间排序

				JSONObject attr=(JSONObject)room.get("attr");//房间样式
				String x=(String)attr.get("x");
				String y=(String)attr.get("y");
				String width=(String)attr.get("width");
				String height=(String)attr.get("height");
				String style=(String)attr.get("style");
				String fill=(String)attr.get("fill");
				String stroke=(String)attr.get("stroke");
				String href=(String)attr.get("href");//房间id
				pwDesignerRoom.setTransform(transform);
				pwDesignerRoom.setShapetype(shapeType);
				pwDesignerRoom.setType(type);
				pwDesignerRoom.setName(name);
				pwDesignerRoom.setRoomId(roomId);
				pwDesignerRoom.setSort(sort);
				pwDesignerRoom.setHref(href);
				pwDesignerRoom.setX(x);
				pwDesignerRoom.setY(y);
				pwDesignerRoom.setWidth(width);
				pwDesignerRoom.setHeight(height);
				pwDesignerRoom.setStyle(style);
				pwDesignerRoom.setFill(fill);
				pwDesignerRoom.setStroke(stroke);
				pwDesignerRoom.setCid(pwDesignerCanvas.getId());
				pwDesignerRoomService.save(pwDesignerRoom);
				//画布房间里物品
				JSONArray children=(JSONArray)room.get("children");
				if(children!=null &&children.size()>0){
					for(int j=0;j<children.size();j++) {
						JSONObject roomAttr =children.getJSONObject(j);
						String cshapeType=(String)roomAttr.get("shapeType");
						String ctype=(String)roomAttr.get("type");
						String cname=(String)roomAttr.get("name");
						String ctext=(String)roomAttr.get("text");
						String cIndex=(String)roomAttr.get("sort");
						JSONObject cattr=(JSONObject)roomAttr.get("attr");//房间样式
						String cattrx=(String)cattr.get("x");
						String cattry=(String)cattr.get("y");
						String cattrfill=(String)cattr.get("fill");
						String cattrstyle=(String)cattr.get("style");
						String cattrWidth=(String)cattr.get("width");
						String cattrHeight=(String)cattr.get("height");
						String cattrHref=(String)cattr.get("href");

						PwDesignerRoomAttr pwDesignerRoomAttr=new PwDesignerRoomAttr();
						pwDesignerRoomAttr.setShapetype(cshapeType);
						pwDesignerRoomAttr.setType(ctype);
						pwDesignerRoomAttr.setName(cname);
						pwDesignerRoomAttr.setText(ctext);
						pwDesignerRoomAttr.setX(cattrx);
						pwDesignerRoomAttr.setY(cattry);
						pwDesignerRoomAttr.setWidth(cattrWidth);
						pwDesignerRoomAttr.setHeight(cattrHeight);
						pwDesignerRoomAttr.setFill(cattrfill);
						pwDesignerRoomAttr.setStyle(cattrstyle);
						pwDesignerRoomAttr.setHref(cattrHref);
						pwDesignerRoomAttr.setShowIndex(cIndex);
						pwDesignerRoomAttr.setRid(pwDesignerRoom.getId());
						pwDesignerRoomAttrService.save(pwDesignerRoomAttr);
					}
				}
			}
		}catch (Exception e){
			logger.error("保存楼层设计失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public JSONObject getAll(String floorId) {
		JSONObject jsonData=new JSONObject();
		//找到画布配置
		try {
			PwSpace pwSpace=pwSpaceService.get(floorId);
			jsonData.put("floorName",pwSpace.getName());

			PwRoom pwRoom=new PwRoom();
			pwRoom.setPwSpace(new PwSpace(floorId));
			List<PwRoom> pwRooms=pwRoomService.findList(pwRoom);
			if(pwRooms!=null && pwRooms.size()>0){
				JSONArray roomInList = new JSONArray();
				for (int rm = 0; rm < pwRooms.size(); rm++) {
					PwRoom pwInRoom = pwRooms.get(rm);
					JSONObject pwInRoomobj = new JSONObject();
					pwInRoomobj.put("roomName",pwInRoom.getName());
					pwInRoomobj.put("roomId",pwInRoom.getId());
					roomInList.add(pwInRoomobj);
				}
				jsonData.put("roomInList",roomInList);
			}
			PwDesignerCanvas pwDesignerCanvas = getPwDesignerCanvasByFloorId(floorId);
			if (pwDesignerCanvas != null) {
				jsonData.put("paperSize", pwDesignerCanvas.getPapersize());
				jsonData.put("picUrl", pwDesignerCanvas.getPicUrl());
				JSONObject data = new JSONObject();
				//找到画布房间配置
				List<PwDesignerRoom> pwDesignerRooms = pwDesignerRoomService.findListByCid(pwDesignerCanvas.getId());
				if (pwDesignerRooms!=null && pwDesignerRooms.size() > 0) {
					JSONArray roomList = new JSONArray();
					for (int i = 0; i < pwDesignerRooms.size(); i++) {
						PwDesignerRoom pwDesignerRoom = pwDesignerRooms.get(i);
						JSONObject room = new JSONObject();
						room.put("transform", pwDesignerRoom.getTransform());
						room.put("shapeType", pwDesignerRoom.getShapetype());
						room.put("type", pwDesignerRoom.getType());
						room.put("name", pwDesignerRoom.getName());
						room.put("roomId", pwDesignerRoom.getRoomId());
						room.put("sort", pwDesignerRoom.getSort());
						JSONObject attr = new JSONObject();
						attr.put("x", pwDesignerRoom.getX());
						attr.put("y", pwDesignerRoom.getY());
						attr.put("width", pwDesignerRoom.getWidth());
						attr.put("height", pwDesignerRoom.getHeight());
						attr.put("style", pwDesignerRoom.getStyle());
						attr.put("fill", pwDesignerRoom.getFill());
						attr.put("stroke", pwDesignerRoom.getStroke());
						attr.put("href", pwDesignerRoom.getHref());
						room.put("attr", attr);
						//画布房间里物品
						List<PwDesignerRoomAttr> pwDesignerRoomAttrs = pwDesignerRoomAttrService.findListByRid(pwDesignerRoom.getId());
						if (pwDesignerRooms.size() > 0) {
							JSONArray children = new JSONArray();
							for (int j = 0; j < pwDesignerRoomAttrs.size(); j++) {
								PwDesignerRoomAttr pwDesignerRoomAttr = pwDesignerRoomAttrs.get(j);
								JSONObject roomAttr = new JSONObject();
								roomAttr.put("text", pwDesignerRoomAttr.getText());
								roomAttr.put("shapeType", pwDesignerRoomAttr.getShapetype());
								roomAttr.put("type", pwDesignerRoomAttr.getType());
								roomAttr.put("name", pwDesignerRoomAttr.getName());
								roomAttr.put("sort", pwDesignerRoomAttr.getShowIndex());
								JSONObject cattr = new JSONObject();
								cattr.put("x", pwDesignerRoomAttr.getX());
								cattr.put("y", pwDesignerRoomAttr.getY());
								cattr.put("width", pwDesignerRoomAttr.getWidth());
								cattr.put("height", pwDesignerRoomAttr.getHeight());
								cattr.put("fill", pwDesignerRoomAttr.getFill());
								cattr.put("style", pwDesignerRoomAttr.getStyle());
								cattr.put("href", pwDesignerRoomAttr.getHref());
								roomAttr.put("attr", cattr);
								children.add(roomAttr);
							}
							room.put("children", children);
						}
						roomList.add(room);
					}
					data.put("list", roomList);
				}
				jsonData.put("data", data);
			}
			//获得成功
			jsonData.put("ret",1);
		}catch (Exception e){
			logger.error("获取楼层失败");
			e.printStackTrace();
			jsonData.put("ret", 0);
		}
		return  jsonData;
	}
}