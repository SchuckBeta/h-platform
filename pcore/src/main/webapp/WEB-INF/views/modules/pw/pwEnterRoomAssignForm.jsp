<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>固定资产管理</title>
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
            <span>入驻场地分配</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div class="content_panel">
        <sys:message content="${message}"/>
        <form:form id="inputForm" modelAttribute="pwEnterRoom" action="${ctx}/pw/pwEnterRoom/assignRoom" method="post"
                   class="form-horizontal">

            <form:hidden path="pwEnter.id" value="${pwEnterRoom.pwEnter.id}"/>

            <div class="control-group">
                <label class="control-label">入驻编号：</label>
                <div class="controls">
                    <p class="control-static">${pwEnterRoom.pwEnter.no}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">类型：</label>
                <div class="controls">
                    <p class="control-static">
                        <c:if test="${not empty pwEnterRoom.pwEnter.eteam}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnterRoom.pwEnter.eteam.type, 'pw_enter_type', '')} </span> </c:if>
                        <c:if test="${not empty pwEnterRoom.pwEnter.eproject}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnterRoom.pwEnter.eproject.type, 'pw_enter_type', '')} </span> </c:if>
                        <c:if test="${not empty pwEnterRoom.pwEnter.ecompany}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnterRoom.pwEnter.ecompany.type, 'pw_enter_type', '')} </span> </c:if>
                    </p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">负责人：</label>
                <div class="controls">
                    <p class="control-static">${pwEnterRoom.pwEnter.applicant.name}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><i>*</i>房间：</label>
                <div class="controls">
                    <sys:treeselectRoomAssign id="room" name="pwRoom.id" value="${pwRoom.pwSpace.id}" labelName="pwSpace.name"
                                              labelValue="${pwRoom.pwSpace.name}"
                                              title="房间" url="/pw/pwRoom/roomTreeData" extId="${pwRoom.pwSpace.id}"
                                              cssClass="required" isAll="true"
                                              allowClear="true" notAllowSelectRoot="true" notAllowSelectParent="true"
                                              cssStyle="width: 175px;"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">已入驻信息</label>
                <div class="controls">
                    <table class="table table-bordered table-condensed table-hover table-center table-orange table-sort table-nowrap table-subscribe">
                        <thead>
                        <tr>
                            <th>序号</th>
                            <th>入驻编号</th>
                            <th>类型</th>
                            <th>负责人</th>
                        </tr>
                        </thead>
                        <tbody>
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
<%--<script>--%>
    <%--var enter = ${fns:toJson(pwEnter)};--%>
    <%--var teamName = enter.eteam ? enter.eteam.team.name : '-';--%>
    <%--var companyName = enter.ecompany ? enter.ecompany.pwCompany.name : '-';--%>
    <%--var proName = enter.eproject ? enter.eproject.project.name : '-';--%>
    <%--if (teamName) {--%>
        <%--var tr = '<tr data-index="' + 1 + '"><td>1</td><td>' + enter.no + '</td><td><span class="team-name">' + teamName + " / " + companyName + " / " + proName + '</span></td><td>' + enter.applicant.name + '</td><td style="display: none">' + enter.remarks + '</td></tr>'--%>
        <%--$("table tbody").append(tr);--%>
    <%--}--%>

<%--</script>--%>
</body>
</html>