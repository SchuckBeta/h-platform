<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>

    <meta charset="UTF-8">
    <meta name="decorator" content="cyjd-site-default"/>
    <title>${fns:getConfig('productName')}</title>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            $('#content').css('minHeight', function () {
                return $(window).height() - 100 - 308
            })
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();

        }
    </script>
</head>

<body>
<div class="container container-ct">
    <div class="cate-table-module">
        <h4 class="title">我的预约列表</h4>
        <div class="table-block">
            <table class="table table-bordered table-condensed table-coffee table-nowrap table-center">
                <thead>
                <tr>
                    <th>会议室</th>
                    <th>开始时间</th>
                    <th>结束时间</th>
                    <th>主题</th>
                    <th>状态</th>
                    <th>容纳人数（人）</th>
                    <th>房间类型</th>
                    <%--<th>备注</th>--%>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="pwAppointment">
                    <tr>
                        <td>${pwAppointment.pwRoom.name}</td>
                        <td>
                            <fmt:formatDate value="${pwAppointment.startDate}" pattern="yyyy/MM/dd hh:mm"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${pwAppointment.endDate}" pattern="yyyy/MM/dd hh:mm"/>
                        </td>
                        <td>${pwAppointment.subject}</td>
                        <td>
                                ${fns:getDictLabel(pwAppointment.status,"pw_appointment_status" , "")}
                        </td>
                        <td>${pwAppointment.pwRoom.num}</td>
                        <td>
                                ${fns:getDictLabel(pwAppointment.pwRoom.type,"pw_room_type" , "")}
                        </td>
                        <%--<td><span class="remark" style="display: inline-block;max-width: 150px;">${pwAppointment.remarks}</span></td>--%>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            ${page.footer}
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="pwAppointment" action="${ctxFront}/pw/pwAppointment/myList" method="post"
               cssClass="hide">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    </form:form>
</div>
</body>
</html>