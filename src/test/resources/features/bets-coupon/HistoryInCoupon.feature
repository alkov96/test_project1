  # language: ru
  Функционал: 3_Ставки

    @HistoryInCoupon
    Сценарий: Проверка вкладки "заключенные пари" в купоне

      * разлогиниваем пользователя
      * открывается страница "Главная страница"

      * пользователь (нажимает кнопку) "Прематч"
      * открывается страница "Прематч"
      * открывается страница "Купон"
      * пользователь (пытается перейти на вкладку в купоне) "Заключенные пари" "неуспешно"

      * открывается страница "Прематч"
      * пользователь (нажимает кнопку) "Вход"
      * открывается страница "Вход"

      * пользователь (логинится с) данными
        | Логин  | Default  |
        | Пароль | Default  |

      * открывается страница "Авторизованная Главная страница"

      * открывается страница "Купон"
      * пользователь (пытается перейти на вкладку в купоне) "Заключенные пари" "успешно"
