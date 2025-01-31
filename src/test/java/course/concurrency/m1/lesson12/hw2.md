# Задание 2 #

1. Синхронизации по умолчанию в JVM вероятно нет по следующим причинам:
   * синхронизация по умолчанию приведет к проблемам с производительностью
   * на разных архитектурах могут быть разные аппаратные механизмы,
     которые возможно сложно привести к "единому знаменателю"
   * аппаратные платформы развиваются и возможно в будущем могут появиться новые механизмы
     синхронизации, которые будут конфликтовать с реализацией в JVM
2. Считаю, что сложнее модель памяти Java, т.к. она предоставляет больше гарантий чем модель
   памяти C++. У C++ больше отдано на откуп разработчику, соответственно модель памяти проще.
3. Чем сложнее архитектура процессора и памяти тем БОЛЬШЕ методов оптимизации
4. Чем меньше ограничений оптимизации в модели памяти, тем БЫСТРЕЕ выполняется код
5. Чем больше ограничиваются оптимизации в модели памяти тем ЛЕГЧЕ писать корректно работающий
   код
6. Если доступ к памяти ускорится, то модель памяти МОЖНО УПРОСТИТЬ.
   Считаю так, потому, что сейчас основная проблема - это как раз скорость доступа к памяти,
   что требует оптимизации кол-ва обращений к памяти, и реализации этих оптимизаций, например,
   локальные кеши процессоров/ядер приводят к несогласованности данных в кешах и основной памяти.
   Таким образом, если доступ к памяти ускорится настолько, что можно будет не использовать кеш,
   то потоки будут обращаться напрямую к памяти, и это устранит проблемы видимости данных из разных 
   потоков, но, скорее всего, оставит проблему атомарности изменений. Однако, считаю, что модель
   памяти можно будет упростить.
