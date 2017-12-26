package com.hch.platform.pcore.modules.impdata.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.actyw.entity.ActYwGnode;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.impdata.dao.ImpInfoDao;
import com.hch.platform.pcore.modules.impdata.entity.BackUserError;
import com.hch.platform.pcore.modules.impdata.entity.GcontestError;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfo;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfoErrmsg;
import com.hch.platform.pcore.modules.impdata.entity.OfficeError;
import com.hch.platform.pcore.modules.impdata.entity.ProMdApprovalError;
import com.hch.platform.pcore.modules.impdata.entity.ProMdCloseError;
import com.hch.platform.pcore.modules.impdata.entity.ProMdMidError;
import com.hch.platform.pcore.modules.impdata.entity.ProjectError;
import com.hch.platform.pcore.modules.impdata.entity.ProjectHsError;
import com.hch.platform.pcore.modules.impdata.entity.StudentError;
import com.hch.platform.pcore.modules.impdata.entity.TeacherError;
import com.hch.platform.pcore.modules.impdata.exception.ImpDataException;
import com.hch.platform.pcore.modules.impdata.web.ImpDataController;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.promodel.dao.ProModelDao;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.proprojectmd.service.ImpExpService;
import com.hch.platform.pcore.modules.sys.dao.BackTeacherExpansionDao;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.OfficeService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.OfficeUtils;
import com.hch.platform.pcore.modules.sys.utils.RegexUtils;
import com.hch.platform.pcore.modules.sys.utils.ThreadPoolUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 导入数据信息表Service
 *
 * @author 9527
 * @version 2017-05-16
 */
