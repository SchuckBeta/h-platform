package com.hch.platform.pcore.modules.pw.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.pw.entity.PwSpace;
import com.hch.platform.pcore.modules.pw.service.PwSpaceService;
import com.hch.platform.pcore.modules.pw.vo.PwSpaceType;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 设施Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwSpace")
public class PwSpaceController extends BaseController {

    @Autowired
    private PwSpaceService pwSpaceService;

    @ModelAttribute
    public PwSpace get(@RequestParam(required = false) String id) {
        PwSpace entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwSpaceService.get(id);
        }
        if (entity == null) {
            entity = new PwSpace();
        }
        return entity;
    }

    @RequiresPermissions("pw:pwSpace:view")
    @RequestMapping(value = {"list", ""})
    public String list(PwSpace pwSpace, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<PwSpace> list = pwSpaceService.findList(pwSpace);
        model.addAttribute("list", list);
        return "modules/pw/pwSpaceList";
    }


    @RequiresPermissions("pw:pwSpace:view")
    @RequestMapping(value = "form")
    public String form(PwSpace pwSpace, Model model, RedirectAttributes redirectAttributes) {
        if (pwSpace.getParent() != null && StringUtil.isNotBlank(pwSpace.getParent().getId())) {
            pwSpace.setParent(pwSpaceService.get(pwSpace.getParent().getId()));
            // 获取排序号，最末节点排序号+30
            if (StringUtil.isBlank(pwSpace.getId())) {
                PwSpace pwSpaceChild = new PwSpace();
                pwSpaceChild.setParent(new PwSpace(pwSpace.getParent().getId()));
                List<PwSpace> list = pwSpaceService.findList(pwSpace);
                if (list.size() > 0) {
                    pwSpace.setSort(list.get(list.size() - 1).getSort());
                    if (pwSpace.getSort() != null) {
                        pwSpace.setSort(pwSpace.getSort() + 30);
                    }
                }
            }
            /**
             * 获取所有父的信息
             */
            PwSpace space = pwSpace;
            while (space.getParent() != null) {
                space = space.getParent();
                if (space == null || SysIds.SYS_TREE_ROOT.getId().equals(space.getId())) {
                    break;
                }
                if (StringUtils.isBlank(space.getType()) || StringUtils.isBlank(space.getName())) {
                    space = pwSpaceService.get(space.getId());
                }
                searchToTop(model, space);
            }
        }
        if (pwSpace.getSort() == null) {
            pwSpace.setSort(30);
        }
        if (PwSpaceType.CAMPUS.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceCampusForm";
        } else if (PwSpaceType.BASE.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceBaseForm";
        } else if (PwSpaceType.BUILDING.getValue().equals(pwSpace.getType())) {
            /**
             * 检查楼栋数是否可编辑
             * 有楼层不可编辑
             */
            if (StringUtils.isBlank(pwSpace.getId())) {
                model.addAttribute("bEmpty", "y");
            } else {
                PwSpace s = new PwSpace();
                PwSpace p = new PwSpace(pwSpace.getId());
                s.setParent(p);
                String empty = pwSpaceService.findList(s).isEmpty() ? "y" : "n";
                model.addAttribute("bEmpty", empty);
            }
            return "modules/pw/pwSpaceBuildingForm";
        } else if (PwSpaceType.FLOOR.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceFloorForm";
        } else {
            return "modules/pw/pwSchoolForm";
        }
    }


    @RequiresPermissions("pw:pwSpace:view")
    @RequestMapping(value = "details")
    public String details(PwSpace pwSpace, Model model, RedirectAttributes redirectAttributes) {
        if (pwSpace.getParent() != null && StringUtil.isNotBlank(pwSpace.getParent().getId())) {
            pwSpace.setParent(pwSpaceService.get(pwSpace.getParent().getId()));
            // 获取排序号，最末节点排序号+30
            if (StringUtil.isBlank(pwSpace.getId())) {
                PwSpace pwSpaceChild = new PwSpace();
                pwSpaceChild.setParent(new PwSpace(pwSpace.getParent().getId()));
                List<PwSpace> list = pwSpaceService.findList(pwSpace);
                if (list.size() > 0) {
                    pwSpace.setSort(list.get(list.size() - 1).getSort());
                    if (pwSpace.getSort() != null) {
                        pwSpace.setSort(pwSpace.getSort() + 30);
                    }
                }
            }
            /**
             * 获取所有父的信息
             */
            PwSpace space = pwSpace;
            while (space.getParent() != null) {
                space = space.getParent();
                if (space == null || SysIds.SYS_TREE_ROOT.getId().equals(space.getId())) {
                    break;
                }
                if (StringUtils.isBlank(space.getType()) || StringUtils.isBlank(space.getName())) {
                    space = pwSpaceService.get(space.getId());
                }
                searchToTop(model, space);
            }
        }
        if (pwSpace.getSort() == null) {
            pwSpace.setSort(30);
        }
        if (PwSpaceType.CAMPUS.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceCampusDetails";
        } else if (PwSpaceType.BASE.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceBaseDetails";
        } else if (PwSpaceType.BUILDING.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceBuildingDetails";
        } else if (PwSpaceType.FLOOR.getValue().equals(pwSpace.getType())) {
            return "modules/pw/pwSpaceFloorDetails";
        } else {
            return "modules/pw/pwSchoolDetails";
        }
    }

    private void searchToTop(Model model, PwSpace pwSpace) {
        if (PwSpaceType.SCHOOL.getValue().equals(pwSpace.getType())) {
            model.addAttribute("school", pwSpace.getName());
        } else if (PwSpaceType.CAMPUS.getValue().equals(pwSpace.getType())) {
            model.addAttribute("campus", pwSpace.getName());
        } else if (PwSpaceType.BASE.getValue().equals(pwSpace.getType())) {
            model.addAttribute("base", pwSpace.getName());
        } else if (PwSpaceType.BUILDING.getValue().equals(pwSpace.getType())) {
            model.addAttribute("building", pwSpace.getName());
        } else {
            model.addAttribute("school", pwSpace.getName());
        }
    }

    @RequiresPermissions("pw:pwSpace:edit")
    @RequestMapping(value = "save")
    public String save(PwSpace pwSpace, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!beanValidator(model, pwSpace)) {
                return form(pwSpace, model, redirectAttributes);
            }
            pwSpaceService.save(pwSpace);
            addMessage(redirectAttributes, "保存设施成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwSpace/?repage";
    }

    @RequiresPermissions("pw:pwSpace:edit")
    @RequestMapping(value = "delete")
    public String delete(PwSpace pwSpace, RedirectAttributes redirectAttributes) {
        try {
            pwSpaceService.delete(pwSpace);
            addMessage(redirectAttributes, "删除设施成功");
        } catch (Exception e) {
            addMessage(redirectAttributes, e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/pw/pwSpace/?repage";
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<PwSpace> list = pwSpaceService.findList(new PwSpace());
        for (int i = 0; i < list.size(); i++) {
            PwSpace e = list.get(i);
            if (StringUtil.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                map.put("type", e.getType());
                mapList.add(map);
            }
        }
        return mapList;
    }


    @RequiresPermissions("pw:pwSpace:view")
    @ResponseBody
    @RequestMapping(value = "jsonList")
    public List<PwSpace> list(PwSpace pwSpace) {
        return pwSpaceService.findList(pwSpace);
    }

    @RequiresPermissions("pw:pwSpace:view")
    @ResponseBody
    @RequestMapping(value = "children/{id}")
    public List<PwSpace> findChildren(@PathVariable String id) {
        return pwSpaceService.findChildren(id);
    }


    @RequiresPermissions("pw:pwSpace:view")
    @RequestMapping(value = "designForm")
    public String designForm(PwSpace pwSpace, Model model) {
        return "modules/pw/pwFloorPlan";
    }
}