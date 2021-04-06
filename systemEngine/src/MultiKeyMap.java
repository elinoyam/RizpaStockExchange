import java.util.*;

public class MultiKeyMap<K,T> {
    private Map<K,T> mapByKeyOne = new TreeMap<>();
    private Map<K,T> mapByKeyTwo = new TreeMap<>();

    public void put(K keyOne,K keyTwo,T value){
        mapByKeyOne.put(keyOne,value);
        mapByKeyTwo.put(keyTwo,value);
    }

    public T get(K key) {
       if( mapByKeyOne.containsKey(key))
           return mapByKeyOne.get(key);
       else if(mapByKeyTwo.containsKey(key))
           return mapByKeyTwo.get(key);
       else
          return null;
    }

    public boolean containsKey(K key){
        if (mapByKeyOne.containsKey(key))
            return true;
        else if (mapByKeyTwo.containsKey(key))
            return true;
        else
            return false;
    }

    public Collection<T> values(){
        return mapByKeyOne.values();
    }

    public int size(){
        return mapByKeyOne.size();
    }

    public void clear(){
        mapByKeyOne.clear();
        mapByKeyTwo.clear();
    }
}
