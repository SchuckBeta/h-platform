<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>学院专家大赛评分</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>

    <style type="text/css">
        .row-info-fluid {
            height: 20px;
            margin-bottom: 15px;
        }

        .row-info-fluid span {
            display: inline-block;
            width: 120px;
            vertical-align: top;
        }

        .form-container {
            padding: 0 45px;
        }

        .row-fluid-wrap p {
            margin-left: 120px;
            margin-bottom: 0;
        }

        .row-fluid-wrap {
            height: auto;
            overflow: hidden;
        }

        .row-fluid-wrap span {
            float: left;
        }

        .form-container .edit-bar {
            margin-left: -30px;
            margin-right: -30px;
        }

        .icon-collaspe {
            position: absolute;
            right: 0px;
            top: 2px;
            padding: 4px 15px;
            text-decoration: none;
        }

        .table-caption {
            display: inline-block;
            border-radius: 4px 4px 0 0;
            background-color: #ebebeb;
            padding: .2em 1em;
        }

        .project-intro {
            margin-bottom: 20px;
            overflow: hidden;
        }

        .project-intro > span {
            display: block;
            float: left;
            margin-bottom: 0;
        }

        .project-intro > p {
            margin-left: 72px;
            margin-bottom: 0;
        }

        .m-table-b {
            background-color: #f4e6d4;
        }

        .table tbody tr.m-table-b > th, .table tbody tr th, .table tbody tr:hover > th {
            background-color: #f4e6d4;
        }

        .table tbody tr.m-table-b > th {
            white-space: nowrap;
        }

        .table tbody tr .m-table-d {
            white-space: nowrap;
        }

        .table thead tr th, .table tbody tr th, .table tbody tr td {
            text-align: center;
        }

        button {
            height: auto;
            width: auto;
        }

    </style>
    <script type="text/javascript">
        $(function () {
            $('a[data-toggle="collapse"]').click(function () {
                $(this).find('i').toggleClass('icon-double-angle-up icon-double-angle-down')
            });

            // 根据附件的名字自动修改其图标
            $('.file-item').each(function () {
                var el = $(this);
                var name = $.trim(el.children('a').text());
                var extname = name.split('.').pop().toLowerCase();
                switch (extname) {
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
                        // 我不太确定这个文件格式
//                    case "project":
                    case "jpg":
                    case "jpeg":
                    case "gif":
                    case "png":
                    case "bmp":
                        extname = "image";
                        break;
                    case "rar":
                    case "zip":
                    case "txt":
                    case "project":
                        // just break
                        break;
                    default:
                        extname = "unknow";
                }
                el.children('img').attr('src', "/img/filetype/" + extname + ".png");
            });
        });

    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>
                <c:if test="${state=='1'}">
                    待学院专家评分
                </c:if>
                <c:if test="${state=='2'}">
                    待学院秘书审核
                </c:if>
                <c:if test="${state=='3'}">
                    待学院专家评分
                </c:if>
                <c:if test="${state=='4'}">
                    待学院管理员审核
                </c:if>
                 <c:if test="${state=='5'}">
                     待学院管理员路演
                 </c:if>
                <c:if test="${state=='6'}">
                    待学院管理员评级
                </c:if>
                <c:if test="${state=='7'}">
                    学院管理员评级
                </c:if>
                <c:if test="${state=='8'}">
                    学院审核未通过
                </c:if>
                <c:if test="${state=='9'}">
                    学院审核未通过
                </c:if>
            </span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="inputForm" modelAttribute="gContest" action="${ctx}/gcontest/gContest/saveAuditWangping"
               method="post" class="form-horizontal form-container">
        <form:hidden path="id"/>
        <form:hidden path="procInsId"/>
        <form:hidden path="auditState"/>
        <div class="row-fluid row-info-fluid">
            <div class="span6">
                <span>大赛编号：</span>${gContest.competitionNumber}
            </div>
            <div class="span6">
                <span>填表日期：</span><fmt:formatDate value="${gContest.subTime}" pattern="yyyy-MM-dd"/>
            </div>
        </div>
        <div class="row-fluid row-info-fluid">
            <div class="span6">
                <span>申报人：</span>${sse.name}
            </div>
            <div class="span6">
                <span>学院：</span><c:if test="${sse.office!=null}">
                ${sse.office.name}
            </c:if>
            </div>
        </div>
        <div class="row-fluid row-info-fluid">
            <div class="span6">
                <span>学号(毕业年份)：</span>${sse.no}
            </div>
            <div class="span6">
                <span>专业年级：</span>${fns:getProfessional(sse.professional)}
            </div>
        </div>
        <div class="row-fluid row-info-fluid">
            <div class="span6">
                <span>联系电话：</span>${sse.mobile}
            </div>
            <div class="span6">
                <span>邮箱：</span>${sse.email}
            </div>
        </div>
        <div class="edit-bar edit-bar-sm clearfix">
            <div class="edit-bar-left">
                <span>项目基本信息</span>
                <i class="line"></i>
            </div>
        </div>
        <div class="row-fluid row-info-fluid row-fluid-wrap">
            <div class="span6">
                <span>参赛项目名称：</span>
                <p class="wrap">${gContest.pName}</p>
            </div>
            <div class="span6">
                <c:if test='${gContest.pId!=null && gContest.pId!=""}'>
                    <span>关联项目：</span>
                    <p class="wrap">${relationProject}</p>
                </c:if>
            </div>
        </div>
        <div class="edit-bar edit-bar-sm clearfix">
            <div class="edit-bar-left">
                <span>团队信息</span>
                <i class="line"></i>
                <a data-toggle="collapse" href="#teamInfo"><i class="icon-collaspe icon-double-angle-up"></i></a>
            </div>
        </div>
        <div id="teamInfo" class="panel-body collapse in">
            <div class="panel-inner">
                <p>项目团队：${team.name}</p>
                <div class="table-caption">学生团队</div>
                <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
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
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${turStudents!=null&&turStudents.size() > 0}">
                    <c:forEach items="${turStudents}" var="item" varStatus="status">
                    <tr>
                        <td>${status.index+1}</td>
                        <td><c:out value="${item.name}"/></td>
                        <td><c:out value="${item.no}"/></td>
                        <td><c:out value="${item.org_name}"/></td>
                        <td><c:out value="${item.professional}"/></td>
                        <td>${item.domain}</td>
                        <td><c:out value="${item.mobile}"/></td>
                        <td><c:out value="${item.instudy}"/></td>
                    <tr>
                        </c:forEach>
                        </c:if>
                    </tbody>
                </table>
                <div class="table-caption">指导老师</div>
                <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>姓名</th>
                        <th>单位（学院或企业、机构）</th>
                        <th>职称（职务）</th>
                        <th>技术领域</th>
                        <th>联系电话</th>
                        <th>E-mail</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${turTeachers!=null&&turTeachers.size() > 0}">
                        <c:forEach items="${turTeachers}" var="item" varStatus="status">
                            <tr>
                                <td>${status.index+1}</td>
                                <td><c:out value="${item.name}"/></td>
                                <td><c:out value="${item.org_name}"/></td>
                                <td><c:out value="${item.technical_title}"/></td>
                                <td>${item.domain}</td>
                                <td><c:out value="${item.mobile}"/></td>
                                <td><c:out value="${item.email}"/></td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="edit-bar edit-bar-sm clearfix">
            <div class="edit-bar-left">
                <span>项目介绍</span>
                <i class="line"></i>
                <a data-toggle="collapse" href="#projectIntroP"><i class="icon-collaspe icon-double-angle-up"></i></a>
            </div>
        </div>
        <div id="projectIntroP" class="panel-body collapse in">
            <div class="panel-inner">
                <div class="project-intro">
                    <span>项目简介：</span>
                    <p>${gContest.introduction}</p>
                </div>
            </div>
        </div>

        <c:if test="${sysAttachments!=null }">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>附&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;件</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="panel-body">
                <div class="panel-inner">
                    <c:forEach items="${sysAttachments}" var="sysAttachment">
                        <div class="doc file-item mgb15">
                            <img src="/img/filetype/unknow.png"/>
                            <a href="javascript:void(0)"
                               onclick="downfile('${sysAttachment.url}','${sysAttachment.name}');return false">
                                    ${sysAttachment.name}</a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty asdiList}">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>审核标准</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="panel-body">
                <div class="panel-inner">
                    <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange"
                           id="tableFormReview">
                        <thead>
                        <tr>
                            <th><i class="require-star">*</i>检查要点</th>
                            <th><i class="require-star">*</i>审核元素</th>
                            <th><i class="require-star">*</i>分值</th>
                            <c:if test="${not empty isScore }">
                                <th>评分</th>
                            </c:if>
                        </tr>
                        </thead>
                            <%--   添加评分标准--%>
                        <tbody>
                        <c:forEach items="${asdiList}" var="de">
                            <tr>
                                <td>${de.checkPoint }</td>
                                <td> ${de.checkElement }</td>
                                <td>${de.viewScore }</td>
                                <c:if test="${not empty isScore }">
                                    <td>${de.score }</td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <tfoot>
                        <c:if test="${not empty isScore }">
                            <tr>
                                <td>总分</td>
                                <td colspan="3" id="totalScoreV">${auditStandard.totalScore}</td>
                            </tr>
                        </c:if>
                        </tfoot>
                    </table>
                </div>
            </div>
        </c:if>

        <c:if test="${state=='1'}">
            <c:if test="${infos!=null}">
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>审核记录</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="panel-inner">
                        <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                            <tr>
                                <th colspan="4">学院审核记录</th>
                            </tr>
                            <tr class="m-table-b">
                                <th style="width: 8%">学院专家</th>
                                <th style="width: 7%">评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${infos}" var="info">
                                <c:if test="${info.createBy.id == loginUser.id}">
                                    <tr>
                                        <td>${fns:getUserById(info.createBy.id).name}</td>
                                        <td>${info.score}</td>
                                        <td colspan="2">${info.suggest}</td>
                                    </tr>
                                </c:if>
                            </c:forEach>

                        </table>
                    </div>
                </div>
            </c:if>
        </c:if>

        <c:if test="${state=='2'}">
            <c:if test="${infos!=null}">
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>审核记录</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="panel-inner">
                        <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                            <tr>
                                <th colspan="4">学院审核记录</th>
                            </tr>
                            <tr class="m-table-b">
                                <th style="width: 8%">学院专家</th>
                                <th style="width: 7%">评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${infos}" var="info">
                            <tr>
                                <td>${fns:getUserById(info.createBy.id).name}</td>
                                <td>${info.score}</td>
                                <td colspan="2">${info.suggest}</td>
                            <tr>
                                </c:forEach>

                        </table>
                    </div>
                </div>

            </c:if>
        </c:if>
        <c:if test="${state=='3'}">
            <c:if test="${infos!=null}">
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>审核记录</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="panel-inner">
                        <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                            <tr>
                                <th colspan="4">学院审核记录</th>
                            </tr>
                            <c:if test="${collegeExportinfos!=null && fn:length(collegeExportinfos) > 0}">
                                <tr class="m-table-b">
                                    <th style="width: 8%">学院专家</th>
                                    <th style="width: 7%">评分</th>
                                    <th colspan="2">建议及意见</th>
                                </tr>
                                <c:forEach items="${collegeExportinfos}" var="info">
                                    <tr>
                                        <td>${fns:getUserById(info.createBy.id).name}</td>
                                        <td>${info.score}</td>
                                        <td colspan="2">${info.suggest}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <tr>
                                <th>学院评分</th>
                                <th class="col-md-2 col-lg-2">排名</th>
                                <th colspan="2" class="col-md-6 col-lg-6">建议及意见</th>
                            </tr>
                            <c:forEach items="${infos}" var="info">
                                <tr>
                                    <td>${info.score}</td>
                                    <td>${info.sort}</td>
                                    <td colspan="2">${info.suggest}</td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td class="m-table-d">学院网评得分</td>
                                <td colspan="3">${gContest.gScore}</td>
                            </tr>
                            <c:if test="${schoolExportinfos!=null && fn:length(schoolExportinfos) > 0}">
                                <tr>
                                    <th colspan="4">学院审核记录</th>
                                </tr>
                                <tr class="m-table-b">
                                    <th>学院专家</th>
                                    <th>评分</th>
                                    <th colspan="2">建议及意见</th>
                                </tr>

                                <c:forEach items="${schoolExportinfos}" var="info">
                                    <c:if test="${info.createBy.id == loginUser.id}">
                                        <tr>
                                            <td>${fns:getUserById(info.createBy.id).name}</td>
                                            <td>${info.score}</td>
                                            <td>${info.suggest}</td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </table>

                    </div>
                </div>
            </c:if>
        </c:if>

        <c:if test="${state=='4'}">
            <c:if test="${infos!=null}">
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>审核记录</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="panel-inner">
                        <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                            <tr>
                                <th colspan="4">学院审核记录</th>
                            </tr>
                            <c:if test="${collegeExportinfos!=null && fn:length(collegeExportinfos) > 0}">
                                <tr class="m-table-b">
                                    <th style="width: 8%">学院专家</th>
                                    <th style="width: 7%">评分</th>
                                    <th colspan="2">建议及意见</th>
                                </tr>
                                <c:forEach items="${collegeExportinfos}" var="info">
                                    <tr>
                                        <td>${fns:getUserById(info.createBy.id).name}</td>
                                        <td>${info.score}</td>
                                        <td colspan="2">${info.suggest}</td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <tr>
                                <td class="m-table-d">学院网评得分</td>
                                <td colspan="3">${collegeInfo.score}</td>
                            </tr>
                            <tr>
                                <td class="m-table-d">学院排名</td>
                                <td colspan="3">${collegeInfo.sort}</td>
                            </tr>
                            <tr>
                                <th colspan="4">学院审核记录</th>
                            </tr>
                            <tr class="m-table-b">
                                <th>学院专家</th>
                                <th>评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${infos}" var="info">

                                <tr>
                                    <td>${fns:getUserById(info.createBy.id).name}</td>
                                    <td>${info.score}</td>
                                    <td>${info.suggest}</td>
                                </tr>

                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:if>
        </c:if>
        <c:if test="${state=='5'}">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>审核记录</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="panel-body">
                <div class="panel-inner">
                    <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <c:if test="${collegeExportinfos!=null && fn:length(collegeExportinfos) > 0}">
                            <tr class="m-table-b">
                                <th style="width: 8%">学院专家</th>
                                <th style="width: 7%">评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${collegeExportinfos}" var="info">
                                <tr>
                                    <td>${fns:getUserById(info.createBy.id).name}</td>
                                    <td>${info.score}</td>
                                    <td colspan="2">${info.suggest}</td>
                                </tr>
                            </c:forEach>
                        </c:if>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${collegeInfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">学院排名</td>
                            <td colspan="3">${collegeInfo.sort}</td>
                        </tr>

                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <tr class="m-table-b">
                            <th>学院专家</th>
                            <th>评分</th>
                            <th colspan="2">建议及意见</th>
                        </tr>
                        <c:forEach items="${schoolExportinfos}" var="info">
                            <tr>
                                <td>${fns:getUserById(info.createBy.id).name}</td>
                                <td>${info.score}</td>
                                <td>${info.suggest}</td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${gContest.gScore}</td>
                        </tr>
                    </table>
                </div>
            </div>
        </c:if>
        <c:if test="${state=='6'}">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>审核记录</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="panel-body">
                <div class="panel-inner">
                    <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <c:if test="${collegeExportinfos!=null && fn:length(collegeExportinfos) > 0}">
                            <tr class="m-table-b">
                                <th style="width: 8%">学院专家</th>
                                <th style="width: 7%">评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${collegeExportinfos}" var="info">
                                <tr>
                                    <td>${fns:getUserById(info.createBy.id).name}</td>
                                    <td>${info.score}</td>
                                    <td colspan="2">${info.suggest}</td>
                                </tr>
                            </c:forEach>
                        </c:if>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${collegeInfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">学院排名</td>
                            <td colspan="3">${collegeInfo.sort}</td>
                        </tr>
                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <tr class="m-table-b">
                            <th>学院专家</th>
                            <th>评分</th>
                            <th colspan="2">建议及意见</th>
                        </tr>
                        <c:forEach items="${schoolExportinfos}" var="info">
                            <tr>
                                <td>${fns:getUserById(info.createBy.id).name}</td>
                                <td>${info.score}</td>
                                <td colspan="2">${info.suggest}</td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${schoolinfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">路演得分</td>
                            <td colspan="3">${lyinfo.score}</td>
                        </tr>
                    </table>
                </div>
            </div>
        </c:if>
        <c:if test="${state=='7'}">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>审核记录</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="panel-body">
                <div class="panel-inner">
                    <table class="table table-bordered table-condensed table-theme-default table-hover table-nowrap table-center table-orange">
                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <c:if test="${collegeExportinfos!=null && fn:length(collegeExportinfos) > 0}">
                            <tr class="m-table-b">
                                <th style="width: 8%">学院专家</th>
                                <th style="width: 7%">评分</th>
                                <th colspan="2">建议及意见</th>
                            </tr>
                            <c:forEach items="${collegeExportinfos}" var="info">
                                <tr>
                                    <td>${fns:getUserById(info.createBy.id).name}</td>
                                    <td>${info.score}</td>
                                    <td colspan="2">${info.suggest}</td>
                                </tr>
                            </c:forEach>
                        </c:if>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${collegeInfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">学院排名</td>
                            <td colspan="3">${collegeInfo.sort}</td>
                        </tr>
                        <tr>
                            <th colspan="4">学院审核记录</th>
                        </tr>
                        <tr class="m-table-b">
                            <th>学院专家</th>
                            <th>评分</th>
                            <th colspan="2">建议及意见</th>
                        </tr>
                        <c:forEach items="${schoolExportinfos}" var="info">
                            <tr>
                                <td>${fns:getUserById(info.createBy.id).name}</td>
                                <td>${info.score}</td>
                                <td colspan="2">${info.suggest}</td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td class="m-table-d">学院网评得分</td>
                            <td colspan="3">${schoolinfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">路演得分</td>
                            <td colspan="3">${lyinfo.score}</td>
                        </tr>
                        <tr>
                            <td class="m-table-d">学院排名</td>
                            <td colspan="3">${pjinfos.sort}</td>
                        </tr>
                        <tr>
                            <th colspan="4">校赛结果</th>
                        </tr>
                        <tr class="m-table-b">
                            <th>校赛总得分</th>
                            <th>校赛排名</th>
                            <th>校赛结果</th>
                            <th>建议及意见</th>
                        </tr>
                        <tr>
                            <td>${pjinfos.score}</td>
                            <td>${pjinfos.sort}</td>
                            <td>
                                    ${fns:getDictLabel(pjinfos.grade, "competition_college_prise", "")}
                            </td>
                            <td>${pjinfos.suggest}</td>
                        </tr>
                    </table>
                </div>
            </div>
        </c:if>

        <div class="text-center">
            <a class="btn btn-default"   href="${ctx}/gcontest/gContest/collegeExportScore">返回</a>
        </div>
    </form:form>
</div>
</body>
</html>