window.baseUrl = xyc.common.basePath();

//表单验证初始化
$("#basic_validate").validate({
    rules:{
        userName : {
            required:true
        },
        pwd : {
            required:true
        },
        rePwd : {
            required:true
        },
        role : {
            required:true
        }
    },
    messages : {
        userName:{
            required: "账号不能为空!"
        },
        pwd:{
            required: "密码不能为空!"
        },
        rePwd:{
            required: "请确认密码!"
        },
        role:{
            required: "角色类型不能为空!"
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
        userName:"",
        pwd:"",
        role:""
    },

    //保存新增账号
    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();
            var pwd1 = $("#pwd").val();
            var pwd2 = $("#rePwd").val();
            if(pwd1!=pwd2){
                window.parent.layer.msg('重复输入密码不一致', {icon: 2}, {time : 1000});
                cldsLoaded();
                return;
            }
            vmcontent.saveParams.pwd = md5(vmcontent.saveParams.pwd);
            $.ajax({
                type : "post",
                url : window.baseUrl + '/user/addUser',
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


