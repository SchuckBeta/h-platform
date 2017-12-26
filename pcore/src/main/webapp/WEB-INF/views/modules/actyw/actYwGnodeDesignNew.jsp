<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>流程管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" href="/css/actyw/actYwDesign.css">
    <link rel="stylesheet" type="text/css" href="/static/cropper/cropper.min.css">
    <script type="text/javascript" src="/static/cropper/cropper.min.js"></script>
    <script src="/other/jquery-ui-1.12.1/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript" src="/js/actYwDesign/vue.min.js"></script>
    <script type="text/javascript" src="/js/actYwDesign/modal.component.js"></script>
    <script src="${ctxStatic}/jsPlumb/jsplumb.js" type="text/javascript"></script>
    <script type="text/javascript" src="/js/uploadCutImage.js"></script>
    <style>
        .default-pic-list {
            margin: 0 0 0 0;
            padding: 0 0 0 10px;
            max-height: 450px;
            overflow-x: hidden;
            overflow-y: auto;
        }

        .default-pic-list .pic-item {
            float: left;
            width: 94px;
            height: 94px;
            margin-right: 10px;
        }

        .upload-img-box {
            margin: 10px 0;
        }

        .upload-img-box .img-box {
            height: 292px;
        }

        .upload-content .btn-upload {
            margin-left: 10px;
        }
    </style>
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

            <li>
                <a href="${ctx}/actyw/actYwGnode?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程列表</a>
            </li>
            <li><a href="${ctx}/actyw/actYwGnode/form?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程添加</a>
            </li>
            <%--<li><a href="${ctx}/actyw/actYwGnode/design?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a>--%>
            <%--</li>--%>
            <li class="active"><a
                    href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a>
            </li>
        </ul>
        <p v-if="!dataLoad" class="text-center" style="line-height: 40px; font-weight: bold">数据加载中...</p>
        <div class="flow-header" v-show="dataLoad" style="display: none">
            <div class="pull-right text-right h26">
                <button id="btnReset" class="btn btn-small" type="button" @click="confirmResetFlow">重置流程</button>
                <button type="button" class="btn btn-small" @click="savePosition">保存位置</button>
                <button type="button" class="btn btn-small" @click="openModalFlowNode">创建节点</button>
                <button type="button" class="btn btn-small" @click="openModalFlowGroup">创建组</button>
                <a href="${ctx }/actyw/actYwGroup/list" class="btn btn-small">完成</a>
            </div>
            <div class="name">
                {{rootNodeGroup.name}}
            </div>
        </div>
        <div id="flowCanvas" class="flow-canvas hide jtk-surface jtk-surface-nopan"
             :style="{display: !dataLoad ? 'none': 'block'}">
            <div class="jtk-node jtk-node-common jkt-flow-other"
                 v-for="(item, index) in otherNodes"
                 :id="'jtkNode'+item.id"
                 :class="{jktFlowOtherEnd: index===1}"
                 :key="item.id"
                 :ref="item.id"
                 :style="{left: item.posLux + 'px',  top: item.posLuy + 'px'}">
                <div>{{ item.name }}</div>
            </div>
            <div class="jtk-node-common"
                 v-for="(itemGroup, index) in flowGroups"
                 :id="'jtkNode'+itemGroup.id"
                 :class="{'jtk-flow-group-node': !itemGroup.hasGroup, 'jtk-flow-group': itemGroup.hasGroup}"
                 :key="itemGroup.id"
                 :ref="itemGroup.id"
                 :group="itemGroup.id"
                 :data-idx="index"
                 :style="{left: itemGroup.posLux + 'px', top: itemGroup.posLuy + 'px', width: (itemGroup.hasGroup ? (itemGroup.width + 'px') : 'auto' ), height:  itemGroup.height + 'px'}">
                <!--流程节点组-->
                <template v-if="itemGroup.hasGroup">
                    <div class="jtk-flow-group-header">
                        <div class="operations">
                            <a href="javascript:void(0);" class="add" title="添加子节点"
                               @click="openModalNode(itemGroup)"><img
                                    src="/images/add-flownode.png"> </a>
                            <a href="javascript:void(0);" class="edit" title="编辑节点组"
                               @click="upDateModalFlowGroup(itemGroup)"><img
                                    src="/images/edit-flownode.png"> </a>
                            <a @click="controlDeleteFlowGroupModal(itemGroup)" href="javascript:void(0);" class="delete"
                               title="删除节点组"><img
                                    src="/images/delete-flownode.png"></a>
                        </div>
                        <span class="name">{{ itemGroup.name }}</span>
                    </div>
                    <div class="jtk-node jtk-node-common jtk-flow-node" v-if="itemNode.parentId == itemGroup.id"
                         v-for="(itemNode, index) in flowNodes" :id="'jtkNode'+itemNode.id"
                         :key="itemNode.id" :ref="itemNode.id"
                         :style="{left: itemNode.posLux + 'px', top: itemNode.posLuy + 'px'}"
                         @contextmenu="jtkNodeDropMenu(index, itemNode.id, $event)"
                         @mouseleave="dropMenuHide(itemNode.id)">
                        <ul class="jtk-node-dropMenu" v-show="jtkNodeDropMenuIndex === index">
                            <li>
                                <a href="javascript: void(0);" @click="openModalEditNode(itemNode)">修改</a>
                            </li>
                            <li v-show="false">
                                <a href="javascript: void(0);">添加下级</a>
                            </li>
                            <li>
                                <a @click="controlDeleteNodeModal(itemNode)" href="javascript: void(0);">删除</a>
                            </li>
                        </ul>
                        <div class="name">{{ itemNode.name }}</div>
                    </div>
                </template>
                <!--流程节点-->
                <template v-if="!itemGroup.hasGroup">
                    <div class="jtk-flow-group-header">
                        <div class="operations">
                            <a href="javascript:void(0);" class="edit" title="编辑节点组"
                               @click="upDateModalFlowNode(itemGroup)"><img
                                    src="/images/edit-flownode.png"> </a>
                            <a @click="controlDeleteFlowGroupModal(itemGroup)" href="javascript:void(0);" class="delete"
                               title="删除节点组"><img
                                    src="/images/delete-flownode.png"></a>
                        </div>
                        <span class="name">{{ itemGroup.name }}</span>
                    </div>
                </template>
            </div>
            <!--拉大流程组hanlder-->
            <div class="sup-draggable-box"
                 v-for="(itemGroup, index) in flowGroups"
                 v-if="itemGroup.hasGroup"
                 :class="'draggable-box-'+ itemGroup.id"
                 :target="itemGroup.nextFunId"
                 :key="'drag'+ itemGroup.id"
                 :style="{left: itemGroup.posLux + 'px', top: itemGroup.posLuy + 'px', width:  itemGroup.width + 'px', height:  itemGroup.height + 'px'}">
                <span class="draggable-handler" :data-idx="index"><img src="/images/flow-scale.png"></span>
            </div>
        </div>
    </div>


    <flow:flowGroupModal></flow:flowGroupModal>
    <flow:flowNodeModal></flow:flowNodeModal>

    <flow-group-modal :control-modal="controlModalFlowGroup"
                      :group-data.sync="nodeGroupForm"
                      :group-name="groupName"
                      :parent-name="parentName"
                      :prev-nodes="prevNodes"
                      :next-nodes="nextNodes"
                      :control-icon-url="isUploadIconUrl"
                      :save-disabled="controlModalFlowGroupDisabled"
                      :title="flowGroupModalTitle"
                      ref="flowGroupModal"
                      @save-flow-group="saveNodeGroup"
                      :icon-list="iconList"
                      @cancel="clearModalFlowGroup"></flow-group-modal>

    <flow-node-modal :control-modal="isFlowNodeVisible"
                     :node-data.sync="nodeForm"
                     :group-name="groupName"
                     :parent-name="parentName"
                     :prev-nodes="prevNodes"
                     :roles="roles"
                     :forms="forms"
                     img-id="iconNodePic"
                     btn-id="uploadNodeIcon"
                     :save-disabled="isValidateFlowNode"
                     :control-icon-url="isUploadNodeFormIconUrl"
                     :next-nodes="nextNodes" @save-node="saveNode"
                     :title="nodeModalTitle"
                     ref="nodeModalOne"
                     :icon-list="iconList"
                     modal-pic-class="modalPic2"
                     @cancel="clearFlowNode"></flow-node-modal>

    <flow-node-modal :control-modal="isFlowNodeGroupVisible"
                     :node-data.sync="flowNodeGroupForm"
                     :group-name="groupName"
                     :parent-name="parentName"
                     :prev-nodes="prevNodes"
                     modal-type="flowNodeGroup"
                     :flow-type="flowType"
                     :pro-type="proType"
                     :save-disabled="isValidateFlowGroupNode"
                     img-id="iconEditNodePic"
                     btn-id="uploadNodeEditIcon"
                     :control-icon-url="isUploadFlowNodeGroupFormIconUrl"
                     :next-nodes="nextNodes" @save-node="saveFlowNode"
                     :title="flowNodeGroupModalTitle"
                     ref="nodeModalTwo"
                     :icon-list="iconList"
                     modal-pic-class="modalPic3"
                     @cancel="clearFlowNodeGroup"></flow-node-modal>


    <my-modal :show.sync="controlFlowGroup" title="删除节点组" @ok="deleteFlowGroup" @cancel="cancelDeleteFlowGroupModal">
        <div class="text-center" slot="body">
            <p v-show="controlFlowGroup" style="margin: 30px 0; display: none">是否删除节点组</p>
        </div>
    </my-modal>

    <my-modal :show.sync="controlNodeModal" title="删除节点" @ok="deleteNodeModalOk" @cancel="cancelDeleteNodeModal">
        <div class="text-center" slot="body">
            <p v-show="controlNodeModal" style="margin: 30px 0; display: none">是否删除节点？</p>
        </div>
    </my-modal>

    <my-modal :show.sync="controlResetFlowNode" title="是否重置流程节点" @ok="controlResetFlowNodeOk"
              @cancel="controlResetFlowNode = false">
        <div class="text-center" slot="body">
            <p v-show="controlResetFlowNode" style="margin: 30px 0; display: none">是否重置流程节点？</p>
        </div>
    </my-modal>


