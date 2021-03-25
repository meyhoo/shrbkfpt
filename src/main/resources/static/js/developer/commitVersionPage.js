window.baseUrl = xyc.common.basePath();

var tableObj = $('#tableList').DataTable({
    "autoWidth":true,				//自动宽度
    "dom": 'frt<"tbb"ilp>',			//分页和显示信息位置
    "processing": true,				//服务器处理
    "serverSide": true,				//服务器处理
    "stateSave": false,
    "searching":false,				//是否显示自带搜索功能
    "ordering": false,
    "oLanguage": {					//显示语言
        sUrl: window.baseUrl + "/frame/dataTables/Chinese.json"
    },
    "ajax":{						//ajax请求地址
        "url" : window.baseUrl + '/developer/searchDeveloperVersionTaskList',
        "type" : "post",
        "data": function ( req ) {
            var reqObj = {
                draw:"",
                start:"",
                pageCount:"",
                versionId:""
            }
            reqObj.draw = req.draw;
            reqObj.start = req.start;
            reqObj.pageCount = req.length;
            reqObj.versionId = vmcontent.saveParams.versionId;
            return reqObj;
        },
        "dataSrc": function( json ){
            json.recordsTotal = json.dataMaxCount;
            json.recordsFiltered = json.dataMaxCount;
            window.message = json.errorMsg;
            return json.data;
        }
    },
    "columns":[
        { title:'<input type="checkbox" id="checkAll" />', name: 'checkbox', width: '20', data: null, className:"align_center", render:function(data, type, row){
                return "<input type=\"checkbox\" name=\"checkList\" />";
            }},
        { title:'序号', name: '', data: null ,"render": function(data, type, row){
                return "";
            }},
        { title:'需求', name: 'taskInfo', data: 'taskInfo' },
        { title:'状态', name: 'state', data: 'state', "render": function ( data, type, row ) {
                var stateName = "";
                if(data == "0") {
                    stateName = "未完成";
                }else if(data == "1"){
                    stateName = "已完成";
                }
                return stateName;
            }},
        { title:'操作', name: 'opration', data: null,"render": function ( data, type, row ) {
                if(row.state=='0'){
                    return 	"<a class='able-a' ms-click=\"changeState('1', '"+row.versionId+"', '"+row.taskInfo+"')\">设为已完成</a>"+
                        "<a class='able-a' ms-click=\"openDelete(\'"+row.versionId+"\', '"+row.taskInfo+"')\">删除</a>";
                }else if(row.state=='1'){
                    return 	"<a class='able-a' ms-click=\"changeState('0', '"+row.versionId+"', '"+row.taskInfo+"')\">设为未完成</a>"+
                        "<a class='able-a' ms-click=\"openDelete(\'"+row.versionId+"\', '"+row.taskInfo+"')\">删除</a>";
                }

            }}
    ],
    "drawCallback": function(settings){
        /*table按钮启用avalon*/
        avalon.scan($("#content")[0], window.vmcontent);
        //添加索引
        window.tableObj.column(1, {
            search: 'applied',
            order: 'applied'
        }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }
});

/*checkbox全选*/
$(document).on("click", "#checkAll", function () {
    if ($(this).prop("checked") === true) {
        $("input[name='checkList']").prop("checked", $(this).prop("checked"));
        $('#tableList tbody tr').addClass('selected');
    } else {
        $("input[name='checkList']").prop("checked", false);
        $('#tableList tbody tr').removeClass('selected');
    }
});

/*点击行中的checkbox*/
$('#tableList tbody').on('click', 'tr input[name="checkList"]', function () {
    var $tr = $(this).parents('tr');
    $tr.toggleClass('selected');
    var $tmp = $('[name=checkList]:checkbox');
    $('#checkAll').prop('checked', $tmp.length == $tmp.filter(':checked').length);
});

//avalon初始化
var vmcontent = avalon.define({
    $id: "bodyController",
    showDialog: "main",
    saveParams: {
        versionId:"",
        versionInfo:"",
        priority:""
    },

    changeState: function (state, versionId, taskInfo) {
        layer.confirm('确认要修改任务状态吗？', {
            title: '提示',
            skin: 'layui-layer-style1',
            btn: ['确定','取消'] //按钮
        }, function(){
            $.post(
                window.baseUrl + '/developer/updateDeveloperVersionTaskState',
                {
                    versionId:versionId,
                    taskInfo:taskInfo,
                    state:state
                },
                function (res) {
                    if (res.errorCode=='000000') {
                        clds_layer.msg("修改成功！", "info");
                        window.tableObj.ajax.reload();
                    } else {
                        clds_layer.msg(res.message, "error");
                    }
                },
                "json"
            );
        });
    },

    openDelete: function(versionId, taskInfo){
        layer.confirm('删除后无法恢复，确定要删除该单位吗？', {
            title: '提示',
            skin: 'layui-layer-style1',
            btn: ['确定','取消'] //按钮
        }, function(){
            $.post(
                window.baseUrl + '/developer/deleteDeveloperVersionTask',
                {
                    versionId:versionId,
                    taskInfo:taskInfo
                },
                function (res) {
                    if (res.errorCode=='000000') {
                        clds_layer.msg("删除成功！", "info");
                        window.tableObj.ajax.reload();
                    } else {
                        clds_layer.msg(res.message, "error");
                    }
                },
                "json"
            );
        });
    },

    saveAction: function(){
        if ($("#basic_validate").valid()) {
            cldsLoading();
            $.ajax({
                type : "post",
                url : window.baseUrl + '/developer/updateDeveloperVersionPriority',
                data : vmcontent.saveParams,
                dataType : "json",
                success : function(res) {
                    if (res.errorCode=='000000') {
                        /*回调函数*/
                        cldsLoaded();
                        window.layer.msg('修改成功', {icon: 1}, {time : 1000});
                        /*刷新表格*/
                        window.parent.tableObj.ajax.reload();
                        // /*返回表格*/
                        // vmcontent.returnBack();
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

    changeDialog: function(t, id) {
        if(t == "add"){
            window.editData = {
                versionId:vmcontent.saveParams.versionId
            };
            $("#subFrame").attr("src", window.baseUrl + "/developer/addTaskPage.html");
        }
        if(t == "main"){
            vmcontent.showDialog = "main";
        }else {
            vmcontent.showDialog = "sub";
        }
    },

    //下载
    downloadAction: function(){
        cldsLoading();
        $.ajax({
            type: "post",
            url: window.baseUrl + '/xx/xxx',
            data: vmcontent.saveParams,
            dataType: "json",
            success: function (res) {
                if (res.errorCode == '000000') {
                    /*回调函数*/
                    cldsLoaded();
                    window.parent.layer.msg('修改成功', {icon: 1}, {time: 1000});
                    /*刷新表格*/
                    window.parent.tableObj.ajax.reload();
                    /*返回表格*/
                    vmcontent.returnBack();
                } else {
                    /* 回调函数 */
                    cldsLoaded();
                    layer.msg('修改失败,' + res.message, {icon: 2}, {time: 1000});
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


