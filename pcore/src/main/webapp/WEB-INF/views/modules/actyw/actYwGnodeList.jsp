<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>流程管理</title>
	<!-- <meta name="decorator" content="default"/> -->
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<link rel="stylesheet" type="text/css" href="/other/jquery-ui-1.12.1/jquery-ui.css"/>
	<script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/common.js" type="text/javascript"></script>
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
		.ui-dialog-buttonset button{
			width: auto;
			height: auto;
		}
		.ui-dialog .ui-dialog-titlebar-close{
			background-image: none;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, ids = [], rootIds = [];
			for (var i=0; i<data.length; i++){
				ids.push(data[i].id);
			}
			ids = ',' + ids.join(',') + ',';
			for (var i=0; i<data.length; i++){
				if (ids.indexOf(','+data[i].parentId+',') == -1){
					if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
						rootIds.push(data[i].parentId);
					}
				}
			}
			for (var i=0; i<rootIds.length; i++){
				addRow("#treeTableList", tpl, data, rootIds[i], true);
			}
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];

				if ((${fns:jsGetVal('row.parentId')}) == pid){
					var isFormVal = getDictLabelByBoolean(${fns:toJson(fns:getDictList('true_false'))}, row.isForm);
					var formNameVal = (row.form != null)?row.form.name:"";
					$(list).append(Mustache.render(tpl, {
						dict: {
							isShow: ((row.isShow || (row.isShow == 'true'))?"显示":"隐藏"),
							isForm: isFormVal,
							formName: ((row.isForm || (row.isForm == 'true'))? isFormVal + " " + formNameVal : isFormVal),
							roleName: ((row.flowGroup != null)? "是 " + row.role.name : "否"),
							isLevel0: ((row.node.level == '0')? true : false),
							isLevel1: ((row.node.level == '1')? true : false),
							isLevel2: ((row.node.level == '2')? true : false),
						blank123:0}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
		function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/actyw/actYwGnode/updateSort?groupId="+$("#searchForm").find("select[name='groupId']").val());
	    	$("#listForm").submit();
    	}

		function resetFlow() {
			showModalMessage(0, '是否重置流程节点', [{
				'text': '确定',
				click: function () {
					loading('正在提交，请稍等...');
					$.ajax({
						type:'post',
						url:'${ctx}/actyw/gnode/reset/' + $("#searchForm").find("select[name='groupId']").val(),
						dataType: "json",
						data: {},
						success:function(data){
							if(data.status){
								$("#searchForm").submit();
							}
						}
					});
				}
			},{
				text: '取消',
				click: function(){
					$(this).dialog('close')
				}
			}])
    	}



	</script>
</head>
<body>
	<div class="mybreadcrumbs">
		<span>流程</span>
	</div>
	<div class="content_panel">
		<ul class="nav nav-tabs">
			<li class="active"><a href="${ctx}/actyw/actYwGnode?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程列表</a></li>
			<li><a href="${ctx}/actyw/actYwGnode/form?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程添加</a></li>
			<%--<li><a href="${ctx}/actyw/actYwGnode/design?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a></li>--%>
			<%--<li><a href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计New</a></li>--%>
			<li><a href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a></li>
		</ul>
		<form:form id="searchForm" modelAttribute="actYwGnode" action="${ctx}/actyw/actYwGnode/" method="post" class="breadcrumb form-search">
			<ul class="ul-form">
				<li class="btns">
					<input id="btnReset" class="btn btn-primary" type="button" onclick="resetFlow();" value="重置流程"/>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询流程"/></li>
				<li><label>自定义流程</label>
					<form:select path="groupId" class="input-xlarge ">
						<form:option value="" label="--请选择--"/>
						<c:forEach var="actYwGroup" items="${actYwGroups }">
							<form:option value="${actYwGroup.id}" label="${actYwGroup.name}"/>
						</c:forEach>
					</form:select>
				</li>
				<%-- <li><label>显示:</label>
					<form:select path="isShow" class="input-medium">
						<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</li> --%>
				<li><label>显示业务</label>
					<select name="isYw" class="input-medium">
						<c:if test="${isYw}"><option value="0">否</option><option value="1" selected="selected">是</option></c:if>
						<c:if test="${!isYw}"><option value="0" selected="selected">否</option><option value="1">是</option></c:if>
					</select>
				</li>
			</ul>
		</form:form>
		<sys:message content="${message}"/>
		<form id="listForm" method="post">
			<table id="treeTable" class="table table-bordered table-condensed table-thead-bg">
				<thead>
					<tr>
						<!-- <th>自定义流程</th> -->
						<th>流程节点</th>
						<!-- <td>{{row.isShow}}</td> -->
						<!-- <th>排序</th> -->
						<%-- <td style="text-align:center;">
							<shiro:hasPermission name="sys:menu:edit">
								<input type="hidden" name="ids" value="{{row.id}}"/>
								<input name="sorts" type="text" value="{{row.sort}}" style="width:50px;margin:0;padding:0;text-align:center;">
							</shiro:hasPermission><shiro:lacksPermission name="sys:menu:edit">
								{{row.sort}}
							</shiro:lacksPermission>
						</td> --%>
						<th>表单 + 角色</th>
						<th>所属机构</th>
						<th>显示</th>
						<th>最后更新时间</th>
						<th>备注</th>
						<shiro:hasPermission name="actyw:actYwGnode:edit"><th>操作</th></shiro:hasPermission>
					</tr>
				</thead>
				<tbody id="treeTableList"></tbody>
			</table>
			<%-- <shiro:hasPermission name="sys:menu:edit">
				<div class="form-actions pagination-left">
					<!-- <input class="btn btn-danger" type="button" value="保存排序" onclick="updateSort();"/> -->
				</div>
			</shiro:hasPermission> --%>
		 </form>
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td style="text-align:left;"><a href="${ctx}/actyw/actYwGnode/formProp?id={{row.id}}">
				{{row.name}}
				</a>
			</td>
			<td>
				{{dict.formName}} + {{dict.roleName}}
			</td>
			<td>
				{{row.office.name}}
			</td>
			<td>{{dict.isShow}}</td>
			<td>
				{{row.updateDate}}
			</td>
			<td>
				{{row.remarks}}
			</td>
			<shiro:hasPermission name="actyw:actYwGnode:edit"><td>
				{{#dict.isLevel1}}
				<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYwGnode/form?parent.id={{row.id}}&parentId={{row.id}}&groupId={{row.group.id}}&group.id={{row.group.id}}">添加下级流程</a>
				{{/dict.isLevel1}}
   				<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/actYwGnode/formProp?id={{row.id}}&groupId={{row.group.id}}&group.id={{row.group.id}}">修改</a>
   				<a class="check_btn btn-pray btn-lx-primary" data-node="delete" data-url="${ctx}/actyw/gnode/delete/{{row.id}}" href="javascript: void (0);" >删除</a>
			</td></shiro:hasPermission>
		</tr>
	</script>

	<%--
		<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/actyw/gnode/delete/{{row.id}}" onclick="return confirmx('确认要删除该流程及所有子流程吗？', this.href)">删除</a>
		<shiro:hasPermission name="actyw:actYwGnode:edit"><td>
 		<a href="${ctx}/actyw/actYwGnode/form?id={{row.id}}&groupId={{row.group.id}}&group.id={{row.group.id}}">修改</a>
		{{#dict.isLevel1}}
		<a href="${ctx}/actyw/actYwGnode/delete?id={{row.id}}" onclick="return confirmx('确认要删除该流程及所有子流程吗？', this.href)">删除</a>
		<a href="${ctx}/actyw/actYwGnode/form?parent.id={{row.id}}&parentId={{row.id}}&groupId={{row.group.id}}&group.id={{row.group.id}}">添加下级流程</a>
		{{/dict.isLevel1}}
		{{#dict.isLevel2}}
		<a href="${ctx}/actyw/actYwGnode/delete?id={{row.id}}" onclick="return confirmx('确认要删除该流程及所有子流程吗？', this.href)">删除</a>
		{{/dict.isLevel2}}
	</td></shiro:hasPermission> --%>
<script>
	$(function () {
		$('#treeTableList').on('click', 'a[data-node="delete"]', function(e){
			e.preventDefault();
			var url = $(this).attr('data-url');
			loading('正在删除...');
			var deleteXhr = $.get(url);
			deleteXhr.success(function (data) {
				closeLoading();
				if(data.status){
					showTip('删除成功', 'success')
					location.reload();
				}else{
					showTip('删除失败', 'fail')
				}
			});
			deleteXhr.error(function (err) {
				closeLoading();
				showTip('网络错误', 'fail')
			})
		})
	})
</script>
</body>
</html>