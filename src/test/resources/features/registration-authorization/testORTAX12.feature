# language: ru
Функционал: 1_Регистрация и авторизация

  Предыстория:

    * сохраняем в память
      | INN | 775459885706 |

    * сохраняем в память
      | SNILS | 37487545236 |

    * определяем незанятый номер телефона и сохраняем в "PHONE"
    * генерим email в "EMAIL"

    * генерируем дату рождения от 18 до 50 лет и сохраняем в "BIRTH_DATE"
    * генерируем дату выдачи паспорта в зависимости от "BIRTH_DATE" и сохраняем в "ISSUE_DATE"

    * выбираем ФИО "FIRSTNAME" "LASTNAME" "PATERNALNAME"
    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Регистрация"
    * открывается страница "Учетная запись"

    * пользователь (заполняет форму с) данными
      | Поле ввода     | Значение      | Переменная сохранения |
      | Дата рождения  | BIRTH_DATE    | BIRTHDATE             |
      | Фамилия        | LASTNAME      | LASTNAME              |
      | Имя            | FIRSTNAME     | FIRSTNAME             |
      | Отчество       | PATERNALNAME  | PATERNALNAME          |
      | E-mail         | EMAIL         | EMAIL                 |
      | Пароль         | Default       | PASSWORD              |
      | Номер телефона | PHONE         | PHONE                 |

    * пользователь (отмечает признак) "Чекбокс оферты"

    * пользователь (нажимает кнопку) "Отправить"
    * открывается страница "Поздравляем!"

    * пользователь (нажимает кнопку) "Ок"

    #* пользователь (завершает регистрацию перейдя по ссылке в) "EMAIL"
     * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"
    * (завершает регистрацию перейдя по ссылке для БД)

#    * пользователь (нажимает кнопку) "Вход"

    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | EMAIL    |
      | Пароль | Default  |

    * открывается страница "Учётная запись подтверждена"
    * пользователь (нажимает кнопку) "Продолжить"

    * открывается страница "Паспортные данные"
    * пользователь (заполняет паспорт с) данными
      | Поле ввода        | Значение     | Переменная сохранения |
      | Серия             | random       | SERIES                |
      | Номер             | random       | NUMBER                |
      | Дата выдачи       | ISSUE_DATE   | ISSUEDATE             |
      | Кем выдан         | ОВД          | ISSUER                |
      | Код подразделения | random       | ISSUERCODE            |
      | Пол               | random       | SEX                   |
      | Место рождения    | random       | BIRTHLACATION         |
      | Регион            | true         | REGION                |
      | Нас. пункт        | true         | TOWN                  |
      | Улица             | true         | STREET                |
      | Дом/владение      | random       | HOUSE                 |
      | Строение          | random       | null                  |
      | Корпус            | random       | null                  |
      | Квартира          | random       | FLAT                  |


    * добавляем данные в JSON объект "ADDRESS" сохраняем в память:
      | regionKLADR       | null          |
      | region            | REGION        |
      | town              | TOWN          |
      | street            | STREET        |
      | building          | HOUSE         |
      | bulk              | null          |
      | flat              | FLAT          |

    * добавляем данные в JSON массив "DOCUMENTS" сохраняем в память:
      | type       | passportRus |
      | series     | SERIES      |
      | number     | NUMBER      |
      | issuer     | ISSUER      |
      | issuedate  | ISSUEDATE   |
      | validto    | null        |
      | issuercode | ISSUERCODE  |

    * запрашиваем дату-время и сохраняем в память
      | DATE_TIME | Current |

    * нажмем Продолжить регу на всякий

  @trt12
  @ortax
  Сценарий: Регистрация нового пользователя до шага 12

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает кнопку) "Столото"


