<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>${backgroundTitle}</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
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
	th {
		background: #f4e6d4 !important;
	}

	td, th {
		text-align: center !important;
	}
	 .form-search input, select{
	  height:30px!important;
	}
	#btnSubmit{
	  background:#e9432d!important;
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
	<div class="mybreadcrumbs"><span>编号规则管理</span></div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/sys/sysNumRule/">编号规则列表</a></li>
			<shiro:hasPermission name="sys:sysNumRule:edit">
				<li><a href="${ctx}/sys/sysNumRule/form?type1='1'">编号规则添加</a></li>
			</shiro:hasPermission>
		</ul>
		<form:form id="searchForm" modelAttribute="sysNumRule"
			action="${ctx}/sys/sysNumRule/" method="post"
			class="breadcrumb form-search" style="padding-top:9px!important">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
			<input id="pageSize" name="pageSize" type="hidden"
				value="${page.pageSize}" />
			<ul class="ul-form">
				<li><label>编号名称</label> <form:input path="name"
						htmlEscape="false" maxlength="32" class="input-medium" style="height:21px!important" /></li>
				<li class="pull-right" style="height:auto!important;float:right!important"><input id="btnSubmit" class="btn btn-primary"
					type="submit" value="查询"  /></li>
				<li class="clearfix"></li>
			</ul>
		</form:form>
		<sys:message content="${message}" />
		<table id="contentTable"
			class="table table-hover table-bordered table-condensed">
			<thead>
				<tr>
					<th>编号名称</th>
					<th>所属编号类别</th>
					<th>前缀</th>
					<th>后缀</th>
					<th>日期</th>
					<th>时间</th>
					<th>开始序号</th>
					<th>编号位数</th>
					<shiro:hasPermission name="sys:sysNumRule:edit">
						<th>操作</th>
					</shiro:hasPermission>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="sysNumRule">
					<tr>
						<td>${sysNumRule.name}</td>
						<td>${fns:getDictLabel(sysNumRule.type, 'sys_role_menu_type', '')}</td>
						<td>${sysNumRule.prefix}</td>
						<td>${sysNumRule.suffix}</td>
						<td>${sysNumRule.dateFormat}</td>
						<td>${sysNumRule.timieFormat}</td>
						<td>${sysNumRule.startNum}</td>
						<td>${sysNumRule.numLength}</td>
						<shiro:hasPermission name="sys:sysNumRule:edit">
							<td style="white-space:nowrap"><a href="${ctx}/sys/sysNumRule/form?id=${sysNumRule.id}" class="btn">修改</a>
								<a href="${ctx}/sys/sysNumRule/delete?id=${sysNumRule.id}"
								onclick="return confirmx('确认要删除该编号规则吗？', this.href)" class="btn">删除</a></td>
						</shiro:hasPermission>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		${page.footer}
	</div>
</body>
</html>