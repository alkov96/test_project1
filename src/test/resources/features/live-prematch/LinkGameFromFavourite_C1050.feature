# language: ru
Функционал: Избранное. Переход из Избранного на игры ЛАЙВ и ПРЕМАТЧ

  @smoke
  @LinkGameFromFavourite_C1050
  Сценарий: LinkGameFromFavourite_C1050

    * переходит на главную страницу 'https://dev-bk-bet-site1.tsed.orglot.office'
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "Прематч"
    * открывается страница "Просмотр событий"

    * пользователь (находит игру по фильтру) "1 час" "позже" "и добавляет в избранное"
    * пользователь (находит игру по фильтру) "1 час" "раньше" "и добавляет в избранное"
    * пользователь (включает фильтр по времени) "1 час"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "Лайв"
    * открывается страница "Лайв просмотр событий"

    * пользователь (находит игру по фильтру видео) "с видео" "и добавляет в избранное"
    * пользователь (находит игру по фильтру видео) "без видео" "и добавляет в избранное"
    * пользователь (выставляет фильтр по видео на) "включен"

    * пользователь (нажимает кнопку) "На главную"
    * открывается страница "Главная страница"

    * пользователь (открывает Избранное)
    * открывается страница "Избранное"
    * пользователь (проверяет что переходы с игр из Избранного работают верно)

    * (закрываем браузер)






