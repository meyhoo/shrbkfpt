var xyc = {};
xyc.common = {
    /**
     * 获得系统根目录路径
     * @returns {string}，系统根目录路径，形如http://ip:port/projectname.
     */
    basePath: function () {
        var strFullPath = window.document.location.href;
        //console.log("strFullPath: "+strFullPath)
        var strPath = window.document.location.pathname;
        //console.log("strPath: "+strPath)
        var pos = strFullPath.indexOf(strPath);
        //console.log("pos: "+pos)
        var prePath = strFullPath.substring(0, pos);
        //console.log("prePath: "+prePath)
        //var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
        //console.log("postPath: "+postPath)
        return prePath;
    },
    /**
     * 将系统中long类型的日期转换为yyyy-mm-dd hh:MM:ss类型
     * @param date
     * @returns {string}
     */
    formatterDateTime: function (date) {
        var datetime = date.getFullYear() +
            "-" // "年"
            +
            ((date.getMonth() + 1) >= 10 ? (date.getMonth() + 1) : "0" +
                (date.getMonth() + 1)) +
            "-" // "月"
            +
            (date.getDate() < 10 ? "0" + date.getDate() : date
                .getDate()) +
            " " +
            (date.getHours() < 10 ? "0" + date.getHours() : date
                .getHours()) +
            ":" +
            (date.getMinutes() < 10 ? "0" + date.getMinutes() : date
                .getMinutes()) +
            ":" +
            (date.getSeconds() < 10 ? "0" + date.getSeconds() : date
                .getSeconds());
        return datetime;
    },

    /**
     * 将系统中long类型的日期转换为yyyy-mm-dd类型
     * @param date
     * @returns {string}
     */
    formatterDate: function (date) {
        var datetime = date.getFullYear() +
            "-" // "年"
            +
            ((date.getMonth() + 1) >= 10 ? (date.getMonth() + 1) : "0" +
                (date.getMonth() + 1)) +
            "-" // "月"
            +
            (date.getDate() < 10 ? "0" + date.getDate() : date
                .getDate())
        return datetime;
    }
}
function cldsLoading(){
    window.clds_loading = layer.load(2,{shade:[0.08,'#000']});
}
function cldsLoaded(){
    layer.close(window.clds_loading);
}
