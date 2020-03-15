package nil.ed.livechat.chatroom.config;

import java.util.Optional;

import nil.ed.livechat.chatroom.stomp.ConnectedCallbackComposite;
import nil.ed.livechat.chatroom.stomp.EchoCallback;
import nil.ed.livechat.chatroom.stomp.KafkaMessageHandler;
import nil.ed.livechat.chatroom.stomp.OnlineCounterCallback;
import nil.ed.livechat.chatroom.stomp.handler.CustomStompSubProtocolErrorHandler;
import nil.ed.livechat.chatroom.stomp.interceptor.OutboundChannelInterceptor;
import nil.ed.livechat.chatroom.stomp.interceptor.StompHandShakeInterceptor;
import nil.ed.livechat.chatroom.stomp.interceptor.StompMessageInterceptor;
import nil.ed.livechat.chatroom.stomp.subscription.CustomSubscriptionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Configuration
public class StompConfig extends WebSocketMessageBrokerConfigurationSupport {
    @Bean
    public StompHandShakeInterceptor stompHandShakeInterceptor(){
        return new StompHandShakeInterceptor();
    }

    @Bean
    public OnlineCounterCallback onlineCounterCallback(){
        return new OnlineCounterCallback();
    }

    @Bean
    public EchoCallback echoCallback(){
        return new EchoCallback();
    }

    @Bean
    public CustomStompSubProtocolErrorHandler customStompSubProtocolErrorHandler(){
        return new CustomStompSubProtocolErrorHandler();
    }

    @Bean
    public StompMessageInterceptor stompMessageInterceptor(){
        return new StompMessageInterceptor();
    }

    @Bean
    public OutboundChannelInterceptor outboundChannelInterceptor(){
        OutboundChannelInterceptor outboundChannelInterceptor = new OutboundChannelInterceptor();
        ConnectedCallbackComposite connectedCallbackComposite = new ConnectedCallbackComposite();
        connectedCallbackComposite.setConnectionCallbackList(onlineCounterCallback(), echoCallback());
        outboundChannelInterceptor.setConnectedCallback(connectedCallbackComposite);
        return outboundChannelInterceptor;
    }

    @Bean
    public KafkaMessageHandler kafkaMessageHandler(SubscribableChannel clientInboundChannel) {
        return new KafkaMessageHandler(clientInboundChannel);
    }

    @Bean
    public CustomSubscriptionRegistry subscriptionRegistry() {
        CustomSubscriptionRegistry subscriptionRegistry = new CustomSubscriptionRegistry();
        SimpleBrokerMessageHandler brokerMessageHandler = (SimpleBrokerMessageHandler)simpleBrokerMessageHandler();
        System.out.println(brokerMessageHandler);
        Optional.ofNullable(brokerMessageHandler).ifPresent(m -> m.setSubscriptionRegistry(subscriptionRegistry));
        return subscriptionRegistry;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat/room").withSockJS();
        registry.setErrorHandler(customStompSubProtocolErrorHandler());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandShakeInterceptor(), stompMessageInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundChannelInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix("/user")
                .setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic");
    }

}
