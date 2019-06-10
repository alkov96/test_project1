# language: ru
Функционал: 1_Регистрация и авторизация

  Предыстория:

    * редактируем некоторые активные опции сайта
      |fast_registration  | true  |

    * сохраняем в память
      | INN | 775459885706 |

    * сохраняем в память
      | MERCHANT | Default |

    * сохраняем в память
      | SNILS | 37487545236 |
    * генерим email в "EMAIL"

    * определяем незанятый номер телефона и сохраняем в "PHONE"

    * генерируем дату рождения от 18 до 50 лет и сохраняем в "BIRTH_DATE"
    * генерируем дату выдачи паспорта в зависимости от "BIRTH_DATE" и сохраняем в "ISSUE_DATE"

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Регистрация"
    * открывается страница "Учетная запись"

    * пользователь (заполняет форму с) данными
      | Поле ввода     | Значение   | Переменная сохранения |
      | Дата рождения  | BIRTH_DATE | BIRTHDATE             |
      | Имя            | random     | FIRSTNAME             |
      | E-mail         | EMAIL      | EMAIL                 |
      | Пароль         | Default    | PASSWORD              |
      | Номер телефона | PHONE      | PHONE                 |

    * пользователь (отмечает признак) "Чекбокс оферты"

    * пользователь (нажимает кнопку) "Отправить"
    * открывается страница "Поздравляем!"

    * пользователь (нажимает кнопку) "Ок"

    #* пользователь (завершает регистрацию перейдя по ссылке в) "EMAIL"
    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"
    * (завершает регистрацию перейдя по ссылке для БД)

    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | EMAIL    |
      | Пароль | Default  |

    * открывается страница "Учётная запись подтверждена"
    * пользователь (нажимает кнопку) "Продолжить"

    * открывается страница "Паспортные данные"
    * пользователь (заполняет паспорт с) данными
      | Поле ввода        | Значение   | Переменная сохранения |
      | Фамилия           | random     | LASTNAME              |
      | Имя               | FIRSTNAME  | FIRSTNAME             |
      | Отчество          | random     | PATERNALNAME          |
      | Серия             | random     | SERIES                |
      | Номер             | random     | NUMBER                |
      | Дата выдачи       | ISSUE_DATE | ISSUEDATE             |
      | Кем выдан         | ОВД        | ISSUER                |
      | Код подразделения | random     | ISSUERCODE            |
      | Пол               | random     | SEX                   |
      | Место рождения    | random     | BIRTHLOCATION         |
      | Регион            | true       | REGION                |
      | Нас. пункт        | true       | TOWN                  |
      | Улица             | true       | STREET                |
      | Дом/владение      | random     | HOUSE                 |
      | Строение          | random     | null                  |
      | Корпус            | random     | null                  |
      | Квартира          | random     | FLAT                  |


    * добавляем данные в JSON объект "ADDRESS" сохраняем в память:
      | regionKLADR       | 77            |
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

  @enabledFeatures
  @smoke
  @rega
  @NewRegistration_fast_unknown
  Сценарий: Регистрация нового пользователя через Wave
    * сохраняем в память
      | CITIZENSHIP | RUS |
    * сохраняем в память
      | IDENTITYSTATE | LIMITED |
    * сохраняем в память
      | GENDER | FEMALE |

    * редактируем некоторые активные опции сайта
      |identification_with_skype_only|false|
      |identification_with_wave|true|

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает кнопку) "Столото"

    * запрос к API "api/stoloto/identification/approveUserByPhone" и сохраняем в "RESPONCE_API":
      | operationdatetime   | DATE_TIME     |
      | phone               | PHONE         |
      | firstname           | FIRSTNAME     |
      | lastname            | LASTNAME      |
      | paternalname        | PATERNALNAME  |
      | sex                 | GENDER        |
      | birthdate           | BIRTHDATE     |
      | birthlocation       | BIRTHLOCATION |
      | citizenship         | CITIZENSHIP   |
      | publicperson        | null          |
      | publicperson        | null          |
      | address             | ADDRESS       |
      | documents           | DOCUMENTS     |
      | operationofficecode | 222           |
      | operatorlogin       | 333           |
      | inn                 | INN           |
      | SNILS               | SNILS         |
      | method              | betshop       |
      | error               |               |
      | reason              |               |
      | identityState       | IDENTITYSTATE |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "state":"ok" |

    * (закрываем окно 'Перейти в ЦУПИС' если выскочит)
    * пользователь (проверяет присутствие текста) "Вы зарегистрированы"

    * пользователь (нажимает кнопку) "Иконка юзера"

    * открывается страница "Мини Личный Кабинет"
    * пользователь (переходит по ссылке) "Профиль"

    * открывается страница "Профиль"
    * пользователь (сравнивает значения в ЛК с тем, с которыми пользователь регистрировался) данными
      | Электронная почта | EMAIL         |
      | Телефон           | PHONE         |
      | Фамилия           | LASTNAME      |
      | Имя               | FIRSTNAME     |
      | Отчество          | PATERNALNAME  |
      | Дата рождения     | BIRTH_DATE    |
      | Место рождения    | BIRTHLOCATION |
      | Регион            | REGION        |
      | Нас. пункт        | TOWN          |
      | Улица             | STREET        |
      | Дом/владение      | HOUSE         |
      | Квартира          | FLAT          |
      | Серия             | SERIES        |
      | Номер             | NUMBER        |
      | Дата выдачи       | ISSUEDATE     |
      | Кем выдан         | ISSUER        |
      | Код подразд.      | ISSUERCODE    |