@Service
public class ImpDataService extends CrudService<ImpInfoDao, ImpInfo> {
  public static final String YRAR="yyyy";//学生信息导入
	public static final String impStu="1";//学生信息导入
	public static final String impTea="2";//导师信息导入
	public static final String impBackUser="3";//后台用户信息导入
	public static final String impOrg="4";//机构信息导入
	public static final String impProject="5";//大创项目到结项节点的信息导入
	public static final String impMdAppr="6";//民大项目立项信息导入
	public static final String impMdMid="7";//民大项目中期检查信息导入
	public static final String impMdClose="8";//民大项目结项信息导入
	public static final String impProjectHs="9";//大创项目到中期检查节点的信息导入
	public static final String impGcontest="10";//互联网+大赛的信息导入
	@Autowired
	private OfficeService officeService;
	@Autowired
	private ProMdCloseErrorService proMdCloseErrorService;
	@Autowired
	private ProMdMidErrorService proMdMidErrorService;
	@Autowired
	private ProMdApprovalErrorService proMdApprovalErrorService;
	@Autowired
	private StudentErrorService studentErrorService;
	@Autowired
	private TeacherErrorService teacherErrorService;
	@Autowired
	private BackUserErrorService backUserErrorService;
	@Autowired
	private OfficeErrorService officeErrorService;
	@Autowired
	private ImpInfoService impInfoService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private ProjectErrorService projectErrorService;
	@Autowired
	private GcontestErrorService gcontestErrorService;
	@Autowired
	private ProjectHsErrorService projectHsErrorService;
	@Autowired
	private UserService userService;
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private ActTaskService actTaskService;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private ImpInfoErrmsgService impInfoErrmsgService;
	public static Logger logger = Logger.getLogger(ImpDataService.class);
	private void checkTemplate(XSSFSheet datasheet,HttpServletRequest request) throws Exception{
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String sheetname=datasheet.getSheetName();
		String[] ss=sheetname.split("-");
		if (ss.length!=2) {
			throw new ImpDataException("模板错误,请下载最新的模板");
		}
		FileInputStream fs = null;
		try {
			String filename="";
			if ("学生".equals(ss[0])) {
				filename="student_data_template.xlsx";
			}else if ("导师".equals(ss[0])) {
				filename="teacher_data_template.xlsx";
			}else if ("后台用户".equals(ss[0])) {
				filename="backuser_data_template.xlsx";
			}else if ("机构".equals(ss[0])) {
				filename="org_data_template.xlsx";
			}else if ("国创项目".equals(ss[0])) {
				filename="project_data_template.xlsx";
			}else if ("项目信息".equals(ss[0])) {
				filename="project_hs_data_template.xlsx";
			}else if ("互联网+大赛信息".equals(ss[0])) {
				filename="gcontest_data_template.xlsx";
			}else{
				throw new ImpDataException("模板错误,请下载最新的模板");
			}
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator +filename);
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sheet = wb.getSheetAt(0);
			if (!sheet.getSheetName().split("-")[1].equals(ss[1])) {
				throw new ImpDataException("模板错误,请下载最新的模板");
			}
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet).equals(getStringByCell(datasheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					throw new ImpDataException("模板错误,请下载最新的模板");
				}
			}
		}catch (Exception e) {
			throw new ImpDataException("模板错误,请下载最新的模板");
		}finally {
			try {
				if (fs!=null)fs.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	private void checkMdTemplate(String type,XSSFWorkbook swb,HttpServletRequest request) throws ImpDataException, IOException{
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		if(impMdAppr.equals(type)){
			FileInputStream fs = null;
			try {
				String filename="exp_approval_template.xlsx";
				File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
						+ File.separator +filename);
				fs = new FileInputStream(fi);
				// 读取了模板内所有sheet内容
				XSSFWorkbook wb = new XSSFWorkbook(fs);
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFSheet datasheet = swb.getSheetAt(0);
				try {
					for(int j=0;j<sheet.getRow(ImpExpService.approval_sheet0_head).getLastCellNum();j++) {
						if (!getStringByCell(sheet.getRow(ImpExpService.approval_sheet0_head).getCell(j),sheet).equals(getStringByCell(datasheet.getRow(2).getCell(j),sheet))) {
							throw new ImpDataException("请选择正确的文件");
						}
					}
				} catch (Exception e) {
					throw new ImpDataException("请选择正确的文件");
				}
				XSSFSheet sheet1 = wb.getSheetAt(1);
				XSSFSheet datasheet1 = swb.getSheetAt(1);
				for(int j=0;j<sheet1.getRow(ImpExpService.approval_sheet1_head).getLastCellNum();j++) {
					if (!getStringByCell(sheet1.getRow(ImpExpService.approval_sheet1_head).getCell(j),sheet1).equals(getStringByCell(datasheet1.getRow(2).getCell(j),sheet1))) {
						throw new ImpDataException("请选择正确的文件");
					}
				}
			} catch (Exception e) {
				throw new ImpDataException("请选择正确的文件");
			}finally {
				try {
					if (fs!=null)fs.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}else if(impMdMid.equals(type)){
			FileInputStream fs = null;
			try {
				String filename="exp_mid_template.xlsx";
				File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
						+ File.separator +filename);
				fs = new FileInputStream(fi);
				// 读取了模板内所有sheet内容
				XSSFWorkbook wb = new XSSFWorkbook(fs);
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFSheet datasheet = swb.getSheetAt(0);
				for(int j=0;j<sheet.getRow(ImpExpService.mid_sheet0_head).getLastCellNum();j++) {
					if (!getStringByCell(sheet.getRow(ImpExpService.mid_sheet0_head).getCell(j),sheet).equals(getStringByCell(datasheet.getRow(2).getCell(j),sheet))) {
						throw new ImpDataException("请选择正确的文件");
					}
				}
			}catch (Exception e) {
				throw new ImpDataException("请选择正确的文件");
			}finally {
				try {
					if (fs!=null)fs.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}else if(impMdClose.equals(type)){
			FileInputStream fs = null;
			try {
				String filename="exp_close_template.xlsx";
				File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
						+ File.separator +filename);
				fs = new FileInputStream(fi);
				// 读取了模板内所有sheet内容
				XSSFWorkbook wb = new XSSFWorkbook(fs);
				XSSFSheet sheet = wb.getSheetAt(0);
				XSSFSheet datasheet = swb.getSheetAt(0);
				for(int j=0;j<sheet.getRow(ImpExpService.close_sheet0_head).getLastCellNum();j++) {
					if (!getStringByCell(sheet.getRow(ImpExpService.close_sheet0_head).getCell(j),sheet).equals(getStringByCell(datasheet.getRow(2).getCell(j),sheet))) {
						throw new ImpDataException("请选择正确的文件");
					}
				}
			}catch (Exception e) {
				throw new ImpDataException("请选择正确的文件");
			}finally {
				try {
					if (fs!=null)fs.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	private int getTotalRow(XSSFSheet sheet,String tag){
		for(int i=0;i<=sheet.getLastRowNum();i++){
			String s=sheet.getRow(i).getCell(0).getStringCellValue();
			if(StringUtil.isNotEmpty(s)&&s.contains(tag)){
				return i;
			}
		}
		return sheet.getLastRowNum();
	}
	public void importMdData(String type,List<MultipartFile> imgFiles,HttpServletRequest request){
		for(MultipartFile imgFile:imgFiles){
			importMdData(type,imgFile,request);
		}
	}
	public void importMdData(String type,MultipartFile imgFile,HttpServletRequest request){
		InputStream is = null;
		ImpInfo ii=new ImpInfo();
		ii.setImpTpye(type);
		ii.setTotal("0");
		ii.setFail("0");
		ii.setSuccess("0");
		ii.setIsComplete("0");
		ii.setFilename(imgFile.getOriginalFilename());
		impInfoService.save(ii);//插入导入信息
		try {
			is = imgFile.getInputStream();
			XSSFWorkbook wb = new XSSFWorkbook(is);
			checkMdTemplate(type,wb,request);//检查模板版本
			if (impMdAppr.equals(type)) {//民大立项
				XSSFSheet sheet = wb.getSheetAt(0); // 获取第一个sheet表
				XSSFSheet sheet2 = wb.getSheetAt(1); // 获取第一个sheet表
				JSONObject js=new JSONObject();
				int t0=getTotalRow(sheet,"联系人签字：");
				int t1=getTotalRow(sheet2,"联系人签字：");
				js.put("title0", sheet.getRow(0).getCell(0).getStringCellValue());
				js.put("tel0", sheet.getRow(t0).getCell(0).getStringCellValue());
				js.put("title1", sheet2.getRow(0).getCell(0).getStringCellValue());
				js.put("tel1", sheet2.getRow(t1).getCell(0).getStringCellValue());
				js.put("oname", sheet.getRow(1).getCell(0).getStringCellValue());
				ii.setTotal((t0+t1-8)+"");
				ii.setMsg(js.toString());
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importMdApproval(sheet,sheet2,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error(FileStepEnum.S2000.getName()+"信息导入出错",e);
						}
					}
				});
			}else if(impMdMid.equals(type)){//民大中期
				XSSFSheet sheet = wb.getSheetAt(0); // 获取第一个sheet表
				JSONObject js=new JSONObject();
				int t0=getTotalRow(sheet,"经手人：");
				js.put("title0", sheet.getRow(0).getCell(0).getStringCellValue());
				js.put("tel0", sheet.getRow(t0).getCell(0).getStringCellValue());
				js.put("oname", sheet.getRow(1).getCell(0).getStringCellValue());
				ii.setTotal((t0-4)+"");
				ii.setMsg(js.toString());
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importMdMid(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error(FileStepEnum.S2200.getName()+"信息导入出错",e);
						}
					}
				});
			}else if(impMdClose.equals(type)){//民大结项
				XSSFSheet sheet = wb.getSheetAt(0); // 获取第一个sheet表
				JSONObject js=new JSONObject();
				int t0=getTotalRow(sheet,"经手人：");
				js.put("title0", sheet.getRow(0).getCell(0).getStringCellValue());
				js.put("tel0", sheet.getRow(t0).getCell(0).getStringCellValue());
				js.put("oname", sheet.getRow(1).getCell(0).getStringCellValue());
				ii.setTotal((t0-4)+"");
				ii.setMsg(js.toString());
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importMdClose(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error(FileStepEnum.S2300.getName()+"信息导入出错",e);
						}
					}
				});
			}
		}catch (POIXMLException e) {
			ii.setIsComplete("1");
			ii.setErrmsg("请选择正确的文件");
			impInfoService.save(ii);
			logger.error("导入出错", e);
		}catch (ImpDataException e) {
			ii.setIsComplete("1");
			ii.setErrmsg(e.getMessage());
			impInfoService.save(ii);
			logger.error("导入出错", e);
		}catch (Exception e) {
			ii.setIsComplete("1");
			ii.setErrmsg("导入出错");
			impInfoService.save(ii);
			logger.error("导入出错", e);
		}finally {
			try {
				is.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	private void importMdClose(XSSFSheet sheet0,ImpInfo ii) {
		XSSFRow rowData;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		String sheetIndx0="0";
		for (int i = 4; i < getTotalRow(sheet0,"经手人："); i++) {
			int tag=0;//有几个错误字段
			ProMdCloseError se=new ProMdCloseError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet0.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet0.getRow(2).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet0))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			String	pid=getStringByCell(rowData.getCell(15),sheet0);
			ImpInfoErrmsg iie3=new ImpInfoErrmsg();
			iie3.setSheetIndx(sheetIndx0);
			iie3.setImpId(ii.getId());
			iie3.setDataId(se.getId());
			iie3.setColname("1");
			ProModel pm=proModelDao.get(pid);
			if(StringUtil.isEmpty(pid)||pm==null){
				tag++;
				iie3.setErrmsg("文档结构已被修改，无法匹配");
			}
			if (StringUtil.isNotEmpty(iie3.getErrmsg())) {
				impInfoErrmsgService.save(iie3);
			}

			String	result=getStringByCell(rowData.getCell(12),sheet0);
			ImpInfoErrmsg iie2=new ImpInfoErrmsg();
			iie2.setSheetIndx(sheetIndx0);
			iie2.setImpId(ii.getId());
			iie2.setDataId(se.getId());
			iie2.setColname("12");
			if(StringUtil.isEmpty(result)){
				tag++;
				iie2.setErrmsg("请填写学院审查意见");
			}else if(getResultCode(result)==null){
				tag++;
				iie2.setErrmsg("未知的学院审查意见");
				result=null;
			}
			if (StringUtil.isNotEmpty(iie2.getErrmsg())) {
				impInfoErrmsgService.save(iie2);
			}else{
				result=getResultCode(result);
			}
			String	exc=getStringByCell(rowData.getCell(13),sheet0);
			ImpInfoErrmsg iie1=new ImpInfoErrmsg();
			iie1.setSheetIndx(sheetIndx0);
			iie1.setImpId(ii.getId());
			iie1.setDataId(se.getId());
			iie1.setColname("13");
			if(!StringUtil.isEmpty(exc)){
				if(getYorNCode(exc)==null){
					tag++;
					iie1.setErrmsg("未知的输入");
					result=null;
				}
			}
			if (StringUtil.isNotEmpty(iie1.getErrmsg())) {
				impInfoErrmsgService.save(iie1);
			}else{
				exc=getYorNCode(exc);
			}


			String	gnode=getStringByCell(rowData.getCell(16),sheet0);
			ImpInfoErrmsg iie4=new ImpInfoErrmsg();
			iie4.setSheetIndx(sheetIndx0);
			iie4.setImpId(ii.getId());
			iie4.setDataId(se.getId());
			iie4.setColname("1");
			if(StringUtil.isEmpty(gnode)){
				tag++;
				iie4.setErrmsg("文档结构已被修改，无法匹配");
			}else if(pm!=null&&!checkGnode(gnode, pm.getProcInsId())){
				tag++;
				iie4.setErrmsg("该项目已被导入过");
			}
			if (StringUtil.isNotEmpty(iie4.getErrmsg())) {
				impInfoErrmsgService.save(iie4);
			}
			se.setPNumber(getStringByCell(rowData.getCell(1),sheet0));
			se.setPName(getStringByCell(rowData.getCell(2),sheet0));
			se.setLeaderName(getStringByCell(rowData.getCell(3),sheet0));
			se.setNo(getStringByCell(rowData.getCell(4),sheet0));
			se.setMobile(getStringByCell(rowData.getCell(5),sheet0));
			se.setMembers(getStringByCell(rowData.getCell(6),sheet0));
			se.setTeaName(getStringByCell(rowData.getCell(7),sheet0));
			se.setTeaNo(getStringByCell(rowData.getCell(8),sheet0));
			se.setLevel(getStringByCell(rowData.getCell(9),sheet0));
			se.setProCategory(getStringByCell(rowData.getCell(10),sheet0));
			se.setResult(getStringByCell(rowData.getCell(11),sheet0));
			se.setAuditResult(getStringByCell(rowData.getCell(12),sheet0));
			se.setExcellent(getStringByCell(rowData.getCell(13),sheet0));
			se.setReimbursementAmount(getStringByCell(rowData.getCell(14),sheet0));
			se.setProModelMdId(getStringByCell(rowData.getCell(15),sheet0));
			se.setGnodeid(getStringByCell(rowData.getCell(16),sheet0));
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				proMdCloseErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					proMdCloseErrorService.saveProMdClose(exc,result,pm);
					success++;
				} catch (Exception e) {
					logger.error("保存民大项目结项审核信息出错",e);
					fail++;
					proMdCloseErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importMdMid(XSSFSheet sheet0,ImpInfo ii) {
		XSSFRow rowData;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		String sheetIndx0="0";
		for (int i = 4; i < getTotalRow(sheet0,"经手人："); i++) {
			int tag=0;//有几个错误字段
			ProMdMidError se=new ProMdMidError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet0.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet0.getRow(2).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet0))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			String	pid=getStringByCell(rowData.getCell(12),sheet0);
			ImpInfoErrmsg iie3=new ImpInfoErrmsg();
			iie3.setSheetIndx(sheetIndx0);
			iie3.setImpId(ii.getId());
			iie3.setDataId(se.getId());
			iie3.setColname("1");
			ProModel pm=proModelDao.get(pid);
			if(StringUtil.isEmpty(pid)||pm==null){
				tag++;
				iie3.setErrmsg("文档结构已被修改，无法匹配");
			}
			if (StringUtil.isNotEmpty(iie3.getErrmsg())) {
				impInfoErrmsgService.save(iie3);
			}

			String	result=getStringByCell(rowData.getCell(9),sheet0);
			ImpInfoErrmsg iie2=new ImpInfoErrmsg();
			iie2.setSheetIndx(sheetIndx0);
			iie2.setImpId(ii.getId());
			iie2.setDataId(se.getId());
			iie2.setColname("9");
			if(StringUtil.isEmpty(result)){
				tag++;
				iie2.setErrmsg("请填写学院审查意见");
			}else if(getMidResultCode(result)==null){
				tag++;
				iie2.setErrmsg("未知的学院审查意见");
				result=null;
			}
			if (StringUtil.isNotEmpty(iie2.getErrmsg())) {
				impInfoErrmsgService.save(iie2);
			}else{
				result=getMidResultCode(result);
			}



			String	gnode=getStringByCell(rowData.getCell(13),sheet0);
			ImpInfoErrmsg iie4=new ImpInfoErrmsg();
			iie4.setSheetIndx(sheetIndx0);
			iie4.setImpId(ii.getId());
			iie4.setDataId(se.getId());
			iie4.setColname("1");
			if(StringUtil.isEmpty(gnode)){
				tag++;
				iie4.setErrmsg("文档结构已被修改，无法匹配");
			}else if(pm!=null&&!checkGnode(gnode, pm.getProcInsId())){
				tag++;
				iie4.setErrmsg("该项目已被导入过");
			}
			if (StringUtil.isNotEmpty(iie4.getErrmsg())) {
				impInfoErrmsgService.save(iie4);
			}
			se.setPNumber(getStringByCell(rowData.getCell(1),sheet0));
			se.setPName(getStringByCell(rowData.getCell(2),sheet0));
			se.setLeaderName(getStringByCell(rowData.getCell(3),sheet0));
			se.setNo(getStringByCell(rowData.getCell(4),sheet0));
			se.setMobile(getStringByCell(rowData.getCell(5),sheet0));
			se.setTeachers(getStringByCell(rowData.getCell(6),sheet0));
			se.setProCategory(getStringByCell(rowData.getCell(7),sheet0));
			se.setLevel(getStringByCell(rowData.getCell(8),sheet0));
			se.setResult(getStringByCell(rowData.getCell(9),sheet0));
			se.setStageResult(getStringByCell(rowData.getCell(10),sheet0));
			se.setReimbursementAmount(getStringByCell(rowData.getCell(11),sheet0));
			se.setProModelMdId(getStringByCell(rowData.getCell(12),sheet0));
			se.setGnodeid(getStringByCell(rowData.getCell(13),sheet0));
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				proMdMidErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					proMdMidErrorService.saveProMdMid(result,pm);
					success++;
				} catch (Exception e) {
					logger.error("保存民大项目中期检查信息出错",e);
					fail++;
					proMdMidErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private String getMidResultCode(String s){
		if("通过中检".equals(s)){
			return "1";
		}else if("项目终止".equals(s)){
			return "0";
		}else{
			return null;
		}
	}
	private String getYorNCode(String s){
		if("是".equals(s)){
			return "1";
		}else if("否".equals(s)){
			return "0";
		}else{
			return null;
		}
	}
	private String getResultCode(String s){
		if("通过".equals(s)){
			return "1";
		}else if("不通过".equals(s)){
			return "0";
		}else{
			return null;
		}
	}
	//false 已被导入过
	private boolean checkGnode(String gnode,String proc){
		ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(proc);
		if(actYwGnode!=null&&!actYwGnode.getSuspended()&&actYwGnode.getId().equals(gnode)){
			return true;
		}
		return false;
	}
	private String getAbc(XSSFRow rowData,XSSFSheet sheet0){
		if("√".equals(getStringByCell(rowData.getCell(8),sheet0))){
			return "A";
		}else if("√".equals(getStringByCell(rowData.getCell(9),sheet0))){
			return "B";
		}else if("√".equals(getStringByCell(rowData.getCell(10),sheet0))){
			return "C";
		}else{
			return "";
		}
	}
	private void importMdApproval(XSSFSheet sheet0,XSSFSheet sheet1,ImpInfo ii) {
		XSSFRow rowData;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		String sheetIndx0="0";
		for (int i = 4; i < getTotalRow(sheet0,"联系人签字："); i++) {
			int tag=0;//有几个错误字段
			ProMdApprovalError se=new ProMdApprovalError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet0.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet0.getRow(2).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet0))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			String	pid=getStringByCell(rowData.getCell(22),sheet0);
			ImpInfoErrmsg iie3=new ImpInfoErrmsg();
			iie3.setSheetIndx(sheetIndx0);
			iie3.setImpId(ii.getId());
			iie3.setDataId(se.getId());
			iie3.setColname("4");
			ProModel pm=proModelDao.get(pid);
			if(StringUtil.isEmpty(pid)||pm==null){
				tag++;
				iie3.setErrmsg("文档结构已被修改，无法匹配");
			}
			if (StringUtil.isNotEmpty(iie3.getErrmsg())) {
				impInfoErrmsgService.save(iie3);
			}

			String	pnumber=getStringByCell(rowData.getCell(3),sheet0);
			ImpInfoErrmsg iie1=new ImpInfoErrmsg();
			iie1.setSheetIndx(sheetIndx0);
			iie1.setImpId(ii.getId());
			iie1.setDataId(se.getId());
			iie1.setColname("3");
			if(StringUtil.isEmpty(pnumber)){
				tag++;
				iie1.setErrmsg("请填写项目编号");
			}else if(pnumber.length()>64){
				tag++;
				iie1.setErrmsg("最多64个字符");
				pnumber=null;
			}else if(proMdApprovalErrorService.checkMdProNumber(pnumber, pid,pm)){
				tag++;
				iie1.setErrmsg("该项目编号已存在");
			}
			if (StringUtil.isNotEmpty(iie1.getErrmsg())) {
				impInfoErrmsgService.save(iie1);
			}
			String	result=getStringByCell(rowData.getCell(21),sheet0);
			ImpInfoErrmsg iie2=new ImpInfoErrmsg();
			iie2.setSheetIndx(sheetIndx0);
			iie2.setImpId(ii.getId());
			iie2.setDataId(se.getId());
			iie2.setColname("21");
			if(StringUtil.isEmpty(result)){
				tag++;
				iie2.setErrmsg("请填写评审结果");
			}else if(getResultCode(result)==null){
				tag++;
				iie2.setErrmsg("未知的评审结果");
				result=null;
			}
			if (StringUtil.isNotEmpty(iie2.getErrmsg())) {
				impInfoErrmsgService.save(iie2);
			}else{
				result=getResultCode(result);
			}



			String	gnode=getStringByCell(rowData.getCell(23),sheet0);
			ImpInfoErrmsg iie4=new ImpInfoErrmsg();
			iie4.setSheetIndx(sheetIndx0);
			iie4.setImpId(ii.getId());
			iie4.setDataId(se.getId());
			iie4.setColname("4");
			if(StringUtil.isEmpty(gnode)){
				tag++;
				iie4.setErrmsg("文档结构已被修改，无法匹配");
			}else if(pm!=null&&!checkGnode(gnode, pm.getProcInsId())){
				tag++;
				iie4.setErrmsg("该项目已被导入过");
			}
			if (StringUtil.isNotEmpty(iie4.getErrmsg())) {
				impInfoErrmsgService.save(iie4);
			}
			se.setProCategory(getStringByCell(rowData.getCell(1),sheet0));
			se.setLevel(getStringByCell(rowData.getCell(2),sheet0));
			se.setPNumber(getStringByCell(rowData.getCell(3),sheet0));
			se.setPName(getStringByCell(rowData.getCell(4),sheet0));
			se.setLeaderName(getStringByCell(rowData.getCell(5),sheet0));
			se.setNo(getStringByCell(rowData.getCell(6),sheet0));
			se.setMobile(getStringByCell(rowData.getCell(7),sheet0));
			se.setProSource(getAbc(rowData, sheet0));
			se.setSourceProjectName(getStringByCell(rowData.getCell(11),sheet0));
			se.setSourceProjectType(getStringByCell(rowData.getCell(12),sheet0));
			se.setTeachers1(getStringByCell(rowData.getCell(13),sheet0));
			se.setTeachers2(getStringByCell(rowData.getCell(14),sheet0));
			se.setTeachers3(getStringByCell(rowData.getCell(15),sheet0));
			se.setTeachers4(getStringByCell(rowData.getCell(16),sheet0));
			se.setRufu(getStringByCell(rowData.getCell(17),sheet0));
			se.setMembers1(getStringByCell(rowData.getCell(18),sheet0));
			se.setMembers2(getStringByCell(rowData.getCell(19),sheet0));
			se.setMembers3(getStringByCell(rowData.getCell(20),sheet0));
			se.setResult(getStringByCell(rowData.getCell(21),sheet0));
			se.setProModelMdId(getStringByCell(rowData.getCell(22),sheet0));
			se.setGnodeid(getStringByCell(rowData.getCell(23),sheet0));
			se.setSheetIndex(sheetIndx0);

			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				proMdApprovalErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					proMdApprovalErrorService.saveProMdApproval(pnumber,result,pm);
					success++;
				} catch (Exception e) {
					logger.error("保存民大项目立项信息出错",e);
					fail++;
					proMdApprovalErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		String sheetIndx1="1";
		for (int i = 4; i < getTotalRow(sheet1,"联系人签字："); i++) {
			int tag=0;//有几个错误字段
			ProMdApprovalError se=new ProMdApprovalError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet1.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet1.getRow(2).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet1))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			String	pid=getStringByCell(rowData.getCell(22),sheet1);
			ImpInfoErrmsg iie3=new ImpInfoErrmsg();
			iie3.setSheetIndx(sheetIndx1);
			iie3.setImpId(ii.getId());
			iie3.setDataId(se.getId());
			iie3.setColname("4");
			ProModel pm=proModelDao.get(pid);
			if(StringUtil.isEmpty(pid)||pm==null){
				tag++;
				iie3.setErrmsg("文档结构已被修改，无法匹配");
			}
			if (StringUtil.isNotEmpty(iie3.getErrmsg())) {
				impInfoErrmsgService.save(iie3);
			}

			String	pnumber=getStringByCell(rowData.getCell(3),sheet1);
			ImpInfoErrmsg iie1=new ImpInfoErrmsg();
			iie1.setSheetIndx(sheetIndx1);
			iie1.setImpId(ii.getId());
			iie1.setDataId(se.getId());
			iie1.setColname("3");
			if(StringUtil.isEmpty(pnumber)){
				tag++;
				iie1.setErrmsg("请填写项目编号");
			}else if(pnumber.length()>64){
				tag++;
				iie1.setErrmsg("最多64个字符");
				pnumber=null;
			}else if(proMdApprovalErrorService.checkMdProNumber(pnumber, pid,pm)){
				tag++;
				iie1.setErrmsg("该项目编号已存在");
			}
			if (StringUtil.isNotEmpty(iie1.getErrmsg())) {
				impInfoErrmsgService.save(iie1);
			}
			String	result=getStringByCell(rowData.getCell(21),sheet1);
			ImpInfoErrmsg iie2=new ImpInfoErrmsg();
			iie2.setSheetIndx(sheetIndx1);
			iie2.setImpId(ii.getId());
			iie2.setDataId(se.getId());
			iie2.setColname("21");
			if(StringUtil.isEmpty(result)){
				tag++;
				iie2.setErrmsg("请填写评审结果");
			}else if(getResultCode(result)==null){
				tag++;
				iie2.setErrmsg("未知的评审结果");
				result=null;
			}
			if (StringUtil.isNotEmpty(iie2.getErrmsg())) {
				impInfoErrmsgService.save(iie2);
			}else{
				result=getResultCode(result);
			}

			String	gnode=getStringByCell(rowData.getCell(23),sheet1);
			ImpInfoErrmsg iie4=new ImpInfoErrmsg();
			iie4.setSheetIndx(sheetIndx1);
			iie4.setImpId(ii.getId());
			iie4.setDataId(se.getId());
			iie4.setColname("4");
			if(StringUtil.isEmpty(gnode)){
				tag++;
				iie4.setErrmsg("文档结构已被修改，无法匹配");
			}else if(pm!=null&&!checkGnode(gnode, pm.getProcInsId())){
				tag++;
				iie4.setErrmsg("该项目已被导入过");
			}
			if (StringUtil.isNotEmpty(iie4.getErrmsg())) {
				impInfoErrmsgService.save(iie4);
			}
			se.setProCategory(getStringByCell(rowData.getCell(1),sheet1));
			se.setLevel(getStringByCell(rowData.getCell(2),sheet1));
			se.setPNumber(getStringByCell(rowData.getCell(3),sheet1));
			se.setPName(getStringByCell(rowData.getCell(4),sheet1));
			se.setLeaderName(getStringByCell(rowData.getCell(5),sheet1));
			se.setNo(getStringByCell(rowData.getCell(6),sheet1));
			se.setMobile(getStringByCell(rowData.getCell(7),sheet1));
			se.setProSource(getAbc(rowData, sheet1));
			se.setSourceProjectName(getStringByCell(rowData.getCell(11),sheet1));
			se.setSourceProjectType(getStringByCell(rowData.getCell(12),sheet1));
			se.setTeachers1(getStringByCell(rowData.getCell(13),sheet1));
			se.setTeachers2(getStringByCell(rowData.getCell(14),sheet1));
			se.setTeachers3(getStringByCell(rowData.getCell(15),sheet1));
			se.setTeachers4(getStringByCell(rowData.getCell(16),sheet1));
			se.setRufu(getStringByCell(rowData.getCell(17),sheet1));
			se.setMembers1(getStringByCell(rowData.getCell(18),sheet1));
			se.setMembers2(getStringByCell(rowData.getCell(19),sheet1));
			se.setMembers3(getStringByCell(rowData.getCell(20),sheet1));
			se.setResult(getStringByCell(rowData.getCell(21),sheet1));
			se.setProModelMdId(getStringByCell(rowData.getCell(22),sheet1));
			se.setGnodeid(getStringByCell(rowData.getCell(23),sheet1));
			se.setSheetIndex(sheetIndx1);

			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				proMdApprovalErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					proMdApprovalErrorService.saveProMdApproval(pnumber,result,pm);
					success++;
				} catch (Exception e) {
					logger.error("保存民大项目立项信息出错",e);
					fail++;
					proMdApprovalErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	public void importData(MultipartFile imgFile,HttpServletRequest request) throws Exception  {
		InputStream is = null;
		try {
			is = imgFile.getInputStream();
			XSSFWorkbook wb = new XSSFWorkbook(is);
			XSSFSheet sheet = wb.getSheetAt(0); // 获取第一个sheet表
			String sheetname=sheet.getSheetName();
			checkTemplate(sheet,request);//检查模板版本
			String sname=sheetname.split("-")[0];
			if ("学生".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impStu);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importStudent(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(UserUtils.USER_CACHE);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("学生信息导入出错",e);
						}
					}
				});
			}else if ("导师".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impTea);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importTeacher(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(UserUtils.USER_CACHE);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("导师信息导入出错",e);
						}
					}
				});
			}else if ("后台用户".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impBackUser);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try{
							importBackUser(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(UserUtils.USER_CACHE);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("后台用户信息导入出错",e);
						}
					}
				});
			}else if ("机构".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impOrg);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try {
							importOrg(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("机构信息导入出错",e);
						}
					}
				});
			}else if ("国创项目".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impProject);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try {
							importProject(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("国创项目信息导入出错",e);
						}
					}
				});
			}else if ("项目信息".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impProjectHs);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try {
							importHsProject(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("项目信息导入出错",e);
						}
					}
				});
			}else if ("互联网+大赛信息".equals(sname)) {
				ImpInfo ii=new ImpInfo();
				ii.setImpTpye(impGcontest);
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				ThreadPoolUtils.fixedThreadPool.execute(new Thread() {
					@Override
					public void run() {
						try {
							importGcontest(sheet,ii);
						} catch (Exception e) {
							ii.setIsComplete("1");
							impInfoService.save(ii);
							CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
							logger.error("互联网+大赛信息导入出错",e);
						}
					}
				});
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	private void importStudent(XSSFSheet sheet,ImpInfo ii) {
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		Dict d = null;
		Office office=null;//学院
		Office professional = null;//专业
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			StudentExpansion st=new StudentExpansion();
			AbsUser user=new AbsUser();
			int tag=0;//有几个错误字段
			StudentError se=new StudentError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if ("用户名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setLoginName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (UserUtils.getByLoginNameOrNo(val)!=null) {
							tag++;
							iie.setErrmsg("用户名已存在");
						}else if (val.length()>100) {
							tag++;
							iie.setErrmsg("最多100个字符");
							se.setLoginName(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setLoginName(val);
					}
				}else if ("姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						se.setName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setName(val);
					}
				}else if ("学号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						se.setNo(null);
					}else if (UserUtils.isExistNo(val)) {
						tag++;
						iie.setErrmsg("学号已存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setNo(val);
					}
				}else if ("手机号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.mobileRegex, val)) {
							tag++;
							iie.setErrmsg("手机号格式不正确");
						}else if (UserUtils.isExistMobile(val)) {
							tag++;
							iie.setErrmsg("手机号已存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setMobile(val);
					}
				}else if ("邮箱".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setEmail(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.emailRegex, val)) {
							tag++;
							iie.setErrmsg("邮箱格式不正确");
						}else if (val.length()>200) {
							tag++;
							iie.setErrmsg("最多200个字符");
							se.setEmail(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setEmail(val);
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>255) {
						tag++;
						iie.setErrmsg("最多255个字符");
						se.setRemarks(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setRemarks(val);
					}
				}else if ("出生年月".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setBirthday(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						try {
							user.setBirthday(DateUtil.parseDate(val,"yyyy-MM-dd"));
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("日期格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("证件类别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setIdType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("id_type", val))==null) {
							tag++;
							iie.setErrmsg("证件类别不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setIdType(d.getValue());
					}
				}else if ("证件号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setIdNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>32) {
							tag++;
							iie.setErrmsg("最多32个字符");
							se.setIdNo(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setIdNumber(val);
					}
				}else if ("性别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setSex(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("sex", val))==null) {
						tag++;
						iie.setErrmsg("性别不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setSex(d.getValue());
					}
				}else if ("擅长技术领域".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setDomain(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					List<String> list = new ArrayList<String>();
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("擅长技术领域内容过长");
							se.setDomain(null);
						}else {
							String[] vs=val.split(",");
							for(String v:vs) {
								if ((d=DictUtils.getDictByLabel("technology_field", v))==null) {
									tag++;
									iie.setErrmsg("擅长技术领域不存在");
									break;
								}else{
									list.add(d.getValue());
								}
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setDomain(StringUtil.join(list.toArray(), ","));
					}
				}else if ("学位".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setDegree(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("degree_type", val))==null) {
							tag++;
							iie.setErrmsg("学位不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setDegree(d.getValue());
					}
				}else if ("学历".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setEducation(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("enducation_level", val))==null) {
							tag++;
							iie.setErrmsg("学历不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setEducation(d.getValue());
					}
				}else if ("学院".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					office=null;
					se.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((office=OfficeUtils.getOfficeByName(val))==null) {
						tag++;
						iie.setErrmsg("学院不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setOffice(office);
						user.setProfessional(office.getId());
					}
				}else if ("专业".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					professional=null;
					se.setProfessional(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (office!=null&&(professional=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
							tag++;
							iie.setErrmsg("专业不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (professional!=null)user.setProfessional(professional.getId());
					}
				}else if ("班级".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setTClass(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>32) {
						tag++;
						iie.setErrmsg("最多32个字符");
						se.setTClass(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						st.setTClass(val);
					}
				}else if ("国家/地区".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setCountry(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						se.setCountry(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setCountry(val);
					}
				}else if ("民族".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setNational(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setNational(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setNational(val);
					}
				}else if ("政治面貌".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setPolitical(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setPolitical(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setPolitical(val);
					}
				}else if ("项目经历".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					st.setProjectExperience(val);
				}else if ("大赛经历".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					st.setContestExperience(val);
				}else if ("获奖作品".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					st.setAward(val);
				}else if ("入学时间".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setEnterdate(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if(StringUtil.isNotEmpty(val)){
						try {
							st.setEnterdate(DateUtil.parseDate(val,"yyyy-MM-dd"));
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("日期格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("休学时间".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setTemporaryDate(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if(StringUtil.isNotEmpty(val)){
						try {
							st.setTemporaryDate(DateUtil.parseDate(val,"yyyy-MM-dd"));
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("日期格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("毕业时间".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setGraduation(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if(StringUtil.isNotEmpty(val)){
						try {
							st.setGraduation(DateUtil.parseDate(val,"yyyy-MM-dd"));
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("日期格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("联系地址".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setAddress(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>255) {
						tag++;
						iie.setErrmsg("最多255个字符");
						se.setAddress(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						st.setAddress(val);
					}
				}else if ("在读学位".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setInstudy(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("degree_type", val))==null) {
						tag++;
						iie.setErrmsg("在读学位不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)st.setInstudy(d.getValue());
					}
				}else if ("现状".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setCurrState(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("current_sate", val))==null) {
						tag++;
						iie.setErrmsg("在读学位不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)st.setCurrState(d.getValue());
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				studentErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					st.setUser(user);
					studentErrorService.saveStudent(st);
					success++;
				} catch (Exception e) {
					logger.error("保存学生信息出错",e);
					fail++;
					studentErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importTeacher(XSSFSheet sheet,ImpInfo ii) {
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		Dict d = null;
		Office office=null;//学院
		Office professional = null;//专业
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			BackTeacherExpansion tc=new BackTeacherExpansion();
			AbsUser user=new AbsUser();
			int tag=0;//有几个错误字段
			TeacherError te=new TeacherError();
			te.setImpId(ii.getId());
			te.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if ("用户名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setLoginName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (UserUtils.getByLoginNameOrNo(val)!=null) {
							tag++;
							iie.setErrmsg("用户名已存在");
						}else if (val.length()>100) {
							tag++;
							iie.setErrmsg("最多100个字符");
							te.setLoginName(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setLoginName(val);
					}
				}else if ("导师来源".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setTeachertype(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("master_type", val))==null) {
						tag++;
						iie.setErrmsg("导师来源不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)tc.setTeachertype(d.getValue());
					}
				}else if ("工号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						te.setNo(null);
					}else if (UserUtils.isExistNo(val)) {
						tag++;
						iie.setErrmsg("工号已存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setNo(val);
					}
				}else if ("姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						te.setName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setName(val);
					}
				}else if ("性别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setSex(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("sex", val))==null) {
						tag++;
						iie.setErrmsg("性别不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setSex(d.getValue());
					}
				}else if ("手机号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.mobileRegex, val)) {
							tag++;
							iie.setErrmsg("手机号格式不正确");
						}else if (UserUtils.isExistMobile(val)) {
							tag++;
							iie.setErrmsg("手机号已存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setMobile(val);
					}
				}else if ("邮箱".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setEmail(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.emailRegex, val)) {
							tag++;
							iie.setErrmsg("邮箱格式不正确");
						}else if (val.length()>200) {
							tag++;
							iie.setErrmsg("最多200个字符");
							te.setEmail(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setEmail(val);
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>255) {
						tag++;
						iie.setErrmsg("最多255个字符");
						te.setRemarks(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setRemarks(val);
					}
				}else if ("出生年月".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setBirthday(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						try {
							user.setBirthday(DateUtil.parseDate(val,"yyyy-MM-dd"));
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("日期格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("证件类别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setIdType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("id_type", val))==null) {
							tag++;
							iie.setErrmsg("证件类别不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setIdType(d.getValue());
					}
				}else if ("证件号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setIdNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>32) {
							tag++;
							iie.setErrmsg("最多32个字符");
							te.setIdNo(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setIdNumber(val);
					}
				}else if ("擅长技术领域".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setDomain(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					List<String> list = new ArrayList<String>();
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("擅长技术领域内容过长");
							te.setDomain(null);
						}else {
							String[] vs=val.split(",");
							for(String v:vs) {
								if ((d=DictUtils.getDictByLabel("technology_field", v))==null) {
									tag++;
									iie.setErrmsg("擅长技术领域不存在");
									break;
								}else{
									list.add(d.getValue());
								}
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setDomain(StringUtil.join(list.toArray(), ","));
					}
				}else if ("学历类别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setEducationType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("enducation_type", val))==null) {
							tag++;
							iie.setErrmsg("学历类别不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)tc.setEducationType(d.getValue());
					}
				}else if ("学位".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setDegree(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("degree_type", val))==null) {
							tag++;
							iie.setErrmsg("学位不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setDegree(d.getValue());
					}
				}else if ("学历".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setEducation(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if ((d=DictUtils.getDictByLabel("enducation_level", val))==null) {
							tag++;
							iie.setErrmsg("学历不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setEducation(d.getValue());
					}
				}else if ("学院".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					office=null;
					te.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((office=OfficeUtils.getOfficeByName(val))==null) {
						tag++;
						iie.setErrmsg("学院不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setOffice(office);
						user.setProfessional(office.getId());
					}
				}else if ("专业".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					professional=null;
					te.setProfessional(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!StringUtil.isEmpty(val)&&office!=null&&(professional=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
							tag++;
							iie.setErrmsg("专业不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (professional!=null)user.setProfessional(professional.getId());
					}
				}else if ("民族".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setNational(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						te.setNational(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setNational(val);
					}
				}else if ("政治面貌".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setPolitical(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						te.setPolitical(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setPolitical(val);
					}
				}else if ("国家/地区".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setArea(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						te.setArea(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setArea(val);
					}
				}else if ("学科门类".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					te.setDiscipline(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("professional_type", val))==null) {
						tag++;
						iie.setErrmsg("学科门类不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)tc.setDiscipline(Integer.parseInt(d.getValue()));
					}
				}else if ("行业".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setIndustry(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>32) {
						tag++;
						iie.setErrmsg("最多32个字符");
						te.setIndustry(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setIndustry(val);
					}
				}else if ("职称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setTechnicalTitle(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						te.setTechnicalTitle(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setTechnicalTitle(val);
					}
				}else if ("服务意向".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setServiceIntention(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					List<String> list = new ArrayList<String>();
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("服务意向内容过长");
							te.setServiceIntention(null);
						}else {
							String[] vs=val.split(",");
							for(String v:vs) {
								if ((d=DictUtils.getDictByLabel("master_help", v))==null) {
									tag++;
									iie.setErrmsg("服务意向不存在");
									break;
								}else{
									list.add(d.getValue());
								}
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setServiceIntention(StringUtil.join(list.toArray(), ","));
					}
				}else if ("工作单位".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setWorkUnit(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						te.setWorkUnit(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setWorkUnit(val);
					}
				}else if ("联系地址".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setAddress(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						te.setAddress(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setAddress(val);
					}
				}else if ("开户银行".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setFirstBank(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						te.setFirstBank(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						tc.setFirstBank(val);
					}
				}else if ("银行账号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setBankAccount(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>16) {
							tag++;
							iie.setErrmsg("最多16个字符");
							te.setBankAccount(null);
						}else{
							try {
								Long.valueOf(val);
							} catch (Exception e) {
								tag++;
								iie.setErrmsg("只能包含数字");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (!StringUtil.isEmpty(val))tc.setBankAccount(val);
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				teacherErrorService.insert(te);
			}else{//无错误字段，保存信息
				try {
					tc.setUser(user);
					teacherErrorService.saveTeacher(tc);
					success++;
				} catch (Exception e) {
					logger.error("保存导师信息出错",e);
					fail++;
					teacherErrorService.insert(te);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importBackUser(XSSFSheet sheet,ImpInfo ii) {
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		Dict d = null;
		Office office=null;//学院
		Role role=null;//角色
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			AbsUser user=new AbsUser();
			int tag=0;//有几个错误字段
			BackUserError se=new BackUserError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if ("用户名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setLoginName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (UserUtils.getByLoginNameOrNo(val)!=null) {
						tag++;
						iie.setErrmsg("用户名已存在");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						se.setLoginName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setLoginName(val);
					}
				}else if ("用户类型".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					d=null;
					se.setUserType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("sys_user_type", val))==null||"1".equals(d.getValue())||"2".equals(d.getValue())) {
						tag++;
						iie.setErrmsg("用户类型不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)user.setUserType(d.getValue());
					}
				}else if ("角色".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setRoles(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					List<Role> list = new ArrayList<Role>();
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>1024) {
							tag++;
							iie.setErrmsg("角色内容过长");
							se.setRoles(null);
					}else {
						for(String v:getSetFromStr(val)) {
							if ((role=getRoleByName(v))==null) {
								tag++;
								iie.setErrmsg("角色不存在");
								break;
							}else{
								list.add(role);
							}
						}
					}

					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setRoleList(list);
					}
				}else if ("姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						se.setName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setName(val);
					}
				}else if ("工号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						se.setNo(null);
					}else if (UserUtils.isExistNo(val)) {
						tag++;
						iie.setErrmsg("工号已存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setNo(val);
					}
				}else if ("手机号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (!Pattern.matches(RegexUtils.mobileRegex, val)) {
						tag++;
						iie.setErrmsg("手机号格式不正确");
					}else if (UserUtils.isExistMobile(val)) {
						tag++;
						iie.setErrmsg("手机号已存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setMobile(val);
					}
				}else if ("邮箱".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setEmail(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.emailRegex, val)) {
							tag++;
							iie.setErrmsg("邮箱格式不正确");
						}else if (val.length()>200) {
							tag++;
							iie.setErrmsg("最多200个字符");
							se.setEmail(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setEmail(val);
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>255) {
						tag++;
						iie.setErrmsg("最多255个字符");
						se.setRemarks(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setRemarks(val);
					}
				}else if ("擅长技术领域".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setDomain(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					List<String> list = new ArrayList<String>();
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("擅长技术领域内容过长");
							se.setDomain(null);
						}else {
							String[] vs=val.split(",");
							for(String v:vs) {
								if ((d=DictUtils.getDictByLabel("technology_field", v))==null) {
									tag++;
									iie.setErrmsg("擅长技术领域不存在");
									break;
								}else{
									list.add(d.getValue());
								}
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setDomain(StringUtil.join(list.toArray(), ","));
					}
				}else if ("学校/学院".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					office=null;
					se.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((office=OfficeUtils.getOrgByName(val))==null) {
						tag++;
						iie.setErrmsg("学校/学院不存在");
					}else{
						if ("3".equals(user.getUserType())||"4".equals(user.getUserType())) {
							if (!"2".equals(office.getGrade())) {
								tag++;
								iie.setErrmsg("只能填学院");
							}
						}else if ("5".equals(user.getUserType())||"6".equals(user.getUserType())||"7".equals(user.getUserType())) {
							if (!"1".equals(office.getGrade())) {
								tag++;
								iie.setErrmsg("只能填学校");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setOffice(office);
						user.setProfessional(office.getId());
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				backUserErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					backUserErrorService.saveBackUser(user);
					success++;
				} catch (Exception e) {
					logger.error("保存后台用户信息出错",e);
					fail++;
					backUserErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importOrg(XSSFSheet sheet,ImpInfo ii) {
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		Office school=OfficeUtils.getSchool();//学校
		Office office=null;//学院
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			Office org=new Office();
			int tag=0;//有几个错误字段
			OfficeError se=new OfficeError();
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if ("学院名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					String prostr=getStringByCell(rowData.getCell(j+1),sheet);
					office=OfficeUtils.getOfficeByName(val);
					se.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (school==null) {
						tag++;
						iie.setErrmsg("学校不存在");
					}else if (StringUtil.isEmpty(prostr)&&office!=null) {
						tag++;
						iie.setErrmsg("该学院已存在");
					}else if (!StringUtil.isEmpty(prostr)&&office==null) {
						tag++;
						iie.setErrmsg("学院不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						org.setType("4");
						org.setGrade("2");
						org.setParent(school);
						org.setName(val);
					}
				}else if ("专业名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setProfessional(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (office!=null) {
							if (OfficeUtils.getProfessionalByName(office.getName(),val)!=null) {
								tag++;
								iie.setErrmsg("该专业已存在");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (!StringUtil.isEmpty(val)) {
							org.setType("5");
							org.setGrade("3");
							org.setParent(office);
							org.setName(val);
						}
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>255) {
						tag++;
						iie.setErrmsg("最多255个字符");
						se.setRemarks(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						org.setRemarks(val);
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				officeErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					officeService.save(org);
					success++;
				} catch (Exception e) {
					logger.error("保存机构信息出错",e);
					fail++;
					officeErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private Set<String> getSetFromStr(String val) {
		Set<String> set =new HashSet<String>();
		for(String s:val.split(",")) {
			set.add(s);
		}
		return set;
	}
	private String getStringByCell(XSSFCell cell,XSSFSheet sheet) {
		String ret=null;
		if (cell==null) {
			return ret;
		}else{

			switch (cell.getCellType())
            {
                case XSSFCell.CELL_TYPE_NUMERIC:// 数字
                	ret = cell.getRawValue();
	                break;
	            case XSSFCell.CELL_TYPE_STRING:// 字符串
	            	ret = cell.getStringCellValue();
	                break;
	            case XSSFCell.CELL_TYPE_FORMULA:// 公式
	            	FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
	            	ret = evaluator.evaluate(cell).getStringValue();
	            	if (StringUtil.isEmpty(ret)) {
	            		ret = String.valueOf(evaluator.evaluate(cell).getNumberValue());
	            	}
	                break;
	            default:
	            	ret = cell.getStringCellValue();
	                break;
	        }
			return ret;
		}
	}
	public Page<Map<String, String>> getList(Page<Map<String, String>> page, Map<String, Object> param) {
		if (page.getPageNo() <= 0) {
			page.setPageNo(1);
		}
		int count = dao.getListCount(param);
		param.put("offset", (page.getPageNo() - 1) * page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String, String>> list = null;
		if (count > 0) {
			list = dao.getList(param);
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}

	public ImpInfo get(String id) {
		return super.get(id);
	}

	public List<ImpInfo> findList(ImpInfo impInfo) {
		return super.findList(impInfo);
	}

	public Page<ImpInfo> findPage(Page<ImpInfo> page, ImpInfo impInfo) {
		return super.findPage(page, impInfo);
	}

	@Transactional(readOnly = false)
	public void save(ImpInfo impInfo) {
		super.save(impInfo);
	}

	@Transactional(readOnly = false)
	public void delete(ImpInfo impInfo) {
		super.delete(impInfo);
	}
	private Role getRoleByName(String name) {
		List<Role> list=UserUtils.getRoleList();
		if (!list.isEmpty()) {
			for(Role r:list) {
				if (r.getName().equals(name)) {
					return r;
				}
			}
		}
		return null;
	}
	private void importProject(XSSFSheet sheet,ImpInfo ii) {
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			List<AbsUser> leader=null;
			int tag=0;//有几个错误字段
			ProjectError se=new ProjectError();
			ProjectError validinfo=new ProjectError();//用于保存处理之后的信息，以免再次查找数据库.
			se.setImpId(ii.getId());
			se.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if ("立项年份".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setApprovingYear(val);
					validinfo.setApprovingYear(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>4) {
						tag++;
						iie.setErrmsg("最多4个字符");
						se.setApprovingYear(null);
					}else{
						try {
							DateUtil.parseDate(val+"-01-01","yyyy-MM-dd");
						} catch (ParseException e) {
							tag++;
							iie.setErrmsg("格式不正确");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("省（区、市）".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setProvince(val);
					validinfo.setProvince(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setProvince(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("高校代码".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setUniversityCode(val);
					validinfo.setUniversityCode(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setUniversityCode(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("高校名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setUniversityName(val);
					validinfo.setUniversityName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setUniversityName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目编号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setNumber(val);
					validinfo.setNumber(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setNumber(null);
					}else{
						List<ProjectDeclare> plist=projectDeclareService.getProjectByCdn(val, null, null);
						if (plist!=null&&!plist.isEmpty()) {
							tag++;
							iie.setErrmsg("该项目编号已经存在");
						}

					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					leader=userService.getStuByCdn(getStringByCell(rowData.getCell(j+3),sheet), getStringByCell(rowData.getCell(j+2),sheet));
					se.setName(val);
					validinfo.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						se.setName(null);
					}else if (leader!=null&&leader.size()==1) {
						List<ProjectDeclare> plist=projectDeclareService.getProjectByCdn(null, val, leader.get(0).getId());
						if (plist!=null&&!plist.isEmpty()) {
							tag++;
							iie.setErrmsg("该项目负责人下已存在相同项目名称的项目");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目类型".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					se.setType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setType(null);
					}else if ((d=DictUtils.getDictByLabel("project_type", val))==null) {
						tag++;
						iie.setErrmsg("项目类型不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setType(d.getValue());
					}
				}else if ("项目负责人姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setLeader(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setLeader(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目负责人学号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setLeaderNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						se.setLeaderNo(null);
					}else if (leader==null||leader.isEmpty()) {
						tag++;
						iie.setErrmsg("系统中未找到该项目负责人，请确认姓名和学号无误或先在系统中录入该项目负责人");
					}else if (leader!=null&&leader.size()>1) {
						tag++;
						iie.setErrmsg("系统中存在多个与此相同姓名、学号的学生，请联系管理员处理");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setLeaderNo(leader.get(0).getId());
					}
				}else if ("项目其他成员信息".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setTeamStuInfo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						List<String> temlist=new ArrayList<String>();
						String[] mebs=val.split(",");
						Map<String,String> map=new HashMap<String,String>();
						for(String meb:mebs) {
							String[] info=meb.split("/");
							if (info==null||info.length!=2) {
								tag++;
								iie.setErrmsg("格式有误");
								break;
							}else{
								List<AbsUser> mlist=userService.getStuByCdn(info[1], info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+",系统中未找到该项目成员，请确认姓名和学号无误或先在系统中录入该项目成员");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+",系统中存在多个与此相同姓名、学号的学生，请联系管理员处理");
									break;
								}else if(info[1].equals(se.getLeaderNo())){
									tag++;
									iie.setErrmsg(info[1]+"学号和负责人重复");
									break;
								}else if(map.get(info[1])!=null){
									tag++;
									iie.setErrmsg(info[1]+"成员学号重复");
									break;
								}else{
									map.put(info[1],info[1]);
									temlist.add(mlist.get(0).getId());
								}
							}
						}
						validinfo.setTeamStuInfo(StringUtil.join(temlist.toArray(), ","));
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("指导教师姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setTeacher(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else{
						Map<String,String> map=new HashMap<String,String>();
						List<String> temlist=new ArrayList<String>();
						String[] mebs=val.split(",");
						for(String meb:mebs) {
							String[] info=meb.split("/");
							if (info.length==1) {
								List<AbsUser> mlist=userService.getTeaByCdn(null, info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"，系统中未找到该导师，请确认姓名无误或先在系统中录入该导师");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"，系统中存在多个与此相同姓名的导师，请以姓名/工号的格式添加工号，多个导师以英文输入法逗号分隔");
									break;
								}else if(map.get(info[0])!=null){
									tag++;
									iie.setErrmsg(info[0]+"姓名重复");
									break;
								}else{
									map.put(info[0],info[0]);
									temlist.add(mlist.get(0).getId());
								}
							}else if (info.length==2) {
								List<AbsUser> mlist=userService.getTeaByCdn(info[1], info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+"，系统中未找到该导师，请确认姓名、工号无误或先在系统中录入该导师");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+"，系统中存在多个与此相同姓名、工号的导师，请联系管理员处理");
									break;
								}else if(map.get(info[0])!=null){
									tag++;
									iie.setErrmsg(info[0]+"姓名重复");
									break;
								}else if(map.get(info[1])!=null){
									tag++;
									iie.setErrmsg(info[1]+"工号重复");
									break;
								}else{
									map.put(info[0],info[0]);
									map.put(info[1],info[1]);
									temlist.add(mlist.get(0).getId());
								}
							}
						}
						validinfo.setTeacher(StringUtil.join(temlist.toArray(), ","));
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("财政拨款(元)".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setFinanceGrant(val);
					validinfo.setFinanceGrant(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						se.setFinanceGrant(null);
					}else if (!Pattern.matches(RegexUtils.grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入0或0以上的数，最多可有2位小数");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("校拨(元)".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setUniversityGrant(val);
					validinfo.setUniversityGrant(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						se.setUniversityGrant(null);
					}else if (!Pattern.matches(RegexUtils.grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入0或0以上的数，最多可有2位小数");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("总经费(元)".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setTotalGrant(val);
					validinfo.setTotalGrant(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>20) {
						tag++;
						iie.setErrmsg("最多20个字符");
						se.setTotalGrant(null);
					}else if (!Pattern.matches(RegexUtils.grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入0或0以上的数，最多可有2位小数");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目简介".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					se.setIntroduction(val);
					validinfo.setIntroduction(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(se.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>1024) {
						tag++;
						iie.setErrmsg("最多1024个字符");
						se.setIntroduction(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				projectErrorService.insert(se);
			}else{//无错误字段，保存信息
				try {
					projectErrorService.saveProject(validinfo);
					success++;
				} catch (Exception e) {
					logger.error("保存项目信息出错",e);
					fail++;
					projectErrorService.insert(se);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importGcontest(XSSFSheet sheet,ImpInfo ii) {
		Office office=null;
		Office profes=null;
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			int tag=0;//有几个错误字段
			GcontestError phe=new GcontestError();
			GcontestError validinfo=new GcontestError();//用于保存处理之后的信息，以免再次查找数据库.
			phe.setImpId(ii.getId());
			phe.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if(val!=null){//去掉所有空格
					val=val.replaceAll(" ", "");
				}
				if ("参赛项目名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setName(val);
					validinfo.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						phe.setName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("大赛类别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					phe.setType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						phe.setType(null);
					}else if ((d=DictUtils.getDictByLabel("competition_net_type", val))==null) {
						tag++;
						iie.setErrmsg("大赛类别不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setType(d.getValue());
					}
				}else if ("参赛组别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					phe.setGroups(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						phe.setGroups(null);
					}else if ((d=DictUtils.getDictByLabel("gcontest_level", val))==null) {
						tag++;
						iie.setErrmsg("参赛组别不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setGroups(d.getValue());
					}
				}else if ("申报人/学号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setLeader(val);
					validinfo.setLeader(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						phe.setLeader(null);
					}else if (val.split("/").length!=2) {
						tag++;
						iie.setErrmsg("格式有误");
					}else{
						AbsUser u=userService.getByNo(val.split("/")[1]);
						if(u!=null&&!"1".equals(u.getUserType())){
							tag++;
							iie.setErrmsg("找到该学号人员，但不是学生");
						}else if(u!=null&&!val.split("/")[0].equals(u.getName())){
							tag++;
							iie.setErrmsg("申报人学号和姓名不一致");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("所属学院".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					office=null;
					phe.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((office=OfficeUtils.getOfficeByName(val))==null) {
						tag++;
						iie.setErrmsg("学院不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setOffice(office.getId());
					}
				}else if ("所属专业".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					profes=null;
					phe.setProfes(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (office!=null&&(profes=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
							tag++;
							iie.setErrmsg("专业不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (profes!=null)validinfo.setProfes(profes.getId());
					}
				}else if ("申报人手机".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setMobile(val);
					validinfo.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.mobileRegex, val)) {
							tag++;
							iie.setErrmsg("手机号格式不正确");
						}else{
							AbsUser u=new AbsUser();
							u.setMobile(val);
							AbsUser temu=userService.getByMobile(u);
							if(temu!=null&&phe.getLeader()!=null&&phe.getLeader().split("/").length==2&&!phe.getLeader().split("/")[1].equals(temu.getNo())){
								tag++;
								iie.setErrmsg("手机号已存在");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("校内导师/工号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setSteachers(val);
					validinfo.setSteachers(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>256) {
						tag++;
						iie.setErrmsg("最多256个字符");
						phe.setSteachers(null);
					}else{
						String s=checkSTeachers(val);
						if(s!=null){
							tag++;
							iie.setErrmsg(s);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("企业导师/工号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setEteachers(val);
					validinfo.setEteachers(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>256) {
							tag++;
							iie.setErrmsg("最多256个字符");
							phe.setEteachers(null);
						}else{
							String s=checkETeachers(val,getMapFromStr(phe.getSteachers()));
							if(s!=null){
								tag++;
								iie.setErrmsg(s);
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setRemarks(val);
					validinfo.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("最多1024个字符");
							phe.setRemarks(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("荣获奖项".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					phe.setHuojiang(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>64) {
							tag++;
							iie.setErrmsg("最多64个字符");
							phe.setHuojiang(null);
						}else if ((d=DictUtils.getDictByLabel("competition_college_prise", val))==null) {
							tag++;
							iie.setErrmsg("荣获奖项不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if(d!=null)validinfo.setHuojiang(d.getValue());
					}
				}
			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				gcontestErrorService.insert(phe);
			}else{//无错误字段，保存信息
				try {
					gcontestErrorService.saveGcontest(validinfo);
					success++;
				} catch (Exception e) {
					logger.error("保存项目信息出错",e);
					fail++;
					gcontestErrorService.insert(phe);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private void importHsProject(XSSFSheet sheet,ImpInfo ii) {
		Office office=null;
		Office profes=null;
		XSSFRow rowData;
		ImpInfoErrmsg iie;
		int fail=0;//失败数
		int success=0;//成功数
		//转换、校验所有字段并塞入要用到的各种对象。最后根据校验的结果判断要保存什么对象
		for (int i = ImpDataController.descHeadRow+1; i < sheet.getLastRowNum() + 1; i++) {
			int tag=0;//有几个错误字段
			ProjectHsError phe=new ProjectHsError();
			ProjectHsError validinfo=new ProjectHsError();//用于保存处理之后的信息，以免再次查找数据库.
			phe.setImpId(ii.getId());
			phe.setId(IdGen.uuid());
			rowData = sheet.getRow(i);
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种*/
			int validcell=0;
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				if (!StringUtil.isEmpty(getStringByCell(rowData.getCell(j),sheet))) {
					validcell++;
					break;
				}
			}
			if (validcell==0) {
				continue;
			}
			/*判断这一行数据是不是都是空，文件中是删除数据未删除行的那种end*/
			for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
				String val=getStringByCell(rowData.getCell(j),sheet);
				if(val!=null){//去掉所有空格
					val=val.replaceAll(" ", "");
				}
				if ("学院".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					office=null;
					phe.setOffice(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((office=OfficeUtils.getOfficeByName(val))==null) {
						tag++;
						iie.setErrmsg("学院不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setOffice(office.getId());
					}
				}else if ("项目名称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setName(val);
					validinfo.setName(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						phe.setName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目编号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setNumber(val);
					validinfo.setNumber(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						phe.setNumber(null);
					}else{
						List<ProjectDeclare> plist=projectDeclareService.getProjectByCdn(val, null, null);
						if (plist!=null&&!plist.isEmpty()) {
							tag++;
							iie.setErrmsg("该项目编号已经存在");
						}

					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("项目类型".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					phe.setType(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>64) {
						tag++;
						iie.setErrmsg("最多64个字符");
						phe.setType(null);
					}else if ((d=DictUtils.getDictByLabel("project_type", val))==null) {
						tag++;
						iie.setErrmsg("项目类型不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setType(d.getValue());
					}
				}else if ("负责人姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setLeader(val);
					validinfo.setLeader(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						phe.setLeader(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("学号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setNo(val);
					validinfo.setNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						phe.setNo(null);
					}else{
						AbsUser u=userService.getByNo(val);
						if(u!=null&&!"1".equals(u.getUserType())){
							tag++;
							iie.setErrmsg("找到该学号人员，但不是学生");
						}else if(u!=null&&phe.getLeader()!=null&&!phe.getLeader().equals(u.getName())){
							tag++;
							iie.setErrmsg("负责人学号和姓名不一致");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("联系电话".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setMobile(val);
					validinfo.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.mobileRegex, val)) {
							tag++;
							iie.setErrmsg("手机号格式不正确");
						}else{
							AbsUser u=new AbsUser();
							u.setMobile(val);
							AbsUser temu=userService.getByMobile(u);
							if(temu!=null&&phe.getNo()!=null&&!phe.getNo().equals(temu.getNo())){
								tag++;
								iie.setErrmsg("手机号已存在");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("电子邮箱".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setEmail(val);
					validinfo.setEmail(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(RegexUtils.emailRegex, val)) {
							tag++;
							iie.setErrmsg("邮箱格式不正确");
						}else if (val.length()>128) {
							tag++;
							iie.setErrmsg("最多128个字符");
							phe.setEmail(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("专业".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					profes=null;
					phe.setProfes(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (office!=null&&(profes=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
							tag++;
							iie.setErrmsg("专业不存在");
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (profes!=null)validinfo.setProfes(profes.getId());
					}
				}else if ("年级".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setGrade(val);
					validinfo.setGrade(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if(!checkYear(val)){
							tag++;
							iie.setErrmsg("年级填写有误");
						}else if (val.length()>12) {
							tag++;
							iie.setErrmsg("最多12个字符");
							phe.setGrade(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("团队成员及学号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setMembers(val);
					validinfo.setMembers(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>256) {
							tag++;
							iie.setErrmsg("最多256个字符");
							phe.setMembers(null);
						}else{
							String s=checkMembers(val,phe.getNo());
							if(s!=null){
								tag++;
								iie.setErrmsg(s);
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("第一指导教师姓名".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setTeachers(val);
					validinfo.setTeachers(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						phe.setTeachers(null);
					}else{
						String temval=getStringByCell(rowData.getCell(j+1),sheet);
						if(temval!=null){//去掉所有空格
							temval=temval.replaceAll(" ", "");
						}
						String s=checkTeaName(val, temval);
						if(s!=null){
							tag++;
							iie.setErrmsg(s);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("指导教师工号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setTeaNo(val);
					validinfo.setTeaNo(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>128) {
						tag++;
						iie.setErrmsg("最多128个字符");
						phe.setTeaNo(null);
					}else{
						String s=checkTeaNo(phe.getTeachers(), val);
						if(s!=null){
							tag++;
							iie.setErrmsg(s);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("职称".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setTeaTitle(val);
					validinfo.setTeaTitle(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>100) {
							tag++;
							iie.setErrmsg("最多100个字符");
							phe.setTeaTitle(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}else if ("级别".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					Dict d=null;
					phe.setLevel(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						phe.setLevel(null);
					}else if ((d=DictUtils.getDictByLabel("project_degree", val))==null) {
						tag++;
						iie.setErrmsg("项目级别不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						validinfo.setLevel(d.getValue());
					}
				}else if ("备注".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					phe.setRemarks(val);
					validinfo.setRemarks(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(phe.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (val.length()>1024) {
							tag++;
							iie.setErrmsg("最多1024个字符");
							phe.setRemarks(null);
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}
				}

			}
			if (tag!=0) {//有错误字段,记录错误信息
				fail++;
				projectHsErrorService.insert(phe);
			}else{//无错误字段，保存信息
				try {
					projectHsErrorService.saveProject(validinfo);
					success++;
				} catch (Exception e) {
					logger.error("保存项目信息出错",e);
					fail++;
					projectHsErrorService.insert(phe);
				}
			}
			ii.setFail(fail+"");
			ii.setSuccess(success+"");
			ii.setTotal((fail+success)+"");
			CacheUtils.put(CacheUtils.IMPDATA_CACHE, ii.getId(), ii);
		}
		ii.setIsComplete("1");
		impInfoService.save(ii);
		CacheUtils.remove(CacheUtils.IMPDATA_CACHE, ii.getId());
	}
	private boolean checkYear(String year){
		try {
		  new SimpleDateFormat(YRAR).parse(year);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	private String checkTeaName(String teaName,String teaNo){
		if(StringUtil.isEmpty(teaNo)){
			return null;
		}
		List<String> lname=new ArrayList<String>();
		List<String> lno=new ArrayList<String>();
		for(String s:teaName.split("、")){
			if(StringUtil.isNotEmpty(s)){
				lname.add(s);
			}
		}
		for(String s:teaNo.split("、")){
			if(StringUtil.isNotEmpty(s)){
				lno.add(s);
			}
		}
		if(lname.size()<lno.size()){
			return "请填写导师姓名";
		}
		return null;
	}
	private String checkTeaNo(String teaName,String teaNo){
		if(StringUtil.isEmpty(teaName)){
			return null;
		}
		List<String> lname=new ArrayList<String>();
		List<String> lno=new ArrayList<String>();
		for(String s:teaName.split("、")){
			if(StringUtil.isNotEmpty(s)){
				lname.add(s);
			}
		}
		for(String s:teaNo.split("、")){
			if(StringUtil.isNotEmpty(s)){
				lno.add(s);
			}
		}
		if(lname.size()>lno.size()){
			return "请填写导师工号";
		}else if(lname.size()==lno.size()){
			Map<String,String> map=new HashMap<String,String>();
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<lname.size();i++){
				AbsUser u=userService.getByNo(lno.get(i));
				if(u!=null&&!"2".equals(u.getUserType())){
					sb.append(lname.get(i)).append(lno.get(i)).append("找到该工号人员，但不是导师;");
				}else if(u!=null&&!lname.get(i).equals(u.getName())){
					sb.append(lname.get(i)).append(lno.get(i)).append("姓名工号不一致;");
				}else if(map.get(lno.get(i))!=null){
					sb.append(lno.get(i)).append("工号重复;");
				}else{
					map.put(lno.get(i),lno.get(i));
				}
			}
			if(StringUtil.isNotEmpty(sb)){
				return sb.toString();
			}
		}
		return null;
	}
	private String checkSTeachers(String teachers){
		StringBuffer sb=new StringBuffer();
		String[] list=teachers.split("、");
		if(list!=null&&list.length>0){
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<list.length;i++){
				String[] tea=list[i].split("/");
				if(tea.length!=2){
					return "格式有误";
				}else if(StringUtil.isEmpty(tea[0])){
					return "请填写校内导师名称";
				}else if(tea[0].length()>100){
					return "校内导师名称最多100个字符";
				}else if(StringUtil.isEmpty(tea[1])){
					return "请填写校内导师工号";
				}else if(tea[1].length()>100){
					return "校内导师工号最多100个字符";
				}else if(map.get(tea[1])!=null){
					return tea[1]+"校内导师工号重复";
				}else{
					AbsUser u=userService.getByNo(tea[1]);
					if(u!=null&&!"2".equals(u.getUserType())){
						sb.append(tea[0]).append(tea[1]).append("找到该工号人员，但不是校内导师;");
					}if(u!=null&&"2".equals(u.getUserType())&&backTeacherExpansionDao.findTeacherByUserIdAndType(u.getId(), "1")==null){
						sb.append(tea[0]).append(tea[1]).append("找到该工号人员，但不是校内导师;");
					}else if(u!=null&&!tea[0].equals(u.getName())){
						sb.append(tea[0]).append(tea[1]).append("姓名工号不一致;");
					}else{
						map.put(tea[1], tea[1]);
					}
				}
			}
		}
		if(StringUtil.isNotEmpty(sb)){
			return sb.toString();
		}
		return null;
	}
	private Map<String,String> getMapFromStr(String steas){
		Map<String,String> map=new HashMap<String,String>();
		if(StringUtil.isNotEmpty(steas)){
			String[] list=steas.split("、");
			if(list!=null&&list.length>0){
				for(int i=0;i<list.length;i++){
					String[] tea=list[i].split("/");
					if(tea.length==2){
						map.put(tea[1], tea[1]);
					}
				}
			}
		}
		return map;
	}
	private String checkETeachers(String teachers,Map<String,String> steas){
		StringBuffer sb=new StringBuffer();
		String[] list=teachers.split("、");
		if(list!=null&&list.length>0){
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<list.length;i++){
				String[] tea=list[i].split("/");
				if(tea.length!=2){
					return "格式有误";
				}else if(StringUtil.isEmpty(tea[0])){
					return "请填写企业导师名称";
				}else if(tea[0].length()>100){
					return "企业导师名称最多100个字符";
				}else if(StringUtil.isEmpty(tea[1])){
					return "请填写企业导师工号";
				}else if(tea[1].length()>100){
					return "企业导师工号最多100个字符";
				}else if(map.get(tea[1])!=null){
					return tea[1]+"企业导师工号重复";
				}else if(steas.get(tea[1])!=null){
					return tea[1]+"企业导师工号和校内导师工号重复";
				}else{
					AbsUser u=userService.getByNo(tea[1]);
					if(u!=null&&!"2".equals(u.getUserType())){
						sb.append(tea[0]).append(tea[1]).append("找到该工号人员，但不是企业导师;");
					}if(u!=null&&"2".equals(u.getUserType())&&backTeacherExpansionDao.findTeacherByUserIdAndType(u.getId(), "2")==null){
						sb.append(tea[0]).append(tea[1]).append("找到该工号人员，但不是企业导师;");
					}else if(u!=null&&!tea[0].equals(u.getName())){
						sb.append(tea[0]).append(tea[1]).append("姓名工号不一致;");
					}else{
						map.put(tea[1], tea[1]);
					}
				}
			}
		}
		if(StringUtil.isNotEmpty(sb)){
			return sb.toString();
		}
		return null;
	}
	private String checkMembers(String members,String leaderno){
		StringBuffer sb=new StringBuffer();
		List<String[]> list=getMembersData(members);
		if(list!=null&&list.size()>0){
			Map<String,String> map=new HashMap<String,String>();
			for(String[] mem:list){
				if(StringUtil.isEmpty(mem[0])){
					return "请填写成员名称";
				}else if(mem[0].length()>100){
					return "成员名称最多100个字符";
				}else if(StringUtil.isEmpty(mem[1])){
					return "请填写成员学号";
				}else if(mem[1].length()>100){
					return "成员学号最多100个字符";
				}else if(mem[1].equals(leaderno)){
					return "成员学号和负责人学号重复";
				}else{
					AbsUser u=userService.getByNo(mem[1]);
					if(u!=null&&!"1".equals(u.getUserType())){
						sb.append(mem[0]).append(mem[1]).append("找到该学号人员，但不是学生;");
					}else if(u!=null&&!mem[0].equals(u.getName())){
						sb.append(mem[0]).append(mem[1]).append("姓名学号不一致;");
					}else if(map.get(mem[1])!=null){
						sb.append(mem[1]).append("学号重复;");
					}else{
						map.put(mem[1], mem[1]);
					}
				}
			}
			if(StringUtil.isNotEmpty(sb)){
				return sb.toString();
			}
		}
		return null;
	}
	//解析名称学号
	public static List<String[]> getMembersData(String members){
		String regxpForTag = "\\d+" ;
        Pattern patternForTag = Pattern.compile (regxpForTag,Pattern.CASE_INSENSITIVE );
		if(StringUtil.isNotEmpty(members)){
			List<String[]> list=new ArrayList<String[]>();
			for(String mem:members.split(",")){
				if(StringUtil.isNotEmpty(mem)){
					String[] ss=new String[2];
					Matcher matcherForTag = patternForTag.matcher(mem);
			        if(matcherForTag.find()) {
			        	ss[1]=matcherForTag.group(0);
			        	ss[0]=mem.substring(0, matcherForTag.start());
			        	list.add(ss);
			        }
				}
			}
			return list;
		}else{
			return null;
		}
	}
}