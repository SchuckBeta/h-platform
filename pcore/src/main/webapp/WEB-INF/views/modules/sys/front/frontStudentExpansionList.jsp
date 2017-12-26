<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<html>
<head>
    <title>${frontTitle}</title>
    <meta name="decorator" content="site-decorator"/>
    <link rel="stylesheet" type="text/css" href="/css/talentsList.css">
    <style type="text/css">
        .option-btn .dropdown{
            display: inline-block;
        }
        .table-dropdown>thead>tr>th{
            background-color: transparent !important;
        }
        .table-dropdown{
            margin-bottom: 0;
        }
        .dropdown-menu{
            min-width: 240px;
        }
        .table-dropdown .btn-primary{
            color: #fff;
            background-color: #e9432d;
            border-color: #e53018;
        }

        .table-dropdown .btn-primary:focus,
        .table-dropdown .btn-primary.focus {
            color: #fff;
            background-color: #cd2b16;
            border-color: #71180c;
        }
        .table-dropdown .btn-primary:hover {
            color: #fff;
            background-color: #cd2b16;
            border-color: #ad2412;
        }

        .table-dropdown .btn-default{
            color: #333;
            background-color: #fff;
            border-color: #ccc;
        }

        .table-dropdown .btn-default {
            color: #333;
            background-color: #fff;
            border-color: #ccc
        }

        .table-dropdown .btn-default.focus,  .table-dropdown .btn-default:focus {
            color: #333;
            background-color: #e6e6e6;
            border-color: #8c8c8c
        }

        .table-dropdown .btn-default:hover {
            color: #333;
            background-color: #e6e6e6;
            border-color: #adadad
        }

        .table-dropdown .btn-default.active,  .table-dropdown .btn-default:active, .open>.dropdown-toggle.btn-default
        {
            color: #333;
            background-color: #e6e6e6;
            border-color: #adadad
        }
        .form-search input, select{
            height: auto;
        }
        .checkbox-inline label{
            font-weight: normal;
        }
        .table>thead>tr>th{
            white-space: nowrap;
        }
        .table>tbody>tr:last-child>td{
            white-space: nowrap;
        }
        .num_page{
            padding-right: 0;
        }
        span.team-number{
            max-width: 100px;
        }
        span.team-name{
            max-width: 170px;
        }
        span.team-number,span.team-name{
            display: inline-block;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            //学院专业联动
            $("#collegeId").change(function () {
                var parentId = $(this).val();
                //根据当前学院id 更改
                $("#professionalSelect").empty();
                $("#professionalSelect").append('<option value="">-请选择-</option>');
                $.ajax({
                    type: "post",
                    url: "/f/sys/office/findProfessionals",
                    data: {"parentId": parentId},
                    async: true,
                    success: function (data) {
                        $.each(data, function (i, val) {
                            if (val.id == "${studentExpansion.user.professional}") {
                                $("#professionalSelect").append('<option selected="selected" value="' + val.id + '">' + val.name + '</option>')
                            } else {
                                $("#professionalSelect").append('<option value="' + val.id + '">' + val.name + '</option>')
                            }

                        })
                    }
                });

            })
            $("#collegeId").trigger('change');
            $('.pagination_num').removeClass('row')
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

    </script>
</head>
<body>
<div class="container-fluid" style="margin-top: 60px;margin-bottom: 100px">
    <div class="student-title">学生库</div>
    <div class="guide-teacher-wrap">
        <form:form id="searchForm" modelAttribute="studentExpansion" action="/f/sys/frontStudentExpansion/"
                   method="post"
                   class="form-horizontal form-search">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="search-form-wrap">
                    <div class="form-inline">
                        <div class="form-group">
                            <label class="sr-only" for="keyWord">关键字</label>
                            <form:input path="keyWord" class="form-control" htmlEscape="false"
                                        placeholder="关键字"/>
                        </div>
                        <input id="btnSubmit" class="btn btn-search" type="submit" value="查询"/>
                    </div>
                </div>
                <div class="condition-main">
                    <div class="row">
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="user.office.id" class="control-label">所属学院</label>
                                <div class="input-box">
                                    <form:select path="user.office.id" class="form-control" id="collegeId">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="user.professional" class="control-label">所属专业</label>
                                <div class="input-box">
                                    <form:select path="user.professional" class="form-control" id="professionalSelect">
                                        <form:option value="" label="--请选择--"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="currState" class="control-label">学生现状</label>
                                <div class="input-box">
                                    <form:select path="currState" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('current_sate')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="user.degree" class="control-label">学位</label>
                                <div class="input-box">
                                    <form:select path="user.degree" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('degree_type')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="nowProject" class="control-label">在研项目</label>
                                <div class="input-box">
                                    <form:select path="nowProject" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="contestExperience" class="control-label">大赛经历</label>
                                <div class="input-box">
                                    <form:select path="contestExperience" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="projectExperience" class="control-label">项目经历</label>
                                <div class="input-box">
                                    <form:select path="projectExperience" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="award" class="control-label">获奖经历</label>
                                <div class="input-box">
                                    <form:select path="award" class="form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="curJoinStr" class="control-label">当前在研</label>
                                <div class="input-box">
                                    <form:checkboxes path="curJoinStr" items="${fns:getPublishDictList()}"
                                                     itemValue="value"
                                                     itemLabel="label" htmlEscape="false"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>

        <table id="contentTable" class="table table-hover table-bordered table-condensed ">
            <thead>
            <tr>
                <th>序号</th>
                <th>姓名</th>
                <th>性别</th>
                <th>当前在研</th>
                <th>现状</th>
                <th>学院</th>
                <th>专业班级</th>
                <th>学历</th>
                <th>学位</th>
                <th>荣获最高奖项</th>
                <th>技术领域</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="studentExpansion" varStatus="varStatus">
                <tr>
                    <td>${varStatus.count}</td>
                    <td>${studentExpansion.user.name}</td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.sex,"sex" , "")}
                    </td>
                    <td>${studentExpansion.curJoin}</td>
                    <td>${fns:getDictLabel(studentExpansion.currState, 'current_sate', '')}</td>
                    <td>${studentExpansion.user.office.name}</td>
                    <td>${fns:getOffice(studentExpansion.user.professional).name}
                            ${studentExpansion.tClass}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.education,"enducation_level" , "")}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.degree,"degree_type" , "")}
                    </td>
                    <td>${studentExpansion.topPrise}</td>
                    <td>${studentExpansion.user.domainlt}</td>
                    <td class="option-btn">
                            <%--<c:if test="${canInvite}">--%>
                            <%--<c:if test="${studentExpansion.msg!=1 }">--%>
                            <%--<a href="#"--%>
                            <%--onclick="return yaoq('${ctx}/sys/frontStudentExpansion/toInvite?user.id=${studentExpansion.user.id}')"--%>
                            <%--class="btn">邀请</a>--%>
                            <%--</c:if>--%>
                            <%--<c:if test="${studentExpansion.msg==1 }">--%>
                            <%--<a href="javascript:return false;" onclick="return false;" style="opacity: 0.2"--%>
                            <%--class="btn">邀请</a>--%>
                            <%--</c:if>--%>
                            <%--</c:if>--%>
                            <c:if test="${not canInvite}">
                            	<button type="button" disabled class="btn btn-dropdown-handler">
	                                      		邀请
	                                        <span class="caret"></span>
	                                    </button>
                            </c:if>
					<c:if test="${canInvite}">
						<c:if test="${not studentExpansion.canInvite}">
							<button type="button" disabled class="btn btn-dropdown-handler">
	                                      		邀请
	                                        <span class="caret"></span>
	                                    </button>
						</c:if>
						<c:if test="${studentExpansion.canInvite}">
							<c:if test="${not empty teams}">
	                                <div class="dropdown">
	                                    <button type="button" class="btn btn-dropdown-handler">
	                                      		邀请
	                                        <span class="caret"></span>
	                                    </button>
	                                    <div class="dropdown-menu pull-right" aria-labelledby="dLabel">
	                                        <table class="table table-condensed table-dropdown datatb" userid="${studentExpansion.user.id}">
	                                            <thead>
	                                            <tr>
	                                                <th></th><th style="display:none">团队编号</th><th>团队名称</th>
	                                            </tr>
	                                            </thead>
	                                            <tbody>
	                                            <c:forEach items="${teams}" var="team" varStatus="varStatus">
		                                            <tr>
		                                                <td>
		                                                <c:if test="${fn:contains(studentExpansion.canInviteTeamIds,team.id)}">
			                                                <c:if test="${fn:length(teams)==1}">
			                                                	<input value="${team.id}" type="checkbox" checked >
			                                                </c:if>
			                                                <c:if test="${fn:length(teams)!=1}">
			                                                	<input value="${team.id}" type="checkbox" >
			                                                </c:if>
		                                                </c:if>
		                                                <c:if test="${not fn:contains(studentExpansion.canInviteTeamIds,team.id)}">
		                                                	<input value="${team.id}" type="checkbox" disabled="disabled" >
		                                                </c:if>
		                                                </td>
		                                                <td style="display:none"><span class="team-number">${team.number}</span></td>
		                                                <td><span class="team-name">${team.name}</span></td>
		                                            </tr>
	                                            </c:forEach>
	                                            </tbody>
	                                            <tfoot>
	                                            <td colspan="3">
	                                                <button type="button" class="btn btn-primary">确定</button>
	                                                <button type="button" class="btn btn-default">取消</button>
	                                            </td>
	                                            </tfoot>
	                                        </table>
	                                    </div>
	                                </div>
							</c:if>
						</c:if>
					</c:if>
                        <a href="${ctx}/sys/frontStudentExpansion/form?id=${studentExpansion.id}"
                           class="btn">查看</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        ${page.footer}
    </div>
