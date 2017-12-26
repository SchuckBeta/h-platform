<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>课程学分认定</title>
	<meta name="decorator" content="site-decorator" />
	<link rel="stylesheet" type="text/css" href="/css/sco/frontCreditModule.css">
	<script type="text/javascript" src="/js/sco/lessonCodeSearch.js"></script>
	<script type="text/javascript">
		//添加学分课程
		function add(){
			var courseId = $("#courseId").val();
			if(courseId==''){
				showModalMessage(0, "请先选择课程");
				return false;
			}
			$.ajax({
				type:'post',
				url:'/f/scoapply/saveAdd',
				dataType:'json',
				data: {courseId:courseId},
				success:function(data){
					if(data.success){
						showModalMessage(1, data.msg, {
							'是': function() {
								top.location = "/f/scoapply/scoApplyAdd";
							},
							'否': function() {
								top.location = "/f/scoapply/scoApplyList"
							}
						});
					}else{
						showModalMessage(0,data.msg, {
							'确定': function() {
								top.location = "/f/scoapply/scoApplyAdd";
							}});
					}
				}
			});

		}
		function goList() {
			window.location.href='/f/scoapply/scoApplyList';
		}
	</script>
</head>
<body>
<div class="container">
	<div class="edit-bar clearfix">
		<div class="edit-bar-left">
			<span>添加认定课程</span>
			<i class="line weight-line"></i>
		</div>
	</div>
	<form id="formLesson" class="form-horizontal">
		<div class="form-group">
			<label class="control-label col-md-2">课程查询：</label>
			<div class="col-md-5">
				<div class="input-group">
					<input class="form-control" name="keyword" id="searchCode" type="text" placeholder="输入课程代码或者课程名称">
					<span class="input-group-addon"  data-search="lessonCode"  style="cursor: pointer"><i class="icon-search"></i></span>
				</div>
			</div>
			<div id="result" class="col-md-offset-2 col-md-10">
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-md-2">课程名：</label>
			<div class="col-md-5">
				<input class="form-control" name="name" type="text" readonly>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-md-2">计划课时：</label>
			<div class="col-md-5">
				<input class="form-control" name="planTime" type="text" readonly>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-md-2">计划学分：</label>
			<div class="col-md-5">
				<input class="form-control" name="planScore" type="text" readonly>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-md-2">合格成绩：</label>
			<div class="col-md-5">
				<div class="input-group">
					<input class="form-control" name="overScore" type="text" readonly>
					<span class="input-group-addon">分及以上</span>
				</div>
			</div>
		</div>
		<div class="form-group">
			<div class="col-md-9 text-center">
				<button type="button" class="btn btn-primary-oe" onclick="add();">添加</button>
				<button type="button" class="btn btn-cancel-oe" onclick="goList();">取消</button>
			</div>
		</div>
		<input type="hidden" id="courseId" name="courseId" />
	</form>
</div>
<div id="modalResult" class="modal fade">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">课程代码搜索</h4>
			</div>
			<form:form modelAttribute="scoCourse" >
			<div class="modal-body">
				<div id="modalFormInline" class="form-inline" style="margin-bottom: 15px">
					<div>
						<div class="form-group">
							<label class="control-label">课程查询</label>
							<input type="text" name="keyword" class="form-control input-sm">
						</div>
						<div class="form-group">
							<label class="control-label">课程类型</label>
							<form:select path="type" class="form-control input-sm">
								<form:option value="" label="--请选择--"/>
								<form:options items="${fns:getDictList('0000000102')}" itemLabel="label" itemValue="value"
											  htmlEscape="false"/>
							</form:select>
						</div>
						<div class="form-group">
							<label class="control-label">课程性质</label>
							<form:select path="nature" class="form-control input-sm">
								<form:option value="" label="--请选择--"/>
								<form:options items="${fns:getDictList('0000000108')}" itemLabel="label" itemValue="value"
											  htmlEscape="false"/>
							</form:select>
						</div>
						<button type="button" id="modalBtnSearch" data-search="lessonCode" class="btn btn-primary-oe btn-sm pull-right">查询</button>
					</div>
					<div class="form-group form-block" style="margin-top: 15px">
						<label class="control-label">面向专业科别</label>
					   <form:checkboxes path="professional" items="${fns:getDictList('0000000111')}" itemLabel="label" itemValue="value"></form:checkboxes>
					</div>
				</div>
				<div id="modalResultBox"></div>
			</div>
			</form:form>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal" >取消</button>
			</div>
		</div>
	</div>
</div>

</body>
</html>