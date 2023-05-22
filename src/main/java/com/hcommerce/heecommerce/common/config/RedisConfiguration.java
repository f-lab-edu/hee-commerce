package com.hcommerce.heecommerce.common.config;

import com.hcommerce.heecommerce.redis.MessagePublisher;
import com.hcommerce.heecommerce.redis.Publisher;
import com.hcommerce.heecommerce.redis.Subscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("smsQueue");
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return redisTemplate;
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory( lettuceConnectionFactory() );
        container.addMessageListener( messageListener(), topic() );
        return container;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new Subscriber() );
    }

    @Bean
    public MessagePublisher messagePublisher() {
        return new Publisher(redisTemplate(lettuceConnectionFactory()),topic());
    }
}
