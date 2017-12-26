<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>自定义流程管理</title>
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
		<span>自定义流程</span>
	</div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/actyw/actYwGroup/">自定义流程列表</a></li>
			<li class="active"><a href="${ctx}/actyw/actYwGroup/form?id=${actYwGroup.id}">自定义流程${not empty actYwGroup.id?'修改':'添加'}</a></li>
		</ul><br/>
		<form:form id="inputForm" modelAttribute="actYwGroup" action="${ctx}/actyw/actYwGroup/save" method="post" class="form-horizontal">
			<form:hidden path="id"/>
			<sys:message content="${message}"/>
			<div class="control-group">
				<label class="control-label"><i class="icon-required" style="margin-right: 4px; color: red; font-style: normal">*</i>流程名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				</div>
			</div>
			<%-- <div class="control-group">
				<label class="control-label">流程惟一标识：</label>
				<div class="controls">
					<form:input path="keyss" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				</div>
			</div> --%>
			<%-- <div class="control-group">
				<label class="control-label">生效状态:</label>
				<div class="controls">
					<form:select path="status" class="input-xlarge required">
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</div>
			</div> --%>
			<div class="control-group">
				<label class="control-label"><i class="icon-required" style="margin-right: 4px; color: red; font-style: normal">*</i>流程类型：</label>
				<div class="controls">
					<form:select path="flowType" class="input-xlarge required">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('act_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</div>
			</div>
			<%-- <div class="control-group">
				<label class="control-label">项目类型：</label>
				<div class="controls">
					<form:select path="type" class="input-xlarge required">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('act_project_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</div>
			</div> --%>
			<%-- <div class="control-group">
				<label class="control-label">流程作者：</label>
				<div class="controls">
					<form:input path="author" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				</div>
			</div> --%>
			<%-- <div class="control-group">
				<label class="control-label">流程版本：</label>
				<div class="controls">
					<form:input path="version" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">排序：</label>
				<div class="controls">
					<form:input path="sort" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				</div>
			</div> --%>
			<div class="control-group">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
				</div>
			</div>
			<div class="form-actions">
				<shiro:hasPermission name="actyw:actYwGroup:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</form:form>
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
</body>
</html>