<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>资源管理</title>
<!-- <meta name="decorator" content="default"/> -->
<%@include file="/WEB-INF/views/include/backtable.jsp"%>
<!-- <link href="/common/common-css/pagenation.css" rel="stylesheet"/>-->
<link rel="stylesheet" type="text/css"
	href="/static/common/tablepage.css" />


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

#btnSubmit {
	background: #e9432d !important;
}
.form-search input, select {
    max-width: 210px !important;
}
</style>
<link rel="stylesheet" type="text/css"
	href="/other/jquery-ui-1.12.1/jquery-ui.css">
<script src="/other/jquery-ui-1.12.1/jquery-ui.js"
	type="text/javascript" charset="utf-8"></script>
	<!-- 配置文件 -->
    <script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
    <!-- 编辑器源码文件 -->
    <script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<script src="/js/common.js" type="text/javascript"></script>
<script src="/common/common-js/ajaxfileupload.js"></script>
<script src="/js/cmsIndexResourceForm.js" type="text/javascript"></script>

</head>
<body>
	<div class="mybreadcrumbs">
		<span>资源管理</span>
	</div>
	<div class="table-page">
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/cms/cmsIndexResource/">资源列表</a></li>
			<li class="active"><a
				href="${ctx}/cms/cmsIndexResource/form?id=${cmsIndexResource.id}">资源<shiro:hasPermission
						name="cms:cmsIndexResource:edit">${not empty cmsIndexResource.id?'修改':'添加'}</shiro:hasPermission>
					<shiro:lacksPermission name="cms:cmsIndexResource:edit">查看</shiro:lacksPermission></a></li>
		</ul>
		<br />
		<form:form id="inputForm" modelAttribute="cmsIndexResource"
			action="${ctx}/cms/cmsIndexResource/save" method="post"
			class="form-horizontal">
			<form:hidden path="id" />
			<sys:message content="${message}" />
			<c:if
				test="${not empty cmsIndexResource.cmsIndexRegion.category.name}">
				<div class="control-group">
					<label class="control-label"><span class="help-inline"><font
							color="red">*&nbsp;</font> </span>站点栏目：</label>
					<div class="controls">
						${cmsIndexResource.cmsIndexRegion.category.name}</div>
				</div>
			</c:if>
			<c:if
				test="${not empty cmsIndexResource.id}">
			<div class="control-group htmlmodel" style="display: ${cmsIndexResource.resModel=='2' ? '' : 'none'}">
				<label class="control-label">页面链接：</label>
				<div class="controls">
					<form:input path="" value="/f/staticPage-${cmsIndexResource.id}" readonly="true" htmlEscape="false" maxlength="64"
						class="input-xlarge required" />
				</div>
			</div>
			</c:if>
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>模式：</label>
				<div class="controls">
					<form:select path="resModel" class="input-xlarge required" onchange="onResModelChange()"> 
						<form:option value="" label="--请选择--" />
						<form:options items="${fns:getDictList('region_model')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>资源名称：</label>
				<div class="controls">
					<form:input path="resName" htmlEscape="false" maxlength="64"
						class="input-xlarge required" />
				</div>
			</div>
			<div class="control-group htmlmodel" style="display: ${cmsIndexResource.resModel=='2' ? '' : 'none'}">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>页面Title：</label>
				<div class="controls">
					<form:input path="title" htmlEscape="false" maxlength="64"
						class="input-xlarge required" />
				</div>
			</div>
			<div class="control-group parammodel templemodel" style="display: ${cmsIndexResource.resModel=='2' ? 'none' : ''}">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>栏目区域：</label>
				<div class="controls">
					<sys:treeselect id="cmsIndexRegion" name="cmsIndexRegion.id"
						value="${cmsIndexResource.cmsIndexRegion.id}"
						labelName="cmsIndexResource.cmsIndexRegion.regionName"
						labelValue="${cmsIndexResource.cmsIndexRegion.regionName}"
						title="栏目" url="/cms/cmsIndexRegion/treeData"
						extId="${cmsIndexRegion.id}" cssClass="required"
						allowClear="false" notAllowSelectParent="true" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>状态：</label>
				<div class="controls">
					<form:select path="resState" class="input-xlarge required">
						<form:option value="" label="--请选择--" />
						<form:options items="${fns:getDictList('resstate_flag')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			</div>
			<%-- <div class="control-group">
				<label class="control-label">排序：</label>
				<div class="controls">
					<form:input path="resSort" htmlEscape="false" maxlength="64"
						class="input-xlarge required" />
				</div>
			</div> --%>
			
			<!-- 参数模式 -->
			<div class="parammodel" style="display: ${cmsIndexResource.resModel=='0' ? '' : 'none'}">
				<div class="control-group">
					<label class="control-label">按钮1名称：</label>
					<div class="controls">
						<form:input path="botton1Name" htmlEscape="false" maxlength="64"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">资源地址1：</label>
					<div class="controls">
						<div id="image-resUrl1"
							style="width: 237px; height: 76px; background-color: #eee;"></div>
						<form:input path="resUrl1" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
						<sys:fileUpload type="cmsIndexResource" postfix="1"
							seletorItem="#image-resUrl1" seletorHids="input[name=\'resUrl1\']"
							model="image"
							fileitem="${fns:ftpImgUrl(cmsIndexResource.resUrl1)}"
							isshow="true" />
						<span class="help-inline">建议上传资源大小请按照对应显示区域文件尺寸上传</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">跳转地址1：</label>
					<div class="controls">
						<form:input path="jumpUrl1" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">按钮2名称：</label>
					<div class="controls">
						<form:input path="botton2Name" htmlEscape="false" maxlength="64"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">资源地址2：</label>
					<div class="controls">
						<div id="image-resUrl2"
							style="width: 237px; height: 76px; background-color: #eee;"></div>
						<form:input path="resUrl2" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
						<sys:fileUpload type="cmsIndexResource" postfix="2"
							seletorItem="#image-resUrl2" seletorHids="input[name=\'resUrl2\']"
							model="image"
							fileitem="${fns:ftpImgUrl(cmsIndexResource.resUrl2)}"
							isshow="true" />
						<span class="help-inline">建议上传资源大小请按照对应显示区域文件尺寸上传</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">跳转地址2：</label>
					<div class="controls">
						<form:input path="jumpUrl2" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">按钮3名称：</label>
					<div class="controls">
						<form:input path="botton3Name" htmlEscape="false" maxlength="64"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">资源地址3：</label>
					<div class="controls">
						<div id="image-resUrl3"
							style="width: 237px; height: 76px; background-color: #eee;"></div>
						<form:input path="resUrl3" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
						<sys:fileUpload type="cmsIndexResource" postfix="3"
							seletorItem="#image-resUrl3" seletorHids="input[name=\'resUrl3\']"
							model="image"
							fileitem="${fns:ftpImgUrl(cmsIndexResource.resUrl3)}"
							isshow="true" />
						<span class="help-inline">建议上传资源大小请按照对应显示区域文件尺寸上传</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">跳转地址3：</label>
					<div class="controls">
						<form:input path="jumpUrl3" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">预留字段1：</label>
					<div class="controls">
						<form:input path="reserve1" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">预留字段2：</label>
					<div class="controls">
						<form:input path="reserve2" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">预留字段3：</label>
					<div class="controls">
						<form:input path="reserve3" htmlEscape="false" maxlength="255"
							class="input-xlarge " />
					</div>
				</div>
			</div>
			<!-- 参数模式 -->
			<!-- 模板模式 -->
			<div class="control-group templemodel" style="display: ${cmsIndexResource.resModel=='1' ? '' : 'none'}">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>模板：</label>
				<div class="controls">
					<select id="tplUrl" name="tplUrl" class="input-xlarge required" onchange="ontplUrlchange()">
						<option jsonparam="" value="" selected="selected">--请选择--</option>
						<c:forEach items="${fns:getTemplateList()}" var="item" varStatus="status">
							<option jsonparam="${item.jsonparam}" value="${item.value}" <c:if test="${cmsIndexResource.tplUrl==item.value}">selected="selected"</c:if> >${item.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group templemodel" style="display: ${cmsIndexResource.resModel=='1' ? '' : 'none'}">
				<label class="control-label">参数示例：</label>
				<div class="controls">
					<form:textarea path="" id="templeParam" readonly="true" htmlEscape="false" rows="4" style="width: 1006px; height: 327px;"/>
				</div>
			</div>
			<div class="control-group templemodel" style="display: ${cmsIndexResource.resModel=='1' ? '' : 'none'}">
				<label class="control-label">文件上传：</label>
				<div class="controls">
						<input type="file" style="display: none" id="fileToUpload" name="fileName"/>
					<form:input path="" id="image-upload-input" style="width: 520px;display: ${cmsIndexResource.resModel=='1' ? '' : 'none'}" readonly="true" htmlEscape="false" maxlength="255"
						class="input-xlarge templemodel" />
					<form:input path="" id="image-upload-input-ftp" style="width: 520px;display: ${cmsIndexResource.resModel=='2' ? '' : 'none'}" readonly="true" htmlEscape="false" maxlength="255"
						class="input-xlarge htmlmodel" />
					<a href="javascript:;" class="btn uploadFiles-btn" id="upload">上传文件</a>
					<span class="help-inline">上传文件后复制文件链接到下面，文件大小不超过100M</span>
				</div>
			</div>
			<div class="control-group templemodel" style="display: ${cmsIndexResource.resModel=='1' ? '' : 'none'}">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>参数：</label>
				<div class="controls">
					<form:textarea path="tplJson" htmlEscape="false" rows="4" style="width: 1006px; height: 327px;"/>
				</div>
			</div>
		<!-- 模板模式 -->
		<!-- html模式 -->
			<div class="control-group htmlmodel" style="display: ${cmsIndexResource.resModel=='2' ? '' : 'none'}">
				<label class="control-label"><span class="help-inline"><font
						color="red">*&nbsp;</font> </span>页面内容:</label>
				<div class="controls">
					<script id="container" name="content" type="text/plain" style="width:800px;height:600px">
        				${cmsIndexResource.content}
    				</script>
				</div>
			</div>
		<!-- html模式 -->
			<div class="form-actions">
				<shiro:hasPermission name="cms:cmsIndexResource:edit">
					<input id="btnSubmit" class="btn btn-primary" type="submit"
						value="保 存" />&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回"
					onclick="history.go(-1)" />
			</div>
		</form:form>
	</div>
</body>
</html>