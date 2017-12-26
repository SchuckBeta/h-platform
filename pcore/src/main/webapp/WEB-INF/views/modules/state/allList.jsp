<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目查询</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript" src="/js/backTable/sort.js"></script><!--排序js-->
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function selectAll(ob) {
            if ($(ob).attr("checked")) {
                $("input[name='subck']:checkbox").attr("checked", true);
            } else {
                $("input[name='subck']:checkbox").attr("checked", false);
            }
        }
        function resall() {
            var temarr = [];
            $("input[name='subck']:checked").each(function (i, v) {
                temarr.push($(v).val());
            });
            if (temarr.length == 0) {
                alertx("请选择要发布的项目");
                return;
            }
            confirmx("确定发布所选项目到门户网站？", function () {
                $.ajax({
                    type: 'post',
                    url: '/a/excellent/resall',
                    dataType: "json",
                    data: {
                        fids: temarr.join(",")
                    },
                    success: function (data) {
                        if (data) {
                            alertx(data.msg);
                        }
                    }
                });
            });
        }
        function subckchange(ob) {
            if (!$(ob).attr("checked")) {
                $("#selectAllbtn").attr("checked", false);
            }
            /* if($("input[name='subck']:checked").length==0){
             $("#resallbtn").removeAttr("onclick");
             }else{
             $("#resallbtn").attr("onclick","resall()");
             } */
        }
    </script>
    <style>
        .table .team-name{
            width: 160px;
            min-width: 160px;
            max-height: 40px;
            margin: 0 auto;
            text-align: center;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            line-height: 20px;
            overflow: hidden;
        }
        .table .team-number{
            width: 100px;
            max-width: 174px;
            height: 20px;
            margin: 0 auto;
            text-align: center;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        @media (min-width: 1200px) {
            .table .team-name{
                width:auto;
            }
        }
        @media (min-width: 1100px) {
            .table .team-number{
                width: 174px;
            }
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>项目查询</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="projectDeclare" action="${ctx}/state/allList"
               class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input type="hidden" id="orderBy" name="orderBy" value="${page.orderBy}"/>
        <input type="hidden" id="orderByType" name="orderByType" value="${page.orderByType}"/><!--desc向下 asc向上-->
        <div class="search-form-wrap form-inline">
            <form:input path="keyword" cssClass="input-medium" type="text" placeholder="关键字"/>
            <button id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit">查询</button>
            <shiro:hasPermission name="excellent:projectShow:edit">
                <input class="btn btn-back-oe btn-primaryBack-oe" id="resallbtn" onclick="resall()" type="button"
                       value="一键发布优秀项目"/>
            </shiro:hasPermission>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="type" class="control-label">项目类别</label>
                        <div class="controls">
                            <form:select path="type" cssClass="form-control input-medium">
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
                        <label for="level" class="control-label">项目级别</label>
                        <div class="controls">
                            <form:select cssClass="form-control input-medium" path="level">
                                <form:option value="" label="请选择"/>
                                <c:if test="${fns:getUser().userType=='4'}"> <!--学院专家-->
                                    <form:option value="3" label="B级"/>
                                    <form:option value="4" label="C级"/>
                                </c:if>
                                <c:if test="${fns:getUser().userType=='5'}"> <!--学院专家-->
                                    <form:option value="1" label="A+级"/>
                                    <form:option value="2" label="A级"/>
                                </c:if>
                                <c:if test="${fns:getUser().userType  eq '3'|| fns:getUser().userType  eq '6' || fns:getUser().userType  eq '7' || fns:getUser().id  eq '1'}">
                                    <form:options items="${fns:getDictList('project_degree')}" itemLabel="label"
                                                  itemValue="value"
                                                  htmlEscape="false"/>
                                </c:if>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item" style="width: 280px;">
                    <div class="control-group">
                        <label class="control-label">申报时间</label>
                        <div class="controls">
                            <input name="startDateStr" class="Wdate form-control input-medium" type="text"
                                   style="display: inline-block"
                                   value="${projectDeclare.startDateStr}"
                                   onclick="WdatePicker({dateFmt:'yyyy',isShowClear:false});"/>
                            <span>年</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable"
           class="table table-bordered table-condensed table-hover  table-sort table-theme-default table-center">
        <thead>
        <tr>
            <th><input type="checkbox" id="selectAllbtn" onclick="selectAll(this)"></th>
            <th data-name="a.number">
                <a class="btn-sort" href="javascript:void(0)"><span>项目编号</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="a.name">
                <a class="btn-sort" href="javascript:void(0)"><span>项目名称</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="a.type" style="white-space: nowrap;">
                <a class="btn-sort" href="javascript:void(0)"><span>项目类别</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="leaderString" style="white-space: nowrap;">
                <a class="btn-sort" href="javascript:void(0)"><span>负责人</span><i class="icon-sort"></i></a>
            </th>
            <th style="white-space: nowrap;">组人数</th>
            <th data-name="tnames" style="white-space: nowrap;">
                <a class="btn-sort" href="javascript:void(0)"><span>指导老师</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="a.level" style="white-space: nowrap;">
                <a class="btn-sort" href="javascript:void(0)"><span>项目级别</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="finalResultString" style="white-space: nowrap">
                <a class="btn-sort" href="javascript:void(0)"><span>项目结果</span><i class="icon-sort"></i></a>
            </th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="item">
            <tr>
                <td><input type="checkbox" name="subck" onclick="subckchange(this)" value="${item.id}"></td>
                <td><p class="team-number">${item.number}</p></td>
                <td><p class="team-name"><a href="${ctx}/state/projectDetail?id=${item.id}">${item.name}</a></p></td>
                <td>${item.typeString}</td>
                <td>${item.leaderString}</td>
                <td>
                ${pj:getTeamNum(item.snames)}
               </td><%-- 组人数--%>
                <td>${item.tnames}</td>
                <td>${item.levelString}</td>
                <td>${item.finalResultString}</td>
                <td>
                    <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(item.id)}"/>
                    <c:if test="${empty item.finalResultString}">
                        <%-- <a href="${ctx}/act/task/processMap2?proInsId=${item.procInsId}" target="_blank">待${item.act.taskName}</a> --%>
                        <a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}"
                           target="_blank">
                                <%--${item.act.taskName}--%>
                                 ${pj:getAuditStatus(projectDeclare.status_code)}
                        </a>
                    </c:if>
                    <c:if test="${not empty item.finalResultString}">
                        <c:if test="${not empty item.procInsId}">
                            <%-- <a href="${ctx}/act/task/processMapByType?proInstId=${item.procInsId}&type=gc&status=${item.status}" target="_blank">${pj:getStatus(item.id)}</a> --%>
                            <a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}"
                               target="_blank">${pj:getStatus(item.id)}</a>
                        </c:if>
                        <c:if test="${ empty item.procInsId}">
                            ${pj:getStatus(item.id)}
                        </c:if>
                    </c:if>
                </td>
                <td style="white-space: nowrap">
                    <shiro:hasPermission name="project:dcproject:modify">
                        <c:if test="${not empty item.procInsId}">
                            <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                               href="${ctx}/state/projectEdit?id=${item.id}">
                                变更
                            </a>
                        </c:if>
                        <%--   <a class="btn btn-small" href="${ctx}/state/projectDelete?id=${item.id}"
                              onclick="return confirmx('会删除项目相关信息,确认要删除吗？', this.href)">删除</a>--%>
                    </shiro:hasPermission>
                    <shiro:hasPermission name="excellent:projectShow:edit">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/excellent/projectShowForm?projectId=${item.id}">
                            展示
                        </a>
                    </shiro:hasPermission>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>