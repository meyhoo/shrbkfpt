window.baseUrl = xyc.common.basePath();

window.tableObj = $('#tableList').DataTable({
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
        "url" : window.baseUrl + '/public/searchSqlTemplateGroupList',
        "type" : "post",
        "data": function ( req ) {
            window.vmcontent.sendParams.draw = req.draw;
            window.vmcontent.sendParams.start = req.start;
            window.vmcontent.sendParams.pageCount = req.length;
            return window.vmcontent.sendParams;
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
        { title:'模板组名称', name: 'templateGroupId', data: 'templateGroupId' },
        { title:'中文说明', name: 'templateGroupInfo', data: 'templateGroupInfo' },
        { title:'操作', name: 'opration', data: null,"render": function ( data, type, row ) {
                return 	"<a class='able-a' ms-click=\"changeDialog('create', '"+row.templateGroupId+"')\">生成</a>";
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
window.vmcontent = avalon.define({
    $id: "bodyController",
    showDialog: "main",
    sendParams: {
        templateGroupId: ""
    },
    searchAction: function(){
        window.tableObj.ajax.reload();
    },
    resetAction: function(){
        window.vmcontent.sendParams.templateGroupId = "";
    },

    //生成SQL页面
    changeDialog: function(t, id) {
        if(t =="create"){
            window.editData = window.tableObj.row($(this).parent().parent()).data();
            $("#subFrame").attr("src", window.baseUrl + "/developer/sqlAutoCreatePage.html?templateGroupId="+id);
        }
        if(t == "main"){
            window.vmcontent.showDialog = "main";
        }else {
            window.vmcontent.showDialog = "sub";
        }
    }
});
avalon.scan();