</div>

<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

<script>


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
                    iconUrl: '',
                    iconImgUrl: ''
                },
                isFlowNodeGroupVisible: false,
                flowNodeGroupForm: {
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
                    iconUrl: '',
                    iconImgUrl: '',
                    hasGateway: false
                },
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
                    width: 200,
                    height: 200,
                    iconUrl: '',
                    iconImgUrl: ''
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
                flowType: '',
                isUploadFlowNodeGroupFormIconUrl: false,
                isUploadNodeFormIconUrl: false,
                isUploadIconUrl: false,
                nodeActionType: '',
                flowNodeActionType: '',
                controlFlowGroup: false,
                controlNodeModal: false,
                controlModalFlowGroup: false,
                deleteModalData: {},
                proType: '',
                controlModalFlowGroupDisabled: true,
                isValidateFlowNode: true,
                isValidateFlowGroupNode: true,
                flowGroupModalTitle: '',
                nodeModalTitle: '',
                flowNodeGroupModalTitle: '',
                controlResetFlowNode: false,
                iconList: []
            }
        },

        computed: {
            httpFtp: function () {
                var ftpPath = $('#ftpPath').val();
                return ftpPath.substring(0, ftpPath.indexOf('/oseasy/'));
            }
        },

        watch: {
            'nodeGroupForm.iconUrl': function (value) {
                this.controlModalFlowGroupDisabled = !(value && this.nodeGroupForm.name)
            },
            'nodeGroupForm.name': function (value) {
                this.controlModalFlowGroupDisabled = !(value && this.nodeGroupForm.iconUrl)
            },
            'nodeForm.iconUrl': function (value) {
                this.isValidateFlowNode = !(value && this.nodeForm.name);
            },
            'nodeForm.name': function (value) {
                this.isValidateFlowNode = !(value && this.nodeForm.iconUrl);

            },
            'flowNodeGroupForm.iconUrl': function (value) {
                this.isValidateFlowGroupNode = !(value && this.flowNodeGroupForm.name);
            },
            'flowNodeGroupForm.name': function (value) {
                this.isValidateFlowGroupNode = !(value && this.flowNodeGroupForm.iconUrl);
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

                this.flowInstance.draggable(jsPlumb.getSelector(".flow-canvas .jtk-flow-group-node"));

                //连接线
                this.connections.forEach(function (item) {
                    var source = item.source;
                    var target = item.target;
                    var uuids = [source, target];
                    self._connect(uuids, item.scope, true);
                });

                this.flowGroups.forEach(function (item) {
                    if (item.hasGroup) {
                        self.flowInstance.addGroup({
                            el: $('#jtkNode' + item.id)[0],
                            id: item.id,
                            constrain: true,
                            anchor: "TopCenter",
                            endpoint: "Blank",
                            droppable: false
                        });
                    }

                });

                this.flowNodes.forEach(function (item) {
                    if (self.getHasGroup(item.parentId)) {
                        self.flowInstance.addToGroup(item.parentId, [$('#jtkNode' + item.id)[0]]);
                    }
                });

                jsPlumb.fire("jsPlumbDemoLoaded", this.flowInstance);


            },

            getHasGroup: function (id) {
                var hasGroup;
                this.flowGroups.forEach(function (item) {
                    if (item.id === id) {
                        hasGroup = item.hasGroup;
                    }
                });
                return hasGroup;
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

            //打开节点modal，并创建
            openModalNode: function (nodeGroup) {
                this.currentNodeGroup = nodeGroup;
                this.nodeActionType = 'create';
                this.nodeModalTitle = '添加流程节点'
                this.getNodeData('create');

            },

            //更新节点
            openModalEditNode: function (node) {
                this.currentNode = node;
                this.nodeActionType = 'update';
                this.nodeModalTitle = '编辑流程节点'
                this.getNodeData('update');
            },

            //获取节点数据promise
            getNodeDataXhr: function (type) {
                //兼容编辑和新增节点
                var url = this.getNodeUrl(type);
                var xhr = $.get(url);
                var self = this;
                var nodeForm = this.nodeForm;
                var currentNode = this.currentNode;

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
                        if (type === 'update') {
                            nodeForm.formId = currentNode.formId;
                            nodeForm.flowGroup = currentNode.flowGroup;
                            nodeForm.iconUrl = currentNode.iconUrl;
                            nodeForm.iconImgUrl = self.setIconUrl(currentNode.iconUrl, '/tool');
                            nodeForm.remarks = currentNode.remarks;
                            nodeForm.name = currentNode.name;
                            nodeForm.id = currentNode.id;
                            nodeForm.posLux = currentNode.posLux;
                            nodeForm.posLuy = currentNode.posLuy;
                        }
                    }
                });
                return xhr;
            },

            //获取及节点数据
            getNodeData: function (type) {
                var self = this;
                var subs, len;
                var nodeForm = this.nodeForm;
                if (type === 'create') {
                    subs = this.currentNodeGroup.subs;
                    len = subs.length;
                }

                this.getNodeDataXhr(type).then(function () {
                    if (type === 'create') {
                        if (subs.length > 0) {
                            nodeForm.preFunId = subs[len - 1].id;
                        } else {
                            nodeForm.preFunId = self.startNodeEvent.id;
                        }
                        nodeForm.nextFunId = self.endNodeEvent.id
                    }
                });
                this.isFlowNodeVisible = true;
            },

            //获取节点链接
            getNodeUrl: function (type) {
                var groupId = this.projectFlowId;
                var id, parentId;
                if (type === 'create') {
                    id = this.currentNodeGroup.id;
                    return '/a/actyw/gnode/query/' + groupId + '?parentId=' + id;
                } else {
                    parentId = this.currentNode.parentId;
                    return '/a/actyw/gnode/query/' + groupId + '?parentId=' + parentId;
                }
            },

            //保存节点
            saveNode: function () {
                var self = this;
                var postNodeXhr;
                var nodeForm = this.nodeForm;
                var $inputFile = $('#iconNodePic').next();
                var nodeTypes = this.nodeTypes;
                var currentNodeGroup = this.currentNodeGroup;
                var subs, subsLen, url, currentNode;

                if (this.nodeActionType === 'create') {
                    subs = currentNodeGroup.subs;
                    subsLen = subs.length;
                    url = '/a/actyw/gnode/saveProcess';
//                    nodeForm.iconUrl = $inputFile.val();
                }

                if (this.nodeActionType === 'update') {
                    currentNode = this.currentNode;
                    url = '/a/actyw/gnode/updateProcess/';
//                    nodeForm.iconUrl = $inputFile.size() > 0 ? $inputFile.val() : currentNode.iconUrl;
                    nodeForm.groupId = this.projectFlowId;
                    nodeForm.parent.id = currentNode.parentId;
                }

                nodeForm.nodeId = nodeTypes[1];
                nodeForm.hasGroup = false;

                nodeForm.nextFunGnode.id = nodeForm.nextFunId;
                nodeForm.nextFunGnode.node.id = nodeTypes[1];
                nodeForm.preFunGnode.id = nodeForm.preFunId;
                nodeForm.preFunGnode.node.id = nodeTypes[0];


                if (nodeForm.iconUrl) {
                    if (this.nodeActionType === 'create') {
                        if (subsLen > 0) {
                            nodeForm.posLux = subs[0].posLux;
                            nodeForm.posLuy = parseInt(subs[subsLen - 1].posLuy) + 40;
                        } else {
                            nodeForm.posLux = 33;
                            nodeForm.posLuy = 40;
                        }
                    }
                    this.isValidateFlowNode = true;
                    postNodeXhr = $.ajax({
                        url: url,
                        type: 'POST',
                        data: JSON.stringify(nodeForm),
                        dataType: 'json',
                        contentType: 'application/json'
                    });
                    postNodeXhr.success(function (res) {
//                        self.nodeActionType === 'create' && self.savePosition();
                        self.isFlowNodeVisible = false;
                        self.isValidateFlowNode = false;
                        location.reload();
                    });
                    postNodeXhr.error(function (error) {
                        console.log(error)
                        self.isValidateFlowNode = false;
                    });
                } else {
                    self.isUploadNodeFormIconUrl = true;
                }
            },

            //添加组
            openModalFlowGroup: function () {
                var flowGroups = this.flowGroups;
                var flowGroupLen = this.flowGroups.length;
                var rootNodeGroup = this.rootNodeGroup;
                this.currentNodeGroup = flowGroupLen > 0 ? flowGroups[flowGroupLen - 1] : rootNodeGroup;
                this.isUpdateFlowGroup = false;
                this.nodeGroupForm.hasGroup = true;
                this.flowGroupModalTitle = '添加流程组'
                this.getNodeGroup();
            },

            //更新流程组
            upDateModalFlowGroup: function (flowGroup) {
                this.currentNodeGroup = flowGroup;
                this.nodeGroupForm.hasGroup = true;
                this.isUpdateFlowGroup = true;
                this.flowGroupModalTitle = '编辑流程组';
                this.getNodeGroup();
            },

            //获取流程组xhr
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
                        nodeGroupForm.iconUrl = currentNodeGroup.iconUrl;
                        nodeGroupForm.iconImgUrl = self.setIconUrl(currentNodeGroup.iconUrl, '/tool');
                        nodeGroupForm.posLux = currentNodeGroup.posLux;
                        nodeGroupForm.posLuy = currentNodeGroup.posLuy;
                    } else {
                        nodeGroupForm.preFunId = flowGroupLen > 0 ? currentNodeGroup.id : self.startNodeEvent.id;
                        nodeGroupForm.nextFunId = self.endNodeEvent.id;
                    }
                });
