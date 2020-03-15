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
                        let obj = JSON.parse(data.body);
                        chatroom.messages.push({
                            creator: '系统消息',
                            msgBody: obj.content
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
                        let obj = JSON.parse(data.body);
                        chatroom.messages.push({
                            creator: obj.user.nickname,
                            msgBody: obj.content
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
                        hasAudio: true,        //开启音频
                        hasVideo: true,
                        stashInitialSize: 128,
                        enableStashBuffer: true,
                        withCredentials: false,
                        lazyLoadMaxDuration: 3 * 60,
                        seekType: 'range',
                        fixAudioTimestampGap: false,
                        cors: true,
                        url: 'http://localhost:12010/live?port=1935&app=videochat&stream=1&' +  document.cookie
                    });
                    flvPlayer.attachMediaElement(videoElement);
                    flvPlayer.load();
                    flvPlayer.play();
                } else {
                    alert("not support");
                }
            },
            sendMessage: function () {
                if (chatroom.messageinput) {
                    let input = chatroom.messageinput.trim();
                    let name = null;
                    if (input.startsWith("@")) {
                        name = input.substr(1, input.indexOf(" ")).trim();
                    }
                    let dest = (name ? "/user/" + name + "/topic/oneToOne." : "/topic/group.") + this.roomId;
                    this.stompClient.send(dest, {}, chatroom.messageinput);
                    this.messageinput = null;
                }
            }
        }, mounted() {
            this.connectToSocket();
            this.initVideo();
        }
    })
;

let subcribeButtomTemplate = `<span style="float: right"><button type="button" {{# d.isSubscribed ? "disabled='disabled'" : "" }} class="layui-btn layui-btn-warm layui-btn-radius" style="width: 150px" id="subscribe-button"><i class="layui-icon">{{ d.icon }}</i>&nbsp;&nbsp;{{ d.title }}</button></span>`;


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

layui.use(['laytpl', 'layer'], () => {
    let laytpl = layui.laytpl;

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
    let roomId = getQueryString("roomId");
    $.get(`/livechat/room/info/get?id=${roomId}`, res => {
       if (res.code === 0) {
           initRoom(res.data);
       } else {
           alert("服务器错误");
       }
    });

});

function initHeadImg(imgUrl) {
    let headElem = layui.jquery("#head-span");
    headElem.empty();
    headElem.append(`<a href="javascript:"><img src="${imgUrl}" class="layui-nav-img"/></a>`);
}

function initRoom(room) {
    let $ = layui.jquery,laytpl = layui.laytpl;
    let roomTitleEle = document.getElementById('room-title');

    roomTitleEle.append(room.title);

    let roomLabelListElem = $("#room-label-list");
    let roomOwner = $("#room-owner");
    room.tags.forEach(tag => {
        roomLabelListElem.append(`<span class="room-tab">${tag.word}</span>`)
    });
    initHeadImg(room.user.head_img);
    roomOwner.append(room.user.nickname);
    $("#subscription-count").append(room.subscription_count);
    $("#activation").append(room.activation);
    $("#description-pane").append(room.description);
    let isSubscribed = room.is_subscribed;
    let subscribeButton = laytpl(subcribeButtomTemplate).render({
        icon: isSubscribed ? "&#xe67a;" : "&#xe67b;",
        title: isSubscribed ? "已订阅" : "订阅",
        isSubscribed: isSubscribed
    });
    $("#subscribe-button-container").append(subscribeButton);
    $("#subscribe-button").on("click", isSubscribed ? unsubscribe(room.id) : subscribe(room.id));
}

function subscribe(id) {
    return () => {
        let $ = layui.jquery;
        $.post(`/livechat/room/subscribe?id=${id}`, res => {
            if (res.code === 0) {
                alert("订阅成功");
                window.location.reload();
            } else {
                alert("订阅失败");
            }
        });
    }
}

function unsubscribe(id) {
    return () => {
        let $ = layui.jquery;
        openConfirmLayer("提示消息", "是否取消订阅?", () => {
            $.post(`/livechat/room/unsubscribe?id=${id}`, res => {
                if (res.code === 0) {
                    alert("取消订阅成功");
                    window.location.reload();
                } else {
                    alert("取消订阅失败");
                }
            });
        });
    }
}
