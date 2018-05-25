  # language: ru
  Функционал: Проверка ставки типа Ординар
    @smoke
    @OrdinarBet_C1048
    Сценарий: Проверка нескольких ставок Ординар
      * переходит на главную страницу
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Вход"

      * открывается страница "Вход"

      * пользователь (логинится с) данными
        | Логин  | bladekeeper@ya.ru |
        | Пароль | 1qwert2ASDF       |

      * пользователь (нажимает кнопку) "Войти"

      * открывается страница "Авторизованная Главная страница"

      * пользователь (запоминает значение баланса) "в рублях"

      * пользователь (нажимает кнопку) "Прематч"
      * открывается страница "Прематч"
      * пользователь (нажимает кнопку) "Лайв-календарь"
      * открывается страница "Лайв-календарь"

      * пользователь (включает быструю ставку и вводит сумму) "больше баланса"
      * пользователь (проверяет наличие сообщения об ошибке в купоне) "пополните баланс"

      * пользователь (включает быструю ставку и вводит сумму) "1"
      * пользователь (добавляет ставки из разных событий в количестве) "1"

      * пользователь осуществляет переход в "Купон"
      * открывается страница "Купон"
      * пользователь (проверяет изменение баланса) "рублей"