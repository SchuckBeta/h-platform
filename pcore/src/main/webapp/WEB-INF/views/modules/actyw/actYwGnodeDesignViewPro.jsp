<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>流程管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <script type="text/javascript" src="/js/actYwDesign/vue.min.js"></script>
    <script src="${ctxStatic}/jsPlumb/jsplumb.js" type="text/javascript"></script>


    <style type="text/css">

        .flowLevelOne {
            min-width: 120px;
            text-align: center;
        }

        .flowEnd {
            margin-bottom: 60px;
        }

        .rStatus2 {
            border: 1px solid #e9432d;
        }

        .node-title {
            margin: 0;
        }

        .rStatus2 .node-title {
            margin: 0;
            line-height: 30px;
            background-color: #e9432d;
            color: #fff;
        }

        .rStatus0 {
            border: 1px solid #ddd;
        }

        .rStatus0 .node-title {
            margin: 0;
            line-height: 30px;
            background-color: #ddd;
            color: #333;
        }

        .rStatus1 {
            border: 1px solid #ffcc99;
        }

        .rStatus1 .node-title {
            margin: 0;
            line-height: 30px;
            background-color: #ffcc99;
            color: #333;
        }

        .flowStart.rStatus2, .flowEnd.rStatus2 {
            padding: 5px 20px;
            text-align: center;
            border-radius: 50%;
            background-color: #e9432d;
        }

        .flowStart.rStatus1, .flowEnd.rStatus1 {
            padding: 5px 20px;
            text-align: center;
            border-radius: 50%;
            background-color: #e9432d;
        }

        .flowStart.rStatus0, .flowEnd.rStatus0 {
            padding: 5px 20px;
            text-align: center;
            border-radius: 50%;
            background-color: #ddd;
        }

        .flowLevelOne > span {
            line-height: 20px;
            color: #333;
            padding: 0 5px;
        }

        .flowLevelTwo {
            display: inline-block;
            padding: 6px 4px;
            min-width: 60px;
            text-align: center;
            border: 5px solid rgba(128, 128, 128, .3);
            background-color: rgba(255, 204, 153, 0.45);
        }

        .flowLevelTwo > span {
            line-height: 20px;
            color: #333;
            padding: 0 5px;
        }

        .jkt-node-box {
            position: absolute;
        }

        .jktLabel {
            width: 42px;
            height: 20px;
            font-size: 12px;
            border: 1px dashed #ffcc99;
            background-color: rgba(255, 255, 255, .8);
            text-align: center;
            z-index: 2000;
        }

        .legends {
            position: absolute;
            top: 150px;
            right: 45px;
        }

        .legends .legend {
            margin-bottom: 8px;
        }

        .legends .legend > span {
            display: inline-block;
            width: 20px;
            height: 8px;
            margin-right: 5px;
            vertical-align: middle;
        }

        .legends .legend .green {
            background-color: #e9432d;
        }

        .legends .legend .blue {
            background-color: #ffcc99;
        }

        .legends .legend .orange {
            background-color: #ddd;
        }

        .node-flow-group {
            padding: 3px 8px;
        }

        .sub-flow-title {

        }

        .sub-flow-title, .sub-flow-sh {
            margin: 0;
            line-height: 24px;
            padding: 0 10px;
        }

        .sub-node-flow-name, .sub-flow {
            margin: 10px 20px;
        }

        .sub-node-flow-name > p, .sub-flow > p {
            margin: 0;
        }

        .sub-flow.rStatus2 {
            border: 1px solid #e9432d;
        }

        .sub-flow.rStatus1 {
            border: 1px solid #fff;
        }

        .sub-flow.rStatus0 {
            border: 1px solid #ddd;
        }

        .sub-flow.rStatus2 .sub-flow-title {
            background-color: #e9432d;
            color: #fff;
        }

        .sub-flow.rStatus1 .sub-flow-title {
            background-color: #ffcc99;
            color: #333;
        }

        .sub-flow.rStatus0 .sub-flow-title {
            background-color: #ddd;
            color: #333;
        }

        .sub-box + .sub-box {
            margin-top: 60px;
        }

        .jkt-node-box .user-list {
            display: inline-block;
            margin-bottom: 0;
        }

        .jkt-node-box .user-list span, .jkt-node-box .user-list a {
            margin-left: 8px;
        }

        .users-more-container {
            padding-left: 10px;
        }

        .popover {
            display: block;
        }

        .popover-title {
            background-color: #ffcc99;
        }

        .popover .popover-content span {
            display: inline-block;
            white-space: nowrap;
            padding: 0 4px;
        }

        .fade-enter-active, .fade-leave-active {
            transition: opacity .5s
        }

        .fade-enter, .fade-leave-to /* .fade-leave-active in below version 2.1.8 */
        {
            opacity: 0
        }
    </style>

