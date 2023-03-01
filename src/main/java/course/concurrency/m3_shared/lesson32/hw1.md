# Задание 2. Практика: объекты и мониторы #
1. В данной ситуации поток T2 спокойно выполнит clear
2. Это произойдет потому, что потоку T2 не пытается захватывать монитор set (внутри метода нет 
   блока `synchronized (set) {/* ... */}`). Поэтому поток T2 не заблокируется и не будет ожидать
   пока поток T1 освободит монитор, а просто очистит множество.

Для проверки написал вот такой кривейший код
```java
class ScratchSynchronized {
   private final Set<Integer> set = new HashSet<>();

   public static void main(String[] args) {
      System.out.println("started: " + Thread.currentThread().getName());
      var scratchSynchronized = new ScratchSynchronized();
      scratchSynchronized.set.add(42);
      System.out.println("before size: " + scratchSynchronized.set.size());
      var executorService = Executors.newFixedThreadPool(4);
      System.out.println("before time: " + Instant.now());
      Future<?> updFuture = executorService.submit(scratchSynchronized::update);
      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
      Future<?> clearFuture = executorService.submit(scratchSynchronized::clear);
      try {
         clearFuture.get();
         System.out.println("after size: " + scratchSynchronized.set.size());
         System.out.println("after time: " + Instant.now());
         updFuture.get();
         executorService.shutdown();
      } catch (InterruptedException | ExecutionException e) {
         throw new RuntimeException(e);
      }
      System.out.println("stop time: " + Instant.now());
   }

   public void update() {
      System.out.println("update thread: " + Thread.currentThread().getName());
      synchronized (set) {
         try {
            Thread.sleep(10000L);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void clear() {
      System.out.println("clear thread: " + Thread.currentThread().getName());
      set.clear();
   }
}
```
вывод:
```text
started: main
before size: 1
before time: 2023-03-01T19:56:15.038678600Z
update thread: pool-1-thread-1
clear thread: pool-1-thread-2
after size: 0
after time: 2023-03-01T19:56:15.155271900Z
stop time: 2023-03-01T19:56:25.054293600Z
```
