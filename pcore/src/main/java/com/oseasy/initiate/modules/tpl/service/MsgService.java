package com.hch.platform.pcore.modules.tpl.service;

import com.hch.platform.putil.common.utils.FileUtil;
import com.hch.platform.pcore.common.utils.FreeMarkers;
import com.hch.platform.putil.common.utils.ObjectUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.utils.file.FileType;
import com.hch.platform.pcore.modules.actyw.tool.process.vo.FlowProjectType;
import com.hch.platform.pcore.modules.tpl.vo.IMparam;
import com.hch.platform.pcore.modules.tpl.vo.IWparam;
import com.hch.platform.pcore.modules.tpl.vo.MsgType;
import com.hch.platform.pcore.modules.tpl.vo.Wversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class MsgService {
  public static final Logger log = LoggerFactory.getLogger(MsgService.class);

  public String getClassPath(String path) {
    String clzzPath = this.getClass().getResource(FileUtil.LINE).getPath();
    return clzzPath+path;
  }

  public String getWebPath(String path) {
    String clzzPath = this.getClass().getResource(FileUtil.LINE).getPath();
    return clzzPath.replace(FileUtil.WEB_INF_CLASSES, "") + path;
  }

  /**
   * 模板渲染.
   * 只支持FTL模板，文件名不带后缀.
   * @param path 模板文件路径
   * @param param 参数
   */
  public String render(IMparam param){
      Map<String, Object> model = getDataModel(param);
      File file = new File(getWebPath(TplService.ROOT_MSG) + param.getTplFileName() + FileType.FT_FTL.getSuffer());
      try {
        String cotent = FileUtil.readFileToString(file);
        return FreeMarkers.renderString(cotent, model);
      } catch (IOException e) {
        log.warn("Msg 模板渲染失败！", e);
      }
    return null;
  }

  /**
   * 对模板参数特殊处理方法
   * @param param 模板参数
   * @return Map
   */
  private Map<String, Object> getDataModel(IMparam param) {
    return ObjectUtil.obj2Map(param);
  }
}
