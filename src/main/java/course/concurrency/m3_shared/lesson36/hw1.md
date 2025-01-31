# Задание 2. Практика: анализ кода #

Откройте класс Intellij IDEA [CloudRuntimeTask](https://github.com/JetBrains/intellij-community/blob/b379f1b42484fae4904bfa30da574caccc8fc3c7/platform/remote-servers/impl/src/com/intellij/remoteServer/util/CloudRuntimeTask.java).
Посмотрите, как используются поля

```java
class CloudRuntimeTask {
    private final AtomicReference<Boolean> mySuccess = new AtomicReference<>();
    private final AtomicReference<String> myErrorMessage = new AtomicReference<>();
}
```

Представьте, что вам отправили этот класс на код-ревью. Что бы вы написали?

#### Ответ ####
В данном случае в методе `CloudRuntimeTask.perform` создается локальная переменная - лямбда `progressive`,
в коде которой, в том числе, проверяется поле `mySuccess`. Если я правильно понимаю, то эта лямбда
обеспечивает обработку (продвижение) прогресс-индикатора и запускает пост-обработку как только основная 
задача завершится. Я думаю что в данном случае этот код подвержен ABA проблеме, т.к. поле `mySuccess` 
может измениться на `true` и потом снова на `false`. Можно порекомендовать заменить поля `mySuccess`
и `myErrorMessage` на одно поле `AtomicStampedReference<String> myErrorMessage` и соответствующим образом
поменять алгоритм проверки.

Далее занудные подробности и объяснения. Допустим, что `CloudRuntimeTask.perform` запустился в 
первом потоке, который установил `mySuccess` в `false`, создал задачу по обработке прогресса,
засабмитил основную задачу в `serverRuntime.getTaskExecutor()` и поставил задачу по обработке прогресса в очередь.
Поле `mySuccess` установится в `true` когда выполнится основная задача, обработка прогресса "сработает", когда
это поле прочитается в лямбде `progressive`. Если второй поток с другой задачей "вклинится" в промежутке между 
тем как в первом потоке завершится основная задача, и тем, как в лямбде прочитается поле `mySuccess`, 
то поле`mySuccess` поменяет свое значение следующим образом: `false -> true -> false` и лямбда первого потока
не заметит изменений. Если при этом другая задача "упадет" с ошибкой до того как в лямбде первой задачи прочитается
`mySuccess`, то обработка индикатора (прогресса) и пост-обработка результата первой задачи может не выполниться. 
Но даже если вторая задаче не упадет, работа кода всё равно кажется не совсем корректной, хотя, похоже,
в этом случае последствия будут менее критичными и обработка все-таки выполнится.

> Q: Можно так, а если бы эти переменные не были связаны между собой (в классе кажется, сейчас так и есть), 
> как можно упростить работу с ними?

Сейчас там не используются операции именно атомарного изменения (CAS) и там нет операций, которые изменяют
переменные на основе предыдущих значений. String - иммутабельный тип, Boolean тоже, но даже если бы и не был
иммутабельным - вместо него можно использовать примитив, т.е. эти AtomicReference* по идее можно просто
заменить на volatile переменные: private volatile String myErrorMessage и private volatile boolean mySuccess.
