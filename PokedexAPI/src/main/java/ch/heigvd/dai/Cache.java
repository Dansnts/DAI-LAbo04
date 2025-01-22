package ch.heigvd.dai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache <K, V> {
    private final Map<K, CacheEntry<V>> store = new ConcurrentHashMap<>();

    private static class CacheEntry<V> {
        V value;
        long expirationTime;

        CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }

    public void set(K key, V value, long ttl) {
        store.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    public V get(K key) {
        CacheEntry<V> entry = store.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() < entry.expirationTime) {
                return entry.value;
            } else {
                store.remove(key); // Remove if expired
            }
        }
        return null;
    }
}
