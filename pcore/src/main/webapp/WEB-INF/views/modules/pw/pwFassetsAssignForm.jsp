<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#inputForm").validate({
                rules: {
                    'pwSpace.name': 'required'
                },
                messages: {
                    'pwSpace.name': {
                        required: '必填信息'
                    }
                },
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio")) {
                        error.appendTo(element.parent().parent());
                    } else if (element.attr('name') === 'pwSpace.name') {
                        error.appendTo(element.parent())
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
            <span>固定资产</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div class="content_panel">
        <ul class="nav nav-tabs">
            <li><a href="${ctx}/pw/pwFassets/">固定资产列表</a></li>
            <li class="active"><a href="">固定资产分配</a></li>
        </ul>
        <sys:message content="${message}"/>
        <form:form id="inputForm" modelAttribute="pwFassetsAssign" action="${ctx}/pw/pwFassets/assign" method="post"
                   class="form-horizontal">

            <form:hidden path="fassetsIds" value="${fassetsIds}"/>
            <div class="control-group">
                <label class="control-label"><i>*</i>房间：</label>
                <div class="controls">
                    <sys:treeselectFloor id="parent" name="roomId" value="${pwRoom.pwSpace.id}" labelName="pwSpace.name"
                                         labelValue="${pwRoom.pwSpace.name}"
                                         title="房间" url="/pw/pwRoom/roomTreeData" extId="${pwRoom.pwSpace.id}"
                                         cssClass="required" isAll="true"
                                         allowClear="true" notAllowSelectRoot="true" notAllowSelectParent="true"
                                         cssStyle="width: 175px;"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><i>*</i>使用人：</label>
                <div class="controls">
                    <form:input path="respName" class="required"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">手机号：</label>
                <div class="controls">
                    <form:input path="respPhone" class="phone_number"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">资产列表</label>
                <div class="controls">
                    <table class="table table-bordered table-condensed table-hover table-center table-orange table-sort table-nowrap table-subscribe">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th>编号</th>
                            <th>资产类型</th>
                            <th>资产名称</th>
                            <th>品牌型号</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${list}" var="pwFassets" varStatus="status">
                            <tr>
                                <td>
                                        ${ status.index + 1}
                                </td>
                                <td>
                                        ${pwFassets.name}
                                </td>
                                <td>
                                        ${pwFassets.pwCategory.parent.name}
                                </td>
                                <td>
                                        ${pwFassets.pwCategory.name}
                                </td>
                                <td>
                                        ${pwFassets.brand}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="form-actions">
                <shiro:hasPermission name="pw:pwFassets:edit">
                    <button type="submit" class="btn btn-primary">保 存</button>
                </shiro:hasPermission>
                <button type="button" class="btn btn-default" onclick="history.go(-1)">返 回</button>
            </div>

        </form:form>
    </div>
</div>

</body>
</html>