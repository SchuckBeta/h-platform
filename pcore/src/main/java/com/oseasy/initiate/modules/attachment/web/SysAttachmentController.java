package com.oseasy.initiate.modules.attachment.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.ftp.exceptions.FtpException;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.ftp.service.UeditorUploadService;

/**
 * 附件信息表Controller
 * @author zy
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/attachment/sysAttachment")
public class SysAttachmentController extends BaseController {
  @Autowired
  private UeditorUploadService ueditorUploadService;

	@Autowired
	private SysAttachmentService sysAttachmentService;

	@ModelAttribute
	public SysAttachment get(@RequestParam(required=false) String id) {
		SysAttachment entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = sysAttachmentService.get(id);
		}
		if (entity == null) {
			entity = new SysAttachment();
		}
		return entity;
	}

	@RequestMapping(value = {"list", ""})
	public String list(SysAttachment sysAttachment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysAttachment> page = sysAttachmentService.findPage(new Page<SysAttachment>(request, response), sysAttachment);
		model.addAttribute("page", page);
//		return "modules/sys/attachment/sysAttachmentList";
    return "modules/sys/attachment/sysAttachmentIconsGnode";
	}

	@RequiresPermissions("attachment:sysAttachment:view")
	@RequestMapping(value = "form")
	public String form(SysAttachment sysAttachment, Model model) {
		model.addAttribute("sysAttachment", sysAttachment);
		return "modules/sys/attachment/sysAttachmentForm";
	}

	@RequiresPermissions("attachment:sysAttachment:edit")
	@RequestMapping(value = "save")
	public String save(SysAttachment sysAttachment, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysAttachment)) {
			return form(sysAttachment, model);
		}
		sysAttachmentService.save(sysAttachment);
		addMessage(redirectAttributes, "保存附件信息表成功");
		return "redirect:"+Global.getAdminPath()+"/attachment/sysAttachment/?repage";
	}

	@RequiresPermissions("attachment:sysAttachment:edit")
	@RequestMapping(value = "delete")
	public String delete(SysAttachment sysAttachment, RedirectAttributes redirectAttributes) {
		sysAttachmentService.delete(sysAttachment);
		addMessage(redirectAttributes, "删除附件信息表成功");
		return "redirect:"+Global.getAdminPath()+"/attachment/sysAttachment/?repage";
	}


  @RequestMapping(value = {"/icons"})
  public String icons(SysAttachment sysAttachment, List<String> fsteps, List<FileStepEnum> fileSteps, HttpServletRequest request, HttpServletResponse response, Model model) {
    if((fsteps != null) && (fsteps.size() > 0)){
      sysAttachment.setFileSteps(fsteps);
    }

    if((fileSteps == null)){
      fileSteps = Lists.newArrayList();
    }

    Page<SysAttachment> page = sysAttachmentService.findPage(new Page<SysAttachment>(request, response), sysAttachment);
    model.addAttribute("page", page);
    model.addAttribute("fileSteps", fileSteps);
    return "modules/sys/attachment/sysAttachmentIcons";
  }

  @RequestMapping(value = {"/icons/gnode"})
  public String gnodeIcons(SysAttachment sysAttachment, String fstep, HttpServletRequest request, HttpServletResponse response, Model model) {
    /**
     * 查询条件.
     */
    sysAttachment.setType(FileTypeEnum.S_FLOW_ICON);
    List<String> fsteps = Lists.newArrayList();
    if(StringUtil.isNotEmpty(fstep)){
      fsteps.add(fstep);
    }else{
      fsteps.add(FileStepEnum.S_FLOW_NODELV1.getValue());
      fsteps.add(FileStepEnum.S_FLOW_NODELV2.getValue());
    }

    /**
     * 默认过滤条件.
     */
    List<FileStepEnum> fileSteps = Lists.newArrayList();
    fileSteps.add(FileStepEnum.S_FLOW_NODELV1);
    fileSteps.add(FileStepEnum.S_FLOW_NODELV2);
    return icons(sysAttachment, fsteps, fileSteps, request, response, model);
  }

  /**
   * 查询流程节点附件附件.
   * @param sysAttachment 查询条件
   * @param fstep 附件子类别
   * @param request
   * @param response
   * @param model
   * @return List
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxIcons/gnode"})
  public List<SysAttachment> ajaxIcons(SysAttachment sysAttachment, String fstep, HttpServletRequest request, HttpServletResponse response) {
    return sysAttachmentService.ajaxIcons(sysAttachment, fstep, request, response);
  }

  /**
   * 获取根据UID附件.
   * @param uid 业务ID
   * @param type 附件类型
   * @param fstep 附件子类别
   * @param fsteps 子类别集合，逗号分隔
   * @param gnodeId 节点ID
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxFiles/{uid}"})
  public ActYwRstatus<List<SysAttachment>> ajaxFiles(@PathVariable("uid") String uid, @RequestParam(required=true) String type, @RequestParam(required=false) String fstep, @RequestParam(required=false) String fsteps, @RequestParam(required=false) String gnodeId) {
    return sysAttachmentService.ajaxFiles(uid, type, fstep, fsteps, gnodeId);
  }

  /**
   * 异步上传附件.
   * 默认移动,传false表示不移动，需要手动移动
   * @param ftype 类型
   * @param fileStep 类别
   * @param uid 业务ID
   * @param isMove 是否移动
   * @param fileName 文件名
   * @param request 请求
   * @param model 模型
   * @return Rstatus
   * @throws IOException
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxUpload"})
  public ActYwRstatus<SysAttachment> ajaxUpload(@RequestParam(required=true) String ftype, @RequestParam(required=true) String fileStep, @RequestParam(required=false) String uid, @RequestParam(required=false) Boolean isMove, @RequestParam(required=false)String fileName, HttpServletRequest request, Model model) throws IOException {
    return sysAttachmentService.ajaxUpload(ftype, fileStep, uid, isMove, fileName, ueditorUploadService, request);
  }

  /**
   * 异步删除附件.
   * @param id 类型
   * @param fileUrl 附件地址
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxDelete/{id}"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<SysAttachment> ajaxDelete(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
    return sysAttachmentService.ajaxDelete(null, id, ueditorUploadService, request, response);
  }

  /**
   * 异步删除附件.
   * @param fileUrl 附件地址
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxDeleteByUrl"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<SysAttachment> ajaxDeleteByUrl(@RequestParam(required=true) String fileUrl, HttpServletRequest request, HttpServletResponse response) {
    return sysAttachmentService.ajaxDelete(fileUrl, null, ueditorUploadService, request, response);
  }

  /**
   * 异步移动附件.
   * @param id 类型
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = {"/ajaxMoveFtpFile/{id}"})
  public ActYwRstatus<SysAttachment> ajaxMoveFtpFile(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
    try {
      return sysAttachmentService.ajaxMoveFtpFile(id, request, response);
    } catch (FtpException e) {
      return new ActYwRstatus<SysAttachment>(false, "删除失败, 附件处理异常！");
    } catch (IOException e) {
      return new ActYwRstatus<SysAttachment>(false, "删除失败, 附件异常！");
    }
  }
}