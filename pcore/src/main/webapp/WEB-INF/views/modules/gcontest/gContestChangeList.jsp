<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>大赛变更列表</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript" src="/js/backTable/sort.js"></script>
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
    </script>
    <style type="text/css">
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
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>大赛变更列表</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="gContest" action="${ctx}/gcontest/gContest/gContestChangeList"
               method="post" class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input type="hidden" id="orderBy" name="orderBy" value="${page.orderBy}"/>
        <input type="hidden" id="orderByType" name="orderByType" value="${page.orderByType}"/>
        <div class="search-form-wrap form-inline">
            <button id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit">查询</button>
            <input class="btn btn-back-oe btn-primaryBack-oe" onclick="resall()" type="button" value="一键发布优秀项目"/>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="type" class="control-label">大赛类型</label>
                        <div class="controls">
                            <form:select path="type" class="form-control input-medium">
                                <form:option value="" label="所有大赛类型"/>
                                <form:options items="${fns:getDictList('competition_net_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item" style="width: 275px;">
                    <div class="control-group">
                        <label for="pName" class="control-label" style="width: 92px;">参赛项目名称</label>
                        <div class="controls" style="margin-left: 107px;">
                            <form:input type="text" cssClass="form-control input-medium" path="pName"/>
                        </div>
                    </div>
                </div>
                <%--<div class="condition-item">--%>
                    <%--<div class="control-group">--%>
                        <%--<label class="control-label">融资情况</label>--%>
                        <%--<div class="controls">--%>
                            <%--<select class="form-control input-medium">--%>
                                <%--<option>-请选择-</option>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="condition-item">--%>
                    <%--<div class="control-group">--%>
                        <%--<label class="control-label">组别</label>--%>
                        <%--<div class="controls">--%>
                            <%--<select class="form-control input-medium">--%>
                                <%--<option>-请选择-</option>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>
        </div>
    </form:form>
    <table id="contentTable"
           class="table table-bordered table-condensed table-sort table-hover table-center table-theme-default">
        <thead>
        <tr>
            <th><input type="checkbox" onclick="selectAll(this)"></th>
            <th data-name="t.competition_number" style="max-width: 170px;">
                <a class="btn-sort" href="javascript:void(0)"><span>大赛编号</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="t.p_name">
                <a class="btn-sort" href="javascript:void(0)"><span>项目名称</span><i class="icon-sort"></i></a>
            </th>
            <th data-name="f.name">
                <a class="btn-sort" href="javascript:void(0)"><span>学院</span><i class="icon-sort"></i></a>
            </th>
            <th style="white-space: nowrap">申报人</th>
            <th style="white-space: nowrap">组人数</th>
            <%--<th data-name="t.level" style="min-width: 54px;">--%>
                <%--<a class="btn-sort" href="javascript:void(0)"> <span>组别</span><i class="icon-sort"></i></a>--%>
            <%--</th>--%>
            <%--<th data-name="t.type" style="min-width: 80px;">--%>
                <%--<a class="btn-sort" href="javascript:void(0)"><span>大赛类型</span><i class="icon-sort"></i></a>--%>
            <%--</th>--%>
            <%--<th data-name="t.financing_stat" width="80">--%>
                <%--<a class="btn-sort" href="javascript:void(0)"><span>融资情况</span><i class="icon-sort"></i></a>--%>
            <%--</th>--%>
            <th style="white-space: nowrap">指导老师</th>
            <th style="white-space: nowrap">网评分</th>
            <th style="white-space: nowrap">路演分</th>
            <th>评级</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="gContest">
            <tr>
                <td><input type="checkbox" name="subck" value="${gContest.id}"></td>
                <td><p class="team-number">${gContest.competitionNumber}</p></td>
                <td><p class="team-name"><a href="${ctx}/gcontest/gContest/auditedView?gcontestId=${gContest.id}&state=${gContest.auditCode}">${gContest.pName}</a></p></td>
                <td>${gContest.collegeName}</td>
                <td>${gContest.declareName}</td>
                <td>${pj:getTeamNum(gContest.snames)}</td> <!--组人数-->
                <%--<td>${fns:getDictLabel(gContest.level, "gcontest_level", "")}</td>--%>
                <%--<td>${fns:getDictLabel(gContest.type, "competition_net_type", "")}</td>--%>
                <%--<td>${fns:getDictLabel(gContest.financingStat, "financing_stat", "")}</td>--%>
                <td>${gContest.tnames}</td>
                <td>${gContest.schoolExportScore}</td>
                <td>${gContest.schoolluyanScore}</td>
                <td style="white-space: nowrap">
                    <c:if test="${gContest.auditCode =='7'}">
                        ${fns:getDictLabel(gContest.schoolendResult, "competition_college_prise", "")}
                    </c:if>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${gContest.auditCode!=null && gContest.auditCode!='7' && gContest.auditCode!='8'&& gContest.auditCode!='9'}">
                            <%--<a href="${ctx}/act/task/processMap?procDefId=${gContest.taskDef}&proInstId=${gContest.taskIn}" target="_blank">${gContest.auditState}</a>--%>
                            <a href="${ctx}/actyw/actYwGnode/designView/${gContest.auditCode}?groupId=${gContest.groupId}"
                               class="countNum" target="_blank">${gContest.auditState}</a>
                        </c:when>
                        <c:otherwise>
                            ${gContest.auditState}
                        </c:otherwise>
                    </c:choose>
                </td>
                <td style="white-space: nowrap">
                    <c:if test="${college == null}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/gcontest/gContest/changeGcontest?gcontestId=${gContest.id}&state=${gContest.auditCode}"/>
                        变更
                        </a>
                    </c:if>
                    <shiro:hasPermission name="excellent:gcontestShow:edit">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/excellent/gcontestShowForm?gcontestId=${gContest.id}">
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