# language: ru
Функционал: Проверка ставки типа Система
  @smoke
  @bets-coupon
  @SystemBet_C1048
  Сценарий: Проверка ставки Система из 5 событий (4/5)
    * переходит на главную страницу 'https://dev-bk-bet-site1.tsed.orglot.office'
#    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"

    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"
    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (очищает купон)
    * пользователь (добавляет ставки из разных событий в количестве) "5"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Система"
    * пользователь (проверяет изменение количества экспрессов при переключении вида системы)

    * пользователь (вводит сумму ставки) "1"
    * пользователь (проверяет что кнопка Заключить Пари) "заблокирована"
    * пользователь (вводит сумму ставки) "5"
    * пользователь (проверяет что кнопка Заключить Пари) "активна"

    * пользователь (вводит сумму ставки) "1" "для каждого экспресса"
    * пользователь (заключает пари)
    * пользователь (проверяет изменение баланса) "рублей"