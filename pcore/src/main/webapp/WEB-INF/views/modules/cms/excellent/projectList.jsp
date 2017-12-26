<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
    <title>优秀项目管理</title>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css"/>
    <link rel="stylesheet" type="text/css" href="/css/state/titlebar.css">
    <link rel="stylesheet" type="text/css" href="/css/topSearchForm.css">
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
          //增加学院下拉框change事件
            $("#collegeId").change(function () {
                var parentId = $(this).val();
                //根据当前学院id 更改
                $("#professionalSelect").empty();
                $("#professionalSelect").append('<option value="">--所有专业--</option>');
                $.ajax({
                    type: "post",
                    url: "/a/sys/office/findProfessionals",
                    data: {"parentId": parentId},
                    async: true,
                    success: function (data) {
                        $.each(data, function (i, val) {
                            if (val.id == "${vo.profession}") {
                                $("#professionalSelect").append('<option selected="selected" value="' + val.id + '">' + val.name + '</option>')
                            } else {
                                $("#professionalSelect").append('<option value="' + val.id + '">' + val.name + '</option>')
                            }

                        })
                    }
                });

            })
            $("#collegeId").trigger('change');
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
	function selectAll(ob){
    	if($(ob).attr("checked")) {  
            $("input[name='subck']:checkbox").attr("checked", true);  
        } else {  
            $("input[name='subck']:checkbox").attr("checked", false);  
        }  
    }
    function resall(){
    	var temarr=[];
		$("input[name='subck']:checked").each(function(i,v){
			temarr.push($(v).attr("data-fid"));
		});
		if(temarr.length==0){
			alertx("请选择要发布的项目");
			return;
		}
		confirmx("确定发布所选项目到门户网站？",function(){
			$.ajax({
    			type:'post',
    			url:'/a/excellent/resall',
    			dataType: "json",
    			data: {fids:temarr.join(",")
    				  },
    			success:function(data){
   					if(data.ret=="2"){
   						alertx(data.msg);
   					}else{
   						location.href=location;
   					}
    			}
    		});
		});
    }
    function unresall(){
    	var temarr=[];
		$("input[name='subck']:checked").each(function(i,v){
			temarr.push($(v).val());
		});
		if(temarr.length==0){
			alertx("请选择要取消发布的项目");
			return;
		}
		confirmx("确定取消发布所选项目？",function(){
			location.href="${ctx}/cms/excellent/unrelease?from=projectList&ids="+temarr.join(",");
		});
    }
    function deleteall(){
    	var temarr=[];
		$("input[name='subck']:checked").each(function(i,v){
			temarr.push($(v).val());
		});
		if(temarr.length==0){
			alertx("请选择要删除的项目");
			return;
		}
		confirmx("确定删除所选项目？",function(){
			location.href="${ctx}/cms/excellent/delete?from=projectList&ids="+temarr.join(",");
		});
    }
    function subckchange(ob){
    	if(!$(ob).attr("checked")){
    		$("#selectAllbtn").attr("checked",false);
    	}
    }
    </script>
    <style type="text/css">
        td, th {
            text-align: center !important;
        }

        th {
            background: #f4e6d4 !important;
        }

        .ul_info {
            margin-left: -32px;
        }

        .btns {
            float: right !important;
        }

        #btnSubmit {
            background: #e9432d !important;
        }

        table td .btn {
            padding: 3px 11px;
            background: #e5e5e5;
            color: #666;
        }

        table td .btn:hover {
            background: #e9432d;
            color: #fff;
        }
    </style>
