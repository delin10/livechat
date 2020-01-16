layui.use(["laytpl"], () => {
    let laytpl = layui.laytpl;
    let $ = layui.jquery;
    let container = $("#index-container");
    let count = 0;
    let colCount = 4;
    for (let i = 0; i < 10;) {
        container.append(`<div id = "inner-row-container-${count}" class="layui-row layui-col-space10"></div>`);
        let innerRowContainer = $(`#inner-row-container-${count}`);
        for (let j = 0; j < colCount && i < 10; ++j, ++i) {
            let one = laytpl(roomCardTemplate).render({
                title: "这是标题哦-" + i,
                cover: "http://localhost:12011/chatroom/image/a.png",
                desc: "这是描述哦uduashudakjhdjhasdkjasjdjkhasjkhdjaksjdkajskdhkasjhkdhjksajk",
                name: "李德林"
            });
            innerRowContainer.append(one);
            ++count;
            console.log(one);
        }
    }
});

let roomCardTemplate =
    "<div class=\"layui-card layui-col-md3\">\n" +
    "        <div class=\"layui-card-header\" style=\"font: 15px bold;height: 26px;\">{{ d.title }}</div>\n" +
    "        <div class=\"layui-card-body\">\n" +
    "            <div style='text-align: center'><a href='http://baidu.com'><img src=\"{{ d.cover }}\" style=\"width:200px;height:100px\"/></a></div>\n" +
    "            <div style=\"padding: 2px; font-size: 10px\">\n" +
    "                <span style='width: 100px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;float: left'>{{ d.desc }}</span>\n" +
    "                <span style='text-align: right;width: 80px;font-size: 8px;overflow-wrap: break-word;float: right;padding-right: 5px'>{{ d.name }}</span>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "    </div>";