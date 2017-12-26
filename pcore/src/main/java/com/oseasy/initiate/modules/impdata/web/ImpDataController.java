package com.hch.platform.pcore.modules.impdata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfo;
import com.hch.platform.pcore.modules.impdata.enums.BackUserColEnum;
import com.hch.platform.pcore.modules.impdata.enums.GcontestColEnum;
import com.hch.platform.pcore.modules.impdata.enums.OfficeColEnum;
import com.hch.platform.pcore.modules.impdata.enums.ProjectColEnum;
import com.hch.platform.pcore.modules.impdata.enums.ProjectHsColEnum;
import com.hch.platform.pcore.modules.impdata.enums.ProjectMdApprovalColEnum;
import com.hch.platform.pcore.modules.impdata.enums.ProjectMdCloseColEnum;
import com.hch.platform.pcore.modules.impdata.enums.ProjectMdMidColEnum;
import com.hch.platform.pcore.modules.impdata.enums.StudentColEnum;
import com.hch.platform.pcore.modules.impdata.enums.TeacherColEnum;
import com.hch.platform.pcore.modules.impdata.exception.ImpDataException;
import com.hch.platform.pcore.modules.impdata.service.BackUserErrorService;
import com.hch.platform.pcore.modules.impdata.service.GcontestErrorService;
import com.hch.platform.pcore.modules.impdata.service.ImpDataService;
import com.hch.platform.pcore.modules.impdata.service.ImpInfoErrmsgService;
import com.hch.platform.pcore.modules.impdata.service.ImpInfoService;
import com.hch.platform.pcore.modules.impdata.service.OfficeErrorService;
import com.hch.platform.pcore.modules.impdata.service.ProMdApprovalErrorService;
import com.hch.platform.pcore.modules.impdata.service.ProMdCloseErrorService;
import com.hch.platform.pcore.modules.impdata.service.ProMdMidErrorService;
import com.hch.platform.pcore.modules.impdata.service.ProjectErrorService;
import com.hch.platform.pcore.modules.impdata.service.ProjectHsErrorService;
import com.hch.platform.pcore.modules.impdata.service.StudentErrorService;
import com.hch.platform.pcore.modules.impdata.service.TeacherErrorService;
import com.hch.platform.pcore.modules.proprojectmd.service.ImpExpService;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 导入数据Controller
 *
 * @author 9527
 * @version 2017-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/impdata")
