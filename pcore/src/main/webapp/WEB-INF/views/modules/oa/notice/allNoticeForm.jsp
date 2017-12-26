<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
<title>通知管理</title>
<link rel="stylesheet" type="text/css" href="/static/common/tablepage.css" />
<script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<link href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css" type="text/css" rel="stylesheet" />
<script src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var validate;
	UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	UE.Editor.prototype.getActionUrl = function(action) {
		if (action == 'uploadimage' || action == 'uploadscrawl' || action == 'uploadvideo'
				||action=='uploadfile'||action=='catchimage'||action=='listimage'||action=='listfile'){
			return '/ftp/ueditorUpload/uploadTemp?folder=oaNotice';
		}else {
			return this._bkGetActionUrl.call(this, action);
		}
	}
	$(document).ready(function() {
		$('#formKeyword').on('keydown',function(e){
			var keywordHtml = '';
			var keyword = $(this).val();
			var $next = $("#formKeyword").next();
			if(e.keyCode == 13){
				if(hasSameKeyword(keyword)){
					if($next.children().size() < 1){
						$next.append('<label class="error">请不要重复添加</label>');
					}else{
						$next.find('label').text('请不要重复添加').show();
					}
					return false;
				}
				keywordHtml += tmpKeyWord(keyword);
				$('.col-keyword-box').append(keywordHtml);
				
				$next.empty();
				return false;
			}
		});
		validate=$("#inputForm").validate({
				submitHandler : function(form) {
					       if (UE.getEditor('content').getContent()==""){
									top.$.jBox.tip('请填写内容','warning');
								}else{
									loading('正在提交，请稍等...');
									form.submit();
								}
							},
							rules: {
								"title": {
									remote: {
										async: true,
										url: "/a/oa/oaNotify/checkTitle",     //后台处理程序
										type: "post",               //数据发送方式
										dataType: "json",           //接受数据格式
										data: {                     //要传递的数据
											oldTitle : "${oaNotify.title}",
											title : function () {
												return $("#title").val();
											}
										}
									}
								}
							},
							messages: {
								"title": {
									remote: "该标题已经存在"
								}
							},
							errorPlacement : function(error, element) {
								if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
									error.appendTo(element.parent().parent());
								} else {
									error.insertAfter(element);
								}

							}
				});

		if("${oaNotify.id}"!=""&&"${oaNotify.status}"=="1"){
			$('#type').attr("disabled",true);
			$('#title').attr("disabled",true);
			$('#status').attr("disabled",true);
			$('#btnSubmit').hide();
		}


	});
function hasSameKeyword(keyword){
	var hasSameKeyword = false;
	$keywords = $('.keyword');
	$keywords.each(function(i,item){
		var val = $(item).find('span').text();
		if(val == keyword){
			hasSameKeyword = true
			return false;
		}
	})
	
	return hasSameKeyword;
}
function tmpKeyWord(keyword){
	return '<span class="keyword"><input name="keywords" value="'+keyword+'" type="hidden"/><span>'+keyword+'</span><a class="delete-keyword" href="javascript:void(0);" onclick="delKey(this);">&times;</a></span>'
}
function delKey(ob){
	$(ob).parent().remove();
}
function onTypeChange(){
	var type=$("#type").val();
	if(type==""||type=="3"){
		$(".cmodel").attr("style","display:none");
	}else{
		$(".cmodel").attr("style","display:");
	}
}
</script>


<style>
#btnSubmit{
  background: #e9432d !important;
}
#btnCancel{
 background:#DDDDDD!important;
}
input{
  height:30px!important;
}
</style>
</head>
<body>
<div class="mybreadcrumbs"><span>通告发布</span></div>
<div class="table-page">
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/oa/oaNotify/broadcastList">通告列表</a></li>
		<li class="active">
			<a href="${ctx}/oa/oaNotify/allNoticeForm?id=${oaNotify.id}">
			通告${oaNotify.status eq '1' ? '查看' : not empty oaNotify.id ? '修改':'添加'}</a>
		</li>
	</ul>

	<form:form id="inputForm" modelAttribute="oaNotify" action="${ctx}/oa/oaNotify/saveAllNotice"
		method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<input type="hidden"  id="sendType" name="sendType" value="1" />
		<div class="control-group">
			<label class="control-label">
				<span class="help-inline">
				<font color="red">*&nbsp;</font>
				</span>通知类型：
			</label>
			<div class="controls">
				<form:select path="type" class="input-xlarge required" onchange="onTypeChange()">
					<form:option value="" label="--请选择--" />
					<form:options items="${fns:getDictList('oa_notify_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="control-group">

			<label class="control-label"><span class="help-inline">
				<font color="red">*&nbsp;</font>
				</span>标题：
			</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="200" class="input-xlarge required" />
				<span id="yzName"></span>
			</div>
		</div>
		<div class="control-group cmodel" style="display: ${(oaNotify.type==null or oaNotify.type=='' or  oaNotify.type=='3') ? 'none' : ''}">

			<label class="control-label"><span class="help-inline">
				</span>来源：
			</label>
			<div class="controls">
				<form:input path="source" htmlEscape="false" maxlength="200" class="input-xlarge" />
			</div>
		</div>
		<div class="control-group form-keyword cmodel" style="display: ${(oaNotify.type==null or oaNotify.type==' ' or  oaNotify.type=='3') ? 'none' : ''}">

			<label class="control-label"><span class="help-inline">
				</span>关键字：
			</label>
			<div class="controls">
				<input type="text" class="form-control form-keyword" id="formKeyword" autocomplete="off" style="height: 30px;"
                                   placeholder="输入关键字按回车键添加">
                                   <span  class="col-md-3 col-error"></span>
			</div>
		</div>
        <div class="row row-keyword cmodel" style="display: ${(oaNotify.type==null or oaNotify.type==' ' or  oaNotify.type=='3') ? 'none' : ''}">
                    <div class="col-md-2 col-w92"></div>
                    <div class="col-md-9 col-offset-md-2 col-offset-w92 col-keyword-box">
                        <c:forEach items="${oaNotify.keywords}" var="item">
                    		<span class="keyword">
                    			<input name="keywords" value="${item}" type="hidden"/>
	                    		<span>${item}</span>
	                    		<a class="delete-keyword" href="javascript:void(0);" onclick="delKey(this);">×</a>
                    		</span>
                        </c:forEach>
                    </div>
                </div>
		<div class="control-group">
			<label class="control-label">
				<span class="help-inline">
					<font color="red">*&nbsp;</font>
				</span>内容：
			</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="6" maxlength="2000" class="required" style="width:95%;max-width:1170px;height:300px" />
				<script type="text/javascript">
					var ue = UE.getEditor('content');
					//如果是发布状态
					ue.addListener('ready', function () {
						if("${oaNotify.id}"!=""&&"${oaNotify.status}"=="1"){
							ue.setDisabled();
						}
					});
				</script>
			</div>

		</div>

		<div class="control-group">
			<label class="control-label"><span class="help-inline">
				<font color="red">*&nbsp;</font></span>状态：</label>
			<div class="controls self-label">
				<form:radiobuttons path="status" items="${fns:getDictList('oa_notify_status')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required" />
			</div>
		</div>


		<div class="form-actions" >
			<input id="btnSubmit" class="btn btn-primary"   type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>

</body>
</html>