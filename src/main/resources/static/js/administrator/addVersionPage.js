window.baseUrl = xyc.common.basePath();

//表单验证初始化
$("#basic_validate").validate({
    rules:{
        versionId : {
            required:true
        },
        versionInfo : {
            required:false
        }
    },
    messages : {
        versionId:{
            required: "版本名称不能为空!"
        }
    },
    errorClass : "help-inline",
    errorElement : "span",
    errorPlacement : function(error, element) {
        error.appendTo(element.parent().parent());
    }
});

//avalon初始化
var vmcontent = avalon.define({
    $id: "bodyController",
    saveParams: {
        versionId:"",
        versionInfo:""
    },

    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();
            $.ajax({
                type : "post",
                url : window.baseUrl + '/administrator/addAdministratorVersion',
                data : vmcontent.saveParams,
                dataType : "json",
                success : function(res) {
                    if (res.errorCode=='000000') {
                        /*回调函数*/
                        cldsLoaded();
                        window.parent.layer.msg('保存成功', {icon: 1}, {time : 1000});
                        /*刷新表格*/
                        window.parent.tableObj.ajax.reload();
                        /*返回表格*/
                        vmcontent.returnBack();
                    } else{
                        /* 回调函数 */
                        cldsLoaded();
                        layer.msg('保存失败,' + res.errorMsg, {icon: 2}, {time : 1000});
                    }
                },
                error: function(err) {
                    /*回调函数*/
                    cldsLoaded();
                    layer.msg('连接错误', {icon: 2}, {time:1000});
                }
            });
        }
    },
    returnBack: function(){
        window.parent.vmcontent.changeDialog("main");
    }
});
avalon.scan();


