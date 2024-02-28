package kong.unirest.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class MultiHashMap<K, V> extends HashMap<K, Set<V>> {
    public void add(K key, V value){
        compute(key, (k,v) -> {
            if(v == null){
                var set = new HashSet<V>();
                set.add(value);
                return set;
            } else {
                v.add(value);
                return v;
            }
        });
    }
}