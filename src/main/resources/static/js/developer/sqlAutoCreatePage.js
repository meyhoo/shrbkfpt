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
        var requestObj = {};
        $('.form-input.myinput').each(function(){
            requestObj[$(this).attr('name')] = $(this).val()
        });
        requestObj['runSqlContent'] = $('#runSqlTemplate').val();
        requestObj['rollbackSqlContent'] = $('#rollbackSqlTemplate').val();
        $.ajax({
            type: "post",
            url: window.baseUrl + '/developer/createSql',
            data: requestObj,
            dataType: "json",
            success: function (res) {
                if (res.errorCode == '000000') {
                    var runSqlContent = res.data.runSqlContent;
                    var rollbackSqlContent = res.data.rollbackSqlContent;
                    if($("#runSqlContent").val()==''){
                        $("#runSqlContent").val(runSqlContent);
                    }else{
                        $("#runSqlContent").val($("#runSqlContent").val()+'\n\n'+runSqlContent);
                    }
                    if($("#rollbackSqlContent").val()==''){
                        $("#rollbackSqlContent").val(rollbackSqlContent);
                    }else{
                        $("#rollbackSqlContent").val($("#rollbackSqlContent").val()+'\n\n'+rollbackSqlContent);
                    }
                    $('.form-input.myinput').each(function(){
                        $(this).val('');
                    });
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
    //清空
    clearAction: function(){
        $("#runSqlContent").val('');
        $("#rollbackSqlContent").val('');
    },
    returnBack: function(){
        window.parent.vmcontent.changeDialog("main");
    }
});
avalon.scan();

//取得要修改的值
vmcontent.saveParams = window.parent.editData;


