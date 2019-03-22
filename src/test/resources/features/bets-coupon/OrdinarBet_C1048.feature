# language: ru
Функционал: 3_Ставки

  @smoke
  @OrdinarBet_C1048
  @coupon

  Сценарий: Проверка нескольких ставок "Ординар".

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"

    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"
    * пользователь (выбирает следующий день недели с более чем событиями) "2"

    * пользователь (очищает купон)
    * пользователь (добавляет ставки из разных событий в количестве) "3"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Ординар"
    * пользователь (вводит сумму одной ставки Ординар) "1"

    * пользователь (нажимает кнопку ВНИЗ - дублирование ставки для всех пари)
    * пользователь (заключает пари)

    * пользователь (проверяет изменение баланса) "рублей"