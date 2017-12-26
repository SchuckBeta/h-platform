package com.oseasy.initiate.modules.pw.service;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwApply;
import com.oseasy.initiate.modules.actyw.exception.ApplyException;
import com.oseasy.initiate.modules.actyw.service.ActYwApplyService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowYwId;
import com.oseasy.initiate.modules.actyw.vo.ActYwApplyVo;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.oa.vo.OaNotifySendType;
import com.oseasy.initiate.modules.oa.vo.OaNotifyTypeStatus;
import com.oseasy.initiate.modules.pw.dao.PwAppointmentDao;
import com.oseasy.initiate.modules.pw.entity.PwAppointment;
import com.oseasy.initiate.modules.pw.entity.PwAppointmentRule;
import com.oseasy.initiate.modules.pw.entity.PwRoom;
import com.oseasy.initiate.modules.pw.entity.PwSpace;
import com.oseasy.initiate.modules.pw.utils.CommonUtils;
import com.oseasy.initiate.modules.pw.vo.*;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.enums.EuserType;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约Service.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwAppointmentService extends CrudService<PwAppointmentDao, PwAppointment> {


    @Autowired
    private ActYwApplyService actYwApplyService;

    @Autowired
    private PwRoomService pwRoomService;

    @Autowired
    private PwAppointmentDao pwAppointmentDao;

    @Autowired
    private PwAppointmentRuleService pwAppointmentRuleService;

    @Autowired
    private OaNotifyService oaNotifyService;


    public PwAppointment get(String id) {
        return super.get(id);
    }

    public List<PwAppointment> findListByCalendarParam(PwAppCalendarParam pwAppcParam) {
        return dao.findListByCalendarParam(pwAppcParam);
    }

    public List<PwAppointment> findList(PwAppointment pwAppointment) {
        return super.findList(pwAppointment);
    }

    public Page<PwAppointment> findPage(Page<PwAppointment> page, PwAppointment pwAppointment) {
        return super.findPage(page, pwAppointment);
    }

    /**
     * 添加预约
     *
     * @param pwAppointment
     * @return
     */
    @Transactional(readOnly = false)
    public String add(PwAppointment pwAppointment) {
        User user = UserUtils.getUser();
        validate(pwAppointment, user);
        pwAppointment.setUser(user);
        pwAppointment.setId(IdGen.uuid());
        pwAppointment.setIsNewRecord(true);
        if (!isAdmin(user)) {
            pwAppointment.setOpType("0");
            pwAppointment.setStatus(PwAppointmentStatus.WAIT_AUDIT.getValue());//待审核
            super.save(pwAppointment);
            this.saveApply(pwAppointment);//保存预约信息到流程
        } else {
            pwAppointment.setOpType("1");
            pwAppointment.setStatus(PwAppointmentStatus.LOCKED.getValue());
            pwAppointment.setSubject("锁定");
            super.save(pwAppointment);
        }
        return pwAppointment.getId();
    }

    private void validate(PwAppointment pwAppointment, User user) {
        if (pwAppointment.getPwRoom() == null || StringUtils.isBlank(pwAppointment.getPwRoom().getId())
                || pwRoomService.get(pwAppointment.getPwRoom().getId()) == null) {
            throw new RuntimeException("未找到房间信息");
        }
        Date startDate = pwAppointment.getStartDate();
        Date endDate = pwAppointment.getEndDate();
        if (startDate == null || endDate == null) {
            throw new RuntimeException("未设置完整的开始和结束时间");
        }
        if (startDate.after(endDate)) {
            throw new RuntimeException("开始时间应该早于结束时间");
        }
        if (startDate.getTime() < new Date().getTime()) {
            throw new RuntimeException("不能预约过去的时间");
        }
        PwAppointmentRule pwAppointmentRule = pwAppointmentRuleService.getPwAppointmentRule();
        if (pwAppointmentRule == null) {
            throw new RuntimeException("未设置预约规则");
        }
        Integer delay = Integer.valueOf(pwAppointmentRule.getAfterDays());
        Date dayToEnd = CommonUtils.getDayToEnd(delay);
        if (!isAdmin(user) && endDate.after(dayToEnd)) {
            throw new RuntimeException(String.format("只能预约%s天内的房间", delay));
        }
        PwAppointmentVo vo = new PwAppointmentVo();
        List<String> rooms = new ArrayList<>(1);
        rooms.add(pwAppointment.getPwRoom().getId());
        vo.setRoomIds(rooms);
        List<String> status = new ArrayList<>(2);
        status.add(PwAppointmentStatus.WAIT_AUDIT.getValue());
        status.add(PwAppointmentStatus.PASS.getValue());
        status.add(PwAppointmentStatus.LOCKED.getValue());
        vo.setStatus(status);
        vo.setStartDate(CommonUtils.getDayFromBegin(startDate, 0));
        vo.setEndDate(CommonUtils.getDayToEnd(endDate, 0));
        List<PwAppointment> list = this.findListByPwAppointmentVo(vo);
        if (!list.isEmpty()) {
            for (PwAppointment appointment : list) {
                if (!(appointment.getStartDate().getTime() < startDate.getTime() && appointment.getEndDate().getTime() <= startDate.getTime())
                        && !(appointment.getStartDate().getTime() >= endDate.getTime() && appointment.getEndDate().getTime() > endDate.getTime())) {
                    throw new RuntimeException("当前时间段不能预约");
                }

            }
        }
    }

    /**
     * 保存预约的申报信息.
     *
     * @param pwAppointment 预约对象
     * @return ActYwApply
     */
    @Transactional(readOnly = false)
    public ActYwApply saveApply(PwAppointment pwAppointment) {
        ActYwApply actYwApply = new ActYwApply();
        actYwApply.setActYw(new ActYw(FlowYwId.FY_APPOINTMENT.getId()));
        actYwApply.setType(FlowType.FWT_APPOINTMENT.getKey());
        actYwApply.setRelId(pwAppointment.getId());
        actYwApplyService.saveApply(actYwApply);
        ActYwApplyVo actYwApplyVo = new ActYwApplyVo(actYwApply.getId(), actYwApply.getActYw(), actYwApply.getApplyUser());
        PwAppointmentRule pwAppointmentRule = pwAppointmentRuleService.getPwAppointmentRule();
        ActYwRstatus actYwRstatus;
        if ("1".equals(pwAppointmentRule.getIsAuto())) {
            actYwRstatus = actYwApplyService.timeSubmit(actYwApplyVo);
        } else {
            actYwRstatus = actYwApplyService.submit(actYwApplyVo);
        }
        if (!actYwRstatus.getStatus()) {
            throw new ApplyException(actYwRstatus.getMsg(), null);
        }
        return actYwApply;
    }


    @Transactional(readOnly = false)
    public void delete(PwAppointment pwAppointment) {
        PwAppointment newPwAppointment = this.get(pwAppointment.getId());
        if (newPwAppointment == null) {
            throw new RuntimeException("未找到指定的预约信息");
        }
        super.delete(newPwAppointment);
    }


    @Transactional(readOnly = false)
    public void deleteByRoomIds(List<String> roomIds) {
        pwAppointmentDao.deleteByRoomIds(roomIds);
    }

    public Page<PwAppointment> findMyPwAppointmentList(Page<PwAppointment> page, PwAppointment pwAppointment) {
        pwAppointment.setPage(page);
        page.setList(dao.findMyPwAppointmentList(pwAppointment));
        return page;
    }

    public List<PwAppointment> findViewMonth(PwAppointment pwAppointment) {
        return dao.findViewMonth(pwAppointment);
    }

    /**
     * 根据条件查询预约记录
     *
     * @param pwAppointmentVo
     * @return
     */
    public List<PwAppointment> findListByPwAppointmentVo(PwAppointmentVo pwAppointmentVo) {
        return pwAppointmentDao.findListByPwAppointmentVo(pwAppointmentVo);
    }

    /**
     * 根据信息查询预约、房间、预约规则
     *
     * @param pwAppointmentVo
     * @return
     */
    public Map<String, Object> mouthSearch(PwAppointmentVo pwAppointmentVo) {
        Map<String, Object> map = new HashMap<>();
        List<PwRoom> rooms = pwRoomService.findListByPwAppointmentVo(pwAppointmentVo);
        PwAppointmentRule pwAppointmentRule = pwAppointmentRuleService.getPwAppointmentRule();
        setRoomPath(rooms);
        JSONObject jsonData = findViewMonthList(pwAppointmentVo);
        map.put("list", jsonData.get("dataList"));
        map.put("rooms", rooms);
        map.put("appRule", pwAppointmentRule);
        map.put("now", CommonUtils.formatDate(new Date()));
        return map;
    }

    private void setRoomPath(List<PwRoom> rooms) {
        if (!rooms.isEmpty()) {
            for (PwRoom room : rooms) {
                PwSpace pwSpace = room.getPwSpace();
                StringBuffer sb = new StringBuffer(room.getName());
                while (pwSpace != null && StringUtils.isNotBlank(pwSpace.getName())) {
                    if (StringUtils.isBlank(pwSpace.getType()) || PwSpaceType.SCHOOL.getValue().equals(pwSpace.getType())) {
                        break;
                    }
                    sb.insert(0, pwSpace.getName());
                    pwSpace = pwSpace.getParent();
                }
                sb.insert(0, "【" + PwRoomType.getNameByValue(room.getType()) + "】");
                room.setPath(sb.toString());
            }
        }
    }


    /**
     * 根据信息查询预约、房间、预约规则
     *
     * @param pwAppointmentVo
     * @return
     */
    public Map<String, Object> weekAndDaySearch(PwAppointmentVo pwAppointmentVo) {
        Map<String, Object> map = new HashMap<>();
        List<PwAppointment> list = this.findListByPwAppointmentVo(pwAppointmentVo);
        List<PwRoom> rooms = pwRoomService.findListByPwAppointmentVo(pwAppointmentVo);
        setRoomPath(rooms);
        PwAppointmentRule pwAppointmentRule = pwAppointmentRuleService.getPwAppointmentRule();
        map.put("list", list);
        map.put("rooms", rooms);
        map.put("appRule", pwAppointmentRule);
        map.put("now", CommonUtils.formatDate(new Date()));
        return map;
    }

    /**
     * 取消预约
     *
     * @param pwAppointment
     * @return
     */
    @Transactional(readOnly = false)
    public String cancel(PwAppointment pwAppointment) {
        PwAppointment newPwAppointment = get(pwAppointment.getId());
        User user = UserUtils.getUser();
        if (!user.getId().equals(newPwAppointment.getUser().getId()) && !isAdmin(user)) {
            throw new RuntimeException("当前用户没有权限取消该预约");
        }
        this.changeStatus(pwAppointment.getId(), PwAppointmentStatus.CANCELED);
        if (!user.getId().equals(newPwAppointment.getUser().getId()) && !isAdmin(newPwAppointment.getUser())) {//不是本人取消预约,也不是管理员的预约，发站内信
            this.sendOaNotify(newPwAppointment, user);
        }
        return newPwAppointment.getId();
    }

    /**
     * 更新预约的状态
     *
     * @param pwAppointmentId
     * @param status
     */
    @Transactional(readOnly = false)
    public void changeStatus(String pwAppointmentId, PwAppointmentStatus status) {
        PwAppointment newPwAppointment = get(pwAppointmentId);
        if (newPwAppointment == null) {
            throw new RuntimeException("未找到预约信息");
        }
        if (newPwAppointment.getStatus().equals(status.getValue())) {
            throw new RuntimeException(String.format("已经是%s状态", status.getName()));
        }
        newPwAppointment.setStatus(status.getValue());
        super.save(newPwAppointment);
    }


    public JSONObject findViewMonthList(PwAppointmentVo pwAppointmentVo) {
        List<PwAppMouthVo> wAppMouthVoList = dao.findViewMonthList(pwAppointmentVo);
        JSONObject jsonDataList = new JSONObject();
        JSONArray yeardata = new JSONArray();
        for (PwAppMouthVo vo : wAppMouthVoList) {
            JSONObject jsonData = new JSONObject();
            String title = "您有" + vo.getNum() + "条预约" +
                    DictUtils.getDictLabel(vo.getStatus(), "pw_appointment_status", "");
            jsonData.put("title", title);
            jsonData.put("start", vo.getMday());
            jsonData.put("link", "/pw/pwAppointment/getListByState?mDay=" + vo.getMday() + "&state=" + vo.getStatus());

            jsonData.put("state", vo.getStatus());
            yeardata.add(jsonData);
        }
        jsonDataList.put("dataList", yeardata);
        return jsonDataList;
    }

    /**
     * 发送站内信.
     *
     * @param pwAppointment
     * @param user
     */
    @Transactional(readOnly = false)
    public void sendOaNotify(PwAppointment pwAppointment, User user) {
        OaNotifyRecord oaNotifyRecord = new OaNotifyRecord();
        OaNotify oaNotify = new OaNotify();
        oaNotify.setTitle("预约通知");
        oaNotify.setContent(String.format("您预约%s到%s的%s已被管理员取消", CommonUtils.formatDate(pwAppointment.getStartDate(), "yyyy-MM-dd HH:mm"), CommonUtils.formatDate(pwAppointment.getEndDate(), "yyyy-MM-dd HH:mm"), pwAppointment.getPwRoom().getName()));
        oaNotify.setType(OaNotify.Type_Enum.TYPE15.getValue());
        oaNotify.setsId(pwAppointment.getId());
        oaNotify.setCreateBy(user);
        oaNotify.setCreateDate(new Date());
        oaNotify.setUpdateBy(user);
        oaNotify.setUpdateDate(oaNotify.getCreateDate());
        oaNotify.setEffectiveDate(oaNotify.getCreateDate());
        oaNotify.setStatus(OaNotifyTypeStatus.DEPLOY.getKey());
        oaNotify.setSendType(OaNotifySendType.DIRECRIONAL.getVal());

        List<OaNotifyRecord> recList = new ArrayList<>();
        oaNotifyRecord.setId(IdGen.uuid());
        oaNotifyRecord.setOaNotify(oaNotify);
        oaNotifyRecord.setUser(pwAppointment.getUser());
        oaNotifyRecord.setReadFlag(Global.NO);
        oaNotifyRecord.setOperateFlag(Global.NO);
        recList.add(oaNotifyRecord);

        oaNotify.setOaNotifyRecordList(recList);
        oaNotifyService.save(oaNotify);
    }


    /**
     * 人工审核
     *
     * @param id
     * @param grade
     */
    @Transactional(readOnly = false)
    public void manualAudit(String id, String grade) {
        PwAppointment newPwAppointment = get(id);
        if (newPwAppointment == null) {
            throw new RuntimeException("未找到预约信息");
        }
        if (!newPwAppointment.getStatus().equals(PwAppointmentStatus.WAIT_AUDIT.getValue())) {
            throw new RuntimeException("预约已经审核过");
        }
        ActYwApply actYwApply = new ActYwApply();
        actYwApply.setRelId(id);
        List<ActYwApply> applyList = actYwApplyService.findList(actYwApply);
        if (applyList.isEmpty()) {
            throw new RuntimeException("未找到预约相关的流程信息");
        }
        actYwApplyService.pwAppointAudit(applyList.get(0), grade);
    }

    /**
     * 用户是否是管理员
     * 服务于预约视图
     * 目前的逻辑是只要能看到菜单同时用户类型不是学生或是导师
     *
     * @param user
     * @return
     */
    public boolean isAdmin(User user) {
        return !(EuserType.UT_C_TEACHER.getType().equals(user.getUserType())
                || EuserType.UT_C_STUDENT.getType().equals(user.getUserType()));
    }


}