package io.github.openunirest.http.utils;

import java.util.*;

public class Multimap<K, V>  {
    private Map<K, List<V>> inList = new TreeMap<>();

    public Set<K> keySet() {
        return inList.keySet();
    }

    public List<V> get(K key) {
        return inList.get(key);
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return inList.entrySet();
    }

    public void add(K key, V value) {
        inList.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
}
