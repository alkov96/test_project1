# language: ru
Функционал: 2_Пополнение и вывод

  @regress
  @WithdrawLimits_C36187
  Сценарий: Проверка лимитов вывода.

    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |
    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "Вывод"
    * пользователь (вводит крупную сумму для вывода)

    * пользователь (проверяет появление сообщения о недостаточном количестве средств на балансе)
    * пользователь (вводит крупную сумму, превышающую текущее значение баланса на 1)

    * пользователь (проверяет, появляется ли сообщение о недостаточном количестве средств на балансе)
    * пользователь (вводит сумму меньше минимальной и проверяем для каждого способа)

    * пользователь (проверяет, что при нажатии на сумму-ссылку появляется НДФЛ и бонусы)
    * (закрываем браузер)






