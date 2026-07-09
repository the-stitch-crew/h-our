package stitch.crew.hour.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "custom.redis")
public class RedisProperties {
    private Boolean enabled = true;
    private String host;
    private Integer port;
}
