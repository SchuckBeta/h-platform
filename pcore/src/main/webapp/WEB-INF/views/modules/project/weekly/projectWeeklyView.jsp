<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>项目周报</title>
<meta name="decorator" content="site-decorator" />
<link rel="stylesheet" type="text/css" href="/static/bootstrap/2.3.1/awesome/font-awesome.min.css">
<link rel="stylesheet" type="text/css"
	href="/common/common-css/bootstrap.min.css" />
<script type="text/javascript"
	src="/common/common-js/datepicker/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" type="text/css" href="/css/projectForm.css" />
	<script src="/other/jquery.form.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/common/common-js/jquery.validate.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/common/common-js/messages_zh.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="/js/projectWeekly.js" type="text/javascript" charset="utf-8"></script>
</head>
<body>
	<div class="container-fluid">
		<div class="top-title">
			<h3>${projectDeclare.name}</h3>
			<h4>项目周报</h4>
			<div class="top-bread">
				<div class="top-prj-num">
					<span>项目编号:</span>${projectDeclare.number}</div>
				<div class="top-prj-num">
					<span>创建时间:</span>
					<fmt:formatDate value="${projectDeclare.createDate}" />
				</div>
				<a href="javascript:;" class="btn btn-sm btn-primary"
					onclick="history.go(-1);">返回</a>
			</div>
		</div>
		<form:form id="inputForm" modelAttribute="vo" action="submit" method="post" class="form-horizontal" enctype="multipart/form-data">
			<input type="hidden" id="projectWeekly.projectId" name="projectWeekly.projectId"
				value="${projectWeekly.projectId}">
			<div class="outer-wrap">
				<div class="container-fluid">
					<div class="row content-wrap">
						<section class="row">
							<div class="form-horizontal" novalidate>
								<div class="form-group col-sm-4 col-md-4 col-lg-4">
									<label class="col-xs-5 ">汇报人：</label>
									<p class="col-xs-7">${cuser.name}</p>
								</div>
								<div class="form-group col-sm-4 col-md-4 col-lg-4"></div>
								<div class="form-group col-sm-4 col-md-4 col-lg-4">
									<label class="col-xs-5">职责：</label>
									<p class="col-xs-7">${duty}</p>
								</div>
							</div>

							<div class="form-horizontal" novalidate>
								<div class="form-group col-sm-4 col-md-4 col-lg-4">
									<label class="col-xs-5 ">项目组成员：</label>
									<p class="col-xs-7">${teamList}</p>
								</div>
								<div class="form-group col-sm-4 col-md-4 col-lg-4"></div>
								<div class="form-group col-sm-4 col-md-4 col-lg-4">
									<label class="col-xs-5">项目导师：</label>
									<p class="col-xs-7">${teacher}</p>
								</div>
							</div>
						</section>
							<section class="row">
								<div class="prj_common_info"
									style="height: 40px; line-height: 40px;">
									<h4 class="sub-file" style="margin-top: 25px;">上周任务小结</h4><span class="yw-line yw-line-fj"></span>
									<span href="javascript:;" class="upload-file"
										style="background: none; color: #656565 !important;"><strong>时间：<fmt:formatDate
												value="${vo.lastpw.startDate}" pattern="yyyy-MM-dd" />-<fmt:formatDate
												value="${vo.lastpw.endDate}" pattern="yyyy-MM-dd" /></strong></span>
								</div>
								<div class="table-wrap">
									<div class="form-horizontal" novalidate>
										<label class="col-xs-5 " style="padding-left: 0;">上周任务计划：</label>
										<div class="textarea-wrap">
											<textarea readonly="readonly"
												class="col-xs-12 col-md-12 col-sm-12 col-lg-12 my-textarea">${vo.lastpw.plan}</textarea>
										</div>
									</div>

									<div class="form-horizontal" novalidate>
										<label class="col-xs-5 " style="padding-left: 0;">完成情况：</label>
										<div class="textarea-wrap">
											<textarea readonly="readonly"
												class="col-xs-12 col-md-12 col-sm-12 col-lg-12 my-textarea">${vo.lastpw.achieved}</textarea>
										</div>
									</div>

									<div class="form-horizontal" novalidate>
										<label class="col-xs-5 " style="padding-left: 0;">存在的问题：</label>
										<div class="textarea-wrap">
											<textarea readonly="readonly"
												class="col-xs-12 col-md-12 col-sm-12 col-lg-12 my-textarea">${vo.lastpw.problem}</textarea>
										</div>
									</div>
								</div>
							</section>

							<section class="row">
								<div class="prj_common_info prj-task-info"
									style="height: 40px; line-height: 40px;">
									<h4 class="sub-file" style="margin-top: 25px;">本周任务计划</h4><span class="yw-line yw-line-fj"></span>
									<div class="task_time_wrap">
										<span class="time_label">任务时间：</span>
										<div class="task_time">
											<input readonly="readonly" class="Wdate" type="text" name="projectWeekly.startDate"  value='<fmt:formatDate
												value="${vo.projectWeekly.startDate}" pattern="yyyy-MM-dd" />'
												/> <span>至</span> <input readonly="readonly" value='<fmt:formatDate
												value="${vo.projectWeekly.endDate}" pattern="yyyy-MM-dd" />'
												class="Wdate" type="text" name="projectWeekly.endDate"
												/>
										</div>
									</div>
								</div>
								<div class="textarea-wrap">
									<textarea readonly="readonly" name="projectWeekly.plan"
										class="col-xs-12 col-md-12 col-sm-12 col-lg-12 my-textarea">${vo.projectWeekly.plan}</textarea>
								</div>
							</section>

							<section class="row">
								<div class="prj_common_info"
									style="height: 40px; line-height: 40px;">
									<h4 class="sub-file" style="margin-top: 25px;">附件</h4><span class="yw-line yw-line-fj"></span>
								</div>
								<sys:frontFileUpload fileitems="${vo.fileInfo}" filepath="weekly" btnid="upload" readonly="true"></sys:frontFileUpload>
							</section>
							<section class="row">
								<div class="prj_common_info"
									style="height: 40px; line-height: 40px;">
									<h4 class="sub-file" style="margin-top:25px;">导师意见及建议</h4><span class="yw-line yw-line-fj"></span>
									<span href="javascript:;" class="upload-file2"
										style="background: none; color: #656565 !important;"><strong>时间：<fmt:formatDate
												value="${vo.projectWeekly.suggestDate}" pattern="yyyy-MM-dd" /></strong></span>
								</div>
								<textarea name="suggest" readonly="readonly"
									class="col-xs-12 col-md-12 col-sm-12 col-lg-12 my-textarea">${projectWeekly.suggest}</textarea>
							</section>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</body>
</html>