<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理门户-审核团队信息</title>
    <link rel="stylesheet" type="text/css" href="/common/common-css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/css/team.css">
    <script src="${ctxStatic}/jquery/jquery-1.8.3.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/common/initiate.js" type="text/javascript"></script>
    <script type="text/javascript">
    function checkTeam(vl){
    	$.ajax({
			type:'get',
			url:'/a/team/checkTeam?teamId='+$("#teamId").val()+"&res="+vl,
			success:function(data){
				if(data.ret==1){
					alertx(data.msg,function(){
						window.location.href = document.referrer;
					});
				}else{
					alertx(data.msg);
				}
			}
		});
    }
    </script>
</head>
<body>
<div class="container-fluid">
	<input type="hidden" id="teamId" value="${teamDetails.teamId }">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>团队建设</span>
            <i class="line"></i>
        </div>
    </div>
    <div class="team-info" role="main">
        <h4 class="title team-title">团队信息</h4>
        <div class="wrap">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队信息</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队名称：</label>
                        <div class="ti-box">
                            <p>${teamDetails.name }</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队有效期：</label>
                        <div class="ti-box">
                            <p><c:if test="${team.validDateStart!=null && team.validDateEnd!=null }">
                                <fmt:formatDate value="${teamDetails.validDateStart }" pattern="yyyy-MM-dd"/>&nbsp;至&nbsp;
                                <fmt:formatDate value="${teamDetails.validDateEnd }" pattern="yyyy-MM-dd"/>
                            </c:if></p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-6">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>所属学院：</label>
                        <div class="ti-box">
                            <p>${teamDetails.localCollege }</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6">
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
                            <p>${teamDetails.summary }</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>项目介绍：</label>
                        <div class="ti-box">
                            <p>${teamDetails.projectIntroduction }</p>
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
                        <label class="ti-label"><i class="icon-require">*</i>企业导师：</label>
                        <div class="ti-box">
                            <p>${teamDetails.enterpriseTeacherNum }人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>组员要求：</label>
                        <div class="ti-box">
                            <p>${teamDetails.membership }</p>
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
                    <th>学院</th>
                    <th>专业</th>
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
                        <td>${info.officeId }</td>
                        <td>${fns:getOffice(info.professional).name}</td>
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
                    <th>单位(学院或企业、机构)</th>
                    <th>导师来源</th>
                    <th>技术领域</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${teamTeacherInfo }" var="tInfo" varStatus="sta">
                    <tr>
                        <td>${sta.index+1 }</td>
                        <td>${tInfo.uName }</td>
                        <td>${tInfo.officeId }</td>
                        <td>${fns:getDictLabel(tInfo.teacherType, 'master_type', '')}</td>
                        <td>${tInfo.domainlt }</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${teamDetails.id!=null}">
              <div class="user-details-info">
              <div class="user-detail-group">
                  <div class="ud-inner">
                      <p class="ud-title">项目经历</p>
                      <div class="info-cards">
                          <c:forEach items="${projectExpVo}" var="projectExp">
                          <div class="info-card">
                              <p class="info-card-title">国创项目</p>
                              <div class="row">
                                  <div class="col-sm-12">
                                      <div class="ic-box">
                                          <label class="user-label-control">项目周期：</label>
                                          <div class="user-val">
                                              <fmt:formatDate value="${projectExp.startDate }" pattern="yyyy-MM-dd" />
                                              -
                                              <fmt:formatDate value="${projectExp.endDate }" pattern="yyyy-MM-dd" />
                                          </div>
                                      </div>
                                  </div>
                                  <div class="col-sm-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">项目名称：</label>
                                          <div class="user-val"> ${projectExp.name }</div>
                                      </div>
                                  </div>
                                  <div class="col-md-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">担任角色：</label>
                                          <div class="user-val">
                                                  ${projectExp.roleName }
                                          </div>
                                      </div>
                                  </div>
                                  <div class="col-sm-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">项目级别：</label>
                                          <div class="user-val"> ${projectExp.level }</div>
                                      </div>
                                  </div>
                                  <div class="col-md-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">项目结果：</label>
                                          <div class="user-val">
                                                  ${projectExp.result }
                                          </div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          </c:forEach>
                      </div>
                      <p class="ud-title">大赛经历</p>
                      <div class="info-cards">
                          <c:forEach items="${gContest}" var="gContest">
                          <div class="info-card info-match">
                              <p class="info-card-title">参赛项目</p>
                              <div class="row">
                                  <div class="col-sm-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">参赛类别：</label>
                                          <div class="user-val">
                                              <c:if test="${gContest.type ==1}">
                                                  互联+大赛
                                             </c:if>
                                          </div>
                                      </div>
                                  </div>
                                  <div class="col-sm-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">参赛时间：</label>
                                          <div class="user-val"><fmt:formatDate value="${gContest.createDate }"  pattern="yyyy-MM-dd" /></div>
                                      </div>
                                  </div>
                                  <div class="col-md-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">参赛项目名称：</label>
                                          <div class="user-val">
                                                  ${gContest.pName }
                                          </div>
                                      </div>
                                  </div>
                                  <div class="col-sm-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">担任角色：</label>
                                          <div class="user-val">
                                              <c:if test="${gContest.sponsor==studentExpansion.user.id }">
                                              项目负责人
                                              </c:if>
                                              <c:if test="${gContest.sponsor!=studentExpansion.user.id }">
                                                  成员
                                              </c:if>
                                          </div>
                                      </div>
                                  </div>
                                  <div class="col-md-6">
                                      <div class="ic-box">
                                          <label class="user-label-control">获奖情况：</label>
                                          <div class="user-val">
                                              <c:if test="${gContest.award ==2}">三等奖</c:if>
                                              <c:if test="${gContest.award ==3}">二等奖</c:if>
                                              <c:if test="${gContest.award ==4}">一等奖</c:if>
                                          </div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          </c:forEach>
                      </div>
                  </div>
              </div>
          </div>
          </c:if>

        <div class="back-box">
            <button type="button" class="btn btn-back-w" onclick="checkTeam(1)">通过</button>
            <button type="button" class="btn btn-back-w" onclick="checkTeam(0)">不通过</button>
            <button type="button" class="btn btn-back-w" onclick="history.go(-1)">返回</button>
        </div>
    </div>
</div>
</body>
</html>