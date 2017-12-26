<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>机构管理</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>机构用户</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <sys:message content="${message}"/>
    <div class="organization-content clearfix">
        <div class="z-tree-wrap">
            <div class="z-tree-head">
                <span>组织机构</span>
                <a class="btn-refresh pull-right" href="javascript:void (0)"><i class="icon-refresh"></i></a>
            </div>
            <div class="z-tree-body">
                <div id="ztree" class="ztree"></div>
            </div>
        </div>
        <div class="open-handler"></div>
        <div class="table-main-content">
            <iframe id="officeContent" src="${ctx}/sys/office/list?id=&parentIds=" width="100%" height="100%"
                    frameborder="0"></iframe>
        </div>
    </div>
</div>
<script type="text/javascript">

    $(function () {
        var $openHandler = $('.open-handler');
        var $btnRefresh = $('.btn-refresh');
        var $organizationContent = $('.organization-content');
        var setting = {
            data: {simpleData: {enable: true, idKey: "id", pIdKey: "pId", rootPId: '0'}},
            callback: {
                onClick: function (event, treeId, treeNode) {
                    var id = treeNode.pId == '0' ? '' : treeNode.pId;
                    $('#officeContent').attr("src", "${ctx}/sys/office/list?id=" + id + "&parentIds=" + treeNode.pIds);
                }
            }
        };
        $btnRefresh.on('click',function(e){
            e.stopPropagation();
            e.preventDefault();
            $.getJSON("${ctx}/sys/office/treeData", function (data) {
                $.fn.zTree.init($("#ztree"), setting, data).expandAll(true);
            });
        }).click();
        $openHandler.on('click',function(){
            $organizationContent.toggleClass('close-ztree')
        })
    });





</script>
</body>
</html>