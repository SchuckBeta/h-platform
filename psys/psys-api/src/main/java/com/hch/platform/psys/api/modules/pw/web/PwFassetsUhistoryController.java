package com.hch.platform.pcore.modules.pw.web;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.pw.entity.PwFassetsUhistory;
import com.hch.platform.pcore.modules.pw.service.PwFassetsUhistoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 固定资产使用记录Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwFassetsUhistory")
public class PwFassetsUhistoryController extends BaseController {

    @Autowired
    private PwFassetsUhistoryService pwFassetsUhistoryService;

    @ModelAttribute
    public PwFassetsUhistory get(@RequestParam(required = false) String id) {
        PwFassetsUhistory entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwFassetsUhistoryService.get(id);
        }
        if (entity == null) {
            entity = new PwFassetsUhistory();
        }
        return entity;
    }

    @RequiresPermissions("pw:pwFassetsUhistory:view")
    @RequestMapping(value = {"list", ""})
    public String list(PwFassetsUhistory pwFassetsUhistory, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PwFassetsUhistory> page = pwFassetsUhistoryService.findPage(new Page<PwFassetsUhistory>(request, response), pwFassetsUhistory);
        model.addAttribute("page", page);
        return "modules/pw/pwFassetsUhistoryList";
    }

    @RequiresPermissions("pw:pwFassetsUhistory:view")
    @RequestMapping(value = "form")
    public String form(PwFassetsUhistory pwFassetsUhistory, Model model) {
        model.addAttribute("pwFassetsUhistory", pwFassetsUhistory);
        return "modules/pw/pwFassetsUhistoryForm";
    }

    @RequiresPermissions("pw:pwFassetsUhistory:edit")
    @RequestMapping(value = "save")
    public String save(PwFassetsUhistory pwFassetsUhistory, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwFassetsUhistory)) {
            return form(pwFassetsUhistory, model);
        }
        pwFassetsUhistoryService.save(pwFassetsUhistory);
        addMessage(redirectAttributes, "保存固定资产使用记录成功");
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassetsUhistory/?repage";
    }

    @RequiresPermissions("pw:pwFassetsUhistory:edit")
    @RequestMapping(value = "delete")
    public String delete(PwFassetsUhistory pwFassetsUhistory, RedirectAttributes redirectAttributes) {
        pwFassetsUhistoryService.delete(pwFassetsUhistory);
        addMessage(redirectAttributes, "删除固定资产使用记录成功");
        return "redirect:" + Global.getAdminPath() + "/pw/pwFassetsUhistory/?repage";
    }

}