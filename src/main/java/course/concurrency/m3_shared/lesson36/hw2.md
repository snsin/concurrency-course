# Задание 3. Анализ альтернатив AtomicMarkableReference #

В чем преимущество A`tomicMarkableReference<String>` перед
  1.  двумя полями `volatile String` и `volatile boolean`?
  2.  полем `String` и `boolean`, чтение и изменение которых происходят в `synchronized` секции?

#### Ответ ####
1. a) Преимущество `AtomicMarkableReference<String>` перед двумя полями `volatile String` и `volatile boolean`
   в том, что в `AtomicMarkableReference<String>` флаг и ссылку можно изменить атомарно, а чтобы сделать такое
   с двумя `volatile` переменными потребуется дополнительная синхронизация, например `synchronized` блок или
   метод.
2. б) Преимущество `AtomicMarkableReference<String>` перед полями `String` и `boolean`, чтение и изменение
   которых происходят в synchronized секции, в том, что `AtomicMarkableReference<String>` будет производительнее.
   Это преимущество достигается за счет того, что выполняются неблокирующие операции. Также работа с
   `AtomicMarkableReference<String>` может производиться из нескольких потоков, что хорошо когда требуется частое
   чтение переменной из разных потоков и редкое обновление.
