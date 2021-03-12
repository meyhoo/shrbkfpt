window.baseUrl = xyc.common.basePath();

//表单验证初始化
$("#basic_validate").validate({
    rules:{
        oldPwd:{
            required:true
        },
        newPwd:{
            required:true
        },
        reNewPwd : {
            required:true
        }
    },
    messages : {
        oldPwd:{
            required: "请输入旧密码!"
        },
        newPwd:{
            required: "请输入新密码!"
        },
        reNewPwd:{
            required: "请确认新密码!"
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
        adminAccount:"",
        adminType:"",
        oldPwd:"",
        newPwd:""
    },

    //修改
    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();
            var newPwd = $("#newPwd").val();
            var reNewPwd = $("#reNewPwd").val();
            if(newPwd!=reNewPwd){
                window.parent.layer.msg('重复输入新密码不一致', {icon: 2}, {time : 1000});
                cldsLoaded();
                return;
            }
            $.ajax({
                type : "post",
                url : window.baseUrl + '/account/updatePwd',
                data : vmcontent.saveParams,
                dataType : "json",
                success : function(res) {
                    if (res.isSuccess) {
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

var accountType = vmcontent.saveParams.adminType;
if(accountType=='1'){
    $("#accountType").val('超级管理员');
}
if(accountType=='2'){
    $("#accountType").val('业务操作员');
}
if(accountType=='3'){
    $("#accountType").val('监控管理员');
}

