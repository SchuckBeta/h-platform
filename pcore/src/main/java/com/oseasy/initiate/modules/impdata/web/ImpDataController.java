package com.oseasy.initiate.modules.impdata.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
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

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.impdata.entity.ImpInfo;
import com.oseasy.initiate.modules.impdata.enums.BackUserColEnum;
import com.oseasy.initiate.modules.impdata.enums.OfficeColEnum;
import com.oseasy.initiate.modules.impdata.enums.ProjectColEnum;
import com.oseasy.initiate.modules.impdata.enums.StudentColEnum;
import com.oseasy.initiate.modules.impdata.enums.TeacherColEnum;
import com.oseasy.initiate.modules.impdata.exception.ImpDataException;
import com.oseasy.initiate.modules.impdata.service.BackUserErrorService;
import com.oseasy.initiate.modules.impdata.service.ImpDataService;
import com.oseasy.initiate.modules.impdata.service.ImpInfoErrmsgService;
import com.oseasy.initiate.modules.impdata.service.ImpInfoService;
import com.oseasy.initiate.modules.impdata.service.OfficeErrorService;
import com.oseasy.initiate.modules.impdata.service.ProjectErrorService;
import com.oseasy.initiate.modules.impdata.service.StudentErrorService;
import com.oseasy.initiate.modules.impdata.service.TeacherErrorService;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

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
		User user = UserUtils.getUser();
		param.put("userid", user.getId());
		Page<Map<String, String>> page = impInfoService.getList(new Page<Map<String, String>>(request, response),
				param);
		model.addAttribute("page", page);
		return "modules/impdata/impdataList";
	}

	@RequestMapping(value = "delete")
	public String delete(ImpInfo impInfo, RedirectAttributes redirectAttributes) {
		impInfoService.delete(impInfo);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + Global.getAdminPath() + "/impdata/?repage";
	}
	@RequestMapping(value = "getImpInfo")
	@ResponseBody
	public ImpInfo getImpInfo(HttpServletRequest request, HttpServletResponse response) {
		String id=request.getParameter("id");
		return impInfoService.getImpInfo(id);
	}
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
	
	@RequestMapping(value = "downTemplate")
	public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			out= response.getOutputStream();
			
			// excel模板路径
			String fileName = "学生信息导入.xls";
			String fileName2 = "student_data_template.xlsx";
			String type=request.getParameter("type");
			if ("1".equals(type)) {
				fileName = "学生信息导入.xls";
				fileName2 = "student_data_template.xlsx";
			}else if ("2".equals(type)) {
				fileName = "导师信息导入.xls";
				fileName2 = "teacher_data_template.xlsx";
			}else if ("3".equals(type)) {
				fileName = "后台用户信息导入.xls";
				fileName2 = "backuser_data_template.xlsx";
			}else if ("4".equals(type)) {
				fileName = "机构信息导入.xls";
				fileName2 = "org_data_template.xlsx";
			}else if ("5".equals(type)) {
				fileName = "项目信息导入.xls";
				fileName2 = "project_data_template.xlsx";
			}
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + fileName2);
			fs = new FileInputStream(fi);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			if ("1".equals(type)) {
				setStudentExcelHead(sheet);
			}else if ("2".equals(type)) {
				setTeacherExcelHead(sheet);
			}else if ("3".equals(type)) {
				setBackUserExcelHead(sheet);
			}else if ("4".equals(type)) {
				setOrgExcelHead(sheet);
			}else if ("5".equals(type)) {
				setProjectExcelHead(sheet);
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@RequestMapping(value = "expData")
	public void expData(HttpServletRequest request, HttpServletResponse response) {
		String id=request.getParameter("id");
		String type=request.getParameter("type");
		if ("1".equals(type)) {
			studentExp(id,request, response);
		}else if ("2".equals(type)) {
			teacherExp(id, request, response);
		}else if ("3".equals(type)) {
			backUserExp(id, request, response);
		}else if ("4".equals(type)) {
			orgExp(id, request, response);
		}else if ("5".equals(type)) {
			projectExp(id, request, response);
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
		String headDesc = "填写数据说明：红色名称为必填信息。日期格式举例2017-05-19；擅长技术领域为多选信息，若有多个则用英文输入法逗号分隔;\r\n";
		List<Dict> list=DictUtils.getDictList("technology_field");
		List<String> temlist = new ArrayList<String>();
		for(Dict d:list) {
			temlist.add(d.getLabel());
		}
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString(headDesc+"擅长技术领域可选值有:"+StringUtil.join(temlist.toArray(), ",")));
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
		sheet.getRow(0).getCell(0).setCellValue(new XSSFRichTextString("填写数据说明：红色名称为必填信息。立项年份举例：2016。\r\n项目其他成员信息：姓名/学号,姓名/学号；举例：刘家胜/2014210163,郭力/2014210132；以英文输入法逗号分隔"));
	}
	private void studentExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "学生信息导入.xls";
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void teacherExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "导师信息导入.xls";
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			String fileName = "后台用户信息导入.xls";
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void orgExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "机构信息导入.xls";
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void projectExp(String id,HttpServletRequest request,HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
			// excel模板路径
			String fileName = "项目信息导入.xls";
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}