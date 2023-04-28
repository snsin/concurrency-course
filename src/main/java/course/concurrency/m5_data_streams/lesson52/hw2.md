# Задание 3. Практика: экзекьюторы и очереди #

1. Создайте экземпляр экзекьютора, который обрабатывает задачи по принципу LIFO
2. Создайте экземпляр экзекьютора, который содержит 8 потоков и отбрасывает задачи,
   если нет свободных потоков для их обработки

#### Ответы ####

Написал код создания экземпляров и иллюстрацию работы [CustomExecutorsExample](./CustomExecutorsExample.java)

1. Реализовал с помощью "кастомной" двухсторонней очереди - наследника `LinkedBlockingDeque<Runnable>` у которой
   переопределены методы `offer(Runnable runnable)` и `offer(Runnable runnable, long timeout, TimeUnit unit)`.
   Переопределенная реализация возвращает первый элемент очереди вместо последнего. Кастомную очередь передаю
   в конструктор `ThreadPoolExecutor`, см. метод `CustomExecutorsExample.createLifoExecutor(...)`.
2. Реализовал с помощью `SynchronousQueue` и обработчика отклоненных тасок `ThreadPoolExecutor.DiscardPolicy`.
   Очередь и обработчик передаю в конструктор `ThreadPoolExecutor`,
   см. метод `CustomExecutorsExample.fixedThreadsRejectingWhenBusyExecutor(...)`

Результаты выполнения кода:

```text
========= lifo executor =========
pool-1-thread-1 task # 0
pool-1-thread-1 task # 4
pool-1-thread-1 task # 3
pool-1-thread-1 task # 2
pool-1-thread-1 task # 1

========= rejecting executor =========
tasks submitted: 41690; tasks rejected: 999958310
```
