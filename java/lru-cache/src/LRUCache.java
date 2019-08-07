import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private static final float factor = 0.75f;
    private LinkedHashMap<K, V> cache;
    private int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        int _capacity = (int)Math.ceil(capacity / factor) + 1;
        this.cache = new LinkedHashMap<K, V>(_capacity, factor, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized Collection<V> values() {
        return cache.values();
    }
}
