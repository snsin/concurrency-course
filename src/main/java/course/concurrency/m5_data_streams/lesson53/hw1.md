# Задание 2. Практика: реактивность и текущий проект #

Поделитесь опытом или своими размышлениями:)

### Вариант А: у вашего рабочего проекта “традиционная” архитектура ###

1. Будет ли польза от перевода вашего проекта на реактивный стек?
2. Если да - какие были бы первые шаги? И какие сложности сразу бросаются в глаза?

### Вариант Б: рабочий проект уже использует реактивный стэк ###

1. Расскажите, как вам работается на таком проекте?
2. Какую пользу вы замечаете от реактивности?
3. Какие сложности по сравнению с традиционным стэком?

### Ответы. Вариант А ###

У текущего проекта "традиционная" архитектура, я бы даже сказал самая что ни на есть традиционная:
spring-boot, контейнер сервлетов (встроенный tomcat), блокирующее взаимодействие с БД (JPA + Hibernate).

1. Полагаю, что пользы от перевода на реактивный стек не будет, т.к. мой проект - это звено в цепи
   интеграций, а не отдельная система. Нагрузка на систему маленькая, ресурсов хватает.
   Также есть условия, которые делают такой переход сложным и дорогостоящим. Архитектура системы,
   с которой мы интегрируемся - также традиционная. Запросы из интеграционной платформы - синхронные
   с гарантированной доставкой, размер сообщений средний (5-10 кБайт). Чтобы перевести это дело на
   реактивную архитектуру, потребуется переработка не только моего проекта, но и смежных систем,
   что не представляется возможным, в первую очередь потому, что в принципе нет такой потребности,
   во-вторых, из-за высоких затрат. Хотя, конечно, это действительно система в которой много
   взаимодействия с БД и внешними системами, и возможно, переход на реактивную архитектуру снизил
   бы затраты на оборудование, поэтому попытаюсь ответить на второй вопрос.
2. Считаю, что первыми шагами на пути к реактивной архитектуре был бы переход на реактивное взаимодействие
   с БД (у нас postgres) и пересмотр структуры обмена с интеграционной системой: смену парадигмы
   запрос-ответ на подписки и публикации. Сложностей вижу много, основная - взаимодействие с командой
   интеграционной системы. Если же говорить о технической части, то несомненно первой и основной сложностью
   будет необходимость практически полностью переписать код проекта, останется пожалуй только структура БД,
   JPA-сущности, и форматы сообщений (структура JSON).

Ниже краткое описание проекта, если будет интересно (но лучше не читать).

Попробую кратко описать текущий проект - это звено в системе интеграции с порталом гос. закупок.

Глобальная картина: существует система расчетов (СР), которая каждый месяц генерирует отчетные
документы, для простоты акты выполненных работ, для клиентов - государственных и муниципальных
организаций. Эти документы через интеграционную платформу (ИП) направляются в систему обработки
документов (СОД) и дополнительно ИП обогащает каждый документ договором с клиентом. Договоры
регистрируются во внешней системе - портале гос. закупок. Эта система принадлежит регулятору и
осуществляет учетные и контролирующие функции. Регулятором установлено правило, что отчетные
документы по государственным и муниципальным контрактам должны быть учтены на портале гос. закупок.
В текущем состоянии система расчетов не обладает функциями, позволяющими сформировать отчетные
документы, удовлетворяющие условиям портала гос.закупок, поэтому формирование и направление таких
документов выполняется системой обработки документов (это как раз и есть мой проект).
Направление документов для учета на портале гос.закупок также выполняется через интеграционную платформу.

Система обработки документов СОД - состоит из нескольких сервисов: core - сохранение и обработка документов;
reports - отчеты по документам; notifications - рассылка e-mail, в основном отчетов; frontend - SPA-приложение,
которое общается в core по REST-API.
Сервис core предоставляет REST-API для интеграционной подсистемы и фронт-энд части и обеспечивает
следующие функции:

- принимает и сохраняет документ от интеграционной платформы, в ответ отдает идентификатор документа
- принимает от интеграционной платформы контракт для сохраненного документа, связь документа с контрактом
  по id документа
- отображает пользователям (операторам) системы документы с возможностью фильтрации по параметрам
- предоставляет интерфейс для ограниченного редактирования документов, таким образом, чтобы они подходили
  под критерии регулятора
- валидирует и отправляет подготовленные документы на портал гос. закупок (через интеграционную платформу)
- принимает сообщения о состоянии документа после отправки, т.к. после отправки документ проходит необходимые
  контроли корректности на портале гос.закупок, после чего документ подписывается исполнителем и подписывается
  либо отклоняется заказчиком

Загрузка документа и договора в СОД и сообщения о состоянии документа, оправленного на портал гос. закупок
выполняются интеграционной платформой и представляют собой синхронные запросы с гарантированной доставкой.
Сами документы на стороне системы обработки документов хранятся в реляционной БД, а обмен между сервисами
СОД и между СОД и интеграционной платформой ведется в json-формате. Существует несколько представлений
документа, т.е. между сервисами "ходят" немного разные json-ы - где-то более подробная информация, где-то
сокращенная. В общем, можно сказать, что json-ы средне-мелкого размера 5-10 кБайт. Нагрузка небольшая, в
биллинговый период (3-4 дня в начале каждого месяца) где-то 10 запросов в секунду, всего загружается около
5000 документов. В остальное время запросов еще меньше.

