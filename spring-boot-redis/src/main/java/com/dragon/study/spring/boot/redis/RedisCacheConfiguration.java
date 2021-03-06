package com.dragon.study.spring.boot.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by dragon on 16/7/18.
 */
@Configuration
@Import(RedisConfiguration.class)
public class RedisCacheConfiguration extends CachingConfigurerSupport {


  @Bean
  public KeyGenerator phoneKeyGenerator() {
    return (target, method, params) -> {
      StringBuilder sb = new StringBuilder();
      sb.append(
          Arrays.asList(params).stream().map(p -> p.toString()).collect(Collectors.joining(",")));
      return sb.toString();
    };
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    return new RedisCacheManager(redisTemplate);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
    StringRedisTemplate template = new StringRedisTemplate(factory);
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(
        Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(om);
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
