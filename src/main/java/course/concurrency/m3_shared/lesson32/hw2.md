# Задание 3. Практика: объекты и мониторы 2 #
Перед вами код:
```java
class TwoMonitorExample {
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    private int value = 0;

    public int read() {
        synchronized (readLock) {
            return value;
        }
    }

    public void update(int value) {
        synchronized (writeLock) {
            this.value = value++;
        }
    }
}
```

1. Какое максимальное количество потоков могут одновременно работать с переменной value? (работать = читать или обновлять)
2. Какие ошибки возможны в этом коде?

#### Ответы ####

1. Одновременно с переменной value могут работать два потока: один из них может забрать монитор объекта
   readLock, а другой - монитор объекта writeLock.
2. В этом коде возможно, что разным потокам будет "видно" разное значение переменной.

Написал следующий код для примера [TwoMonitorExample.java](./TwoMonitorExample.java), 
в результате получал различный вывод, например такой:

```text
i = 10, readValue = 9
i = 15, readValue = 14
i = 147, readValue = 146
i = 389, readValue = 388
i = 393, readValue = 392
i = 705, readValue = 704
i = 728, readValue = 727
```