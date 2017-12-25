/**
 *
 */
package com.oseasy.initiate.modules.sys.entity;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.supcan.annotation.treelist.cols.SupCol;
import com.oseasy.initiate.common.utils.Collections3;
import com.oseasy.initiate.common.utils.excel.annotation.ExcelField;
import com.oseasy.initiate.common.utils.excel.fieldtype.RoleListType;
import com.oseasy.initiate.modules.sys.utils.DictUtils;

/**
 * 用户Entity


 */
public class User extends DataEntity<User> {

	private static final long serialVersionUID = 1L;
	private Office company;	// 归属公司
	private Office office;	// 归属 学院
	private String loginName;// 登录名
	private String password;// 密码
	private String no;		// 工号 学号
	private String name;	// 姓名
	private String email;	// 邮箱
	private String phone;	// 电话
	private String mobile;	// 手机
	private String userType;// 用户类型    1学生 2导师 3 教学秘书 4院级专家 5校级专家 6校级管理员
	private String loginIp;	// 最后登陆IP
	private Date loginDate;	// 最后登陆日期
	private String loginFlag;	// 是否允许登陆
	private String photo;	// 头像

	private String oldLoginName;// 原登录名
	private String newPassword;	// 新密码

	private String oldLoginIp;	// 上次登陆IP
	private Date oldLoginDate;	// 上次登陆日期

	private String systemType; //允许登陆的系统类型 0 前台  1后台

	private Role role;	// 根据角色查询用户条件

	private String degree;//学位；  1：学士2：硕士3：博士
	private String education;//学历
	private String area;//地区
	private  int sex; //0 女 1 男
	private Date birthday;//生日
	private String professional;//专业
	private String domain;//擅长/技术领域
	private String idType;//证件
	private String national;//民族
	private String political;//政治面貌
	private String country;//国家
	private String idNumber;//身份证

	private List<String> domainIdList;//
	private String domainlt;//擅长/技术领域,显示列表时用

	private String roleId;//根据角色查询时用
	private String postCode;//邮编

	private String teacherType;//导师类型

	private int age;

	public String getAge() {
		if (birthday!=null) {
			Date date=new Date();
			long day=(date.getTime()-birthday.getTime())/(24*60*60*1000) + 1;
			String year=new DecimalFormat("#").format(day/365f);
			return year;
		}

		return "";
	}


	public String getTeacherType() {
		return teacherType;
	}
	public void setTeacherType(String teacherType) {
		this.teacherType = teacherType;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getNational() {
		return national;
	}

	public void setNational(String national) {
		this.national = national;
	}

	public String getPolitical() {
		return political;
	}

	public void setPolitical(String political) {
		this.political = political;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}



	public String getDomainlt() {
		if (StringUtils.isNotBlank(domain)) {
			String domainNames = DictUtils.getDictLabels(domain, "technology_field", "未知");
			return domainNames;
		}
		return domainlt;
	}

	public void setDomainlt(String domainlt) {
		this.domainlt = domainlt;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

	public User() {
		super();
		this.loginFlag = Global.YES;
	}

	public User(String id) {
		super(id);
	}

	public User(String id, String loginName) {
		super(id);
		this.loginName = loginName;
	}

	public User(Role role) {
		super();
		this.role = role;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

//	@SupCol(isUnique="true", isHide="true")
//	@ExcelField(title="ID", type=1, align=2, sort=1)
//	public String getId() {
//		return id;
//	}

	@JsonIgnore
	@NotNull(message="归属公司不能为空")
	@ExcelField(title="归属公司", align=2, sort=20)
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	@JsonIgnore
	@NotNull(message="归属部门不能为空")
	@ExcelField(title="归属部门", align=2, sort=25)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min=1, max=100, message="登录名长度必须介于 1 和 100 之间")
	@ExcelField(title="登录名", align=2, sort=30)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@JsonIgnore
	@Length(min=1, max=100, message="密码长度必须介于 1 和 100 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Length(min=1, max=100, message="姓名长度必须介于 1 和 100 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getName() {
		return name;
	}

	@Length(min=1, max=100, message="工号长度必须介于 1 和 100 之间")
	@ExcelField(title="工号", align=2, sort=45)
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Email(message="邮箱格式不正确")
	@Length(min=0, max=200, message="邮箱长度必须介于 1 和 200 之间")
	@ExcelField(title="邮箱", align=1, sort=50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min=0, max=200, message="电话长度必须介于 1 和 200 之间")
	@ExcelField(title="电话", align=2, sort=60)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200, message="手机长度必须介于 1 和 200 之间")
	@ExcelField(title="手机", align=2, sort=70)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@ExcelField(title="备注", align=1, sort=900)
	public String getRemarks() {
		return remarks;
	}

	@Length(min=0, max=100, message="用户类型长度必须介于 1 和 100 之间")
	@ExcelField(title="用户类型", align=2, sort=80, dictType="sys_user_type")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@ExcelField(title="创建时间", type=0, align=1, sort=90)
	public Date getCreateDate() {
		return createDate;
	}

	@ExcelField(title="最后登录IP", type=1, align=1, sort=100)
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="最后登录日期", type=1, align=1, sort=110)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null) {
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null) {
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	@ExcelField(title="拥有角色", align=1, sort=800, fieldType=RoleListType.class)
	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@JsonIgnore
	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
	}

	public List<String> getDomainIdList() {

		if (StringUtils.isNotBlank(domain)) {
			String [] domainArray = domain.split(",");
			domainIdList = Lists.newArrayList();
			for(String id:domainArray) {
				domainIdList.add(id);
			}
		}

		return domainIdList;
	}

	public void setDomainIdList(List<String> domainIdList) {
		//this.domainIdList = domainIdList;
		//domainIdList = Lists.newArrayList();
		if (domainIdList!=null&&domainIdList.size()>0) {
		StringBuffer strbuff = new StringBuffer();
		for(String domainId:domainIdList) {
			strbuff.append(domainId);
			strbuff.append(",");
		}
		String domainIds=strbuff.substring(0, strbuff.lastIndexOf(","));
		//String domainNames = DictUtils.getDictLabels(domainIds, "technology_field", "0");
		//setDomain(domainNames);
		setDomain(domainIds);
		}
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		//System.out.println("===========roleList.size()"+roleList.size());
		return Collections3.extractToString(roleList, "name", ",");
	}

	public boolean getAdmin() {
		return getAdmin(this.id);
	}

	public static boolean getAdmin(String id) {
		return id != null && "1".equals(id);
	}

	@Override
	public String toString() {
		return id;
	}
}