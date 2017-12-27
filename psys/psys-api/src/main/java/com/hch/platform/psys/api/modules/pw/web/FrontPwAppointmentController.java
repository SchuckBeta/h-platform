package com.hch.platform.pcore.modules.pw.web;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.hch.platform.pcore.modules.pw.entity.PwAppointment;
import com.hch.platform.pcore.modules.pw.service.PwAppointmentService;
import com.hch.platform.pcore.modules.pw.utils.CommonUtils;
import com.hch.platform.pcore.modules.pw.vo.*;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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
@RequestMapping(value = "${frontPath}/pw/pwAppointment")
public class FrontPwAppointmentController extends BaseController {

    @Autowired
    private PwAppointmentService pwAppointmentService;

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


    @RequestMapping(value = "myList")
    public String myList(PwAppointment pwAppointment, HttpServletRequest request, HttpServletResponse response, Model model) {
        AbsUser user = UserUtils.getUser();
        pwAppointment.setUser(user);
        //pwAppointment.setApplyUserId("1");
        Page<PwAppointment> page = pwAppointmentService.findMyPwAppointmentList(new Page<PwAppointment>(request, response), pwAppointment);
        model.addAttribute("page", page);
        return "modules/pw/frontPwAppointment/myPwAppointmentList";
    }

    @RequestMapping(value = "form")
    public String form(PwAppointment pwAppointment, Model model) {
        model.addAttribute("pwAppointment", pwAppointment);
        return "modules/pw/pwAppointmentForm";
    }

    @ResponseBody
    @RequestMapping(value = "getListByState")
    public List<PwAppointment> getListByState( HttpServletRequest request, Model model) {
        String mDay=request.getParameter("mDay");
        String state=request.getParameter("state");
        String startString=mDay+" 00:00:00";
        String endString=mDay+" 23:59:59";
        List<PwAppointment> list=null;
        try {
            Date startDay = DateUtil.parseDate(startString,"yyyy-MM-dd HH:mm:ss");
            Date endDay=DateUtil.parseDate(endString,"yyyy-MM-dd HH:mm:ss");
            PwAppointmentVo pwAppointmentVo =new PwAppointmentVo();
            pwAppointmentVo.setUser(UserUtils.getUser());
            pwAppointmentVo.setStartDate(startDay);
            pwAppointmentVo.setEndDate(endDay);
            List<String> states=new ArrayList<String>();
            states.add(state);
            pwAppointmentVo.setStatus(states);
            list = pwAppointmentService.findListByPwAppointmentVo(pwAppointmentVo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
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

    @RequestMapping(value = "delete")
    public String delete(PwAppointment pwAppointment, RedirectAttributes redirectAttributes) {
        pwAppointmentService.delete(pwAppointment);
        addMessage(redirectAttributes, "删除预约成功");
        return "redirect:" + Global.getAdminPath() + "/pw/pwAppointment/?repage";
    }


    @ResponseBody
    @RequestMapping(value = "mouthSearch")
    public Map viewMouthSearch(PwAppointmentVo pwAppointmentVo) {
        beforeMouthSearch(pwAppointmentVo);
        pwAppointmentVo.setUser(UserUtils.getUser());
        Map<String, Object> map = pwAppointmentService.mouthSearch(pwAppointmentVo);
        return map;
    }

    @RequestMapping(value = "viewMonth")
    public String viewMonth(PwAppointmentVo pwAppointmentVo, Model model) {
        //得到当前日期
        beforeMouthSearch(pwAppointmentVo);
        pwAppointmentVo.setUser(UserUtils.getUser());
        Map<String, Object> map = pwAppointmentService.mouthSearch(pwAppointmentVo);
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));
        return "modules/pw/frontPwAppointment/myPwAppointmentViewMonth";
    }


    @RequestMapping(value = "viewWeek")
    public String viewWeek(PwAppointmentVo pwAppointmentVo, Model model) {
        beforeWeekSearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        model.addAttribute("isAdmin", false);
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));
        return "modules/pw/frontPwAppointment/myPwAppointmentViewWeek";
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
    @RequestMapping(value = "weekSearch")
    public Map viewWeekSearch(PwAppointmentVo pwAppointmentVo) {
        beforeWeekSearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        map.put("isAdmin", false);
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

    @RequestMapping(value = "viewDay")
    public String viewDay(PwAppointmentVo pwAppointmentVo, Model model) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        model.addAttribute("isAdmin", false);
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        model.addAttribute("now", map.get("now"));
        return "modules/pw/frontPwAppointment/myPwAppointmentViewDay";
    }

    @RequestMapping(value = "mouthViewDay")
    public String mouthViewDay(PwAppointmentVo pwAppointmentVo, Model model,HttpServletRequest request) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        model.addAttribute("isAdmin", false);
        model.addAttribute("list", map.get("list"));
        model.addAttribute("rooms", map.get("rooms"));
        model.addAttribute("appRule", map.get("appRule"));
        String newDay= request.getParameter("newDay");
        if(newDay!=null){
            model.addAttribute("now",newDay);
        }else{
            model.addAttribute("now", map.get("now"));
        }

        return "modules/pw/pwAppointmentViewDay";
    }

    /**
     * 日视图的默认查询时间段
     *
     * @param pwAppointmentVo
     */
    private void beforeDaySearch(PwAppointmentVo pwAppointmentVo) {
        if (pwAppointmentVo.getStartDate() == null) {
            pwAppointmentVo.setStartDate(CommonUtils.getDayFromBegin(-30));
        }
        if (pwAppointmentVo.getEndDate() == null) {
            pwAppointmentVo.setEndDate(CommonUtils.getDayFromBegin(30));
        }
        if (pwAppointmentVo.getRoomTypes() != null && pwAppointmentVo.getRoomTypes().isEmpty()) {
            pwAppointmentVo.setRoomTypes(null);
        }
        setDefaultStatus(pwAppointmentVo);
    }

    @ResponseBody
    @RequestMapping(value = "daySearch")
    public Map viewDaySearch(PwAppointmentVo pwAppointmentVo) {
        beforeDaySearch(pwAppointmentVo);
        Map<String, Object> map = pwAppointmentService.weekAndDaySearch(pwAppointmentVo);
        map.put("isAdmin", false);
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
}