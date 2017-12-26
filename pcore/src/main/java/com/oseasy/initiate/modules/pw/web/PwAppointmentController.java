package com.oseasy.initiate.modules.pw.web;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwAppointment;
import com.oseasy.initiate.modules.pw.service.PwAppointmentRuleService;
import com.oseasy.initiate.modules.pw.service.PwAppointmentService;
import com.oseasy.initiate.modules.pw.utils.CommonUtils;
import com.oseasy.initiate.modules.pw.vo.*;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwAppointment")
public class PwAppointmentController extends BaseController {

    private static final String MENU_WEEK = "周视图";

    private static final String MENU_DAY = "日视图";

    @Autowired
    private PwAppointmentService pwAppointmentService;

    @Autowired
    private PwAppointmentRuleService pwAppointmentRuleService;

    @ModelAttribute
    public PwAppointment get(@RequestParam(required = false) String id) {
        PwAppointment entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwAppointmentService.get(id);
        }
        if (entity == null) {
            entity = new PwAppointment();
        }
        return entity;
    }


    @RequestMapping(value = {"list", ""})
    public String list(PwAppointment pwAppointment, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwAppointment> page = pwAppointmentService.findPage(new Page<PwAppointment>(request, response), pwAppointment);
        model.addAttribute("page", page);
        return "modules/pw/pwAppointmentList";
    }


    @RequestMapping(value = "form")
    public String form(PwAppointment pwAppointment, Model model) {
        model.addAttribute("pwAppointment", pwAppointment);
        return "modules/pw/pwAppointmentForm";
    }


    @RequestMapping(value = "save")
    public String save(PwAppointment pwAppointment, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwAppointment)) {
            return form(pwAppointment, model);
        }
        pwAppointmentService.save(pwAppointment);
        addMessage(redirectAttributes, "保存预约成功");
        return "redirect:" + Global.getAdminPath() + "/pw/pwAppointment/?repage";
    }

    @RequiresPermissions("pw:pwAppointment:view")
    @RequestMapping(value = "viewMonth")
    public String viewMonth(PwAppointmentVo pwAppointmentVo, Model model) {
        //得到当前日期
        Date nowDate = new Date();
        String nowString = DateUtil.formatDate(nowDate, "yyyy-MM-dd");
        //根据配置得出当前提前预约天数
//        PwAppointmentRule pwAppointmentRuleIn = pwAppointmentRuleService.getPwAppointmentRule();
//        String afterday = pwAppointmentRuleIn.getAfterDays();
        //根据配置得出当前预约终止时间

        beforeMouthSearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.mouthSearch(pwAppointmentVo);
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));

