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

> Маленький дополнительный вопрос - а изменится ли что-то, если serialNum сделать статическим полем?

Если `serialNum` сделать статическим полем, то изменится количество экземпляров
`ThreadLocal` - он станет 1, независимо от того сколько будет создано экземпляров `GridThreadSerialNumber`.
В этом случае `nextSerialNum` тоже нужно будет сделать статическим полем, синхронизация также потребуется
(либо `AtomicInteger`). И самое важное, на мой взгляд, если мы будем определять последовательные номера потоков в
двух различных пулах потоков, используя различные экземпляры класса `GridThreadSerialNumber`, то в случае со
статическим полем и в случае с полем экземпляра номера будут определяться по-разному, даже если мы будем использовать
разные экземпляры класса `GridThreadSerialNumber` со статическим полем `serialNum` для разных пулов. В случае
статического поля `serialNum`, а значит также статического `nextSerialNum`, нумерация будет сквозной.
Это будет происходить потому, что в разных пулах потоков будет увеличиваться одна и та же переменная
`nextSerialNum`. Без контекста сложно сказать какое поведение является корректным, но я бы всё-же предположил,
что корректным является поведение с нестатическим полем, хотя бы потому, что оно позволяет сделать как
сквозную нумерацию, так и нумерацию в пределах пула потоков.

