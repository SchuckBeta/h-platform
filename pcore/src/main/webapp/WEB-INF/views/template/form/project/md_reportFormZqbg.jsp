<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page errorPage="/WEB-INF/views/error/formMiss.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <link rel="stylesheet" type="text/css" href="/css/sco/frontCreditModule.css">
    <link rel="stylesheet" type="text/css" href="/css/project/form.css"/>
    <script type="text/javascript" src="/static/webuploader/webuploader.min.js"></script>
    <script type="text/javascript" src="/js/sco/upload-accessoryStep.js"></script>
    <title>${frontTitle}</title>
    <style>
        .contest-content .input-box > .form-control-static {
            padding: 7px 0;
        }

        textarea{
            resize: none;
        }

        button {
            width: auto;
            height: auto;
        }

        .table-pro-work > tbody > tr > td {
            line-height: 30px;
        }

        .table-theme-default > thead > tr {
            background-color: #f4e6d4;
        }

        .btn-default-oe {
            color: #333;
            background-color: #fff;
            border-color: #ccc
        }

        .btn-default-oe.focus, .btn-default-oe:focus {
            color: #333;
            background-color: #e6e6e6;
            border-color: #8c8c8c
        }

        .btn-default-oe:hover {
            color: #333;
            background-color: #e6e6e6;
            border-color: #adadad
        }

        .step-indicator {
            margin-bottom: 20px;
            line-height: 30px;
        }

        a.step {
            display: block;
            float: left;
            font-weight: bold;
            background: #f7f7f7;
            padding-right: 10px;
            height: 30px;
            width: 240px;
            text-align: center;
            line-height: 32px;
            margin-right: 33px;
            position: relative;
            text-decoration: none;
            color: #4c4b4a;
            cursor: default;
        }

        .step:before {
            content: "";
            display: block;
            width: 0;
            height: 0;
            position: absolute;
            top: 0;
            left: -30px;
            border: 15px solid transparent;
            border-color: #f7f7f7;
            border-left-color: transparent;
        }

        .step:after {
            content: "";
            display: block;
            width: 0;
            height: 0;
            position: absolute;
            top: 0;
            right: -30px;
            border: 15px solid transparent;
            border-left-color: #f7f7f7;
        }

        .step:first-of-type {
            border-radius: 2px 0 0 2px;
            padding-left: 15px;
        }

        .step:first-of-type:before {
            display: none;
        }

        .step:last-of-type {
            border-radius: 0 2px 2px 0;
            margin-right: 25px;
            padding-right: 15px;
        }

        .step:last-of-type:after {
            display: none;
        }

        a.step:hover {
            text-decoration: none;
        }

        .step.completed {
            background: #ffdacf;
            color: #de3b0a;
            cursor: pointer;
        }

        .step.completed:before {
            border-color: #ffdacf;
            border-left-color: transparent;
        }

        .step.completed:after {
            border-left-color: #ffdacf;
        }

        .step.completed:hover {
            background: #ffdacf;
            border-color: #ffdacf;
            color: #de3b0a;
            text-decoration: none;
        }

        .step.completed:hover:before {
            border-color: #ffdacf;
            border-left-color: transparent;
        }

        .step.completed:hover:after {
            border-left-color: #ffdacf;
        }

        .step-row {
            width: 565px;
            height: 30px;
            margin-left: auto;
            margin-right: auto;
            margin-bottom: 20px;
        }

        .result-textarea-box {
            margin-bottom: 20px;
        }

        #btnUpload.disabled{
            cursor: not-allowed;
            filter: alpha(opacity = 65);
            -webkit-box-shadow: none;
            box-shadow: none;
            opacity: .65;
        }
        #btnUpload.disabled .webuploader-pick{
            cursor: not-allowed;
        }
    </style>
</head>
<body>


