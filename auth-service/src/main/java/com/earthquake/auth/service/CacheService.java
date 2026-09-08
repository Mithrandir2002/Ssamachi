package com.earthquake.auth.service;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    void put(String key, Object value, Duration ttl);

    <T> Optional<T> get(String key, Class<T> type);

    boolean delete(String key);
}
