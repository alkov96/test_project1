# language: ru
Функционал: 5_Лайв, Прематч и динамическое меню

  @before
  @after
  @coupon
  @regress
  @Multimarkets


  Сценарий: Проверка работы мультирынков в "Прематч".
    * редактируем некоторые активные опции сайта
      | multimarkets_enable | true  |

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"

    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (очищает купон)
#    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"
    * пользователь (в меню выбора видов спорта выбирает) "Баскетбол"
    * пользователь (в меню выбора видов спорта выбирает) "Футбол"
    * пользователь (выбирает следующий день недели с более чем событиями) "1"

    * (переходит на игру с активной ставкой)
    * открывается страница "Просмотр событий"
    * пользователь (режим мультирынков) "включает"
    * пользователь (проверяет совпадение названия маркета и размера коэффициента в центральной области и в контейнере мультирынка для активной игры)





