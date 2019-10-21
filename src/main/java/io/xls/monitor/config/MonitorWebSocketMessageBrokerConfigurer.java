package io.xls.monitor.config;

import io.xls.commons.utils.ArrayUtils;
import io.xls.commons.utils.StringUtils;
import io.xls.spring.boot.autoconfigure.websocket.WebSocketProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.HandshakeHandler;

/**
 * @author kevin
 * @date 2019-10-18 13:39
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(value = {WebSocketProperties.class})
public class MonitorWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProperties webSocketProperties;

    private HandshakeHandler handshakeHandler;

    private WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory;

    public MonitorWebSocketMessageBrokerConfigurer(WebSocketProperties webSocketProperties,
                                                   ObjectProvider<HandshakeHandler> handshakeHandlers,
                                                   ObjectProvider<WebSocketHandlerDecoratorFactory> webSocketHandlerDecoratorFactories) {
        this.webSocketProperties = webSocketProperties;
        this.handshakeHandler = handshakeHandlers.getIfAvailable();
        this.webSocketHandlerDecoratorFactory = webSocketHandlerDecoratorFactories.getIfAvailable();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProperties.getEndpoint()).setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins(webSocketProperties.getAllowedOrigins()).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        DefaultManagedTaskScheduler scheduler = new DefaultManagedTaskScheduler();
        registry.enableSimpleBroker(webSocketProperties.getSimpleBrokers()).setTaskScheduler(scheduler);
        String[] applicationDestinationPrefixes = webSocketProperties.getApplicationDestinationPrefixes();
        if (ArrayUtils.isNotEmpty(applicationDestinationPrefixes)) {
            registry.setApplicationDestinationPrefixes(applicationDestinationPrefixes);
        }
        String userDestinationPrefix = webSocketProperties.getUserDestinationPrefix();
        if (StringUtils.isNotEmpty(userDestinationPrefix)) {
            registry.setUserDestinationPrefix(userDestinationPrefix);
        }
        Integer cacheLimit = webSocketProperties.getCacheLimit();
        if (null != cacheLimit) {
            registry.setCacheLimit(cacheLimit);
        }
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        if (this.webSocketHandlerDecoratorFactory != null) {
            registration.addDecoratorFactory(webSocketHandlerDecoratorFactory);
        }
    }
}
