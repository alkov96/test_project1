# language: ru
Функционал: 10_Разное

  @regress
  @HistoryCheckPages
  Сценарий: Проверка пролистывания страниц в истории операций.
    * разлогиниваем пользователя

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |
    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "История операций"
    * пользователь осуществляет переход в "История операций"
    * открывается страница "История операций"
    * пользователь (проверяет пролистывание страниц)
    * (закрываем браузер)

