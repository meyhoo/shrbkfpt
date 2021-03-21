window.baseUrl = xyc.common.basePath();

//表单验证初始化
$("#basic_validate").validate({
    rules:{
        templateGroupId : {
            required:true
        },
        templateGroupInfo : {
            required:false
        },
        templateIds : {
            required:true
        }
    },
    messages : {
        templateGroupId:{
            required: "模板组名称不能为空!"
        },
        templateIds:{
            required: "模板列表不能为空! 多个模板id之间用竖线符号分割"
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
        templateGroupId:"",
        templateGroupInfo:"",
        templateIds:""
    },

    //修改
    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();
            $.ajax({
                type : "post",
                url : window.baseUrl + '/public/updateSqlTemplateGroup',
                data : vmcontent.saveParams,
                dataType : "json",
                success : function(res) {
                    if (res.errorCode=='000000') {
                        /*回调函数*/
                        cldsLoaded();
                        window.parent.layer.msg('修改成功', {icon: 1}, {time : 1000});
                        /*刷新表格*/
                        window.parent.tableObj.ajax.reload();
                        /*返回表格*/
                        vmcontent.returnBack();
                    } else{
                        /* 回调函数 */
                        cldsLoaded();
                        layer.msg('修改失败,' + res.message, {icon: 2}, {time : 1000});
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

//取得要修改的值
vmcontent.saveParams = window.parent.editData;