</head>
<body>
<div class="container-fluid" style="padding-right:20px;padding-left: 20px;">
    <div class="edit-bar edit-bar-tag clearfix" style="margin-top: 30px">
        <div class="edit-bar-left">
            <span>优秀项目</span>
            <i class="line weight-line"></i>
        </div>
    </div>
     <c:if test="${not empty message}">
        <c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if>
        <c:if test="${empty type}"><c:set var="ctype"
                                          value="${fn:indexOf(message,'失败') eq -1?'success':'error'}"/></c:if>
        <div class="alert alert-${ctype}">
            <button data-dismiss="alert" class="close">×</button>
                ${message}</div>
    </c:if>
    <div>
        <form:form id="searchForm" modelAttribute="vo" action="${ctx}/cms/excellent/projectList" method="post"
                   class="form-inline form-lesson-box">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="right-form pull-right">
                    <form:input path="name" htmlEscape="false" maxlength="200" class="input-medium"
                                placeholder='项目名称/组成员'/>
                    <input id="btnSubmit" class="btn btn-danger" type="submit" value="查询"/>
                </div>
                <div class="left-form">
                    <div class="form-group">
                        <label for="categoryId">项目类型</label>
                        <form:select path="type" class="form-control" >
					<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('project_style')}"
							itemValue="value" itemLabel="label" htmlEscape="false" />
					</form:select>
                    </div>
                    <div class="form-group">
                        <label for="type">项目类别</label>
                        <form:select path="subtype" class="form-control" >
					<form:option value="" label="--请选择--"/>
						<form:options items="${fns:getDictList('project_type')}"
							itemValue="value" itemLabel="label" htmlEscape="false"/>
					</form:select>
                    </div>
                    <div class="form-group">
                        <label for="status">项目级别</label>
                        <form:select path="level" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('project_degree')}" itemLabel="label" itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                    <div class="form-group">
                    	<label for="collegeId">学院</label>
                        <form:select path="office" class="input-medium form-control" id="collegeId">
                            <form:option value="" label="--所有学院--"/>
                            <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                    <div class="form-group">
                    	<label for="collegeId">专业</label>
                        <form:select path="profession" class="input-medium form-control"
                                         id="professionalSelect">
                                <form:option value="" label="--所有专业--"/>
                            </form:select>
                    </div>
                    <div class="form-group">
                        <label for="publishFlag">状态分类</label>
                        <select name="state"  class="input-medium">
                            <option value="">--请选择--</option>
                            <option value="1" <c:if test="${vo.state=='1' }">selected</c:if> >已发布</option>
                            <option value="0" <c:if test="${vo.state=='0' }">selected</c:if> >待发布</option>
                        </select>
                    </div>
                </div>

            </div>
            <div class="row">
            	<div class="right-form pull-right">
            	<shiro:hasPermission name="excellent:projectShow:edit">
                    <input class="btn btn-danger" id="resallbtn" onclick="resall()" type="button" style="width: auto; height: auto;background: #e9432d !important" value="一键发布"/>
                    <input class="btn btn-danger" onclick="unresall()" type="button" style="width: auto; height: auto;background: #e9432d !important" value="取消发布"/>
                    <input class="btn btn-danger" onclick="deleteall()" type="button" style="width: auto; height: auto;background: #e9432d !important" value="批量删除"/>
                   </shiro:hasPermission>
                </div>
            </div>
        </form:form>
        <table id="contentTable" class="table table-hover table-bordered table-condensed table-no-radius">
            <thead>
            <tr><th><input type="checkbox" id="selectAllbtn" onclick="selectAll(this)"></th>
                <th>项目编号</th>
                <th>项目名称</th>
                <th>项目类型</th>
                <th>负责人</th>
                <th>项目组成员</th>
                <th>项目级别</th>
                <th>项目结果</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="es">
                <tr><td><input type="checkbox" name="subck" onclick="subckchange(this)" data-fid="${es.foreignId}" value="${es.id}" ></td>
                    <td>${es.number}
                    </td>
                    <td>${es.name}
                    </td>
                    <td>${es.typeStr }
                    </td>
                    <td>${es.leader }
                    </td>
                    <td>${es.members }
                    </td>
                    <td>${es.levelStr }
                    </td>
                    <td>${es.resultStr }
                    </td>
                    <td>
                    	${es.stateStr}
                    </td>
                    <td style="white-space:nowrap">
                    <shiro:hasPermission name="excellent:projectShow:edit">
                    <c:if test="${es.state=='0' }">
                    	<a href="${ctx}/cms/excellent/projectShowForm?projectId=${es.foreignId}" class="btn">审核</a>
                    </c:if>
                    <c:if test="${es.state=='1' }">
                        <a href="${ctx}/cms/excellent/unrelease?ids=${es.id}&from=projectList" class="btn">取消发布</a>
                    </c:if>
                        <a href="${ctx}/cms/excellent/delete?ids=${es.id}&from=projectList"
                           onclick="return confirmx('确认要删除吗？', this.href)" class="btn">删除</a>
                      </shiro:hasPermission>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        ${page.footer}
    </div>


</div>
</body>
</html>