package nil.ed.livechat.chatroom;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class StompTest {
    @Test
    @Ignore
    public void test() throws Exception{
        ExecutorService executor = Executors.newCachedThreadPool();
        List<WebSocketStompClient> clients = new LinkedList<>();
        IntStream.range(0,1000).forEach(i->{
            executor.execute(()->{
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    WebSocketStompClient client = newConnect(i);
                    clients.add(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        TimeUnit.DAYS.sleep(1);
    }

    @Test
    public void testSend() {
        WebSocketStompClient client = newConnect(0);
    }

    /**
     * 创建websocket连接
     * @param i
     * @return
     */
    private WebSocketStompClient newConnect(int i) {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient socketClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(socketClient);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        stompClient.setTaskScheduler(taskScheduler);

        String url = "ws://localhost:12001/chat/room";
        StompHeaders httpHeaders = new StompHeaders();
        httpHeaders.add("room-id","4");
        stompClient.connect(url, null, httpHeaders, new TestConnectHandler(), new Object[]{url});
        return stompClient;
    }

    private static class TestConnectHandler extends StompSessionHandlerAdapter { }
}
