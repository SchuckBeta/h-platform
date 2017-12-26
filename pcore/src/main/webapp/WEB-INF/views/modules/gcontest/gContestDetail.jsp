<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <link rel="stylesheet" type="text/css" href="/css/competitionRegistration.css"/>
    <link rel="stylesheet" type="text/css" href="/css/gContestForm.css">
    <link rel="stylesheet" type="text/css" href="/other/jquery-ui-1.12.1/jquery-ui.css"/>
    <link rel="stylesheet" href="/static/bootstrap/2.3.1/awesome/font-awesome.min.css">
    <title>创新创业大赛查看</title>

</head>
<body>
<div class="container g-contest-detail">
    <h4 class="main-title">2017年互联网+大学生创新创业大赛</h4>
    <form:form class="form-horizontal" modelAttribute="gContest" enctype="multipart/form-data">
        <div class="contest-content">
            <div class="tool-bar">
                <a class="btn-print" onClick="window.print()" href="javascript:void(0);">打印申报表</a>
                <div class="inner">
                    <span>大赛编号：</span>
                    <i>${competitionNumber}</i>
                    <span>填表日期：<fmt:formatDate value="${gContest.createDate}" pattern="yyyy-MM-dd hh:mm:ss"/></span>
                </div>
            </div>
            <h4 class="contest-title">大赛报名</h4>
            <div class="contest-wrap">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">申报人：</label>
                            <div class="input-box">
                                <p class="form-control-static">${sse.name}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">学院：</label>
                            <div class="input-box">
                                <p  class="form-control-static">${sse.office.name}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">学号（毕业年份）：</label>
                            <div class="input-box input-inline">
                                <p  class="form-control-static">${sse.no}
                                    <c:if test="${studentExpansion.graduation!=null && studentExpansion.graduation!=''}">
                                    （<fmt:formatDate value='${studentExpansion.graduation}'
                                                             pattern='yyyy'/>）年
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">专业年级：</label>
                            <div class="input-box">
                                <p  class="form-control-static">${fns:getProfessional(sse.professional)}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">联系电话：</label>
                            <div class="input-box">
                                <p  class="form-control-static">${sse.mobile}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">E-mail：</label>
                            <div class="input-box">
                                <p  class="form-control-static">${sse.email}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>大赛基本信息</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>关联项目：</label>
                            <div class="input-box">
                                <%--<p  class="form-control-static">${relationProject}</p>--%>
                                <input type="text" name="relationProject" class="form-control"  value="${relationProject}" readonly />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>参赛项目名称：</label>
                            <div class="input-box">
                                <%--<p  class="form-control-static">${gContest.pName}</p>--%>
                                <input type="text" name="pName" class="form-control" value="${gContest.pName}"
                                       readonly="readonly"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>大赛类别：</label>
                            <div class="input-box">
                                <%--<p  class="form-control-static">${gContest.pName}</p>--%>
                                <form:select path="type" class="form-control" disabled="true">
                                    <form:option value="" label="请选择"/>
                                    <form:options items="${fns:getDictList('competition_net_type')}" itemLabel="label"
                                                  itemValue="value" htmlEscape="false"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>参赛组别：</label>
                            <div class="input-box">
                                <form:select path="level" class="form-control" disabled="true">
                                    <form:option value="" label="请选择"/>
                                    <form:options items="${fns:getDictList('gcontest_level')}" itemLabel="label"
                                                  itemValue="value" htmlEscape="false"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>融资情况：</label>
                            <div class="input-box">
                                <form:select path="financingStat" class="form-control" disabled="true">
                                    <form:option value="" label="请选择"/>
                                    <form:options items="${fns:getDictList('financing_stat')}" itemLabel="label"
                                                  itemValue="value" htmlEscape="false"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>团队信息</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="table-condition">
                    <div class="form-group">
                        <label class="control-label"><i class="icon-require">*</i>团队信息：</label>
                        <div class="input-box" style="max-width: 394px;">
                                <%--<form:select required="required" path="teamId" disabled="true"--%>
                                <%--class="form-control"><form:option value="" label="--请选择--"/>--%>
                                <%--<form:options items="${teams}" itemValue="id" itemLabel="name" htmlEscape="false"/>--%>
                                <%--</form:select>--%>
                                    <label class="control-label">${team.name}</label>
                        </div>
                    </div>
                </div>
                <div class="table-title">
                    <span>学生团队</span>
                </div>
                <table class="table table-bordered table-team studenttb">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>姓名</th>
                        <th>学号</th>
                        <th>学院</th>
                        <th>专业</th>
                        <th>技术领域</th>
                        <th>联系电话</th>
                        <th>在读学位</th>
                        <c:if test="${fns:checkMenuByNum(5)}">
                        <th class='credit-ratio'>学分配比</th>
                        </c:if>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${gContestVo.teamStudent!=null&&gContestVo.teamStudent.size() > 0}">
                    <c:forEach items="${gContestVo.teamStudent}" var="item" varStatus="status">
                    <tr>
                        <td>${status.index+1}</td>
                        <td><c:out value="${item.name}"/></td>
                        <td><c:out value="${item.no}"/></td>
                        <td><c:out value="${item.org_name}"/></td>
                        <td><c:out value="${item.professional}"/></td>
                        <td><c:out value="${item.domain}"/></td>
                        <td><c:out value="${item.mobile}"/></td>
                        <td><c:out value="${item.instudy}"/></td>
                        <c:if test="${fns:checkMenuByNum(5)}">
                        <td class="credit-ratio">
                                ${item.weightVal}
                        </td>
                        </c:if>
                    <tr>
                        </c:forEach>
                        </c:if>
                    </tbody>
                </table>
                <div class="table-title">
                    <span>指导教师</span>
                </div>
                <table class="table table-bordered table-team teachertb">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>姓名</th>
                        <th>工号</th>
                        <th>单位（学院或企业、机构）</th>
                        <th>职称（职务）</th>
                        <th>技术领域</th>
                        <th>联系电话</th>
                        <th>E-mail</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${gContestVo.teamTeacher!=null&&gContestVo.teamTeacher.size() > 0}">
                        <c:forEach items="${gContestVo.teamTeacher}" var="item" varStatus="status">
                            <tr>
                                <td>${status.index+1}</td>
                                <td><c:out value="${item.name}"/></td>
                                <td><c:out value="${item.no}"/></td>
                                <td><c:out value="${item.org_name}"/></td>
                                <td><c:out value="${item.technical_title}"/></td>
                                <td><c:out value="${item.domain}"/></td>
                                <td><c:out value="${item.mobile}"/></td>
                                <td><c:out value="${item.email}"/></td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>项目介绍</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label"><i class="icon-require">*</i>项目介绍：</label>
                    <div class="input-box">
                        <p style="height: auto">${gContest.introduction}</p>
                    </div>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>附     件</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="accessory-box">
                    <sys:frontFileUploadCommon fileitems="${sysAttachments}" filepath="promodel"
                                               btnid="btnUpload" readonly="true"></sys:frontFileUploadCommon>
                    <%--<ul id="file">--%>
                        <%--<li>--%>
                            <%--<c:forEach items="${sysAttachments}" var="sysAttachment">--%>
                                <%--<p style="margin-bottom: 0;height: 34px;line-height: 34px;"><a href="javascript:void(0)"--%>
                                      <%--onclick="downfile('${sysAttachment.url}','${sysAttachment.name}');return false">--%>
                                        <%--${sysAttachment.name}</a>--%>
                                <%--</p>--%>
                            <%--</c:forEach>--%>
                        <%--</li>--%>
                    <%--</ul>--%>
                </div>
                <div class="form-group">
                    <table class="table table-bordered table-contest-details">
                        <thead>
                        <tr>
                            <th>赛制</th>
                            <th>审核时间</th>
                            <th>评审人</th>
                            <th>建议及意见</th>
                            <th>评审内容</th>
                            <th>得分</th>
                            <th>当前排名</th>
                            <th>荣获奖项</th>
                            <th>荣获奖金</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- 	学院 -->
                        <c:choose>
                            <c:when test="${collegeinfos[0]!=null && collegeinfos[0] != ''}">
                                <tr>
                                    <td rowspan="3" class="ros_thr">校赛(学院及学院)</td>
                                    <td>
                                        <fmt:formatDate value="${collegeinfos[0].createDate}"
                                                        pattern="yyyy-MM-dd hh:mm:ss"/>
                                    </td>
                                    <td>学院专家及教学秘书</td>
                                    <td>${collegeinfos[0].suggest}</td>
                                    <td>${collegeinfos[0].auditName}</td>
                                    <td>${collegeinfos[0].score}</td>
                                    <td>${collegeinfos[0].sort}</td>
                                    <c:choose>
                                        <c:when test="${gca!=null}">
                                            <td rowspan="3">
                                                    ${fns:getDictLabel(gca.award, "competition_college_prise", "")}
                                            </td>
                                            <td rowspan="3">${gca.money}</td>
                                        </c:when>
                                        <c:otherwise>
                                            <td rowspan="3"></td>
                                            <td rowspan="3"></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td rowspan="3">校赛(学院及学院)</td>
                                    <td><span style="visibility: hidden">1</span></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td rowspan="3"></td>
                                    <td rowspan="3"></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        <!-- 	学院 -->
                        <c:choose>
                            <c:when test="${wpinfos[0]!= null && wpinfos[0]!= ''}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${wpinfos[0].createDate}" pattern="yyyy-MM-dd hh:mm:ss"/>
                                    </td>
                                    <td>校级专家及管理员</td>
                                    <td>${wpinfos[0].suggest}</td>
                                    <td>${wpinfos[0].auditName}</td>
                                    <td>${wpinfos[0].score}</td>
                                    <td>${wpinfos[0].sort}</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td><span style="visibility: hidden">1</span></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        <!-- 	end -->
                        <c:choose>
                            <c:when test="${lyinfos[0]!=null && lyinfos[0]!=''}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${lyinfos[0].createDate}" pattern="yyyy-MM-dd hh:mm:ss"/>
                                    </td>
                                    <td>校级管理员</td>
                                    <td>${lyinfos[0].suggest}</td>
                                    <td>${lyinfos[0].auditName}</td>
                                    <td>${lyinfos[0].score}</td>
                                    <td>${lyinfos[0].sort}</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td><span style="visibility: hidden">1</span></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
                <div class="btn-tool-bar">
                    <a class="btn btn-default" href="javascript:void(0)" onClick="history.back(-1)">返回</a>
                </div>
            </div>
        </div>
    </form:form>
</div>

<script src="/js/fileUpLoad.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>