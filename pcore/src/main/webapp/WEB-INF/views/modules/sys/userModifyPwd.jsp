<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>修改密码</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript">
        $(function () {
            $("#oldPassword").focus();
            $("#inputForm").validate({
                rules: {},
                messages: {
                    confirmNewPassword: {
                        equalTo: "输入与上面相同的密码"
                    }
                },
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
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
        });
    </script>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>修改密码</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="inputForm" modelAttribute="user"
               action="${ctx}/sys/user/modifyPwd" method="post"
               class="form-horizontal">
        <form:hidden path="id"/>
        <sys:message content="${message}"/>
        <div class="control-group">
            <label class="control-label"><span class="help-inline"><font
                    color="red">*</font> </span>&nbsp;旧密码:</label>
            <div class="controls">
                <input id="oldPassword" name="oldPassword" type="password" value=""
                       maxlength="50" minlength="3" class="required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"><span class="help-inline"><font
                    color="red">*</font> </span>&nbsp;新密码:</label>
            <div class="controls">
                <input id="newPassword" name="newPassword" type="password" value=""
                       maxlength="20" minlength="6" class="required"/>

            </div>
        </div>
        <div class="control-group">
            <label class="control-label"><span class="help-inline"><font
                    color="red">*</font> </span>&nbsp;确认新密码:</label>
            <div class="controls">
                <input id="confirmNewPassword" name="confirmNewPassword"
                       type="password" value="" maxlength="20" minlength="6"
                       class="required" equalTo="#newPassword"/>
            </div>
        </div>

        <div class="control-group">
            <label class="contorl-label"></label>
            <div class="controls">
                <input class="btn btn-back-oe btn-primaryBack-oe" type="submit" value="保 存"/>
                <input class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </div>
    </form:form>
</div>

</body>
</html>