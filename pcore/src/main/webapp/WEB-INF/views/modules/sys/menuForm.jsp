\<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<script src="/common/common-js/ajaxfileupload.js"></script>
<title>${backgroundTitle}</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" type="text/css"
	href="/static/common/tablepage.css" />
<script type="text/javascript">
	var prePath = $('#fileToUpload').val();
	var fileUp = false;
	$(document)
			.ready(
					function() {
						$("#name").focus();
						$("#inputForm").validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
										} else {
											error.insertAfter(element);
										}
									}
								});
						$("#upload").on('click', function() {
							$('#fileToUpload').click();
						});
						//选择文件之后执行上传  
						$('#fileToUpload')
								.on(
										'change',
										function() {
											if (fileUp) {
												return false;
											}
											fileUp = true;
											var curPath = $('#fileToUpload')
													.val();
											if (prePath == curPath) {
												fileUp = false;
												alert("不能上传相同文件名");
												return false;
											}
											prePath = curPath;

											var ftpIds = $("input[name='ftpId']");
											var ftpId = "";
											if (ftpIds.length > 0) {
												ftpId = ftpIds[0].value;
											}
											$
													.ajaxFileUpload({
														url : '/ftp/urlLoad/urlRenzhengLoad',
														secureuri : false,
														fileElementId : 'fileToUpload',//file标签的id
														dataType : 'json',//返回数据的类型
														data : {
															type : 'menuImg',
															ftpId : ftpId
														},//一同上传的数据
														success : function(
																data, status) {
															//把图片替换
															var obj = jQuery
																	.parseJSON(data);
															var state = obj.state;
															if (state == "3") {
																alert("上传文件名相同");
															} else if (state == "2") {
																alert("上传失败");
															} else {
																$("#menu_img")
																		.attr(
																				"src",
																				"/a/download?fileName="
																						+ encodeURIComponent(obj.arrUrl));
																$("#arrUrl")
																		.val(
																				obj.arrUrl);
															}
														},
														error : function(data,
																status, e) {
															alert(e);
														}
													});
										});
					});
</script>
<style>
td, th {
	text-align: center !important;
}

th {
	background: #f4e6d4 !important;
}

.control-group {
	border-bottom: none !important;
}

.form-actions {
	background-color: #fff !important;
	border-top: none !important;
}

.yy {
	width: 80%;
	height: 35px;
	position: relative;
	left:0px;
}

.yy .yw-line{
	position:absolute;
	height:1px;
	width:80%;
	background:orange;
	left:181px;
	top:22px;
}
#upload {
	width: 102px;
	height: 30px;
	text-align:center;
	background-color: #e9442d;
	color: #fff;
	line-height: 30px;
	position: absolute;
	top: 15px;
	left:225px;
	border-radius:2px;
}
textarea {
  resize : none;
}
	#btnSubmit {
	background: #e9432d !important;
}
label {
	font-size: 14px !important;
}
.table-page>.nav-tabs>li>a{
	color: #646464;
	border:1px solid #ddd;
}
.table-page>.nav-tabs>li>a:hover{
	background-color: #f4e6d4;
}
.table-page>.nav-tabs>.active>a{
	color:#646464;
	background-color: #f4e6d4;
	font-weight: bold;
}
.mybreadcrumbs{
	height: 41px;
	position: relative;
}
.mybreadcrumbs span{
	padding-right:20px;
	font-size: 16px;
	line-height: 41px;
	background: #fff;
}
.mybreadcrumbs .yw-line{
	position: absolute;
	height: 3px;
	width: 100%;
	background:#f4e6d4;
	left: 0px;
	top:20px;
	z-index: -10;
}
</style>
</head>
<body>
	<div class="mybreadcrumbs" style=" margin-left: 25px;  margin-top: 10px; margin-right: 25px;">
		<span>部署流程</span>
		<p class="yw-line"></p>
	</div>
	<div class="table-page">
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/sys/menu/">菜单列表</a></li>
			<li class="active"><a
				href="${ctx}/sys/menu/form?id=${menu.id}&parent.id=${menu.parent.id}">菜单<shiro:hasPermission
						name="sys:menu:edit">${not empty menu.id?'修改':'添加'}</shiro:hasPermission>
					<shiro:lacksPermission name="sys:menu:edit">查看</shiro:lacksPermission></a></li>
		</ul>
		<div style="border:1px solid #ddd; border-top: none;">
			<p style="background-color:#f4e6d4; height: 30px; margin-bottom: 20px; "></p>
		<form:form id="inputForm" modelAttribute="menu"
			action="${ctx}/sys/menu/save" method="post" class="form-horizontal">
			<form:hidden path="id" />
			<sys:message content="${message}" />
			<div class="control-group">
				<label class="control-label">上级菜单：</label>
				<div class="controls">
					<sys:treeselect id="menu" name="parent.id"
						value="${menu.parent.id}" labelName="parent.name"
						labelValue="${menu.parent.name}" title="菜单"
						url="/sys/menu/treeData" extId="${menu.id}" cssClass="required" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">名称：</label>
				<div class="controls">
					<form:input path="name" htmlEscape="false" maxlength="50"
						class="required input-xlarge" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">链接：</label>
				<div class="controls">
					<form:input path="href" htmlEscape="false" maxlength="2000"
						class="input-xxlarge" />
					<span class="help-inline">点击菜单跳转的页面</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">目标：</label>
				<div class="controls">
					<form:input path="target" htmlEscape="false" maxlength="10"
						class="input-small" />
					<span class="help-inline">链接地址打开的目标窗口，默认：mainFrame</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">图标：</label>
				<div class="controls">
					<sys:iconselect id="icon" name="icon" value="${menu.icon}" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">排序：</label>
				<div class="controls">
					<form:input path="sort" htmlEscape="false" maxlength="50"
						class="required digits input-small" />
					<span class="help-inline">排列顺序，升序。</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">可见：</label>
				<div class="controls">
					<form:radiobuttons path="isShow"
						items="${fns:getDictList('show_hide')}" itemLabel="label"
						itemValue="value" htmlEscape="false" class="required" />
					<span class="help-inline">该菜单或操作是否显示到系统菜单中</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">权限标识：</label>
				<div class="controls">
					<form:input path="permission" htmlEscape="false" maxlength="100"
						class="input-xxlarge" />
					<span class="help-inline">控制器中定义的权限标识，如：@RequiresPermissions("权限标识")</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="3"
						maxlength="200" class="input-xxlarge" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" style="margin-top:10px;">菜单图片：</label>
				<div class="yy">
					<%--<span class="yw-line"></span>--%>
					<img id="menu_img" src="${fns:ftpImgUrl(menu.imgUrl)}" alt=""
						width="50px" height="50px" />
					<a href="javascript:void(0)" class="upload" id="upload">上传图片</a>
					<input type="file" accept="image/gif, image/jpeg, image/bmp,image/x-png"
						style="display: none" id="fileToUpload" name="fileName" />
					<input type='hidden' id='arrUrl' name='arrUrl' value='' />
					<input type='hidden' id='menu.imgUrl' name='menu.imgUrl' value='${menu.imgUrl }' />
					<input type='hidden' name='ftpId' />
				</div>
			</div>
			<div class="form-actions">
				<shiro:hasPermission name="sys:menu:edit">
					<input id="btnSubmit" class="btn btn-danger" type="submit"
						value="保 存" />&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回"
					onclick="history.go(-1)" />
			</div>
		</form:form>
		</div>
	</div>
</body>
</html>