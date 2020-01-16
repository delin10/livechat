package nil.ed.livechat.chatroom.stomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.messaging.Message;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class ConnectedCallbackComposite implements ConnectedCallback {
    private List<ConnectedCallback> connectionCallbackList = new ArrayList<>(4);

    public void setConnectionCallbackList(ConnectedCallback...connectionCallbacks) {
        Collections.addAll(connectionCallbackList, connectionCallbacks);
    }

    @Override
    public void onConnected(String roomId, Message<?> message) {
        for (ConnectedCallback connectedCallback : connectionCallbackList){
            connectedCallback.onConnected(roomId, message);
        }
    }
}