</div>
<script>
    $(function () {
        $('input[name="curJoinStr"]').parent().addClass('checkbox-inline')
        $(document).on('click', 'button.btn-dropdown-handler,.dropdown-menu .table .btn-default', function () {
            $(this).parents('.dropdown').toggleClass('open');
            var $tr = $(this).parents('tr');
            $tr.siblings().find('.dropdown').removeClass('open');
        });



        $(document).on('click', '.dropdown-menu .table .btn-primary', function () {
        	var sbtn=$(this);
        	var datatb= $(this).parents('.datatb');
        	var temarr=[];
    		$(datatb).find("input:checked").each(function(i,v){
    			temarr.push($(v).val());
    		});
    		if(temarr.length==0){
    			return false;
    		}
            $(this).parents('.dropdown').toggleClass('open');
            $.ajax({
                type: "post",
                url: "/f/sys/frontStudentExpansion/toInvite",
                data: {
                    'userIds': $(datatb).attr("userid"),
                    'teamId': temarr.join(","),
                    'userType': "1"
                },
                dataType: "json",
                success: function (data) {
                    if (data.ret=="1") {
                    	showModalMessage(1, data.msg, [{
                            text: '确定',
                            click: function () {
                                $(this).dialog('close');
                                restInviteBtn(sbtn,data.teamIds);
                            }
                        }]);
                    }else{
                    	showModalMessage(0, data.msg, [{
                            text: '确定',
                            click: function () {
                                $(this).dialog('close');
                            }
                        }]);
                    }
                },
                error: function () {
                      showModalMessage(0, "系统异常!");
                }
            });
        })
    });
    function restInviteBtn(sbtn,teamIds){
    	if(teamIds&&teamIds!=""){
	    	$.each(teamIds.split(","),function(i,v){
	    		sbtn.parents('.datatb').find("input[value='"+v+"']").attr("disabled","disabled").removeAttr("checked");
	    	});
	    	if(sbtn.parents('.datatb').find("input:not(:disabled)").length==0){
	    		sbtn.parents('.dropdown').find("button.btn-dropdown-handler").attr("disabled","disabled");
	    	}
    	}
    }
</script>
</body>
</html>