public class ImpDataController extends BaseController {
	public static Logger logger = Logger.getLogger(ImpDataController.class);
	@Autowired
	private ImpDataService impDataService;
	@Autowired
	private ImpInfoService impInfoService;
	@Autowired
	private StudentErrorService studentErrorService;
	@Autowired
	private TeacherErrorService teacherErrorService;
	@Autowired
	private BackUserErrorService backUserErrorService;
	@Autowired
	private OfficeErrorService officeErrorService;
	@Autowired
	private ProjectErrorService projectErrorService;
	@Autowired
	private ImpInfoErrmsgService impInfoErrmsgService;
	@Autowired
	private ProMdApprovalErrorService proMdApprovalErrorService;
	@Autowired
	private ProMdMidErrorService proMdMidErrorService;
	@Autowired
	private ProMdCloseErrorService proMdCloseErrorService;
	@Autowired
	private ProjectHsErrorService projectHsErrorService;
	@Autowired
	private GcontestErrorService gcontestErrorService;
	public static int descHeadRow=3;//Excel文件说明部分行数
	@ModelAttribute
	public ImpInfo get(@RequestParam(required=false) String id) {
		ImpInfo entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = impInfoService.get(id);
		}
		if (entity == null) {
			entity = new ImpInfo();
		}
		return entity;
	}
	@RequestMapping(value = { "list", "" })
	public String list(ImpInfo impInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> param = new HashMap<String, Object>();
		AbsUser user = UserUtils.getUser();
		param.put("userid", user.getId());
		Page<Map<String, String>> page = impInfoService.getList(new Page<Map<String, String>>(request, response),
				param);
		model.addAttribute("page", page);
		return "modules/impdata/impdataList";
	}
	@RequestMapping(value = { "mdlist"})
	public String mdlist(ImpInfo impInfo,String type,String referrer, HttpServletRequest request, HttpServletResponse response, Model model) {
		String reqreferer = referrer;
		if(StringUtil.isEmpty(referrer)){
			reqreferer=request.getHeader("referer");
		}else{
			try {
				reqreferer=URLDecoder.decode(StringEscapeUtils.unescapeHtml4(reqreferer), "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("type", type);
		Page<Map<String, String>> page = impInfoService.getMdList(new Page<Map<String, String>>(request, response),
				param);
		model.addAttribute("page", page);
		model.addAttribute("type", type);
		model.addAttribute("referrer", reqreferer);
		try {
			model.addAttribute("encodereferrer", URLEncoder.encode(URLEncoder.encode(reqreferer, "utf-8"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return "modules/proprojectmd/impdataList";
	}
	@RequestMapping(value = "delete")
	public String delete(ImpInfo impInfo,String referrer, RedirectAttributes redirectAttributes) {
		impInfoService.delete(impInfo);
		addMessage(redirectAttributes, "删除成功");
		String type=impInfo.getImpTpye();
		if (ImpDataService.impMdAppr.equals(type)
				||ImpDataService.impMdMid.equals(type)
				||ImpDataService.impMdClose.equals(type)) {
			return "redirect:" + Global.getAdminPath() + "/impdata/mdlist/?repage&type="+type+"&referrer="+referrer;
		}else {
			return "redirect:" + Global.getAdminPath() + "/impdata/?repage";
		}
	}
	@RequestMapping(value = "getImpInfo")
	@ResponseBody
	public ImpInfo getImpInfo(HttpServletRequest request, HttpServletResponse response) {
		String id=request.getParameter("id");
		return impInfoService.getImpInfo(id);
	}
	/*民大项目数据导入*/
	@RequestMapping(value = "importMdData")
	@ResponseBody
	@RequiresPermissions("sys:user:import")
	public JSONObject importMdData(HttpServletRequest request, HttpServletResponse response) {
		JSONObject obj = new JSONObject();
		String type=request.getParameter("type");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 读取上传的文件内容
		List<MultipartFile> imgFile1 = multipartRequest.getFiles("fileName");
		if (imgFile1 == null||imgFile1.size()==0) {
			obj.put("ret", "0");
			obj.put("msg", "上传失败");
			return obj;
		} else {
			impDataService.importMdData(type,imgFile1, request);
			obj.put("ret", "1");
			obj.put("msg", "上传成功");
		}
		return obj;
	}
	/*数据导入*/
	@RequestMapping(value = "importData")
	@ResponseBody
	@RequiresPermissions("sys:user:import")
	public JSONObject importData(HttpServletRequest request, HttpServletResponse response) {
		JSONObject obj = new JSONObject();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 读取上传的文件内容
		MultipartFile imgFile1 = multipartRequest.getFile("fileName");
		if (imgFile1 == null) {
			obj.put("ret", "0");
			obj.put("msg", "上传失败");
			return obj;
		} else {
			try {
				impDataService.importData(imgFile1, request);
			}catch(ImpDataException w) {
				obj.put("ret", "0");
				obj.put("msg", w.getMessage());
				logger.error("导入出错", w);
				return obj;
			}catch (POIXMLException e) {
				obj.put("ret", "0");
				obj.put("msg", "请选择正确的文件");
				logger.error("导入出错", e);
				return obj;
			}catch (Exception e) {
				obj.put("ret", "0");
				obj.put("msg", "导入出错");
				logger.error("导入出错", e);
				return obj;
			}
			obj.put("ret", "1");
			obj.put("msg", "上传成功");
		}
		return obj;
	}
	/*下载模板*/
	@RequestMapping(value = "downTemplate")
	public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			out= response.getOutputStream();

			// excel模板路径
			String fileName = "学生信息导入.xlsx";
			String fileName2 = "student_data_template.xlsx";
			String type=request.getParameter("type");
			if (ImpDataService.impStu.equals(type)) {
				fileName = "学生信息导入.xlsx";
				fileName2 = "student_data_template.xlsx";
			}else if (ImpDataService.impTea.equals(type)) {
				fileName = "导师信息导入.xlsx";
				fileName2 = "teacher_data_template.xlsx";
			}else if (ImpDataService.impBackUser.equals(type)) {
				fileName = "后台用户信息导入.xlsx";
				fileName2 = "backuser_data_template.xlsx";
			}else if (ImpDataService.impOrg.equals(type)) {
				fileName = "机构信息导入.xlsx";
				fileName2 = "org_data_template.xlsx";
			}else if (ImpDataService.impProject.equals(type)) {
				fileName = "项目信息导入.xlsx";
				fileName2 = "project_data_template.xlsx";
			}else if (ImpDataService.impProjectHs.equals(type)) {
				fileName = "项目信息导入.xlsx";
				fileName2 = "project_hs_data_template.xlsx";
			}else if (ImpDataService.impGcontest.equals(type)) {
				fileName = "互联网+大赛信息导入.xlsx";
				fileName2 = "gcontest_data_template.xlsx";
			}
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + fileName2);
			fs = new FileInputStream(fi);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			if (ImpDataService.impStu.equals(type)) {
				setStudentExcelHead(sheet);
			}else if (ImpDataService.impTea.equals(type)) {
				setTeacherExcelHead(sheet);
			}else if (ImpDataService.impBackUser.equals(type)) {
				setBackUserExcelHead(sheet);
			}else if (ImpDataService.impOrg.equals(type)) {
				setOrgExcelHead(sheet);
			}else if (ImpDataService.impProject.equals(type)) {
				setProjectExcelHead(sheet);
			}else if (ImpDataService.impProjectHs.equals(type)) {
				setProjectHsExcelHead(sheet);
			}else if (ImpDataService.impGcontest.equals(type)) {
				setGcontestExcelHead(sheet);
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	/*错误数据的导出*/
	@RequestMapping(value = "expData")
	public void expData(HttpServletRequest request, HttpServletResponse response) {
		String id=request.getParameter("id");
		String type=request.getParameter("type");
		if (ImpDataService.impStu.equals(type)) {
			studentExp(id,request, response);
		}else if (ImpDataService.impTea.equals(type)) {
			teacherExp(id, request, response);
		}else if (ImpDataService.impBackUser.equals(type)) {
			backUserExp(id, request, response);
		}else if (ImpDataService.impOrg.equals(type)) {
			orgExp(id, request, response);
		}else if (ImpDataService.impProject.equals(type)) {
			projectExp(id, request, response);
		}else if (ImpDataService.impMdAppr.equals(type)) {
			projectMdApprovalExp(id, request, response);
		}else if (ImpDataService.impMdMid.equals(type)) {
			projectMdMidExp(id, request, response);
		}else if (ImpDataService.impMdClose.equals(type)) {
			projectMdCloseExp(id, request, response);
		}else if (ImpDataService.impProjectHs.equals(type)) {
			projectHsExp(id, request, response);
		}else if (ImpDataService.impGcontest.equals(type)) {
			gcontestExp(id, request, response);
		}
	}
	private void setStudentExcelHead(XSSFSheet sheet) {
		String headDesc = "填写数据说明：红色名称为必填信息。日期格式举例2017-05-19；擅长技术领域为多选信息，若有多个则用英文输入法逗号分隔;\r\n";
		List<Dict> list=DictUtils.getDictList("technology_field");
		List<String> temlist = new ArrayList<String>();
		for(Dict d:list) {
			temlist.add(d.getLabel());
		}
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString(headDesc+"擅长技术领域可选值有:"+StringUtil.join(temlist.toArray(), ",")));
	}
	private void setTeacherExcelHead(XSSFSheet sheet) {
		StringBuffer headDesc = new StringBuffer("填写数据说明：红色名称为必填信息。日期格式举例2017-05-19；擅长技术领域、服务意向为多选信息，若有多个则用英文输入法逗号分隔;\r\n");
		List<Dict> list=DictUtils.getDictList("technology_field");
		List<String> temlist = new ArrayList<String>();
		for(Dict d:list) {
			temlist.add(d.getLabel());
		}
		headDesc.append("擅长技术领域可选值有:"+StringUtil.join(temlist.toArray(), ","));
		list=DictUtils.getDictList("master_help");
		temlist = new ArrayList<String>();
		for(Dict d:list) {
			temlist.add(d.getLabel());
		}
		headDesc.append("\r\n服务意向可选值有:"+StringUtil.join(temlist.toArray(), ","));
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString(headDesc.toString()));
	}
	private void setBackUserExcelHead(XSSFSheet sheet) {
		String headDesc = "填写数据说明：红色名称为必填信息。日期格式举例2017-05-19；擅长技术领域和角色为多选信息，若有多个则用英文输入法逗号分隔;\r\n";
		List<Dict> list=DictUtils.getDictList("technology_field");
		List<String> temlist = new ArrayList<String>();
		for(Dict d:list) {
			temlist.add(d.getLabel());
		}
		List<Role> list2=UserUtils.getRoleList();
		List<String> temlist2 = new ArrayList<String>();
		for(Role d:list2) {
			temlist2.add(d.getName());
		}
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString(headDesc
				+"擅长技术领域可选值有:"+StringUtil.join(temlist.toArray(), ",")+"\r\n"
				+"角色可选值有:"+StringUtil.join(temlist2.toArray(), ",")+"\r\n"));
	}
	private void setOrgExcelHead(XSSFSheet sheet) {
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString("填写数据说明：导入学院只需要填写学院，导入专业则需要填写该专业所属学院"));
	}
	private void setProjectExcelHead(XSSFSheet sheet) {
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString("填写数据说明：红色名称为必填信息。立项年份举例：2016。\r\n项目其他成员信息：姓名/学号,姓名/学号；举例：刘家胜/2014210163,郭力/2014210132;以英文输入法逗号分隔"));
	}
	private void setProjectHsExcelHead(XSSFSheet sheet) {
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString("填写数据说明：红色名称为必填信息。年级示例：2016。\r\n团队成员及学号示例：韩浩2015211240,马翔宇2014211510,于欣彤2015211927,游漫 2016211182;以英文输入法逗号分隔\r\n指导教师姓名、工号、职称多个输入以中文顿号分隔;示例：李名峰、王涛"));
	}
	private void setGcontestExcelHead(XSSFSheet sheet) {
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString("填写数据说明：红色名称为必填信息。\r\n申报人/学号示例：韩浩/2015211240\r\n校内导师/工号和企业导师/工号多个输入以中文顿号分隔;示例：韩浩/2015211240、韩浩/2015211240"));
	}
	private void setMdApprovalExcelHead(ImpInfo ii,XSSFSheet sheet,String sindx) {
		if(ii!=null){
			JSONObject js=JSONObject.fromObject(ii.getMsg());
			sheet.getRow(0).getCell(0).setCellValue(js.getString("title"+sindx));
			sheet.getRow(1).getCell(0).setCellValue(js.getString("oname"));
		}
	}
	private void setMdMidExcelHead(ImpInfo ii,XSSFSheet sheet) {
		if(ii!=null){
			JSONObject js=JSONObject.fromObject(ii.getMsg());
			sheet.getRow(0).getCell(0).setCellValue(js.getString("title0"));
			sheet.getRow(1).getCell(0).setCellValue(js.getString("oname"));
		}
	}
	private void setMdCloseExcelHead(ImpInfo ii,XSSFSheet sheet) {
		if(ii!=null){
			JSONObject js=JSONObject.fromObject(ii.getMsg());
			sheet.getRow(0).getCell(0).setCellValue(js.getString("title0"));
			sheet.getRow(1).getCell(0).setCellValue(js.getString("oname"));
		}
	}
	private void studentExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "学生信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "student_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=studentErrorService.getListByImpId(id);
			setStudentExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=StudentColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void teacherExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "导师信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "teacher_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=teacherErrorService.getListByImpId(id);
			setTeacherExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=TeacherColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void backUserExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "后台用户信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "backuser_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=backUserErrorService.getListByImpId(id);
			setBackUserExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=BackUserColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void orgExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "机构信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "org_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=officeErrorService.getListByImpId(id);
			setOrgExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=OfficeColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void projectHsExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "项目信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "project_hs_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=projectHsErrorService.getListByImpId(id);
			setProjectHsExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=ProjectHsColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		}	catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void gcontestExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "互联网+大赛信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "gcontest_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=gcontestErrorService.getListByImpId(id);
			setGcontestExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=GcontestColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		}	catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void projectExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "项目信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "project_data_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			CreationHelper factory = wb.getCreationHelper();
			ClientAnchor anchor = factory.createClientAnchor();
			Drawing drawing = sheet.createDrawingPatriarch();
			List<Map<String, String>> list=projectErrorService.getListByImpId(id);
			setProjectExcelHead(sheet);
			if (!list.isEmpty()) {
				Map<String, Integer> rowIndex=new HashMap<String, Integer>();
				// 在相应的单元格进行赋值
				for(int i=0;i<list.size();i++) {
					Map<String, String> map=list.get(i);
					XSSFRow row=sheet.createRow(i+1+descHeadRow);
					rowIndex.put(map.get("id"), i+1+descHeadRow);
					for(String key:map.keySet()) {
						Integer cindex=ProjectColEnum.getValueByName(key);
						if (cindex!=null) {
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}
					}
				}
				List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
				if (!errlist.isEmpty()) {
					for(Map<String, String> errmap:errlist) {
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void projectMdApprovalExpSheet(XSSFWorkbook wb,String sheetindx,String id){
		XSSFSheet sheet = wb.getSheetAt(Integer.parseInt(sheetindx));

		XSSFCellStyle rowStyle = wb.createCellStyle();
		rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
		rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
		rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
		rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框

		CreationHelper factory = wb.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		Drawing drawing = sheet.createDrawingPatriarch();
		List<Map<String, String>> list=proMdApprovalErrorService.getListByImpId(id,sheetindx);
		ImpInfo ii=impInfoService.get(id);
		setMdApprovalExcelHead(ii,sheet,sheetindx);
		int row0=ImpExpService.approval_sheet0_head+2;
		int row1=ImpExpService.approval_sheet1_head+2;
		Map<String, Integer> rowIndex=null;
		if (!list.isEmpty()) {
			rowIndex=new HashMap<String, Integer>();
			int rowh=0;
			if("0".equals(sheetindx)){
				rowh=ImpExpService.approval_sheet0_head+2;
			}else if("1".equals(sheetindx)){
				rowh=ImpExpService.approval_sheet1_head+2;
			}
			// 在相应的单元格进行赋值
			for(int i=0;i<list.size();i++) {
				int rowinx=0;
				if("0".equals(sheetindx)){
					rowinx=row0-ImpExpService.approval_sheet0_head-1;
					row0++;
				}else if("1".equals(sheetindx)){
					rowinx=row1-ImpExpService.approval_sheet1_head-1;
					row1++;
				}
				Map<String, String> map=list.get(i);
				XSSFRow row=sheet.createRow(i+rowh);
				XSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(rowinx+"");
				rowIndex.put(map.get("id"), i+4);
				for(String key:map.keySet()) {
					Integer cindex=ProjectMdApprovalColEnum.getValueByName(key);
					if (cindex!=null) {
						if(cindex!=8){
							XSSFCell cell = row.createCell(cindex);
							cell.setCellValue(map.get(key));
						}else{
							if("A".equals(map.get(key))){
								XSSFCell cell = row.createCell(8);
								cell.setCellValue("√");
							}else if("B".equals(map.get(key))){
								XSSFCell cell = row.createCell(9);
								cell.setCellValue("√");
							}else if("C".equals(map.get(key))){
								XSSFCell cell = row.createCell(10);
								cell.setCellValue("√");
							}
						}
					}
				}
				//设置样式
				for(int m=0;m<=23;m++){
					if(row.getCell(m)==null){
						row.createCell(m).setCellStyle(rowStyle);
					}else{
						row.getCell(m).setCellStyle(rowStyle);
					}
				}
			}
			List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
			if (!errlist.isEmpty()) {
				for(Map<String, String> errmap:errlist) {
					Integer ri=rowIndex.get(errmap.get("data_id"));
					if(ri!=null){
						Comment comment0 = drawing.createCellComment(anchor);
						RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
						XSSFFont commentFormatter = wb.createFont();
						str0.applyFont(commentFormatter);
						comment0.setString(str0);
						if (sheet.getRow(ri).getCell(Integer.parseInt(errmap.get("colname")))==null) {
							sheet.getRow(ri).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}else{
							sheet.getRow(ri).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
						}
					}
				}
			}
		}
		//尾部
		XSSFCellStyle cellStyle = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("仿宋_GB2312");
		font.setFontHeightInPoints((short) 14);//设置字体大小
		cellStyle.setFont(font);
		if("0".equals(sheetindx)){
			if(ii!=null){
				JSONObject js=JSONObject.fromObject(ii.getMsg());
				sheet.createRow(row0).createCell(0).setCellValue(js.getString("tel0"));
				sheet.getRow(row0).getCell(0).setCellStyle(cellStyle);
			}
			for(int k=1;k<ImpExpService.approval_sheet0_foot.length;k++){
				sheet.createRow(row0+k).createCell(0).setCellValue(ImpExpService.approval_sheet0_foot[k]);
			}
		}else if("1".equals(sheetindx)){
			if(ii!=null){
				JSONObject js=JSONObject.fromObject(ii.getMsg());
				sheet.createRow(row1).createCell(0).setCellValue(js.getString("tel1"));
				sheet.getRow(row1).getCell(0).setCellStyle(cellStyle);
			}
			for(int k=1;k<ImpExpService.approval_sheet1_foot.length;k++){
				sheet.createRow(row1+k).createCell(0).setCellValue(ImpExpService.approval_sheet1_foot[k]);
			}
		}
	}
	private void projectMdApprovalExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "立项信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_approval_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			projectMdApprovalExpSheet(wb, "0",id);
			projectMdApprovalExpSheet(wb, "1",id);
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void projectMdMidExpSheet(XSSFWorkbook wb,String id){
		XSSFSheet sheet = wb.getSheetAt(0);

		XSSFCellStyle rowStyle = wb.createCellStyle();
		rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
		rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
		rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
		rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框

		CreationHelper factory = wb.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		Drawing drawing = sheet.createDrawingPatriarch();
		List<Map<String, String>> list=proMdMidErrorService.getListByImpId(id);
		ImpInfo ii=impInfoService.get(id);
		setMdMidExcelHead(ii,sheet);
		int row0=ImpExpService.mid_sheet0_head+2;
		Map<String, Integer> rowIndex=null;
		if (!list.isEmpty()) {
			rowIndex=new HashMap<String, Integer>();
			int rowh=ImpExpService.mid_sheet0_head+2;
			// 在相应的单元格进行赋值
			for(int i=0;i<list.size();i++) {
				int rowinx=row0-ImpExpService.mid_sheet0_head-1;
				row0++;
				Map<String, String> map=list.get(i);
				XSSFRow row=sheet.createRow(i+rowh);
				XSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(rowinx+"");
				rowIndex.put(map.get("id"), i+4);
				for(String key:map.keySet()) {
					Integer cindex=ProjectMdMidColEnum.getValueByName(key);
					if (cindex!=null) {
						XSSFCell cell = row.createCell(cindex);
						cell.setCellValue(map.get(key));
					}
				}
				//设置样式
				for(int m=0;m<=11;m++){
					if(row.getCell(m)==null){
						row.createCell(m).setCellStyle(rowStyle);
					}else{
						row.getCell(m).setCellStyle(rowStyle);
					}
				}
			}
			List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
			if (!errlist.isEmpty()) {
				for(Map<String, String> errmap:errlist) {
					Comment comment0 = drawing.createCellComment(anchor);
					RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
					XSSFFont commentFormatter = wb.createFont();
					str0.applyFont(commentFormatter);
					comment0.setString(str0);
					if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
						sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
					}else{
						sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
					}
				}
			}
		}
		//尾部
		XSSFCellStyle cellStyle = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("仿宋_GB2312");
		font.setFontHeightInPoints((short) 14);//设置字体大小
		cellStyle.setFont(font);
		if(ii!=null){
			JSONObject js=JSONObject.fromObject(ii.getMsg());
			sheet.createRow(row0).createCell(0).setCellValue(js.getString("tel0"));
			sheet.getRow(row0).getCell(0).setCellStyle(cellStyle);
		}
		for(int k=1;k<ImpExpService.mid_sheet0_foot.length;k++){
			sheet.createRow(row0+k).createCell(0).setCellValue(ImpExpService.mid_sheet0_foot[k]);
		}
	}
	private void projectMdMidExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "中期检查信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_mid_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			projectMdMidExpSheet(wb,id);
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	private void projectMdCloseExpSheet(XSSFWorkbook wb,String id){
		XSSFSheet sheet = wb.getSheetAt(0);

		XSSFCellStyle rowStyle = wb.createCellStyle();
		rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
		rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
		rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
		rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框

		CreationHelper factory = wb.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		Drawing drawing = sheet.createDrawingPatriarch();
		List<Map<String, String>> list=proMdCloseErrorService.getListByImpId(id);
		ImpInfo ii=impInfoService.get(id);
		setMdCloseExcelHead(ii,sheet);
		int row0=ImpExpService.close_sheet0_head+2;
		Map<String, Integer> rowIndex=null;
		if (!list.isEmpty()) {
			rowIndex=new HashMap<String, Integer>();
			int rowh=ImpExpService.close_sheet0_head+2;
			// 在相应的单元格进行赋值
			for(int i=0;i<list.size();i++) {
				int rowinx=row0-ImpExpService.close_sheet0_head-1;
				row0++;
				Map<String, String> map=list.get(i);
				XSSFRow row=sheet.createRow(i+rowh);
				XSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(rowinx+"");
				rowIndex.put(map.get("id"), i+4);
				for(String key:map.keySet()) {
					Integer cindex=ProjectMdCloseColEnum.getValueByName(key);
					if (cindex!=null) {
						XSSFCell cell = row.createCell(cindex);
						cell.setCellValue(map.get(key));
					}
				}
				//设置样式
				for(int m=0;m<=14;m++){
					if(row.getCell(m)==null){
						row.createCell(m).setCellStyle(rowStyle);
					}else{
						row.getCell(m).setCellStyle(rowStyle);
					}
				}
			}
			List<Map<String, String>> errlist=impInfoErrmsgService.getListByImpId(id);
			if (!errlist.isEmpty()) {
				for(Map<String, String> errmap:errlist) {
					Comment comment0 = drawing.createCellComment(anchor);
					RichTextString str0 = factory.createRichTextString(errmap.get("errmsg"));
					XSSFFont commentFormatter = wb.createFont();
					str0.applyFont(commentFormatter);
					comment0.setString(str0);
					if (sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname")))==null) {
						sheet.getRow(rowIndex.get(errmap.get("data_id"))).createCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
					}else{
						sheet.getRow(rowIndex.get(errmap.get("data_id"))).getCell(Integer.parseInt(errmap.get("colname"))).setCellComment(comment0);
					}
				}
			}
		}
		//尾部
		XSSFCellStyle cellStyle = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("仿宋_GB2312");
		font.setFontHeightInPoints((short) 14);//设置字体大小
		cellStyle.setFont(font);
		if(ii!=null){
			JSONObject js=JSONObject.fromObject(ii.getMsg());
			sheet.createRow(row0).createCell(0).setCellValue(js.getString("tel0"));
			sheet.getRow(row0).getCell(0).setCellStyle(cellStyle);
		}
		for(int k=1;k<ImpExpService.close_sheet0_foot.length;k++){
			sheet.createRow(row0+k).createCell(0).setCellValue(ImpExpService.close_sheet0_foot[k]);
		}
	}
	private void projectMdCloseExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "结项审核信息导入.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_close_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			projectMdCloseExpSheet(wb,id);
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}