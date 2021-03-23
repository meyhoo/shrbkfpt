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
        "url" : window.baseUrl + '/administrator/searchCommitterTaskList',
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
        { title:'负责人', name: 'userName', data: 'userName' },
        { title:'状态', name: 'state', data: 'state', "render": function ( data, type, row ) {
                var stateName = "";
                if(data == "0") {
                    stateName = "未完成";
                }else if(data == "1"){
                    stateName = "已完成";
                }
                return stateName;
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
    saveParams: {
        versionId:"",
        versionInfo:""
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


