package io.quarkus.eureka.operation;

import io.quarkus.runtime.configuration.QuarkusConfigFactory;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.junit.jupiter.api.BeforeAll;

public class SmallRyeConfigInitializer {

    static volatile SmallRyeConfig config;

    @BeforeAll
    public static void setItUp() {
        final SmallRyeConfigBuilder builder = new SmallRyeConfigBuilder();
        builder.addDefaultSources();
        builder.addDiscoveredConverters();
        builder.addDiscoveredSources();
        config = builder.build();
        QuarkusConfigFactory.setConfig(config);
    }
}
