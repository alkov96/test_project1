# language: ru
Функционал: Добавление события в избранное со страницы События Дня.
  @smoke
  @AddFavoriteFromDayEvents_C1050
  Сценарий: Проверка добавления события в избранное со страницы События Дня
    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | bladekeeper@ya.ru |
      | Пароль | 1qwert2ASDF       |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Бургер"
    * открывается страница "Бургер"

    * пользователь (нажимает кнопку) "События дня"
    * открывается страница "События дня"

    * пользователь (добавляет событие в избранное)

    * пользователь (открывает Избранное)
    * открывается страница "Избранное"

    * пользователь (сравнивает названия событий на странице и в избранном)