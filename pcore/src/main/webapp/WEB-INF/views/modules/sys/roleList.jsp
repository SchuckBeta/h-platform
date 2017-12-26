]<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>角色管理</title>
	<meta name="decorator" content="default"/>
		<link rel="stylesheet" type="text/css"
	href="/static/common/tablepage.css" />

	<script src="/js/goback.js" type="text/javascript" charset="UTF-8"></script>
	
<style>
	td, th {
		text-align: center !important;
	}
	th {
		background: #f4e6d4 !important;
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
<div class="table-page">
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/role/">角色列表</a></li>
		<shiro:hasPermission name="sys:role:edit"><li><a href="${ctx}/sys/role/form">角色添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover table-bordered table-condensed">
		<tr><th>角色名称</th><th>英文名称</th><th>归属机构</th><th>数据范围</th><shiro:hasPermission name="sys:role:edit"><th>操作</th></shiro:hasPermission></tr>
		<c:forEach items="${list}" var="role">
			<tr>
				<td><a href="form?id=${role.id}">${role.name}</a></td>
				<td><a href="form?id=${role.id}">${role.enname}</a></td>
				<td>${role.office.name}</td>
				<td>${fns:getDictLabel(role.dataScope, 'sys_data_scope', '无')}</td>
				<shiro:hasPermission name="sys:role:edit"><td style="white-space:nowrap">
					<a href="${ctx}/sys/role/assign?id=${role.id}" class="btn">分配</a>
					<c:if test="${(role.sysData eq fns:getDictValue('是', 'yes_no', '1') && fns:getUser().admin)||!(role.sysData eq fns:getDictValue('是', 'yes_no', '1'))}" >
						<a href="${ctx}/sys/role/form?id=${role.id}" class="btn">修改</a>
					</c:if>
					<a href="${ctx}/sys/role/delete?id=${role.id}" onclick="return confirmx('确认要删除该角色吗？', this.href)" class="btn">删除</a>
				</td></shiro:hasPermission>	
			</tr>
		</c:forEach>
	</table>
	</div>
</body>

</html>