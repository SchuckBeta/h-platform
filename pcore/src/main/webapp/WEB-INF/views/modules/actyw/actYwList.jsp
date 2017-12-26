<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>项目流程管理</title>
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<link rel="stylesheet" type="text/css" href="/other/jquery-ui-1.12.1/jquery-ui.css">
	<script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/common.js" type="text/javascript" charset="utf-8"></script>
	<style>
		.ui-dialog-buttonset button{
			width: auto;
			height: auto;
		}
		.ui-dialog .ui-dialog-titlebar-close{
			background-image: none;
		}

		.table-thead-bg thead tr{
			background-color: #f4e6d4;
		}
		.table th{
			background: none;
		}
		#searchForm{
			height: auto;
			padding: 15px 0 0;
			overflow: hidden;
		}
		.ul-form input[type="text"]{
			height: 20px;
		}
		.ul-form select{
			height: 30px;
			width: 174px;
			max-width: 174px;
		}
		.form-search .ul-form li{
			margin-bottom: 15px;
		}
		.form-search .ul-form li.btns{
			float: right;
		}
		.table{
			margin-bottom: 20px;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#ps").val($("#pageSize").val());
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function sendMsg(id){
			showModalMessage(0, '确定要发布吗？', {
				"确定" : function() {
					window.location.href="${ctx}/oa/oaNotify/formBroadcast?protype=3&sId="+id;
					$(this).dialog("close");
				},
				"取消" : function() {
					$(this).dialog("close");
					return false;
				}

			});
		}
	</script>
</head>
<body>
	<div class="mybreadcrumbs">
		<span><c:if test="${not empty flowProjectTypes[0]}">${flowProjectTypes[0].name }</c:if><c:if test="${empty flowProjectTypes[0]}">项目流程</c:if></span>
	</div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/actyw/actYw/list?group.flowType=${actYw.group.flowType}">
			<c:if test="${not empty flowProjectTypes[0]}">${flowProjectTypes[0].name }</c:if><c:if test="${empty flowProjectTypes[0]}">项目流程</c:if> 列表</a></li>
			<shiro:hasPermission name="actyw:actYw:edit"><li><a href="${ctx}/actyw/actYw/form?group.flowType=${actYw.group.flowType}"><c:if test="${not empty flowProjectTypes[0]}">${flowProjectTypes[0].name }</c:if><c:if test="${empty flowProjectTypes[0]}">项目流程</c:if> 添加</a></li></shiro:hasPermission>
		</ul>
		<form:form id="searchForm" modelAttribute="actYw" action="${ctx}/actyw/actYw" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<ul class="ul-form">
				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li><label>名称</label>
					<form:input path="proProject.projectName" htmlEscape="false" maxlength="255" class="input-medium"/>
				</li>
				<li><label>自定义流程</label>
					<form:input path="group.name" htmlEscape="false" maxlength="255" class="input-medium"/>
					<c:if test="${not empty actYw.group.flowType}">
					<input name="group.flowType" type="hidden" value="${actYw.group.flowType}" /></c:if>
				</li>
				<li><label>发布状态</label>
					<form:select path="isDeploy" class="input-medium">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</li>
			</ul>
		</form:form>
		<sys:message content="${message}"/>
		<table id="contentTable" class="table table-bordered table-condensed table-thead-bg">
			<thead>
				<tr>
					<th>项目名称</th>
					<th>项目有效时间</th>
					<th>工作流程</th>
					<th>项目+流程标识</th>
					<th>发布状态</th>
					<th>消息状态</th>
					<%--<th>时间轴</th>--%>
					<th>修改时间</th>
					<shiro:hasPermission name="actyw:actYw:edit"><th>操作</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="actYw">
				<tr>
					<td>
						${actYw.proProject.projectName}
					</td>
					<td>
						<fmt:formatDate value="${actYw.proProject.startDate}" pattern="yyyy-MM-dd" />
						<c:if test="${actYw.proProject.startDate != null }"> <span>至</span></c:if>
						<fmt:formatDate value="${actYw.proProject.endDate}" pattern="yyyy-MM-dd" />
					</td>
					<%-- <td>
						&lt;%&ndash;<fmt:formatDate value="${actYw.beginDate}" pattern="yyyy-MM-dd" />
						<c:if test="${actYw.beginDate != null }"> <span>至</span></c:if>
						<fmt:formatDate value="${actYw.endDate}" pattern="yyyy-MM-dd" />&ndash;%&gt;
					</td> --%>
					<td><a href="${ctx}/actyw/actYwGroup/form?id=${actYw.groupId}">
						${actYw.group.name}
					</a></td>
					<td>
						${actYw.proProject.projectMark}_${actYw.group.keyss}
					</td>
					<td>${fns:getDictLabel(actYw.isDeploy, 'true_false', '')}</td>
					<td>
						<c:if test="${actYw.status eq '1'}">发布</c:if>
						<c:if test="${actYw.status  ne '1'}">未发布</c:if>
					</td>
					<%--<td>
						<c:if test="${actYw.isShowAxis}">应用中</c:if>
						<c:if test="${!actYw.isShowAxis}"><a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/ajaxShowAxis?id=${actYw.id}&isShowAxis=true"  onclick="return confirmx('确认要显示[${actYw.proProject.projectName}]到时间轴吗？', this.href)">切换</a></c:if>
					</td>--%>
					<td>
						<fmt:formatDate value="${actYw.proProject.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<shiro:hasPermission name="actyw:actYw:edit"><td style="text-align: left;">
    					<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/formGtime?id=${actYw.id}">修改时间</a>
						<c:if test="${(actYw.id ne ywpId) && (actYw.id ne ywgId)}">
							<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/formProp?id=${actYw.id}">修改属性</a>
						</c:if>
						<c:if test="${actYw.isDeploy}">
							<c:if test="${actYw.status  ne '1'}">
								<a class="check_btn btn-pray btn-lx-primary" href="javascript:void(0);" class="btn sendMsg btn-small" onclick="sendMsg('${actYw.id}');">消息发布</a>
							</c:if>
							<c:if test="${(actYw.id ne ywpId) && (actYw.id ne ywgId)}">
								<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/ajaxDeploy?id=${actYw.id}&isDeploy=false"  onclick="return confirmx('确认要取消发布项目[${actYw.proProject.projectName}]吗？', this.href)">取消发布</a>
							</c:if>
	    				</c:if>
	    				<c:if test="${not actYw.isDeploy}">
							<%-- <c:if test="${actYw.group.status eq '1'}"><a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/form?id=${actYw.id}">修改</a></c:if> --%>
							<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/ajaxDeploy?id=${actYw.id}&isDeploy=true&isUpdateYw=true"  onclick="return confirmx('确认要发布项目[${actYw.proProject.projectName}]吗？', this.href)">发布</a>
							<c:if test="${(actYw.id ne ywpId) && (actYw.id ne ywgId)}">
		    					<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYw/delete?id=${actYw.id}" onclick="return confirmx('确认要删除该项目流程吗？', this.href)">删除</a>
							</c:if>
						</c:if>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		${page.footer}
		<%--<ul>--%>
			<%--<li><h1>项目流程重新发布须知：</h1></li>--%>
			<%--<li>1、使用该流程的项目，如果有申报数据或业务数据在处理，流程重新发布会导致正在审核的数据失效</li>--%>
			<%--<li>2、当且仅当流程节点结构变更、流程审核角色变更时，才需要重新发布流程</li>--%>
			<%--<li>3、仅仅只做流程表单、图标名称的变更可以不用重新发布流程</li>--%>
			<%--<li>4、项目、大赛修改时间和属性，不需要重新发布流程</li>--%>
		<%--</ul>--%>
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
</body>
</html>