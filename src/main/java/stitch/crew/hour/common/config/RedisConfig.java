package stitch.crew.hour.common.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import stitch.crew.hour.common.config.properties.RedisProperties;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;


    // Refresh Token 전용 DB
    @Bean
    @Primary
    public RedisConnectionFactory authRedisConnectionFactory(){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redisProperties.getHost(),
                redisProperties.getPort()
        );
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    // 캐시용
    @Bean
    public RedisConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(
                        redisProperties.getHost(),
                        redisProperties.getPort()
                );
        config.setDatabase(2); // 캐시용
        return new LettuceConnectionFactory(config);
    }
    @Bean
    @Qualifier("cache")
    public RedisTemplate<String,String> cacheRedisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cacheRedisConnectionFactory());
        return redisTemplate;
    }

    // 분산락용
    @Bean
    public RedisConnectionFactory lockRedisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(
                        redisProperties.getHost(),
                        redisProperties.getPort()
                );
        config.setDatabase(3); // 캐시용
        return new LettuceConnectionFactory(config);
    }
    @Bean
    @Qualifier("lock")
    public RedisTemplate<String,String> lockRedisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lockRedisConnectionFactory());

        return redisTemplate;
    }

    // RedissonClient 를 Spring Bean으로 등록
    @Bean
    public RedissonClient redisClient() {
        Config config = new Config();
        // URI 설정
        String uri = String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort());
        config.useSingleServer().setAddress(uri);
        return Redisson.create(config);
    }
}
