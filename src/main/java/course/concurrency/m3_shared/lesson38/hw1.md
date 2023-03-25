# Задание 3. Практика: анализ класса GridThreadSerialNumber #

Посмотрите на код класса [GridThreadSerialNumber](https://github.com/apache/ignite/blob/da8a6bb4756c998aa99494d395752be96d841ec8/modules/core/src/test/java/org/apache/ignite/session/GridThreadSerialNumber.java)

Зачем нужен synchronized в методе initialValue?

Предложите более простой код для этой цели

synchronized нужен для того, чтобы можно начальное значение ThreadLocal переменной подсчитывалось корректно.
Когда один экземпляр класса GridThreadSerialNumber используется несколькими потоками, в случае отсутствия
synchronized, поток может считать устаревшее значения переменной nextSerialNum, и порядковый номер потока
будет определен неправильно.
В качестве более простого варианта класса можно предложить следующее [MyGridThreadSerialNumber](./MyGridThreadSerialNumber.java)

```java
class MyGridThreadSerialNumber {

    private final AtomicInteger nextSerialNum = new AtomicInteger(0);

    private final ThreadLocal<Integer> serialNum = ThreadLocal.withInitial(nextSerialNum::getAndIncrement);

    public int get() {
        return serialNum.get();
    }
}
```
