# language: ru
Функционал: 3_Ставки

  @smoke
  @ExpressBet_C1048
  Сценарий: Проверка ставки Экспресс из 4 событий.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * (если выскочил PopUp 'Перейти в ЦУПИС', закрываем)
    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"

    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"
    * пользователь (выбирает следующий день недели с более чем событиями) "4"

    * пользователь (очищает купон)
    * пользователь (добавляет ставки из разных событий в количестве) "4"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки) "1"

    * (заключает пари)
    * пользователь (проверяет изменение баланса) "рублей"

    * закрываем браузер

