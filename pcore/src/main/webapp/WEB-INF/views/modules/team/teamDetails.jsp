<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%-- <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <title>${frontTitle}</title>
    <link rel="stylesheet" type="text/css" href="/css/team.css">
    <script type="text/javascript">
        function backPage(){
            window.location.href="/f/team/indexMyTeamList";
        }
        function acceptInviation(send_id){
        	$(".teamnotifybtn").remove();
	        $.ajax({
	            type: "post",
	            url: "/f/team/acceptInviationByNotify",
	            data: {
	                'send_id': send_id
	            },
	            dataType: "json",
	            success: function (data) {
	            	showModalMessage(data.ret,data.msg);
	            },
	            error: function () {
	                  showModalMessage(0, "系统异常!");
	            }
	        });
        }
        function refuseInviation(send_id){
        	$(".teamnotifybtn").remove();
	        $.ajax({
	            type: "post",
	            url: "/f/team/refuseInviationByNotify",
	            data: {
	                'send_id': send_id
	            },
	            dataType: "json",
	            success: function (data) {
	            	showModalMessage(data.ret,data.msg);
	            },
	            error: function () {
	                  showModalMessage(0, "系统异常!");
	            }
	        });
        }
        function applyJoin(){
        	$(".teamnotifybtn").remove();
	        $.ajax({
	            type: "post",
	            url: "/f/team/applyJoin",
	            data: {
	                'teamId': $("#teamId").val()
	            },
	            success: function (data) {
	            	if(data&&data.indexOf("成功")!=-1){
	            		showModalMessage(1,data);
	            	}else{
	            		showModalMessage(0,data);
	            	}
	            },
	            error: function () {
	                  showModalMessage(0, "系统异常!");
	            }
	        });
        }
    </script>
</head>
<body>
<div class="container">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>基本信息</span>
            <i class="line"></i>
        </div>
    </div>
    <div class="team-info" role="main">
        <h4 class="title team-title">查看团队信息</h4>
        <input type="hidden" id="teamId" value="${teamDetails.id }">
        <div class="wrap">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队信息</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-5">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队名称：</label>
                        <div class="ti-box">
                            <p>
                            <c:if test="${notSponsor&&teamDetails.state==3}">信息审核中</c:if>
                    		<c:if test="${notSponsor&&teamDetails.state==4}">信息审核未通过</c:if>
                    		<c:if test="${not notSponsor||(teamDetails.state!=3&&teamDetails.state!=4)}">${teamDetails.name }</c:if>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>所属学院：</label>
                        <div class="ti-box">
                            <p>${teamDetails.localCollege }</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队负责人：</label>
                        <div class="ti-box">
                            <p>${teamDetails.sponsor }</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队介绍：</label>
                        <div class="ti-box">
                            <p>
                            <c:if test="${notSponsor&&teamDetails.state==3}">信息审核中</c:if>
                    		<c:if test="${notSponsor&&teamDetails.state==4}">信息审核未通过</c:if>
                    		<c:if test="${not notSponsor||(teamDetails.state!=3&&teamDetails.state!=4)}">${teamDetails.summary }</c:if>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>项目介绍：</label>
                        <div class="ti-box">
                            <p>
                            <c:if test="${notSponsor&&teamDetails.state==3}">信息审核中</c:if>
                    		<c:if test="${notSponsor&&teamDetails.state==4}">信息审核未通过</c:if>
                    		<c:if test="${not notSponsor||(teamDetails.state!=3&&teamDetails.state!=4)}">${teamDetails.projectIntroduction }</c:if>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队成员要求</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>项目组人数：</label>
                        <div class="ti-box">
                            <p>${teamDetails.memberNum }人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>校内导师：</label>
                        <div class="ti-box">
                            <p>${teamDetails.schoolTeacherNum }人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label">企业导师：</label>
                        <div class="ti-box">
                            <p>${teamDetails.enterpriseTeacherNum }人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>组员要求：</label>
                        <div class="ti-box">
                            <p>
                            <c:if test="${notSponsor&&teamDetails.state==3}">信息审核中</c:if>
                    		<c:if test="${notSponsor&&teamDetails.state==4}">信息审核未通过</c:if>
                    		<c:if test="${not notSponsor||(teamDetails.state!=3&&teamDetails.state!=4)}">${teamDetails.membership }</c:if>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队组建情况</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="team-table-title">
				<span class="team-status">
					<c:if test="${teamDetails.memberNum-teamInfo.size()==0 }">已组建完成</c:if>
					<c:if test="${teamDetails.memberNum-teamInfo.size()!=0 }">(已加入${teamInfo.size()}人)</c:if>
				</span>
                <span class="team-name">学生团队</span>
            </div>
            <table class="table table-bordered table-team-type">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>姓名</th>
                    <th>工号</th>
                    <th>学院</th>
                    <th>专业</th>
                    <th>手机号</th>
                    <th>现状</th>
                    <th>当前在研</th>
                    <th>技术领域</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${teamInfo }" var="info" varStatus="status">
                    <tr>
                        <td>${status.index+1 }</td>
                        <td>${info.uName }</td>
                        <td>${info.no }</td>
                        <td>${info.officeId }</td>
                        <td>${fns:getOffice(info.professional).name}</td>
                        <td>
                        <c:if test="${not hasJoin&&info.userId!=teamDetails.leaderid}">
                        	${fns:hiddenMobile(info.mobile)}
                        </c:if>
                        <c:if test="${hasJoin||info.userId==teamDetails.leaderid}">
                        	${info.mobile }
                        </c:if>
                        </td>
                        <td>${fns:getDictLabel(info.currState, 'current_sate', '')}</td>
                        <td>${info.curJoin }</td>
                        <td>${info.domainlt }</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="team-table-title">
				<span class="team-status">
					<c:if test="${teamDetails.schoolTeacherNum+teamDetails.enterpriseTeacherNum-teamTeacherInfo.size()==0}">已组建完成</c:if>
	  								<c:if test="${teamDetails.schoolTeacherNum+teamDetails.enterpriseTeacherNum-teamTeacherInfo.size()!=0}">(已加入${teamTeacherInfo.size()}人)</c:if>
				</span>
                <span class="team-name">指导教师</span>
            </div>
            <table class="table table-bordered table-team-type">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>姓名</th>
                    <th>工号</th>
                    <th>单位(学院或企业、机构)</th>
                    <th>导师来源</th>
                    <th>当前指导</th>
                    <th>技术领域</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${teamTeacherInfo }" var="tInfo" varStatus="sta">
                    <tr>
                        <td>${sta.index+1 }</td>
                        <td>${tInfo.uName }</td>
                        <td>${tInfo.no }</td>
                        <td>${tInfo.officeId }</td>
                        <td>${fns:getDictLabel(tInfo.teacherType, 'master_type', '')}</td>
                        <td>${tInfo.curJoin }</td>
                        <td>${tInfo.domainlt }</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="back-box">
            <button type="button" class="btn btn-back-w" onclick="backPage()">返回</button>
            <c:if test="${from=='notify'&&notifyType=='6'}">
            	<button type="button" class="btn btn-back-w teamnotifybtn" onclick="acceptInviation('${notifyId}')">接受</button>
            	<button type="button" class="btn btn-back-w teamnotifybtn" onclick="refuseInviation('${notifyId}')">拒绝</button>
            </c:if>
            <c:if test="${from=='notify'&&notifyType=='7'}">
            	<button type="button" class="btn btn-back-w teamnotifybtn" style="width:80px" onclick="applyJoin()">申请加入</button>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>