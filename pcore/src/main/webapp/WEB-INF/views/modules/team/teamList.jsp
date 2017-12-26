<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>团队信息管理</title>
    <link rel="stylesheet" type="text/css" href="/other/jquery-ui-1.12.1/jquery-ui.css"/>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript" src="/js/team/teamList.js"></script>


    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            $('.pagination_num').removeClass('row')
            var messageInfo = $("#message").html();
            if (messageInfo != null && messageInfo != "") {
                showModalMessage(1, messageInfo)
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
                showModalMessage(1, '请至少选择一条数据!')
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
                showModalMessage(1, '请至少选择一条数据!')
                return;
            }
            var url = "${ctx}/team/batchDis?id=" + ids;
            //alert(url);
            window.location.href = url;
        }
        function unAutoCheck(){
        	$.ajax({
				type:'post',
				url:'/a/team/unAutoCheck',
				success:function(data){
						alertx(data.msg,function(){
							location.href = location.href;
						});
				}
			});
        }
		function autoCheck(){
			confirmx("当前待审核的团队将会审核通过，确定要设置为自动审核？",function() {
				$.ajax({
					type:'post',
					url:'/a/team/autoCheck',
					success:function(data){
							alertx(data.msg,function(){
								if(data.ret==1){
									location.href = location.href;
								}
							});
					}
				});
			});
        }
    </script>
    <style>


        .table>thead>tr>th{
            white-space: nowrap;
            text-align: center;
        }
        .table>tbody>tr>td{
            text-align: center;
        }
        .team-member{
            max-width: 250px;
            margin: 0 auto;
            text-align: center;
            overflow: hidden;
        }
    </style>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>双创团队</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="team" action="" method="post"
               class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="search-form-wrap form-inline">
            <input type="text" id="nameSch" name="nameSch" value="${team.nameSch }" class="search-input input-medium"  placeholder="团队名称及所属"/>
            <input id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit" style="width: auto; height: auto" value="查询"/>
            <input type="button" class="btn btn-back-oe btn-primaryBack-oe" onclick="batchDel()" style="width: auto; height: auto" value="批量删除"/>
            <shiro:hasPermission name="team:audit:edit">
            <c:if test='${teamCheckOnOff==1}'>
            <input type="button" class="btn btn-back-oe btn-primaryBack-oe" onclick="autoCheck()" style="width: auto; height: auto" value="设置为自动审核"/>
            </c:if>
            <c:if test='${teamCheckOnOff!=1}'>
            <input type="button" class="btn btn-back-oe btn-primaryBack-oe" onclick="unAutoCheck()" style="width: auto; height: auto" value="设置为手动审核"/>
            </c:if>
            </shiro:hasPermission>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="state" class="control-label">团队状态</label>
                        <div class="controls">
                            <form:select id="stateSch" path="stateSch" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('teamstate_flag')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="inResearch" class="control-label">在研项目</label>
                        <div class="controls">
                            <form:select id="inResearch" path="inResearch" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item condition-item-tree">
                    <div class="control-group">
                        <label for="localCollege" class="control-label">所属</label>
                        <div class="controls" style="height: 34px;overflow: hidden">
                            <sys:treeselect id="localCollege" name="localCollege" value="${org.id }"
                                            labelName="office.name" labelValue="${org.name}"
                                            title="" url="/sys/office/treeData"
                                            cssClass="input-medium form-control form-control-tree"
                                            allowClear="true" allowInput="true" cssStyle="width:150px;"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <c:if test="${message!=null&& message!=''}">
        <div id="messageBox" class="alert alert-success">
            <button class="close" data-dismiss="alert">×</button>
            <div id="message">${message}</div>
        </div>
    </c:if>
    <table id="contentTable" class="table table-hover table-bordered table-condensed table-theme-default">
        <thead>
        <tr>
            <th><input type="checkbox" id="check_all" data-flag="false"></th>
            <th>团队名称</th>
            <th style="display:none">团队编号</th>
            <th>团队负责人</th>
            <th>所属</th>

            <th>组员</br>已组建/共需</th>
            <th>校内导师</br>已组建/共需</th>
            <th>企业导师</br>已组建/共需</th>
            <th width="80">团队状态</th>
            <th width="110">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="team">
            <tr>
                <td class="checkone"><input type="checkbox" value="${team.id}" name="boxTd"/></td>
                <td>
                        ${team.name}
                </td>
                <td style="display:none">${team.number}</td>
                <td>${team.sponsor}</td>
                <td>${team.localCollege}</td>

                <td>
				 <span <c:if test="${team.userCount!=team.memberNum}">style="color:red"</c:if> >
                         ${team.userCount}
                 </span>/${team.memberNum}</br>
                        <p class="team-member">${team.userName}</p>
                </td>
                <td>
				<span <c:if test="${team.schoolNum!=team.schoolTeacherNum}">style="color:red"</c:if> >
                        ${team.schoolNum}
                </span>/${team.schoolTeacherNum}</br>
                        <p class="team-member">${team.schName }</p>
                </td>
                <td>
				<span <c:if test="${team.enterpriseNum!=team.enterpriseTeacherNum}">style="color:red"</c:if> >
                        ${team.enterpriseNum}
                </span>/${team.enterpriseTeacherNum}</br>
                        ${team.entName }
                </td>
                <td>
                    <c:if test="${team.state==1}">建设完毕</c:if>
                    <c:if test="${team.state==0}">建设中</c:if>
                    <c:if test="${team.state==2}">解散</c:if>
                    <c:if test="${team.state==3}">待审核</c:if>
                    <c:if test="${team.state==4}">未通过</c:if>
                </td>

                    <%-- <td>
                        ${fns:getDictLabel(team.state, 'teamstate_flag', '')}
                    </td>
                    <td>
                        <fmt:formatDate value="${team.validDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                     --%>

                <!--  <td style="white-space:nowrap"> -->
                <td>
                    <a href="${ctx}/team/findByTeamId?id=${team.id}" class="btn btn-back-oe btn-primaryBack-oe btn-small">查看</a>
                    <c:if test="${team.state!=2}">
                        <a href="${ctx}/team/disTeam?id=${team.id}" class="btn btn-small"
                           onclick="return confirmx('确认要解散该团队吗？', this.href)">解散</a>
                    </c:if>
                    <c:if test="${team.state==2}">
                        <a href="${ctx}/team/delete?id=${team.id}" class="btn btn-small"
                           onclick="return confirmx('确认要删除该团队吗？', this.href)">删除</a>
                    </c:if>
                    <shiro:hasPermission name="team:audit:edit">
                    <c:if test="${team.state==3}">
                        <a href="${ctx}/team/toTeamAudit?id=${team.id}" class="btn btn-small">审核</a>
                    </c:if>
                    </shiro:hasPermission>
                </td>

            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%-- <div class="pagination">${page}</div> --%>
    ${page.footer}
</div>

</body>
</html>