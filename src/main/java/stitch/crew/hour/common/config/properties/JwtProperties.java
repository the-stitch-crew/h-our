package stitch.crew.hour.common.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties {
    private final Validations validations;
    private final Secrets secrets;
    private final Payload payload;

    @Getter
    @RequiredArgsConstructor
    public static class Validations {
        private final Long access;
        private final Long refresh;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Secrets {
        private final String appKey;
        private final String originKey;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Payload {
        private final String issuer;
        private final String subjectAccessToken;
        private final String subjectRefreshToken;
    }
}
