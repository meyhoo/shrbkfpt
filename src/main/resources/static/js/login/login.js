function loginAjax(){
    var account = $("#account").val();
    var password = $("#password").val();
    $.ajax({
        "url": xyc.common.basePath() + "/user/login",
        "contentType": "application/x-www-form-urlencoded",
        "async": false,
        "type": "POST",
        "data": {
            "account": account,
            "password": password
        },
        "dataType": "text",
        "success": function (data, callback, settings) {
            if(data=='03'){
                shakeModal3();
            }
            if(data=='02'){
                shakeModal2();
            }
            if(data=='01'){
                shakeModal1();
            }
            if(data=='00'){
                window.location.replace("/user/mainPage.html");
            }
        },
        "error": function (data, callback, settings) {
            shakeModal();
        }
    });
}
function shakeModal(){
    $('#error').show();
    setTimeout( function(){
        $('#error').hide();
    }, 5000 );
}
function shakeModal1(){
    $('#error01').show();
    $('input[type="password"]').val('');
    setTimeout( function(){
        $('#error01').hide();
    }, 5000 );
}
function shakeModal2(){
    $('#error02').show();
    $('input[type="password"]').val('');
    setTimeout( function(){
        $('#error02').hide();
    }, 5000 );
}
function shakeModal3(){
    $('#error03').show();
    $('input[type="password"]').val('');
    setTimeout( function(){
        $('#error03').hide();
    }, 5000 );
}