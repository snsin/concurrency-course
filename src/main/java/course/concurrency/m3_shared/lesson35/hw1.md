# Задание 3. Практика: поиск ошибок в коде #

Посмотрите на работу переменной servePathSpec в классе [WebApp](https://github.com/apache/hadoop/blob/81d7069316451f719d363f5ab7aab617ec03c790/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-common/src/main/java/org/apache/hadoop/yarn/webapp/WebApp.java#L60)

1. Перечислите возможные проблемы и исключения
2. Как можно исправить ВСЕ проблемы из предыдущего пункта?

#### Ответы ####

1. Вижу следующие проблемы:
   * при добавлении пути в список в методе `WebApp.addServePathSpec` изменения в нем могут быть не видны
     другим потокам
   * вытекает из предыдущего пункта, метод `WebApp.getServePathSpecs` может вернуть неправильные результаты.
     Например, пустой массив, хотя там уже будут значения
   * метод `WebApp.configureServlets` может неправильно сконфигурировать приложение. Например, может не
     зарегистрировать добавленные в список пути, как обрабатываемы классом `Dispatcher.java`. с диспетчером.   
   * метод `WebApp.getServePathSpecs` может выбросить IndexOutOfBoundsException. Например, если в методе
     `WebApp.addServePathSpec` было добавлено несколько путей, но метод `WebApp.getServePathSpecs` выполнялся
     в другом потоке и не увидел часть добавленных путей, для конкретики, пусть в переменной был один добавленный
     путь, потом добавился еще один, а поток, который выполнял `WebApp.getServePathSpecs` считал старое значение
     size = 1, и при вызове `this.servePathSpecs.toArray` создал массив из одного элемента. При этом,
     поток выполняющий метод `ArrayList.toArray` также считал устаревшие значения при проверке 
     `if (a.length < size)`, но при выполнении `System.arraycopy(elementData, 0, a, 0, size);` уже считал
     обновленные значения. Код метода `ArrayList.toArray` приведен ниже для наглядности:
        ```java
        public class ArrayList {
            public <T> T[] toArray(T[] a) {
                if (a.length < size)
                    // Make a new array of a's runtime type, but my contents:
                    return (T[]) Arrays.copyOf(elementData, size, a.getClass());
                System.arraycopy(elementData, 0, a, 0, size);
                if (a.length > size)
                    a[size] = null;
                return a;
            }
        }
        ```
   * поле `servePathSpec` не объявлено финальным, но ссылка на него нигде не изменяется
2. Поскольку в данном случае логика работы класса основана на том, что изменяется внутреннее состояние объекта
   `this.servePathSpecs` (в список добавляются значения), без изменения логики работы класса `volatile` 
   здесь не подходит. Если не изменять логику работы, то лучше сделать переменную финальной, 
   а доступ к ней синхронизировать другим способом. Например, с помощью `synchronized`. Пример кода класса ниже:

```java
public class WebAppLight {

    private final List<String> servePathSpecs = new ArrayList<String>();

    void addServePathSpec(String path) {
        synchronized (servePathSpecs) {
            this.servePathSpecs.add(path);
        }
    }

    public String[] getServePathSpecs() {
        String[] paths;
        synchronized (servePathSpecs) {
            paths = this.servePathSpecs.toArray(new String[0]);
        }
        return paths;
    }

    public void configureServlets() {
        setup();

        synchronized (servePathSpecs) {
            for (String path : this.servePathSpecs) {
                serve(path).with(Dispatcher.class);
            }
        }

    }
}
```

Тот же самый код, но в виде `.java` файла - [WebAppLight](./WebAppLight.java)
