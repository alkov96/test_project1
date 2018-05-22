# language: ru
Функционал: Проверка экспресс-ставки
  @smoke
  @ExpressBet_C1048
  Сценарий: Проверка ставки Экспресс из 4 событий
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

    * пользователь (добавляет ставки из разных событий в количестве) "4"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * пользователь (выбирает тип ставки) "Экспресс"
    * пользователь (вводит сумму ставки Экспресс) "1"
    * (заключает пари)

    * пользователь (проверяет изменение баланса) "рублей"

