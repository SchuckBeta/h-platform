<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>流程管理</title>
	<!-- <meta name="decorator" content="default"/> -->
	<%@include file="/WEB-INF/views/include/backtable.jsp" %>
	<link rel="stylesheet" type="text/css" href="/static/webuploader/webuploader.css">
	<link rel="stylesheet" type="text/css" href="/css/course/webuploadLesson.css">
	<script type="text/javascript" src="/static/webuploader/webuploader.min.js"></script>
	<script type="text/javascript" src="/js/actyw/uploadCover.js"></script>
	<style>
		.control-group select{
			height: 30px;
			max-width: 200px;
			width: 200px;
		}
		.control-group input{
			height: 20px;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					var iconUrl = $("#iconUrl").val();
					if(iconUrl == ""){
						alertx("请上传图标");
					}else{
						loading('正在提交，请稍等...');
						form.submit();
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<div class="mybreadcrumbs">
		<span>流程</span>
	</div>
	<div class="content_panel">

		<ul class="nav nav-tabs">
			<li><a href="${ctx}/actyw/actYwGnode?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程列表</a></li>
			<li class="active"><a href="${ctx}/actyw/actYwGnode/form?id=${actYwGnode.id}&parent.id=${actYwGnode.parent.id}&group.id=${actYwGnode.groupId}&groupId=${actYwGnode.groupId}">流程${not empty actYwGnode.id?'修改':'添加'}</a></li>
			<%--<li><a href="${ctx}/actyw/actYwGnode/design?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a></li>--%>
			<%--<li><a href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计New</a></li>--%>
			<li><a href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a></li>
		</ul><br/>
		<form:form id="inputForm" modelAttribute="actYwGnode" action="${ctx}/actyw/actYwGnode/save" method="post" class="form-horizontal">
			<form:hidden path="id"/>
			<sys:message content="${message}"/>
			<div class="control-group">
				<label class="control-label">自定义流程：</label>
				<div class="controls">
					<form:hidden path="group.id"/>
					${actYwGnode.group.name}
				</div>
				<%-- <div class="controls">
					<form:select path="group.id" class="input-xlarge ">
						<form:option value="" label="--请选择--"/>
						<c:forEach var="actYwGroup" items="${actYwGroups }">
							<form:option value="${actYwGroup.id}" label="${actYwGroup.name}"/>
						</c:forEach>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div> --%>
			</div>
			<div class="control-group">
				<label class="control-label">流程类型：</label>
				<div class="controls">
					${fns:getDictLabel(actYwGnode.group.flowType, 'act_category', '')}
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">父级节点:</label>
				<div class="controls">
					<c:if test="${(empty actYwGnode.id) && (empty actYwGnode.parent.id)}">
						${rootNode.name}
						<input type="hidden" name="parent.id" value="${rootNode.id}"/>
						<%-- <sys:treeselect id="parent" name="parent.id" value="${rootNode.id}" labelName="parent.name" labelValue="${rootNode.name}" title="父级" url="/actyw/actYwGnode/treeData" extId="${actYwGnode.id}" cssClass="required" allowClear="true"/> --%>
					</c:if>
					<c:if test="${((empty actYwGnode.id) && (not empty actYwGnode.parent.id))}">
						${actYwGnode.parent.name}
						<input type="hidden" name="parent.id" value="${actYwGnode.parent.id}"/>
						<%-- <sys:treeselect id="parent" name="parent.id" value="${actYwGnode.parent.id}" labelName="parent.name" labelValue="${actYwGnode.parent.name}" title="父级" url="/actyw/actYwGnode/treeData" extId="${actYwGnode.id}" cssClass="required" allowClear="true"/> --%>
					</c:if>
					<c:if test="${(not empty actYwGnode.id)}">
						${actYwGnode.parent.name}
						<input type="hidden" name="parent.id" value="${actYwGnode.parent.id}"/>
						<%-- <sys:treeselect id="parent" name="parent.id" value="${actYwGnode.parent.id}" labelName="parent.name" labelValue="${actYwGnode.parent.name}" title="父级" url="/actyw/actYwGnode/treeData" extId="${actYwGnode.id}" cssClass="required" allowClear="true"/> --%>
					</c:if>
					<!-- <span class="help-inline"><font color="red">*</font> </span> -->
				</div>
			</div>
			<fieldset>
				<!-- <legend><h5>业务节点</h5></legend> -->
				<div id="preFunGnodeDiv" class="control-group">
					<label class="control-label">流程前置业务节点：</label>
					<div class="controls">
						<form:select id="preFunGnode" path="preFunId" class="input-xlarge required">
							<form:option value="" label="--请选择--"/>
							<c:forEach var="actYwPpNnGnode" items="${actYwPpreGnodes }">
								<c:if test="${(actYwPpNnGnode.node.nodeKey ne 'EndNoneEvent') && (actYwPpNnGnode.node.nodeKey ne 'EndErrorEvent') && (actYwPpNnGnode.node.nodeKey ne 'EndCancelEvent') && (actYwPpNnGnode.node.nodeKey ne 'EndTerminateEvent')}">
									<c:if test="${actYwPpNnGnode.id eq actYwGnode.preFunId}">
										<option value="${actYwPpNnGnode.id}" data-id="${actYwPpNnGnode.node.id}" data-name="${actYwPpNnGnode.name}" data-nextId="${actYwPpNnGnode.nextId}" selected="selected">${actYwPpNnGnode.parent.name}->${actYwPpNnGnode.name}</option>
									</c:if>
									<c:if test="${actYwPpNnGnode.id ne actYwGnode.preFunId}">
										<option value="${actYwPpNnGnode.id}" data-id="${actYwPpNnGnode.node.id}" data-name="${actYwPpNnGnode.name}" data-nextId="${actYwPpNnGnode.nextId}">${actYwPpNnGnode.parent.name}->${actYwPpNnGnode.name}</option>
									</c:if>
								</c:if>
							</c:forEach>
						</form:select>
						<form:hidden id="preFunIdHidden" path="preFunGnode.id"/>
						<form:hidden id="preFunNodeIdHidden" path="preFunGnode.node.id"/>
						<form:hidden id="preFunNodeNameHidden" path="preFunGnode.name" />
						<form:hidden id="preFunNodeNextIdHidden" path="preFunGnode.nextId" />
					</div>
					<script type="text/javascript">
						$(function(){
							changePreFunGnode();
						});

						function changePreFunGnode(){
							var preFunGnode = $("#preFunGnode");
							var preFunIdHidden = $("#preFunIdHidden");
							var preFunNodeIdHidden = $("#preFunNodeIdHidden");
							var preFunNodeNameHidden = $("#preFunNodeNameHidden");
							var preFunNodeNextIdHidden = $("#preFunNodeNextIdHidden");

							$(preFunGnode).change(function(){
								$(preFunIdHidden).val($(this).val());
								$(preFunNodeIdHidden).val($(this).find("option:selected").attr("data-id"));
								$(preFunNodeNameHidden).val($(this).find("option:selected").attr("data-name"));
								$(preFunNodeNextIdHidden).val($(this).find("option:selected").attr("data-nextId"));
							});
						}
					</script>
				</div>

				<c:if test="${(not empty actYwGnode.preGnode) && (not empty actYwGnode.nextGnode)}">
					<c:if test="${not empty actYwGnode.preGnode}">
						<div id="nodePreGnodeDiv" class="control-group">
							<label class="control-label">流程前置节点：</label>
							<div class="controls">
								<span id="preGnodeName">${actYwGnode.preGnode.name }</span>
								<form:hidden path="preId"/>
								<form:hidden path="preGnode.id"/>
								<form:hidden id="preGnodeNameHidden" path="preGnode.name" />
							</div>
						</div>
					</c:if>
					<%-- <c:if test="${not empty actYwGnode.nextGnode}">
						<div id="nodeNextGnodeDiv" class="control-group">
							<label class="control-label">流程后置节点：</label>
							<div class="controls">
								<span id="nextGnodeName">${actYwGnode.nextGnode.name }-${actYwGnode.nextGnode.name }</span>
								<form:hidden path="nextId"/>
								<form:hidden path="nextGnode.id"/>
								<form:hidden id="nextGnodeNameHidden" path="nextGnode.name" />
							</div>
						</div>
					</c:if> --%>
				</c:if>

				<div class="control-group">
					<label class="control-label">流程业务节点：</label>
					<div id="gnodeMTWrite" class="controls">
						<form:input path="name" htmlEscape="false" maxlength="64" class="input-xlarge "/>
						<c:if test="${(empty actYwGnode.parent.id) || (actYwGnode.parent.id eq rootNode.id)}">
							<input type="hidden" id="nodeIdHidden" name="node.id" value="991000"/>
						</c:if>
						<c:if test="${(not empty actYwGnode.parent.id) && (actYwGnode.parent.id ne rootNode.id)}">
							<input type="hidden" id="nodeIdHidden" name="node.id" value="992000"/>
						</c:if>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<%-- <div id="gnodeMTSelect" class="controls" style="display: none;">
						<form:select id="nodeGnode" path="nodeId" class="input-xlarge required">
							<option value="">-请选择-</option>
							<c:forEach var="actYwNode" items="${actYwNodes }">
								<c:if test="${actYwNode.id eq actYwGnode.nodeId}">
									<option value="${actYwNode.id}" data-formId="${actYwNode.form.id}" data-roleId="${actYwNode.role.id}" data-name="${actYwNode.name}" data-type="${actYwNode.type}" data-label="${fns:getDictLabel(actYwNode.type, 'act_node_type', '')}" selected="selected">${fns:getDictLabel(actYwNode.type, 'act_node_type', '')}-${actYwNode.name}</option>
								</c:if>
								<c:if test="${actYwNode.id ne actYwGnode.nodeId}">
									<option value="${actYwNode.id}" data-formId="${actYwNode.form.id}" data-roleId="${actYwNode.role.id}" data-name="${actYwNode.name}" data-type="${actYwNode.type}" data-label="${fns:getDictLabel(actYwNode.type, 'act_node_type', '')}" >${fns:getDictLabel(actYwNode.type, 'act_node_type', '')}-${actYwNode.name}</option>
								</c:if>
							</c:forEach>
						</form:select>
						<form:hidden id="nameHidden" path="name"/>
						<form:hidden id="nodeIdHidden" path="node.id"/>
						<form:hidden id="nodeNameHidden" path="node.name" />
						<span class="help-inline"><font color="red">*</font> </span>
						<script type="text/javascript">
							$(function(){
								changeNode();
							});

							function changeNode(){
								var nodeGnode = $("#nodeGnode");
								var nameHidden = $("#nameHidden");
								var nodeIdHidden = $("#nodeIdHidden");
								var nodeNameHidden = $("#nodeNameHidden");

								$(nodeGnode).change(function(){
									$(nameHidden).val($(this).find("option:selected").attr("data-name"));
									$(nodeIdHidden).val($(this).val());
									$(nodeNameHidden).val($(this).find("option:selected").attr("data-name"));
									$("#formIdDiv").find("select").val($(this).find("option:selected").attr("data-formId"));
									$("#flowGroupDiv").find("select").val($(this).find("option:selected").attr("data-roleId"));
								});
							}
						</script>
					</div> --%>

					<c:if test="${empty actYwGnode.id}">
						<script type="text/javascript">
							$(function(){
								/* changeGnodeMT(); */
							});

							function changeGnodeMT(){
								var gnodeMTWrite = $("#gnodeMTWrite");
								var gnodeMTSelect = $("#gnodeMTSelect");

								var gnodeMTWriteTpl = '<input class="gnodeMTWrite" name="gnodeMT" type="radio" value="0" checked="checked">自定义<input class="gnodeMTWrite" name="gnodeMT" type="radio" value="1" >模板';
								var gnodeMTSelectTpl = '<input class="gnodeMTSelect" name="gnodeMT" type="radio" value="0">自定义<input class="gnodeMTSelect" name="gnodeMT" type="radio" value="1" checked="checked">模板';

								$(".gnodeMTSelect").remove();
								$(gnodeMTWrite).append(gnodeMTWriteTpl);

								var gnodeMT = $("input[name='gnodeMT']");
								$(gnodeMT).change(function(){
									var gnodeMTVal = $('input[name="gnodeMT"]:checked').val();
									if(gnodeMTVal == "0"){
										$(gnodeMTWrite).show();
										$(gnodeMTSelect).hide();
										$(".gnodeMTSelect").remove();
										$(gnodeMTWrite).append(gnodeMTWriteTpl);
									}else if(gnodeMTVal == "1"){
										$(gnodeMTWrite).hide();
										$(gnodeMTSelect).show();
										$(".gnodeMTWrite").remove();
										$(gnodeMTSelect).append(gnodeMTSelectTpl);
									}
								});
							}
						</script>
					</c:if>
				</div>

				<c:if test="${(not empty actYwGnode.preGnode) && (not empty actYwGnode.nextGnode)}">
					<%-- <c:if test="${not empty actYwGnode.preGnode}">
						<div id="nodePreGnodeDiv" class="control-group">
							<label class="control-label">流程前置节点：</label>
							<div class="controls">
								<span id="preGnodeName">${actYwGnode.preGnode.name }</span>
								<form:hidden path="preId"/>
								<form:hidden path="preGnode.id"/>
								<form:hidden id="preGnodeNameHidden" path="preGnode.name" />
							</div>
						</div>
					</c:if> --%>
					<c:if test="${not empty actYwGnode.nextGnode}">
						<div id="nodeNextGnodeDiv" class="control-group">
							<label class="control-label">流程后置节点：</label>
							<div class="controls">
								<span id="nextGnodeName">${actYwGnode.nextGnode.name }</span>
								<form:hidden path="nextId"/>
								<form:hidden path="nextGnode.id"/>
								<form:hidden id="nextGnodeNameHidden" path="nextGnode.name" />
							</div>
						</div>
					</c:if>
				</c:if>

				<div id="nextNextGnodeDiv" class="control-group">
					<label class="control-label">流程后置业务节点：</label>
					<div class="controls">
						<form:select id="nextFunGnode" path="nextFunId" class="input-xlarge required">
							<form:option value="" label="--请选择--"/>
							<%-- <c:forEach var="actYwPpNnGnode" items="${actYwPpNnGnodes }"> --%>
							<c:forEach var="actYwPpNnGnode" items="${actYwNnextGnodes }">
								<c:if test="${(actYwPpNnGnode.node.nodeKey ne 'StartErrorEvent') && (actYwPpNnGnode.node.nodeKey ne 'StartMessageEvent') && (actYwPpNnGnode.node.nodeKey ne 'StartSignalEvent') && (actYwPpNnGnode.node.nodeKey ne 'StartTimerEvent') && (actYwPpNnGnode.node.nodeKey ne 'StartNoneEvent')}">
									<c:if test="${actYwPpNnGnode.id eq actYwGnode.nextFunId}">
										<option value="${actYwPpNnGnode.id}" data-id="${actYwPpNnGnode.node.id}" data-name="${actYwPpNnGnode.name}" data-preId="${actYwPpNnGnode.preId}" selected="selected">${actYwPpNnGnode.parent.name}->${actYwPpNnGnode.name}</option>
									</c:if>
									<c:if test="${actYwPpNnGnode.id ne actYwGnode.nextFunId}">
										<option value="${actYwPpNnGnode.id}" data-id="${actYwPpNnGnode.node.id}" data-name="${actYwPpNnGnode.name}" data-preId="${actYwPpNnGnode.preId}">${actYwPpNnGnode.parent.name}->${actYwPpNnGnode.name}</option>
									</c:if>
								</c:if>
							</c:forEach>
						</form:select>
						<form:hidden id="nextFunIdHidden" path="nextFunGnode.id"/>
						<form:hidden id="nextFunNodeIdHidden" path="nextFunGnode.node.id"/>
						<form:hidden id="nextFunNodeNameHidden" path="nextFunGnode.name" />
						<form:hidden id="nextFunNodePreIdHidden" path="preFunGnode.preId" />
					</div>
					<script type="text/javascript">
						$(function(){
							changeNextFunGnode();
						});

						function changeNextFunGnode(){
							var nextFunGnode = $("#nextFunGnode");
							var nextFunIdHidden = $("#nextFunIdHidden");
							var nextFunNodeIdHidden = $("#nextFunNodeIdHidden");
							var nextFunNodeNameHidden = $("#nextFunNodeNameHidden");
							var nextFunNodePreIdHidden = $("#nextFunNodePreIdHidden");

							$(nextFunGnode).change(function(){
								$(nextFunIdHidden).val($(this).val());
								$(nextFunNodeIdHidden).val($(this).find("option:selected").attr("data-id"));
								$(nextFunNodeNameHidden).val($(this).find("option:selected").attr("data-name"));
								$(nextFunNodePreIdHidden).val($(this).find("option:selected").attr("data-preId"));
							});
						}
					</script>
				</div>
				<!-- <legend><h5>表单</h5></legend> -->
				<%-- <div class="control-group">
					<label class="control-label">是否为表单节点:</label>
					<div class="controls">
						<form:select id="isForm" path="isForm" class="input-xlarge required">
							<form:options items="${fns:getDictList('true_false')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div> --%>
				<form:hidden id="isForm" path="isForm" />
				<div id="formIdDiv" style='display: none'class="control-group">
					<label class="control-label">表单：</label>
					<div class="controls">
						<form:select path="formId" class="input-xlarge ">
							<form:option value="" label="--请选择--"/>
							<c:forEach var="item" items="${actYwForms }">
								<c:if test="${item.id eq actYwGnode.formId}">
									<option value="${item.id}" selected="selected">${item.name}</option>
								</c:if>
								<c:if test="${item.id ne actYwGnode.formId}">
									<option value="${item.id}">${item.name}</option>
								</c:if>
							</c:forEach>
						</form:select>
					</div>
				</div>
				<script type="text/javascript">
					$(function(){
						changeFormNode();
					});

					function changeFormNode(){
						var isForm = $("#isForm");
						var formIdDiv = $("#formIdDiv");

						$(isForm).attr("value", true);
						if("${actYwGnode.parent.id eq '1' }" == 'true'){
							$(formIdDiv).hide();
						}else{
							if(($(isForm).val() == 'true')){
								$(formIdDiv).show();
							}else{
								$(formIdDiv).hide();
							}
						}

						$(isForm).change(function(){
							var isFormVal = ($(this).val() == 'true')?true:false;
							if(isFormVal){
								$(formIdDiv).show();
							}else{
								$(formIdDiv).hide();
							}
						});
					}
				</script>
				<div class="control-group">
				   <label class="control-label"><font color="red">*</font> 图标:</label>
				   <form:hidden path="iconUrl"  />
				   <div class="controls">
					   <ul id="uploadList" style="width: 150px;">
						   <c:if test="${not empty actYwGnode.iconUrl}">
							   <li class="file-item thumbnail">
								   <img  src="${fns:ftpImgUrl(actYwGnode.iconUrl)}"  heigth="100" width="100"/>
							   </li>
						   </c:if>
					   </ul>
					   <div id="filePicker">上传图标</div>
				   </div>
			   </div>
				<div class="control-group">
					<label class="control-label">显示:</label>
					<div class="controls">
						<form:select path="isShow" class="input-xlarge ">
							<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
					</div>
				</div>
				<!-- <legend><h5>状态</h5></legend> -->
				<%-- <div class="control-group">
					<label class="control-label">排序：</label>
					<div class="controls">
						<form:input path="sort" htmlEscape="false" maxlength="50"
							class="required digits input-small" />
						<span class="help-inline">排列顺序，升序。</span>
					</div>
				</div> --%>
				<c:if test="${not empty actYwGnode.id }">
					<div class="control-group">
						<label class="control-label">所属机构:</label>
						<div class="controls">
							${actYwGnode.office.name }
						</div>
					</div>
				</c:if>

				<div id="flowGroupDiv" class="control-group">
					<label class="control-label">角色:</label>
					<div class="controls">
						<form:select id="flowGroup" path="flowGroup" class="input-xlarge">
							<form:option value="" label="所有角色"/>
							<c:forEach items="${roleList}" var="role">
								<form:option value="${role.id}" label="${role.name}"/>
							</c:forEach>
						</form:select></div>
				</div>
				<script type="text/javascript">
					$(function(){
						changeRoleNode();
					});

					function changeRoleNode(){
						var flowGroupDiv = $("#flowGroupDiv");

						if("${actYwGnode.parent.id eq '1' }" == 'true'){
							$(flowGroupDiv).hide();
						}else{
							$(flowGroupDiv).show();
						}
					}
				</script>
				<div class="control-group">
					<label class="control-label">备注：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge "/>
					</div>
				</div>
				<div class="form-actions">
					<shiro:hasPermission name="actyw:actYwGnode:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>

			</fieldset>
		</form:form>
	</div>
	<div id="dialog-message" title="信息">
		<p id="dialog-content"></p>
	</div>
</body>
</html>