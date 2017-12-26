<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>${backgroundTitle}</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<script type="text/javascript">
	$(document).ready(
			function() {
				//$("#name").focus();
				var flag = true;
				$("#startNum").change(function(){
					var startNum = $("#startNum").val();
					var re=/^(0|\+?[1-9][0-9]*)$/;
					if(re.test(startNum)){
						$("#yzstartNum").text('');
						flag = true;
					}else{
						$("#yzstartNum").text("*开始序号必须为正整数！").css('color','red');
						flag = false;
					}
				})
				$("#numLength").change(function(){
					var startNum = $("#numLength").val();
					var re=/^(0|\+?[1-9][0-9]*)$/;
					if(re.test(startNum)){
						$("#yzNum").text('');
						flag = true;
					}else{
						$("#yzNum").text("*开始序号必须为正整数！").css('color','red');
						flag = false;
					}
				})
				var pass = false;
				$("#inputForm")
						.validate(
								{
									submitHandler : function(form) {
										if(flag){
											loading('正在提交，请稍等...');
											//if(pass){
												form.submit();
											//}
											$("#btnSubmit").attr("disabled",true);
										}
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
				
				$("#bhName").change(function(){
					var pattern = new RegExp("[~'!@#$%^&*()-+_=:]"); 
					var username=$("#bhName").val();
					var reg = /\s/;
					if(username==" ") {
						$("#yazheng").text("*编号名不能为空!").css('color','red');
						flag = false;
					 }
					 else if( /^\d.*$/.test( username ) ){
						 $("#yazheng").text("*编号名不能以数字开头!").css('color','red');
						flag = false;
					 }
					 else if(username.length<1 || username.length>18 ){
					  	 $("#yazheng").text("*合法长度为1-18个字符!").css('color','red');
						flag = false;
					 }else if(pattern.test(username)){
						 $("#yazheng").text("*编号名不能含有非法字符!").css('color','red');
						flag = false;
					 }else if(reg.exec(username)!=null){
						 $("#yazheng").text("*编号名不能含有非法字符!").css('color','red');
						flag = false;
					 }else{
						 $("#yazheng").text("");
						flag = true;
					 }
				});
			});
</script>
<style>
.dateFormate {
	width: 6%;
}

.da {
	width: 79px
}
.tt{
	  width:284px!important;
	  max-width:none!important;
	}
	
	.control-group{	
	  border-bottom:none;
	}
	.form-actions{
	   border-top:none!important;
	}
	#btnSubmit{
  background:#e9432d!important;
}
</style>
</head>
<body>
	<div class="mybreadcrumbs"><span>编号规则管理</span></div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/sys/sysNumRule/">编号规则列表</a></li>
			<li class="active"><a
				href="${ctx}/sys/sysNumRule/form?id=${sysNumRule.id}">编号规则<shiro:hasPermission
						name="sys:sysNumRule:edit">${not empty sysNumRule.id?'修改':'添加'}</shiro:hasPermission>
					<shiro:lacksPermission name="sys:sysNumRule:edit">查看</shiro:lacksPermission></a></li>
		</ul>
		<br />
		<form:form id="inputForm" modelAttribute="sysNumRule"
			action="${ctx}/sys/sysNumRule/save?type1=${type1 }" method="post"
			class="form-horizontal">
			<form:hidden path="id" />
			<sys:message content="${message}" />
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
					color="red">*&nbsp;</font> </span>所属编号类别：</label>
				<div class="controls">
				<form:select path="type" class="tt required">
					<form:option value="" label="请选择" />
					<form:options items="${fns:getDictList('sys_role_menu_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			</div>
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
					color="red">*&nbsp;</font> </span>编号名称：</label>
				<div class="controls">
					<form:input path="name" id="bhName" htmlEscape="false" maxlength="32"
						class="input-xlarge" />
					<span id="yazheng"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">前缀：</label>
				<div class="controls">
					<form:input path="prefix" htmlEscape="false" maxlength="32"
						class="input-xlarge"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">后缀：</label>
				<div class="controls">
					<form:input path="suffix" htmlEscape="false" maxlength="32"
						class="input-xlarge" />
				</div>
			</div>
			<c:choose>
				<c:when test="${sysNumRule.name!=null && sysNumRule.name!='' }">
					<div class="control-group">
						<label class="control-label"><span class="help-inline"><font
					color="red"></font> </span>日期：</label>
						<div class="controls">
							<tr>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.year!=null && sysNumRule.year!='' }">
											<input type="checkbox" name="year" checked="checked">yyyy
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="year">yyyy
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.month!=null && sysNumRule.month!='' }">
											<input type="checkbox" name="month" checked="checked">MM
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="month">MM
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.day!=null && sysNumRule.day!='' }">
											<input type="checkbox" name="day" checked="checked">dd
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="day">dd
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><span class="help-inline"><font
					color="red"></font> </span>时间：</label>
						<div class="controls">
							<tr>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.hour!=null && sysNumRule.hour!='' }">
											<input type="checkbox" name="hour" checked="checked">HH
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="hour">HH
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.minute!=null && sysNumRule.minute!='' }">
											<input type="checkbox" name="minute" checked="checked">mm
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="minute">mm
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${sysNumRule.second!=null && sysNumRule.second!='' }">
											<input type="checkbox" name="second" checked="checked">ss
										</c:when>
										<c:otherwise>
											<input type="checkbox" name="second">ss
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="control-group">
						<label class="control-label"><span class="help-inline"><font
					color="red"></font> </span>日期：</label>
						<div class="controls">
							<tr>
							<tr>
								<td>
									<input type="checkbox" name="year">
									yyyy</td>
								<td><input type="checkbox" name="month">
									MM</td>
								<td><input type="checkbox"  name="day">
									dd</td>
								<td><span id="dateFormate"></span></td>
							</tr>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><span class="help-inline"><font
					color="red"></font> </span>时间：</label>
						<div class="controls">
							<tr>
								<td><input type="checkbox" name="hour">
									HH</td>
								<td><input type="checkbox" name="minute">
									mm</td>
								<td><input type="checkbox" name="second">
									ss</td>
								<td><span id="timeFormate"></span></td>
							</tr>
						</div>
					</div>
				</c:otherwise>
			</c:choose>

			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
					color="red">*&nbsp;</font> </span>开始编号：</label>
				<div class="controls">
					<form:input path="startNum" id="startNum" htmlEscape="false" 
						maxlength="${sysNumRule.numLength}" class="input-xlarge required" />
					<span id="yzstartNum"></span>
				</div>
				
			</div>
			<div class="control-group">
				<label class="control-label"><span class="help-inline"><font
					color="red">*&nbsp;</font> </span>编号位数：</label>
				<div class="controls">
					<form:input path="numLength" htmlEscape="false" 
						maxlength="${sysNumRule.numLength}" class="input-xlarge required" />
					<span id="yzNum"></span>
				</div>
			</div>
			<div class="form-actions">
				<shiro:hasPermission name="sys:sysNumRule:edit">
					<input id="btnSubmit" class="btn btn-primary" type="submit"
						value="保 存" />&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回"
					onclick="history.go(-1)" />
			</div>
		</form:form>
	</div>
</body>
</html>