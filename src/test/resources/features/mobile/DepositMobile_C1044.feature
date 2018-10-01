# language: ru
Функционал: Мобильная версия
  Предыстория:
    * сохраняем в память
      | СУММА | 1050 |

    * генерим номер карты и сохраняем в "CARD_NUMBER"
    * генерим дату действия краты в "CARD_DATE"
    * транслируем имя и фамилию на латинском в "NAME_FAMILY"

  @mobile
  @DepositMobile_C1044
  Сценарий: Пополнение счёта.
    * разлогиниваем пользователя
    * открывается страница "Мобильная главная страница"

    * пользователь (нажимает кнопку) "Профиль"
    * открывается страница "Мобильный вход"

    * пользователь (залогинивается с мобильными) данными
      | E-mail | Default |
      | Пароль | Default |
    * пользователь (нажимает кнопку) "ВОЙТИ"


    * открывается страница "Авторизованная мобильная главная страница"
    * (записываем значение баланса в) "BALANCE"

    * пользователь (кликает по ссылке) "Баланс"
    * открывается страница "Способы пополнения"

    * пользователь (нажимает кнопку) "VISA_МИР"
    * пользователь (заполняет поле 'Сумма депозита') "СУММА"


    * пользователь (нажимает кнопку) "Далее"

    * открывается страница "Первый ЦУПИС"
    * пользователь (логинится в ЦУПИС с) данными
      | Телефон  | Default  |
      | Пароль   | Default  |

    * пользователь (нажимает кнопку) "Войти"
    * открывается страница "Первый ЦУПИС ЛК"

    * (заполняем форму банковской карты) данными
    | Номер карты             | CARD_NUMBER |
    | ММ/YY                   | CARD_DATE   |
    | Имя и фамилия латиницей | NAME_FAMILY |

    * пользователь (вводит случайное число в CVV)


    * пользователь (нажимает кнопку) "Продолжить"

    * пользователь (проверяет успешность операции)

    * открывается страница "АО Кулькофф Банк"
    * пользователь (нажимает кнопку) "Подтвердить"

    * открывается страница "Первый ЦУПИС ЛК"
    * пользователь (нажимает кнопку 'Вернуться к букмекеру')

    * открывается страница "PopUp Заключить Пари"
    * пользователь (нажимает кнопку) "Заключить пари"

    * открывается страница "Авторизованная мобильная главная страница"
    * пользователь (проверяет увеличение баланса) "BALANCE" "СУММА"

    * закрываем браузер
