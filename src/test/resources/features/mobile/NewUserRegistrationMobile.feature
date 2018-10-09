# language: ru
Функционал: Мобильная версия

  Предыстория:
    * запоминаем значение активных опций сайта в "ACTIVE"
    * сохраняем в память
      | INN | 775459885706 |

    * сохраняем в память
      | SNILS | 37487545236 |
    * генерим email в "EMAIL"

    * определяем незанятый номер телефона и сохраняем в "PHONE"

    * генерируем дату рождения от 18 до 100 лет и сохраняем в "BIRTH_DATE"
    * генерируем дату выдачи паспорта в зависимости от "BIRTH_DATE" и сохраняем в "ISSUEDATE"

    * разлогиниваем пользователя
    * открывается страница "Мобильная главная страница"

    * пользователь (нажимает кнопку) "Профиль"
    * открывается страница "Мобильный вход"

    * пользователь (нажимает кнопку) "Регистрация"
    * открывается страница "Учетная запись мобильная"

    * пользователь (нажимает в поле ввода) "Дата рождения"
    * открывается страница "Datepicker"

    * пользователь (выбирает дату из) "BIRTH_DATE"
    * пользователь (нажимает кнопку) "OK"

    * открывается страница "Учетная запись мобильная"
    * пользователь (заполняет мобильную форму с) данными
      | Поле ввода        | Значение | Переменная сохранения |
      | Фамилия           | random   | LASTNAME              |
      | Имя               | random   | FIRSTNAME             |
      | Отчество          | random   | PATERNALNAME          |
      | Эл. почта         | EMAIL    | EMAIL                 |
      | Мобильный телефон | PHONE    | PHONE                 |
      | Пароль            | Default  | PASSWORD              |

    * пользователь (отмечает признак) "Регистрируясь, я подтверждаю верность"

    * пользователь (нажимает кнопку) "ПРОДОЛЖИТЬ"
    * (текст появляется на странице) "Подтверждение эл. почты"

    * пользователь (завершает регистрацию перейдя по ссылке в) "EMAIL"
    * (текст появляется на странице) "Учетная запись подтверждена"

    * пользователь (нажимает кнопку) "ПРОДОЛЖИТЬ"
    * открывается страница "Мобильный вход"

    * пользователь (залогинивается с мобильными) данными
      | E-mail | EMAIL    |
      | Пароль | PASSWORD |
    * пользователь (нажимает кнопку) "ВОЙТИ"

    * открывается страница "Укажите паспортные данные"
    * пользователь (нажимает в поле ввода) "Дата выдачи"

    * открывается страница "Datepicker"
    * пользователь (выбирает дату из) "ISSUEDATE"
    * пользователь (нажимает кнопку) "OK"

    * открывается страница "Укажите паспортные данные"
    * пользователь (заполняет паспортные данные с) данными
      | Поле ввода        | Значение | Переменная сохранения |
      | Серия             | random   | SERIES                |
      | Номер             | random   | NUMBER                |
      | Кем выдан         | ОВД      | ISSUER                |
      | Код подразделения | random   | ISSUERCODE            |
      | Место рождения    | random   | BIRTHLACATION         |
      | Регион            | true     | REGION                |
      | Нас. пункт        | true     | TOWN                  |
      | Улица             | true     | STREET                |
      | Дом/владение      | random   | HOUSE                 |

    * открывается страница "Укажите паспортные данные"
    * пользователь (нажимает кнопку) "ОТПРАВИТЬ"

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

  @mobile
  @MobileNewUserRegistration_C36189
  Сценарий: Регистрация нового пользователя через Wave
    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      | fast_registration              | true  |
      | identification_with_video      | false |
      | identification_with_euroset    | false |
      | identification_with_skype_only | false |
    * пользователь (перезагружает страницу)

    * (текст появляется на странице) "Шаг 3 из 3. Идентификация"
    * (текст появляется на странице) "«Столото»"

    * эмулируем регистрацию через терминал Wave "api/stoloto/identification/approveUserByPhone" и сохраняем в "RESPONCE_API":
      | operationdatetime   | DATE_TIME     |
      | phone               | PHONE         |
      | firstname           | FIRSTNAME     |
      | lastname            | LASTNAME      |
      | paternalname        | PATERNALNAME  |
      | sex                 | "Мужской"     |
      | birthdate           | BIRTH_DATE    |
      | birthlocation       | BIRTHLACATION |
      | citizenship         | "RUS"         |
      | publicperson        | null          |
      | publicperson        | null          |
      | address             | ADDRESS       |
      | documents           | DOCUMENTS     |
      | operationofficecode | "222"         |
      | operatorlogin       | "333"         |
      | inn                 | INN           |
      | SNILS               | SNILS         |
      | method              | betshop       |
      | error               | ""            |
      | reason              | ""            |
      | identityState       | "LIMITED"     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "state":"ok" |

    * (текст появляется на странице) "Вы зарегистрированы"
    * закрываем браузер



