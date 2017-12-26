Vue.directive('generate-room-asset', {
    inserted: function (element, binding, vnode) {
        var snap = Snap(element);
        var shapeData = binding.value.shapeData;
        var paperId = binding.value.id;
        var dragSnap = Snap(paperId);
        var g = snap.group();

        function generateShape(shapeType, position, content) {
            var text;
            switch (shapeType) {
                case 'rect':
                    return snap.rect(position[0], position[1], position[2], position[3]);
                case 'image':
                    return snap.image(content, position[0], position[1], position[2], position[3]);
                case 'text':
                    text = snap.text(position[0], position[1], content.split('\n'));
                    text.children().forEach(function (t) {
                        t.attr({
                            x: position[0],
                            dy: '1em'
                        })
                    });
                    return text;
            }
        }


        shapeData.forEach(function (t) {
            var cShapeType = t.shapeType;
            var content = cShapeType === 'text' ? t.text : t.attr.href || '';
            var position = [t.attr.x, t.attr.y, t.attr.width, t.attr.height];
            var shape = generateShape(cShapeType, position, content).attr(t.attr);
            g.add(shape);
            shape.drag(function (dx, dy, x, y, event) {
                var width = this.getBBox().width;
                var height = this.getBBox().height;
                vnode.context.paperDrag.style = {
                    'left': event.clientX - width / 2 + 'px',
                    'top': event.clientY - height / 2 + 'px',
                    'width': width + 'px',
                    'height': height + 'px'
                };
            }, function (x, y, event) {
                var width = this.getBBox().width;
                var height = this.getBBox().height;
                var clone = this.clone();
                vnode.context.paperDrag.drag = true;
                vnode.context.paperDrag.style = {
                    'left': event.clientX - width / 2 + 'px',
                    'top': event.clientY - height / 2 + 'px',
                    'width': width + 'px',
                    'height': height + 'px'
                };
                clone.attr({
                    'x': 0,
                    'y': 0
                })
                dragSnap.append(clone.attr('transform', 'matrix(1,0,0,1,0,0)'));

            }, function (event) {
                var target = event.target;
                var snapTarget;
                var parentEleGroup;
                var parentId;

                snapTarget = Snap(target);

                if (event.clientX <= 240 || event.clientX > $('.rd-inspector').offset().left) {
                    dragSnap.clear();
                    return false;
                }

                //出现在Svg上面
                if (t.type === 'roomAsset') {
                    if (snapTarget.data('type') === 'room' || snapTarget.data('type') === 'roomAsset' || (snapTarget.type === 'tspan' && snapTarget.parent().data('type') === 'roomAsset')) {
                        if (snapTarget !== this) {
                            parentId = snapTarget.parent().data('parentId') || snapTarget.parent().hasClass('v-2');
                            if (typeof parentId === 'string') {
                                parentEleGroup = snapTarget.parent().parent()
                            } else {
                                parentEleGroup = snapTarget.parent()
                            }
                            vnode.context.roomAssetActiveData = t;
                            vnode.context.addRoomAssetToSvg(parentEleGroup, target);
                        } else {
                            dragSnap.clear();
                        }
                    } else {
                        dragSnap.clear();
                    }
                } else if (t.type === 'trimming') {
                    if ($(target).parents('#rdPaper').size() > 0 || target.id === 'rdPaper') {
                        if (snapTarget !== this) {
                            vnode.context.roomAssetActiveData = t;
                            vnode.context.addTrimmingToSvg();
                        }
                    }
                }

                dragSnap.clear();
                vnode.context.paperDrag.drag = true;
            })
        })

    }
});