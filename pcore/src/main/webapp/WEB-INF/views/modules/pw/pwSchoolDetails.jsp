<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
	<title>${backgroundTitle}</title>
	<%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
	<script type="text/javascript">
        $(document).ready(function () {
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
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
<div class="container-fluid">
	<div class="edit-bar clearfix">
		<div class="edit-bar-left">
			<span>场地管理</span>
			<i class="line weight-line"></i>
		</div>
	</div>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/pw/pwSpace/">场地列表</a></li>
		<li class="active"><a
				href="${ctx}/pw/pwSpace/form?id=${pwSpace.id}&parent.id=${pwSpaceparent.id}">学校查看</a></li>
	</ul>
	<sys:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="pwSpace" action="${ctx}/pw/pwSpace/save" method="post"
			   class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="parent.id" value="${pwSpace.parent.id}"/>
		<form:hidden path="type" value="0"/>

		<div class="control-group">
			<label class="control-label">学校名称：</label>
			<div class="controls">
				<p class="control-static">${pwSpace.name}</p>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<p class="control-static">${pwSpace.remarks}</p>
			</div>
		</div>
		<div class="form-actions">
			<input class="btn btn-default" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form></div>
<div id="dialog-message" title="信息">
	<p id="dialog-content"></p>
</div>
</body>
</html>