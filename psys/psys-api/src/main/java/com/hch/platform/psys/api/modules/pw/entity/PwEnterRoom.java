package com.hch.platform.pcore.modules.pw.entity;

import java.util.List;

import org.springframework.data.annotation.Transient;

import com.google.common.collect.Lists;
import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.DataEntity;
import com.hch.platform.putil.common.utils.StringUtil;

/**
 * 入驻场地分配Entity.
 * @author chenh
 * @version 2017-11-26
 */
public class PwEnterRoom extends DataEntity<PwEnterRoom> {

	private static final long serialVersionUID = 1L;
	private PwEnter pwEnter;		// 入驻编号
	private PwRoom pwRoom;		// 房间编号
	@Transient
	private Integer cnum;		//数量

	public PwEnterRoom() {
    super();
  }

	public PwEnterRoom(PwRoom pwRoom) {
    super();
    this.pwRoom = pwRoom;
  }

  public PwEnterRoom(String rid, String eid) {
	  super();
	  this.pwEnter = new PwEnter(eid);
	  this.pwRoom = new PwRoom(rid);
	}

	public PwEnterRoom(PwEnter pwEnter) {
	  super();
	  this.pwEnter = pwEnter;
	}

	public PwEnterRoom(PwEnter pwEnter, PwRoom pwRoom) {
	  super();
	  this.pwEnter = pwEnter;
	  this.pwRoom = pwRoom;
	}

	public PwEnterRoom(String id){
		super(id);
	}

  public PwEnter getPwEnter() {
    return pwEnter;
  }

  public void setPwEnter(PwEnter pwEnter) {
    this.pwEnter = pwEnter;
  }

  public PwRoom getPwRoom() {
    return pwRoom;
  }

  public void setPwRoom(PwRoom pwRoom) {
    this.pwRoom = pwRoom;
  }

  public Integer getCnum() {
    return cnum;
  }

  public void setCnum(Integer cnum) {
    this.cnum = cnum;
  }

  public static List<String> getIds(List<PwEnterRoom> pwEnterRooms) {
    return getIds(pwEnterRooms, PwEnterRel.PER_ID);
  }

  public static List<String> getIds(List<PwEnterRoom> pwEnterRooms, String type) {
    List<String> ids = Lists.newArrayList();
    if((pwEnterRooms == null)){
      return ids;
    }

    for (PwEnterRoom pwer : pwEnterRooms) {
      if((PwEnterRel.PER_ID).equals(type)){
        ids.add(pwer.getId());
      }else if((PwEnterRel.PER_EID).equals(type) && (pwer.getPwEnter() != null) && StringUtil.isNotEmpty(pwer.getPwEnter().getId())){
        ids.add(pwer.getPwEnter().getId());
      }else if((PwEnterRel.PER_RID).equals(type) && (pwer.getPwRoom() != null) && StringUtil.isNotEmpty(pwer.getPwRoom().getId())){
        ids.add(pwer.getPwRoom().getId());
      }
    }
    return ids;
  }
}