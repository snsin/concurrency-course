# Задание 1 #
## Ответы ##

[thread-dump](./idea-thread-dump.txt)

1. Не совсем понятно что значит "использует на старте". Поэтому предпложения:
   1. Как и любое java-приложение IDEA начинает работу с метода main - т.е. на старте приложение 
   использует 1 поток, это поток `"main" #1` с самым большим временем, пройденным с момента старта
      (elapsed time).
   2. Если же говорить обо всех потоках попавших в thread-dump, то их там 113
2. Судя по служебным потокам в IDEA используется сборщик мусора G1 (garbage-first).

## Лирическое отступление ##
Мне нравится утилита `jcmd` т.к. с ее помощью можно сделать многое. В рамках рабочих задач я делал heap-dump
и записывал данные для профилирования с помощью JFR

В выполнении этого задания мне помогали:
* Thread-dump - `jcmd <pid> Thread.print` 
* получение команды которой запущена jvm `jcmd <pid> VM.command_line` - там как раз есть про GC
  `-XX:+UseG1GC`
* инфа про heap  `jcmd <pid> GC.heap_info` - там тоже про GC есть `garbage-first`
