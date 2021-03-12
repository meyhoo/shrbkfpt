window.baseUrl = xyc.common.basePath();

//表单验证初始化
$("#basic_validate").validate({
    rules:{
        area : {
            required:true
        },
        areaFirstletter : {
            required:true
        },

    },
    messages : {
        area:{
            required: "城市不能为空!"
        },
        areaFirstletter:{
            required: "首字母不能为空!"
        },

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
        area:"",
        areaFirstletter:"",

    },

    //保存新增城市
    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();

            $.ajax({
                type : "post",
                url : window.baseUrl + '/system/areaAdd',
                data : vmcontent.saveParams,
                dataType : "json",
                success : function(res) {
                    if (res.isSuccess) {
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
                        layer.msg('保存失败,' + res.message, {icon: 2}, {time : 1000});
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


