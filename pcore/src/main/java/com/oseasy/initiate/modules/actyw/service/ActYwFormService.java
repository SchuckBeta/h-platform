package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.dao.ActYwFormDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwFormVo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FormStyleType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FormType;

/**
 * 项目流程表单Service.
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwFormService extends CrudService<ActYwFormDao, ActYwForm> {

  private static final String POSTFIX_JSP = ".jsp";
  public static final String TEMPLATE_FORM_ROOT = "/template/form/";
  private static final String FORM_MODE_FILE = "1";

  public ActYwForm get(String id) {
		return super.get(id);
	}

	public List<ActYwForm> findList(ActYwForm actYwForm) {
		return super.findList(actYwForm);
	}

	/**
	 * 根据Style查询列表只有FormStyleType.FST_LIST类型.
	 * @param actYwForm 实体
	 * @return List
	 */
  public List<ActYwForm> findListByInStyleList(ActYwForm actYwForm) {
    if(actYwForm == null){
      return null;
    }
    return dao.findListByInStyle(new ActYwFormVo(true, actYwForm));
  }

  /**
   * 根据Style查询列表无FormStyleType.FST_LIST类型 .
   * @param actYwForm 实体
   * @return List
   */
  public List<ActYwForm> findListByInStyleNoList(ActYwForm actYwForm) {
    if(actYwForm == null){
      return null;
    }
    return dao.findListByInStyle(new ActYwFormVo(false, actYwForm));
  }

	public Page<ActYwForm> findPage(Page<ActYwForm> page, ActYwForm actYwForm) {
		return super.findPage(page, actYwForm);
	}

	@Transactional(readOnly = false)
	public void save(ActYwForm actYwForm) {
	  if(actYwForm != null){
	    FormType formType = null;
	    if(actYwForm.getFlowType() != null){
        List<FlowType> allFlowTypes = Lists.newArrayList();
        for (String curfType : actYwForm.getFlowTypes()) {
          FlowType curflowType = FlowType.getByKey(curfType);
          if(curflowType != null){
            allFlowTypes.add(curflowType);
          }
        }

        FlowType flowType = null;
        formType = FormType.getByKey(actYwForm.getType());
        if(allFlowTypes.contains(FlowType.FWT_ALL)){
          flowType = FlowType.FWT_ALL;
          actYwForm.setProType(FlowType.FWT_ALL.getType().getTypes());
        }else{
          flowType = FlowType.getByKey(actYwForm.getFlowType());
          actYwForm.setProType(flowType.getType().getTypes());
        }
        if(StringUtil.isEmpty(actYwForm.getName())){
          actYwForm.setName(flowType.getName() + "/" + formType.getName());
        }

        actYwForm.setModel(FORM_MODE_FILE);
        if((actYwForm.getPath()).contains(POSTFIX_JSP)){
          actYwForm.setPath((actYwForm.getPath()).replaceAll(POSTFIX_JSP, ""));
        }
        actYwForm.setPath(actYwForm.getPath());
        if(formType != null){
          actYwForm.setStyleType(formType.getStyle().getKey());
          actYwForm.setClientType(formType.getClient().getKey());
        }
      }
	    super.save(actYwForm);
	    if(formType != null){
        if((formType.getStyle()).equals(FormStyleType.FST_LIST)){
          actYwForm.setListId(actYwForm.getId());
          super.save(actYwForm);
        }
      }
	  }
	}

	@Transactional(readOnly = false)
	public void delete(ActYwForm actYwForm) {
		super.delete(actYwForm);
	}

}