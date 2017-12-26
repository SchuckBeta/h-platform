<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <title>${frontTitle}</title>
    <link rel="stylesheet" type="text/css" href="/css/indexOaNotifyListR.css">
    <style>
        .table>thead>tr>th{
            white-space: nowrap;
        }
        .table>tbody>tr:last-child>td{
            white-space: nowrap;
        }
        span.notify-tr-title{
            display: inline-block;
            max-width: 230px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .table th,.table td{
            font-size: 12px;
        }
        .unreadTr{
        	font-weight:bold;
        }
    </style>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        $(function () {
            var minHeight = $(window).height() - $('.footerBox').height() - $('.header').height();
            $('#content').css('min-height',minHeight)
            var message = "${message}";
            message && showModalMessage(0, "${message}")
        })
    </script>

</head>
<body>
<div class="container container-fluid-oe">
    <div class="notify-wrap">
        <h4 class="notify-title">我的通知</h4>
        <%--<sys:message content="${message}"/>--%>
        <div class="tab-container">
            <ul class="nav nav-tabs" role="tablist">
                <li class="active">
                    <a href="${ctxFront}/oa/oaNotify/indexMyNoticeList">接收消息</a>
                </li>
                <li>
                    <a href="${ctxFront}/oa/oaNotify/indexMySendNoticeList">发送消息</a>
                </li>
            </ul>
            <div class="tab-content">
                <div id="notifyTab1" role="tabpanel" class="tab-pane active">
                    <form:form id="searchForm" modelAttribute="oaNotify"
                               action="${ctxFront}/oa/oaNotify/indexMyNoticeList" method="get"
                               class="form-search">
                        <div class="form-inline text-right">
                            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                            <div class="form-group">
                                <form:input path="title" htmlEscape="false" maxlength="200" class="form-control"
                                            placeholder="关键字"/>
                                <button id="btnSubmit" class="btn btn-primary" type="submit">查询</button>
                                <input id="btnSubmit1" class="btn btn-primary" type="button"  value="批量删除" data-toggle="modal" data-target="#myModal"/>
                            </div>
                        </div>
                        <table id="contentTable" class="table table-hover table-bordered table-condensed table-notify">
                            <thead>
                            <tr>
                                <th><input type="checkbox" id="check_all" data-flag="false"></th>
                                <th>序号</th>
                                <th>标题</th>
                                <th style="display: none">消息内容</th>
                                <th>消息类型</th>
                                <th>消息状态</th>
                                <th>发送人</th>
                                <th>发布时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${page.list}" var="oaNotify" varStatus="status">
                                <tr key_5f820f675bfe4ffbb2b18f49ae12cbfe="${oaNotify.id}" <c:if test="${oaNotify.readFlag!=1 }">class="unreadTr"</c:if> >
                                    <td class="checkone">
                                            <input type="checkbox" value="${oaNotify.id}" name="boxTd" />
                                    </td>
                                    <td>${status.index+1 }</td>
                                    <td><a href="javascript:void(0);" class="select_info"><span class="notify-tr-title">${fns:abbr(oaNotify.title,50)}</span></a><input value="${oaNotify.id }" type="hidden"/></td>
                                    <td style="display: none" class="text-left"><span class="notify-content"> ${fn:substring(fns:replaceEscapeHtml(oaNotify.content),0,30)}</span></td>
                                    <td>${fns:getDictLabel(oaNotify.type, 'oa_notify_msg_type', '')}</td>
                                    <td>
                                    	<c:if test="${oaNotify.readFlag==1 }">已读</c:if>
                                    	<c:if test="${oaNotify.readFlag!=1 }">未读</c:if>
									</td>
                                    <td>${fns:getUserById(oaNotify.createBy.id).name}</td>
                                    <td><fmt:formatDate value="${oaNotify.effectiveDate}"
                                                        pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                    <td data-notify-id="${oaNotify.id }" data-notify-type="${oaNotify.type}">
                                        <input value="${oaNotify.id }" type="hidden"/>
                                        <a href="${ctxFront}/oa/oaNotify/deleteRec?id=${oaNotify.id}" class="btn btn-default btn-sm">删除</a>
                                        <c:if test="${oaNotify.operateFlag==0&&(oaNotify.type==5||oaNotify.type==6)}">
                                            <a href="#" class="btn btn-promise-reject btn-primary btn-sm" data-name="accept">同意</a>
                                            <a href="#" class="btn btn-promise-reject btn-primary btn-sm" data-name="refuse">拒绝</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </form:form>
                    <div class="pagination-container">
                        ${page.footer}
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="notifyModal" class="modal notify-modal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">通知消息</h4>
            </div>
            <div class="modal-body">
                <div class="title-time">
                    <h4 class="title"></h4>
                    <p class="time" data-jst-content="time"></p>
                </div>
                <div class="notify-p-wrap"><p class="notify-content"></p></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" style="width: auto; height: auto">关闭</button>
            </div>
        </div>
    </div>
</div>


<div id="myModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h3 class="modal-title" id="myModalLabel">批量删除</h3>
            </div>
            <div class="modal-body">
               <div id="selectArea">
                   确定删除勾选的消息么?
               </div>
                <div class="buffer_gif" style="text-align:center;padding:20px 0px;display:none;" id="bufferImg">
                    <img src="/img/jbox-loading1.gif" alt="缓冲图片">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-batch" aria-hidden="true" id="confirmBtn" onclick="doBatch('/f/oa/oaNotify/deleteSendBatch');" style="width: auto; height: auto;color: #fff">确定</button>
                <button class="btn btn-default" data-dismiss="modal" style="width: auto; height: auto;">取消</button>
            </div>
        </div>
    </div>
</div>
<script src="/js/oaNotify/notifyBatchDelete.js"></script>
<script type="text/javascript">
    (function (name, definition) {
        var hasDefine = typeof define === 'function';
        var hasExports = typeof module !== 'undefined' && module.exports;
        if (hasDefine) {
            definition()
        } else if (hasExports) {
            module.exports = definition()
        } else {
            window[name] = definition()
        }
    }('notify', function () {
        function Notify() {
            var provenceStatus = [
                {type: "11", name: "拒绝加入"},
                {type: "10", name: "同意加入"},
                {type: "9", name: "省市动态"},
                {type: "8", name: "双创通知"},
                {type: "7", name: "信息发布"},
                {type: "6", name: "邀请加入"},
                {type: "5", name: "申请加入"},
                {type: "4", name: "双创动态"},
                {type: "3", name: "活动通告"},
                {type: "2", name: "奖惩通告"},
                {type: "1", name: "团建通告"}
            ];
            var $ps = $('#ps');
            var $pageSize = $('#pageSize');
            var pageSize = $pageSize.val();
            var $tableContent = $('#contentTable');
            var $notifyModal = $('#notifyModal');
            var $title = $notifyModal.find('.title');
            var $time = $notifyModal.find('.time');
            var $content = $notifyModal.find('.notify-content');

            $ps.val(pageSize);

            $tableContent.on('click', '.select_info', function () {
            	var atag=$(this);
                var oaNotifyId = $(this).siblings('input').val();
                var xhr = $.post('${ctxFront}/oa/oaNotify/view', {
                    oaNotifyId: oaNotifyId
                });
                xhr.success(function (data) {
                    $title.text(data.title);
                    $time.text(data.publishDate);
                    if(data.type=='7'||data.type=='6'||data.type=='10'||data.type=='11'){
                    	$content.html('<a target="_blank" href="/f/team/findByTeamId?from=notify&id=' + data.sId + '&notifyId=' + oaNotifyId+'">'+data.content+'</a>');
                    }else{
                    	$content.text(data.content);
                    }
                    changeUnreadTrForNotify(oaNotifyId);
                });
                $notifyModal.modal({
                    show: true,
                    backdrop: false
                });
            });

            $tableContent.on('click', '.btn-promise-reject', function (e) {
                var name = $(this).data('name');
                var notifyId = $(this).parents('td').data('notifyId');
                var url = name == 'accept' ? '/f/team/acceptInviationByNotify' : '/f/team/refuseInviationByNotify';
                var xhr = $.post(url, {
                    send_id: notifyId
                });
                xhr.success(function (data) {
                    showModalMessage(data.ret, data.msg,{
                    	确定:function(){ $(this).dialog('close');window.location.reload();}
                    });
                });
                xhr.error(function (err) {
                    console.log(err)
                });
                return false;
            });

            function getProvenceState(type, status) {
                var curName;
                $.each(status, function (i, item) {
                    if (item.type == type) {
                        curName = item.name;
                        return false
                    }
                });
                return curName;
            }
            $notifyModal.find('.modal-content').draggable({ handle: ".title-time" });
        }

        return new Notify();
    }))

</script>

</body>
</html>