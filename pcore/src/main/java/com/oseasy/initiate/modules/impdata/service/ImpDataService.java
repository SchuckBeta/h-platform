package com.oseasy.initiate.modules.impdata.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.impdata.dao.ImpInfoDao;
import com.oseasy.initiate.modules.impdata.entity.BackUserError;
import com.oseasy.initiate.modules.impdata.entity.ImpInfo;
import com.oseasy.initiate.modules.impdata.entity.ImpInfoErrmsg;
import com.oseasy.initiate.modules.impdata.entity.OfficeError;
import com.oseasy.initiate.modules.impdata.entity.ProjectError;
import com.oseasy.initiate.modules.impdata.entity.StudentError;
import com.oseasy.initiate.modules.impdata.entity.TeacherError;
import com.oseasy.initiate.modules.impdata.exception.ImpDataException;
import com.oseasy.initiate.modules.impdata.web.ImpDataController;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.OfficeUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 导入数据信息表Service
 * 
 * @author 9527
 * @version 2017-05-16
 */
@Service
public class ImpDataService extends CrudService<ImpInfoDao, ImpInfo> {
	public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
	@Autowired
	private OfficeService officeService;
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
	private UserService userService;
	@Autowired
	private ImpInfoErrmsgService impInfoErrmsgService;
	private static  String mobileRegex = "^0?(13[0-9]|15[012356789]|18[0-9]|17[0-9])[0-9]{8}$";
	private static  String emailRegex = "^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.) {1,63}[a-z0-9]+$";
	private static  String grantRegex="^\\+?[1-9][0-9]*$";
	public static Logger logger = Logger.getLogger(ImpDataService.class);
	private void checkTemplate(XSSFSheet datasheet,HttpServletRequest request) throws ImpDataException, IOException{
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
			try {
				for(int j=0;j<sheet.getRow(ImpDataController.descHeadRow).getLastCellNum();j++) {
					if (!getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet).equals(getStringByCell(datasheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
						throw new ImpDataException("模板错误,请下载最新的模板");
					}
				}
			} catch (Exception e) {
				throw new ImpDataException("模板错误,请下载最新的模板");
			}
		} catch (ImpDataException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}  finally {
			try {
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
				ii.setImpTpye("1");
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				fixedThreadPool.execute(new Thread() {
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
				ii.setImpTpye("2");
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				fixedThreadPool.execute(new Thread() {
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
				ii.setImpTpye("3");
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				fixedThreadPool.execute(new Thread() {
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
				ii.setImpTpye("4");
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				fixedThreadPool.execute(new Thread() {
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
				ii.setImpTpye("5");
				ii.setTotal((sheet.getLastRowNum()-ImpDataController.descHeadRow)+"");
				ii.setFail("0");
				ii.setSuccess("0");
				ii.setIsComplete("0");
				impInfoService.save(ii);//插入导入信息
				fixedThreadPool.execute(new Thread() {
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
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
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
			User user=new User();
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (!Pattern.matches(mobileRegex, val)) {
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
						if (!Pattern.matches(emailRegex, val)) {
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("id_type", val))==null) {
						tag++;
						iie.setErrmsg("证件类别不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>32) {
						tag++;
						iie.setErrmsg("最多32个字符");
						se.setIdNo(null);
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
						if (d!=null)user.setSex(Integer.parseInt(d.getValue()));
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("degree_type", val))==null) {
						tag++;
						iie.setErrmsg("学位不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("enducation_level", val))==null) {
						tag++;
						iie.setErrmsg("学历不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (office!=null&&(professional=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
						tag++;
						iie.setErrmsg("专业不存在");
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
					try {
						st.setEnterdate(DateUtil.parseDate(val,"yyyy-MM-dd"));
					} catch (ParseException e) {
						tag++;
						iie.setErrmsg("日期格式不正确");
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
					try {
						st.setTemporaryDate(DateUtil.parseDate(val,"yyyy-MM-dd"));
					} catch (ParseException e) {
						tag++;
						iie.setErrmsg("日期格式不正确");
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
					try {
						st.setGraduation(DateUtil.parseDate(val,"yyyy-MM-dd"));
					} catch (ParseException e) {
						tag++;
						iie.setErrmsg("日期格式不正确");
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
			User user=new User();
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (UserUtils.getByLoginNameOrNo(val)!=null) {
						tag++;
						iie.setErrmsg("用户名已存在");
					}else if (val.length()>100) {
						tag++;
						iie.setErrmsg("最多100个字符");
						te.setLoginName(null);
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						user.setLoginName(val);
					}
				}else if ("导师类型".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
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
						iie.setErrmsg("导师类型不存在");
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
						if (d!=null)user.setSex(Integer.parseInt(d.getValue()));
					}
				}else if ("手机号".equals(getStringByCell(sheet.getRow(ImpDataController.descHeadRow).getCell(j),sheet))) {
					te.setMobile(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (!Pattern.matches(mobileRegex, val)) {
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
					te.setEmail(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)) {
						if (!Pattern.matches(emailRegex, val)) {
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("id_type", val))==null) {
						tag++;
						iie.setErrmsg("证件类别不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (val.length()>32) {
						tag++;
						iie.setErrmsg("最多32个字符");
						te.setIdNo(null);
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("enducation_type", val))==null) {
						tag++;
						iie.setErrmsg("学历类别不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("degree_type", val))==null) {
						tag++;
						iie.setErrmsg("学位不存在");
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
					if (StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if ((d=DictUtils.getDictByLabel("enducation_level", val))==null) {
						tag++;
						iie.setErrmsg("学历不存在");
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
					if ("1".equals(tc.getTeachertype())&&StringUtil.isEmpty(val)) {
						tag++;
						iie.setErrmsg("必填信息");
					}else if (!StringUtil.isEmpty(val)&&office!=null&&(professional=OfficeUtils.getProfessionalByName(office.getName(),val))==null) {
						tag++;
						iie.setErrmsg("专业不存在");
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
					d=null;
					te.setServiceIntention(val);
					iie=new ImpInfoErrmsg();
					iie.setImpId(ii.getId());
					iie.setDataId(te.getId());
					iie.setColname(j+"");
					if (!StringUtil.isEmpty(val)&&(d=DictUtils.getDictByLabel("master_help", val))==null) {
						tag++;
						iie.setErrmsg("服务意向不存在");
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (d!=null)tc.setServiceIntention(d.getValue());
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
						if (val.length()>11) {
							tag++;
							iie.setErrmsg("最多11个字符");
							te.setBankAccount(null);
						}else{
							try {
								Integer.parseInt(val);
							} catch (Exception e) {
								tag++;
								iie.setErrmsg("只能包含数字");
							}
						}
					}
					if (StringUtil.isNotEmpty(iie.getErrmsg())) {
						impInfoErrmsgService.save(iie);
					}else{
						if (!StringUtil.isEmpty(val))tc.setBankAccount(Integer.parseInt(val));
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
			User user=new User();
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
					}else if (!Pattern.matches(mobileRegex, val)) {
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
						if (!Pattern.matches(emailRegex, val)) {
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
			DecimalFormat df = new DecimalFormat("#");  
	  
			switch (cell.getCellType())  
            {  
                case XSSFCell.CELL_TYPE_NUMERIC:// 数字  
                	ret = df.format(cell.getNumericCellValue());  
	                break;  
	            case XSSFCell.CELL_TYPE_STRING:// 字符串  
	            	ret = cell.getStringCellValue();  
	                break;
	            case XSSFCell.CELL_TYPE_FORMULA:// 公式 
	            	FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
	            	ret = evaluator.evaluate(cell).getStringValue();
	            	if (StringUtil.isEmpty(ret)) {
	            		ret = df.format(evaluator.evaluate(cell).getNumberValue());
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
			List<User> leader=null;
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
						for(String meb:mebs) {
							String[] info=meb.split("/");
							if (info==null||info.length!=2) {
								tag++;
								iie.setErrmsg("格式有误");
								break;
							}else{
								List<User> mlist=userService.getStuByCdn(info[1], info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+",系统中未找到该项目成员，请确认姓名和学号无误或先在系统中录入该项目成员");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+",系统中存在多个与此相同姓名、学号的学生，请联系管理员处理");
									break;
								}else{
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
						List<String> temlist=new ArrayList<String>();
						String[] mebs=val.split(",");
						for(String meb:mebs) {
							String[] info=meb.split("/");
							if (info.length==1) {
								List<User> mlist=userService.getTeaByCdn(null, info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"，系统中未找到该导师，请确认姓名无误或先在系统中录入该导师");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"，系统中存在多个与此相同姓名的导师，请以姓名/工号的格式添加工号，多个导师以英文输入法逗号分隔");
									break;
								}else{
									temlist.add(mlist.get(0).getId());
								}
							}else if (info.length==2) {
								List<User> mlist=userService.getTeaByCdn(info[1], info[0]);
								if (mlist==null||mlist.isEmpty()) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+"，系统中未找到该导师，请确认姓名、工号无误或先在系统中录入该导师");
									break;
								}else if (mlist!=null&&mlist.size()>1) {
									tag++;
									iie.setErrmsg(info[0]+"/"+info[1]+"，系统中存在多个与此相同姓名、工号的导师，请联系管理员处理");
									break;
								}else{
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
					}else if (!Pattern.matches(grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入正整数");
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
					}else if (!Pattern.matches(grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入正整数");
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
					}else if (!Pattern.matches(grantRegex, val)) {
						tag++;
						iie.setErrmsg("请输入正整数");
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
}