</head>
<body>
<div id="gNodeDesignView">
    <input type="hidden" id="groupId" value="${groupId}">
    <div class="mybreadcrumbs">
        <span>流程</span>
    </div>
    <div class="container-fluid" style="padding-left: 20px;padding-right: 20px;">
        <ul class="nav nav-tabs">
            <li class="active"><a href="javascript:void(0);">${group.name}流程查看</a></li>
        </ul>
        <div id="nodeView" class="node-view" v-if="nodeList.length > 0">
            <jkt-node node-level="0" node-name="开始" :id="startNode.gnode.id" :r-status="startNode.rstatus"
                      :flow-group="false"
                      :has-sub="false"></jkt-node>
            <jkt-node node-level="4" node-name="结束" :id="endNode.gnode.id" :flow-group="false"
                      :pre-id="endNode.gnode.preId"
                      :has-sub="false"
                      :r-status="endNode.rstatus"></jkt-node>
            <jkt-node v-for="(item,idx) in nodeList"
                      :key="item.gnode.id"
                      :ref="item.gnode.id"
                      :id="item.gnode.id"
                      :node-level="item.gnode.node.level"
                      :node-name="item.gnode.node.name"
                      :pre-id="item.gnode.preId"
                      :group-style="item.gnode.style"
                      :flow-group="item.gnode.flowGroup"
                      :node="item"
                      @know-more="knowMore"
                      :has-sub="item.gnode.sub && item.gnode.sub.length > 0"
                      :r-status="item.rstatus"></jkt-node>
        </div>
        <div class="legends">
            <div class="legend"><span class="green"></span>已完成</div>
            <div class="legend"><span class="blue"></span>进行中</div>
            <div class="legend"><span class="orange"></span>未开始</div>
        </div>
        <transition name="fade">
            <div ref="popover" class="popover right" v-show="moreUserShow" :style="userStyle">
                <div class="arrow"></div>
                <h3 class="popover-title">审核人</h3>
                <div class="popover-content">
                    <span v-for="(user, index) in moreUser">{{user.name}}</span>
                </div>
            </div>
        </transition>
    </div>
</div>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

