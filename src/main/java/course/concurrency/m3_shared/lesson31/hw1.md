# Задание 3. Практика: гонка между потоками #

Строки с комментариями - это действия, которые выполняются в другом потоке, при этом 
считаем, что `key = 5`, `keyCopy = 5` и `key.equals(keyCopy)` возвращает `true`

#### Проблема 1. Старое значение из БД попадет в кеш и останется там до следующей записи в БД ####
```java
public class CachedRepo<K, V> {
    
    public V read(K key) {
        V result = cache.getIfPresent(key);
        if (result == null) {
            result = readFromDatabase(key);
//                                          writeToDatabase(keyCopy, newValue);
//                                          cache.invalidate(keyCopy);
            cache.put(key, result);
        }
        return result;
    }

    public void write(K key, V value) {
        writeToDatabase(key, value);
        cache.invalidate(key);
    }
}
```

#### Проблема 2. Значение в БД и значение в приложении разойдутся, при этом кеш инвалидируется ####
```java
public class CachedRepo<K, V> {
    
    public V read(K key) {
        V result = cache.getIfPresent(key);
        if (result == null) {
            result = readFromDatabase(key);
//                                          writeToDatabase(keyCopy, newValue);
            cache.put(key, result);
//                                          cache.invalidate(keyCopy);
        }
        return result;
    }

    public void write(K key, V value) {
        writeToDatabase(key, value);
        cache.invalidate(key);
    }
}
```

#### Проблема 3. Инвалидируется кеш после того как переменная прочитается из БД ####
```java
public class CachedRepo<K, V> {
    
    public V read(K key) {
        V result = cache.getIfPresent(key);
        if (result == null) {
//                                          writeToDatabase(keyCopy, newValue);
            result = readFromDatabase(key);
            cache.put(key, result);
//                                          cache.invalidate(keyCopy);
        }
        return result;
    }

    public void write(K key, V value) {
        writeToDatabase(key, value);
        cache.invalidate(key);
    }
}
```