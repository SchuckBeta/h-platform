package com.hch.platform.pcore.modules.blank;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.modules.act.entity.Act;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangzheng on 2017/4/8.
 */
@Controller
@RequestMapping(value = "${adminPath}/blank")
public class BlankController {
    @RequestMapping(value = "")
    public String blank(Model model) {
        model.addAttribute("msg","该模块还在建设中");
        return "error/msg";
    }
}
