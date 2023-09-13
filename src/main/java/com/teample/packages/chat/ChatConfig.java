package com.teample.packages.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   // 메시지 브로커가 지원하는 ‘WebSocket 메시지 처리’를 활성화
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    /* 어플리케이션 내부에서 사용할 path 지정 */
    /* 메모리 기반의 Simple Message Broker 활성화 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");    // 브로커는 "/sub" 로 시작하는 주소의 Subscriber 들에게 메시지 전달
        registry.setApplicationDestinationPrefixes("/pub");     // 클라이언트가 서버로 메세지 전송 시 붙여야 하는 prefix 로 "/pub" 지정
    }

    /* HandShake 와 통신을 담당할 EndPoint 지정 */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")    // 클라이언트가 서버로 웹소켓 연결 하고싶을 때 /ws/chat 로 요청을 보내도록 설정
                .setAllowedOriginPatterns("*")     // CORS 설정
                .withSockJS();                     // 소켓을 지원하지 않는 브라우저라면, sockJS를 사용하도록 설정
    }
}
