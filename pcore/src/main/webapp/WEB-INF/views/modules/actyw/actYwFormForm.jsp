<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>流程表单管理</title>
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<div class="mybreadcrumbs">
		<span>流程表单</span>
	</div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/actyw/actYwForm/">流程表单列表</a></li>
			<li class="active"><a href="${ctx}/actyw/actYwForm/form?id=${actYwForm.id}">流程表单<shiro:hasPermission name="actyw:actYwForm:edit">${not empty actYwForm.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="actyw:actYwForm:edit">查看</shiro:lacksPermission></a></li>
		</ul><br/>
		<form:form id="inputForm" modelAttribute="actYwForm" action="${ctx}/actyw/actYwForm/save" method="post" class="form-horizontal">
			<form:hidden path="id"/>
			<input type="hidden" name="office.id" value="${(empty actYwForm.office.id)?'1': actYwForm.office.id }" />
			<sys:message content="${message}"/>
			<div class="control-group">
				<label class="control-label">流程类型：</label>
				<div class="controls">
					<%-- <form:select id="flowType" path="flowType" class="input-xlarge required">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('act_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select> --%>
					<form:radiobuttons id="flowType" path="flowTypes" items="${fns:getDictList('act_category')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required" />
				</div>
			</div>
			<div id="formTypeDiv" class="control-group">
				<label class="control-label">表单类型：</label>
				<div class="controls">
					<form:select id="formType" path="type" class="input-xlarge required">
						<form:option value="" label="--请选择--"/>
						<c:forEach var="curFormType" items="${formTypeEnums}">
							<c:if test="${actYwForm.type eq curFormType.key}">
								<option value="${curFormType.key}" data-style="${curFormType.style.key}" selected="selected">${curFormType.name }</option>
							</c:if>
							<c:if test="${actYwForm.type ne curFormType.key}">
								<option value="${curFormType.key}" data-style="${curFormType.style.key}">${curFormType.name }</option>
							</c:if>
						</c:forEach>
					</form:select>
				</div>
			</div>
			<script type="text/javascript">
				$(function(){
					changeFlowTypeNode();
					changeFormTypeNode();
				});

				function changeFormTypeNode(){
					var formType = $("#formType");
					var listFormIdDiv = $("#listFormIdDiv");
					if($(formType).find("option:selected").attr("data-style") == '1'){
						$(listFormIdDiv).hide();
					}else{
						$(listFormIdDiv).show();
					}
					$(formType).change(function(){
						if($(formType).find("option:selected").attr("data-style") == '1'){
							$(listFormIdDiv).hide();
						}else{
							$(listFormIdDiv).show();
						}
					});
				}

				function changeFlowTypeNode(){
					var flowTypeSel = "input[name='flowTypes']";
					var flowType = $(flowTypeSel);
					var formType = $("#formType");
					var formTypeDiv = $("#formTypeDiv");

					if((flowType == null) || (flowType == undefined) || (flowType =='')){
						$(formTypeDiv).hide();
					}
					$(flowType).change(function(){
						if((flowType == null) || (flowType == undefined) || (flowType =='')){
							$(formTypeDiv).hide();
						}else{
							$.ajax({
							     type:'post',
							     url:'${ctx}/actyw/actYwForm/getFormTypes?type=' + $(flowTypeSel+":checked").val(),
							     dataType:'json',
							     success:function(data){
							          if((data != null) && (data.length > 0)){
							        	  $(formType).find("option").remove();
							        	  $(formType).append('<option value="" data-value="">--请选择--</option>');
							        	  $.each($(data), function(idx, ele){
								        	  $(formType).append('<option value="' + ele.key + '" data-value="' + ele.value + '">' + ele.name + '</option>');
							        	  });
								 		$(formTypeDiv).show();
							          }else{
							        	$(formTypeDiv).hide();
							          }
							      }
							});
						}
					});


					/*  flowType = $("#flowType");
					var formType = $("#formType");
					var formTypeDiv = $("#formTypeDiv");

					if((flowType == null) || (flowType == undefined) || (flowType =='')){
						$(formTypeDiv).hide();
					}
					$(flowType).change(function(){
						if((flowType == null) || (flowType == undefined) || (flowType =='')){
							$(formTypeDiv).hide();
						}else{
							$.ajax({
							     type:'post',
							     url:'${ctx}/actyw/actYwForm/getFormTypes?type='+$(flowType).val(),
							     dataType:'json',
							     success:function(data){
							          if((data != null) && (data.length > 0)){
							        	  $(formType).find("option").remove();
							        	  $(formType).append('<option value="" data-value="">--请选择--</option>');
							        	  $.each($(data), function(idx, ele){
								        	  $(formType).append('<option value="' + ele.key + '" data-value="' + ele.value + '">' + ele.name + '</option>');
							        	  });
								 		$(formTypeDiv).show();
							          }else{
							        	$(formTypeDiv).hide();
							          }
							      }
							});
						}
					}); */
				}
			</script>
			<div class="control-group">
				<label class="control-label">表单名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">表单模板：</label>
				<div class="controls">
					<form:select id="formPath" path="path" class="input-xlarge required">
						<form:option value="" label="--请选择--"/>
						<c:forEach var="file" items="${filelist }">
							<c:forEach var="fpType" items="${projectMarkTypeEnums }">
								<c:if test="${fpType.value eq file.filename}">
								<optgroup label="${fpType.name }">${fpType.name }</optgroup>
								</c:if>
							</c:forEach>
							<c:forEach var="fchild" items="${file.child }">
								<c:set var="formPath" value="${formRoot}${file.filename }/${fchild.filename }" />
								<c:if test="${(fn:contains(formPath, actYwForm.path))}">
									<option value="${formPath}" <c:if test="${not empty actYwForm.path}">selected="selected"</c:if>>${fchild.filename }</option>
								</c:if>
								<c:if test="${!(fn:contains(formPath, actYwForm.path))}">
									<option value="${formPath}">${fchild.filename }</option>
								</c:if>
							</c:forEach>
						</c:forEach>
					</form:select>
				</div>
			</div>
			<div id="listFormIdDiv" class="control-group">
				<label class="control-label">列表模板：</label>
				<div class="controls">
					<form:select id="listFormId" path="listId" class="input-xlarge">
						<form:option value="" label="--请选择--"/>
						<c:forEach var="flist" items="${formLists }">
							<c:if test="${flist.id eq actYwForm.listId}">
								<option value="${flist.id }" selected="selected">${flist.name }</option>
							</c:if>
							<c:if test="${flist.id ne actYwForm.listId}">
								<option value="${flist.id }">${flist.name }</option>
							</c:if>
						</c:forEach>
					</form:select>
				</div>
			</div>
			<%-- <div class="control-group">
				<label class="control-label">模式：</label>
				<div class="controls">
					<form:select path="model" class="input-xlarge ">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('act_form_tpltype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</div>
			</div> --%>
			<%--<div class="control-group">
				<label class="control-label">表单模板文件参数：</label>
				<div class="controls">
					<form:input path="params" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">表单模板文件路径：</label>
				<div class="controls">
					<form:input path="path" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>--%>
			<div class="control-group">
				<label class="control-label">表单模板内容：</label>
				<div class="controls">
					<form:textarea path="content" htmlEscape="false" maxlength="64" class="input-xlarge "/>
				</div>
			</div>
			<%-- <div class="control-group">
				<label class="control-label">节点所属机构:1、默认（系统全局）；：</label>
				<div class="controls">
					<sys:treeselect id="office" name="office.id" value="${actYwForm.office.id}" labelName="office.name" labelValue="${actYwForm.office.name}"
						title="部门" url="/sys/office/treeData?type=2" cssClass="" allowClear="true" notAllowSelectParent="true"/>
				</div>
			</div> --%>
			<div class="control-group">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge "/>
				</div>
			</div>
			<div class="form-actions">
				<shiro:hasPermission name="actyw:actYwForm:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</form:form>
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
</body>
</html>