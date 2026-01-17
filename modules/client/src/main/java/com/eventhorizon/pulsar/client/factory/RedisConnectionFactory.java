package com.eventhorizon.pulsar.client.factory;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;

public final class RedisConnectionFactory {

    private static final DefaultJedisClientConfig DEFAULT_JEDIS_CONFIG =
            DefaultJedisClientConfig.builder()
                    .clientName("pulsar-client")
                    .build();

    public static JedisPool create(String redisUri) {
        return new JedisPool(
                HostAndPort.from(redisUri),
                DEFAULT_JEDIS_CONFIG
        );
    }
}
