# Задание 3. Практика: гарантии при блокировках # 

Код [LockAndSynchronizedExample](./LockAndSynchronizedExample.java) (дополнил методом log)

1. Какие гарантии даёт lock в методе getValue?
   - Только один поток может зайти внутрь критической секции
   - Переменную value нельзя обновлять в этой критической секции
   - Переменная блокируется для обновления во всех методах класса
   - Значение value не может быть null
2. Какие гарантии даёт synchronized в методе add?
   - Только один поток может зайти внутрь метода
   - Значение newValue не может быть null
   - Переменная value блокируется для чтения во остальных методах класса
   - Ни один апдейт не потеряется
3. Какие проблемы возможны в этом коде?
   - Метод getValue прочитает устаревшее значение
   - Метод getValue прочитает несуществующее значение
   - СoncurrentModificationException
4. Сколько потоков могут работать с переменной value одновременно?
   (работать = читать или обновлять)

#### Ответы ####

1. Какие гарантии даёт lock в методе getValue?
    - Только один поток может зайти внутрь критической секции
2. Какие гарантии даёт synchronized в методе add?
    - Только один поток может зайти внутрь метода
    - Ни один апдейт не потеряется
3. Какие проблемы возможны в этом коде?
    - Метод getValue прочитает устаревшее значение
4. С переменной value одновременно могут работать два потока
