let host = "http://localhost:12011";

function dateToTime(unixTime){
    var type= "Y-M-D H:i:s"
    var date = new Date(unixTime * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    var datetime = "";
    datetime += date.getFullYear() + type.substring(1,2);
    datetime += (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + type.substring(3,4);
    datetime += (date.getDate() < 10 ? '0'+(date.getDate()) : date.getDate());
    if (type.substring(5,6)) {
        if (type.substring(5,6).charCodeAt() > 255) {
            datetime += type.substring(5,6);
            if (type.substring(7,8)) {
                datetime += " " + (date.getHours() < 10 ? '0'+(date.getHours()) : date.getHours());
                if (type.substring(9,10)) {
                    datetime += type.substring(8,9) + (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
                    if (type.substring(11,12)) {
                        datetime += type.substring(10,11) + (date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
                    };
                };
            };
        }else{
            datetime += " " + (date.getHours() < 10 ? '0'+(date.getHours()) : date.getHours());
            if (type.substring(8,9)) {
                datetime += type.substring(7,8) + (date.getMinutes() < 10 ? '0'+(date.getMinutes()) : date.getMinutes());
                if (type.substring(10,11)) {
                    datetime += type.substring(9,10) + (date.getSeconds() < 10 ? '0'+(date.getSeconds()) : date.getSeconds());
                };
            };
        };
    };
    return datetime;
}

function renderLayDate(id, type){
    type = type || "date"
    layui.laydate.render({
        elem: id,
        type: type
        ,format:'yyyy-MM-dd HH:mm:ss'
        ,trigger: 'click'
    })

    layui.form.render()
}

function getObjFromForm(jquery, id){
    var formObj = {};
    var t = jquery("#"+id).serializeArray();
    jquery.each(t, function () {
        formObj[this.name] = this.value;
    });
    return formObj;
}

function basePageMapper(res) {
    return {
        "code": res.code, //解析接口状态
        "msg": res.message, //解析提示文本
        "count": res.data.total, //解析数据长度
        "data": res.data.data //解析数据列表
    }
}

function baseMapper(res) {
    console.log(res);
    return {
        "code": res.code, //解析接口状态
        "msg": res.message, //解析提示文本
        "count": res.data.length, //解析数据长度
        "data": res.data //解析数据列表
    }
}

function postAndAlertMessage(url, data, callback) {
    let $ = layui.jquery;
    var config = {};
    config.url = url;
    if(data) {
        config.data = JSON.stringify(data);
    }
    config.contentType = "application/json";
    config.statusCode = {
        200: callback || responseProcessor.alertMessage,
        403: callback || responseProcessor.alertMessage,
        500: callback || responseProcessor.alertMessage
    }
    $.post(config);
}
var responseProcessor= {
    alertMessage: res => alert(res.message),
    printResponse: res => console.log(res)
};

function getQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}

function defaultUploadDone(res) {
    alert(res);
}

function defaultUploadError() {
}


function renderUpload(config) {

    var uploadInst = layui.upload.render({
        elem: config.elem, //绑定元素
        url: config.url //上传接口
        ,done: config.done ? config.done : defaultUploadDone
        ,error: config.error ? config.error : defaultUploadError
    });
}

function openConfirmLayer(title, msg, yes) {
    layui.layer.open({
        title: title,
        type: 1,
        content: msg,
        btn: ["确定", "取消"],
        yes: (index, layero) => {
            yes(index, layero);
            layui.layer.close(layui.layer.index);
        }
    });
}