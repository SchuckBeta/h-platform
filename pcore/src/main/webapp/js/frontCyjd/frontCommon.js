/**
 * Created by Administrator on 2017/12/11.
 */



var dialogCyjd = {
    successIcon: function (msg) {
        return '<i class="icon-ok-msg"></i><span>' + msg + '</span>';
    },
    errorIcon: function (msg) {
        return '<i class="icon-fail-msg"></i><span>' + msg + '</span>';
    },
    warningIcon: function (msg) {
        return '<i class="icon-warning-msg"></i><span>' + msg + '</span>';
    },
    dialogDefaultOption: {
        id: '#dialogCyjd',
        modal: true,
        resizable: false,
        width: 'auto',
        height: 'auto',
        title: '消息提醒',
        dialogClass: 'dialog-cyjd-container',
        buttons: [{
            text: '确定',
            class: 'btn btn-sm btn-primary',
            click: function () {
                $(this).dialog("close");
            }
        }]
    },
    createDialog: function (res, msg, dialogOption) {
        var html = '';
        var eleId;
        var options;
        dialogOption = dialogOption || {};
        options = $.extend({}, this.dialogDefaultOption, dialogOption);
        eleId = options.id;
        switch (res * 1) {
            case 0 :
                html = this.errorIcon(msg);
                break;
            case 1 :
                html = this.successIcon(msg);
                break;
            case 2 :
                html = this.warningIcon(msg);
                break;
        }
        $(eleId).html(html);
        $(eleId).dialog(options)
    }
}





jQuery.validator.addMethod("positiveNumber", function (value, element) {
    return this.optional(element) || value > 0;
}, "请填写大于0的数字");