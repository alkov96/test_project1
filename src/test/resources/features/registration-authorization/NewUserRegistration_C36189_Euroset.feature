# language: ru
Функционал: 1_Регистрация и авторизация

  Предыстория:
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
      | Фамилия        | random     | LASTNAME              |
      | Имя            | random     | FIRSTNAME             |
      | Отчество       | random     | PATERNALNAME          |
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

  @before
  @after
  @smoke
    @rega
  @NewUserRegistration_C36189_Euroset
  Сценарий: Регистрация нового пользователя через Евросеть

    * редактируем некоторые активные опции сайта
      |identification_with_euroset|true|
      |identification_with_skype_only|false|

    * открывается страница "Способ подтверждения личности"
    * пользователь (нажимает на 'Связной|Евросеть')

    * пользователь (отображается текст) "Проверка документов через «Связной | Евросеть»"
    * запоминаем текущую страницу в "CURRENT_PAGE"

    * пользователь открывает новое окно с url "http://88.198.200.81:27000/testservice/"

    * (завершает регу в Евросети с) данными
      | Идентификатор мерчанта       |  MERCHANT        |
      | Код/логин/ид/имя пользователя|  PHONE                |
      | Способ идентификации         |  betshop (ППС (ЦОК))  |
      | button                       |  Полная идентификация |
      | textis                       |  ok                   |

    * открывается страница "Авторизованная Главная страница"

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






