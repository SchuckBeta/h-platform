<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<!-- <meta name="decorator" content="default"/> -->
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<!-- <link href="/common/common-css/pagenation.css" rel="stylesheet"/>-->
	<link rel="stylesheet" type="text/css"
	href="/static/common/tablepage.css" />

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
		<style>
body {
	font-size: 14px !important;
}

input {
	height: 30px !important;
}

.control-group {
	border-bottom: none !important;
}

.form-actions {
	border-top: none !important;
}

#btnSubmit{
background:#e9432d!important;
}
</style>
</head>
<body>
<div class="mybreadcrumbs"><span>区域管理</span></div>
<div class="table-page">
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/cms/cmsIndexRegion/">区域列表</a></li>
		<li class="active"><a href="${ctx}/cms/cmsIndexRegion/form?id=${cmsIndexRegion.id}">区域<shiro:hasPermission name="cms:cmsIndexRegion:edit">${not empty cmsIndexRegion.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="cms:cmsIndexRegion:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="cmsIndexRegion" action="${ctx}/cms/cmsIndexRegion/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级栏目:</label>
			<div class="controls">
				<sys:treeselect id="category" name="category.id"
					value="${cmsIndexRegion.category.id}" labelName="category.name"
					labelValue="${cmsIndexRegion.category.name}" title="栏目"
					url="/cms/category/treeData" extId="${category.id}"
					cssClass="required" allowClear="true" notAllowSelectParent="true"/>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域编号：</label>
			<div class="controls">
				<form:input path="regionId" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
			</div>
		</div> --%>
		<div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域名：</label>
			<div class="controls">
				<form:input path="regionName" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域模式：</label>
			<div class="controls">
				<form:select path="regionModel" class="input-xlarge required">
					<form:option value="" label="--请选择--"/>
					<form:options items="${fns:getDictList('region_model')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域类型：</label>
			<div class="controls">
				<form:select path="regionType" class="input-xlarge required">
					<form:option value="" label="--请选择--"/>
					<form:options items="${fns:getDictList('regiontype_flag')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div> --%>
		<div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域状态：</label>
			<div class="controls">
				<form:select path="regionState" class="input-xlarge required">
					<form:option value="" label="--请选择--"/>
					<form:options items="${fns:getDictList('regionstate_flag')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><span class="help-inline"><font color="red">*&nbsp;</font> </span>区域排序：</label>
			<div class="controls">
				<form:input path="regionSort" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="cms:cmsIndexRegion:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>
</body>
</html>