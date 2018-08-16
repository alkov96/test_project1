# language: ru
Функционал: 3_Ставки

  @regress
  @BannersInCoupon
  Сценарий: Проверка отображения баннеров в купоне

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Прематч"

    * пользователь (нажимает кнопку) "Лайв-календарь"
    * открывается страница "Лайв-календарь"

    * пользователь (в меню выбора видов спорта выбирает) "Все Виды Спорта"

    * пользователь (очищает купон)
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * (проверяет наличие банеров в купоне)

    * пользователь осуществляет переход в "Лайв-календарь"
    * открывается страница "Лайв-календарь"
    * пользователь (добавляет ставки из разных событий в количестве) "1"

    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"

    * (проверяет отсутствие баннеров в купоне)
