package ch.heigvd.dai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dani Tiago Faria dos Santos et Nicolas Duprat
 * @brief Classe représentant un cache pour le serveur.
 *
 * Cette classe implémente un cache générique qui stocke des valeurs associées à des clés
 * avec une durée de vie (TTL). Les entrées expirées sont automatiquement supprimées.
 *
 * @param <K> Le type de la clé.
 * @param <V> Le type de la valeur.
 */
public class Cache<K, V> {
    private final Map<K, CacheEntry<V>> store = new ConcurrentHashMap<>(); // Stockage des entrées du cache

    /**
     * @brief Classe interne représentant une entrée du cache.
     *
     * Cette classe stocke une valeur et son temps d'expiration.
     *
     * @param <V> Le type de la valeur stockée dans l'entrée du cache.
     */
    private static class CacheEntry<V> {
        V value; // La valeur stockée dans le cache
        long expirationTime; // Le temps d'expiration de l'entrée

        /**
         * @brief Constructeur de CacheEntry.
         *
         * @param value La valeur à stocker.
         * @param expirationTime Le temps d'expiration de l'entrée.
         */
        CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }

    /**
     * @brief Ajoute une valeur dans le cache avec une clé et une durée de vie (TTL).
     *
     * @param key La clé associée à la valeur.
     * @param value La valeur à stocker dans le cache.
     * @param ttl La durée de vie (Time To Live) en millisecondes.
     */
    public void set(K key, V value, long ttl) {
        store.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    /**
     * @brief Récupère une valeur du cache en utilisant sa clé.
     *
     * Si l'entrée a expiré, elle est supprimée du cache et la méthode retourne `null`.
     *
     * @param key La clé associée à la valeur.
     * @return La valeur associée à la clé, ou `null` si la clé n'existe pas ou si l'entrée a expiré.
     */
    public V get(K key) {
        CacheEntry<V> entry = store.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() < entry.expirationTime) {
                return entry.value; // Retourne la valeur si elle n'a pas expiré
            } else {
                store.remove(key); // Supprime l'entrée si elle a expiré
            }
        }
        return null; // Retourne null si la clé n'existe pas ou si l'entrée a expiré
    }
}