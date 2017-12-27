package com.hch.platform.pcore.modules.pw.web;

import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.pw.entity.PwSpace;
import com.hch.platform.pcore.modules.pw.service.PwSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "${frontPath}/pw/pwSpace")
public class FrontPwSpaceController extends BaseController {

    @Autowired
    private PwSpaceService pwSpaceService;

    @ResponseBody
    @RequestMapping(value = "jsonList")
    public List<PwSpace> list(PwSpace pwSpace) {
        return pwSpaceService.findList(pwSpace);
    }

    @ResponseBody
    @RequestMapping(value = "children/{id}")
    public List<PwSpace> findChildren(@PathVariable String id) {
        return pwSpaceService.findChildren(id);
    }
}
