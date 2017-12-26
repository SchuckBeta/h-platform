<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>团队信息管理</title>
    <%--<%@include file="/WEB-INF/views/include/backtable.jsp" %>--%>
    <%--<meta name="decorator" content="default"/>--%>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10"/>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-store">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/common/common-css/pagenation.css"/>
    <link rel="stylesheet" type="text/css" href="/common/common-css/backtable.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/talentsList.css">
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css"/>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="${ctxStatic}/common/mustache.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/common/initiate.js"></script>
    <script type="text/javascript" src="/js/team/teamList.js"></script>
    <style>
        .header, .footerBox {
            display: none;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            var messageInfo = $("#message").html();
            if(messageInfo!=null&&messageInfo!=""){
                showModalMessage(1,messageInfo)
            }
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        //批量删除
        function batchDel() {
            var ids = "";
            $('input[type="checkbox"]:checked').each(function () {
                if (true == $(this).is(':checked')) {
                    ids += $(this).val() + ",";
                }
            });
            if (ids.substr(ids.length - 1) == ',') {
                ids = ids.substr(0, ids.length - 1);
            }
            if (ids == "") {
                showModalMessage(1,'请至少选择一条数据!')
//                alert('请至少选择一条数据!');
                return;
            }
            var url = "${ctx}/team/batchDelete?ids=" + ids;
            //alert(url);
            window.location.href = url;
        }
        //批量解散
        function batchDis() {
            var ids = "";
            $('input[type="checkbox"]:checked').each(function () {
                if (true == $(this).is(':checked')) {
                    ids += $(this).attr("id") + ",";
                }
            });
            if (ids.substr(ids.length - 1) == ',') {
                ids = ids.substr(0, ids.length - 1);
            }
            if (ids == "") {
                showModalMessage(1,'请至少选择一条数据!')
                return;
            }
            var url = "${ctx}/team/batchDis?id=" + ids;
            //alert(url);
            window.location.href = url;
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="table-page">
        <div class="edit-bar clearfix">
            <div class="edit-bar-left">
                <span>双创团队</span>
                <i class="line"></i>
            </div>
        </div>
        <form:form id="searchForm" modelAttribute="team" action="" method="post"
                   class="breadcrumb form-horizontal form-search">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="search-form-wrap" style="width: 300px;">
                    <div class="form-inline">
                        <div class="form-group">
                            <label class="sr-only" for="name">关键字</label>
                            <input type="text" id="name" name="name" class="form-control search-input"  placeholder="关键字"/>
                        </div>
                        <input id="btnSubmit" class="btn btn-search" type="submit" style="width: auto; height: auto" value="查询"/>
                        <input type="button" class="btn btn-add" onclick="batchDel()" style="width: auto; height: auto" value="批量删除"/>
                    </div>
                </div>
                <div class="condition-main" style="margin-right: 300px">
                    <div class="row">
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="localCollege" class="control-label">所属</label>
                                <div class="input-box" style="height: 34px;overflow: hidden">
                                    <sys:treeselect id="localCollege" name="localCollege" value="${org.id }"
                                                    labelName="office.name" labelValue="${org.name}"
                                                    title="" url="/sys/office/treeData"
                                                    cssClass="input-small form-control" cssStyle="width:100%"
                                                    allowClear="true" allowInput="true"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="state" class="control-label">团队状态</label>
                                <div class="input-box">
                                    <form:select id="state" path="state" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('teamstate_flag')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="inResearch" class="control-label">在研项目</label>
                                <div class="input-box">
                                    <form:select id="inResearch" path="inResearch" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>
        <c:if test="${message!=null&& message!=''}">
            <div id="messageBox" class="alert alert-success hide" style="display: block;">
                <button class="close" data-dismiss="alert">×</button>
                <div style="display: none" id="message">${message}</div>

            </div>
        </c:if>
        <table id="contentTable" class="table table-hover table-bordered table-condensed">
            <thead>
            <tr>
                <th><input type="checkbox" id="check_all" data-flag="false"></th>
                <th>团队名称</th>
                <!-- <th>导师人数</th> -->
                <th>团队负责人</th>
                <th>所属</th>

                <th>组员</br>已组建/共需</th>
                <th>校内导师</br>已组建/共需</th>
                <th>企业导师</br>已组建/共需</th>
                <th>团队状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="team">
                <tr>
                    <td class="checkone"><input type="checkbox" value="${team.id}" name="boxTd"/></td>
                    <td>
                            ${team.name}
                    </td>
                    <td>${team.sponsor}</td>
                    <td>${team.localCollege}</td>

                    <td>
				 <span style="color:red">
                         ${team.userCount}
                 </span>/${team.memberNum}</br>
                            ${team.userName}
                    </td>
                    <td>
				<span style="color:red">
                        ${team.schoolNum}
                </span>/${team.schoolTeacherNum}</br>
                            ${team.schName }
                    </td>
                    <td>
				<span style="color:red">
                        ${team.enterpriseNum}
                </span>/${team.enterpriseTeacherNum}</br>
                            ${team.entName }
                    </td>
                    <td>
                        <c:if test="${team.state==1}">建设完毕</c:if>
                        <c:if test="${team.state==0}">建设中</c:if>
                        <c:if test="${team.state==2}">解散</c:if>
                    </td>

                        <%-- <td>
                            ${fns:getDictLabel(team.state, 'teamstate_flag', '')}
                        </td>
                        <td>
                            <fmt:formatDate value="${team.validDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                        </td>
                         --%>

                    <!--  <td style="white-space:nowrap"> -->
                    <td style="white-space:nowrap">
                        <a href="${ctx}/team/findByTeamId?id=${team.id}" class="btn">查看</a>
                        <c:if test="${team.state!=2}">
                            <a href="${ctx}/team/disTeam?id=${team.id}" class="btn"
                               onclick="return confirmx('确认要解散该团队吗？', this.href)">解散</a>
                        </c:if>
                        <c:if test="${team.state==2}">
                            <a href="${ctx}/team/delete?id=${team.id}" class="btn"
                               onclick="return confirmx('确认要删除该团队吗？', this.href)">删除</a>
                        </c:if>
                    </td>

                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%-- <div class="pagination">${page}</div> --%>
        ${page.footer}
    </div>
</div>

</body>
</html>