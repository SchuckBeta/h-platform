<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${backgroundTitle}</title>
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<style>
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
	</script>
</head>
<body>
	<div class="mybreadcrumbs">
		<span>耗材</span>
	</div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/pw/pwCosMaterial/">耗材列表</a></li>
			<shiro:hasPermission name="pw:pwCosMaterial:edit"><li><a href="${ctx}/pw/pwCosMaterial/form">耗材添加</a></li></shiro:hasPermission>
		</ul>
		<form:form id="searchForm" modelAttribute="pwCosMaterial" action="${ctx}/pw/pwCosMaterial/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li><label>名称：</label>
					<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
				</li>
				<li><label>品牌：</label>
				</li>
				<li><label>规格：</label>
				</li>
			</ul>
		</form:form>
		<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed table-thead-bg">
			<thead>
				<tr>
					<th>名称</th>
					<th>品牌</th>
					<th>规格</th>
					<th>库存</th>
					<th>最后更新时间</th>
					<th>备注</th>
					<shiro:hasPermission name="pw:pwCosMaterial:edit"><th>操作</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="pwCosMaterial">
				<tr>
					<td><a href="${ctx}/pw/pwCosMaterial/form?id=${pwCosMaterial.id}">
						${pwCosMaterial.name}
					</a></td>
					<td>
						${pwCosMaterial.brand}
					</td>
					<td>
						${pwCosMaterial.specification}
					</td>
					<td>
						${pwCosMaterial.stocks}
					</td>
					<td>
						<fmt:formatDate value="${pwCosMaterial.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${pwCosMaterial.remarks}
					</td>
					<shiro:hasPermission name="pw:pwCosMaterial:edit"><td>
	    				<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/pw/pwCosMaterial/form?id=${pwCosMaterial.id}">修改</a>
						<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/pw/pwCosMaterial/delete?id=${pwCosMaterial.id}" onclick="return confirmx('确认要删除该耗材吗？', this.href)">删除</a>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		${page.footer}
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
</body>
</html>