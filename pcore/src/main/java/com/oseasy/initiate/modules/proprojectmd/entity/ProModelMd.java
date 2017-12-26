package com.oseasy.initiate.modules.proprojectmd.entity;

import com.oseasy.initiate.modules.promodel.entity.ProModel;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * proProjectMdEntity.
 * @author zy
 * @version 2017-09-18
 */
public class ProModelMd extends DataEntity<ProModelMd> {

	private static final long serialVersionUID = 1L;
	private ProModel proModel;		// model_id
	private String modelId;		// model_id
	private Float appAmount;		// 数字 申请金额 例如：1000000
	private String subject;		// 学科：
	private String subjectName;		// 学科：
	private String appLevel;		// 属性：国家级（含省级备选项目）、&ldquo;校级
	private String appLevelName;		// 属性：国家级（含省级备选项目）、&ldquo;校级
	private String proSource;		// 项目来源：            A为学生自主选题，来源于自己对课题长期积累与兴趣；            B为学生来源于教师科研项目选题；            C为学生承担社会、企业委托项目选题。
	private String sourceProjectName;		// 来源项目名称：限B和C的项目
	private String sourceProjectType;		// 来源项目类别：            写&ldquo;863项目&rdquo;            、&ldquo;973项目&rdquo;、            &ldquo;国家自然科学基金项目&rdquo;、            &ldquo;省级自然科学基金项目&rdquo;、            &ldquo;教师横向科研项目&rdquo;、            &ldquo;企业委托项目&rdquo;、            &ldquo;社会委托项目&rdquo;            以及其他项目标识。
	private String iisIncubation;		// 是否入孵
	private String stageResult;		// 阶段成果：中期
	private String result;		// 结项成果：结项
	private Date appBeginDate;		// 申报开始时间
	private Date appEndDate;		// 申报结束时间
	private String reimbursementAmount;		// 数字  报销金额 例如：1000000

	private String setState;		// 立项状态
	private String midState;		// 中期状态
	private String closeState;		// 结项状态
	private String fileUrl;
	private String fileId;
	public String getAppLevelName() {
    return appLevelName;
  }

  public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

public void setAppLevelName(String appLevelName) {
    this.appLevelName = appLevelName;
  }

  public String getSetState() {
		return setState;
	}

	public void setSetState(String setState) {
		this.setState = setState;
	}

	public String getMidState() {
		return midState;
	}

	public void setMidState(String midState) {
		this.midState = midState;
	}

	public String getCloseState() {
		return closeState;
	}

	public void setCloseState(String closeState) {
		this.closeState = closeState;
	}

	public ProModel getProModel() {
		return proModel;
	}

	public void setProModel(ProModel proModel) {
		this.proModel = proModel;
	}

	public ProModelMd() {
		super();
	}

	public ProModelMd(String id){
		super(id);
	}

	@Length(min=0, max=64, message="model_id长度必须介于 0 和 64 之间")
	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public Float getAppAmount() {
		if(appAmount == null ){
		 appAmount = new Float(0.0f);
		}
		return appAmount;
	}

	public void setAppAmount(Float appAmount) {
		this.appAmount = appAmount;
	}

	@Length(min=0, max=64, message="subject：长度必须介于 0 和 64 之间")
	public String getSubject() {
		return subject;
	}
	public String getSubjectName() {
	  return subjectName;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setSubjectName(String subjectName) {
	  this.subjectName = subjectName;
	}

	@Length(min=0, max=64, message="属性：国家级（含省级备选项目）、&ldquo;校级长度必须介于 0 和 64 之间")
	public String getAppLevel() {
		return appLevel;
	}

	public void setAppLevel(String appLevel) {
		this.appLevel = appLevel;
	}

	@Length(min=0, max=64, message="项目来源：            A为学生自主选题，来源于自己对课题长期积累与兴趣；            B为学生来源于教师科研项目选题；            C为学生承担社会、企业委托项目选题。长度必须介于 0 和 64 之间")
	public String getProSource() {
		return proSource;
	}

	public void setProSource(String proSource) {
		this.proSource = proSource;
	}

	@Length(min=0, max=64, message="来源项目名称：限B和C的项目长度必须介于 0 和 64 之间")
	public String getSourceProjectName() {
		return sourceProjectName;
	}

	public void setSourceProjectName(String sourceProjectName) {
		this.sourceProjectName = sourceProjectName;
	}

	@Length(min=0, max=64, message="来源项目类别：            写&ldquo;863项目&rdquo;            、&ldquo;973项目&rdquo;、            &ldquo;国家自然科学基金项目&rdquo;、            &ldquo;省级自然科学基金项目&rdquo;、            &ldquo;教师横向科研项目&rdquo;、            &ldquo;企业委托项目&rdquo;、            &ldquo;社会委托项目&rdquo;            以及其他项目标识。长度必须介于 0 和 64 之间")
	public String getSourceProjectType() {
		return sourceProjectType;
	}

	public void setSourceProjectType(String sourceProjectType) {
		this.sourceProjectType = sourceProjectType;
	}

	@Length(min=0, max=1, message="是否入孵长度必须介于 0 和 1 之间")
	public String getIisIncubation() {
		return iisIncubation;
	}

	public void setIisIncubation(String iisIncubation) {
		this.iisIncubation = iisIncubation;
	}

	public String getStageResult() {
		return stageResult;
	}

	public void setStageResult(String stageResult) {
		this.stageResult = stageResult;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAppBeginDate() {
		return appBeginDate;
	}

	public void setAppBeginDate(Date appBeginDate) {
		this.appBeginDate = appBeginDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAppEndDate() {
		return appEndDate;
	}

	public void setAppEndDate(Date appEndDate) {
		this.appEndDate = appEndDate;
	}

	public String getReimbursementAmount() {
		return reimbursementAmount;
	}

	public void setReimbursementAmount(String reimbursementAmount) {
		this.reimbursementAmount = reimbursementAmount;
	}

}