# Задание 3. Практика: анализ использования Exchanger #

Откройте код класса [KafkaProducerTest](https://github.com/apache/kafka/blob/194c56fce2e1b35b518f54ff0d2e5a1104a8126a/clients/src/test/java/org/apache/kafka/clients/producer/KafkaProducerTest.java).
Посмотрите на использование Exchanger в методе:

```java
class KafkaProducerTest {

    @Test
    public void testTopicExpiryInMetadata() {
        //...
    }
}
```
Используется Exchanger <Void>, и обмен объектами не подразумевается. Класс используется не по прямому назначению.

Какая его задача в этом тесте?

#### Ответ ####
Его задача в синхронизации времени выполнения кода между потоком t и потоком, в котором выполняется
основной код теста. Т.е. чтобы получить гарантию, что к определенному моменту в тесте выполнились нужные
действия с тестируемым кодом.
