# language: ru
Функционал: 2_Пополнение и вывод

  @smoke
  @Withdraw_C1045
  Сценарий: Проверка вывода средств на карту и начисление бонусов.

    * переходит на главную страницу 'https://dev-bk-bet-site1.tsed.orglot.office'
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |
    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (запоминает значение баланса) "бонусов"
    * пользователь (нажимает кнопку) "Иконка юзера"

    * открывается страница "Мини Личный Кабинет"
    * пользователь (нажимает кнопку) "Вывод"

    * пользователь (вводит минимальную сумму вывода для способа) "карты"
    * пользователь (проверяет снятие правильной суммы, и бонусов, если они были начислены)

    * (закрываем браузер)