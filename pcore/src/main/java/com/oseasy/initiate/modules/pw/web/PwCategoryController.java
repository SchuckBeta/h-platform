package com.oseasy.initiate.modules.pw.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.pw.entity.PwCategory;
import com.oseasy.initiate.modules.pw.service.PwCategoryService;
import com.oseasy.initiate.modules.pw.utils.SpaceUtils;
import com.oseasy.initiate.modules.pw.vo.Msg;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资源类别Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwCategory")
public class PwCategoryController extends BaseController {

    @Autowired
    private PwCategoryService pwCategoryService;

    @ModelAttribute
    public PwCategory get(@RequestParam(required = false) String id) {
        PwCategory entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwCategoryService.get(id);
        }
        if (entity == null) {
            entity = new PwCategory();
        }
        return entity;
    }

    @RequiresPermissions("pw:pwCategory:view")
    @RequestMapping(value = {"list", ""})
    public String list(PwCategory pwCategory, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<PwCategory> list = pwCategoryService.findList(pwCategory);
        model.addAttribute("list", list);
        return "modules/pw/pwCategoryList";
    }


    @RequiresPermissions("pw:pwCategory:view")
    @RequestMapping(value = "form")
    public String form(PwCategory pwCategory, Model model) {
        if (pwCategory.getParent() != null && StringUtil.isNotBlank(pwCategory.getParent().getId())) {
            pwCategory.setParent(pwCategoryService.get(pwCategory.getParent().getId()));
            // 获取排序号，最末节点排序号+30
            if (StringUtil.isBlank(pwCategory.getId())) {
                PwCategory pwCategoryChild = new PwCategory();
                pwCategoryChild.setParent(new PwCategory(pwCategory.getParent().getId()));
                List<PwCategory> list = pwCategoryService.findList(pwCategory);
                if (list.size() > 0) {
                    pwCategory.setSort(list.get(list.size() - 1).getSort());
                    if (pwCategory.getSort() != null) {
                        pwCategory.setSort(pwCategory.getSort() + 30);
                    }
                }
            }
        }
        if (pwCategory.getSort() == null) {
            pwCategory.setSort(30);
        }
        //树没有给父ID时默认给root节点
        if (pwCategory.getParent() == null || StringUtils.isBlank(pwCategory.getParent().getId())) {
            PwCategory category = pwCategoryService.get(SysIds.SYS_TREE_ROOT.getId());//该方法没有join
            pwCategory.setParent(category);
        }
        //查看修改时，如果是第二级节点(资产大类)，并且有子节点，不允许修改父节点
        model.addAttribute("canEditParent", true);
        if (StringUtils.isNotBlank(pwCategory.getId())) {
            if (SysIds.SYS_TREE_ROOT.getId().equals(pwCategory.getParent().getId())) {
                PwCategory p = new PwCategory(pwCategory.getId());
                PwCategory category = new PwCategory();
                category.setParent(p);
                model.addAttribute("canEditParent", pwCategoryService.findList(category).isEmpty());
            }
        }
        model.addAttribute("pwCategory", pwCategory);
        return "modules/pw/pwCategoryForm";
    }

    @RequiresPermissions("pw:pwCategory:view")
    @RequestMapping(value = "details")
    public String details(PwCategory pwCategory, Model model) {
        if (pwCategory.getParent() != null && StringUtil.isNotBlank(pwCategory.getParent().getId())) {
            pwCategory.setParent(pwCategoryService.get(pwCategory.getParent().getId()));
        }
        model.addAttribute("pwCategory", pwCategory);
        return "modules/pw/pwCategoryDetails";
    }


    @RequiresPermissions("pw:pwCategory:edit")
    @RequestMapping(value = "save")
    public String save(PwCategory pwCategory, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, pwCategory)) {
            return form(pwCategory, model);
        }
        try {
            pwCategoryService.save(pwCategory);
            addMessage(redirectAttributes, "保存资产类别成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwCategory/?repage";
    }

    @ResponseBody
    @RequestMapping(value = "asySave", method = RequestMethod.POST)
    public String save(PwCategory pwCategory) {
        Msg msg;
        try {
            pwCategoryService.save(pwCategory);
            msg = new Msg(true, pwCategory.getId());
        } catch (Exception e) {
            msg = new Msg(e.getMessage());
        }
        return msg.toJson();
    }

    @RequiresPermissions("pw:pwCategory:edit")
    @RequestMapping(value = "delete")
    public String delete(PwCategory pwCategory, RedirectAttributes redirectAttributes) {
        try {
            pwCategoryService.delete(pwCategory);
            addMessage(redirectAttributes, "删除资产类别成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwCategory/?repage";
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) Boolean isParent, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<PwCategory> list = pwCategoryService.findList(new PwCategory());
        for (int i = 0; i < list.size(); i++) {
            PwCategory e = list.get(i);
            if (StringUtil.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                if (isParent == null) {
                    map.put("isParent", false);
                } else {
                    map.put("isParent", isParent);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }


    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "pwCategoryTree")
    public List<Map<String, Object>> pwCategoryTree(@RequestParam(required = false) String extId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<String> parentIds = new ArrayList<String>();
        parentIds.add(SysIds.SYS_TREE_PROOT.getId());
        parentIds.add(SysIds.SYS_TREE_ROOT.getId());
        List<PwCategory> list = pwCategoryService.findListByParentIds(parentIds);
        for (int i = 0; i < list.size(); i++) {
            PwCategory e = list.get(i);
            if (StringUtil.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                map.put("isParent", false);
                map.put("prefix", e.getPwFassetsnoRule() != null ? e.getPwFassetsnoRule().getPrefix() : null);
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 根据固定资产的类别ID获取直接子类别
     *
     * @param categoryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/childrenCategory", method = RequestMethod.GET, produces = "application/json")
    public List<PwCategory> children(@RequestParam(required = false) String categoryId) {
        return SpaceUtils.findChildrenCategorys(categoryId);
    }


}