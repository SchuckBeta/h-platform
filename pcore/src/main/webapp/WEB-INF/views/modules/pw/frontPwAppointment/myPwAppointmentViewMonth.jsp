<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${fns:getConfig('productName')}</title>
    <meta name="decorator" content="cyjd-site-default"/>
    <%--<%@include file="/WEB-INF/views/include/treeview.jsp" %>--%>
    <link rel="stylesheet" href="${ctxStatic}/fullcalendar/fullcalendar.min.css">
    <link rel="stylesheet" href="${ctxStatic}/fullcalendar/scheduler.min.css">
    <script src="${ctxStatic}/vue/vue.min.js"></script>
    <script src="${ctxStatic}/fullcalendar/moment.min.js"></script>
    <script src="${ctxStatic}/fullcalendar/fullcalendar.min.js"></script>
    <script src="${ctxStatic}/fullcalendar/scheduler.min.js"></script>
    <script src="${ctxStatic}/fullcalendar/locale/zh-cn.js"></script>
    <style>
        .dialog-cyjd-container .dialog-cyjd {
            padding: 20px 15px;
            min-height: 250px;
            min-width: 320px;
            max-width: 360px;
            text-align: center;
            overflow: hidden;
        }

        .fc-time-grid-event .fc-content {
            margin-top: 10px;
        }

        .cate-check-bar .ch-wrap .ch-block {
            height: 32px;
        }

        .cate-check-bar .ch-wrap .ch-block .input-mini {
            height: auto;
            line-height: 28px;
        }
    </style>
</head>
<body>
<div id="viewWeek" class="container container-ct">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>月视图</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form modelAttribute="pwAppointmentVo" class="form-inline form-appointment"
               action="${ctx}/pw/pwAppointment/viewMonth"
               method="post"
               v-show="isLoad" cssStyle="display: none">
        <div class="cate-attrs">
            <div class="cate-attr-key">
                <span>房间类型</span>
            </div>
            <div class="cate-attr-value">
                <ul class="av-collapse">
                    <li class="av-item av-item-all">
                        <div class="ac-item-box">
                            <input type="checkbox" name="roomTypes" @change="changeRoomType($event)"
                                   v-model="roomTypes" value="all" checked>
                            <label class="avi-label">全部</label>
                        </div>
                    </li>
                    <li class="av-item" v-for="item in roomTypeArr">
                        <div class="ac-item-box">
                            <input type="checkbox" name="roomTypes" :value="item.value"
                                   @change="changeRoomType($event)" checked
                                   v-model="roomTypes">
                            <label class="avi-label">{{item.label}}</label>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div id="buildingsDiv" class="cate-attrs">
            <div class="cate-attr-key">
                <span>楼栋</span>
            </div>
            <div class="cate-attr-value">
                <ul class="av-collapse">
                    <li class="av-item av-item-all">
                        <div class="ac-item-box">
                            <input type="radio" v-model="form.buildingId" name="build" checked value=""
                                   @change="changeBuild">
                            <label class="avi-label">不限</label>
                        </div>
                    </li>
                    <li class="av-item" v-for="build in buildList">
                        <div class="ac-item-box">
                            <input type="radio" v-model="form.buildingId" name="build" :value="build.id"
                                   @change="changeBuild">
                            <label class="avi-label">{{build.name}}</label>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div id="floorsDiv" class="cate-attrs">
            <div class="cate-attr-key">
                <span>楼层</span>
            </div>
            <div class="cate-attr-value">
                <ul class="av-collapse">
                    <li class="av-item av-item-all">
                        <div class="ac-item-box">
                            <input type="radio" name="floor" v-model="form.floorId" checked value=""
                                   @change="changeFloor">
                            <label class="avi-label">不限</label>
                        </div>
                    </li>
                    <li class="av-item" v-for="floor in floorList">
                        <div class="ac-item-box">
                            <input type="radio" v-model="form.floorId" name="floor" :value="floor.id"
                                   @change="changeFloor">
                            <label class="avi-label">{{floor.name}}</label>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div class="cate-check-bar" style="margin-left: -10px;">
            <div class="ch-wrap">
                <div class="ch-block" style="padding:0 10px;">
                    <label style="font-size: 12px;" v-if="item.label === '通过' || item.label === '待审核'"
                           class="checkbox-inline"
                           v-for="(item, index) in pwAppointmentStatus">
                        <input type="checkbox" :value="item.value" v-model="status">{{item.label}}
                    </label>
                </div>
                <div class="ch-block">
                    <span style="line-height: 30px;font-size: 12px;">容纳人数范围：</span>
                    <form:input path="roomNumMin" v-model="form.roomNumMin" class="input-mini"
                                placeholder="最小人数"/>
                    <span>-</span>
                    <form:input path="roomNumMax" v-model="form.roomNumMax" class="input-mini"
                                placeholder="最大人数"/>
                </div>
            </div>
            <div class="pull-right">
                <button type="button" class="btn btn-primary" @click="search">查询</button>
            </div>
        </div>
    </form:form>
    <div class="calendar-container">
        <h3 v-show="isLoad"  class="text-center room-title" style="display: none;margin-bottom: 20px;">{{roomName}}预约及查询</h3>
        <div class="cate-room-bar" v-show="isLoad" style="display: none">
            <div id="roomsDiv" class="cate-room-names" :class="{'cate-room-auto': roomAuto}">
                <a class="cr-item" :class="{active: roomIndex == 'all'}" href="javascript:void (0);"
                   @click="changeRoom('all')">
                    <span></span>全部</a>
                <a class="cr-item" :style="{color: '#333'}" :class="{gray: roomStatus[index].status}"
                   v-for="(room, index) in rooms" :key="room.id"
                   href="javascript:void (0);" @click="changeRoom(room, index)"><span
                        :style="{backgroundColor: '#'+room.color}"></span>{{room.name}}</a>
            </div>
            <a v-show="moreShow" style="display: none" v-more class="cate-room-more" @click="roomAuto = !roomAuto"
               href="javascript:void (0);">更多</a>
        </div>
        <div id="calendar"></div>
    </div>
    <%--<div v-show="searchShow" class="search-tip-container" style="display: none;">--%>
    <%--<div class="search-tip" role="tooltip">查询中...</div>--%>
    <%--<div class="search-layer"></div>--%>
    <%--</div>--%>
