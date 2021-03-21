window.baseUrl = xyc.common.basePath();

//avalon初始化
var vmcontent = avalon.define({
    $id: "bodyController",
    saveParams: {
        templateGroupId:"",
        runSqlContent:"",
        rollbackSqlContent:""
    },

    //生成
    createAction: function(){
        cldsLoading();
        var placeholder = {};
        $('.form-input.myinput').each(function(){
            placeholder[$(this).attr('name')] = $(this).val()
        });
        var requestObj = {};
        requestObj['placeholder'] = placeholder;
        requestObj['runSqlContent'] = $('#runSqlTemplate').val();
        requestObj['rollbackSqlContent'] = $('#rollbackSqlTemplate').val();
        alert(JSON.stringify(requestObj))
        $.ajax({
            type: "post",
            url: window.baseUrl + '/developer/createSql',
            data: requestObj,
            dataType: "json",
            success: function (res) {
                if (res.errorCode == '000000') {
                    /*回调函数*/
                    cldsLoaded();
                    layer.msg('生成成功', {icon: 1}, {time: 1000});
                } else {
                    /* 回调函数 */
                    cldsLoaded();
                    layer.msg('生成失败,' + res.message, {icon: 2}, {time: 1000});
                }
            },
            error: function (err) {
                /*回调函数*/
                cldsLoaded();
                layer.msg('连接错误', {icon: 2}, {time: 1000});
            }
        });
    },
    returnBack: function(){
        window.parent.vmcontent.changeDialog("main");
    }
});
avalon.scan();

//取得要修改的值
vmcontent.saveParams = window.parent.editData;


