package com.eventhorizon.pulsar.client.factory

import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPool

internal object RedisConnectionFactory {
    private val DEFAULT_JEDIS_CONFIG = DefaultJedisClientConfig.builder()
        .clientName("pulsar-client")
        .build()

    fun create(redisUri: String) : JedisPool {
        return JedisPool(
            HostAndPort.from(redisUri),
            DEFAULT_JEDIS_CONFIG
        )
    }
}