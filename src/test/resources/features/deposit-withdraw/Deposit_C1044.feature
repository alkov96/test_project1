# language: ru
Функционал: 2_Пополнение и вывод
  @smoke
  @Deposit_C1044
  Сценарий: Пополнение счёта.

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Вход"
    * открывается страница "Вход"

    * пользователь (логинится с) данными
      | Логин  | Default  |
      | Пароль | Default  |

    * открывается страница "Авторизованная Главная страница"
    * пользователь (запоминает значение баланса) "в рублях"

    * пользователь (нажимает кнопку) "Иконка юзера"
    * открывается страница "Мини Личный Кабинет"

    * пользователь (нажимает кнопку) "Пополнение"
    * пользователь (вводит сумму и выбирает способ пополнения c) данными
    | СУММА  | 1000     |
    | Способ | VISA МИР |

    * открывается страница "Первый ЦУПИС"
    * пользователь (логинится в ЦУПИС с) данными
      | Телефон  | Default  |
      | Пароль   | Default  |

    * пользователь (нажимает кнопку) "Войти"
    * открывается страница "Первый ЦУПИС ЛК"

    * пользователь (вводит случайное число в CVV)
    * пользователь (нажимает кнопку) "Продолжить"

    * открывается страница "АО Кулькофф Банк"
    * пользователь (нажимает кнопку) "Подтвердить"

    * открывается страница "Первый ЦУПИС ЛК"
    * пользователь (нажимает кнопку 'Вернуться к букмекеру')

    * открывается страница "PopUp Заключить Пари"
    * пользователь (нажимает кнопку) "Заключить пари"

    * открывается страница "Авторизованная Главная страница"
    * пользователь (проверяет, увеличился ли баланс на) "СУММА"

    * закрываем браузер
