# Задание 2. Практика: экспериментируем с экзекьютором в CompletableFuture #

1. Процессор `AMD Ryzen 7 5800U` кол-во ядер 8. Заявлено 2 потока на
   ядро, итого получается 16 потоков, которые в ОС названы логическими процессорами.
2. Имитация блокирующего вызова - `sleep()`
   1. `private ExecutorService executor = ForkJoinPool.commonPool();`
      * Execution time: 76 903 мс
   2. `private ExecutorService executor = Executors.newFixedThreadPool(n);`
      * n = 1 (singleThreadPool) Execution time: 300 010 мс (timeout)
      * n = 1 (singleThreadPool) Execution time: 1 085 501 мс (увеличенный до 30 минут таймаут)
      * n = 8 Execution time: 137 186 мс
      * n = 16 Execution time: 69 402 мс
      * n = 32 Execution time: 36 201 мс
      * n = 48 Execution time: 24 150 мс
      * n = 96 Execution time: 15 091 мс
      * n = 200 Execution time: 15 101 мс
   3. `private ExecutorService executor = Executors.newCachedThreadPool();`
      * Execution time: 15 089 мс
   4. `private ExecutorService executor = Executors.newWorkStealingPool(n);`
      * n = 8 Execution time: 137 244 мс
      * n = 16 Execution time: 72 406 мс (приближается к времени выполнения Executors.newFixedThreadPool(16))
      * n = 32 Execution time: 39 211 мс
      * n = 48 Execution time: 25 634 мс
      * n = 64 Execution time: 19 593 мс
      * n = 96 Execution time: 16 572 мс
      * n = 128 Execution time: 15 112 мс
3. Имитация вычислительной нагрузки - `compute()`
   1. `private ExecutorService executor = ForkJoinPool.commonPool();`
      * Execution time: 43 959 мс
   2. `private ExecutorService executor = Executors.newFixedThreadPool(n);`
      * n = 1 Execution time: 186 831 мс
      * n = 6 Execution time: 49 763 мс
      * n = 8 Execution time: 47 000 мс
      * n = 16 Execution time: 49 936 мс
      * n = 32 Execution time: 54 047 мс
   3. `private ExecutorService executor = Executors.newCachedThreadPool();`
      * Execution time: 50 210 мс
   4. `private ExecutorService executor = Executors.newWorkStealingPool(n)`
      * n = 8 Execution time: 46 338 мс
      * n = 16 Execution time: 53 997 мс
      * n = 32 Execution time: 51 200 мс
      * n = 64 Execution time: 44 335 мс
      * n = 128 Execution time: 48 864 мс
4. Выводы: в целом приблизительно повторяется картина с экзекьюторами и это ожидаемы результат, т.к.
   в данном случае для исполнения асинхронных задач используются те же самые экзекьюторы. Рекомендации
   можно вывести примерно такие же:
   * При блокирующих IO-нагрузках для увеличения производительности следует увеличивать кол-во потоков
     практически пока позволяют ресурсы. Для предотвращения исчерпания ресурсов, и в частности
     предотвращения OOM нужно ограничивать кол-во потоков и размер (емкость) очереди. Используемый по
     умолчанию для выполнения асинхронных задач ForkJoinPool.commonPool() оказался на таком характере
     нагрузки не самым лучшим выбором, что ожидаемо, т.к. в параллелизм у него - 15. 
   * При вычислительных нагрузках в принципе работает формула кол-во потоков (или параллелизм) должен
     быть в диапазоне от кол-ва ядер до кол-ва ядер * 2. Именно в данном тесте на моём железе
     чуть лучше были результаты при кол-ве потоков равном половине кол-ва ядер (возможно из-за того,
     что учитывались логические ядра, а не физические, которых 8). При вычислительной нагрузке в данном
     тесте на данном железе лучше всех справился ForkJoinPool.commonPool().
       