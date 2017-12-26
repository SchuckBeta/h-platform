<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${backgroundTitle}</title>
	<%@include file="/WEB-INF/views/include/backcyjd.jsp"%>
</head>
<body>
	<div class="container-fluid container-fluid-audit">
		<div class="edit-bar clearfix">
			<div class="edit-bar-left">
				<span>入驻管理</span> <i class="line weight-line"></i>
			</div>
		</div>
		<form:form id="inputForm" modelAttribute="pwEnter" action=""
			method="post" class="form-horizontal form-container">
			<form:hidden path="id" />
			<form:hidden path="applicant.id" />
			<div class="row-fluid row-info-fluid">
				<div class="span6">
					<span class="item-label">申请人：</span>${pwEnter.applicant.name}
				</div>
				<div class="span6">
					<span class="item-label">专业：</span>${pwEnter.applicant.office.name}
				</div>
			</div>
			<div class="row-fluid row-info-fluid">
				<div class="span6">
					<span class="item-label">学院：</span>${pwEnter.applicant.office.name}
				</div>
				<div class="span6">
					<span class="item-label">学号：</span>${pwEnter.applicant.no}
				</div>
			</div>
			<div class="row-fluid row-info-fluid">
				<div class="span6">
					<span class="item-label">联系方式：</span>${pwEnter.applicant.mobile}
				</div>
				<div class="span6">
					<span class="item-label">邮件：</span>${pwEnter.applicant.email}
				</div>
			</div>
			<c:if test="${not empty pwEnter.eproject}">
				<input id="paramsProjectTeamId" type="hidden"
					value="${pwEnter.eproject.project.teamId}">
				<input id="paramsProjectProId" type="hidden"
					value="${pwEnter.eproject.project.id}">
				<div class="edit-bar edit-bar-sm clearfix">
					<div class="edit-bar-left">
						<span>入驻项目基本信息</span> <i class="line"></i> <a
							data-toggle="collapse" href="#projectInfo"><i
							class="icon-collaspe icon-double-angle-up"></i></a>
					</div>
				</div>
				<div id="projectInfo" class="panel-body collapse in">
					<div class="panel-inner">
						<div class="row-fluid row-info-fluid">
							<div class="span6">
								<span class="item-label">项目名称：</span>${pwEnter.eproject.project.name}
							</div>
							<div class="span6">
								<span class="item-label">项目类型：</span>${fns:getDictLabel(pwEnter.eproject.project.proTyped, 'act_project_type', '')}
							</div>
						</div>
						<div class="row-fluid row-info-fluid">
							<div class="span6">
								<span class="item-label">项目編号：</span>${pwEnter.eproject.project.number}
							</div>
							<div class="span6">
								<span class="item-label">项目类别：</span>
								<c:if test="${fn:contains(pwEnter.eproject.project.proType, '1,')}">
                           			${fns:getDictLabel(pwEnter.eproject.project.type, 'project_style', '')}
                           		</c:if>
                           		<c:if test="${fn:contains(pwEnter.eproject.project.proType, '7,')}">
                           			${fns:getDictLabel(pwEnter.eproject.project.type, 'competition_type', '')}
                           		</c:if>
							</div>
						</div>
					</div>
					<div class="panel-inner">
						<div class="table-caption">学生团队</div>
						<table
							class="table table-bordered table-condensed table-orange table-center table-nowrap table-hover">
							<thead>
								<tr>
									<th>序号</th>
									<th>姓名</th>
									<th>学号</th>
									<th>学院</th>
									<th>专业</th>
									<th>联系电话</th>
								</tr>
							</thead>
							<tbody id="projectTableStudent">

							</tbody>
						</table>
						<div class="table-caption">指导老师</div>
						<table
							class="table table-bordered table-condensed table-hover table-center table-orange table-nowrap">
							<thead>
								<tr>
									<th>序号</th>
									<th>姓名</th>
									<th>工号</th>
									<th>导师类型</th>
									<th>联系电话</th>
									<th>E-mail</th>
									<th>单位（学院或企业、机构）</th>
								</tr>
							</thead>
							<tbody id="projectTableTeacher">

							</tbody>
						</table>
					</div>
					<div class="edit-bar edit-bar-sm clearfix hide">
						<div class="edit-bar-left">
							<span>附件</span> <i class="line"></i>
						</div>
					</div>
					<div class="accessories">
						<div class="accessory" id="projectFiles">
							<%--<div class="accessory-info">--%>
							<%--<a><img src="/img/filetype/rar.png">--%>
							<%--<span class="accessory-name">fhjd.rar</span>--%>
							<%--</a><i class="btn-delete-accessory"><img src="/img/remove-accessory.png"></i>--%>
							<%--</div>--%>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty pwEnter.eteam}">
				<input id="paramsTeamId" type="hidden"
					value="${pwEnter.eteam.team.id}">
				<div class="edit-bar edit-bar-sm clearfix">
					<div class="edit-bar-left">
						<span>入驻团队信息</span> <i class="line"></i> <a data-toggle="collapse"
							href="#teamInfo"><i
							class="icon-collaspe icon-double-angle-up"></i></a>
					</div>
				</div>
				<div id="teamInfo" class="panel-body collapse in">
					<div class="panel-inner">
						<p>项目团队：${pwEnter.eteam.team.name}</p>
						<div class="table-caption">学生团队</div>
						<table
							class="table table-bordered table-condensed table-orange table-center table-nowrap table-hover">
							<thead>
								<tr>
									<th>序号</th>
									<th>姓名</th>
									<th>学号</th>
									<th>学院</th>
									<th>专业</th>
									<th>联系电话</th>
								</tr>
							</thead>
							<tbody id="teamTableStudent">

							</tbody>
						</table>
						<div class="table-caption">指导老师</div>
						<table
							class="table table-bordered table-condensed table-hover table-center table-orange table-nowrap">
							<thead>
								<tr>
									<th>序号</th>
									<th>姓名</th>
									<th>工号</th>
									<th>导师类型</th>
									<th>联系电话</th>
									<th>E-mail</th>
									<th>单位（学院或企业、机构）</th>
								</tr>
							</thead>
							<tbody id="teamTableTeacher">

							</tbody>
						</table>
						<div class="edit-bar edit-bar-sm clearfix hide">
							<div class="edit-bar-left">
								<span>附件</span> <i class="line"></i>
							</div>
						</div>
						<div class="accessories">
							<div class="accessory" id="teamFiles">
								<%--<div class="accessory-info">--%>
								<%--<a><img src="/img/filetype/rar.png">--%>
								<%--<span class="accessory-name">fhjd.rar</span>--%>
								<%--</a><i class="btn-delete-accessory"><img src="/img/remove-accessory.png"></i>--%>
								<%--</div>--%>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty pwEnter.ecompany}">
				<div class="edit-bar edit-bar-sm clearfix">
					<div class="edit-bar-left">
						<span>入驻企业基本信息</span> <i class="line"></i> <a
							data-toggle="collapse" href="#companyDetail"><i
							class="icon-collaspe icon-double-angle-up"></i></a>
					</div>
				</div>
				<div id="companyDetail" class="panel-body collapse in">
					<div class="panel-inner">
						<div class="row-fluid row-info-fluid">
							<div class="span6">
								<span class="item-label">企业名称：</span>${pwEnter.ecompany.pwCompany.name}
							</div>
							<div class="span6">
								<span class="item-label">企业法人：</span>${pwEnter.ecompany.pwCompany.regPerson}
							</div>
						</div>
						<div class="row-fluid row-info-fluid">
							<div class="span6">
								<span class="item-label">企业资金：</span>${pwEnter.ecompany.pwCompany.regMoney}万元
							</div>
							<div class="span6">
								<span class="item-label">企业地址：</span>${pwEnter.ecompany.pwCompany.address}
							</div>
						</div>
						<div class="row-fluid row-info-fluid">
							<div class="span6">
								<span class="item-label">公司执照：</span>${pwEnter.ecompany.pwCompany.no}
							</div>
							<div class="span6">
								<span class="item-label">联系电话：</span>${pwEnter.ecompany.pwCompany.mobile}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" style="width: 120px;"><i>*</i>资金来源：</label>
							<div class="controls controls-checkbox"
								style="margin-left: 140px;">
								<form:checkboxes path="ecompany.pwCompany.regMtypes"
									items="${fns:getDictList('pw_reg_mtype')}" itemLabel="label"
									itemValue="value" htmlEscape="false" class="required" />
							</div>
						</div>
						<div class="edit-bar edit-bar-sm clearfix hide">
							<div class="edit-bar-left">
								<span>附件</span> <i class="line"></i>
							</div>
						</div>
						<div class="accessories-container">
							<div class="accessories">
								<div class="accessory" id="companyFiles">
									<%--<div class="accessory-info">--%>
									<%--<a><img src="/img/filetype/rar.png">--%>
									<%--<span class="accessory-name">fhjd.rar</span>--%>
									<%--</a><i class="btn-delete-accessory"><img src="/img/remove-accessory.png"></i>--%>
									<%--</div>--%>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${(not empty pwEnter.eroom) && (not empty pwEnter.eroom.pwRoom)}">
				<input id="paramsRoomId" type="hidden" value="${pwEnter.eroom.pwRoom.id}">
				<div class="edit-bar edit-bar-sm clearfix">
					<div class="edit-bar-left">
						<span>分配房间信息</span> <i class="line"></i> <a data-toggle="collapse" href="#roomAssetInfo"><i class="icon-collaspe icon-double-angle-up"></i></a>
					</div>
				</div>
				<div id="roomAssetInfo" class="panel-body collapse in">
					<div class="row-fluid row-info-fluid">
						<div class="span6">
							<span class="item-label">房间名：</span>${pwEnter.eroom.pwRoom.name}
						</div>
						<div class="span6">
							<span class="item-label">别名：</span>${pwEnter.eroom.pwRoom.alias}
						</div>
					</div>
					<div class="row-fluid row-info-fluid">
						<div class="span6">
							<span class="item-label">房间类型：</span>${fns:getDictLabel(pwEnter.eroom.pwRoom.type, 'pw_room_type', '')}
						</div>
						<div class="span6">
							<span class="item-label">容纳人数：</span>${pwEnter.eroom.pwRoom.num}
						</div>
					</div>
					<div class="row-fluid row-info-fluid">
						<div class="span6">
							<span class="item-label">负责人：</span>${pwEnter.eroom.pwRoom.person}
						</div>
						<div class="span6">
							<span class="item-label">手机：</span>${pwEnter.eroom.pwRoom.mobile}
						</div>
					</div>
					<div class="row-fluid row-info-fluid">
						<div class="span6">
							<span class="item-label">楼/层：</span>
							<c:if test="${(not empty pwEnter.eroom.pwRoom.pwSpace.parent) && (pwEnter.eroom.pwRoom.pwSpace.parent.id ne root)}">
								<c:if test="${(not empty pwEnter.eroom.pwRoom.pwSpace.parent.parent) && (pwEnter.eroom.pwRoom.pwSpace.parent.parent.id ne root)}">
									<c:if test="${(not empty pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent) && (pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent.id ne root)}">
										<c:if test="${(not empty pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent.parent) && (pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent.parent.id ne root)}">
										${pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent.parent.name}/
										</c:if>${pwEnter.eroom.pwRoom.pwSpace.parent.parent.parent.name}/
									</c:if>${pwEnter.eroom.pwRoom.pwSpace.parent.parent.name}/
								</c:if>${pwEnter.eroom.pwRoom.pwSpace.parent.name}/
							</c:if>${pwEnter.eroom.pwRoom.pwSpace.name}
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${!param.isView}">
				<div class="control-group">
					<label class="control-label"><i>*</i>意见和建议：</label>
					<div class="controls">
						<textarea id="auditRemarks" class="input-xxlarge required"
							rows="3" placeholder="同意">${pwEnter.remarks}</textarea>
					</div>
				</div>
			</c:if>
			<div class="text-center">
				<c:if test="${!param.isView}">
					<c:if test="${pwEnter.status eq '0'}">
						<button class="btn btn-primary" type="button" class="submit"
							onclick="ajaxAudit('${pwEnter.id}', '', '1')">同意</button>
						<button class="btn btn-default" type="button" class="button"
							onclick="ajaxAudit('${pwEnter.id}', '', '0')">拒绝</button>
					</c:if>
					<c:if test="${(empty pwEnter.eroom) || (empty pwEnter.eroom.pwRoom)}">
						<c:if test="${pwEnter.status eq '1'}">
							<a class="btn btn-default" href="${ctx }/pw/pwRoom/treeFPCD" class="button">前往分配场地</a>
						</c:if>
					</c:if>
				</c:if>
				<a class="btn btn-default" onclick="history.go(-1)" class="button">返回</a>
			</div>
		</form:form>
	</div>


	<script>
		var formValidate = $('#inputForm').validate();
		function ajaxAudit(id, edid, atype) {
			var auditRemarks = $("#auditRemarks").val();
			if (atype === '0') {
				if (formValidate.form()) {
					$.ajax({
						type : 'GET',
						url : '${ctx }/pw/pwEnter/ajaxAudit?id=' + id
								+ '&atype=' + atype + '&edid=' + edid
								+ '&remarks=' + auditRemarks,
						dataType : "json",
						success : function(data) {
							if (data.status) {
								window.location.reload();
							} else {
								alertx(data.msg)
							}
						}
					});
				}
			} else {
				$.ajax({
					type : 'GET',
					url : '${ctx }/pw/pwEnter/ajaxAudit?id=' + id + '&atype='
							+ atype + '&edid=' + edid + '&remarks='
							+ auditRemarks,
					dataType : "json",
					success : function(data) {
						if (data.status) {
							window.location.reload();
						} else {
							alertx(data.msg)
						}
					}
				});
			}

		}

		$(function() {
			var tableList = function() {
				var teamId = $("#paramsTeamId").val(),
				paramsProjectTeamId = $("#paramsProjectTeamId").val(),
				paramsProjectProId = $("#paramsProjectProId").val();

	            if((teamId != undefined)){
					$.ajax({
						type : 'GET',
						url : '${ctx}/team/ajaxTeamStudent?teamid=' + teamId,
						dataType : "json",
						success : function(data) {
							if (data && data.datas) {
								var resp = data.datas, tableStudList = [];
								resp.map(function(it, idx) {
									tableStudList.push('<tr>' + '<td>' + (++idx)
											+ '</td>' + '<td>' + it.name + '</td>'
											+ '<td>' + it.no + '</td>' + '<td>'
											+ it.orgName + '</td>' + '<td>'
											+ it.professional + '</td>' + '<td>'
											+ it.mobile + '</td>' + '</tr>');
								});
								$("#teamTableStudent").append(
										tableStudList.join(","));
							} else {
								$("#teamTableStudent").append(
										'<tr>' + '<td colspan="6">没有数据</td>'
												+ '</tr>');
							}
							;
							//返回如果是undefined就不显示
							$("table td").map(function(idx, it) {
								if (it.innerHTML === "undefined") {
									it.innerHTML = "";
								}
								;
							});
						}
					});
	            }

	            if((teamId != undefined)){
					$.ajax({
						type : 'GET',
						url : '${ctx}/team/ajaxTeamTeacher?teamid=' + teamId,
						dataType : "json",
						success : function(data) {
							if (data && data.datas) {
								var resp = data.datas, tableTeaList = [];
								resp.map(function(it, idx) {
									tableTeaList.push('<tr>' + '<td>' + (++idx)
											+ '</td>' + '<td>' + it.name + '</td>'
											+ '<td>' + it.no + '</td>' + '<td>'
											+ it.teacherType + '</td>' + '<td>'
											+ it.mobile + '</td>' + '<td>'
											+ it.email + '</td>' + '<td>'
											+ it.orgName + '</td>' + '</tr>');
								});
								$("#teamTableTeacher").append(
										tableTeaList.join(","));
							} else {
								$("#teamTableTeacher").append(
										'<tr>' + '<td colspan="8">没有数据</td>'
												+ '</tr>');
							}
							;
							//返回如果是undefined就不显示
							$("table td").map(function(idx, it) {
								if (it.innerHTML === "undefined") {
									it.innerHTML = "";
								}
								;
							});
						}
					});
	            }

	            if((paramsProjectTeamId != undefined) || (paramsProjectProId != undefined)){
					$.ajax({
						type : 'GET',
						url : '${ctx}/team/ajaxTeamStudent?teamid=' + paramsProjectTeamId + "&proId=" + paramsProjectProId,
						dataType : "json",
						success : function(data) {
							if (data && data.datas) {
								var resp = data.datas, tableStudList = [];
								resp.map(function(it, idx) {
									tableStudList.push('<tr>' + '<td>' + (++idx)
											+ '</td>' + '<td>' + it.name + '</td>'
											+ '<td>' + it.no + '</td>' + '<td>'
											+ it.orgName + '</td>' + '<td>'
											+ it.professional + '</td>' + '<td>'
											+ it.mobile + '</td>' + '</tr>');
								});
								$("#projectTableStudent").append(
										tableStudList.join(","));
							} else {
								$("#projectTableStudent").append(
										'<tr>' + '<td colspan="6">没有数据</td>'
												+ '</tr>');
							}
							;
							//返回如果是undefined就不显示
							$("table td").map(function(idx, it) {
								if (it.innerHTML === "undefined") {
									it.innerHTML = "";
								}
								;
							});
						}
					});
	            }

	            if((paramsProjectTeamId != undefined) || (paramsProjectProId != undefined)){
					$.ajax({
						type : 'GET',
						url : '${ctx}/team/ajaxTeamTeacher?teamid='
								+ paramsProjectTeamId + "&proId="
								+ paramsProjectProId,
						dataType : "json",
						success : function(data) {
							if (data && data.datas) {
								var resp = data.datas, tableTeaList = [];
								resp.map(function(it, idx) {
									tableTeaList.push('<tr>' + '<td>' + (++idx)
											+ '</td>' + '<td>' + it.name + '</td>'
											+ '<td>' + it.no + '</td>' + '<td>'
											+ it.teacherType + '</td>' + '<td>'
											+ it.mobile + '</td>' + '<td>'
											+ it.email + '</td>' + '<td>'
											+ it.orgName + '</td>' + '</tr>');
								});
								$("#projectTableTeacher").append(
										tableTeaList.join(","));
							} else {
								$("#projectTableTeacher").append(
										'<tr>' + '<td colspan="8">没有数据</td>'
												+ '</tr>');
							}
							;
							//返回如果是undefined就不显示
							$("table td").map(function(idx, it) {
								if (it.innerHTML === "undefined") {
									it.innerHTML = "";
								}
								;
							});
						}
					});

				};
			}
			tableList();

		})

		$(function() {
			var S_ENTER_COMPANY = [];
			var S_ENTER_PROJECT = [];
			var S_ENTER_TEAM = [];
			var $teamFiles = $('#teamFiles');
			var $projectFiles = $('#projectFiles')
			var $companyFiles = $('#companyFiles')
			getFiles()
			function getFiles() {
				var xhr = $
						.get('${ctx}/attachment/sysAttachment/ajaxFiles/${pwEnter.id}?type=30000')
				xhr.success(function(data) {
					if (data.status && !!data.datas) {
						var temp1 = '';
						var temp2 = '';
						var temp3 = '';
						S_ENTER_COMPANY.length = 0;
						S_ENTER_PROJECT.length = 0;
						S_ENTER_TEAM.length = 0;
						data.datas.forEach(function(t) {
							if (t.fileStep === 'S_ENTER_COMPANY') {
								S_ENTER_COMPANY.push(t)
							}
							if (t.fileStep === 'S_ENTER_PROJECT') {
								S_ENTER_PROJECT.push(t)
							}
							if (t.fileStep === 'S_ENTER_TEAM') {
								S_ENTER_TEAM.push(t)
							}
						});
						S_ENTER_COMPANY.forEach(function(t) {
							temp1 += template(t)
						});
						S_ENTER_PROJECT.forEach(function(t) {
							temp2 += template(t)
						});
						S_ENTER_TEAM.forEach(function(t) {
							temp3 += template(t)
						})

						if (temp3) {
							$teamFiles.append(temp3)
							$teamFiles.parents('.accessories').prev().removeClass('hide')
						}
						if (temp2) {
							$projectFiles.append(temp2)
							$projectFiles.parents('.accessories').prev().removeClass('hide')
						}
						if ($companyFiles) {
							$companyFiles.append(temp1)
							$companyFiles.parents('.accessories').prev().removeClass('hide')
						}
					}
				})
			}

			function template(t) {
				var temp = '';
				temp += '<div class="accessory-info"> <a data-ftp-url="' + t.ftpUrl + '" href="' + t.url + '">'
						+ '<img src="'
						+ getImgType(t.suffix)
						+ '"> <span class="accessory-name">'
						+ t.name
						+ '</span>' + ' </a> </div>';
				return temp
			}

			function getImgType(type) {
				var extname;
				switch (type) {
				case "xls":
				case "xlsx":
					extname = "excel";
					break;
				case "doc":
				case "docx":
					extname = "word";
					break;
				case "ppt":
				case "pptx":
					extname = "ppt";
					break;
				case "jpg":
				case "jpeg":
				case "gif":
				case "png":
				case "bmp":
					extname = "image";
					break;
				case 'txt':
					extname = 'txt';
					break;
				case 'zip':
					extname = 'zip';
					break;
				case 'rar':
					extname = 'rar';
					break;
				default:
					extname = "unknow";
				}
				return '/img/filetype/' + extname + '.png';
			}

			$(document).on(
					'click',
					'.accessory-info a',
					function(e) {
						e.preventDefault();
						var url = $(this).attr('href')
						var name = $(this).find('.accessory-name').text();
						var ftpUrl = $(this).attr('data-ftp-url');
						location.href = "/ftp/ueditorUpload/downFile?url="
								+ url + "&fileName="
								+ encodeURI(encodeURI(name));
					})
		})
	</script>
</body>
</html>