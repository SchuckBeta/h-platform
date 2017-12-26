<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
<title>通知管理</title>
<link rel="stylesheet" type="text/css" href="/static/common/tablepage.css" />

<script type="text/javascript">
	$(document).ready(function() {
		$("#ps").val($("#pageSize").val());
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
<style type="text/css">
	td, th {
		text-align: center !important;
	}
	th {
		background: #f4e6d4 !important;
	}

	.ul_info{
		margin-left:-32px;
	}
	.btns{
	  float:right!important;
	}
	#btnSubmit{
	  background: #e9432d !important;
	}
	table td .btn{
		padding:3px 11px ;
		background: #e5e5e5;
		color:#666;
	}
	table td .btn:hover{
		background:#e9432d ;
		color:#fff;
	}

</style>
</head>
<body>
<div class="mybreadcrumbs"><span>通告发布</span></div>
	<div class="table-page">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/oa/oaNotify/broadcastList">通告列表</a></li>
			<li>
				<a href="${ctx}/oa/oaNotify/allNoticeForm">通告添加</a>
			</li>
		</ul>
		<form:form id="searchForm" modelAttribute="oaNotify" action="${ctx}/oa/oaNotify/broadcastList" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
			<ul class="ul-form ul_info">
				<li>
					<label>标题：</label>
					<form:input path="title" htmlEscape="false" maxlength="200" class="input-medium" />
				</li>
				<li>
					<label>类型：</label>
					<form:select path="type" class="input-medium">
						<form:option value="" label="请选择" />
						<form:options items="${fns:getDictList('oa_notify_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</li>

				<li><label>状态：</label>
					<form:radiobuttons path="status" items="${fns:getDictList('oa_notify_status')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</li>

				<li class="btns">
					<input id="btnSubmit" class="btn btn-danger" type="submit" value="查询" />
				</li>
				<li class="clearfix"></li>
			</ul>
		</form:form>
		<sys:message content="${message}" />
		<table id="contentTable" class="table table-hover table-bordered table-condensed">
			<thead>
				<tr>
					<th>标题</th>
					<th>类型</th>
					<th>状态</th>
					<th>发送人</th>
					<th>发布时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="oaNotify">
					<tr>
						<td>
							${fns:abbr(oaNotify.title,50)}
						</td>

						<td>
							<c:choose>
								<c:when test="${oaNotify.sendType eq '2'}">
									站内信
								</c:when>
								<c:otherwise>
									${fns:getDictLabel(oaNotify.type, 'oa_notify_type', '')}
								</c:otherwise>
							</c:choose>
						</td>
						<td>${fns:getDictLabel(oaNotify.status, 'oa_notify_status', '')}
						</td>
						<td>
							${fns:getUserById(oaNotify.createBy.id).getName()}
						</td>
						<td><fmt:formatDate value="${oaNotify.updateDate}" pattern="yyyy-MM-dd" /></td>
						<td style="white-space:nowrap">
							<c:choose>
								<c:when test="${oaNotify.status eq '1'}">
									<c:if test="${oaNotify.sendType eq '2'}">
										<a href="${ctx}/oa/oaNotify/formBroadcast?id=${oaNotify.id}" class="btn">查看</a>
									</c:if>
									<c:if test="${oaNotify.sendType ne '2'}">
										<a href="${ctx}/oa/oaNotify/allNoticeForm?id=${oaNotify.id}" class="btn">查看</a>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${oaNotify.sendType eq '2'}">
										<a href="${ctx}/oa/oaNotify/formBroadcast?id=${oaNotify.id}" class="btn">修改</a>
									</c:if>
									<c:if test="${oaNotify.sendType ne '2'}">
										<a href="${ctx}/oa/oaNotify/allNoticeForm?id=${oaNotify.id}" class="btn">修改</a>
									</c:if>
								</c:otherwise>
							</c:choose>
							<a href="${ctx}/oa/oaNotify/delete?id=${oaNotify.id}" onclick="return confirmx('确认要删除该通告吗？', this.href)" class="btn">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		${page.footer}
	</div>
</body>
</html>