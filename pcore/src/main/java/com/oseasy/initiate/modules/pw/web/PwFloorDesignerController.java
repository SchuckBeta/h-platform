package com.oseasy.initiate.modules.pw.web;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.exception.ExceptionUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.pw.entity.PwFloorDesigner;
import com.oseasy.initiate.modules.pw.entity.PwFloorRooms;
import com.oseasy.initiate.modules.pw.service.PwFloorDesignerService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 楼层设计Controller.
 *
 * @author 章传胜
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwFloorDesigner")
public class PwFloorDesignerController extends BaseController {

    @Autowired
    private PwFloorDesignerService pwFloorDesignerService;


    @ModelAttribute
    public PwFloorDesigner get(@RequestParam(required = false) String id) {
        PwFloorDesigner entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = pwFloorDesignerService.get(id);
        }
        if (entity == null) {
            entity = new PwFloorDesigner();
        }
        return entity;
    }


    @RequestMapping(value = {"list"}, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String list(@RequestParam(required = false) String floorId, HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("floorId", floorId);
        return "modules/pw/pwFloorPlan";
    }


    /**
     * 根据楼层ID获取该楼层房间以及房间内元素信息。
     */
    @RequestMapping(value = {"floorDesDates"}, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public PwFloorRooms floorDesDates(@RequestParam(required = false) String floorId, HttpServletRequest request, HttpServletResponse response, Model model) {
        PwFloorDesigner pwFloorDesigner = new PwFloorDesigner();
        pwFloorDesigner.setFloorId(floorId);
        List<PwFloorDesigner> list = pwFloorDesignerService.findList(pwFloorDesigner);
        PwFloorRooms pwFloorRooms = new PwFloorRooms();
        pwFloorRooms.setRooms(list);
        pwFloorRooms.setFloorId(floorId);
        return pwFloorRooms;
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public JSONObject save(@RequestBody JSONObject param) {
        JSONObject result = new JSONObject();
        try {
            Map classMap = new HashMap();
            classMap.put("rooms",PwFloorDesigner.class);
            PwFloorRooms pwFloorRooms = (PwFloorRooms) JSONObject.toBean(param, PwFloorRooms.class,classMap);
            List<PwFloorDesigner> list = pwFloorRooms.getRooms();
            int res = pwFloorDesignerService.insertAll(list);
            result.put("status", "true");
            result.put("msg", "请求成功");
        } catch (Exception e) {
            result.put("status", "false");
            result.put("msg", ExceptionUtil.getStackTrace(e));
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }


    @RequestMapping(value = {"pwFloorPlan"})
    public String show() {
        return "modules/pw/pwFloorTree";
    }


}