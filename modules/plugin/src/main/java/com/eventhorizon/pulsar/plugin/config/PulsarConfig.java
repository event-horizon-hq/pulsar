package com.eventhorizon.pulsar.plugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class PulsarConfig {
    public static final BuilderCodec<PulsarConfig> CODEC = BuilderCodec.builder(PulsarConfig.class, PulsarConfig::new)
            .append(new KeyedCodec<>("RedisURI", Codec.STRING),
                     (pulsarConfig, s) -> pulsarConfig.redisURI = s,
                    pulsarConfig -> pulsarConfig.redisURI)
            .add()
            .append(new KeyedCodec<>("SingularityURL", Codec.STRING),
                    (pulsarConfig, s) -> pulsarConfig.singularityUrl = s,
                    pulsarConfig -> pulsarConfig.singularityUrl)
            .add()
            .append(new KeyedCodec<Boolean>("DebugMode", Codec.BOOLEAN),
                    (pulsarConfig, state) -> pulsarConfig.debugMode = state,
                    pulsarConfig -> pulsarConfig.debugMode)
            .add()
            .build();

    private String redisURI, singularityUrl;
    private boolean debugMode;
}
