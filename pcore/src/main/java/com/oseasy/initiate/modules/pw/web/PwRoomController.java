package com.oseasy.initiate.modules.pw.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.entity.PwRoom;
import com.oseasy.initiate.modules.pw.entity.PwSpaceRoom;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
import com.oseasy.initiate.modules.pw.service.PwRoomService;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;

/**
 * 房间Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwRoom")
public class PwRoomController extends BaseController {

    @Autowired
    private PwRoomService pwRoomService;
    @Autowired
    private PwEnterService pwEnterService;

    @ModelAttribute
    public PwRoom get(@RequestParam(required = false) String id) {
        PwRoom entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwRoomService.get(id);
        }
        if (entity == null) {
            entity = new PwRoom();
        }
        return entity;
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = {"tree"})
    public String tree(PwRoom pwRoom, HttpServletRequest request, HttpServletResponse response, Model model) {
        return "modules/pw/pwRoomTree";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = {"list", ""})
    public String list(PwRoom pwRoom, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwRoom> page = pwRoomService.findPageByJL(new Page<PwRoom>(request, response), pwRoom);
        model.addAttribute("page", page);
        model.addAttribute("root", SysIds.SYS_TREE_ROOT.getId());
        return "modules/pw/pwRoomList";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = {"treeFPCD"})
    public String treeFP(PwRoom pwRoom, HttpServletRequest request, HttpServletResponse response, Model model) {
        return "modules/pw/pwRoomTreeFPCD";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = {"listFPCD"})
    public String listFPCD(PwRoom pwRoom, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwRoom> page = pwRoomService.findPageByJLKfpCD(new Page<PwRoom>(request, response), pwRoom);
        model.addAttribute("page", page);
        model.addAttribute("root", SysIds.SYS_TREE_ROOT.getId());
        return "modules/pw/pwRoomListFPCD";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = "form")
    public String form(PwRoom pwRoom, Model model) {
        if((pwRoom == null)){
          pwRoom = new PwRoom();
        }

        if(StringUtil.isEmpty(pwRoom.getIsAllowm())){
          pwRoom.setIsAllowm(Global.NO);
        }

        model.addAttribute("pwRoom", pwRoom);
        return "modules/pw/pwRoomForm";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = "details")
    public String details(PwRoom pwRoom, Model model) {
        model.addAttribute("pwRoom", pwRoom);
        return "modules/pw/pwRoomDetails";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = "formSetZC")
    public String formSetZC(PwRoom pwRoom, Model model) {
        model.addAttribute("pwRoom", pwRoom);
        return "modules/pw/pwRoomFormSetZC";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = "formSetCD")
    public String formSetCD(PwRoom pwRoom, Model model) {
        model.addAttribute("pwRoom", pwRoom);
        return "modules/pw/pwRoomFormSetCD";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @RequestMapping(value = "view")
    public String view(PwRoom pwRoom, Model model) {
        List<PwEnter> pwEnters = Lists.newArrayList();
        if((pwRoom != null) && StringUtil.isNotEmpty(pwRoom.getId())){
          PwEnter pwEnter = new PwEnter();
          pwEnter.setPstatus(PwEnterStatus.getKeyByYFP());
          pwEnter.setIds(Arrays.asList(new String[]{pwRoom.getId()}));
          pwEnters = pwEnterService.findListByGroup(pwEnter);
        }

        model.addAttribute("pwRoom", pwRoom);
        model.addAttribute("pwEnters", pwEnters);
        model.addAttribute("root", SysIds.SYS_TREE_ROOT.getId());
        return "modules/pw/pwRoomView";
    }

    @RequiresPermissions("pw:pwRoom:edit")
    @RequestMapping(value = "save")
    public String save(PwRoom pwRoom, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwRoom)) {
            return form(pwRoom, model);
        }

        ActYwRstatus<PwRoom> rstatus = pwRoomService.savePR(pwRoom);
        addMessage(redirectAttributes, rstatus.getMsg());
        return "redirect:" + Global.getAdminPath() + "/pw/pwRoom/?repage";
    }

    @RequiresPermissions("pw:pwRoom:edit")
    @RequestMapping(value = "delete")
    public String delete(PwRoom pwRoom, RedirectAttributes redirectAttributes) {
        try{
            pwRoomService.deleteAndClear(pwRoom);
            addMessage(redirectAttributes, "删除房间成功");
        } catch (Exception e){
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwRoom/?repage";
    }

    /**
     * 房间重名验证.
     * @param id 审核ID
     * @param edid 审核类型
     * @param atype 审核结果类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ajaxVerifyName")
    public Boolean ajaxVerifyName(String name, String sid, String id) {
      ActYwRstatus<String> rstatus = pwRoomService.verifyName(name, sid, id);
      return rstatus.getStatus();
    }


    /**
     * 房间开放预约.
     *
     * @param pwRoom             实体
     * @param model              模型
     * @param request            请求
     * @param redirectAttributes 重定向
     * @return String
     */
    @RequiresPermissions("pw:pwRoom:edit")
    @RequestMapping(value = "ajaxIsUsable")
    public String ajaxIsUsable(PwRoom pwRoom, Model model, String isUsable, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (StringUtil.isNotEmpty(pwRoom.getId()) && StringUtil.isNotEmpty(pwRoom.getIsUsable())) {
            PwRoom newPwRoom = pwRoomService.get(pwRoom.getId());
            newPwRoom.setIsUsable(pwRoom.getIsUsable());
            return save(newPwRoom, model, redirectAttributes);
        }

        return "redirect:" + Global.getAdminPath() + "/pw/pwRoom/?repage";
    }

    /**
     * 房间开放分配.
     *
     * @param pwRoom             实体
     * @param model              模型
     * @param request            请求
     * @param redirectAttributes 重定向
     * @return String
     */
    @RequiresPermissions("pw:pwRoom:edit")
    @RequestMapping(value = "ajaxIsAssign")
    public String ajaxIsAssign(PwRoom pwRoom, Model model, String isUsable, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (StringUtil.isNotEmpty(pwRoom.getId()) && StringUtil.isNotEmpty(pwRoom.getIsUsable())) {
            PwRoom newPwRoom = pwRoomService.get(pwRoom.getId());
            newPwRoom.setIsAssign(pwRoom.getIsAssign());
            return save(newPwRoom, model, redirectAttributes);
        }

        return "redirect:" + Global.getAdminPath() + "/pw/pwRoom/?repage";
    }

    @RequiresPermissions("pw:pwRoom:view")
    @ResponseBody
    @RequestMapping(value = "roomTreeData")
    public List<Map<String, Object>> roomTreeData(PwRoom pwRoom, @RequestParam(required = false) String extId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<PwSpaceRoom> list = pwRoomService.findSpaceAndRoom();
        Set<String> idSet = new HashSet<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            PwSpaceRoom pr = list.get(i);
            if (pr.getPwSpace() == null) {
                continue;
            }
            if (StringUtils.isNotBlank(pr.getRoomId())) {//房间
                if (!idSet.contains(pr.getPwSpace().getId())) {
                    //楼层
                    Map<String, Object> map1 = Maps.newHashMap();
                    map1.put("id", pr.getPwSpace().getId());
                    map1.put("pId", pr.getPwSpace().getParent().getId());
                    map1.put("name", pr.getPwSpace().getName());
                    map1.put("type", pr.getPwSpace().getType());
                    map1.put("isParent", true);
                    mapList.add(map1);
                    idSet.add(pr.getPwSpace().getId());
                }
                if ("1".equals(pr.getRoomDelFlag())) {
                    continue;
                }
                if (pwRoom != null) {
                    if (StringUtils.isNotBlank(pwRoom.getIsUsable()) && !pwRoom.getIsUsable().equals(pr.getUsable())) {
                        continue;
                    }
                }
                //房间
                Map<String, Object> map2 = Maps.newHashMap();
                map2.put("id", pr.getRoomId());
                map2.put("pId", pr.getPwSpace().getId());
                map2.put("name", pr.getRoomName());
                map2.put("type", "room");
                map2.put("respName", pr.getRespName());
                map2.put("mobile", pr.getPwRoom().getMobile());
                map2.put("num", pr.getPwRoom().getNum());
                map2.put("isParent", false);
                mapList.add(map2);
            } else {//非房间（学校、校区、基地、楼栋）
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", pr.getPwSpace().getId());
                map.put("pId", pr.getPwSpace().getParent().getId());
                map.put("name", pr.getPwSpace().getName());
                map.put("type", pr.getPwSpace().getType());
                map.put("isParent", true);
                mapList.add(map);
            }
        }
        return mapList;
    }

    @RequiresPermissions("pw:pwRoom:view")
    @ResponseBody
    @RequestMapping(value = "jsonList")
    public List<PwRoom> list(PwRoom pwRoom) {
        return pwRoomService.findList(pwRoom);
    }


}