# language: ru
Функционал: 1.Регистрация и авторизация.
  Предыстория:

    * сохраняем в память
      | ИНН | 775459885706 |

    * сохраняем в память
      | СНИЛС | 37487545236 |

    * генерим email в "EMAIL"

  @smoke
  @regress
  @NewUserRegistration_C36189
  Сценарий: Регистрация нового пользователя.

#    * переходит на главную страницу 'site1'
    * переходит на главную страницу
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Регистрация"
    * открывается страница "Учетная запись"

    * пользователь (заполняет форму с) данными
    | Дата рождения  | random   |
    | ФИО            | random   |
    | E-mail         | EMAIL    |
    | Пароль         | 1QAZ2wsx |
    | Номер телефона | random   |

    * пользователь (отмечает признак) "Чекбокс оферты"

    * пользователь (нажимает кнопку) "Отправить"
    * открывается страница "Поздравляем!"

    * пользователь (нажимает кнопку) "Ок"
    * пользователь (завершает регистрацию перейдя по ссылке в) "EMAIL"

    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | EMAIL    |
      | Пароль | 1QAZ2wsx |

    * пользователь (нажимает кнопку) "Войти"

    * открывается страница "Учётная запись подтверждена"
    * пользователь (нажимает кнопку) "Продолжить"

    * открывается страница "Паспортные данные"
    * пользователь (заполняет паспорт с) данными
    | Серия             | random |
    | Номер             | random |
    | Дата выдачи       | random |
    | Кем выдан         | ОВД    |
    | Код подразделения | random |
    | Пол               | random |
    | Место рождения    | random |
    | Регион            | true   |
    | Нас. пункт        | true   |
    | Улица             | true   |
    | Дом/владение      | random |
    | Строение          | random |
    | Корпус            | random |
    | Квартира          | random |

    * пользователь (нажимает кнопку) "Отправить"
    * открывается страница "ИНН или СНИЛС"

    * пользователь (заполняет одно из двух полей) "СНИЛС" "ИНН"

    * пользователь (нажимает кнопку) "Продолжить"
    * открывается страница "Видеозвонок"

    * подтверждаем видеорегистрацию "EMAIL"
    * подтверждаем от ЦУПИС "EMAIL"

    * пользователь (нажимает кнопку) "Продолжить регистрацию"
    * открывается страница "Авторизованная Главная страница"

    * пользователь (проверяет присутствие текста) "Вы зарегистрированы"
    * (закрываем браузер)







