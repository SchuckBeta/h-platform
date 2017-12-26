<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
<title>大赛热点</title>
<link rel="stylesheet" type="text/css" href="/static/common/tablepage.css" />
<script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<link href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css" type="text/css" rel="stylesheet" />
<script src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var validate;
	UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	UE.Editor.prototype.getActionUrl = function(action) {
	    if (action == 'uploadimage' || action == 'uploadscrawl' ||action == 'uploadvideo' || action=='uploadfile') {
	        return '/ftp/ueditorUpload/uploadTemp?folder=gcontesthots';
	    }  else {
	        return this._bkGetActionUrl.call(this, action);
	    }
	}
	$(document).ready(function() {
		var ue = UE.getEditor('content');
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
									if($("#inputForm").attr("action")=="save"){
										loading('正在提交，请稍等...');
									}
									form.submit();
								}
							},
							rules: {
							},
							messages: {
							},
							errorPlacement : function(error, element) {
								if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
									error.appendTo(element.parent().parent());
								} else {
									error.insertAfter(element);
								}

							}
				});

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
function preview(){
	$("#inputForm").attr("action",$("#front_url").val()+"/gcontesthots/preView");
	$("#inputForm").attr("target","_blank");
	$("#inputForm").submit();
	$("#inputForm").attr("action","save");
	$("#inputForm").removeAttr("target");
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
<input type="hidden" id="front_url" value="${front_url}"/>
<div class="mybreadcrumbs"><span>大赛热点</span></div>
<div class="table-page">
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/gcontesthots/list">大赛热点列表</a></li>
		<li class="active">
			<a href="javascript:void(0)">大赛热点</a>
		</li>
	</ul>

	<form:form id="inputForm" modelAttribute="gcontestHots" action="save"
		method="post" class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
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
		<div class="control-group">

			<label class="control-label"><span class="help-inline">
				</span>来源：
			</label>
			<div class="controls">
				<form:input path="source" htmlEscape="false" maxlength="200" class="input-xlarge" />
			</div>
		</div>
		<div class="control-group form-keyword">

			<label class="control-label"><span class="help-inline">
				</span>关键字：
			</label>
			<div class="controls">
				<input type="text" class="form-control form-keyword" id="formKeyword" autocomplete="off" style="height: 30px;"
                                   placeholder="输入关键字按回车键添加">
                                   <span  class="col-md-3 col-error"></span>
			</div>
		</div>
        <div class="row row-keyword" style="margin:0 -15px 15px !important;">
                    <div class="col-md-2 col-w92"></div>
                    <div class="col-md-9 col-offset-md-2 col-offset-w92 col-keyword-box">
                        <c:forEach items="${gcontestHots.keywords}" var="item">
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
			</div>

		</div>
		<div class="form-inline form-inline-publish">
            <div class="form-group">
                <label class="control-label">是否发布：</label>
                <form:select path="isRelease"
                             class="form-control">
                    <form:options items="${fns:getDictList('yes_no')}" itemValue="value" itemLabel="label"
                                  htmlEscape="false"/></form:select>
            </div>
            <div class="form-group">
                <label class="control-label">是否置顶：</label>
                <form:select path="isTop"
                             class="form-control">
                    <form:options items="${fns:getDictList('yes_no')}" itemValue="value" itemLabel="label"
                                  htmlEscape="false"/></form:select>
            </div>
        </div>


		<div class="form-actions" >
			<input class="btn btn-primary"   type="button" value="预览" onclick="preview()"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary"   type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>

</body>
</html>