<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>国创项目学院立项审核</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript">
        var validate;
        $(document).ready(function () {
            //左侧菜单加入数字
            $('#sub_list_wrap', window.parent.document).find('li a').each(function () {
                var hrefReg = $(this).attr('href');
                var count = "${page.todoCount}";
                if (hrefReg.indexOf("setAuditList") != -1) {
                    if (count > 0) {
                        if (count > 99) {
                            count = "99+";
                        }
                        if ($(this).find('i').size() > 0) {
                            $(this).find('i').text(count);
                        } else {
                            $(this).append('<i class="unread-tag" style="box-sizing: border-box">' + count + '</i>');
                        }
                    } else {
                        $(this).find('i').detach();
                    }
                    return false;
                }
            });

            $("#ps").val($("#pageSize").val());
            validate = $("#addForm").validate({
                messages: {
                    level: {
                        required: "请选择"
                    }
                },
                errorPlacement: function (error, element) {
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function claim(taskId) {
            $.get('${ctx}/act/task/claim', {taskId: taskId}, function (data) {
                location.reload();
            });
        }
    </script>
</head>
<body>

<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>立项审核</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="act" action="${ctx}/state/schoolSetList" method="post"
               class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="search-form-wrap form-inline">
            <button id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit">查询</button>
            <button id="btnSubmit1" class="btn btn-back-oe btn-primaryBack-oe" type="button" data-toggle="modal"
                    data-target="#myModal" style="width: auto; height: auto">批量审核
            </button>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['number']" class="control-label">项目编号</label>
                        <div class="controls">
                            <form:input type="text" path="map['number']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['name']" class="control-label">项目名称</label>
                        <div class="controls">
                            <form:input type="text" path="map['name']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['type']" class="control-label">项目类别</label>
                        <div class="controls">
                            <form:select path="map['type']" cssClass="form-control input-medium">
                                <form:option value="" label="所有项目类别"/>
                                <form:options items="${fns:getDictList('project_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['level']" class="control-label">项目级别</label>
                        <div class="controls">
                            <form:select path="map['level']" cssClass="form-control input-medium">
                                <form:option value="" label="请选择"/>
                                <form:options items="${fns:getDictList('project_degree')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label path="map['leader']" class="control-label">负责人</label>
                        <div class="controls">
                            <form:input type="text" path="map['leader']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-bordered table-condensed table-hover table-theme-default table-center">
        <thead>
        <tr>
            <th width="32"><input type="checkbox" id="check_all" data-flag="false"></th>
            <th>项目编号</th>
            <th>项目名称</th>
            <th class="white-nowrap">项目类别</th>
            <th class="white-nowrap">负责人</th>
            <th class="white-nowrap">组人数</th>
            <th class="white-nowrap">指导老师</th>
            <th class="white-nowrap">项目级别</th>
            <th class="white-nowrap">项目审核状态</th>
            <th class="white-nowrap">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="act">
            <tr>
                <td class="checkone">
                    <c:if test="${act.status=='todo'}">
                        <input type="checkbox" value="${act.vars.map.id}:${act.task.id}" name="boxTd">
                    </c:if>
                </td>
                <td>
                        ${act.vars.map.number}
                </td>
                <td>
                        ${act.vars.map.name}
                </td>
                <td class="white-nowrap">
                        ${fns:getDictLabel(act.vars.map.type, "project_type", "")}
                </td>
                <td class="white-nowrap">
                        ${act.vars.map.leader}
                </td>
                <td>
                        ${pj:getTeamNum(act.vars.map.teamList)}
                </td>
                <td class="white-nowrap">
                        ${act.vars.map.teacher}
                </td>
                <td class="white-nowrap">
                        ${fns:getDictLabel(pj:getById(act.vars.map.id).level, "project_degree", "")}
                </td>
                <td class="white-nowrap">
                    <c:if test="${act.status=='todo'||act.status=='claim'}">
                        <%--<a href="${ctx}/act/task/processMap?procDefId=${act.task.processDefinitionId}&proInstId=${act.task.processInstanceId}"  target="_blank">待${act.taskName}</a>--%>
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        <a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                             ${pj:getAuditStatus(projectDeclare.status_code)}
                        </a>
                    </c:if>
                    <c:if test="${act.status=='finish'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        ${pj:getAuditStatus(projectDeclare.status_code)}
                    </c:if>
                </td>
                <td>
                    <c:if test="${act.status=='claim'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small" href="javascript:void(0)"
                           onclick="claim('${act.task.id}');">签收</a>
                    </c:if>
                    <c:if test="${act.status=='todo'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/act/task/form?taskId=${act.task.id}&taskName=${fns:urlEncode(act.task.name)}&taskDefKey=${act.task.taskDefinitionKey}&procInsId=${act.task.processInstanceId}&procDefId=${act.task.processDefinitionId}&status=${act.status}">审核</a>
                    </c:if>
                    <c:if test="${act.status=='finish'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/state/infoView?id=${act.vars.map.id}&taskDefinitionKey=${act.histTask.taskDefinitionKey}">查看</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
<!-- Modal -->
<div id="myModal" class="modal fade hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="myModalLabel">批量审核</h3>
    </div>
    <div class="modal-body" id="selectArea">
        <form class="form-horizontal" id="addForm" name="addForm" method="post" novalidate>
            <div class="control-group">
                <label class="control-label" style="width: 175px;">需要将所选项目批量审核为</label>
                <div class="controls" style="margin-left: 190px;">
                    <select class="form-control" id="level" name="level" required>
                        <option value="">--请选择--</option>
                        <option value="1">A+级</option>
                        <option value="2">A级</option>
                        <option value="3">B级</option>
                    </select>
                </div>
            </div>
        </form>
        <div class="buffer_gif" style="text-align:center;padding:20px 0px;display:none;" id="bufferImg">
            <img src="/img/jbox-loading1.gif" alt="缓冲图片">
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn" aria-hidden="true" id="confirmBtn"
                onclick="doBatch('/a/projectBatch/schoolSetBatch');">确定
        </button>
        <button class="btn btn-default" data-dismiss="modal">取消</button>
    </div>
</div>
<script src="/js/gcProject/auditList.js"></script>

</body>
</html>