//        JSONObject jsonData=new JSONObject();
//        jsonData=pwAppointmentService.findViewMonthList(pwAppointmentVo);
//        jsonData.put("nowDate",nowString);
//        model.addAttribute("jsonData", jsonData);
//        model.addAttribute("pwAppMouthVo", pwAppMouthVo);
        return "modules/pw/pwAppointmentViewMonth";
    }

    @ResponseBody
    @RequestMapping(value = "getListByState")
    public List<PwAppointment> getListByState(HttpServletRequest request, Model model) {
        String mDay = request.getParameter("mDay");
        String state = request.getParameter("state");
        String startString = mDay + " 00:00:00";
        String endString = mDay + " 23:59:59";
        List<PwAppointment> list = null;
        try {
            Date startDay = DateUtil.parseDate(startString, "yyyy-MM-dd HH:mm:ss");
            Date endDay = DateUtil.parseDate(endString, "yyyy-MM-dd HH:mm:ss");
            PwAppointmentVo pwAppointmentVo = new PwAppointmentVo();
            pwAppointmentVo.setStartDate(startDay);
            pwAppointmentVo.setEndDate(endDay);
            List<String> states = new ArrayList<String>();
            states.add(state);
            pwAppointmentVo.setStatus(states);
            list = pwAppointmentService.findListByPwAppointmentVo(pwAppointmentVo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }


    @RequestMapping(value = "mouthViewDay")
    public String mouthViewDay(PwAppointmentVo pwAppointmentVo, Model model, HttpServletRequest request) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        User currentUser = UserUtils.getUser();
        model.addAttribute("isAdmin", pwAppointmentService.isAdmin(currentUser));
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        String newDay = request.getParameter("newDay");
        if (newDay != null) {
            model.addAttribute("now", newDay);
        } else {
            model.addAttribute("now", map.get("now"));
        }
        return "modules/pw/pwAppointmentViewDay";
    }

    @RequestMapping(value = "delete")
    public String delete(PwAppointment pwAppointment, RedirectAttributes redirectAttributes) {
        try {
            pwAppointmentService.delete(pwAppointment);
            addMessage(redirectAttributes, "删除预约成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwAppointment/?repage";
    }


    @RequiresPermissions("pw:pwAppointment:view")
    @RequestMapping(value = "viewWeek")
    public String viewWeek(PwAppointmentVo pwAppointmentVo, Model model) {
        beforeWeekSearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        User currentUser = UserUtils.getUser();
        model.addAttribute("isAdmin", pwAppointmentService.isAdmin(currentUser));
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));
        return "modules/pw/pwAppointmentViewWeek";
    }


    /**
     * 月视图的默认查询时间段
     *
     * @param pwAppointmentVo
     */
    private void beforeMouthSearch(PwAppointmentVo pwAppointmentVo) {
        if (pwAppointmentVo.getStartDate() == null) {
            pwAppointmentVo.setStartDate(CommonUtils.getDayFromBegin(-180));
        }
        if (pwAppointmentVo.getEndDate() == null) {
            pwAppointmentVo.setEndDate(CommonUtils.getDayToEnd(180));
        }
        if (pwAppointmentVo.getRoomTypes() != null && pwAppointmentVo.getRoomTypes().isEmpty()) {
            pwAppointmentVo.setRoomTypes(null);
        }
        setDefaultStatus(pwAppointmentVo);
    }

    /**
     * 周视图的默认查询时间段
     *
     * @param pwAppointmentVo
     */
    private void beforeWeekSearch(PwAppointmentVo pwAppointmentVo) {
        if (pwAppointmentVo.getStartDate() == null) {
            pwAppointmentVo.setStartDate(CommonUtils.getDayFromBegin(-180));
        }
        if (pwAppointmentVo.getEndDate() == null) {
            pwAppointmentVo.setEndDate(CommonUtils.getDayToEnd(180));
        }
        if (pwAppointmentVo.getRoomTypes() != null && pwAppointmentVo.getRoomTypes().isEmpty()) {
            pwAppointmentVo.setRoomTypes(null);
        }
        setDefaultStatus(pwAppointmentVo);
    }

    /**
     * //默认查询待审核和通过的预约记录
     *
     * @param pwAppointmentVo
     */
    private void setDefaultStatus(PwAppointmentVo pwAppointmentVo) {
        if (pwAppointmentVo.getStatus() == null || pwAppointmentVo.getStatus().isEmpty()) {
            List<String> status = pwAppointmentVo.getStatus();
            if (status == null) {
                status = new ArrayList<>();
            }
            status.add(PwAppointmentStatus.WAIT_AUDIT.getValue());
            status.add(PwAppointmentStatus.PASS.getValue());
            status.add(PwAppointmentStatus.LOCKED.getValue());
            pwAppointmentVo.setStatus(status);
        }
    }


    @ResponseBody
    @RequestMapping(value = "mouthSearch")
    public Map viewMouthSearch(PwAppointmentVo pwAppointmentVo) {
        beforeMouthSearch(pwAppointmentVo);
        return pwAppointmentService.mouthSearch(pwAppointmentVo);
    }


    @ResponseBody
    @RequestMapping(value = "weekSearch")
    public Map viewWeekSearch(PwAppointmentVo pwAppointmentVo) {
        beforeWeekSearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        User currentUser = UserUtils.getUser();
        map.put("isAdmin", pwAppointmentService.isAdmin(currentUser));
        return map;
    }


    /**
     * 学生预约和管理锁定
     *
     * @param pwAppointment
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "asySave", method = RequestMethod.POST)
    public String save(PwAppointment pwAppointment) {
        Msg msg;
        try {
            String id = pwAppointmentService.add(pwAppointment);
            msg = new Msg(true, id);
        } catch (Exception e) {
            msg = new Msg(e.getMessage());
        }
        return msg.toJson();
    }

    @RequiresPermissions("pw:pwAppointment:view")
    @RequestMapping(value = "viewDay")
    public String viewDay(PwAppointmentVo pwAppointmentVo, Model model) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        User currentUser = UserUtils.getUser();
        model.addAttribute("isAdmin", pwAppointmentService.isAdmin(currentUser));
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));
        return "modules/pw/pwAppointmentViewDay";
    }

    /**
     * 日视图的默认查询时间段
     *
     * @param pwAppointmentVo
     */
    private void beforeDaySearch(PwAppointmentVo pwAppointmentVo) {
        if (StringUtils.isNotBlank(pwAppointmentVo.getSearchDay())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date searchDay = sdf.parse(pwAppointmentVo.getSearchDay());
                pwAppointmentVo.setStartDate(CommonUtils.getDayFromBegin(searchDay, -30));
                pwAppointmentVo.setEndDate(CommonUtils.getDayToEnd(searchDay, 30));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (pwAppointmentVo.getStartDate() == null) {
                pwAppointmentVo.setStartDate(CommonUtils.getDayFromBegin(-30));
            }
            if (pwAppointmentVo.getEndDate() == null) {
                pwAppointmentVo.setEndDate(CommonUtils.getDayToEnd(30));
            }
        }
        if (pwAppointmentVo.getRoomTypes() != null && pwAppointmentVo.getRoomTypes().isEmpty()) {
            pwAppointmentVo.setRoomTypes(null);
        }
        setDefaultStatus(pwAppointmentVo);
    }

    @RequiresPermissions("pw:pwAppointment:view")
    @ResponseBody
    @RequestMapping(value = "daySearch")
    public Map viewDaySearch(PwAppointmentVo pwAppointmentVo) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        User currentUser = UserUtils.getUser();
        map.put("isAdmin", pwAppointmentService.isAdmin(currentUser));
        return map;
    }

    /**
     * 取消预约
     *
     * @param pwAppointment
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    public String cancel(PwAppointment pwAppointment) {
        Msg msg;
        try {
            String id = pwAppointmentService.cancel(pwAppointment);
            msg = new Msg(true, id);
        } catch (Exception e) {
            msg = new Msg(e.getMessage());
        }
        return msg.toJson();
    }


    /**
     * 获取预约申请记录.
     *
     * @param pwAppcParam 申报条件
     * @param response
     * @return ActYwRstatus
     */
    @ResponseBody
    @RequestMapping(value = "/treeData", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ActYwRstatus<List<PwAppCalendarVo>> treeData(@RequestBody PwAppCalendarParam pwAppcParam, HttpServletResponse response) {
        List<PwAppointment> list = pwAppointmentService.findListByCalendarParam(pwAppcParam);
        List<PwAppCalendarVo> acVo = PwAppCalendarVo.convert(list);

        if ((acVo == null) || (acVo.size() <= 0)) {
            return new ActYwRstatus<List<PwAppCalendarVo>>(false, "请求失败或数据为空！");
        }
        return new ActYwRstatus<List<PwAppCalendarVo>>(true, "请求成功", acVo);
    }


    @RequestMapping(value = "manualAudit/{id}/{grade}")
    public String manualAudit(@PathVariable String id, @PathVariable String grade, RedirectAttributes redirectAttributes) {
        try {
            pwAppointmentService.manualAudit(id, grade);
            addMessage(redirectAttributes, "操作成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwAppointment/list?repage";
    }
}