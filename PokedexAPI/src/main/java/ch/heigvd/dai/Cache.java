package ch.heigvd.dai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**author: Dani Tiago Faria dos Santos and Nicolas duprat
 * this class is the cache of the server
 * */
public class Cache <K, V> {
    private final Map<K, CacheEntry<V>> store = new ConcurrentHashMap<>();

    /**
    * this class is the object we store in the cache
    * */
    private static class CacheEntry<V> {
        V value;
        long expirationTime;

        CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }
    /**
     * this function put a value in the cache registered with a key and a ttl
     */
    public void set(K key, V value, long ttl) {
        store.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    /**
     * this function gets a value from the cache by using his key and checks weither the ttl has expired or not,
     * if so it returns null otherwise it returns the value
     * */
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
