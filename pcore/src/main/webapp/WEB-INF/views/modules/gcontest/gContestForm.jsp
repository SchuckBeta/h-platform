<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${frontTitle}</title>
    <meta name="decorator" content="site-decorator"/>
    <%--<link rel="stylesheet" type="text/css" href="/css/competitionRegistration.css"/>--%>
    <link rel="stylesheet" type="text/css" href="/css/gContestForm.css?v=1">
    <%--<link rel="stylesheet" type="text/css" href="/other/jquery-ui-1.12.1/jquery-ui.css"/>--%>
    <%--<link rel="stylesheet" href="/static/bootstrap/2.3.1/awesome/font-awesome.min.css">--%>
</head>
<body>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>
<div class="container g-contest-detail container-fluid-oe">
    <input type="hidden" id="pageType" value="edit">
    <h2 class="main-title" style="margin-top: 0; margin-bottom: 30px;">第三届"互联网+"大学生创新创业大赛报名</h2>
    <form:form id="competitionfm" class="form-horizontal" modelAttribute="gContest" action="/f/gcontest/gContest/save"
               method="post" enctype="multipart/form-data">
        <form:hidden path="id" value="${gContest.id}"/>
        <%--<form:hidden path="announceId" value="${gContestAnnounce.id}"/>--%>
        <form:hidden path="actywId" value="${gContest.actywId}"/>
        <div class="contest-content">
            <div class="tool-bar">
                <a class="btn-print" onClick="window.print()" href="javascript:void(0);">打印申报表</a>
                <div class="inner">
                    <c:if test="${id!=null}">
                        <span>大赛编号：</span>
                        <i>${competitionNumber}</i>
                    </c:if>
                    <input type="hidden" name="competitionNumber" value="${competitionNumber}"/>
                    <span>填表日期:</span>
                    <i>${sysdate}</i>
                </div>
            </div>
            <h4 class="contest-title">大赛报名</h4>
            <div class="contest-wrap">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="shenbaoren" class="control-label"><i class="icon-require">*</i>申报人：</label>
                            <div class="input-box">
                                <input type="text" id="shenbaoren" class="form-control" name="shenbaoren" readonly
                                       value="${sse.name}">
                                <input type="hidden" name="declareId" id="declareId" value="${gContest.declareId}"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="company" class="control-label"><i class="icon-require">*</i>学院：</label>
                            <div class="input-box">
                                <input type="text" id="company" class="form-control" readonly
                                       value="${sse.office.name}"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>学号（毕业年份）：</label>
                            <div class="input-box input-inline">
                                <input type="text" id="zhuanye" class="form-control" readonly value="${sse.no}"/>
                                <input type="text" id="nianfen" class="form-control" readonly
                                       value="<fmt:formatDate value='${studentExpansion.graduation}' pattern='yyyy'/>"/>
                                <span>年</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="zynj" class="control-label">专业年级：</label>
                            <div class="input-box">
                                <input type="text" id="zynj" name="zynj" class="form-control" readonly
                                       value="${fns:getProfessional(sse.professional)}"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="mobile" class="control-label"><i class="icon-require">*</i>联系电话：</label>
                            <div class="input-box">
                                <input type="text" id="mobile" class="form-control" readonly value="${sse.mobile}"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="email" class="control-label"><i class="icon-require">*</i>E-mail：</label>
                            <div class="input-box">
                                <input type="text" id="email" class="form-control" readonly value="${sse.email}"/>
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
                                <label class="radio-inline">
                                    <input id="one" name="guochuang" type="radio" value="1" checked/> 无关联
                                </label>
                                <label class="radio-inline">
                                    <input id="two" name="guochuang" type="radio" value="2"/> 国创项目
                                </label>
                                <form:select path="pId" class="form-control select-inline" cssStyle="visibility: hidden">
                                    <form:option value="" label="--请选择--"/>
                                    <form:options items="${projects}" itemValue="id" itemLabel="name"
                                                  htmlEscape="false"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="projectName" class="control-label"><i class="icon-require">*</i>参赛项目名称：</label>
                            <div class="input-box">
                                <input type="text" name="pName" id="projectName" class="form-control" maxlength='30'
                                       value="${gContest.pName}"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="type" class="control-label"><i class="icon-require">*</i>大赛类别：</label>
                            <div class="input-box">
                                <form:select id="type" path="type" required="required" class="form-control">
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
                            <label for="type" class="control-label"><i class="icon-require">*</i>参赛组别：</label>
                            <div class="input-box">
                                <form:select path="level" required="required" class="form-control">
                                    <form:option value="" label="请选择"/>
                                    <form:options items="${fns:getDictList('gcontest_level')}" itemLabel="label"
                                                  itemValue="value" htmlEscape="false"/>
                                </form:select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="financingStat" class="control-label"><i class="icon-require">*</i>融资情况：</label>
                            <div class="input-box">
                                <form:select id="financingStat" path="financingStat" required="required"
                                             class="form-control">
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
                            <form:select required="required" onchange="DSSB.findTeamPerson();" path="teamId"
                                         class="form-control"><form:option value="" label="--请选择--"/>
                                <form:options items="${teams}" itemValue="id" itemLabel="name" htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="table-title">
                    <span>学生团队</span>
                    <span id="ratio" style="background-color: #fff;color: #df4526;"></span>
                </div>
                <table class="table table-bordered table-team studenttb">
                    <thead>
                    <tr id="studentTr">
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
                        <td>${status.index+1}
                        <input type="hidden" name="teamUserHistoryList[${status.index}].userId" value="${item.userId}">
                        </td>
                        <td><c:out value="${item.name}"/></td>
                        <td><c:out value="${item.no}"/></td>
                        <td><c:out value="${item.org_name}"/></td>
                        <td><c:out value="${item.professional}"/></td>
                        <td><c:out value="${item.domain}"/></td>
                        <td><c:out value="${item.mobile}"/></td>
                        <td><c:out value="${item.instudy}"/></td>
                        <c:if test="${fns:checkMenuByNum(5)}">
                            <td class="credit-ratio">
                                <input class="form-control input-sm " name="TeamUserHistoryList[${status.index}].weightVal" value="${item.weightVal}">
                            </td>
                        </c:if>

                    </tr>
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
                    <label for="introduction" class="control-label"><i class="icon-require">*</i>项目介绍：</label>
                    <div class="input-box">
                        <textarea id="introduction" class="introarea form-control" rows="5" name="introduction"
                                  maxlength='1024'>${gContest.introduction}</textarea>
                    </div>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>附     件</span>
                        <i class="line"></i>
                    </div>
                </div>

                <sys:frontFileUploadCommon fileitems="${sysAttachments}" filepath="gcontest" btnid="btnUpload"></sys:frontFileUploadCommon>
                 <%--   <a class="upload" id="upload">上传附件</a>
                    <input type="file" style="display: none" id="fileToUpload" name="fileName"/>
                    <ul id="file">
                        <c:forEach items="${sysAttachments}" var="item" varStatus="status">
                            <li>
                                <p>
                                    <img src="/img/filetype/${item.imgType}.png">
                                    <a href="javascript:void(0)"
                                       onclick="downfile('${item.arrUrl}','${item.arrName}');return false">
                                            ${item.arrName}</a>
							<span class="del" onclick="delUrlFile(this,'${item.id}','${item.arrUrl}','delFile');">
							<i class='icon-remove-sign'></i></span>

                                </p>
                            </li>
                        </c:forEach>
                    </ul>--%>

                <div class="btn-tool-bar">
                    <%--<a href="javascript:void(0)" onClick="history.back(-1)">返回</a>--%>
                    <%--<a href="javascript:void(0)" onclick="DSSB.save();">保存</a>--%>
                    <%--<a href="javascript:void(0)" onclick="">提交并保存</a>--%>
                    <button type="button" class="btn btn-default" onclick="history.back(-1);">返回</button>
                    <button type="button" class="btn btn-primary-oe" onclick="DSSB.save();" style="width: auto;height: auto;color: #fff">保存</button>
                    <button type="button" class="btn btn-primary-oe" id="btnSubmit" onclick="DSSB.submit();" style="width: auto;height: auto;color: #fff">提交并保存</button>
                    <div class="btn-upload-file" id="btnUpload" style="vertical-align: top;margin-left: 0">上传附件</div>
                </div>
            </div>
        </div>
    </form:form>
</div>


<script type="text/javascript">
    $(function () {
        var initDSSB = function () {
            DSSB.tabletrminus();
            DSSB.tabletrplus();
        }();
    })
</script>


<script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
<script src="/other/jquery.form.min.js" type="text/javascript" charset="utf-8"></script>
<script src="/common/common-js/jquery.validate.min.js" type="text/javascript" charset="utf-8"></script>
<script src="/common/common-js/messages_zh.min.js" type="text/javascript" charset="utf-8"></script>
<script src="/js/DSSBvalidate.js" type="text/javascript" charset="utf-8"></script>
<script src="/other/datepicker/My97DatePicker/WdatePicker.js" type="text/javascript" charset="utf-8"></script>
<script src="/js/common.js" type="text/javascript"></script>
<script src="/js/DSSB.js" type="text/javascript" charset="utf-8"></script>
<script src="/common/common-js/ajaxfileupload.js"></script>
<script src="/js/fileUpLoad.js"></script>
</body>
</html>