<div class="container project-view-contanier">

    <h4 class="main-title">大学生创新创业训练计划中期报告</h4>
    <form:form id="competitionfm" modelAttribute="proModelMd" action="/f/proprojectmd/proModelMd/midSubmit" method="post" class="form-horizontal"
                   enctype="multipart/form-data">

        <div class="contest-content">
            <div class="tool-bar">
                <div class="inner">
                    <span>项目编号：${proModel.competitionNumber}</span>
                    <span style="margin-left: 15px;">填表日期:</span>
                    <i>${sysdate}</i>
                    <span style="margin-left: 15px">申请人:</span>
                    <i>${sse.name}</i>
                </div>
            </div>
            <h4 class="contest-title">项目中期报告</h4>
            <div class="contest-wrap">
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="control-label">项目负责人：</label>
                            <div class="input-box">
                                <p class="form-control-static">${sse.name}</p>
                                <!-- 下载模板必须属性 -->
                                <input type="hidden" id="proNtype" value="2"/>
                                <input type="hidden" id="proId" value="${proModelMd.id}"/>
                                <input type="hidden" id="proCate" value="${proModelMd.proModel.proCategory}"/>

                                <input type="hidden" name="id" id="proModelMdId" value="${proModelMd.id}"/>
                                <input type="hidden" name="proModel.declareId" id="declareId" value="${sse.id}"/>
                               <%--<input type="hidden" name="modelId" id="modelId" value="${proModelMd.modelId}"/>--%>
                                <input type="hidden" name="proModel.id" id="proModelId" value="${proModelMd.modelId}"/>
                                <input type="hidden" name="proModel.actYwId" id="proModel.actYwId" value="${actYw.id}"/>
                                <input type="hidden" name="proModel.type" id="proModel.type" value="${proProject.type}"/>
                                <input type="hidden" name="proModel.proType" id="proModel.proType" value="${proProject.proType}"/>
                                <input type="hidden" name="college" id="college" value="${sse.office.name}"/>
                                <input type="hidden" name="gnodeId" id="gnodeId" value="${gnodeId}"/>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="control-label">学院：</label>
                            <div class="input-box">
                                <p class="form-control-static">${sse.office.name}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="control-label">联系电话：</label>
                            <div class="input-box">
                                <p class="form-control-static">${sse.mobile}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>项目基本信息</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">项目名称：</label>
                            <div class="input-box">
                                <p class="form-control-static">${proModel.pName}</p>
                            </div>
                        </div>
                    </div>
                   <%-- <div class="col-md-6">
                        <div class="form-group">
                            <label class="control-label">项目层次：</label>
                            <div class="input-box">
                                    &lt;%&ndash;是否需要用treeselect&ndash;%&gt;
                                <p class="form-control-static">1层</p>
                            </div>
                        </div>
                    </div>--%>
                </div>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>团队信息</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="table-condition">
                    <div class="form-group">
                        <label class="control-label">团队信息：</label>
                        <div class="input-box" style="max-width: 394px;">
                            <p class="form-control-static">${team.name}</p>
                        </div>
                    </div>
                </div>
                <div class="table-title">
                    <span>学生团队</span>
                    <span id="ratio" style="background-color: #fff;color: #df4526;"></span>
                </div>
                <table class="table table-bordered table-pro-work table-condensed table-theme-default">
                    <thead>
                    <tr id="studentTr">
                        <th>序号</th>
                        <th>姓名</th>
                        <th>学号</th>
                        <th>手机号</th>
                        <th>所在学院</th>

                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${teamStu!=null&&teamStu.size() > 0}">
                        <c:forEach items="${teamStu}" var="item" varStatus="status">
                            <tr>
                                <td>${status.index+1}</td>
                                <td><c:out value="${item.name}"/></td>
                                <td><c:out value="${item.no}"/></td>
                                <td><c:out value="${item.mobile}"/></td>
                                <td><c:out value="${item.org_name}"/></td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
                <div class="table-title">
                    <span>指导教师</span>
                </div>
                <table class="table table-bordered table-condensed table-theme-default">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>姓名</th>
                        <th>工号</th>
                        <th>导师来源</th>
                        <th>职称（职务）</th>
                        <th>学历</th>
                        <th>联系电话</th>
                        <th width="175">E-mail</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${teamTea!=null&&teamTea.size() > 0}">
                        <c:forEach items="${teamTea}" var="item" varStatus="status">
                            <tr>
                                <td>${status.index+1}</td>
                                <td><c:out value="${item.name}"/></td>
                                <td><c:out value="${item.no}"/></td>
                                <td><c:out value="${item.teacherType}"/></td>
                                <td><c:out value="${fns:getDictLabel(item.technical_title,'postTitle_type','')}"/></td>
                                <td><c:out value="${fns:getDictLabel(item.education,'enducation_level','')}"/></td>
                                <td><c:out value="${item.mobile}"/></td>
                                <td><c:out value="${item.email}"/></td>
                            </tr>
                        </c:forEach>
                      </c:if>
                    </tbody>
                </table>
                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>已取得阶段性成果</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="result-textarea-box">
                    <textarea placeholder="不少于300字" name="stageResult" minLength="300" class="form-control" rows="5"></textarea>
                </div>
                <%--<div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>中期报告附件</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="row">
                	<flow:flowAjaxWord title="下载中期申报表" prefix="${wprefix}" types="${wtypes}" proId="${proId}" proCategory="${proModel.proCategory}" wordType="2"></flow:flowAjaxWord>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>上传申报资料：</label>
                            <div class="input-box">
                                <ul id="accessoryListPdf" class="file-list">
                                </ul>
                                <p class="form-control-static" style="color: #999">请上传文档或者压缩包</p>
                            </div>
                        </div>
                    </div>
                </div>--%>



                <div class="edit-bar edit-bar-sm clearfix">
                    <div class="edit-bar-left">
                        <span>项目申报资料</span>
                        <i class="line"></i>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label">下载项目申请表：</label>
                            <div class="input-box">
                                <ul id="downFileUl" class="file-list">
                                    <li class="file-item">
                                        <div class="file-info">
                                            <img src="/img/filetype/word.png">
                                            <a href="javascript:void(0);" data-url=""
                                               data-original="" data-size=""
                                               data-title=""
                                               data-type=""
                                               data-ftp-url=""></a>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="control-label"><i class="icon-require">*</i>上传申报资料：</label>
                            <div class="input-box">
                                <ul id="accessoryListPdf" class="file-list">
                                    <%--<c:forEach items="${sysAttachments}" var="sysAttachment">
                                    <li class="file-item">
                                        <div class="file-info">
                                            <img src="/img/filetype/word.png">
                                            <a href="javascript:void(0);" data-url="${sysAttachment.url}"
                                               data-original="" data-size="" data-id="${sysAttachment.id}"
                                               data-title="${sysAttachment.name}"
                                               data-type=""
                                               data-ftp-url="${sysAttachment.url}">${sysAttachment.name}</a>
                                            <c:if test="${isSubmit==null || isSubmit!=1}"><i class="icon icon-remove-sign"></i> </c:if>
                                        </div>
                                    </li>
                                    </c:forEach>--%>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="btngroup">
                    <div id="btnUpload" class="btn btn-primary-oe">上传中期检查资料</div>
                    <%--<button type="button" class="btn btn-primary-oe">保存</button>--%>
                    <button type="button" class="btn btn-primary-oe" onclick="midSubmit(this)">提交</button>
                    <button type="button" onclick="history.go(-1)" class="btn btn-default-oe">返回</button>
                </div>
            </div>
        </div>
        <div id="fujianpp" style="display:none">
           <%-- <c:forEach items="${sysAttachments}" var="sysAttachment">
                <input type='hidden' name='proModel.attachMentEntity.fielSize' value='${sysAttachment.size}'/>
                <input type='hidden' name='proModel.attachMentEntity.fielTitle' value='${sysAttachment.name}'/>
                <input type='hidden' name='proModel.attachMentEntity.fielType' value='${sysAttachment.suffix}'/>
                <input type='hidden' name='proModel.attachMentEntity.fielFtpUrl' value='${sysAttachment.url}'/>
            </c:forEach>--%>
        </div>
    </form:form>
</div>
<script>
    window.wtypes = '${wtypes}';
    window.wprefix = '${wprefix}';
</script>
<script src="/js/md/downloadTplMd.js" type="text/javascript" charset="utf-8"></script>
<script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
<script src="/other/jquery.form.min.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>
