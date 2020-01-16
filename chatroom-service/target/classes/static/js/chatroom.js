let chatroom = new Vue({
        el: '#chatroom',
        data: {
            stompClient: null,
            messages: [],
            messageinput: null,
            number: 0,
            roomId: getQueryString("roomId"),
            player: null
        },
        methods: {
            connectToSocket() {
                this.messages.push({
                    creator: '系统消息',
                    msgBody: '连接中...'
                });

                var websocket = new SockJS("/chat/room");
                this.stompClient = Stomp.over(websocket);
                this.stompClient.connect({"heart-beat": "0,0", "room-id": this.roomId}, connectFrame => {
                    chatroom.messages.push({
                        creator: '系统消息',
                        msgBody: '连接成功！'
                    });
                    //接收聊天室消息
                    chatroom.stompClient.subscribe('/topic/echo.' + this.roomId, function (data) {
                        console.log(data);
                        chatroom.messages.push({
                            creator: '系统消息',
                            msgBody: data.body
                        });
                    });
                    chatroom.stompClient.subscribe('/user/topic/oneToOne.' + this.roomId, function (data) {
                        console.log(data);
                        chatroom.messages.push({
                            creator: '一对一消息',
                            msgBody: data.body
                        });
                    });
                    chatroom.stompClient.subscribe('/topic/group.' + this.roomId, function (data) {
                        chatroom.messages.push({
                            creator: data.headers['user-name'],
                            msgBody: data.body
                        });
                    });
                }, errorFrame => {
                    console.log(errorFrame);
                    if (errorFrame.headers) {
                        chatroom.messages.push({
                            creator: "系统消息",
                            msgBody: errorFrame.headers['message']
                        });
                    }
                });
            }, initVideo() {
                if (flvjs.isSupported()) {
                    var videoElement = document.getElementById('live-player');
                    var flvPlayer = flvjs.createPlayer({
                        type: 'flv',
                        enableWorker: true,     //浏览器端开启flv.js的worker,多进程运行flv.js
                        isLive: true,           //直播模式
                        hasAudio: false,        //关闭音频
                        hasVideo: true,
                        stashInitialSize: 128,
                        enableStashBuffer: false,
                        url: 'http://localhost:12010/live?port=1935&app=videochat&stream=1'
                    });
                    flvPlayer.attachMediaElement(videoElement);
                    flvPlayer.load();
                    flvPlayer.play();
                } else {
                    alert("not support");
                }
            },
            sendMessage: function () {
                if (chatroom.messageinput != null) {
                    console.log(chatroom.messageinput);
                    let input = chatroom.messageinput.trim();
                    let name = null;
                    if (input.startsWith("@")) {
                        name = input.substr(1, input.indexOf(" ")).trim();
                    }
                    let dest = (name ? "/user/" + name + "/topic/oneToOne." : "/topic/group.") + this.roomId;
                    this.stompClient.send(dest, {}, chatroom.messageinput);
                    this.messageinput = null;
                } else if (chatroom.messageinput == null) {
                    chatroom.messageinput = "请输入内容!!!";
                }
            }
        }, mounted() {
            var roomTitleEle = document.getElementById('room-title');
            console.log("title ", getQueryString("title"));
            roomTitleEle.append(unescape(getQueryString("title")));
            this.connectToSocket();
            this.initVideo();
        }
    })
;

let subcribeButtomTemplate = "<span style=\"float: right\"><button type=\"button\" {{ d.isSubscribed ? \"disabled='disabled'\" : \"\" }} class=\"layui-btn layui-btn-warm layui-btn-radius\" style=\"width: 150px\"><i class=\"layui-icon\">{{ d.icon }}</i>&nbsp;&nbsp;{{ d.title }}</button></span>";


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

layui.use(['laytpl'], () => {
    let laytpl = layui.laytpl;
    let isSubscribed = true;
    let subscribeButton = laytpl(subcribeButtomTemplate).render({
        icon: isSubscribed ? "&#xe67a;" : "&#xe67b;",
        title: isSubscribed ? "已订阅" : "订阅",
        isSubscribed: isSubscribed
    });
    layui.jquery("#subscribe-button-container").append(subscribeButton);

    let colCount = 4;
    let count = 0;
    let container = layui.jquery("#room-recommendation-container");
    for (let i = 0; i < 11;) {
        container.append(`<div id = "room-inner-row-container-${count}" class="layui-row layui-col-space10"></div>`);
        let innerRowContainer = $(`#room-inner-row-container-${count}`);
        for (let j = 0; j < colCount && i < 11; ++j, ++i) {
            let one = laytpl(roomCardTemplate).render({
                title: "这是标题哦-" + i,
                cover: "http://localhost:12011/chatroom/image/a.png",
                desc: "这是描述哦uduashudakjhdjhasdkjasjdjkhasjkhdjaksjdkajskdhkasjhkdhjksajk",
                name: "李德林"
            });
            innerRowContainer.append(one);
            ++count;
        }
    }
});
