layui.use(["laytpl"], () => {
    let laytpl = layui.laytpl;
    $.get("/livechat/room/index/list", res => {
        console.log(res);
        if (res.code === 0) {
            renderIndexRoomList(res.data.list);
        } else {
            alert(`服务器错误: ${res.message}`);
        }
    });

});

function renderIndexRoomList(list) {
    let $ = layui.jquery, laytpl = layui.laytpl;
    let container = $("#index-container");
    let count = 0;
    let colCount = 4;
    for (let i = 0; i < list.length;) {
        container.append(`<div id = "inner-row-container-${count}" class="layui-row layui-col-space10"></div>`);
        let innerRowContainer = $(`#inner-row-container-${count}`);
        for (let j = 0; j < colCount && i < list.length; ++j, ++i) {
            let one = laytpl(roomCardTemplate).render(list[i]);
            innerRowContainer.append(one);
            ++count;
            console.log(one);
        }
    }
}

let roomCardTemplate =
    "<div class=\"layui-card layui-col-md3\">\n" +
    "        <div class=\"layui-card-header\" style=\"font: 15px bold;height: 26px;\">{{ d.title }}</div>\n" +
    "        <div class=\"layui-card-body\">\n" +
    "            <div style='text-align: center'><a href='./chatroom.html?roomId={{ d.id }}'><img src=\"{{ d.cover }}\" style=\"width:200px;height:100px\"/></a></div>\n" +
    "            <div style=\"padding: 2px; font-size: 10px\">\n" +
    "                <span style='width: 100px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;float: left'>{{ d.description }}</span>\n" +
    "                <span style='text-align: right;width: 80px;font-size: 8px;overflow-wrap: break-word;float: right;padding-right: 5px'>{{ d.user.nickname }}</span>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "    </div>";