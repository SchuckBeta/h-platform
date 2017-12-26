<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${backgroundTitle}</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
		<link rel="stylesheet" type="text/css"
	href="/static/common/tablepage.css" />
	<script src="/js/goback.js" type="text/javascript" charset="UTF-8"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3}).show();
		});
    	function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/sys/menu/updateSort");
	    	$("#listForm").submit();
    	}
	</script>
	<style>
	td, th {
		text-align: center !important;
	}
	th {
		background: #f4e6d4 !important;
	}
	.pagination-left{
	   background-color:#fff!important;
	   border-top:none!important;
	}
	#btnSubmit {
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
	.table-page>.nav-tabs>li>a{
		color: #646464;
		border:1px solid #ddd;
	}
	.table-page>.nav-tabs>li>a:hover{
		background-color: #f4e6d4;
	}
	.table-page>.nav-tabs>.active>a{
		color:#646464;
		background-color: #f4e6d4;
		font-weight: bold;
	}
	</style>
</head>
<body>
<div class="table-page">
	<ul class="nav nav-tabs" style="margin-left: 0px;margin-right:0px;">
		<li class="active"><a href="${ctx}/sys/menu/">菜单列表</a></li>
		<shiro:hasPermission name="sys:menu:edit"><li><a href="${ctx}/sys/menu/form">菜单添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-hover table-bordered table-condensed hide">
			<thead><tr><th>名称</th><th>链接</th><th style="text-align:center;">排序</th><th>可见</th><th>权限标识</th><shiro:hasPermission name="sys:menu:edit"><th>操作</th></shiro:hasPermission></tr></thead>
			<tbody><c:forEach items="${list}" var="menu">
				<tr id="${menu.id}" pId="${menu.parent.id ne '1'?menu.parent.id:'0'}">
					<td nowrap style="text-align: left !important;"><i class="icon-${not empty menu.icon?menu.icon:' hide'}"></i><a href="${ctx}/sys/menu/form?id=${menu.id}">${menu.name}</a></td>
					<td title="${menu.href}">${fns:abbr(menu.href,30)}</td>
					<td style="text-align:center;">
						<shiro:hasPermission name="sys:menu:edit">
							<input type="hidden" name="ids" value="${menu.id}"/>
							<input name="sorts" type="text" value="${menu.sort}" style="width:50px;margin:0;padding:0;text-align:center;">
						</shiro:hasPermission><shiro:lacksPermission name="sys:menu:edit">
							${menu.sort}
						</shiro:lacksPermission>
					</td>
					<td>${menu.isShow eq '1'?'显示':'隐藏'}</td>
					<td title="${menu.permission}">${fns:abbr(menu.permission,30)}</td>
					<shiro:hasPermission name="sys:menu:edit"><td nowrap style="white-space:nowrap">
						<a href="${ctx}/sys/menu/form?id=${menu.id}" class="btn">修改</a>
						<a href="${ctx}/sys/menu/delete?id=${menu.id}" onclick="return confirmx('要删除该菜单及所有子菜单项吗？', this.href)" class="btn">删除</a>
						<a href="${ctx}/sys/menu/form?parent.id=${menu.id}" class="btn">添加下级菜单</a>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach></tbody>
		</table>
		<shiro:hasPermission name="sys:menu:edit"><div class="form-actions pagination-left">
			<input id="btnSubmit" class="btn btn-danger" type="button" value="保存排序" onclick="updateSort();"/>
		</div></shiro:hasPermission>
	 </form>
	 </div>
</body>

</html>