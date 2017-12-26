<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>机构管理</title>
    <meta name="decorator" content="default"/>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css"/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#name").focus();
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

        textarea {
            resize: none;
        }

        #btnSubmit {
            background: #e9432d !important;
        }

        label {
            font-size: 14px !important;
        }

        .table-page > .nav-tabs > li > a {
            color: #646464;
            border: 1px solid #ddd;
        }

        .table-page > .nav-tabs > li > a:hover {
            background-color: #f4e6d4;
        }

        .table-page > .nav-tabs > .active > a {
            color: #646464;
            background-color: #f4e6d4;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="table-page" style="margin-top: 5px;">
    <ul class="nav nav-tabs" style="margin-left: 0px; margin-right: 0px;">
        <li><a href="${ctx}/sys/office/list?id=${office.parent.id}&parentIds=${office.parentIds}">机构列表</a></li>
        <li class="active"><a
                href="${ctx}/sys/office/form?id=${office.id}&parent.id=${office.parent.id}">机构<shiro:hasPermission
                name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                name="sys:office:edit">查看</shiro:lacksPermission></a></li>
    </ul>
    <%--<br/>--%>
    <div style="border: 1px solid #ddd; border-top: none;min-height: 750px;">
        <p style="background-color:#f4e6d4;height: 30px;">&nbsp;</p>
        <form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/office/save" method="post"
                   class="form-horizontal" style="margin-top:30px;">
            <form:hidden path="id"/>
            <sys:message content="${message}"/>
            <div class="control-group">
                <label class="control-label">上级机构:</label>
                <div class="controls">
                    <sys:treeselect id="office" name="parent.id" value="${office.parent.id}" labelName="parent.name"
                                    labelValue="${office.parent.name}"
                                    title="机构" url="/sys/office/treeData" extId="${office.id}" cssClass=""
                                    allowClear="${office.currentUser.admin}"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><span class="help-inline"><font color="red">*</font> </span>机构名称:</label>
                <div class="controls">
                    <form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">机构编码:</label>
                <div class="controls">
                    <form:input path="code" htmlEscape="false" maxlength="50"/>
                </div>
            </div>


            <div class="form-actions" style="padding-left:255px;padding-top:0px;">
                <shiro:hasPermission name="sys:office:edit"><input id="btnSubmit" class="btn btn-danger" type="submit"
                                                                   value="保 存"/>&nbsp;</shiro:hasPermission>
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>