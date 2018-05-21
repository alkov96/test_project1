# language: ru
Функционал: Изменение условий в купоне

  @smoke
  @CouponAcceptChange_C1048
  Сценарий: Изменение условий в купоне
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

    * пользователь (добавляет несколько событий в купон)
    * пользователь осуществляет переход в "Купон"
    * открывается страница "Купон"
    * пользователь (устанавливает условие для принятия коэфицентов как 'Никогда')
    * пользователь (проверяет, что после изменения условий в купоне появляется кнопка 'Принять' и информационное сообщение)
