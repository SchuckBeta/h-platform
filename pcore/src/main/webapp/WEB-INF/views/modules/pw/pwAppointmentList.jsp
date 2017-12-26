<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <script type="text/javascript" src="/js/backTable/sort.js"></script><!--排序js-->
    <title>${backgroundTitle}</title>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();

        }
    </script>
</head>

<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>预约审核列表</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="pwAppointment" action="${ctx}/pw/pwAppointment/list"
               method="post"
               cssClass="form-horizontal clearfix form-search-block">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input type="hidden" id="orderBy" name="orderBy" value="${page.orderBy}"/>
        <input type="hidden" id="orderByType" name="orderByType" value="${page.orderByType}"/>
        <div class="col-control-group">

            <div class="control-group">
                <label class="control-label">申请人</label>
                <div class="controls">
                    <form:input class="input-medium" path="user.name" htmlEscape="false" maxlength="100"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">房间 </label>
                <div class="controls">
                    <sys:treeselectFloor id="parent" name="pwRoom.id" value="${pwAppointment.pwRoom.id}"
                                         labelName="pwRoom.name"
                                         labelValue="${pwAppointment.pwRoom.name}"
                                         title="房间" url="/pw/pwRoom/roomTreeData?isUsable=1" extId="${pwRoom.pwSpace.id}"
                                         cssClass="" cssStyle="width:119px;" isAll="true"
                                         allowClear="true" notAllowSelectRoot="true" notAllowSelectParent="true"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">状态 </label>
                <div class="controls controls-checkbox">
                    <form:checkboxes path="multiStatus" cssClass="required"
                                     items="${fns:getDictList('pw_appointment_status')}"
                                     itemLabel="label" itemValue="value" htmlEscape="false"></form:checkboxes>
                </div>
            </div>
        </div>
        <div class="search-btn-box">
            <button id="searchBtn" type="submit" class="btn btn-primary">查询</button>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable"
           class="table table-bordered table-condensed table-hover table-center table-orange table-nowrap">
        <thead>
        <tr>
            <th width="120" data-name="r.name"><a class="btn-sort" href="javascript:void(0);">会议室<i class="icon-sort"></i></a></th>
            <th width="100" data-name="u.name"><a class="btn-sort" href="javascript:void(0);">申请人<i class="icon-sort"></i></a></th>

            <th width="160" data-name="a.start_date"><a class="btn-sort" href="javascript:void(0);">开始时间<i
                    class="icon-sort"></i></a></th>
            <th width="160" data-name="a.end_date"><a class="btn-sort" href="javascript:void(0);">结束时间<i class="icon-sort"></i></a>
            </th>
            <th>主题</th>
            <%--<th>备注</th>--%>
            <th width="64" data-name="a.status"><a class="btn-sort" href="javascript:void(0);">状态<i class="icon-sort"></i></a></th>
            <th width="220">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="pwAppointment">
            <tr>
                <td>${pwAppointment.pwRoom.name}</td>
                <td>${pwAppointment.user.name}</td>

                <td>
                    <fmt:formatDate value="${pwAppointment.startDate}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>
                    <fmt:formatDate value="${pwAppointment.endDate}" pattern="yyyy-MM-dd HH:mm"/>
                </td>
                <td>${pwAppointment.subject}</td>
                <%--<td><span class="remark"--%>
                          <%--style="display: inline-block;max-width: 150px;">${pwAppointment.remarks}</span></td>--%>
                <td>
                        ${fns:getDictLabel(pwAppointment.status,"pw_appointment_status" , "")}
                </td>
                <td>
                    <c:if test="${pwAppointment.status == '0'}">
                        <a class="btn btn-small btn-primary"
                           href="${ctx}/pw/pwAppointment/manualAudit/${pwAppointment.id}/1">审核通过</a>
                        <a class="btn btn-small btn-primary"
                           href="${ctx}/pw/pwAppointment/manualAudit/${pwAppointment.id}/2">审核不通过</a>
                    </c:if>

                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>