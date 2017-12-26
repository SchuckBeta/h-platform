var Utils = {
    fitSize: function (cw, ch, ow, oh) {
        var ratio1 = cw / ow; //100 60
        var ratio2 = ch / oh; //80 60
        var nw, nh;
        if (ratio1 <= ratio2) {
            nh = cw * oh / ow;
            return [0, (ch - nh) / 2, cw, nh]
        } else {
            nw = ch * ow / oh;
            return [(cw - nw) / 2, 0, nw, ch]
        }
    }
}
String.prototype.colorRgb = function () {
    var sColor = this.toLowerCase();
    //十六进制颜色值的正则表达式
    var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
    // 如果是16进制颜色
    if (sColor && reg.test(sColor)) {
        if (sColor.length === 4) {
            var sColorNew = "#";
            for (var i = 1; i < 4; i += 1) {
                sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1));
            }
            sColor = sColorNew;
        }
        //处理六位的颜色值
        var sColorChange = [];
        for (var i = 1; i < 7; i += 2) {
            sColorChange.push(parseInt("0x" + sColor.slice(i, i + 2)));
        }
        return "RGB(" + sColorChange.join(",") + ")";
    }
    return sColor;
};

;(function (window, Snap, $) {
    function App(option) {
        this.options = $.extend({}, App.DEFAULT, option);
        this.snap = this.createSnap(this.options.elId);
        this.sGroup = this.snap.group();
        this.resizeBoxEle = $(this.options.elId).next(this.options.resizeEle);
        this.resizeElements = this.resizeBoxEle.children();
        this.$rdViewport = $('.rd-viewport');
        this.maxWidth = this.$rdViewport.width();
        this.maxHeight = this.$rdViewport.height();
        this.startX = 0;
        this.startY = 0;
        this.currentParentElement = null;
        this.rooms = {
            paperSize: '',
            data: {
                list: []
            }
        };
        this.init();

    }

    App.prototype.init = function () {
        var paperSize = this.options.paperSize.split(',');
        this.setPaperSize({
            width: paperSize[0] * 1,
            height: paperSize[1] * 1,
            left: 600,
            top: 0
        });
        this.setSGroupZoom();
        this.snapMousedown();
        this.parseRoomList();
        this.parseTrimmingList();
        this.resizeElementDrag();
        this.getRooms()
    };

    //生成画布
    App.prototype.createSnap = function (id) {
        return Snap(id);
    };

    App.prototype.setSGroupZoom = function () {
        this.sGroup.attr('transform', this.options.transform).attr('class', 'super-group');
    };

    App.prototype.snapMousedown = function () {
        var $rdFreeTransform = $('.rd-free-transform')
        this.snap.mousedown(function (event) {
            if (event.target.id === 'rdPaper') {
                $rdFreeTransform.hide();

            }
        })
    }

    //生成形状
    App.prototype.shape = function (shapeType, position, content) {
        var text;
        switch (shapeType) {
            case 'rect':
                return this.snap.rect(position[0], position[1], position[2], position[3]);
            case 'image':
                return this.snap.image(content, position[0], position[1], position[2], position[3]);
            case 'text':
                // text = this.snap.text(position[0], position[1], $.isArray(content) ? content : content.split('\n'));
                text = this.snap.text(position[0], position[1], content.split('\n'));
                text.children().forEach(function (t) {
                    t.attr({
                        x: position[0],
                        dy: '1em'
                    })
                });
                return text;
        }
    };

    //添加房间房间
    App.prototype.addRoom = function (data) {
        var group = this.snap.group();
        var position = [data.attr.x, data.attr.y, data.attr.width, data.attr.height];
        var rect = this.shape(data.shapeType, position).data('type', data.type);
        var children = data.children;
        var sGroup = this.sGroup;
        group.attr('transform', data.transform);
        rect.attr(data.attr)
        rect.addClass('rd-move rect-parent');
        group.add(rect).data({
            'type': data.type,
            'roomId': data.roomId
        });
        group.addClass('v-2')
        sGroup.add(group);
        this.dealGroupDragEvent(rect);
        this.addGroupMouseDownEvent(group);
        this.parseRoomAssetList(children, group);
        // this.rooms.data.list.push(data)
    };

    //添加房间资产
    App.prototype.addRoomAsset = function (data, target) {
        var content = data.shapeType === 'text' ? data.text : data.attr.href || '';
        var position = [data.attr.x, data.attr.y, data.attr.width, data.attr.height];
        var shape = this.shape(data.shapeType, position, content).attr(data.attr).data({
            'type': 'roomAsset',
            'name': data.name
        });
        shape.addClass('rd-move rd-room-asset');
        var rect = target.children()[0];
        shape.data('parentId', rect.id);
        shape.parentElement = rect;
        target.add(shape);
        this.addRoomAssetDrag(shape);
    };

    //添加房间挂饰
    App.prototype.addRoomTrimming = function (data) {
        var group = this.snap.group();
        var content = data.shapeType === 'text' ? data.text : data.attr.href || '';
        var position = [data.attr.x, data.attr.y, data.attr.width, data.attr.height];
        var shape = this.shape(data.shapeType, position, content).attr(data.attr);
        var sGroup = this.sGroup;
        shape.data('name', data.name || '');
        shape.data('type', data.type).addClass('rd-move rect-parent')
        shape.attr({
            'x': 0,
            'y': 0
        })
        group.attr('transform', data.transform);
        group.add(shape).data('type', data.type)
        group.addClass('v-trimming');
        sGroup.add(group);
        this.dealGroupDragEvent(shape);
        this.addGroupMouseDownEvent(group);
    };

    //修改房间id
    App.prototype.changeText = function (textEle, rect, text, attr, g, roomId) {
        var shape = this.shape('text', [attr.x, attr.y], text).attr(attr).data({
            'type': 'roomAsset'
        });
        textEle.remove();
        shape.insertAfter(rect);
        shape.data('parentId', rect.id);
        shape.parentElement = rect;
        g.data('roomId', roomId);
        this.addRoomAssetDrag(shape);
    };

    //修改资产text
    App.prototype.changeRoomAssetText = function (ele, text, position) {
        var tspan = [];
        ele.node.innerHTML = '';
        text.forEach(function (t) {
            tspan.push('<tspan x="' + position[0] + '" dy="1em">' + t + '</tspan>')
        });
        ele.node.innerHTML = tspan.join('')
    }

    //处理group拖拽事件
    App.prototype.dealGroupDragEvent = function (element) {
        var resizeBoxEle = this.resizeBoxEle;
        var maxWidth = this.maxWidth;
        var maxHeight = this.maxHeight;
        var self = this;
        element.drag(function (dx, dy) {
            var totalMatrix = this.totalMatrix;
            var left = totalMatrix.e + dx / this.zoomVal;
            var top = totalMatrix.f + dy / this.zoomVal;
            var BBox = this.getBBox();
            var strokeWidth = this.attr('strokeWidth').replace('px', '') * 1;
            left = Math.min(Math.max(left, strokeWidth * 2), maxWidth - BBox.width - strokeWidth * 2);
            top = Math.min(Math.max(top, strokeWidth * 2), maxHeight - BBox.height - strokeWidth * 2);


            this.parent().attr({
                'transform': 'matrix(1,0,0,1,' + (left) + ',' + (top) + ')'
            });
            resizeBoxEle.css({
                left: left * this.zoomVal - strokeWidth,
                top: top * this.zoomVal - strokeWidth
            })

        }, function (x, y) {
            this.totalMatrix = this.parent().matrix;
            this.zoomVal = self.sGroup.matrix.a;
            this.viewportWidth = $('.rd-viewport').width();
            this.scrollFlag = false;
            RoomDefined.$data.colorPickerShow = false;
        })
    };


    App.prototype.setScrollLimit = function (width, zoom, viewWidth) {
        var addWidth = viewWidth + width * zoom;
        this.$rdViewport.width(addWidth);
        this.$rdViewport.parent().width(addWidth + 1200)
        this.$rdViewport.parent().parent().scrollLeft(width * zoom + 600)
    }

    //添加鼠标按下出现外框事件
    App.prototype.addGroupMouseDownEvent = function (ele) {
        var resizeBoxEle = this.resizeBoxEle;
        var self = this;
        ele.mousedown(function (e) {
            var posSize, matrix, rectParentEle, roomData, strokeWidth, attr, zoom;
            resizeBoxEle.show();
            matrix = this.matrix;
            rectParentEle = this.select('.rect-parent');
            attr = rectParentEle.attr();
            strokeWidth = rectParentEle.attr('strokeWidth').replace('px', '');
            zoom = self.sGroup.matrix.a;
            posSize = {
                left: (matrix.e * zoom - strokeWidth * zoom - 1),
                top: (matrix.f * zoom - strokeWidth * zoom - 1),
                width: (attr.width * 1 + strokeWidth * 2) * zoom,
                height: (attr.height * 1 + strokeWidth * 2) * zoom
            };
            self.currentParentElement = this;
            resizeBoxEle.css({
                'width': posSize.width,
                'height': posSize.height,
                'left': posSize.left,
                'top': posSize.top
            })
            roomData = self.getRoomData(this);
            if (roomData.type === 'room') {
                RoomDefined.$data.currentRoomTrimmingData = {};
                RoomDefined.$data.currentRoomData = roomData;
                RoomDefined.$data.currentRoomAssetData = roomData.children.filter(function (t, i) {
                    return i > 0
                });
            } else {
                RoomDefined.$data.currentRoomData = [];
                RoomDefined.$data.currentRoomAssetData = [];
                RoomDefined.$data.currentRoomTrimmingData = roomData;
            }
        })
    };

    //解析type === 'room'的数据
    App.prototype.parseRoomList = function () {
        var list = this.options.data.list || [];
        var self = this;
        var roomList = list.filter(function (t) {
            return t.type === 'room'
        });
        roomList.forEach(function (t, i) {
            self.addRoom(t, i)
        });
        this.rooms.data.list = list || [];
    };

    //解析type === 'roomAsset'的数据
    App.prototype.parseRoomAssetList = function (list, group) {
        var self = this;
        if (!list && list.length < 1) return;
        list.forEach(function (t, i) {
            self.addRoomAsset(t, group, i)
        })
    };

    //解析type==='trimming'的数据
    App.prototype.parseTrimmingList = function () {
        var list = this.options.data.list || [];
        var self = this;
        var trimmingList = list.filter(function (t) {
            return t.type === 'trimming'
        });
        trimmingList.forEach(function (t) {
            self.addRoomTrimming(t)
        })

    }

    //添加房间资产拖拽
    App.prototype.addRoomAssetDrag = function (element) {
        var self = this;
        element.drag(function (dx, dy) {
            var bBox = this.startBBox;
            var parentElementstrokeWidth = this.parentElementstrokeWidth;
            var x, y, dxZoomX, dxXZoomY;
            var tBBox = this.getBBox();
            dxZoomX = dx / this.zoomValue;
            dxXZoomY = dy / this.zoomValue;
            y = Math.min(Math.max(this.startY + dxXZoomY, bBox.y), bBox.y + bBox.height - tBBox.height);
            if (this.type === 'text') {
                x = Math.min(Math.max(this.startX + tBBox.width / 2 + dxZoomX, bBox.x + tBBox.width / 2 + parentElementstrokeWidth), bBox.x + bBox.width - tBBox.width / 2 - parentElementstrokeWidth);
                this.attr({
                    x: x,
                    y: y
                });
                this.children().forEach(function (t) {
                    t.attr({
                        x: x
                    })
                })
            } else {
                this.attr({
                    x: Math.min(Math.max(this.startX + dxZoomX, bBox.x), bBox.x + bBox.width - tBBox.width),
                    y: y
                });
            }
        }, function (x, y) {
            this.startX = this.getBBox().x * 1;
            this.startY = this.getBBox().y * 1;
            this.startBBox = this.parentElement ? this.parentElement.getBBox() : '';
            this.parentElementstrokeWidth = this.parentElement ? this.parentElement.attr('strokeWidth').replace('px', '') * 2 : 0;
            this.zoomValue = self.sGroup.matrix.a;
        })
    }

    //设置画布大小
    App.prototype.setPaperSize = function (params) {
        var scrollerWidth = params.width + (params.left * 2);
        var scrollerHeight = params.height + (params.top * 2);
        var $scroller = this.$rdViewport.parent();
        var $scrollerParent = $scroller.parent();
        var scrollerPW = $scrollerParent.width();
        var scrollerPH = $scrollerParent.height();
        var scrollLeft, scrollTop;
        this.$rdViewport.css({
            'left': params.left,
            'top': params.top,
            'width': params.width,
            'height': params.height
        });
        $scroller.css({
            'width': scrollerWidth,
            'height': scrollerHeight
        });
        scrollLeft = (scrollerWidth - scrollerPW) / 2;
        scrollTop = (scrollerHeight - scrollerPH) / 2;
        this.maxWidth = params.width;
        this.maxHeight = params.height;
        this.setScrollerCenter({
            left: scrollLeft,
            top: scrollTop
        });
        this.rooms.paperSize = [params.width, params.height].join(',')
    }

    //设置画布居中
    App.prototype.setScrollerCenter = function (scroll) {
        var $scrollerP = this.$rdViewport.parent().parent()
        $scrollerP.scrollLeft(scroll.left);
        $scrollerP.scrollTop(scroll.top);
    }


    //获取所有json数据
    App.prototype.getRooms = function () {
        var groups = this.snap.selectAll('.v-2');
        var groupTrimming = this.snap.selectAll('.v-trimming');
        var rooms = this.rooms;
        var list;
        rooms.data = {};
        rooms.data.list = [];
        list = rooms.data.list;
        groups.forEach(function (t, i) {
            var matrix = t.matrix;
            var rectParent = t.select('.rect-parent');
            var rpAttr = rectParent.attr();
            var roomAsset = t.selectAll('.rd-room-asset');
            rpAttr.fill = rpAttr.fill.colorRgb();
            list.push({
                transform: 'matrix(1,0,0,1,' + matrix.e + ',' + matrix.f + ')',
                attr: rpAttr,
                shapeType: rectParent.type,
                type: t.data('type'),
                name: t.data('name'),
                index: '0',
                roomId: t.data('roomId')
            });
            if (roomAsset.length) {
                list[i].children = [];
                roomAsset.forEach(function (t2, i2) {
                    var text = [], tspan, rgbAttr;
                    if (t2.type === 'text') {
                        tspan = t2.children();
                        tspan.forEach(function (t3) {
                            text.push(t3.node.innerHTML)
                        })
                    }
                    rgbAttr = t2.attr()
                    rgbAttr.fill = t2.attr().fill.colorRgb();
                    list[i].children.push({
                        attr: rgbAttr,
                        shapeType: t2.type,
                        type: t2.data('type'),
                        text: text.join('\n'),
                        name: t2.data('name'),
                        index: i2 == '0' ? '1' : '0'
                    })
                })
            }
        })
        groupTrimming.forEach(function (t) {
            var matrix = t.matrix;
            var rectParent = t.select('.rect-parent');
            var rpAttr = rectParent.attr();
            rpAttr.fill = rpAttr.fill.colorRgb();
            list.push({
                transform: 'matrix(1,0,0,1,' + matrix.e + ',' + matrix.f + ')',
                attr: rpAttr,
                shapeType: rectParent.type,
                type: t.data('type'),
                name: t.data('name'),
                index: '0'
            });
        })
        return this.rooms;
    }

    //获取单个房间数据
    App.prototype.getRoomData = function (ele) {
        var data = {};
        var matrix = ele.matrix;
        var rectParent = ele.select('.rect-parent');
        var rpAttr = rectParent.attr();
        var roomAsset = ele.selectAll('.rd-room-asset');
        var fill = rpAttr.fill;
        var stroke = rectParent.attr('stroke');
        var strokeWidth;
        fill = fill.indexOf('rgba') > -1 ? 'transparent' : fill;

        if (stroke) {
            stroke = stroke.indexOf('rgba') > -1 || stroke === 'none' ? 'transparent' : stroke;
        }
        strokeWidth = rectParent.attr('strokeWidth').replace('px', '') * 1;
        data = {
            transform: 'matrix(1,0,0,1,' + matrix.e + ',' + matrix.f + ')',
            attr: rpAttr,
            fill: fill,
            ele: rectParent,
            g: ele,
            name: ele.data('type') === 'trimming' ? rectParent.data('name') : '',
            stroke: stroke,
            roomId: ele.data('roomId'),
            shapeType: rectParent.type,
            strokeWidth: strokeWidth,
            type: ele.data('type')
        };

        if (roomAsset.length) {
            data.children = [];
            roomAsset.forEach(function (t2, i) {
                var text = [], tspan, fill2, stroke2, strokeWidth2, fontFamily, fontSize, attr;
                attr = t2.attr();
                fill2 = attr.fill;
                fill2 = fill2.indexOf('rgba') > -1 ? 'transparent' : fill2;
                if (t2.type === 'text') {
                    fontFamily = t2.attr('fontFamily');
                    fontSize = t2.attr('fontSize').replace('px', '') * 1;
                    tspan = t2.children();
                    tspan.forEach(function (t3) {
                        text.push(t3.node.innerHTML)
                    });
                    data.children.push({
                        attr: attr,
                        fill: fill2,
                        ele: t2,
                        name: t2.data('name') || '',
                        fontFamily: fontFamily,
                        fontSize: fontSize,
                        shapeType: t2.type,
                        type: t2.data('type'),
                        text: text.join('\n')
                    })
                }

                if (t2.type === 'rect') {
                    stroke2 = t2.attr('stroke');
                    stroke2 = stroke2.indexOf('rgba') > -1 || stroke2 === 'none' ? 'transparent' : stroke2;
                    strokeWidth2 = t2.attr('strokeWidth').replace('px', '') * 1;
                    data.children.push({
                        attr: attr,
                        shapeType: t2.type,
                        type: t2.data('type'),
                        ele: t2,
                        fill: fill2,
                        name: t2.data('name') || '',
                        stroke: stroke2,
                        strokeWidth: strokeWidth2
                    })
                }

                if (t2.type === 'image') {
                    data.children.push({
                        attr: attr,
                        ele: t2,
                        name: t2.data('name') || '',
                        shapeType: t2.type,
                        type: t2.data('type'),
                        fill: fill2
                    })
                }
            })
        }
        return data;
    }

    App.prototype.limitDrag = function (params) {
        var rectEH = params.rectEH;
        var rectEW = params.rectEW;
        var rectEY = params.rectEY;
        var rectEX = params.rectEX;
        var maxHeight = params.maxHeight;
        var maxWidth = params.maxWidth;
        var parentBoxTop = params.parentBoxTop;
        var parentBoxLeft = params.parentBoxLeft;
        var strokeWidth = params.strokeWidth;
        var zoom = params.zoom;
        var dx = params.dx;
        var dy = params.dy;
        var boxMinWidthValue = this.options.boxMinWidthValue;
        var boxMinHeightValue = this.options.boxMinHeightValue;
        var sdx = dx / zoom;
        var sdy = dy / zoom;
        var sMaxWidth = maxWidth - rectEX * zoom - strokeWidth;
        var sMaxRectWidth = sMaxWidth / zoom;
        var sRectWidth = Math.min(Math.max(boxMinWidthValue, sdx + rectEW), sMaxRectWidth);
        var pWidth = Math.min(dx + rectEW * zoom + strokeWidth, maxWidth - rectEX * zoom);

        var sMaxHeight = maxHeight - rectEY * zoom - strokeWidth;
        var sMaxRectHeight = sMaxHeight / zoom;
        var sRectHeight = Math.min(Math.max(boxMinHeightValue, sdy + rectEH), sMaxRectHeight);
        var pHeight = Math.min(dy + rectEH * zoom + strokeWidth, maxHeight - rectEY * zoom);

        return {
            sRectWidth: sRectWidth,
            pWidth: pWidth,
            sRectHeight: sRectHeight,
            pHeight: pHeight,
            top: (parentBoxTop - dy)
        }

    };
    //处理放大缩小事件
    App.prototype.resizeElementDrag = function () {
        var self = this;
        var boxMinHeightValue = this.options.boxMinHeightValue;
        var boxMinWidthValue = this.options.boxMinWidthValue;
        this.resizeElements.on('mousedown', function (e) {
            var startX = e.clientX;
            var startY = e.clientY;
            var zoomVal = self.sGroup.matrix.a;
            var currentParentElement = self.currentParentElement;
            var rectParentElement = currentParentElement.select('.rect-parent');
            var BBox = rectParentElement.getBBox();
            var rectEH = BBox.height;
            var rectEW = BBox.width;
            var rectEY = currentParentElement.matrix.f * 1;
            var rectEX = currentParentElement.matrix.e * 1;
            var strokeWidth = currentParentElement.attr('strokeWidth').replace('px', '') * 1;
            var maxHeight = self.$rdViewport.css('height').replace('px', '') * 1;
            var maxWidth = self.$rdViewport.css('width').replace('px', '') * 1;
            var $this = $(this);
            var $parentBox = $this.parents('.rd-free-transform');
            var parentBoxTop = $parentBox.css('top').replace('px', '') * 1;
            var parentBoxLeft = $parentBox.css('left').replace('px', '') * 1;
            var position = $(this).data('position');
            var resizeBY = position === 'bottom';
            var resizeRX = position === 'right';
            var resizeTY = position === 'top';
            var resizeLX = position === 'left';
            var resizeTLXY = position === 'top-left';
            var resizeBTXY = position === 'bottom-left';
            var resizeTRXY = position === 'top-right';
            var resizeBRXY = position === 'bottom-right';
            var strokeWidthDouble = strokeWidth * 2;
            $(document).on('mousemove.query', function (e) {
                var dx, dy, my, mx, height, width, maxRectHeight, maxRectWidth, top, left;
                var limit = self.limitDrag({
                    dx: (e.clientX - startX),
                    dy: (e.clientY - startY),
                    rectEH: rectEH,
                    rectEW: rectEW,
                    rectEY: rectEY,
                    rectEX: rectEX,
                    maxHeight: maxHeight,
                    maxWidth: maxWidth,
                    parentBoxTop: parentBoxTop,
                    parentBoxLeft: parentBoxLeft,
                    strokeWidth: strokeWidthDouble,
                    zoom: zoomVal
                });
                if (resizeBY) {
                    // maxRectHeight = maxHeight - rectEY * zoomVal - strokeWidthDouble;
                    // dy = ((e.clientY - startY) / zoomVal + rectEH);
                    // height = Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight);
                    // rectParentElement.attr({
                    //     height: Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight / zoomVal)
                    // });
                    // $parentBox.height(((e.clientY - startY) + rectEH * zoomVal + strokeWidthDouble))
                    rectParentElement.attr({
                        height: limit.sRectHeight
                    });
                    $parentBox.height(limit.pHeight)
                }

                if (resizeRX) {
                    // dx = (e.clientX - startX) / zoomVal + rectEW;
                    // maxRectWidth = maxWidth - rectEX * zoomVal - strokeWidthDouble;
                    // width = Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth);
                    // rectParentElement.attr({
                    //     width: Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth / zoomVal)
                    // });
                    // $parentBox.width(Math.min(maxRectWidth, width * zoomVal + strokeWidthDouble))
                    rectParentElement.attr({
                        width: limit.sRectWidth
                    });
                    $parentBox.width(limit.pWidth)
                }

                if (resizeTY) {
                    my = (startY - e.clientY) / zoomVal;
                    dy = (my + rectEH);
                    dy = dy < 0 ? 0 : dy;
                    top = rectEY * zoomVal - (startY - e.clientY);
                    if (top < 0) {
                        return;
                    }
                    rectParentElement.attr({
                        height: dy
                    });
                    currentParentElement.attr('transform', 'matrix(1,0,0,1,' + rectEX + ',' + ((rectEY - my) < strokeWidthDouble ? strokeWidthDouble : (rectEY - my)) + ')');
                    $parentBox.css({
                        height: dy * zoomVal + strokeWidthDouble,
                        top: top
                    })
                }

                if (resizeLX) {
                    mx = (startX - e.clientX) / zoomVal;
                    dx = (mx + rectEW);
                    dx = dx < 0 ? 0 : dx;
                    left = rectEX * zoomVal - (startX - e.clientX);
                    if (left < 0) {
                        return;
                    }
                    rectParentElement.attr({
                        width: dx
                    });
                    currentParentElement.attr('transform', 'matrix(1,0,0,1,' + ((rectEX - mx) < strokeWidthDouble ? strokeWidthDouble : (rectEX - mx)) + ',' + (rectEY) + ')');
                    $parentBox.css({
                        width: dx * zoomVal + strokeWidthDouble,
                        left: left
                    })
                }

                if (resizeTLXY) {

                    mx = (startX - e.clientX) / zoomVal;
                    dx = (mx + rectEW);
                    dx = dx < 0 ? 0 : dx;
                    left = rectEX * zoomVal - (startX - e.clientX);


                    my = (startY - e.clientY) / zoomVal;
                    dy = my + rectEH;
                    dy = dy < 0 ? 0 : dy;
                    top = rectEY * zoomVal - (startY - e.clientY);

                    if (left < 0 || top < 0) {
                        return;
                    }
                    rectParentElement.attr({
                        width: dx,
                        height: dy
                    });
                    currentParentElement.attr('transform', 'matrix(1,0,0,1,' + ((rectEX - mx) < strokeWidthDouble ? strokeWidthDouble : (rectEX - mx)) + ',' + ((rectEY - my) < strokeWidthDouble ? strokeWidthDouble : (rectEY - my)) + ')');
                    $parentBox.css({
                        width: dx * zoomVal + strokeWidthDouble,
                        height: dy * zoomVal + strokeWidthDouble,
                        left: left,
                        top: top
                    })
                }
                //待优化
                if (resizeTRXY) {
                    my = (startY - e.clientY) / zoomVal;
                    dy = my + rectEH;
                    top = parentBoxTop - (startY - e.clientY);
                    maxRectHeight = maxHeight * zoomVal;
                    height = Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight);

                    dx = (e.clientX - startX) / zoomVal + rectEW;
                    maxRectWidth = maxWidth - rectEX * zoomVal - strokeWidthDouble;
                    width = Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth);


                    if (height <= boxMinHeightValue || top <= strokeWidthDouble) {
                        return;
                    }
                    rectParentElement.attr({
                        width: width,
                        height: height
                    });
                    currentParentElement.attr('transform', 'matrix(1,0,0,1,' + rectEX + ',' + (rectEY - my) + ')');
                    $parentBox.css({
                        height: height * zoomVal + strokeWidthDouble,
                        width: width * zoomVal + strokeWidthDouble,
                        top: top
                    })
                }

                if (resizeBRXY) {
                    dx = (e.clientX - startX) / zoomVal + rectEW;
                    maxRectWidth = maxWidth - rectEX * zoomVal - strokeWidthDouble;
                    width = Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth);
                    maxRectHeight = maxHeight - rectEY * zoomVal - strokeWidthDouble;
                    dy = (e.clientY - startY) / zoomVal + rectEH;
                    height = Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight);
                    rectParentElement.attr({
                        height: Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight / zoomVal),
                        width: Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth / zoomVal)
                    });
                    $parentBox.css({
                        // height:  Math.min(Math.max(boxMinHeightValue, ((e.clientY - startY)*zoomVal+ rectEH)), maxRectHeight),
                        height: ((e.clientY - startY) * zoomVal + rectEH * zoomVal),
                        width: width * zoomVal + strokeWidthDouble
                    })
                }

                if (resizeBTXY) {
                    maxRectHeight = maxHeight - rectEY * zoomVal - strokeWidthDouble;
                    dy = (e.clientY - startY) / zoomVal + rectEH;
                    height = Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight);

                    mx = (startX - e.clientX) / zoomVal;
                    dx = mx + rectEW;
                    left = parentBoxLeft - (startX - e.clientX);
                    maxRectWidth = maxWidth;
                    width = Math.min(Math.max(boxMinWidthValue, dx), maxRectWidth);
                    if (width <= boxMinWidthValue || left <= strokeWidthDouble) {
                        return;
                    }
                    rectParentElement.attr({
                        width: width,
                        height: Math.min(Math.max(boxMinHeightValue, dy), maxRectHeight / zoomVal)
                    });
                    currentParentElement.attr('transform', 'matrix(1,0,0,1,' + (rectEX - mx) + ',' + (rectEY) + ')');
                    $parentBox.css({
                        width: width * zoomVal + strokeWidthDouble,
                        height: height * zoomVal + strokeWidthDouble,
                        left: left
                    })
                }

            });

            $(document).on('mouseup.query', function () {
                $(document).off('mousemove.query');
                $(document).off('mouseup.query');
            })
        })
    };


    App.DEFAULT = {
        elId: '#rdPaper',
        paperSize: '1000,1000',
        resizeEle: '.rd-free-transform',
        boxMinHeightValue: 30,
        boxMinWidthValue: 30,
        transform: 'matrix(1,0,0,1,0,0)',
        data: {
            // backgroundImage: {
            //     url: 'timg.jpg',
            //     size: '1200, 674'
            // },
            // list: [{
            //     type: 'room',
            //     shapeType: 'rect',
            //     roomId: '',
            //     transform: 'matrix(1,0,0,1,100,100)',
            //     attr: {
            //         style: 'stroke-width: 2px;',
            //         fill: 'transparent',
            //         stroke: '#222138',
            //         x: 0,
            //         y: 0,
            //         width: 300,
            //         height: 300
            //     },
            //     children: [{
            //         type: 'roomAsset',
            //         shapeType: 'text',
            //         text: '房间名',
            //         attr: {
            //             fill: '#e9442d',
            //             x: 190,
            //             y: 130,
            //             style: 'font-size: 12px;font-family:Arial;text-anchor:middle;'
            //         }
            //     }]
            // }]
        }
    };


    window.AppPaper = App;
})(window, Snap, jQuery);
