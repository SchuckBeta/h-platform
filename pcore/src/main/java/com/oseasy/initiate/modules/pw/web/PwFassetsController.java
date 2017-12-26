package com.oseasy.initiate.modules.pw.web;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwCategory;
import com.oseasy.initiate.modules.pw.entity.PwFassets;
import com.oseasy.initiate.modules.pw.entity.PwRoom;
import com.oseasy.initiate.modules.pw.service.PwFassetsService;
import com.oseasy.initiate.modules.pw.service.PwRoomService;
import com.oseasy.initiate.modules.pw.utils.SpaceUtils;
import com.oseasy.initiate.modules.pw.vo.PwFassetsAssign;
import com.oseasy.initiate.modules.pw.vo.PwFassetsBatch;
import com.oseasy.initiate.modules.pw.vo.PwFassetsStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 固定资产Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwFassets")
public class PwFassetsController extends BaseController {

    @Autowired
    private PwFassetsService pwFassetsService;
    @Autowired
    private PwRoomService pwRoomService;

    @ModelAttribute
    public PwFassets get(@RequestParam(required = false) String id) {
        PwFassets entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwFassetsService.get(id);
        }
        if (entity == null) {
            entity = new PwFassets();
        }
        return entity;
    }

    @RequiresPermissions("pw:pwFassets:view")
    @RequestMapping(value = {"list", ""})
    public String list(PwFassets pwFassets, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwFassets> page = pwFassetsService.findPage(new Page<PwFassets>(request, response), pwFassets);
        model.addAttribute("page", page);
        return "modules/pw/pwFassetsList";
    }

    @RequestMapping(value = {"listYfp"})
    public String listYfp(PwFassets pwFassets, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwFassets> page = pwFassetsService.findPageByYfp(new Page<PwFassets>(request, response), pwFassets);
        model.addAttribute("page", page);
        return "modules/pw/pwFassetsListYfp";
    }

    @RequiresPermissions("pw:pwFassets:view")
    @RequestMapping(value = "form")
    public String form(PwFassets pwFassets, Model model) {
        model.addAttribute("pwFassets", pwFassets);
        return "modules/pw/pwFassetsForm";
    }

    @RequiresPermissions("pw:pwFassets:view")
    @RequestMapping(value = "details")
    public String details(PwFassets pwFassets, Model model) {
        model.addAttribute("pwFassets", pwFassets);
        return "modules/pw/pwFassetsDetails";
    }

    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "save")
    public String save(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwFassets)) {
            return form(pwFassets, model);
        }
        try {
            pwFassetsService.save(pwFassets);
            addMessage(redirectAttributes, "保存固定资产成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }

    @RequiresPermissions("pw:pwFassets:view")
    @RequestMapping(value = "batchForm")
    public String batchForm(PwFassetsBatch pwFassetsModel, Model model) {
        model.addAttribute("pwFassetsModel", pwFassetsModel);
        return "modules/pw/pwFassetsBatchAddForm";
    }

    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "batchSave")
    public String batchSave(PwFassetsBatch pwFassetsModel, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwFassetsModel)) {
            return batchForm(pwFassetsModel, model);
        }
        try {
            pwFassetsService.batchSave(pwFassetsModel);
            addMessage(redirectAttributes, "批量添加固定资产成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }


    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "delete")
    public String delete(PwFassets pwFassets, RedirectAttributes redirectAttributes) {
        try {
            pwFassetsService.delete(pwFassets);
            addMessage(redirectAttributes, "删除固定资产成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }

    /**
     * 给房间分配固定资产.
     *
     * @param pwFassets 实体
     * @param model     模型
     * @return ActYwRstatus
     */
    @ResponseBody
    @RequestMapping(value = "set")
    public String set(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtil.isEmpty(pwFassets.getId())) {
            addMessage(redirectAttributes, "分配固定资产失败，资产ID不能为空！");
            return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage";
        }

        if ((pwFassets.getPwRoom() == null) || StringUtil.isEmpty(pwFassets.getPwRoom().getId())) {
            addMessage(redirectAttributes, "分配固定资产失败，房间ID不能为空！");
            return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage";
        }

        try {
            pwFassetsService.assign(pwFassets);
            addMessage(redirectAttributes, "分配固定资产成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage";
    }

    /**
     * 从房间中取消固定资产.
     *
     * @param pwFassets 实体
     * @param model     模型
     * @return ActYwRstatus
     */
    @RequestMapping(value = "cancel")
    public String cancel(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtil.isEmpty(pwFassets.getId())) {
            addMessage(redirectAttributes, "删除失败，数据不存在");
            return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage&pwRoom.id=" + pwFassets.getPwRoom().getId();
        }

        try {
            pwFassetsService.unAssign(pwFassets);
            addMessage(redirectAttributes, "取消分配成功");
            return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage&pwRoom.id=" + pwFassets.getPwRoom().getId();
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/listYfp/?repage";
    }

    /**
     * 分配固定资产.
     *
     * @param pwFassets 实体
     * @param model     模型
     * @return ActYwRstatus
     */
    @ResponseBody
    @RequestMapping(value = "ajaxSet")
    public ActYwRstatus<PwFassets> ajaxSet(PwFassets pwFassets, Model model, HttpServletResponse response) {
        if (StringUtil.isEmpty(pwFassets.getId())) {
            return new ActYwRstatus<PwFassets>(false, "分配失败，资产数据不存在");
        }

        if ((pwFassets.getPwRoom() == null) || StringUtil.isEmpty(pwFassets.getPwRoom().getId())) {
            return new ActYwRstatus<PwFassets>(false, "分配失败，房间数据不存在");
        }

        try {
            pwFassetsService.assign(pwFassets);
        } catch (Exception e) {
            return new ActYwRstatus<PwFassets>(false, e.getMessage());
        }
        return new ActYwRstatus<PwFassets>(true, "分配成功", pwFassets);
    }

    /**
     * 批量分配固定资产.
     *
     * @param pwFassets 实体
     * @param model     模型
     * @return ActYwRstatus
     */
    @ResponseBody
    @RequestMapping(value = "ajaxSetPL")
    public ActYwRstatus<PwFassets> ajaxSetPL(PwFassets pwFassets, Model model, HttpServletResponse response) {
        if (StringUtil.isEmpty(pwFassets.getId())) {
            return new ActYwRstatus<PwFassets>(false, "分配失败，资产数据不存在");
        }

        if ((pwFassets.getPwRoom() == null) || StringUtil.isEmpty(pwFassets.getPwRoom().getId())) {
            return new ActYwRstatus<PwFassets>(false, "分配失败，房间数据不存在");
        }

        PwRoom pwRoom = pwRoomService.get(pwFassets.getPwRoom().getId());
        if (pwRoom == null) {
            return new ActYwRstatus<PwFassets>(true, "分配失败，场地不存在", pwFassets);
        } else {
            pwFassets.setRespName(pwRoom.getPerson());
            pwFassets.setRespPhone(pwRoom.getPhone());
            pwFassets.setRespMobile(pwRoom.getMobile());
        }
        pwFassetsService.batchAssign(pwFassets, pwFassetsService.findListByIds(Arrays.asList(StringUtil.split(pwFassets.getId(), StringUtil.DOTH))));
        return new ActYwRstatus<PwFassets>(true, "分配成功", pwFassets);
    }

    /**
     * 批量取消分配固定资产.
     *
     * @param pwFassets 实体
     * @param model     模型
     * @return ActYwRstatus
     */
    @ResponseBody
    @RequestMapping(value = "ajaxCancelPL")
    public ActYwRstatus<PwFassets> ajaxCancelPL(PwFassets pwFassets, Model model, HttpServletResponse response) {
        if (StringUtil.isEmpty(pwFassets.getId())) {
            return new ActYwRstatus<PwFassets>(false, "取消失败，资产数据不存在");
        }

        if ((pwFassets.getPwRoom() == null) || StringUtil.isEmpty(pwFassets.getPwRoom().getId())) {
            return new ActYwRstatus<PwFassets>(false, "取消失败，房间数据不存在");
        }

        List<PwFassets> pwFassetss = pwFassetsService.findListByIds(Arrays.asList(StringUtil.split(pwFassets.getId(), StringUtil.DOTH)));
        pwFassetsService.updateByPL(pwFassets.getPwRoom().getId(), PwFassetsStatus.UNUSED.getValue(), pwFassetss);
        return new ActYwRstatus<PwFassets>(true, "取消成功", pwFassets);
    }

    /**
     * 根据房间获取资产.
     *
     * @param rid      类别ID
     * @param response
     * @return ActYwRstatus
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeDataRoom/{rid}")
    public ActYwRstatus<List<PwFassets>> treeDataRoom(@PathVariable String rid, HttpServletResponse response) {
        List<PwFassets> list = pwFassetsService.findListByRoom(new PwFassets(new PwRoom(rid)));
        if ((list == null) || (list.size() <= 0)) {
            return new ActYwRstatus<List<PwFassets>>(false, "请求失败或数据为空！");
        }
        return new ActYwRstatus<List<PwFassets>>(true, "请求成功", list);
    }

    /**
     * 根据类别获取资产.
     *
     * @param cid      类别ID
     * @param response
     * @return ActYwRstatus
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData/{cid}")
    public ActYwRstatus<List<PwFassets>> treeData(@PathVariable String cid, HttpServletResponse response) {
        List<PwFassets> list = pwFassetsService.findListByNoRoom(new PwFassets(new PwCategory(cid)));
        if ((list == null) || (list.size() <= 0)) {
            return new ActYwRstatus<List<PwFassets>>(false, "请求失败或数据为空！");
        }
        return new ActYwRstatus<List<PwFassets>>(true, "请求成功", list);
    }

    /**
     * 根据类别获取资产.
     *
     * @param response
     * @return ActYwRstatus
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeDataAll")
    public ActYwRstatus<List<PwFassets>> treeData(HttpServletResponse response) {
        List<PwFassets> list = pwFassetsService.findListByNoRoom(new PwFassets());
        if ((list == null) || (list.size() <= 0)) {
            return new ActYwRstatus<List<PwFassets>>(false, "请求失败或数据为空！");
        }
        return new ActYwRstatus<List<PwFassets>>(true, "请求成功", list);
    }


    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "unassign")
    public String unAssign(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        try {
            pwFassetsService.unAssign(pwFassets);
            addMessage(redirectAttributes, "取消分配成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }

    @RequiresPermissions("pw:pwFassets:view")
    @RequestMapping(value = "assignForm")
    public String assignForm(PwFassetsAssign pwFassetsAssign, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isBlank(pwFassetsAssign.getFassetsIds())) {
            addMessage(redirectAttributes, "未选择资产");
            return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
        }
        String[] split = pwFassetsAssign.getFassetsIds().split(",");
        List<PwFassets> list = pwFassetsService.findListByIds(Arrays.asList(split));
        model.addAttribute("list", list);
        return "modules/pw/pwFassetsAssignForm";
    }

    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "assign")
    public String assign(PwFassetsAssign pwFassetsAssign, Model model, RedirectAttributes redirectAttributes) {
        try {
            pwFassetsService.batchAssign(pwFassetsAssign);
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
            return assignForm(pwFassetsAssign, model, redirectAttributes);
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }

    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "changeBroken")
    public String changeBroken(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        try {
            pwFassetsService.changeBroken(pwFassets);
            addMessage(redirectAttributes, "标记成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }

    @RequiresPermissions("pw:pwFassets:edit")
    @RequestMapping(value = "changeUnused")
    public String changeUnused(PwFassets pwFassets, Model model, RedirectAttributes redirectAttributes) {
        try {
            pwFassetsService.changeUnused(pwFassets);
            addMessage(redirectAttributes, "标记成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassets/?repage";
    }
}