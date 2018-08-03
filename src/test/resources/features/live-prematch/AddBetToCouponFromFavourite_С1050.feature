# language: ru
Функционал: 5_Лайв, Прематч и динамическое меню

  @regress
  @AddBetToCouponFromFavourite_С1050
  Сценарий: "Избранное". Добавление ставки в купон из "Избранное"

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (нажимает кнопку) "Бургер"

    * открывается страница "Бургер"
    * пользователь (нажимает кнопку) "Прематч"

    * открывается страница "Просмотр событий"
    * пользователь (находит игру по фильтру) "1 час" "позже" "и добавляет в избранное"

    * пользователь (находит игру по фильтру) "1 час" "раньше" "и добавляет в избранное"
    * пользователь (нажимает кнопку) "На главную"

    * открывается страница "Главная страница"
    * пользователь (открывает Избранное)

    * открывается страница "Избранное"
    * пользователь (проверяет что в избранном все нужные игры)

    * пользователь (добавляет ставку в купон)
    * пользователь осуществляет переход в "Купон"

    * открывается страница "Купон"
    * пользователь (проверяет, совпадают ли события в купоне с ожидаемыми)

    * пользователь (проверяет, совпадает ли исход в купоне с ожидаемым)
    * пользователь (сравнивает коэфиценты)

    * открывается страница "Просмотр событий"
    * пользователь (очищает избранное через левое меню)

    #* закрываем браузер