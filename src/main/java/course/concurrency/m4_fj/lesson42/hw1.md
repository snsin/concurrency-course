# Задание 2. Практика: разница экзекьюторов #

Сравните две маленькие программы

Эта завершится, но ничего не напечатает в консоли:

```java
public class FjpExample {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(() -> System.out.println(
                IntStream.range(0, 10000000).average().getAsDouble()));
    }
}
```

Эта напечатает 49999.5, но не закончит выполнение:

```java
public class CtpExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> System.out.println(
                IntStream.range(0, 10000000).average().getAsDouble()));
    }
}
```

Попробуйте ответить на вопросы ниже без дебага, на основе знаний о деталях реализации:

1. Почему получились разные результаты?
2. Как исправить ситуацию в первом случае, чтобы и результат вывелся, и программа завершилась?
3. А во втором?

#### Ответы ####

1. В первом случае задача сабмитится в `ForkJoinPool`, при этом она автоматически преобразуется в `RecursiveTask`,
   однако у нее не вызывается метод `join()` (или `get()`), поэтому задача не выполняется.
   Во втором случае задача выполняется после того как засабмитилась, однако, пула потоков не вызывается
   метод `shutdown`,
   он остается работать поэтому программа не завершается. На самом деле она завершается, но сильно потом, т.к.
   в кеширующем пуле, по-умолчанию, если поток простаивает какое-то время, то он завершается и удаляется из кеша.
2. В первом случае нужно сохранить ссылку на задачу и вызвать `join()`, либо после того как её засабмитили.
   ```java
    public class FjpExample {
        public static void main(String[] args){
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            ForkJoinTask<?> task = forkJoinPool.submit(() -> System.out.println(
                 IntStream.range(0, 10000000).average().getAsDouble()));
            task.join();
        }
    }
    ```
3. Во втором случае нужно завершить пул потоков `executor.shutdown()`.
    ```java
    public class CtpExample {
        public static void main(String[] args) {
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> System.out.println(
                    IntStream.range(0, 10000000).average().getAsDouble()));
            executor.shutdown();
        }
    }
    ```