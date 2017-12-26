<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>流程管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <script type="text/javascript" src="/js/actYwDesign/vue.min.js"></script>
    <script type="text/javascript" src="/js/actYwDesign/modal.component.js"></script>
    <script src="${ctxStatic}/jsPlumb/jsplumb.js" type="text/javascript"></script>

    <style type="text/css">
        #nodeDesign select {
            height: 30px;
        }
        .form-horizontal .control-label{
            padding-top: 5px;
        }
        .form-horizontal .control-static{
            height: 30px;
            display: block;
            line-height: 30px;
        }
        .form-search input, select{
            max-width: 200px;
        }

        .flow-canvas {
            position: relative;
        }

        .flow-canvas .node {
            position: absolute;
            width: 202px;
            border: 1px solid #ccc;
            border-radius: 5px;
            overflow: hidden;
        }

        .flow-canvas .node .title {
            line-height: 2;
            font-size: 14px;
            font-weight: 700;
            margin-bottom: 8px;
            color: #333;
            background-color: #ffcc99;
            border-bottom: 1px solid #ccc;
            text-align: center;
        }

        .flow-canvas .node .manager {
            margin:0 8px 8px 8px;
            font-size: 12px;
        }

        .modal {
            display: block;
        }

        .modal-transition {
            transition: all .6s ease;
        }

        .modal-leave {
            border-radius: 1px !important; /* 样式没什么用，但可以让根标签的transitionEnd生效，以去掉modal-leave */
        }

        .modal-transition .modal-dialog, .modal-transition .modal-backdrop {
            transition: all .5s ease;
        }

        .modal-enter .modal-dialog, .modal-leave .modal-dialog {
            opacity: 0;
            transform: translateY(-30%);
        }

        .modal-enter .modal-backdrop, .modal-leave .modal-backdrop {
            opacity: 0;
        }
        .modal-body{
            max-height: 600px;
        }

        .label {
            background-color: transparent;
        }

        .hot-box {
            position: absolute;
            left: 0;
            top: 0;
            overflow: visible;
        }

        .hotbox .state {
            display: none;
            margin-left: 100px;
            position: absolute;
            overflow: visible;
        }

        .hotbox .state.active {
            display: block;
        }

        .hotbox .state .ring-shape {
            position: absolute;
            left: -25px;
            top: -25px;
            border: 25px solid rgba(0, 0, 0, .3);
            border-radius: 100%;
            box-sizing: content-box;
        }

        .hotbox .state .button {
            background: #F9F9F9;
            overflow: hidden;
            cursor: default;
        }

        .hotbox .state .button.enabled {
            background-color: #fff;
        }

        .hotbox .state .button.enabled.selected {
            -webkit-animation: selected .1s ease;
            background: #e45d5c;
        }

        .hotbox .state .button.enabled.selected .label {
            color: #fff;
        }

        .hotbox .state .button.enabled.selected .key {
            color: #fadfdf;
        }

        .hotbox .state .button.enabled .key, .hotbox .state .button.enabled .label {
            opacity: 1;
        }

        .hotbox .state .center .button, .hotbox .state .ring .button {
            position: absolute;
            width: 70px;
            height: 70px;
            margin-left: -35px;
            margin-top: -35px;
            border-radius: 100%;
            box-shadow: 0 0 30px rgba(0, 0, 0, .3);
        }

        .hotbox .state .button .key, .hotbox .state .button .label {
            opacity: .3
        }

        .hotbox .state .center .key, .hotbox .state .ring .key {
            opacity: .3;
        }

        .hotbox .state .center .key, .hotbox .state .ring .key {
            font-size: 12px;
            color: #999;
        }

        .hotbox .state .center .key, .hotbox .state .center .label, .hotbox .state .ring .key, .hotbox .state .ring .label {
            display: block;
            text-align: center;
            line-height: 1.4em;
            vertical-align: middle;
        }

        .hotbox .state .center .label, .hotbox .state .ring .label {
            font-size: 16px;
            margin-top: 17px;
            color: #000;
            font-weight: 400;
            line-height: 1em;
        }

        .hotbox label {
            display: inline;
            padding: .2em .6em .3em;
            font-size: 75%;
            color: #fff;
            border-radius: .25em;
        }

        .km-editor > .receiver {
            position: absolute;
            background: #fff;
            outline: 0;
            box-shadow: 0 0 20px rgba(0, 0, 0, .5);
            left: 0;
            top: 0;
            padding: 3px 5px;
            margin-left: -3px;
            margin-top: -5px;
            max-width: 300px;
            width: auto;
            font-size: 14px;
            line-height: 1.4em;
            min-height: 1.4em;
            box-sizing: border-box;
            overflow: hidden;
            word-break: break-all;
            word-wrap: break-word;
            border: none;
            -webkit-user-select: text;
            pointer-events: none;
            opacity: 0;
            z-index: -1000;
        }

        .km-editor > .receiver.input {
            pointer-events: all;
            opacity: 1;
            z-index: 999;
            background: #fff;
            outline: 0;
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

<div id="nodeDesign" class="container-fluid km-editor" @click="hideHotBox($event)">
    <div class="mybreadcrumbs">
        <span>流程</span>
    </div>
    <div class="content_panel">
        <ul class="nav nav-tabs">
            <li><a href="${ctx}/actyw/actYwGnode?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程列表</a></li>
            <li><a href="${ctx}/actyw/actYwGnode/form?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程添加</a></li>
            <li class="active"><a href="${ctx}/actyw/actYwGnode/design?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计</a></li>
            <li><a href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGnode.group.id}&groupId=${actYwGnode.group.id}">流程设计New</a></li>
        </ul>
        <div class="text-right">
            <button type="button" class="btn" @click="savePosition">保存位置</button>
            <a href="${ctx }/actyw/actYwGroup/list" class="btn">完成</a>
        </div>
        <div id="flowCanvas" @contextmenu="contextmenu" class="flow-canvas jtk-surface jtk-surface-nopan"
             style="min-height: 1000px;">
            <div :id="nodeStart.id" class="node jkt-node jkt-node-start" :style="firstStyle"
                 :ref="nodeStart.id" :next-fun-id="nodeStart.id" :prev-fun-id="nodeStart.id"
                 @mousedown.right="showHotBox(nodeStart,'start')">
                <p class="title">{{nodeStart.name}}</p>
                <div class="manager"><span>审核人：</span>{{nodeStart.author}}</div>
            </div>
            <div class="node jkt-node" v-for="(item,index) in nodeList" :key="item.id" :id="item.id" :ref="item.id"
                 v-if="hasNodeList"
                 :next-fun-id="item.nextFunId" :prev-fun-id="item.preFunId"
                 :style="{left:(item.posAlux+ 'px'),top: (item.posAluy) + 'px'}" @mousedown.right="showHotBox(item)">
                <div v-if="item.node">
                    <p class="title">{{item.node.name}}</p>
                    <div class="manager" v-if="item.form"><span>审核表单：</span>{{item.form.name | cutFormName}}</div>
                    <div class="manager" v-if="item.role"><span>审核人：</span>{{item.role.name}}</div>
                </div>
            </div>
        </div>
    </div>
    <div class="hotbox" @contextmenu="contextmenu">
        <div class="state main" :class="{active: hotBoxVisible}"
             :style="{left: stateOffsetLeft, top: stateOffsetTop}">
            <div class="ring-shape">
                <div class="center">
                    <div class="button selected" :class="{enabled: isEditEnabled}" @click="editLabel($event)">
                        <span class="label">编辑</span>
                        <span class="key">F2</span>
                    </div>
                </div>
                <div class="ring">
                    <div class="button" style="left: -3px; top: -90px;"><span
                            class="label">前移</span><span
                            class="key">Alt+Up</span></div>
                    <div class="button" @click="addChildNodeLabel($event)" :class="{enabled: isAddChildEnabled}"
                         style="left: 77.9423px; top: -45px;"><span class="label">下级</span><span
                            class="key">Tab</span></div>

                    <div class="button"
                         @click="addSameNodeLabel($event)" :class="{enabled: false}"  <%--isAddSameEnabled--%>
                         style="left: 77.9423px; top: 45px;"><span
                            class="label">同级</span><span
                            class="key">Enter</span></div>
                    <div class="button" style="left:-3px; top: 90px;"><span
                            class="label">后移</span><span
                            class="key">Alt+Down</span></div>
                    <div class="button"
                         @click="deleteNodeLabel($event)" style="left: -77.9423px; top: 45px;"><span
                            class="label">删除</span><span
                            class="key">Delete</span>
                    </div>
                    <div class="button" style="left: -77.9423px; top: -45px;"><span class="label">上级</span><span
                            class="key">Shift+Tab</span></div>
                </div>
            </div>
        </div>
    </div>
    <my-modal title="创建流程节点" :show.sync="createNodeShow" @ok="createNodeOk" @cancel="createNodeCancel"
              ok-text="确定" cancel-text="取消" :large="true">
        <div class="modal-body" slot="body" @contextmenu="contextmenu">
            <form class="form-horizontal" :form="formNode">
                <div class="control-group">
                    <label class="control-label">自定义流程：</label>
                    <div class="controls">
                        <input type="hidden" v-model="formNode.groupId">
                        <span class="control-static">{{groupName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">流程跟节点：</label>
                    <div class="controls">
                        <input type="hidden" v-model="formNode.parentId">
                        <span class="control-static">{{parentName}}</span>
                    </div>
                </div>
                <div v-show="isShowPrevSelect" class="control-group">
                    <label class="control-label">流程前置业务节点：</label>
                    <div class="controls">
                        <select v-model="formNode.preFunId">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in prevNodes" :value="item.id" :key="item.id">{{item.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <%--<div class="control-group">--%>
                    <%--<label class="control-label">流程业务节点：</label>--%>
                    <%--<div class="controls">--%>
                        <%--<select v-model="formNode.nodeId">--%>
                            <%--<option :value="0">请选择</option>--%>
                            <%--<option v-for="(item,idx) in processNodes" :value="item.id" :key="item.id" >{{item.name}}--%>
                            <%--</option>--%>
                        <%--</select>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="control-group">
                    <label class="control-label">流程业务节点：</label>
                    <div class="controls">
                        <input type="text" class="form-control" v-model="formNode.name">
                    </div>
                </div>
                <div v-show="isShowNextSelect" class="control-group">
                    <label class="control-label">流程后置业务节点：</label>
                    <div class="controls">
                        <select v-model="formNode.nextFunId">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in filterNextNodes" :value="item.id" :key="item.id">{{item.node.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">表单：</label>
                    <div class="controls">
                        <select v-model="formNode.formId">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in formTypes" :value="item.id" :key="item.id">{{item.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">显示：</label>
                    <div class="controls">
                        <label>
                            <input type="radio" v-model="formNode.isShow" :value="1" class="form-control">是
                        </label>
                        <label>
                            <input type="radio" v-model="formNode.isShow" :value="0" class="form-control">否
                        </label>
                    </div>
                </div>
                <div v-show="isShowRuler" class="control-group">
                    <label class="control-label">角色：</label>
                    <div class="controls">
                        <select v-model="formNode.flowGroup">
                            <option :value="0">所有角色</option>
                            <option v-for="(item,idx) in rulers" :value="item.id" :key="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <textarea rows="3" v-model="formNode.remarks"></textarea>
                    </div>
                </div>
            </form>
        </div>
    </my-modal>



    <my-modal title="修改流程节点" :show.sync="updateNodeShow" @ok="updateNodeOk" @cancel="updateNodeCancel"
              ok-text="确定" cancel-text="取消" :large="true">
        <div class="modal-body" slot="body" @contextmenu="contextmenu">
            <form class="form-horizontal" :form="updateFormNode">
                <div class="control-group">
                    <label class="control-label">自定义流程：</label>
                    <div class="controls">
                        <input type="hidden" v-model="updateFormNode.groupId">
                        <span class="control-static">{{groupName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">流程跟节点：</label>
                    <div class="controls">
                        <input type="hidden" v-model="updateFormNode.parentId">
                        <span class="control-static">{{parentName}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">流程业务节点：</label>
                    <div class="controls">
                        <span class="control-static" v-if="currentNode.node">{{currentNode.node.name}}</span>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">表单：</label>
                    <div class="controls">
                        <select v-model="updateFormNode.formId">
                            <option :value="0">-请选择-</option>
                            <option v-for="(item,idx) in formTypes" :value="item.id" :key="item.id">{{item.name}}
                            </option>
                        </select>
                    </div>
                </div>
                <div v-show="!!updateFormNode.flowGroup" class="control-group">
                    <label class="control-label">角色：</label>
                    <div class="controls">
                        <select v-model="updateFormNode.flowGroup">
                            <option :value="0">所有角色</option>
                            <option v-for="(item,idx) in rulers" :value="item.id" :key="item.id">{{item.name}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <textarea rows="3" v-model="updateFormNode.remarks"></textarea>
                    </div>
                </div>
            </form>
        </div>
    </my-modal>
</div>


<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

<script>


    var connectorPaintStyle = {
        strokeWidth: 2,
        stroke: "#61B7CF",
        joinstyle: "round",
        outlineStroke: "white",
        outlineWidth: 2
    };

    var sourceEndpoint = {
        endpoint: "Dot",
        paintStyle: {
            stroke: "#7AB02C",
            fill: "transparent",
            radius: 7,
            strokeWidth: 1
        },
        isSource: true,
        connector: ["Flowchart", {stub: [30, 50], gap: 10, cornerRadius: 5, alwaysRespectStubs: true}],
        connectorStyle: connectorPaintStyle,
        maxConnections: -1,
        dropOptions: {hoverClass: "hover", activeClass: "active"}
    };

    var targetEndpoint = {
        endpoint: "Dot",
        paintStyle: {fill: "#7AB02C", radius: 7},
        maxConnections: -1,
        dropOptions: {hoverClass: "hover", activeClass: "active"},
        isTarget: true
    };

    var nodeDesign = new Vue({
        el: '#nodeDesign',
        data: function () {
            return {
                //节点数组
                nodeList: [],
                //是否有数
                hasNodeList: false,
                //第一个节点
                nodeStart: {
                    group: {
                        id: ''
                    }
                },
                //跟节点数据
                root: {},
//                seMap: {},
                //当前select node
                currentNode: {
                    node: {}
                },
                //弹出modal
                createNodeShow: false,
                //弹出编辑框
                hotBoxVisible: false,
                //定位
                stateOffsetLeft: '',
                stateOffsetTop: '',
                //可编辑
                isEditEnabled: false,
                isAddSameEnabled: false,
                isAddChildEnabled: false,
                nodeTypes: [],
                //表单数据
                formNode: {
                    name: '',
                    node:{
                        id: ''
                    },
                    nodeId: '',
                    remarks: '',
                    flowGroup: 0,
                    isShow: 1,
                    formId: 0,
                    nextFunId: 0,
                    preFunId: 0,
                    groupId: '',
                    hasGateway: false,
                    posAlux: 0,
                    posAluy: 0,
                    parent: {
                        id: ''
                    },
					datas:{}
                },
                //表单数据
                formTypes: [],
                processNodes: [],
                rulers: [],
                node: [],
                nextNodes: [],
                prevNodes: [],
                filterNextNodes: [],
                groupName: '',
                parentName: '',
                startNodeEvent: {},
                endNodeEvent: {},
                isShowPrevSelect: true,
                isShowNextSelect: true,
                isShowRuler: false,
                isStartNode: false,
                isLevelOneNode: false,
                currentEventType: '',
                //修改
                updateNodeShow: false,
                updateFormNode: {
                    id: '',
                    formId: '',
                    flowGroup: '',
                    groupId: '',
                    remarks: ''
                },
                edges: [],
                edgesVertical: []
            }
        },
        filters: {
           cutFormName: function(value){
               if(!value) return '';
               value = value.toString();
               return value.substring(value.indexOf('/')+1)
           }
        },
        computed: {
            flowInstance: function () {
                return jsPlumb.getInstance({
                    DragOptions: {
                        cursor: 'pointer',
                        zIndex: 2000
                    },
                    ConnectionOverlays: [
                        ["Arrow", {
                            location: 1,
                            visible: true,
                            width: 11,
                            length: 11,
                            id: "ARROW",
                            events: {
                                click: function () {
//                                    alert("you clicked on the arrow overlay")
                                }
                            }
                        }]
                    ],
                    Container: 'flowCanvas'
                })
            },
//            edges: function () {
//
//            },

            firstStyle: function () {
                var flowCanvasWidth = this.flowCanvasWidth;
                return {
                    left: ((flowCanvasWidth - 204) / 2) + 'px',
                    top: '50px'
                }
            },

            styles: function () {
                var flowCanvasWidth = this.flowCanvasWidth;
                var styles = [];
                var nodeList = this.nodeList;
                var senNum = this.getSourceNum('1');
                var senFlag = false;
                var senLeft;
                var senInit = 0;
                var leftNum = nodeList.length - senNum;
                var leftLeft;
                var leftINit = 0;
                var leftFlag = false;
                for (var i = 0; i < nodeList.length; i++) {
                    if (nodeList[i]['parentId'] == '1') {
                        if (!senFlag) {
                            senFlag = true;
                            senLeft = (flowCanvasWidth - 204 * senNum - (senNum - 1) * 20) / 2
                        }
                        styles.push({
                            left: (senLeft + (senInit * 224)) + 'px',
                            top: '200px'
                        });
                        senInit++;
                        senNum--;
                    } else {

                        if (!leftFlag) {
                            leftFlag = true;
                            leftLeft = (flowCanvasWidth - 204 * leftNum - (leftNum - 1) * 20) / 2
                        }
                        styles.push({
                            left: 100 + 'px',
                            top: '400px'
                        });
                        leftINit++;
                        leftNum--;
                    }
                }
                return styles
            },

            flowCanvasWidth: function () {
                return $('#flowCanvas').width();
            }
        },
        watch: {},
        methods: {
            _addEndpoints: function (toId, sourceAnchors, targetAnchors) {
                for (var i = 0; i < sourceAnchors.length; i++) {
                    var sourceUUID = toId + sourceAnchors[i];
                    this.flowInstance.addEndpoint(toId, sourceEndpoint, {anchor: sourceAnchors[i], uuid: sourceUUID});
                }
                for (var j = 0; j < targetAnchors.length; j++) {
                    var targetUUID = toId + targetAnchors[j];
                    this.flowInstance.addEndpoint(toId, targetEndpoint, {anchor: targetAnchors[j], uuid: targetUUID});
                }
            },

            _addSourcePoints: function (toId, sourceAnchors, targetAnchors) {
                for (var i = 0; i < sourceAnchors.length; i++) {
                    var sourceUUID = toId + sourceAnchors[i];
                    this.flowInstance.addEndpoint(toId, sourceEndpoint, {anchor: sourceAnchors[i], uuid: sourceUUID});
                }
            },

           getEdges: function () {
               var nodeEdges = [];
               var nodeEdgesV = [];
               var nodeList = this.nodeList;
               for (var i = 0; i < nodeList.length; i++) {
                   var curNodeId = nodeList[i].id;
                   var level = nodeList[i].node.level;
                   if (nodeList[i].parentId == '1') {
                       nodeEdges.push({
                           source: this.nodeStart.id,
                           target: nodeList[i].id,
                           level: level
                       });
                   }

                   for (var j = 0; j < nodeList.length; j++) {
                       var parentId = nodeList[j].parentId;
                       var preFunId = nodeList[j].preFunId;
                       var id = nodeList[j].id;
                       if (level == '2') {
                           if (curNodeId == preFunId) {
                               nodeEdges.push({
                                   source: preFunId,
                                   target: id,
                                   level: level
                               })
                           }
                       } else {
                           if (curNodeId == parentId) {
                               nodeEdges.push({
                                   source: curNodeId,
                                   target: id,
                                   level: level
                               });
                               break;
                           }
                       }
                       if(level == 1){
                           nodeEdgesV.push({
                               source: preFunId,
                               target: id,
                               level: level
                           });
                       }
                   }
               }
               this.edges = nodeEdges;
               this.edgesVertical = nodeEdgesV;
           },

            savePosition: function () {
                var $nodes = $('.jkt-node');
                var positions = [];
                $nodes.each(function (i, node) {
                    var $node = $(node);
                    positions.push({
                        id: $node.attr('id'),
                        posAlux: parseInt($node.css('left')),
                        posAluy: parseInt($node.css('top'))
                    })
                });
                var group = {
                    groupId: this.nodeStart.id,
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

            getNodeList: function () {
                var groupId = $('#groupId').val();
                var xhr = $.get(('/a/actyw/gnode/queryTree/' + groupId));
                var self = this;
                var nodeList;
                xhr.success(function (data) {
                    nodeList = data.datas.lists;
                    self.root = data.datas.root;
                    self.nodeStart = data.datas.group;
                    //有节点
                    if (nodeList.length > 0) {
                        self.hasNodeList = true;
                    }

                    var totalList = [];
//                    var totalSEMap = {};
                    for (var i = 0; i < nodeList.length; i++) {
                        var subs = [];
                        var curNode = nodeList[i];
                        /**
                         * 初始化子节点list
                         */
                        for (var j = 0; j < nodeList.length; j++) {
                            var curSubNode = nodeList[j];
                            if (curNode.id == curSubNode.parentId) {
                                subs.push(curSubNode)
                            }
                        }
                        curNode.subs = subs;
                        totalList.push(curNode);

                        /**
                         * 初始化开始和结束节点map
                         * 只有根节点和子流程节点才有开始和结束节点，即（level=1或0）
                         */
//                        if((curNode.node.level == 0) || (curNode.node.level == 1)){
//                            var curMap = {start:"", end:""};
//                            for (var j = 0; j < subs.length; j++) {
//                                var curSubNode = subs[j];
//                                if (curSubNode.node.nodeKey == 'StartNoneEvent') {
//                                    curMap.start = curSubNode;
//                                }else if (curSubNode.node.nodeKey == 'EndNoneEvent') {
//                                    curMap.end = curSubNode;
//                                }
//                            }
//                            totalSEMap[curNode.id] = curMap;
//                        }
                    }
                    self.nodeList = totalList;
                    self.getEdges();
//                    self.seMap = totalSEMap;
                    //console.log(self.seMap)
                    self.$nextTick(function () {
                        self.flowInstanceBatch();
                        self.updateNodePosition();
                    })
                });
            },

            updateNodePosition: function () {
                var tops = [];
                var $jktNodes = $('.jkt-node');
                $jktNodes.each(function (i, item) {
                    var $item = $(item);
                    var top = parseInt($item.css('top'));
                    tops.push(top);
                });
                var maxTop = Math.max.apply(null, tops);
                var maxIndex = tops.indexOf(maxTop);
                $jktNodes.eq(maxIndex).css('marginBottom', '100px');
            },

            flowInstanceBatch: function () {
                var self = this;
                this.flowInstance.batch(function () {
                    var edges = self.edges;

                    self._addSourcePoints(self.nodeStart.id, ["BottomCenter"], ["TopCenter"]);

                    for (var i = 0; i < self.nodeList.length; i++) {
                        if(self.nodeList[i].node.level == 1){
                            self._addEndpoints(self.nodeList[i].id, ["RightMiddle","BottomCenter"], ["LeftMiddle","TopCenter"]);
                        }else{
                            self._addEndpoints(self.nodeList[i].id, ["BottomCenter"], ["TopCenter"]);
                        }

                    }


                  self.edgesVertical.forEach(function (item) {
                      var source;
                      var target;
                      var uuids;
                      if(item.level == 1){
//                          self._addEndpoints(item.id, ["RightMiddle"], ["LeftMiddle"]);
                          source = item.source + 'RightMiddle';
                          target = item.target + 'LeftMiddle';
                          uuids = [source, target];
                          self.flowInstance.connect({uuids: uuids, detachable:false })
                      }
                  });

                    for (var j = 0; j < edges.length; j++) {
                        var source = edges[j].source + 'BottomCenter';
                        var target = edges[j].target + 'TopCenter';
                        var uuids = [source, target];
                        self.flowInstance.connect({uuids: uuids, detachable:false })
                    }
                    self.flowInstance.draggable(jsPlumb.getSelector(".flow-canvas .jkt-node"), {grid: [20, 20]});
                })
            },

            getSourceNum: function (id) {
                var num = 0;
                var nodeList = this.nodeList;
                for (var i = 0; i < nodeList.length; i++) {
                    if (nodeList[i].parentId == id) {
                        num++;
                    }
                }
                return num;
            },

            contextmenu: function ($event) {
                $event.preventDefault();
            },

            hideHotBox: function ($event) {
                if ($($event.target).parents('.hotbox').size() < 1) {
                    this.hotBoxVisible = false;
                }
            },

            showHotBox: function (item, start) {
                var parentNode;
                var id = item.id;
                var $element;
                var left;
                var top;
                var refId;
                this.currentNode = item;
                this.isStartNode = (start == 'start');




                //是否可编辑同级 起始节点
                if (this.isStartNode) {
                    this.isEditEnabled = false;
                    this.isAddSameEnabled = false;
                    this.isAddChildEnabled = true;
                } else {
                    this.isEditEnabled = true;
                }

                if (this.hasNodeList && !this.isStartNode) {
                    this.isLevelOneNode = (item.node.level == '1' && !this.isStartNode);
                    //是否可编辑同级 1级节点
                    if (item.node.level == '1' && !this.isStartNode) {
                        this.isAddSameEnabled = true;
                        this.isAddChildEnabled = (item.subs.length <= 0);
                    }
                    //是否可编辑 level2 下级节点
                    if (item.node.level == '2') {
                        parentNode = this.getParentNode(item.parentId);
                        this.isAddChildEnabled = true;
                        this.isAddSameEnabled = !(parentNode.subs[0]['id'] == id);
                    }
                }

                refId = id;
                $element = $(this.$refs[refId]);
                left = $element.offset().left;
                top = $element.offset().top;

                this.stateOffsetLeft = (parseInt(left)) + 'px';
                this.stateOffsetTop = (parseInt(top) + 15) + 'px';
                this.hotBoxVisible = true;
            },

            //添加下级的url
            getChildRequestUrl: function(){
                var groupId = this.nodeStart.id;
                var url = '';
                var parentId;
                if (this.isStartNode) {
                    parentId = this.root.id
                } else if (this.isLevelOneNode) {
                    parentId = this.currentNode.id;
                } else {
                    parentId = this.currentNode.parentId;
                }
                url += '/a/actyw/gnode/query/' + groupId + '?parentId=' + parentId;
                return url;
            },

            //同级的url
            getSameRequestUrl: function(){
                var groupId = this.nodeStart.id;
                var parentId;
                var url = '';
                if(this.isLevelOneNode){
                    parentId = this.root.id
                }else{
                    parentId = this.currentNode.parentId;
                }
                url += '/a/actyw/gnode/query/' + groupId + '?parentId=' + parentId;
                return url;
            },

            getAddFormNode: function(url){
                var xhr = $.get(url);
                var self = this;
                xhr.success(function (data) {
                    if (data.status) {
                        var datas = data.datas;
                        self.processNodes = datas.nodes;
                        self.rulers = datas.roles;
                        self.formTypes = datas.forms;
                        self.prevNodes = datas.pregnodes;
                        self.nextNodes = datas.nextgnodes;
                        self.formNode.parent.id = datas.parentId;
                        self.parentName = datas.parentName;
                        self.formNode.groupId = datas.groupId;
                        self.groupName = datas.groupName;
                        self.startNodeEvent = datas.start;
                        self.endNodeEvent = datas.end;
                        self.filterNextNodes = datas.nextgnodes.slice(0);
                        self.nodeTypes = datas.nodeTypes;
//                        self.formNode.id = self.currentNode.id;
                        //更新表单
                        self.updateFormNode.groupId = datas.groupId;
                        self.updateFormNode.id = self.currentNode.id;


                        self.updateFormNode.formId = self.currentNode.formId;
                        self.updateFormNode.flowGroup = self.currentNode.flowGroup;
                        self.updateFormNode.remarks = self.currentNode.remarks;


                        self.setNextFunId();
                        self.setPreFunId();
                    }
                });
            },

            addChildNodeLabel: function (event) {
                var $event = $(event.target);
                var url;
                if(!($event.parent().hasClass('enabled') || $event.hasClass('enabled'))){
                    return false;
                }
                this.currentEventType = 'child';
                url = this.getChildRequestUrl();
                this.getAddFormNode(url);
                this.createNodeShow = true;
            },

            addSameNodeLabel: function (event) {
                var url;
                var $event = $(event.target);
                if(!($event.parent().hasClass('enabled') || $event.hasClass('enabled'))){
                    return false;
                }
                this.currentEventType = 'same';
                url = this.getSameRequestUrl();
                this.getAddFormNode(url);
                this.createNodeShow = true;
            },

            setPreFunId: function () {
                var allLevelNodes;
                var prevNodes = this.prevNodes;

                if(this.currentEventType == 'same'){
                    if(this.isLevelOneNode){
                        allLevelNodes = this.getAllLevelNodes();
                        this.isShowRuler = false;
                        if (allLevelNodes.length > 0) {
                            this.formNode.preFunId = allLevelNodes[allLevelNodes.length - 1]['id'];
                        }
                    }else{
                        this.isShowRuler = true;
                        this.formNode.preFunId = this.currentNode.preFunId;
                    }
                }else if(this.currentEventType == 'child'){
                    if (this.isStartNode) {
                        allLevelNodes = this.getAllLevelNodes();
                        this.isShowRuler = false;
                        if (allLevelNodes.length > 0) {
                            this.formNode.preFunId = allLevelNodes[allLevelNodes.length - 1]['id'];
                        }else{
                            this.formNode.preFunId = this.startNodeEvent.id;
                        }
                    }else if(this.isLevelOneNode) {
                        this.formNode.preFunId = prevNodes[0]['id'];
                        this.isShowRuler = true;
                    }else{
                        this.formNode.preFunId = this.currentNode.id;
                        this.isShowRuler = true;
                    }
                }
            },

            setNextFunId: function () {
                var parentNode;
                var subs;
                var isGetWay = false;
                var curIndex;
                var currentNode = this.currentNode;
                var startEventNode = this.startNodeEvent;
                var nextDisableIds = [];
                //初始和一级节点都为结束为结束无事件节点
                if (this.isStartNode) {
                    this.formNode.nextFunId = this.endNodeEvent.id;
                    this.isShowNextSelect = false;
                } else {
                    if (this.isLevelOneNode) {
                        this.formNode.nextFunId = this.endNodeEvent.id;
//                        this.isShowNextSelect = false;
                    }
                    //level为2 的非第一个节点
                    if (currentNode.node.level == '2') {
                        parentNode = this.getParentNode(currentNode.parentId);
                        subs = parentNode.subs;

                        for (var i = 0; i < subs.length; i++) {
                            if (subs[i].id == currentNode.id) {
                                curIndex = i;
                                nextDisableIds.push(subs[i].id);
                                if (i != subs.length - 1) {
                                    isGetWay = true;
                                    //网关
                                    break;
                                }

                            }else{
                                nextDisableIds.push(subs[i].id)
                            }
                        }

                        this.filterNextNodes = this.filterNextNodes.filter(function(item){
                            return (nextDisableIds.indexOf(item['id']) < 0 && item['id'] !== startEventNode.id)
                        });

                        //有网关
                        this.formNode.hasGateway = isGetWay;
                        this.isShowNextSelect = isGetWay;
                        this.formNode.nextFunId = this.endNodeEvent.id;
                    }
                }
            },


            editLabel: function (event) {
                var url;
                var $event = $(event.target);
                if(!($event.parent().hasClass('enabled') || $event.hasClass('enabled'))){
                    return false;
                }
                url = this.getSameRequestUrl();
                this.getAddFormNode(url);


                this.updateNodeShow =true;
            },

            //设置定位
            setFormNodePos: function(){
                var $flowCanvas =  $('#flowCanvas');
                var $jktNodes;
                var allLevelNodes = this.getAllLevelNodes();
                var maxLeft;
                var maxTop;
                var nodesX = [];
                var nodesY = [];
                if(!this.currentNode.posAlux){
                    $jktNodes = $flowCanvas.find('.jkt-node');
                    if($jktNodes.size() == 1){
                        this.formNode.posAlux = parseInt($jktNodes.eq(0).css('left'));
                        this.formNode.posAluy = parseInt($jktNodes.eq(0).css('top')) + 100;
                    }else{
                        $.each(allLevelNodes,function(i,node){
                            var $node = $('#'+node.id);
                            nodesX.push(parseInt($node.css('left')));
                            nodesY.push(parseInt($node.css('top')));
                        });
                        maxLeft = Math.max.apply(null,nodesX);
                        maxTop = Math.max.apply(null,nodesY);
                        this.formNode.posAlux = maxLeft + 100;
                        this.formNode.posAluy = maxTop + 20;
                    }
                    return false
                }
                if(this.formNode.hasGateway){
                    this.formNode.posAlux = parseInt(this.currentNode.posAlux) + 100;
                    this.formNode.posAluy = this.currentNode.posAluy;
                }else{
                    this.formNode.posAlux = this.currentNode.posAlux;
                    this.formNode.posAluy = parseInt(this.currentNode.posAluy) + 150;
                }
            },

            updateNodeOk: function(){
                var self = this;
                var nodeList = this.nodeList;
                var currentNodeId = this.currentNode.id;
                var rulerName;



                $.ajax({
                    url: '/a/actyw/gnode/updateProcess/',
                    type: 'POST',
                    data: JSON.stringify(this.updateFormNode),
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (data) {
                        self.updateNodeShow =false;
                        window.location.reload();
                    }
                });
            },
            updateNodeCancel: function(){
                this.updateNodeShow =false;
            },


            //添加节点
            createNodeOk: function () {
                var self = this;
                if(this.currentEventType == 'same'){
                    this.formNode.hasGateway = true;
                }
                this.setFormNodePos();
                //console.log(this.formNode)
                //关闭网关
                //return false;
                this.formNode.hasGateway = false;

                console.log(this.isStartNode,this.isLevelOneNode)
                if(this.isStartNode){
                    this.formNode.node.id = this.nodeTypes[0]
                    this.formNode.nodeId = this.nodeTypes[0]
                }else{
                    this.formNode.node.id = this.nodeTypes[1]
                    this.formNode.nodeId = this.nodeTypes[1]
                }
//                return false;
                $.ajax({
                    url: '/a/actyw/gnode/saveProcess',
                    type: 'POST',
                    data: JSON.stringify(this.formNode),
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (data) {
                        self.createNodeShow = false;
                        window.location.reload();
                    }
                });
            },
            createNodeCancel: function () {
                this.createNodeShow = false;
            },
            deleteNodeLabel: function () {

            },
            getAllLevelNodes: function () {
                var nodeList = this.nodeList;
                var allLevelNodes = [];
                for (var i = 0; i < nodeList.length; i++) {
                    if (nodeList[i].node.level == 1) {
                        allLevelNodes.push(nodeList[i])
                    }
                }
                return allLevelNodes;
            },
            getParentNode: function (id) {
                var node;
                for (var i = 0; i < this.nodeList.length; i++) {
                    if (this.nodeList[i].id == id) {
                        node = this.nodeList[i];
                        break;
                    }
                }
                return node;
            }
        },
        beforeMount: function () {
            this.getNodeList()
        },
        mounted: function () {
        },
        updated: function () {

        }
    });


</script>
</body>
</html>