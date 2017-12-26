<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>流程管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/actyw/nodeExhibition.css">
    <script type="text/javascript" src="/js/actyw/vue.js"></script>
    <script type="text/javascript" src="/js/actyw/components.js"></script>
    <style>
        .flow-work .title{
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    </style>
</head>
<body>
<div class="mybreadcrumbs">
    <span>流程预览</span>
</div>
<div class="content_panel">
    <ul class="nav nav-tabs">
        <%-- <li><a href="${ctx}/actyw/actYwGnode/">自定义流程列表</a></li>
        <li><a href="${ctx}/actyw/actYwGroup/">项目流程列表</a></li> --%>
        <li class="active"><a href="${ctx}/actyw/actYwGnode/${actYwGnode.group.id}/view">项目流程视图</a></li>
        <%--<shiro:hasPermission name="actyw:actYwGnode:edit">--%>
            <%--<li><a href="${ctx}/actyw/actYwGnode/form">项目流程(${actYwGnode.group.name})配置</a></li>--%>
        <%--</shiro:hasPermission>--%>
    </ul>
    <sys:message content="${message}"/>
    <br>
    <div id="exhibition" class="container-fluid">
        <div class="flow-title">
            <p>${group.name}预览</p>
            <div class="btn-group">
                <%--<button type="button" class="btn btn-default btn-sm" @click="createNode">创建工作节点</button>--%>
                <button type="button" style="display: none" class="btn btn-default btn-sm" @click="edit">编辑</button>
                <button type="button" style="display: none" class="btn btn-default btn-sm" @click="save"
                        :disabled="editable">保存
                </button>
            </div>
        </div>
        <div id="flowWork" class="flow-work" v-if="nodes.length > 0">
            <div class="fw-level-one clearfix">
                <%--<c:forEach  items="${list}" var="node">--%>
                <div v-if="node.parentId != 1" class="fw-item" v-for="(index, node) in nodes" :class="{'fw-item-one': node.parentId == 1}">
                    <div class="fw-inner">
                        <p class="title text-center">{{node.name}}</p>
                        <ul class="info-list">
                            <template v-if="index === 0">
                                <li><span>所属节点</span>{{node.group && node.group.name}}</li>

                            </template>
                            <template v-if="index > 0">
                                <li><span>所属节点</span>{{nodes[index - 1].name}}</li>
                            </template>
                            <li v-if="node.role"><span>关联角色</span>{{node.role && node.role.name}}</li>
                            <!-- <li><span>执行条件</span></li>
                            <li><span>动作名称</span></li> -->
                            <li v-if="node.form" title="{{node.form &&  node.form.name | cutText}}"><span>关联表单</span>{{node.form &&  node.form.name | cutText}}</li>
                            <li><span></span></li>
                        </ul>
                        <button type="button" class="btn-fw-close"><span>&times;</span></button>
                    </div>
                </div>
                <%--</c:forEach>--%>
            </div>
            <p v-show="hasSubNode" style="text-align: center;display: none;margin-bottom: 0;">没有流程子节点</p>
        </div>
        <p v-show="nodes.length < 1" style="text-align: center;display: none">不开心，暂时没有数据预览</p>

        <!-- <my-modal title="创建流程节点" :show.sync="createModal.show" @ok="createModalOk" @cancel="createModalCancel" ok-text="确认" cancel-text="取消">
            <div class="modal-body" slot="body">
                <form class="form-horizontal form-create-node">
                    <div class="form-group">
                        <label for="flowPathNode" class="control-label w130"><i class="icon-require">*</i>所属节点：</label>
                        <div class="input-box">
                            <select id="flowPathNode"  class="form-control">
                                <option>-请选择-</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="checker" class="control-label w130"><i class="icon-require">*</i>审核人：</label>
                        <div class="input-box">
                            <select id="checker"  class="form-control">
                                <option>-请选择-</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="actionCondition" class="control-label w130"><i class="icon-require">*</i>执行条件：</label>
                        <div class="input-box">
                            <input type="text" id="actionCondition"  class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="nextChecker" class="control-label w130"><i class="icon-require">*</i>下一级审核：</label>
                        <div class="input-box">
                            <input type="text" id="nextChecker"  class="form-control">
                        </div>
                    </div>
                </form>
            </div>
        </my-modal> -->
    </div>
    <div id="dialog-message" title="信息">
        <p id="dialog-content"></p>
    </div>
    <script type="text/javascript" src="/js/actyw/nodeExhibitionVue.js"></script>
    <script>
        $(function () {

            window['exhibition'].$data.nodes =${fns:toJson(list)}
            console.log(window['exhibition'].$data.nodes)
        })
    </script>
</body>
</html>