<script>
    //组件
    var jktNode = Vue.component('jkt-node', {
        template: '<div class="jkt-node-box"><div class="jkt-node" :style="groupStyle" :class="jktNodeClass"> <p class="node-title">{{nodeName | replaceXm}}</p>' +
        '<div v-if="hasSub" v-for="(item, idx) in node.gnode.sub" :id="item.gnode.id" class="sub-box">' +
        '<template v-if="node.gnode.hasGroup">' +
        '<div class="sub-flow" :class="{rStatus0: item.rstatus == 0, rStatus1: item.rstatus == 1, rStatus2: item.rstatus == 2}"><p class="sub-flow-title hide">{{item.gnode.name}}</p>' +
        '<div v-if="item.rstatus == 1 && !item.isFront" class="sub-flow-sh">审核人：<p class="user-list"><span>{{item.gnode.role.name}}</span>(<span v-for="(user, index) in userList[idx]">{{user.name}}</span>' +
        '<a v-if="item.gnode.users.length > 2" @click="knowMore(item.gnode.users, $event)" href="javascript: void(0)">查看更多</a> )</p></div></div>' +
        '</template>' +
        '<template v-if="!node.gnode.hasGroup">' +
        '<div v-if="item.rstatus == 1 && !item.isFront" class="sub-node-flow-name"><div>审核人：<p class="user-list"><span>{{item.gnode.role.name}}</span>(<span v-for="(user, index) in userList[idx]">{{user.name}}</span>' +
        '<a v-if="item.gnode.users.length > 2" @click="knowMore(item.gnode.users, $event)"  href="javascript: void(0)">查看更多</a> )</p></div></div>' +
        '</template>' +
        '</div>' +
        '</div></div> ',
        props: {
            hasSub: {
                type: Boolean,
                default: false
            },
            node: {
                type: Object,
                default: function () {
                    return {
                        gnode: {
                            sub: []
                        }
                    }
                }
            },
            nodeLevel: {
                type: [String, Boolean]
            },
            nodeName: {
                type: String
            },
            flowGroup: {
                type: [String, Boolean],
                default: ''
            },
            groupStyle: {
                type: Object,
                default: function () {
                    return {}
                }
            },
            rStatus: {
                type: [String, Number]
            }
        },
        computed: {
            jktNodeClass: function () {
                return {
                    'flowLevelOne': this.nodeLevel === '1',
                    'flowLevelTwo': this.nodeLevel === '2',
//                    'flowLevelThree': this.rStatus == '0' && this.nodeLevel === '2',
//                    'flowLevelGoing': this.nodeLevel === '2' && this.rStatus == '1',
                    'rStatus1': this.rStatus == '1',
                    'rStatus0': this.rStatus == '0',
                    'rStatus2': this.rStatus == '2',
                    'flowStart': this.nodeLevel === '0',
                    'flowEnd': this.nodeLevel === '4',
                    'commonNode': true
                }
            },
            userList: function () {
                var nodeList = this.node.gnode.sub;
                var userList = [];
                nodeList.forEach(function (item) {
                    if (item.gnode.users.length >= 2) {
                        userList.push(item.gnode.users.slice(0, 2));
                    } else {
                        userList.push(item.gnode.users)
                    }
                });
                return userList
            }
        },
        data: function () {
            return {}
        },
        filters: {
            replaceXm: function (val) {
                return val.replace(/(\（项目\）)|(\（大赛\）)/, '')
            }
        },
        methods: {
            knowMore: function (users, $event) {
                var $target = $($event.target);
                var offsetX = $target.offset().left;
                var offsetY = $target.offset().top;
                var pos = {
                    left: (offsetX + 56) + 'px',
                    top: (offsetY + 10) + 'px'
                };
                this.$emit('know-more', [users, pos]);
            }
        }
    });

    //链接点样式


    var pointStyles = {
        complete: {
            endpoint: "Dot",
            paintStyle: {
                stroke: "transparent",
                fill: "transparent",
                radius: 1,
                strokeWidth: 1
            },
            isSource: true,
            connector: ["Flowchart", {stub: [40, 60], gap: 1, cornerRadius: 1, alwaysRespectStubs: true}],
            connectorStyle: {
                strokeWidth: 1,
                stroke: "#e9432d",
                joinstyle: "round",
                outlineStroke: "white",
                outlineWidth: 1
            },
            maxConnections: 1,
            isTarget: true,
            draggable: false,
            dropOptions: {hoverClass: "hover", activeClass: "active"}
        },
        going: {
            endpoint: "Dot",
            paintStyle: {
                stroke: "transparent",
                fill: "transparent",
                radius: 1,
                strokeWidth: 1
            },
            isSource: true,
            connector: ["Flowchart", {stub: [40, 60], gap: 3, cornerRadius: 1, alwaysRespectStubs: true}],
            connectorStyle: {
                strokeWidth: 1,
                stroke: "#ffcc99",
                joinstyle: "round",
                outlineStroke: "white",
                outlineWidth: 1
            },
            maxConnections: 1,
            isTarget: true,
            draggable: false,
            dropOptions: {hoverClass: "hover", activeClass: "active"}
        },
        NotStarted: {
            endpoint: "Dot",
            paintStyle: {
                stroke: "transparent",
                fill: "transparent",
                radius: 1,
                strokeWidth: 1
            },
            isSource: true,
            connector: ["Flowchart", {stub: [40, 60], gap: 1, cornerRadius: 1, alwaysRespectStubs: true}],
            connectorStyle: {
                strokeWidth: 1,
                stroke: "#ddd",
                joinstyle: "round",
                outlineStroke: "white",
                outlineWidth: 1
            },
            maxConnections: 1,
            isTarget: true,
            draggable: false,
            dropOptions: {hoverClass: "hover", activeClass: "active"}
        }
    };


    var gNodeDesignView = new Vue({
        el: '#gNodeDesignView',
        data: function () {
            return {
                nodeList: [],
                snap: 62,
                totalTop: 0,
                startNode: {
                    gnode: {
                        id: ''
                    }
                },
                endNode: {
                    gnode: {
                        id: ''
                    }
                },
                subNodeList: [],
                plaintEdgesArr: [],
                userList: [],
                moreUserShow: false,
                userStyle: {},
                popoverTimerId: null
            }
        },
        computed: {
            flowInstance: function () {
                return jsPlumb.getInstance({
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
//                        ["Label", {
//                            location: 0.2,
//                            id: "label",
//                            cssClass: "jktLabel",
//                            events: {
////                                tap:function() { alert("hey"); }
//                            }
//                        }]
                    ],
                    Container: 'nodeView',
                    draggable: false
                })
            },
            moreUser: function () {
                return this.userList.length ? this.userList.slice(2, this.userList.length > 15 ? 15 : this.userList.length - 1) : [];
            }
        },
        watch: {
            moreUserShow: function (val) {
                var self = this;
                if (!val) {
                    this.popoverTimerId && clearTimeout(this.popoverTimerId);
                } else {
                    this.popoverTimerId && clearTimeout(this.popoverTimerId);
                    this.popoverTimerId = setTimeout(function () {
                        self.moreUserShow = false;
                    }, 2500)
                }
            }
        },
        methods: {
            _addGroup: function (ele, groupId) {
                this.flowInstance.addGroup({
                    el: ele,
                    id: groupId,
                    constrain: false,
                    anchor: "TopCenter",
                    endpoint: "Blank",
                    droppable: false,
                    draggable: false
                });
            },

            _addEndpoints: function (toId, sourceAnchors, targetAnchors, rstatus) {
                for (var i = 0; i < sourceAnchors.length; i++) {
                    var sourceUUID = toId + sourceAnchors[i];
                    var pointStyle;
                    if (rstatus == 2) {
                        pointStyle = pointStyles['complete']
                    } else if (rstatus == 1) {
                        pointStyle = pointStyles['going']
                    } else {
                        pointStyle = pointStyles['NotStarted']
                    }
                    this.flowInstance.addEndpoint(toId, pointStyle, {anchor: sourceAnchors[i], uuid: sourceUUID});
                }
                for (var j = 0; j < targetAnchors.length; j++) {
                    var targetUUID = toId + targetAnchors[j];
                    this.flowInstance.addEndpoint(toId, pointStyle, {anchor: targetAnchors[j], uuid: targetUUID});
                }
            },

            getLineData: function (id) {
                var line;
                var nodeList = this.nodeList;
                for (var i = 0; i < nodeList.length; i++) {
                    if (nodeList[i].gnode.id === id) {
                        line = nodeList[i];
                        break;
                    }
                }
                if (!line) {
                    line = this.startNode.gnode.id === id ? this.startNode : this.endNode;
                }
                return line;
            },

            flowInstanceBatch: function () {
                var self = this;
                this.flowInstance.batch(function () {
                    var startId = self.startNode.gnode.id;
                    var endId = self.endNode.gnode.id;
                    self._addEndpoints(startId, ["BottomCenter"], ["TopCenter"], self.startNode.rstatus);
                    self._addEndpoints(endId, ["BottomCenter"], ["TopCenter"], self.endNode.rstatus);
                    self.nodeList.forEach(function (item, i) {
                        self._addEndpoints(item.gnode.id, ["BottomCenter"], ["TopCenter"], item.rstatus);
                        var sub = item.gnode.sub;
                        if (item.gnode.hasGroup && sub.length) {
                            sub.forEach(function (subItem) {
                                self._addEndpoints(subItem.gnode.id, ["BottomCenter"], ["TopCenter"], subItem.rstatus);
                            })
                        }
                    });
                    self.nodeList.forEach(function (item, i) {
                        var uuids;
                        if (i < self.nodeList.length - 1) {
                            uuids = [item.gnode.id + 'BottomCenter', self.nodeList[i + 1].gnode.id + 'TopCenter'];
                        }
                        if (uuids) {
                            self.flowInstance.connect({uuids: uuids});
                        }
                        var ele = self.$refs[item.gnode.id][0].$el;
                        self._addGroup(ele, item.id);

                        var sub = item.gnode.sub;
                        if (item.gnode.hasGroup && sub.length > 1) {
                            sub.forEach(function (subItem, subI) {
                                if (subI < sub.length - 1) {
                                    var subUuids = [subItem.gnode.id + 'BottomCenter', sub[subI + 1].gnode.id + 'TopCenter'];
                                    self.flowInstance.connect({uuids: subUuids});
                                }
                            })
                        }

                    });
                    self.flowInstance.connect({uuids: [startId + 'BottomCenter', self.nodeList[0].gnode.id + 'TopCenter']});
                    self.flowInstance.connect({uuids: [self.nodeList[self.nodeList.length - 1].gnode.id + 'BottomCenter', endId + 'TopCenter']});

                    //子类添加

                })
            },
            nodePositions: function () {
                var self = this;
                var winWidth = $(window).width();
                var $startNode = $('#' + this.startNode.gnode.id);
                var $endNode = $('#' + this.endNode.gnode.id);
                var $ds = $(this.$refs[this.nodeList[this.nodeList.length - 1].gnode.id][0].$el);
                self.nodeList.forEach(function (item, i) {
                    var $item = $('#' + item.gnode.id);
                    var beforeOffsetTop;
                    var beforeEle;
                    if (i > 0) {
                        beforeEle = $item.prev();
                        beforeOffsetTop = $item.prev().offset().top;
                        $item.css({
                            'top': beforeOffsetTop + self.snap + beforeEle.height(),
                            'left': (winWidth - $item.width()) / 2
                        })
                    } else {
                        $item.css({
                            'top': 237,
                            'left': (winWidth - $item.width()) / 2
                        })
                    }

                });
                $startNode.css({
                    'left': (winWidth - $startNode.width()) / 2
                });
                $endNode.css({
                    'left': (winWidth - $startNode.width()) / 2,
                    'top': $ds.offset().top + self.snap + $ds.height()
                })

            },
            knowMore: function (users) {
                var popover = this.$refs.popover;
                var self = this;
                var userStyle = users[1];
                this.userList = users[0];

                if (!this.moreUserShow) {
                    this.moreUserShow = true;
                }
                setTimeout(function () {
                    userStyle.top = (parseInt(userStyle.top) - $(popover).height() / 2 ) + 'px';
                    self.userStyle = userStyle;
                })

            },
            getNodeList: function () {
                var groupId = $('#groupId').val();
                var url =('/${faUrl}/actyw/actYwGnode/queryStatusTreeByGnode/${groupId}?gnodeId=${gnode.id}&grade=${grade}');
                var xhr = $.get(url);
                var self = this;
                xhr.success(function (data) {
                    if (data.status) {
                        var nodeList = data.datas.slice(0);
                        var fakeNode = [];
                        var filterNodeList;
                        var startNode = data.datas[0];
                        filterNodeList = nodeList.filter(function (item) {
                            return item.gnode.node.nodeType == 'node' && (item.gnode.node.nodeKey != 'EndNoneEvent' && item.gnode.node.nodeKey != 'StartNoneEvent')
                        });

                        $.each(filterNodeList, function (i, item) {
                            if (item.gnode.parentId == 1) {
                                var id = item.gnode.id;
                                item.gnode['sub'] = [];
                                $.each(filterNodeList, function (i2, item2) {
                                    if (item2.gnode.parentId == id) {
                                        item.gnode['sub'].push(item2);
                                        self.subNodeList.push(item2);
                                    }
                                })
                            }
                        });

                        $.each(filterNodeList, function (i, item) {
                            if (item.gnode.parentId == 1) {
                                var sub = item.gnode.sub;
                                var isComplete;
                                if (sub && sub.length) {
                                    if (item.rstatus == '2') {
                                        isComplete = sub.every(function (subItem) {
                                            return subItem.rstatus == '2'
                                        });
                                        if (isComplete) {
                                            item.rstatus = '2';
                                        } else {
                                            item.rstatus = '1';
                                        }
                                    }
                                }
                                if (item.gnode.hasGroup) {
                                    if (sub && sub.length) {
                                        item.gnode.style = {
                                            height: ''
                                        }
                                    }
                                } else {
                                    item.gnode.style = {
                                        height: ''
                                    }
                                }
                                fakeNode.push(item)
                            }
                        });

                        var isJx = fakeNode.every(function (item) {
                            return item.rstatus == '2';
                        });

                        var isSomeStart = fakeNode.some(function (item) {
                            return item.rstatus == '2';
                        });

                        var endNode = data.datas[data.datas.length - 1];

                        if (isJx) {
                            endNode['rstatus'] = '2';
                            self.endNode = endNode;
                        } else {
                            self.endNode = endNode;
                        }

                        if (isSomeStart) {
                            startNode['rstatus'] = 2;
                        }
                        var filterFakeNode = fakeNode.slice(0);
                        filterFakeNode.forEach(function (item) {
                            var sub = item.gnode.sub;
                            var updateSub = [];
                            if(sub.length > 0){
                                updateSub =  sub.filter(function (subItem) {
                                    return subItem.rstatus == 1
                                });
                                item.gnode.sub = updateSub;
                            }
                        });
                        self.startNode = startNode;
                        self.nodeList = filterFakeNode;
                        self.$nextTick(function () {
                            self.nodePositions();
                            self.flowInstanceBatch();
                        })
                    } else {
                        console.log(data)
                    }
                });
                xhr.error(function (error) {
                    console.log(error)
                })
            }
        },
        beforeMount: function () {
            this.getNodeList();
        }
    })

</script>
</body>
</html>