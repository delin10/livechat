layui.use(["form"], function() {
    let form = layui.form, $ = layui.jquery;
    form.on("submit(register-btn)", data=>{
        let submitObj = data.field;
        console.log(submitObj);
        postAndAlertMessage("/livechat/user/register", submitObj, res => {
            if (res.code === 0) {
                alert("注册成功");
                window.location.href = host + "/chatroom/login.html";
            } else {
                alert("注册失败");
            }
        });
        return false;
    });
});