package com.oseasy.initiate.modules.pw.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.sys.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 预约Entity.
 *
 * @author chenh
 * @version 2017-11-26
 */
public class PwAppointment extends DataEntity<PwAppointment> {

    private static final long serialVersionUID = 1L;
    private PwRoom pwRoom;        // 房间
    private String applyId;        // 申报编号
    private String year;        // 预约日期(年)
    private String month;        // 预约日期(月)
    private String day;        // 预约日期(日)
    private User user;        // 预约申请人
    private Date startDate;        // 预约开始时间
    private Date endDate;        // 预约结束时间

    private String status;        // 预约状态
    private String subject;        // 会议主题
    private String personNum;        // 会议人数
    private String opType;        // 预约类型
    private String attendUser;        // 参会名单
    private String color;           //颜色

    private List<String> multiStatus;  //状态（方便多状态查询）

    public PwAppointment() {
        super();
    }

    public PwAppointment(String id) {
        super(id);
    }

    public PwAppointment(PwRoom pwRoom, String applyId, String year, String month, String day, Date startDate, Date endDate, String applyName, String status, String subject,
                         String personNum, String opType) {
        super();
        this.pwRoom = pwRoom;
        this.applyId = applyId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.subject = subject;
        this.personNum = personNum;
        this.opType = opType;
    }

    public String getAttendUser() {
        return attendUser;
    }

    public void setAttendUser(String attendUser) {
        this.attendUser = attendUser;
    }

    public PwRoom getPwRoom() {
        return pwRoom;
    }

    public void setPwRoom(PwRoom pwRoom) {
        this.pwRoom = pwRoom;
    }

    @Length(min = 1, max = 64, message = "申报编号长度必须介于 1 和 64 之间")
    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    @Length(min = 0, max = 11, message = "预约日期(年)长度必须介于 0 和 11 之间")
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Length(min = 0, max = 11, message = "预约日期(月)长度必须介于 0 和 11 之间")
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Length(min = 0, max = 11, message = "预约日期(日)长度必须介于 0 和 11 之间")
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Length(min = 0, max = 1, message = "预约状态长度必须介于 0 和 1 之间")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Length(min = 1, max = 100, message = "会议主题长度必须介于 1 和 100 之间")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Length(min = 0, max = 11, message = "会议人数长度必须介于 0 和 11 之间")
    public String getPersonNum() {
        return personNum;
    }

    public void setPersonNum(String personNum) {
        if (StringUtils.isBlank(personNum)) {
            return;
        }
        this.personNum = personNum;
    }

    @Length(min = 0, max = 1, message = "预约类型长度必须介于 0 和 1 之间")
    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getMultiStatus() {
        return multiStatus;
    }

    public void setMultiStatus(List<String> multiStatus) {
        this.multiStatus = multiStatus;
    }
}