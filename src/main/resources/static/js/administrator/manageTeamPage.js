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
        "url" : window.baseUrl + '/user/searchUserList',
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
        { title:'成员', name: 'userName', data: 'userName' },
        { title:'角色', name: 'role', data: 'role', "render": function ( data, type, row ) {
                var stateName = "";
                if(data == "1") {
                    stateName = "管理者";
                }else if(data == "2"){
                    stateName = "开发者";
                }
                return stateName;
            }},
        { title:'操作', name: 'opration', data: null,"render": function ( data, type, row ) {
                return 	"<a class='able-a' ms-click=\"changeDialog('edit', "+row.id+")\">修改密码</a>"+
                    "<a class='able-a' ms-click=\"openDelete(\'"+row.userName+"\')\">删除</a>";
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
        userName: "",
        role: ""
    },
    searchAction: function(){
        window.tableObj.ajax.reload();
    },
    resetAction: function(){
        window.vmcontent.sendParams.userName = "";
        window.vmcontent.sendParams.role = "";
    },

    //批量删除
    doDeletes: function(){
        var $selected = $('#tableList tbody').find('[name=checkList]:checked');
        if($selected.length == 0){
            clds_layer.msg("请至少选择一条记录", "warn");
            return;
        }
        //获取选中所有adminAccount
        var selectedIds = [];
        $selected.each(function(){
            var data = window.tableObj.row($(this).parent().parent()).data();
            selectedIds.push(data.userName);
        });
        layer.confirm('删除后无法恢复，确定要删除所选元素吗？', {
            title: '提示',
            skin: 'layui-layer-style1',
            btn: ['确定','取消'] //按钮
        }, function(){
            $.post(
                window.baseUrl + '/user/deleteUsers',
                {
                    userNames: JSON.stringify(selectedIds)
                },
                function(res){
                    if(res.errorCode=='000000'){
                        clds_layer.msg("删除成功！", "info");
                        window.tableObj.ajax.reload();
                    }else{
                        clds_layer.msg(res.message, "error");
                    }
                },
                "json"
            );
        });
    },

    //单个删除
    openDelete: function(param){
        layer.confirm('删除后无法恢复，确定要删除该单位吗？', {
            title: '提示',
            skin: 'layui-layer-style1',
            btn: ['确定','取消'] //按钮
        }, function(){
            $.post(
                window.baseUrl + '/user/deleteUser',
                {
                    userName:param
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

    //新增或修改页面
    changeDialog: function(t, id) {
        if(t == "add"){
            $("#subFrame").attr("src", window.baseUrl + "/administrator/addUserPage.html");
        }else if(t =="edit"){
            window.editData = window.tableObj.row($(this).parent().parent()).data();
            $("#subFrame").attr("src", window.baseUrl + "/system/getAccountEdit");
        }
        if(t == "main"){
            window.vmcontent.showDialog = "main";
        }else {
            window.vmcontent.showDialog = "sub";
        }
    }
});
avalon.scan();