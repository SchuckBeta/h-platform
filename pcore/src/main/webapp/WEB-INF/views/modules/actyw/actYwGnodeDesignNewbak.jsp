<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>流程管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/static/cropper/cropper.min.css">
    <script type="text/javascript" src="/static/cropper/cropper.min.js"></script>
    <script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript" src="/js/actYwDesign/vue.min.js"></script>
    <script type="text/javascript" src="/js/actYwDesign/vue-validate.min.js"></script>
    <script type="text/javascript" src="/js/actYwDesign/modal.component.js"></script>
    <script src="${ctxStatic}/jsPlumb/jsplumb.js" type="text/javascript"></script>
    <script type="text/javascript" src="/js/uploadCutImage.js"></script>
    <style type="text/css">
        .red {
            color: red;
        }

        .form-search input, select {
            height: auto;
        }

        .form-search input, select {
            max-width: none;
        }

        .jtk-node {
            position: absolute;
            z-index: 100;
        }

        .jtk-flow-node {
            padding: 0 8px;
            margin: 0;
            max-width: 160px;
            height: 32px;
            line-height: 32px;
            border: 1px solid #ccc;
            background-color: #ffcc99;
        }

        .jtk-flow-node .name {
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
        }

        .jtk-flow-group {
            position: absolute;
            min-width: 200px;
            min-height: 200px;
            border: 1px dashed #ccc;
            z-index: 1;
        }

        .jtk-endpoint {
            z-index: 100;
        }

        .flow-canvas {
            position: relative;
        }

        .nodeDesign {
            position: relative;
        }

        .sup-draggable-box {
            position: absolute;
            bottom: 0;
            min-width: 200px;
            min-height: 200px;
            padding-right: 20px;
            padding-right: 20px;
            background-color: transparent;
        }

        .jktFlowOtherEnd {
            margin-bottom: 200px;
        }

        .jtk-flow-group-header {
            height: 30px;
            background-color: #ffcc99;
            overflow: hidden;
        }

        .jtk-flow-group-header .operations {
            float: right;
            height: 30px;
            overflow: hidden;
        }

        .jtk-flow-group-header .operations .add, .jtk-flow-group-header .operations .edit, .jtk-flow-group-header .operations .delete {
            display: block;
            float: left;
            width: 16px;
            margin-top: 5px;
            margin-right: 3px;
        }

        .jtk-flow-group-header .name {
            display: block;
            line-height: 30px;
            padding: 0 5px;
            height: 30px;
            font-weight: 700;
            text-align: left;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
        }

        .jtk-node-dropMenu {
            position: absolute;
            left: 100%;
            top: 0;
            margin: 0;
            padding: 0;
            width: 80px;
            text-align: center;
            border: 1px solid #ccc;
            background-color: #fff;
            z-index: 3000;
        }

        .jtk-node-dropMenu > li {
            line-height: 24px;
            border-bottom: 1px solid #ccc;
        }

        .jtk-node-dropMenu > li:last-child {
            border-bottom: none;
        }

        .jtk-node-dropMenu > li > a {
            display: block;
            text-decoration: none;
        }

        .jkt-flow-other {
            width: 64px;
            height: 30px;
            border-radius: 50%;
            text-align: center;
            background-color: #ffcc99;
            line-height: 30px;
        }

        /*.jkt-root-group {*/
        /*width: 120px;*/
        /*height: 120px;*/
        /*line-height: 120px;*/
        /*text-align: center;*/
        /*background: url('/images/bg-flowRootGroup.png');*/
        /*background-size: cover;*/
        /*}*/

        .jkt-root-group .name {
            display: inline-block;
            line-height: 24px;
            font-size: 14px;
            max-height: 48px;
            overflow: hidden;
            margin: 0 7px;
            font-weight: bold;
            vertical-align: middle;
        }

        .draggable-handler {
            position: absolute;
            bottom: 0;
            right: 0;
            margin-bottom: -2px;
            width: 20px;
            height: 20px;
            opacity: 0;
            /*cursor: crosshair;*/
        }

        .draggable-handler:hover {
            opacity: 1;
        }

        .flow-header {
            position: relative;
            padding-top: 10px;
            overflow: hidden;
            padding-bottom: 10px;
            border-bottom: 1px solid #ddd;
        }

        .flow-header .name {
            font-size: 14px;
            line-height: 26px;
            text-align: center;
            color: #e9432d;
            font-weight: bold;
        }

        .h26 {
            position: absolute;
            right: 0;
            height: 26px;
        }

        .jtk-flow-group-node {
            position: absolute;
        }

    </style>

    <script type="text/javascript">
        $(document).ready(function () {
            $("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
</head>
<body>
<input type="hidden" id="groupId" value="${actYwGnode.groupId}">
<input type="hidden" id="ftpPath" value="${fns:ftpImgUrl('/tool/oseasy/ueditor/1.png')}">
<div id="nodeDesign" class="container-fluid km-editor">
    <div class="mybreadcrumbs">
        <span>流程</span>
    </div>
    <div class="content_panel">
        <ul class="nav nav-tabs">
            <li><a href="${ctx}/actyw/actYwGnode/">流程列表</a></li>
            <li><a href="${ctx}/actyw/actYwGnode/form?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程添加</a>
            </li>
            <li><a href="${ctx}/actyw/actYwGnode/design?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a>
            </li>
            <li class="active"><a
                    href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计New</a>
            </li>
        </ul>
        <p v-if="!dataLoad" class="text-center" style="line-height: 40px; font-weight: bold">数据加载中...</p>
        <div class="flow-header hide" :style="{display: !dataLoad ? 'none': 'block'}">
            <div class="pull-right text-right h26">
                <button type="button" class="btn btn-small" @click="savePosition">保存位置</button>
                <button type="button" class="btn btn-small" @click="openModalFlowNode">创建节点</button>
                <button type="button" class="btn btn-small" @click="openModalFlowGroup">创建组</button>
                <a href="${ctx }/actyw/actYwGroup/list" class="btn btn-small">完成</a>
            </div>
            <div class="name">
                {{rootNodeGroup.name}}
            </div>
        </div>
        <div id="flowCanvas" class="flow-canvas jtk-surface hide jtk-surface-nopan"
             :style="{display: !dataLoad ? 'none': 'block'}">
            <div class="jtk-node jtk-node-common jkt-flow-other" :class="{jktFlowOtherEnd: index===1}"
                 v-for="(item, index) in otherNodes"
                 :id="'jtkNode'+item.id"
                 :key="item.id"
                 :ref="item.id" :style="{left: item.posLux + 'px',  top: item.posLuy + 'px'}">
                <div>{{ item.name }}</div>
            </div>

            <%--<div class="jtk-node jtk-node-common jkt-root-group"--%>
            <%--:id="'jtkNode'+rootNodeGroup.id"--%>
            <%--:ref="rootNodeGroup.id"--%>
            <%--:style="{left: rootNodeGroup.posLux + 'px', top: rootNodeGroup.posLuy  + 'px'}">--%>
            <%--<span class="name">{{ rootNodeGroup.name }}</span>--%>
            <%--</div>--%>
            <div class="jtk-node-common jtk-flow-group-node" v-for="(itemGroup, index) in flowGroups"
                 v-if="!itemGroup.hasGroup"
                 :id="'jtkNode'+itemGroup.id"
                 :key="itemGroup.id" :ref="itemGroup.id" :group="itemGroup.id"
                 :data-idx="index"
                 :style="{left: itemGroup.posLux + 'px', top: itemGroup.posLuy + 'px', width:  (itemGroup.width == '0' ? 'auto' : '') + 'px', height:  itemGroup.height + 'px'}">
                <div class="jtk-flow-group-header">
                    <div class="operations">
                        <a href="javascript:void(0);" class="edit" title="编辑节点组"
                           @click="upDateModalFlowNode(itemGroup)"><img
                                src="/images/edit-flownode.png"> </a>
                        <a v-if="false" href="javascript:void(0);" class="delete" title="删除节点组"><img
                                src="/images/delete-flownode.png"></a>
                    </div>
                    <span class="name">{{ itemGroup.name }}</span>
                </div>
            </div>
            <div class="jtk-flow-group jtk-node-common" v-for="(itemGroup, index) in flowGroups"
                 v-if="itemGroup.hasGroup && itemGroup.hasGroup"
                 :id="'jtkNode'+itemGroup.id"
                 :key="itemGroup.id" :ref="itemGroup.id" :group="itemGroup.id"
                 :data-idx="index"
                 :style="{left: itemGroup.posLux + 'px', top: itemGroup.posLuy + 'px', width:  itemGroup.width + 'px', height:  itemGroup.height + 'px'}">
                <div class="jtk-flow-group-header">
                    <div class="operations">
                        <a href="javascript:void(0);" class="add" title="添加子节点" @click="openModalNode(itemGroup)"><img
                                src="/images/add-flownode.png"> </a>
                        <a href="javascript:void(0);" class="edit" title="编辑节点组"
                           @click="upDateModalFlowGroup(itemGroup)"><img
                                src="/images/edit-flownode.png"> </a>
                        <a v-if="false" href="javascript:void(0);" class="delete" title="删除节点组"><img
                                src="/images/delete-flownode.png"></a>
                    </div>
                    <span class="name">{{ itemGroup.name }}</span>
                </div>
                <div class="jtk-node jtk-node-common jtk-flow-node" v-if="itemNode.parentId == itemGroup.id"
                     v-for="(itemNode, index) in flowNodes" :id="'jtkNode'+itemNode.id"
                     :key="itemNode.id" :ref="itemNode.id"
                     :style="{left: itemNode.posLux + 'px', top: itemNode.posLuy + 'px'}"
                     @contextmenu="jtkNodeDropMenu(index, itemNode.id, $event)" @mouseleave="dropMenuHide(itemNode.id)">
                    <ul class="jtk-node-dropMenu" v-show="jtkNodeDropMenuIndex === index">
                        <li>
                            <a href="javascript: void(0);" @click="openModalEditNode(itemNode)">修改</a>
                        </li>
                        <li v-if="false">
                            <a href="javascript: void(0);">添加下级</a>
                        </li>
                        <li v-if="false">
                            <a href="javascript: void(0);">删除</a>
                        </li>
                    </ul>
                    <div class="name">{{ itemNode.name }}</div>
                </div>
            </div>
            <div class="sup-draggable-box" :class="'draggable-box-'+ itemGroup.id" :target="itemGroup.nextFunId"
                 v-for="(itemGroup, index) in flowGroups"
                 v-if="itemGroup.hasGroup"
                 :key="'drag'+ itemGroup.id"
                 :style="{left: itemGroup.posLux + 'px', top: itemGroup.posLuy + 'px', width:  itemGroup.width + 'px', height:  itemGroup.height + 'px'}">
                <span class="draggable-handler" :data-idx="index"><img src="/images/flow-scale.png"></span>
            </div>
        </div>
    </div>

    <my-modal title="创建流程組" :show.sync="isFlowGroupVisible" @ok="saveNodeGroup" @cancel="cancelNodeGroup"
              ok-text="确定" cancel-text="取消" :large="true">
        <div class="modal-body" slot="body">
            <form :form="nodeGroupForm" @submit.prevent="validateForm('nodeGroupForm')" data-vv-scope="nodeGroupForm"
                  class="form-horizontal hide" :style="{display: !dataLoad ? 'none': 'block'}">
                <div class="control-group">
                    <label class="control-label">流程组名：</label>
                    <div class="controls">
                        <input type="hidden" v-model="nodeGroupForm.groupId">
                        <span class="control-static">{{groupName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">流程跟节点：</label>
                    <div class="controls">
                        <%--<input type="hidden" v-model="nodeGroupForm.parentId">--%>
                        <span class="control-static">{{parentName}}</span>
                    </div>
                </div>
                <div v-show="hidePreFunIds" class="control-group">
                    <label class="control-label">流程前置业务节点：</label>
                    <div class="controls">
                        <select v-model="nodeGroupForm.preFunId" class="form-control input-large">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in prevNodes" :value="item.id" :key="item.id">{{item.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><span class="red" style="margin-right: 4px;">*</span>流程业务节点：</label>
                    <div class="controls">
                        <input type="text" class="form-control input-large" v-validate="'required'" name="name"
                               v-model="nodeGroupForm.name">
                        <div v-show="errors.has('nodeGroupForm.name')" class="red">{{
                            errors.first('nodeGroupForm.name')}}
                        </div>
                    </div>
                </div>
                <div v-show="hideNextFunIds" class="control-group">
                    <label class="control-label">流程后置业务节点：</label>
                    <div class="controls">
                        <select v-model="nodeGroupForm.nextFunId" class="form-control input-large">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in nextNodes" :value="item.id" :key="item.id">{{item.node.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><span class="red" style="margin-right: 4px;">*</span>图标</label>
                    <div class="controls">
                        <div class="img-content" style="max-width: 100px; max-height: 100px;line-height: 1">
                            <img id="iconPic" :src="nodeGroupForm.iconUrl" style="display: block;max-width: 100%">
                            <%--<input type="text" style="display: none" name="file" v-model="nodeGroupForm.iconUrl">--%>
                        </div>
                        <input type="button" id="uploadIcon" class="btn" style="" value="更新图标"/>
                        <div v-show="isUploadIconUrl" class="red">请上传图标</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">显示：</label>
                    <div class="controls">
                        <label>
                            <input type="radio" v-model="nodeGroupForm.isShow" :value="1" class="form-control">是
                        </label>
                        <label>
                            <input type="radio" v-model="nodeGroupForm.isShow" :value="0" class="form-control">否
                        </label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <textarea rows="3" v-model="nodeGroupForm.remarks" class="input-xlarge"></textarea>
                    </div>
                </div>
            </form>
        </div>
    </my-modal>

    <my-modal title="创建流程节点" :show.sync="isFlowNodeVisible" @ok="saveNode" @cancel="cancelNode"
              ok-text="确定" cancel-text="取消" :large="true">
        <div class="modal-body" slot="body">
            <form :form="nodeForm" class="form-horizontal hide" @submit.prevent="validateForm('nodeForm')"
                  data-vv-scope="nodeForm" :style="{display: !dataLoad ? 'none': 'block'}">
                <div class="control-group">
                    <label class="control-label">自定义流程：</label>
                    <div class="controls">
                        <input type="hidden" v-model="nodeForm.groupId">
                        <span class="control-static">{{groupName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">父级节点：</label>
                    <div class="controls">
                        <input type="hidden" v-model="nodeForm.parent.id">
                        <span class="control-static">{{parentName}}</span>
                    </div>
                </div>
                <div v-show="hidePreFunIds" class="control-group">
                    <label class="control-label">前置节点：</label>
                    <div class="controls">
                        <select v-model="nodeForm.preFunId">
                            <option :value="0">请选择</option>
                            <option v-for="(item, index) in prevNodes" :value="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><span class="red" style="margin-right: 4px;">*</span>流程节点：</label>
                    <div class="controls">
                        <input type="text" class="form-control input-large" v-validate="'required'" name="name"
                               v-model="nodeForm.name" data-vv-scope="nodeForm">
                        <div v-show="errors.has('nodeForm.name')" class="red">{{ errors.first('nodeForm.name')}}</div>
                    </div>
                </div>
                <div v-show="hideNextFunIds" class="control-group">
                    <label class="control-label">后置节点：</label>
                    <div class="controls">
                        <select v-model="nodeForm.nextFunId" class="form-control input-large">
                            <option :value="0">请选择</option>
                            <option v-for="(item, index) in nextNodes" :value="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">表单：</label>
                    <div class="controls">
                        <select v-model="nodeForm.formId" class="form-control input-large">
                            <option :value="0">请选择</option>
                            <option v-for="(item, index) in forms" :value="item.id">{{item.name | replaceFormType}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><span class="red" style="margin-right: 4px;">*</span>图标</label>
                    <div class="controls">
                        <div class="img-content" style="max-width: 100px; max-height: 100px;line-height: 1">
                            <img id="iconNodePic" :src="nodeForm.iconUrl" src=""
                                 style="display: block;max-width: 100%">
                        </div>
                        <%--<input type="hidden" name="file" v-model="nodeGroupForm.iconUrl">--%>
                        <input type="button" id="uploadNodeIcon" class="btn" style="" value="更新图标"/>
                        <div v-show="isUploadNodeFormIconUrl" class="red">请上传图标</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">显示：</label>
                    <div class="controls">
                        <label>
                            <input type="radio" v-model="nodeForm.isShow" :value="1" class="form-control">是
                        </label>
                        <label>
                            <input type="radio" v-model="nodeForm.isShow" :value="0" class="form-control">否
                        </label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">角色：</label>
                    <div class="controls">
                        <select v-model="nodeForm.flowGroup" class="form-control input-large">
                            <option :value="0">所有角色</option>
                            <option v-for="(item, index) in roles" :value="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <textarea class="form-control input-xlarge" v-model="nodeForm.remarks"></textarea>
                    </div>
                </div>
            </form>
        </div>
    </my-modal>

    <my-modal title="修改流程节点" :show.sync="isFlowNodeEditVisible" @ok="saveEditNode" @cancel="cancelEditNode"
              ok-text="确定" cancel-text="取消" :large="true">
        <div class="modal-body" slot="body">
            <form :form="nodeEditForm" class="form-horizontal hide" @submit.prevent="validateForm('nodeEditForm')"
                  data-vv-scope="nodeEditForm" :style="{display: !dataLoad ? 'none': 'block'}">
                <div class="control-group">
                    <label class="control-label">自定义流程：</label>
                    <div class="controls">
                        <input type="hidden" v-model="nodeEditForm.groupId">
                        <span class="control-static">{{groupName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">父级节点：</label>
                    <div class="controls">
                        <input type="hidden" v-model="nodeEditForm.parent.id">
                        <span class="control-static">{{parentName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">流程节点：</label>
                    <div class="controls">
                        <input type="text" class="form-control input-large" v-validate="'required'" name="name"
                               v-model="nodeEditForm.name" data-vv-scope="nodeEditForm">
                        <div v-show="errors.has('nodeEditForm.name')" class="red">{{
                            errors.first('nodeEditForm.name')}}
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">表单：</label>
                    <div class="controls">
                        <select v-model="nodeEditForm.formId" class="form-control input-large">
                            <option :value="0">请选择</option>
                            <option v-for="(item, index) in forms" :value="item.id">{{item.name | replaceFormType}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><span class="red" style="margin-right: 4px;">*</span>图标</label>
                    <div class="controls">
                        <div class="img-content" style="max-width: 100px; max-height: 100px;line-height: 1">
                            <img id="iconEditNodePic" :src="nodeEditForm.iconUrl"
                                 style="display: block;max-width: 100%">
                        </div>
                        <%--<input type="hidden" name="file" v-model="nodeGroupForm.iconUrl">--%>
                        <input type="button" id="uploadNodeEditIcon" class="btn" style="" value="更新图标"/>
                        <div v-show="isUploadNodeEditFormIconUrl" class="red">请上传图标</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">显示：</label>
                    <div class="controls">
                        <label>
                            <input type="radio" v-model="nodeEditForm.isShow" :value="1" class="form-control">是
                        </label>
                        <label>
                            <input type="radio" v-model="nodeEditForm.isShow" :value="0" class="form-control">否
                        </label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">角色：</label>
                    <div class="controls">
                        <select v-model="nodeEditForm.flowGroup" class="form-control input-large">
                            <option :value="0">所有角色</option>
                            <option v-for="(item, index) in roles" :value="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <textarea class="form-control input-xlarge" v-model="nodeEditForm.remarks"></textarea>
                    </div>
                </div>
            </form>
        </div>
    </my-modal>



    <sys:upLoadCutImage width="100" height="100" btnid="uploadIcon" imgId="iconPic" column="file" filepath="ueditor"
                        className="modal-avatar hide" modalId="modalAvatar1"></sys:upLoadCutImage>
    <sys:upLoadCutImage width="100" height="100" btnid="uploadNodeIcon" imgId="iconNodePic" column="file"
                        filepath="ueditor"
                        className="modal-avatar hide" modalId="modalAvatar2"></sys:upLoadCutImage>
    <sys:upLoadCutImage width="100" height="100" btnid="uploadNodeEditIcon" imgId="iconEditNodePic" column="file"
                        filepath="ueditor"
                        className="modal-avatar hide" modalId="modalAvatar3"></sys:upLoadCutImage>
</div>


<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

<script>

    Vue.use(VeeValidate);

    var dictionary = {
        zhCN: {
            messages: {
                required: function () {
                    return '必填信息'
                }
            }
        }
    };


    var jsPlumbMixin = {
        //连接线样式
        paintStyle: {
            strokeWidth: 6,
            stroke: '#567567',
            outlineStroke: 'black',
            outlineWidth: 1
        },
        //默认流程连接样式，stub源，目标到端的最小值，默认为30 cornerRadius 转角角度 默认为0
        connector: ["Flowchart", {
            stub: [20, 20],
            gap: 0,
            cornerRadius: 10,
            alwaysRespectStubs: false
        }, {cssClass: 'connector1'}],
        //连接头样式 目标元素为实心原
        connectorPaintStyleSource: {
            stroke: '#ffcc99',
            strokeWidth: 1,
            joinstyle: 'round',
            outlineStroke: 'white',
            outlineWidth: 1
        },
        dragOptions: {cursor: 'pointer', zIndex: 2000},
        flowInstance: null,
        methods: {
            getFlowInstance: function () {
                this.flowInstance = jsPlumb.getInstance({
                    DragOptions: {
                        cursor: 'pointer',
                        zIndex: 2000
                    },
                    ConnectionOverlays: [
                        ["Arrow", {
                            location: 1,
                            visible: true,
                            width: 6,
                            length: 6,
                            id: "ARROW",
                            events: {
                                click: function () {
//                                    alert("you clicked on the arrow overlay")
                                }
                            }
                        }]
                    ],
                    Container: 'flowCanvas'
                });
            },

            getSourceEndPoint: function (id) {
                return {
                    endpoint: "Dot",
                    paintStyle: {
                        stroke: "#ffcc99",
                        fill: "transparent",
                        radius: 3,
                        strokeWidth: 1
                    },
                    cssClass: 'sourceEndPoint' + id,
                    isSource: true,
                    connector: ["Bezier", {curviness: 100}, {cssClass: 'connector1'}],
                    connectorStyle: this.$options.connectorPaintStyleSource,
                    maxConnections: -1,
                    dropOptions: {hoverClass: "hover", activeClass: "active"}
                }
            },

            getTargetEndPoint: function (id) {
                return {
                    endpoint: "Dot",
                    paintStyle: {fill: "#ffcc99", radius: 3},
                    maxConnections: -1,
                    cssClass: 'targetEndPoint' + id,
                    dropOptions: {hoverClass: "hover", activeClass: "active"},
                    isTarget: true
                }
            },

            _addEndpoints: function (toId, sourceAnchors, targetAnchors) {
                for (var i = 0; i < sourceAnchors.length; i++) {
                    var sourceUUID = toId + sourceAnchors[i];
                    this.flowInstance.addEndpoint(toId, this.getSourceEndPoint(toId), {
                        anchor: sourceAnchors[i],
                        uuid: sourceUUID
                    });
                }

                for (var j = 0; j < targetAnchors.length; j++) {
                    var targetUUID = toId + targetAnchors[j];
                    this.flowInstance.addEndpoint(toId, this.getTargetEndPoint(toId), {
                        anchor: targetAnchors[j],
                        uuid: targetUUID
                    });
                }
            },

            _addSourceEndPoints: function (toId, sourceAnchors) {
                for (var i = 0; i < sourceAnchors.length; i++) {
                    var sourceUUID = toId + sourceAnchors[i];
                    this.flowInstance.addEndpoint(toId, this.getSourceEndPoint(toId), {
                        anchor: sourceAnchors[i],
                        uuid: sourceUUID
                    });
                }
            },

            _connect: function (uuids, scope, detachable) {
                this.flowInstance.connect({uuids: uuids, detachable: detachable, scope: scope})
            },


            _draggable: function (ele) {
                this.flowInstance.draggable(ele);
            },

            _addToGroup: function (groupId, eles) {
                this.flowInstance.addToGroup(groupId, eles);
            },

            _addGroup: function (ele, groupId) {
                this.flowInstance.addGroup({
                    el: ele,
                    id: groupId,
                    constrain: true,
                    anchor: "TopCenter",
                    endpoint: "Blank",
                    droppable: false
                });
            },
            _createNodePoint: function (id) {
                this._addEndpoints('jtkNode' + id, ['BottomCenter'], ['TopCenter']);
            },
            //创建一个节点组节点
            _createNode: function (id, groupId) {
                var ele = this.$refs[id][0];
                this._createNodePoint(id);
                this._draggable(ele);
                this._addToGroup(groupId, ele);
            },

            _createNodeGroup: function (id, groupId) {
                var ele = this.$refs[id][0];
                this._createNodePoint(id);
                this.flowInstance.draggable(ele);
                this._addGroup(ele, groupId);
            }
        },
        created: function () {
        }
    };

    var nodeDesignComponent = new Vue({
        el: '#nodeDesign',
        mixins: [jsPlumbMixin],

        data: function () {
            return {
                projectFlowId: '',
                flowNodes: [],
                flowGroups: [],
                connections: [],
                otherNodes: [],
                isFlowNodeVisible: false,
                nodeForm: {
                    id: '',
                    name: '',
                    groupId: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    nextFunId: '',
                    isShow: 1,
                    flowGroup: 0,
                    formId: 0,
                    remarks: '',
                    hasGateway: false,
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    hasGroup: false,
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                },
                isFlowNodeEditVisible: false,
                nodeEditForm: {
                    id: '',
                    name: '',
                    groupId: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    isShow: 1,
                    flowGroup: 0,
                    formId: 0,
                    remarks: '',
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                },
                isFlowGroupVisible: false,
                nodeGroupForm: {
                    id: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    name: '',
                    nextFunId: '',
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    isShow: 1,
                    role: '',
                    remarks: '',
                    hasGateway: false,
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    hasGroup: false,
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                },
                endNodeEvent: {},
                startNodeEvent: {},
                groupName: '',
                nodeName: '',
                parentName: '',
                roles: [],
                prevNodes: [],
                nextNodes: [],
                forms: [],
                createGroupTime: '',
                createNodeTime: '',
                isAddNodeGroup: false,
                currentGroupId: '',
                nodeList: [],
                rootNodeGroup: {},
                rootNode: {},
                nodeTypes: [],
                groupLeft: '',
                //当前流程组
                currentNodeGroup: {},
                currentNode: {},
                jtkNodeDropMenuIndex: '',
                isUpdateFlowGroup: false,
                dataLoad: false,
                hidePreFunIds: false,
                hideNextFunIds: false,
                isUploadNodeEditFormIconUrl: false,
                isUploadNodeFormIconUrl: false,
                isUploadIconUrl: false
            }
        },

        computed: {
            httpFtp: function () {
                var ftpPath = $('#ftpPath').val();
                return ftpPath.substring(0, ftpPath.indexOf('/oseasy/'));
//                return 'http://192.168.0.105:38888'
            }
        },

        filters: {
            replaceFormType: function (value) {
                return value.indexOf('/') ? value.substring(value.indexOf('/') + 1) : value
            }
        },

        methods: {
            //初始化jsPlumb
            initJsPlumb: function () {
                var self = this;
                var idx;
                var flowGroups = this.flowGroups;

                this.getFlowInstance();

                this.flowGroups.forEach(function (item) {
                    self._addEndpoints('jtkNode' + item.id, ['BottomCenter'], ['TopCenter'])
                });
                //生成点
                this.otherNodes.forEach(function (item) {
                    self._addEndpoints('jtkNode' + item.id, ['BottomCenter'], ['TopCenter'])
                });

                //rootNodeGroup生产点
//                self._addEndpoints('jtkNode' + self.rootNodeGroup.id, ['BottomCenter'], ['TopCenter']);

                //生成点
                this.flowNodes.forEach(function (item) {
                    self._addEndpoints('jtkNode' + item.id, ['BottomCenter'], ['TopCenter']);
                    self.flowInstance.draggable($('#jtkNode' + item.id)[0]);
                });

                this.flowInstance.draggable(jsPlumb.getSelector(".flow-canvas .jkt-flow-other"));

                this.flowInstance.draggable(jsPlumb.getSelector(".flow-canvas .jtk-flow-group"), {
                    start: function (params) {
                        idx = $(params.el).attr('data-idx');
                    },
                    stop: function (params) {
                        flowGroups[idx]['posLux'] = params.pos[0];
                        flowGroups[idx]['posLuy'] = params.pos[1];
                    }
                });

                //连接线
                this.connections.forEach(function (item) {
                    var source = item.source;
                    var target = item.target;
                    var uuids = [source, target];
                    self._connect(uuids, item.scope, true);
                });

                this.flowGroups.forEach(function (item) {
                    self.flowInstance.addGroup({
                        el: $('#jtkNode' + item.id)[0],
                        id: item.id,
                        constrain: true,
                        anchor: "TopCenter",
                        endpoint: "Blank",
                        droppable: false
                    });
                });

                this.flowNodes.forEach(function (item) {
                    self.flowInstance.addToGroup(item.parentId, [$('#jtkNode' + item.id)[0]]);
                });

                jsPlumb.fire("jsPlumbDemoLoaded", this.flowInstance);


            },

            //创建组
            createGroup: function (id, groupId) {
                this.$nextTick(function () {
                    this._createNodeGroup(id, groupId);
                });
            },
            //创建节点
            createNode: function (id, groupId) {
                this.$nextTick(function () {
                    this._createNode(id, groupId);
                });
            },

            setIconUrl: function (url, str) {
                if (!url) {
                    //默认图像
                    return '';
                }
                var index = url.indexOf(str);
                if (index > -1) {
                    return this.httpFtp + url.substr(index + 5)
                }
            },

            jtkNodeDropMenu: function (index, id, $event) {
                $event.preventDefault();
                this.$refs[id][0].style.zIndex = '101';
                this.jtkNodeDropMenuIndex = index;
            },

            dropMenuHide: function (id) {
                this.$refs[id][0].style.zIndex = '';
                this.jtkNodeDropMenuIndex = '';

            },
            openModalNode: function (nodeGroup) {
                this.currentNodeGroup = nodeGroup;
                this.getNodeData();
            },

            //保存节点
            saveNode: function () {
                var self = this;
                var postNodeXhr;
                var nodeForm = this.nodeForm;
                var $inputFile = $('#iconNodePic').next();
                var nodeTypes = this.nodeTypes;
                var currentNodeGroup = this.currentNodeGroup;
                var subs = currentNodeGroup.subs;
                var subsLen = subs.length;
                nodeForm.nodeId = nodeTypes[1];
                nodeForm.hasGroup = false;
                nodeForm.iconUrl = $inputFile.val();
                nodeForm.nextFunGnode.id = nodeForm.nextFunId;
                nodeForm.nextFunGnode.node.id = nodeTypes[1];
                nodeForm.preFunGnode.id = nodeForm.preFunId;
                nodeForm.preFunGnode.node.id = nodeTypes[0];

                this.validateForm('nodeForm').then(function (result) {
                    if (result) {
                        if (nodeForm.iconUrl) {
                            if (subsLen > 0) {
                                nodeForm.posLux = subs[0].posLux;
                                nodeForm.posLuy = parseInt(subs[subsLen - 1].posLuy) + 40;
                            } else {
                                nodeForm.posLux = 33;
                                nodeForm.posLuy = 40;
                            }
                            postNodeXhr = $.ajax({
                                url: '/a/actyw/gnode/saveProcess',
                                type: 'POST',
                                data: JSON.stringify(nodeForm),
                                dataType: 'json',
                                contentType: 'application/json'
                            });
                            postNodeXhr.success(function (res) {
                                self.savePosition();
                                self.cancelNode();
                                window.location.reload();
                            });
                            postNodeXhr.error(function (error) {
                                console.log(error)
                            });
                        } else {
                            self.isUploadNodeFormIconUrl = true;
                        }
                    }
                });
            },
            //关闭nodeModel
            cancelNode: function () {
                this.nodeForm = {
                    id: '',
                    name: '',
                    groupId: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    nextFunId: '',
                    isShow: 1,
                    flowGroup: 0,
                    formId: 0,
                    remarks: '',
                    hasGateway: false,
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    hasGroup: false,
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                };
                this.isFlowNodeVisible = false;
            },

            getNodeData: function () {
                var self = this;
                var subs = this.currentNodeGroup.subs;
                var len = subs.length;
                var nodeForm = this.nodeForm;
                this.getNodeDataXhr().then(function () {
                    if (subs.length > 0) {
                        nodeForm.preFunId = subs[len - 1].id;
                    } else {
                        nodeForm.preFunId = self.startNodeEvent.id;
                    }
                    nodeForm.nextFunId = self.endNodeEvent.id
                });
                this.isFlowNodeVisible = true;
            },

            getNodeDataXhr: function () {
                var url = this.getNodeUrl();
                var xhr = $.get(url);
                var self = this;
                var nodeForm = this.nodeForm;

                xhr.success(function (data) {
                    if (data.status) {
                        var nodeData = data.datas;
                        self.parentName = nodeData.parentName;
                        self.groupName = nodeData.groupName;
                        self.prevNodes = nodeData.pregnodes;
                        self.nextNodes = nodeData.nextgnodes;
                        self.forms = nodeData.forms;
                        self.roles = nodeData.roles;
                        self.nodeTypes = nodeData.nodeTypes;
                        nodeForm.groupId = nodeData.groupId;
                        nodeForm.parent.id = nodeData.parentId;
                        self.endNodeEvent = nodeData.end;
                        self.startNodeEvent = nodeData.start;
                    }
                });
                return xhr;
            },

            getNodeUrl: function () {
                var id = this.currentNodeGroup.id;
                var groupId = this.projectFlowId;
                return '/a/actyw/gnode/query/' + groupId + '?parentId=' + id;
            },

            openModalEditNode: function (node) {
                this.currentNode = node;
                this.isFlowNodeEditVisible = true;
                this.getEditNodeData();
            },

            saveEditNode: function () {
                var self = this;
                var nodeEditForm = this.nodeEditForm;
                var currentNode = this.currentNode;
                var $inputFile = $('#iconEditNodePic').next();
                nodeEditForm.nodeId = this.nodeTypes[1];
                nodeEditForm.preFunId = currentNode.preFunId;
                nodeEditForm.nextFunId = currentNode.nextFunId;
                nodeEditForm.iconUrl = $inputFile.next().size() > 0 ? $inputFile.next().val() : currentNode.iconUrl;
                nodeEditForm.nextFunGnode.id = nodeEditForm.nextFunId;
                nodeEditForm.nextFunGnode.node.id = this.nodeTypes[1];
                nodeEditForm.preFunGnode.id = nodeEditForm.preFunId;
                nodeEditForm.preFunGnode.node.id = this.nodeTypes[0];
                nodeEditForm.groupId = this.projectFlowId;
                nodeEditForm.parent.id = currentNode.parentId;
                nodeEditForm.hasGroup = false;

                this.validateForm('nodeEditForm').then(function (result) {
                    if (result) {
                        if (nodeEditForm.iconUrl) {
                            $.ajax({
                                url: '/a/actyw/gnode/updateProcess/',
                                type: 'POST',
                                data: JSON.stringify(nodeEditForm),
                                dataType: 'json',
                                contentType: 'application/json',
                                success: function (data) {
                                    self.cancelEditNode();
                                    window.location.reload();
                                }
                            });
                        } else {
                            self.isUploadNodeFormIconUrl = true;
                        }
                    }
                });


            },

            cancelEditNode: function () {
                this.nodeEditForm = {
                    id: '',
                    name: '',
                    groupId: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    isShow: 1,
                    flowGroup: 0,
                    formId: 0,
                    remarks: '',
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                };
                this.isFlowNodeEditVisible = false;
            },

            getEditNodeData: function () {
                this.getEditNodeXhr();
            },

            getEditNodeXhr: function () {
                var url = this.getEditNodeUrl();
                var xhr = $.get(url);
                var self = this;
                var nodeEditForm = this.nodeEditForm;
                var currentNode = this.currentNode;
                xhr.success(function (data) {
                    if (data.status) {
                        var nodeData = data.datas;
                        self.parentName = nodeData.parentName;
                        self.groupName = nodeData.groupName;
                        self.prevNodes = nodeData.pregnodes;
                        self.nextNodes = nodeData.nextgnodes;
                        self.roles = nodeData.roles;
                        self.forms = nodeData.forms;
                        self.nodeTypes = nodeData.nodeTypes;
                        nodeEditForm.formId = currentNode.formId;
                        nodeEditForm.flowGroup = currentNode.flowGroup;
                        nodeEditForm.iconUrl = self.setIconUrl(currentNode.iconUrl, '/tool');
                        nodeEditForm.remarks = currentNode.remarks;
                        nodeEditForm.name = currentNode.name;
                        nodeEditForm.id = currentNode.id;
                    }
                });
                return xhr;
            },

            getEditNodeUrl: function () {
                var parentId = this.currentNode.parentId;
                var groupId = this.projectFlowId;
                return '/a/actyw/gnode/query/' + groupId + '?parentId=' + parentId;
            },

            upDateModalFlowNode: function (flowGroup) {
                this.nodeGroupForm.hasGroup = false;
                this.isUpdateFlowGroup = true;
                this.currentNodeGroup = flowGroup;
                this.getNodeGroup();
            },

            upDateModalFlowGroup: function (flowGroup) {
                this.currentNodeGroup = flowGroup;
                this.nodeGroupForm.hasGroup = true;
                this.isUpdateFlowGroup = true;
                this.getNodeGroup();
            },

            openModalFlowNode: function () {
                var flowGroups = this.flowGroups;
                var flowGroupLen = this.flowGroups.length;
                var rootNodeGroup = this.rootNodeGroup;
                this.currentNodeGroup = flowGroupLen > 0 ? flowGroups[flowGroupLen - 1] : rootNodeGroup;
                this.nodeGroupForm.hasGroup = false;
                this.isUpdateFlowGroup = false;
                this.getNodeGroup();
            },
            //添加
            openModalFlowGroup: function () {
                var flowGroups = this.flowGroups;
                var flowGroupLen = this.flowGroups.length;
                var rootNodeGroup = this.rootNodeGroup;
                this.currentNodeGroup = flowGroupLen > 0 ? flowGroups[flowGroupLen - 1] : rootNodeGroup;
                this.isUpdateFlowGroup = false;
                this.nodeGroupForm.hasGroup = true;
                this.getNodeGroup();
            },

            saveNodeGroup: function () {
                var postNodeGroupXhr;
                var url;
                var self = this;
                var $win = $(window);
                var $ele;
                var endPosY = this.otherNodes[1].posLuy;
                var $iconPic = $('#iconPic');
                var nodeGroupForm = this.nodeGroupForm;
                var isUpdateFlowGroup = this.isUpdateFlowGroup;
                var flowGroups = this.flowGroups;
                var hasGroup = nodeGroupForm.hasGroup;
                var otherNodes = this.otherNodes;
                var currentNodeGroup = this.currentNodeGroup;

                nodeGroupForm.nodeId = this.nodeTypes[0];
                nodeGroupForm.nextFunGnode.id = nodeGroupForm.nextFunId;
                nodeGroupForm.nextFunGnode.node.id = this.nodeTypes[1];
                nodeGroupForm.preFunGnode.id = nodeGroupForm.preFunId;
                nodeGroupForm.preFunGnode.node.id = this.nodeTypes[0];

                if (isUpdateFlowGroup) {
                    url = '/a/actyw/gnode/updateProcess/';
                    nodeGroupForm.iconUrl = $iconPic.next().size() > 0 ? $iconPic.next().val() : currentNodeGroup.iconUrl;
                } else {
                    url = '/a/actyw/gnode/saveProcess';
                    nodeGroupForm.iconUrl = $iconPic.next().val();
                    if (flowGroups.length > 0) {
                        $ele = $(this.$refs[flowGroups[flowGroups.length - 1].id]);
                    } else {
                        $ele = $(this.$refs[otherNodes[0].id]);
                    }
                    nodeGroupForm.posLux = ($win.width() - 200) / 2 - 100;
                    nodeGroupForm.posLuy = $ele.position().top + $ele.height() + 40;
                }

                this.validateForm('nodeGroupForm').then(function (result) {
                    if (result) {
                        if (nodeGroupForm.iconUrl) {
                            if (!isUpdateFlowGroup) {
                                if (hasGroup) {
                                    otherNodes[1].posLuy = parseInt(endPosY) + 250;
                                } else {
                                    otherNodes[1].posLuy = parseInt(endPosY) + 80;
                                }
                            }
                            postNodeGroupXhr = $.ajax({
                                url: url,
                                type: 'POST',
                                data: JSON.stringify(nodeGroupForm),
                                dataType: 'json',
                                contentType: 'application/json'
                            });
                            postNodeGroupXhr.success(function (res) {
                                if (!isUpdateFlowGroup) {
                                    self.savePosition();
                                }
                                self.cancelNodeGroup();
                                window.location.reload();
                            });
                            postNodeGroupXhr.error(function (error) {
                                console.log(error)
                            });
                        } else {
                            self.isUploadIconUrl = true;
                        }
                    }
                });
            },

            //关闭model group
            cancelNodeGroup: function () {
                this.nodeGroupForm = {
                    id: '',
                    preFunId: '',
                    preFunGnode: {
                        node: {
                            id: ''
                        }
                    },
                    name: '',
                    nextFunId: '',
                    nextFunGnode: {
                        id: '',
                        node: {
                            id: ''
                        }
                    },
                    isShow: 1,
                    role: '',
                    remarks: '',
                    hasGateway: false,
                    parent: {
                        id: ''
                    },
                    nodeId: '',
                    hasGroup: false,
                    posLux: '',
                    posLuy: '',
                    width: '',
                    height: '',
                    iconUrl: ''
                };
                this.isFlowGroupVisible = false;
            },

            getNodeGroup: function () {
                var self = this;
                var nodeGroupForm = this.nodeGroupForm;
                var currentNodeGroup = this.currentNodeGroup;
                var isUpdateFlowGroup = this.isUpdateFlowGroup;
                var flowGroups = this.flowGroups;
                var flowGroupLen = flowGroups.length;
                this.getNodeGroupData().then(function () {
                    if (isUpdateFlowGroup) {
                        nodeGroupForm.preFunId = currentNodeGroup.preFunId;
                        nodeGroupForm.nextFunId = currentNodeGroup.nextFunId;
                        nodeGroupForm.isShow = currentNodeGroup.isShow ? '1' : '0';
                        nodeGroupForm.remarks = currentNodeGroup.remarks;
                        nodeGroupForm.name = currentNodeGroup.name;
                        nodeGroupForm.id = currentNodeGroup.id;
                        nodeGroupForm.iconUrl = self.setIconUrl(currentNodeGroup.iconUrl, '/tool');
                    } else {
                        nodeGroupForm.preFunId = flowGroupLen > 0 ? currentNodeGroup.id : self.startNodeEvent.id;
                        nodeGroupForm.nextFunId = self.endNodeEvent.id;
                    }
                });
                this.isFlowGroupVisible = true;
            },
            //获取组
            getNodeGroupData: function () {
                var url = this.getNodeGroupUrl();
                var xhr = $.get(url);
                var self = this;
                xhr.success(function (data) {
                    if (data.status) {
                        var nodeData = data.datas;
                        self.parentName = nodeData.parentName;
                        self.groupName = nodeData.groupName;
                        self.prevNodes = nodeData.pregnodes;
                        self.nextNodes = nodeData.nextgnodes;
                        self.forms = nodeData.forms;
                        self.roles = nodeData.roles;
                        self.nodeTypes = nodeData.nodeTypes;
                        self.nodeGroupForm.groupId = nodeData.groupId;
                        self.nodeGroupForm.parent.id = nodeData.parentId;
                        self.endNodeEvent = nodeData.end;
                        self.startNodeEvent = nodeData.start;
                    }
                });
                return xhr;
            },

            //返回级别string
            getNodeGroupUrl: function () {
                var parentId;
                var url;
                //判断是否是rootNodeGroup
                parentId = this.rootNode.id;
                url = '/a/actyw/gnode/query/' + this.projectFlowId + '?parentId=' + parentId;
                return url;
            },
            //获取节点Node
            getNode: function (id, nodeList) {
                var node;
                $.each(nodeList, function (item, idx) {
                    if (item.id === id) {
                        node = item;
                        return false;
                    }
                });
                return node;
            },

            //获取节点列表
            getNodeList: function () {
                var self = this;
                var groupId = $('#groupId').val();
                var xhr = $.get(('/a/actyw/gnode/queryTree/' + groupId));
                var nodeList;
                var flowNodes = [];
                var flowGroups = [];
                var connections = [];
                var otherNodes = [];
                var flowGroupLen;
                var dataList;
                var $win = $(window);
                this.projectFlowId = groupId;
                xhr.success(function (data) {
                    if (data.status) {
                        dataList = data.datas;
                        nodeList = dataList.lists;
                        self.rootNodeGroup = dataList.group;
                        self.rootNode = dataList.root;
                        otherNodes.push(dataList.rootStart);
                        otherNodes.push(dataList.rootEnd);

                        $.each(nodeList, function (index, item) {
                            var subs = [];
                            if (item.node.level == 1 && item.parentId == 1) {
                                //组
                                flowGroups.push(item);
                            } else if (item.node.level == 2) {
                                //流程节点
                                flowNodes.push(item);

                            }
                            nodeList.forEach(function (subItem) {
                                if (item.id == subItem.parentId && subItem.parentId != 1) {
                                    subs.push(subItem)
                                }
                            });
                            item['subs'] = subs;
                        });

                        self.nodeList = nodeList;
                        self.flowGroups = flowGroups;
                        self.flowNodes = flowNodes;
                        self.otherNodes = otherNodes;
                        flowGroupLen = flowGroups.length;

                        if (flowGroupLen > 1) {
                            flowGroups.forEach(function (item) {
                                connections.push({
                                    target: 'jtkNode' + item['id'] + 'TopCenter',
                                    source: 'jtkNode' + item.preFunId + 'BottomCenter',
                                    scope: 'scope' + item['id']
                                });
                            })
                        }

                        nodeList.forEach(function (item) {
                            if (item['subs'] && item['subs'].length > 0) {
                                item['subs'].forEach(function (subItem, m) {
                                    if (m > 0) {
                                        connections.push({
                                            target: 'jtkNode' + subItem.id + 'TopCenter',
                                            source: 'jtkNode' + subItem.preFunId + 'BottomCenter',
                                            toGroup: subItem.parentId,
                                            scope: 'scope' + subItem['id']
                                        });
                                    }
                                })
                            }
                        });

                        if (flowGroupLen > 0) {
                            connections.push({
                                target: 'jtkNode' + otherNodes[1]['id'] + 'TopCenter',
                                source: 'jtkNode' + flowGroups[flowGroupLen - 1]['id'] + 'BottomCenter',
                                scope: 'scope' + otherNodes[1]['id']
                            });
                            connections.push({
                                target: 'jtkNode' + flowGroups[0]['id'] + 'TopCenter',
                                source: 'jtkNode' + otherNodes[0]['id'] + 'BottomCenter',
                                scope: 'scope' + flowGroups[0]['id']
                            });
                        } else {
                            otherNodes.forEach(function (item, i) {
                                item.posLux = ($win.width() - 64) / 2 - 32;
                                if (i == 0) {
                                    item.posLuy = 10;
                                } else {
                                    item.posLuy = $win.height() - 350
                                }
                            })
                        }

                        self.connections = self.connections.concat(connections);
                        self.dataLoad = true;
                        self.$nextTick(function () {
                            self.initJsPlumb();
                            self.uiDraggable();

                        })
                    }
                });

                return xhr;
            },

            savePosition: function () {
                var $nodes = $('.jtk-node-common');
                var positions = [];
                $nodes.each(function (i, node) {
                    var $node = $(node);
                    positions.push({
                        id: $node.attr('id').replace('jtkNode', ''),
                        posLux: parseInt($node.css('left')),
                        posLuy: parseInt($node.css('top')),
                        width: (function () {
                            return $node.hasClass('jtk-flow-group-node') ? '0' : $node.width()
                        })(),
                        height: $node.height()
                    })
                });
                var group = {
                    groupId: this.projectFlowId,
                    gnodes: positions
                };
                $.ajax({
                    url: '/a/actyw/gnode/updateGpostion',
                    type: 'POST',
                    data: JSON.stringify(group),
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (data) {
                    }
                });
            },

            validateForm: function (scope) {
                return this.$validator.validateAll(scope)
            },

            uiDraggable: function () {
                var startLeft;
                var startTop;
                var parentCls;
                var $ele;
                var eleW;
                var eleH;
                var defaultLeft;
                var defaultTop;
                var idx;
                var flowGroups = this.flowGroups;
                var nodeGroupId;
                var self = this;
                var nextFunId;
                var $helper;
                $('.draggable-handler').draggable({
                    start: function (event, ui) {
                        startLeft = ui.position.left;
                        startTop = ui.position.top;
                        parentCls = $(event.target).parent().attr('class');
                        $ele = $('div[group="' + parentCls.replace('sup-draggable-box draggable-box-', '') + '"]');
                        eleW = $ele.width();
                        eleH = $ele.height();
                        defaultLeft = ui.position.left;
                        defaultTop = ui.position.top;
                        $helper = ui.helper;
                        idx = $(event.target).attr('data-idx');
                        nodeGroupId = parentCls.replace('sup-draggable-box draggable-box-', '');
                        nextFunId = $(event.target).parent().attr('target');
                        self.flowInstance.selectEndpoints({source: $('#jtkNode' + nodeGroupId)[0]}).delete();
                    },
                    drag: function (event, ui) {
                        var width = eleW + ui.position.left - defaultLeft;
                        var height = eleH + ui.position.top - defaultTop;

                        if (width > 200) {
                            flowGroups[idx]['width'] = width;
                        }
                        if (height > 200) {
                            flowGroups[idx]['height'] = height
                        }
                    },
                    stop: function (event, ui) {
                        self._addSourceEndPoints('jtkNode' + nodeGroupId, ['Left']);
                        self._connect(['jtkNode' + nodeGroupId + 'Left', 'jtkNode' + nextFunId + 'TopCenter'], false, 'nextFunId');
                        $helper.css({
                            'left': '',
                            'top': ''
                        })
                    }
                })
            }
        },
        beforeMount: function () {
            this.$validator.updateDictionary(dictionary);
            this.$validator.setLocale('zhCN');
        },

        mounted: function () {
            var self = this;
            this.getNodeList();

            $(".modal").draggable({
                handle: ".modal-title,.modal-header"
            });
        }
    });

    $('#modalAvatar1').on('hide.bs.modal', function () {
        nodeDesignComponent.$data.isUploadIconUrl = false;
    });
    $('#modalAvatar2').on('hide.bs.modal', function () {
        nodeDesignComponent.$data.isUploadNodeFormIconUrl = false;
    });
    $('#modalAvatar3').on('hide.bs.modal', function () {
        nodeDesignComponent.$data.isUploadNodeEditFormIconUrl = false;
    })
</script>
</body>
</html>