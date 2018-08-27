# language: ru
Функционал: 1_Регистрация и авторизация

  Предыстория:

    * запоминаем значение активных опций сайта в "ACTIVE"
    * сохраняем в память
      | INN | 775459885706 |

    * сохраняем в память
      | SNILS | 37487545236 |
    * генерим email в "EMAIL"

    * разлогиниваем пользователя
    * открывается страница "Главная страница"

    * пользователь (нажимает кнопку) "Регистрация"
    * открывается страница "Учетная запись"

    * пользователь (заполняет форму с) данными
      | Поле ввода     | Значение | Переменная сохранения |
      | Дата рождения  | random   | BIRTHDATE             |
      | Фамилия        | random   | LASTNAME              |
      | Имя            | random   | FIRSTNAME             |
      | Отчество       | random   | PATERNALNAME          |
      | E-mail         | EMAIL    | EMAIL                 |
      | Пароль         | Default  | PASSWORD              |
      | Номер телефона | random   | PHONE                 |

    * пользователь (отмечает признак) "Чекбокс оферты"

    * пользователь (нажимает кнопку) "Отправить"
    * открывается страница "Поздравляем!"

    * пользователь (нажимает кнопку) "Ок"

    * пользователь (завершает регистрацию перейдя по ссылке в) "EMAIL"

    * открывается страница "Вход"
    * пользователь (логинится с) данными
      | Логин  | EMAIL    |
      | Пароль | Default  |

    * открывается страница "Учётная запись подтверждена"
    * пользователь (нажимает кнопку) "Продолжить"

    * открывается страница "Паспортные данные"
    * пользователь (заполняет паспорт с) данными
      | Поле ввода        | Значение | Переменная сохранения |
      | Серия             | random   | SERIES                |
      | Номер             | random   | NUMBER                |
      | Дата выдачи       | random   | ISSUEDATE             |
      | Кем выдан         | ОВД      | ISSUER                |
      | Код подразделения | random   | ISSUERCODE            |
      | Пол               | random   | SEX                   |
      | Место рождения    | random   | BIRTHLACATION         |
      | Регион            | true     | REGION                |
      | Нас. пункт        | true     | TOWN                  |
      | Улица             | true     | STREET                |
      | Дом/владение      | random   | HOUSE                 |
      | Строение          | random   | null                  |
      | Корпус            | random   | null                  |
      | Квартира          | random   | FLAT                  |


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

  @smoke
  @NewUserRegistration_C36189
  Сценарий: Регистрация нового пользователя через Wave

    * переключаем регистрацию на "WAVE"
    * обновляем страницу

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает кнопку) "Столото"

    * эмулируем регистрацию через терминал Wave "api/stoloto/identification/approveUserByPhone" и сохраняем в "RESPONCE_API":
    | operationdatetime   | DATE_TIME     |
    | phone               | PHONE         |
    | firstname           | FIRSTNAME     |
    | lastname            | LASTNAME      |
    | paternalname        | PATERNALNAME  |
    | sex                 | SEX           |
    | birthdate           | BIRTHDATE     |
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

    * (закрываем окно 'Перейти в ЦУПИС' если выскочит)
    * пользователь (проверяет присутствие текста) "Вы зарегистрированы"

    * закрываем браузер

  @smoke
  @NewUserRegistration_C36189
  Сценарий: Регистрация нового пользователя через Skype

    * добавляем активную опцию сайта "identification_with_video"
    * обновляем страницу

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает на 'ИНН или СНИЛС')
    * открывается страница "ИНН или СНИЛС"

    * пользователь (заполняет одно из двух полей) "SNILS" "INN"
    * пользователь (нажимает кнопку) "Продолжить"

    * подтверждаем видеорегистрацию "EMAIL"
    * подтверждаем от ЦУПИС "EMAIL"

    * обновляем страницу

    * (закрываем окно 'Перейти в ЦУПИС' если выскочит)
    * пользователь (проверяет присутствие текста) "Вы зарегистрированы"

    * закрываем браузер

  @smoke
  @NewUserRegistration_C36189
  Сценарий: Регистрация нового пользователя через Евросеть

    * добавляем активную опцию сайта "identification_with_euroset"
    * обновляем страницу

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает на 'Связной|Евросеть')

    * пользователь (отображается текст) "Проверка документов через «Связной | Евросеть»"
    * запоминаем текущую страницу в "CURRENT_PAGE"

    * открывается копия страницы в новой вкладке
    * пользователь открывает новый url "http://88.198.200.81:27000/testservice/"

    * открывается страница "Тестовый сервис"
    * пользователь (вводит в поле) "Идентификатор мерчанта" "favorit_test"

    * пользователь (вводит в поле) "Код/логин/ид/имя пользователя" "EMAIL"
    * пользователь (выбирает) "Способ идентификации" "betshop (ППС (ЦОК))"

    * пользователь (нажимает кнопку) "Полная идентификация"
    * пользователь (отображается текст) "ok"

    * закрываем текущее окно и возвращаемся на "CURRENT_PAGE"
    * (закрываем окно 'Перейти в ЦУПИС' если выскочит)

    * пользователь (проверяет присутствие текста) "Вы зарегистрированы"
    * закрываем браузер





