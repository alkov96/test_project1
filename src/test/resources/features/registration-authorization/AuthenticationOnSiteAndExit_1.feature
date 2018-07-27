# language: ru
Функционал: 1_Регистрация и авторизация

  @regress
  @AuthenticationOnSiteAndExit_1
  Сценарий: Аутентификация на сайте и выход.

      * разлогиниваем пользователя
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Вход"
      * открывается страница "Вход"

      * пользователь (логинится с) данными
        | Логин  | Default |
        | Пароль | Default |

      * пользователь (закрывает всплывающее окно 'Перейти в ЦУПИС')
      * открывается страница "Авторизованная Главная страница"

      * пользователь (нажимает кнопку) "Иконка юзера"

      * открывается страница "Мини Личный Кабинет"
      * пользователь (нажимает кнопку) "Выйти"

      * открывается страница "Главная страница"
      * (закрываем браузер)
