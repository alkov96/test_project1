# language: ru
Функционал: 10_Разное

  @regress
  @HistorySortBalance
  Сценарий: Проверка сортировки по балансу.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "История операций"
    * пользователь осуществляет переход в "История операций"

    * открывается страница "История операций"
    * пользователь (проверяет сортировку по балансу)

