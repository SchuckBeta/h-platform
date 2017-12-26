<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
<title>大赛热点</title>
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
<div class="mybreadcrumbs"><span>大赛热点</span></div>
	<div class="table-page">
		<ul class="nav nav-tabs">
			<li class="active"><a href="javascript:void(0);">大赛热点列表</a></li>
			<li>
				<a href="${ctx}/gcontesthots/form">大赛热点添加</a>
			</li>
		</ul>
		<form:form id="searchForm" modelAttribute="gcontestHots" action="${ctx}/gcontesthots/list" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
			<ul class="ul-form ul_info">
				<li>
					<label>标题：</label>
					<form:input path="title" htmlEscape="false" maxlength="200" class="input-medium" />
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
					<th>来源</th>
					<th>发布</th>
					<th>置顶</th>
					<th>发布时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="hot">
					<tr>
						<td>
							${fns:abbr(hot.title,50)}
						</td>
						<td>
							${fns:abbr(hot.source,50)}
						</td>
						<td>${fns:getDictLabel(hot.isRelease, 'yes_no', '')}
						</td>
						<td>
							${fns:getDictLabel(hot.isTop, 'yes_no', '')}
						</td>
						<td><fmt:formatDate value="${hot.releaseDate}" pattern="yyyy-MM-dd" /></td>
						<td style="white-space:nowrap">
							<a href="${ctx}/gcontesthots/form?id=${hot.id}"  class="btn">修改</a>
							<a href="${ctx}/gcontesthots/delete?id=${hot.id}" onclick="return confirmx('确认要删除吗？', this.href)" class="btn">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		${page.footer}
	</div>
</body>
</html>