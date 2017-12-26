package com.hch.platform.pcore.modules.pw.entity;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * 房间Entity.
 *
 * @author chenh
 * @version 2017-11-26
 */
public class PwRoom extends DataEntity<PwRoom> {

    private static final long serialVersionUID = 1L;
    @Transient
    private List<String> ids;        // 设施ID(用作级联查询)
    private String name;        // 房间名
    private String person;   // 负责人
    private String phone;   // 电话
    private String mobile;    // 手机
    private String alias;        // 别名
    private String type;        // 房间类型
    private String num;        // 容纳人数
    private String isAllowm;        // 是否允许多团队入驻
    private String isUsable;        // 可否预约
    private String isAssign;        // 可否分配
    private String color;   // 颜色
    private PwSpace pwSpace;        // 楼层编号
    private PwEnterRoom pwEnterRoom;        // 楼层编号
    @Transient
    private String path;       //从学校到楼层整个路径

    public PwRoom() {
        super();
    }

    public PwRoom(String id) {
        super(id);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Length(min = 1, max = 100, message = "房间名长度必须介于 1 和 100 之间")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    @Length(min = 0, max = 200, message = "电话长度必须介于 0 和 200 之间")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Length(min = 0, max = 1, message = "房间类型长度必须介于 0 和 1 之间")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 11, message = "容纳人数长度必须介于 0 和 11 之间")
    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Length(min = 0, max = 1, message = "可否预约长度必须介于 0 和 1 之间")
    public String getIsAssign() {
        return isAssign;
    }

    public void setIsAssign(String isAssign) {
        this.isAssign = isAssign;
    }

    @Length(min = 0, max = 1, message = "可否预约长度必须介于 0 和 1 之间")
    public String getIsUsable() {
        return isUsable;
    }

    public void setIsUsable(String isUsable) {
        this.isUsable = isUsable;
    }

    public String getIsAllowm() {
        return isAllowm;
    }

    public void setIsAllowm(String isAllowm) {
        this.isAllowm = isAllowm;
    }

    public Boolean getIsFull() {
        if ((this.pwEnterRoom != null) && (this.pwEnterRoom.getCnum() != null)) {
            if (((Global.NO).equals(this.isAllowm)) && (this.pwEnterRoom.getCnum() == 1)) {
                return true;
            }
        }
        return false;
    }

    public PwSpace getPwSpace() {
        return pwSpace;
    }

    public void setPwSpace(PwSpace pwSpace) {
        this.pwSpace = pwSpace;
    }

    public PwEnterRoom getPwEnterRoom() {
        return pwEnterRoom;
    }

    public void setPwEnterRoom(PwEnterRoom pwEnterRoom) {
        this.pwEnterRoom = pwEnterRoom;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}