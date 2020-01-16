function createWebSocket(url) {
    let websocket = new WebSocket(url);
    initWebsocket(websocket);
    return websocket;
}

function createSockJs(url) {
    let websocket = new SockJS(url);
    initWebsocket(websocket);
    return websocket;
}

function initWebsocket(websocket) {
    websocket.onerror = setErrorMessage;
    websocket.onopen = setOnopenMessage;
    websocket.onmessage = setOnmessageMessage;
    websocket.onclose = setOncloseMessage;
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = onbeforeunload;
    function setErrorMessage() {
        console.log("WebSocket连接发生错误" + '   状态码：' + websocket.readyState);
    }
    function setOnopenMessage() {
        console.log("WebSocket连接成功" + '   状态码：' + websocket.readyState);
    }
    function setOnmessageMessage(ws, ev) {
        console.log(ev.data);
    }
    function setOncloseMessage() {
        console.log("WebSocket连接关闭" + '   状态码：' + this.websocket.readyState);
    }
    function onbeforeunload() {
        websocket.close();
    }
}