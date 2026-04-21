package pl.ing.trading.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trading")
@Getter
@Setter
public class TradingProperties {
    private Long accountId;
}