</div>


<script>

    +(function ($) {
        var datalist = '${fns:toJson(list)}';
        var viewWeek = new Vue({
            el: '#viewWeek',
            data: function () {
                return {
                    appList: JSON.parse('${fns:toJson(list)}'),
                    appRule: JSON.parse('${fns:toJson(appRule)}'),
                    user: JSON.parse('${fns:toJson(fns:getUser())}'),
                    pwAppointmentStatus: JSON.parse('${fns:toJson(fns:getDictList('pw_appointment_status'))}'),
                    modalCalendarShow: false,
                    roomTypeArr: JSON.parse('${fns:toJson(fns:getDictList('pw_room_type'))}'),
                    roomName: '全部场地',
                    roomTypes: ['all'],
                    dayEventList: [],
                    status: [],
                    form: {
                        roomTypes: '',
                        buildingId: '',
                        floorId: '',
                        roomNumMin: '',
                        roomNumMax: '',
                        status: ''
                    },
                    roomForm: {
                        roomIds: '',
                        status: 0
                    },
                    appointmentForm: {
                        roomName: '',
                        roomId: '',
                        subject: '',
                        personNum: '',
                        startDate: '',
                        endDate: '',
                        remarks: ''
                    },
                    appointmentValidateForm: '',
                    rooms: JSON.parse('${fns:toJson(rooms)}'),
                    roomStatus: [],
                    buildList: [],
                    floorList: [],
                    roomIndex: 'all',
                    calendar: '',
                    eventData: {},
                    currentRoom: {},
                    isSave: false,
                    isLoad: false,
                    roomAuto: false,
                    moreShow: false,
                    backDate: '',
                    searchShow: false
                }
            },
            watch: {
                'appointmentForm.roomId': function () {
                    this.appointmentForm['pwRoom.id'] = this.appointmentForm.roomId;
                },
                'roomTypes': function () {
                    this.form.roomTypes = this.roomTypes.join(',')
                    if(this.isLoad){
                        this.search();
                    }
                },
                'status': function () {
                    this.form.status = this.status.join(',')
                },
                'rooms': function () {
                    this.moreShow = ($('.calendar-container').width() / ($('.cate-room-names .cr-item').eq(0).width() + 10)) < this.rooms.length
                }
            },
            directives: {
                more: {
                    inserted: function (element, binding, vnode) {
                        vnode.context.moreShow = ($('.calendar-container').width() / ($('.cate-room-names .cr-item').eq(0).width() + 10)) < vnode.context.rooms.length
                    }
                }
            },
            computed: {
                eventList: function () {
                    this.appList.forEach(function (t) {
                        switch (t.state) {
                            case '1':  //待审核
                                t.color = '#52c41a';
                                t.textColor = '#fff';
                                break;
                            case '0': //审核通过
                                t.color = '#e9432d';
                                t.textColor = '#fff';
                                break;
                            case '2': //审核拒绝
                                t.color = '#d9d9d9';
                                t.textColor = '#333333';
                                break;
                            default:
                                t.color = '#1890FF';
                                t.textColor = '#fff';
                        }
                    })
                    return this.appList;
                }
            },
            methods: {
                changeRoomType: function ($event) {
                    var val = $event.target.value;
                    var self = this;
                    var isAll = false;
                    if (val === 'all') {
                        if ($event.target.checked) {
                            this.roomTypeArr.forEach(function (t) {
                                if (!(self.roomTypes.indexOf(t.value) > -1)) {
                                    self.roomTypes.push(t.value)
                                }
                            })
                        } else {
                            this.roomTypes.length = 0;
                        }
                    } else {
                        if ($event.target.checked) {
                            isAll = true;
                            if (this.roomTypes.length == this.roomTypeArr.length) {
                                this.roomTypes.forEach(function (t) {
                                    if (!t) {
                                        isAll = false
                                    }
                                })
                                this.roomTypes.push('all')
                            }
                        } else {
                            this.roomTypes.forEach(function (t, i) {
                                if (t == 'all') {
                                    self.roomTypes.splice(i, 1)
                                }
                            })
                        }
                    }

                },
                //获取楼栋
                getBuildList: function () {
                    var self = this;
                    var buildListXhr = $.get('${ctxFront}/pw/pwSpace/jsonList', {type: 3});
                    buildListXhr.success(function (data) {
                        self.buildList = data;
                        self.isLoad = true;
                    })
                    buildListXhr.error(function (error) {
                        self.isLoad = true;
                    })
                },


                //获取楼层
                changeBuild: function () {
                    var self = this;
                    var floorListXhr;
                    this.form.floorId = '';
                    if (!this.form.buildId) {
                        self.floorList.length = 0;
                        if(this.isLoad){
                            this.search();
                        }
                        return false;
                    }
                    floorListXhr = $.get('${ctxFront}/pw/pwSpace/children/' + this.form.buildId);
                    floorListXhr.success(function (data) {
                        self.floorList = data;
                    });
                    floorListXhr.error(function (error) {
                        self.floorList.length = 0;
                    })
                    this.search();

                },

                changeFloor: function () {
                    this.search();
                },
                //查询
                search: function () {
                    var self = this;
                    var xhr = $.get('${ctxFront}/pw/pwAppointment/mouthSearch', this.form);
                    self.searchShow = true;
                    xhr.success(function (data) {
                        self.rooms = data.rooms;
                        self.roomIndex = 'all';
                        self.getRoomStatus();
                        self.changeCalendarData(data)
                    });

                    xhr.error(function () {

                    })

                },

                //房间查询
                changeRoom: function (item, index) {
                    var xhr, roomIds = [], self = this;
                    if (item === 'all') {
                        this.roomName = '全部场地';
                        this.roomStatus.forEach(function (t, i) {
                            t.status = true;
                            roomIds.push(self.rooms[i].id);
                        });
                        this.roomForm.roomIds = roomIds.join(',');
                        this.appointmentForm.roomId = '';
                        this.appointmentForm.roomName = '';
                        this.currentRoom = {};
                        this.roomIndex = 'all';
                    } else {
                        self.roomName = this.rooms[index].path;
                        this.roomStatus.forEach(function (t, i) {
                            t.status = true;
                            if (!t.status && self.rooms[i]) {
                                roomIds.push(self.rooms[i].id);
                            }
                        });
                        this.roomStatus[index].status = !this.roomStatus[index].status;
                        this.roomForm.roomIds = this.rooms[index].id;
                        this.appointmentForm.roomId = this.rooms[index].id;
                        this.appointmentForm.roomName = this.rooms[index].name;
                        this.currentRoom = this.rooms[index];
                        this.roomIndex = index;
                    }


                    if (!this.roomForm.roomIds) {
                        //数据置空，不发送请求
                        return false;
                    }
                    this.roomForm.status = this.form.status;
                    xhr = $.get('${ctxFront}/pw/pwAppointment/mouthSearch', this.roomForm);
                    xhr.success(function (data) {
                        self.changeCalendarData(data)
                    });
                    xhr.error(function (error) {

                    })
                },

                //获取房间状态
                getRoomStatus: function () {
                    var self = this;
                    this.roomStatus.length = 0;
                    this.rooms.forEach(function (t) {
                        self.roomStatus.push({
                            status: true
                        });
                    });
                },
                //改变视图数据
                changeCalendarData: function (data) {
                    this.appList = data.list;
                    this.calendar.fullCalendar('removeEvents');
                    this.searchShow = false;
                    if (data.list.length > 0) {
                        this.calendar.fullCalendar('addEventSource', this.eventList)
                    }
                }

            },
            beforeMount: function () {
                var self = this;
                this.getBuildList();
                this.changeBuild();
                this.getRoomStatus();
                this.roomTypeArr.forEach(function (t) {
                    self.roomTypes.push(t.value)
                })
            },
            mounted: function () {
                var self = this;
                this.calendar = $('#calendar').fullCalendar({
                    weekends: true,
                    defaultDate: moment('${now}').format(),
                    customButtons: {
                        dButton: {
                            text: '日',
                            click: function () {
                                location.href = '${ctxFront}/pw/pwAppointment/viewDay'
                            }
                        },
                        wButton: {
                            text: '周',
                            click: function () {
                                location.href = '${ctxFront}/pw/pwAppointment/viewWeek'
                            }
                        },
                        tButton: {
                            text: '本月',
                            click: function () {
                                self.calendar.fullCalendar('changeView', 'month')
                                self.calendar.fullCalendar('gotoDate', '${now}')
                                self.changeCalendarData({list: self.eventList})
                            }
                        },
                        month: {
                            text: '月',
                            click: function () {
                                self.calendar.fullCalendar('changeView', 'month')
                                self.changeCalendarData({list: self.eventList})
                            }
                        }
                    },
                    header: {
                        left: 'prev,next tButton',
                        center: 'title',
                        right: 'month,wButton,dButton'
                    },
                    navLinkDayClick: false,
                    aspectRatio: 2.8,
                    allDaySlot: false,
                    navLinks: true, // can click day/week names to navigate views
                    editable: false,
                    selectable: false,
                    schedulerLicenseKey: 'GPL-My-Project-Is-Open-Source',
                    events: this.eventList,
                    viewRender: function (element) {
//                        console.log(element)
                    },
                    eventClick: function (calEvent, jsEvent, view) {
//                        var view = self.calendar.fullCalendar('getView');
                        if (view.name !== 'month') {
                            return false;
                        }
                        var link = calEvent.link;
                        var xhr = $.get('${ctxFront}' + link);
                        xhr.success(function (data) {
                            if (data) {
                                self.dayEventList.length = 0;
                                self.calendar.fullCalendar('removeEvents');
                                data.forEach(function (t) {
                                    var item = {
                                        id: t.id,
                                        start: t.startDate,
                                        end: t.endDate
                                    };
                                    if (t.user.id == self.user.id) {
                                        item.color = '#' + t.color;
                                        item.title = '主题：' + t.subject + '\n预约人：' + t.user.name + '';
                                    } else {
                                        item.overlap = false;
                                        item.rendering = 'background';
                                        item.color = '#d7d7d7'
                                    }
                                    self.dayEventList.push(item);
                                });
                                self.backDate = calEvent.start._i;
                                self.calendar.fullCalendar('addEventSource', self.dayEventList);
                                self.calendar.fullCalendar('changeView', 'basicDay', calEvent.start._i)
                            } else {
                                dialogCyjd.createDialog(0, '没有数据')
                            }
                        });
                        xhr.error(function (error) {
                            dialogCyjd.createDialog(0, '网络连接失败，错误代码' + error.status);
                        })
                    },
                    dayClick: function (calEvent) {
                        var date = calEvent._d;
                        var url = '${ctxFront}/pw/pwAppointment/viewDay?searchDay=' + moment(date).format('YYYY-MM-DD');
                        location.href = url;
                    }
                });

            }
        })
        $(document).on('click', 'a.fc-day-number', function () {
            var goto = $(this).attr('data-goto');
            goto = JSON.parse(goto)
            var url = '${ctxFront}/pw/pwAppointment/viewDay?searchDay='+ goto.date;
            location.href = url;
        })


    })(jQuery);


</script>
</body>
</html>