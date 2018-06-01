# language: ru
  Функционал: 1.Регистрация и авторизация.

    @smoke
    @AuthenticationOnSiteAndExit_1
    Сценарий: Аутентификация на сайте и выход.

      * переходит на главную страницу
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Вход"
      * открывается страница "Вход"

      * пользователь (логинится с) данными
        | Логин  | Default |
        | Пароль | Default |

      * пользователь (нажимает кнопку) "Войти"

      * открывается страница "Авторизованная Главная страница"
      * пользователь (нажимает кнопку) "Иконка юзера"

      * открывается страница "Мини Личный Кабинет"
      * пользователь (нажимает кнопку) "Выйти"

      * открывается страница "Главная страница"
      * (закрываем браузер)