//                this.isFlowGroupVisible = true;
                this.controlModalFlowGroup = true;
            },

            //获取组数据
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

            //保存组
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
//                    nodeGroupForm.iconUrl = $iconPic.next().size() > 0 ? $iconPic.next().val() : currentNodeGroup.iconUrl;
                } else {
                    url = '/a/actyw/gnode/saveProcess';
//                    nodeGroupForm.iconUrl = $iconPic.next().val();
                    if (flowGroups.length > 0) {
                        $ele = $(this.$refs[flowGroups[flowGroups.length - 1].id]);
                    } else {
                        $ele = $(this.$refs[otherNodes[0].id]);
                    }
                    nodeGroupForm.posLux = ($win.width() - 200) / 2 - 100;
                    nodeGroupForm.posLuy = $ele.position().top + $ele.height() + 40;
                }


                if (nodeGroupForm.iconUrl) {
                    if (!isUpdateFlowGroup) {
                        if (hasGroup) {
                            otherNodes[1].posLuy = parseInt(endPosY) + 250;
                        } else {
                            otherNodes[1].posLuy = parseInt(endPosY) + 80;
                        }
                    }

                    this.controlModalFlowGroupDisabled = true;


                    postNodeGroupXhr = $.ajax({
                        url: url,
                        type: 'POST',
                        data: JSON.stringify(nodeGroupForm),
                        dataType: 'json',
                        contentType: 'application/json'
                    });
                    postNodeGroupXhr.success(function (res) {
                        self.controlModalFlowGroup = false;
                        if (self.flowGroups.length < 1) {
                            self.savePosition().success(function () {
                                location.reload();
                            });
                        } else {
                            location.reload();
                        }
                    });
                    postNodeGroupXhr.error(function (error) {
                        self.controlModalFlowGroupDisabled = true;
                        console.log(error)
                    });
                } else {
                    self.isUploadIconUrl = true;
                }
            },

            //添加流程节点
            openModalFlowNode: function () {
                var flowGroups = this.flowGroups;
                var flowGroupLen = this.flowGroups.length;
                var rootNodeGroup = this.rootNodeGroup;
                this.flowNodeActionType = 'create';
                this.currentNodeGroup = flowGroupLen > 0 ? flowGroups[flowGroupLen - 1] : rootNodeGroup;
                this.flowNodeGroupModalTitle = '添加流程组节点'
                this.getFlowNodeGroup();
            },

            //更新流程组节点
            upDateModalFlowNode: function (flowGroup) {
                this.flowNodeGroupForm.hasGroup = false;
                this.isFlowNodeGroupVisible = true;
                this.currentNodeGroup = flowGroup;
                this.flowNodeActionType = 'update';
//                this.getNodeGroup();
                this.flowNodeGroupModalTitle = '更新流程组节点'
                this.getUpdateFlowNodeGroup();

            },

            //获取流程组节点数据
            getFlowNodeGroup: function () {
                var url = this.getNodeGroupUrl();
                var xhr = $.get(url);
                var self = this;
                var flowNodeGroupForm = this.flowNodeGroupForm;
                var currentNodeGroup = this.currentNodeGroup;
                var flowGroups = this.flowGroups;
                var flowGroupLen = flowGroups.length;
                xhr.success(function (data) {
                    if (data.status) {
                        var nodeData = data.datas;
                        self.parentName = nodeData.parentName;
                        self.groupName = nodeData.groupName;
                        self.prevNodes = nodeData.pregnodes;
                        self.nextNodes = nodeData.nextgnodes;
                        self.nodeTypes = nodeData.nodeTypes;
                        flowNodeGroupForm.groupId = nodeData.groupId;
                        flowNodeGroupForm.parent.id = nodeData.parentId;
                        self.endNodeEvent = nodeData.end;
                        self.startNodeEvent = nodeData.start;
                        self.isFlowNodeGroupVisible = true;
                        flowNodeGroupForm.preFunId = flowGroupLen > 0 ? currentNodeGroup.id : self.startNodeEvent.id;
                        flowNodeGroupForm.nextFunId = self.endNodeEvent.id;
                    }
                })
            },

            //获取流程组节点更新数据
            getUpdateFlowNodeGroup: function () {
                var groupId = this.projectFlowId;
                var parentId = this.currentNodeGroup.id;
                var url = '/a/actyw/gnode/query/' + groupId + '?parentId=' + parentId;
                var xhr = $.get(url);
                var self = this;
                var flowNodeGroupForm = this.flowNodeGroupForm;
                var currentNodeGroup = this.currentNodeGroup;
                var flowGroups = this.flowGroups;
                xhr.success(function (data) {
                    if (data.status) {
                        var nodeData = data.datas;
                        self.parentName = nodeData.parentName;
                        self.groupName = nodeData.groupName;
                        self.prevNodes = nodeData.pregnodes;
                        self.nextNodes = nodeData.nextgnodes;
                        self.nodeTypes = nodeData.nodeTypes;
                        flowNodeGroupForm.groupId = nodeData.groupId;
                        flowNodeGroupForm.parent.id = nodeData.parentId;
                        flowNodeGroupForm.preFunId = currentNodeGroup.subs[0].preFunId;
                        flowNodeGroupForm.nextFunId = currentNodeGroup.subs[0].nextFunId;
                        flowNodeGroupForm.isShow = currentNodeGroup.subs[0].isShow ? '1' : '0';
                        flowNodeGroupForm.remarks = currentNodeGroup.subs[0].remarks;
                        flowNodeGroupForm.name = currentNodeGroup.subs[0].name;
                        flowNodeGroupForm.id = currentNodeGroup.subs[0].id;
                        flowNodeGroupForm.formId = currentNodeGroup.subs[0].formId;
                        flowNodeGroupForm.flowGroup = currentNodeGroup.subs[0].flowGroup;
                        flowNodeGroupForm.iconImgUrl = self.setIconUrl(currentNodeGroup.subs[0].iconUrl, '/tool');
                        flowNodeGroupForm.iconUrl = currentNodeGroup.subs[0].iconUrl;
                        flowNodeGroupForm.posLux = currentNodeGroup.posLux;
                        flowNodeGroupForm.posLuy = currentNodeGroup.posLuy;
                        self.isFlowNodeGroupVisible = true;
                    }
                })
            },

            //保存流程组节点
            saveFlowNode: function () {
                var flowNodeActionType = this.flowNodeActionType;
                if (flowNodeActionType === 'create') {
                    this.saveCreateFlowNode()
                }
                if (flowNodeActionType === 'update') {
                    this.saveUpdateFlowNode();
                }
            },

            //保存新建流程组节点
            saveCreateFlowNode: function () {
                var url, $ele;
                var flowNodeGroupForm = this.flowNodeGroupForm;
                var $inputFile = $('#iconEditNodePic').next();
                var nodeTypes = this.nodeTypes;
                var flowNodeActionType = this.flowNodeActionType;
                var self = this;
                var flowGroups = this.flowGroups;
                var otherNodes = this.otherNodes;
                var endPosY = this.otherNodes[1].posLuy;
                var $win = $(window);


                if (flowNodeActionType === 'create') {
//                    flowNodeGroupForm.iconUrl = $inputFile.val();
                    url = '/a/actyw/gnode/saveProcess'
                }

                if (!flowNodeGroupForm.iconUrl) {
                    this.isUploadFlowNodeGroupFormIconUrl = true;
                    return false;
                }

                var nodeFormId = flowNodeGroupForm.formId;
                flowNodeGroupForm.nodeId = nodeTypes[0];
                flowNodeGroupForm.hasGroup = false;
                flowNodeGroupForm.nextFunGnode.id = flowNodeGroupForm.nextFunId;
                flowNodeGroupForm.nextFunGnode.node.id = nodeTypes[0];
                flowNodeGroupForm.preFunGnode.id = flowNodeGroupForm.preFunId;
                flowNodeGroupForm.preFunGnode.node.id = nodeTypes[0];
                if (flowGroups.length > 0) {
                    $ele = $(this.$refs[flowGroups[flowGroups.length - 1].id]);
                } else {
                    $ele = $(this.$refs[otherNodes[0].id]);
                }
                flowNodeGroupForm.posLux = ($win.width() - 200) / 2 - 100;
                flowNodeGroupForm.posLuy = $ele.position().top + $ele.height() + 40;

                otherNodes[1].posLuy = parseInt(endPosY) + 80;

                this.isValidateFlowGroupNode = true;

                flowNodeGroupForm.formId = this.$refs['nodeModalTwo'].$refs['option'+ nodeFormId][0].getAttribute('data-list-id');
                var ajaxXhr = $.ajax({
                    url: url,
                    type: 'POST',
                    data: JSON.stringify(flowNodeGroupForm),
                    dataType: 'json',
                    contentType: 'application/json'
                });

                ajaxXhr.success(function (data) {
                    if (data.status) {
                        flowNodeGroupForm.nodeId = nodeTypes[1];
                        flowNodeGroupForm.parent.id = data.datas.id;
                        var childData = data.datas.childGnodes;
                        childData.forEach(function (item) {
                            switch (item.nodeId) {
                                    //开始
                                case '990211':
                                    flowNodeGroupForm.preFunId = item.id;
                                    flowNodeGroupForm.preFunGnode.id = item.id;
                                    flowNodeGroupForm.preFunGnode.node.id = nodeTypes[0];
                                    flowNodeGroupForm.preFunGnode.node.type = item.node.type;
                                    break;
                                    //结束
                                case '990911':
                                    flowNodeGroupForm.nextFunId = item.id;
                                    flowNodeGroupForm.nextFunGnode.id = item.id;
                                    flowNodeGroupForm.nextFunGnode.node.id = nodeTypes[1];
                                    flowNodeGroupForm.nextFunGnode.node.type = item.node.type;
                                    break;
                            }
                        });
                        flowNodeGroupForm.formId = nodeFormId;
                        $.ajax({
                            url: url,
                            type: 'POST',
                            data: JSON.stringify(flowNodeGroupForm),
                            dataType: 'json',
                            contentType: 'application/json',
                            success: function (data) {
                                self.isFlowNodeGroupVisible = false;
                                self.isValidateFlowGroupNode = false;
                                if (self.flowGroups.length < 1) {
                                    self.savePosition().success(function () {
                                        location.reload();
                                    });
                                } else {
                                    location.reload();
                                }

                            },
                            error: function (data) {
                                self.isValidateFlowGroupNode = false;
                            }
                        })
                    }

                })
            },

            //保存更新流程组节点
            saveUpdateFlowNode: function () {
                var url = '/a/actyw/gnode/updateProcess';
                var flowNodeGroupForm = this.flowNodeGroupForm;
                var $inputFile = $('#iconEditNodePic').next();
                var nodeTypes = this.nodeTypes;
                var currentNodeGroup = this.currentNodeGroup;
                var self = this;
                var nodeFormId = flowNodeGroupForm.formId;

//                flowNodeGroupForm.iconUrl = $inputFile.size() > 0 ? $inputFile.val() : currentNodeGroup.iconUrl;
                flowNodeGroupForm.nodeId = nodeTypes[0];
                flowNodeGroupForm.hasGroup = false;
                flowNodeGroupForm.nextFunGnode.id = flowNodeGroupForm.nextFunId;
                flowNodeGroupForm.nextFunGnode.node.id = nodeTypes[0];
                flowNodeGroupForm.preFunGnode.id = flowNodeGroupForm.preFunId;
                flowNodeGroupForm.preFunGnode.node.id = nodeTypes[0];
                flowNodeGroupForm.parent.id = 1;
                flowNodeGroupForm.id = currentNodeGroup.id;
                self.isValidateFlowGroupNode = true;

                flowNodeGroupForm.formId = this.$refs['nodeModalTwo'].$refs['option'+ nodeFormId][0].getAttribute('data-list-id');
                var ajaxXhr = $.ajax({
                    url: url,
                    type: 'POST',
                    data: JSON.stringify(flowNodeGroupForm),
                    dataType: 'json',
                    contentType: 'application/json'
                });

                ajaxXhr.success(function (data) {
                    if (data.status) {
                        flowNodeGroupForm.id = currentNodeGroup.subs[0].id;
                        flowNodeGroupForm.nodeId = nodeTypes[1];
                        flowNodeGroupForm.parent.id = currentNodeGroup.subs[0].id;
                        flowNodeGroupForm.nextFunGnode.id = currentNodeGroup.subs[0].nextFunId;
                        flowNodeGroupForm.nextFunGnode.node.id = nodeTypes[0];
                        flowNodeGroupForm.preFunGnode.id = currentNodeGroup.subs[0].preFunId;
                        flowNodeGroupForm.preFunGnode.node.id = nodeTypes[0];
                        flowNodeGroupForm.formId = nodeFormId;
                        var ajaxNodeXhr = $.ajax({
                            url: url,
                            type: 'POST',
                            data: JSON.stringify(flowNodeGroupForm),
                            dataType: 'json',
                            contentType: 'application/json'
                        });


                        ajaxNodeXhr.success(function (data) {
                            self.isFlowNodeGroupVisible = false;
                            self.isValidateFlowGroupNode = false;
                            location.reload();
                        })
                        ajaxNodeXhr.error(function (data) {
                            self.isValidateFlowGroupNode = false;
                        })
                    }
                })
            },

            //获取流程组链接
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

                        nodeList = dataList.lists.slice(0);
                        self.rootNodeGroup = dataList.group;
                        self.rootNode = dataList.root;
                        otherNodes.push(dataList.rootStart);
                        otherNodes.push(dataList.rootEnd);

                        $.each(dataList.lists, function (index, item) {
                            var subs = [];
                            if (item.node) {
                                if (item.node.level == 1 && item.parentId == 1) {
                                    //组
                                    flowGroups.push(item);
                                } else if (item.node.level == 2) {
                                    //流程节点
//                                    flowNodes.push(item);

                                }
                            }

                            dataList.lists.forEach(function (subItem, i) {
                                if (item.id == subItem.parentId && subItem.parentId != 1) {
                                    subs.push(subItem)
                                }
                                if (item.id === subItem.parentId && item.hasGroup && subItem.parentId != 1) {
                                    flowNodes.push(subItem)
                                }
                            });
                            item['subs'] = subs;
                        });


                        self.nodeList = nodeList;
                        self.flowGroups = flowGroups;
                        self.flowNodes = flowNodes;
                        self.otherNodes = otherNodes;
                        self.flowType = data.datas.group.flowType;
                        self.proType = data.datas.group.type;
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

            //获取图片地址
            getIconList: function () {
                var self = this;
                var xhr = $.get('/a/attachment/sysAttachment//ajaxIcons/gnode');
                xhr.success(function (data) {
                    self.iconList = data
                })
            },

            //关闭流程组并清空
            clearModalFlowGroup: function () {
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
                this.controlModalFlowGroup = false;
            },

            //关闭流程节点
            clearFlowNode: function () {
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

            //关闭流程组节点
            clearFlowNodeGroup: function () {
                this.flowNodeGroupForm = {
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
                    iconUrl: '',
                    hasGateway: false
                };
                this.isFlowNodeGroupVisible = false;
            },

            //控制删除节点组
            controlDeleteFlowGroupModal: function (itemGroup) {
                this.controlFlowGroup = true;
                this.deleteModalData = itemGroup;
            },

            deleteFlowGroup: function () {
                var url = '/a/actyw/gnode/delete/' + this.deleteModalData.id;
                var deleteXhr = $.get(url);
                var self = this;
                deleteXhr.success(function (data) {
                    if (data.status) {
                        location.reload();
                    } else {
                        console.log('删除失败')
                    }
                    self.controlFlowGroup = false;
                })

                deleteXhr.error(function (error) {

                })
            },


            //取消删除节点组
            cancelDeleteFlowGroupModal: function () {
                this.controlFlowGroup = false;
                this.deleteModalData = {};
            },

            //控制删除节点modal
            controlDeleteNodeModal: function (itemNode) {
                this.controlNodeModal = true;
                this.deleteModalData = itemNode;
            },
            //删除节点
            deleteNodeModalOk: function () {
                var url = '/a/actyw/gnode/delete/' + this.deleteModalData.id;
                var deleteXhr = $.get(url);
                var self = this;
                deleteXhr.success(function (data) {
                    if (data.status) {
                        location.reload();
                    } else {
                        console.log('删除失败')
                    }
                    self.controlNodeModal = false;
                })

                deleteXhr.error(function (error) {

                })
            },
            //取消删除节点
            cancelDeleteNodeModal: function () {
                this.controlNodeModal = false;
                this.deleteModalData = {};
            },

            confirmResetFlow: function () {
                this.controlResetFlowNode = true
            },

            //重置流程
            resetFlow: function () {
                $.ajax({
                    type: 'post',
                    url: '${ctx}/actyw/gnode/reset/' + this.projectFlowId,
                    dataType: "json",
                    data: {},
                    success: function (data) {
                        if (data.status) {
                            location.reload();
                        }
                    }
                });
            },

            controlResetFlowNodeOk: function () {
                this.resetFlow();
            },

            //保存位置
            savePosition: function () {
                var $nodes = $('.jtk-node-common');
                var positions = [];
                var posXhr;

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
                return posXhr = $.ajax({
                    url: '/a/actyw/gnode/updateGpostion',
                    type: 'POST',
                    data: JSON.stringify(group),
                    dataType: 'json',
                    contentType: 'application/json'
                });
            },

            //设置流程组可以拖动放大
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
            this.getIconList()
        },

        mounted: function () {
            var self = this;
            this.getNodeList();

            $(".modal").draggable({
                handle: ".modal-title,.modal-header"
            });
        }
    });
</script>
</body>
</html>