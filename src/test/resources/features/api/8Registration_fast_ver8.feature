# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | DIFFFIRSTNAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | ONEBIRTHDATE | randomDate |
    * сохраняем в память
      | DIFFBIRTHDATE | randomDate |
    * сохраняем в память
      | DEVID  | randomNumber 4 |


    * определяем незанятый номер телефона и сохраняем в "PHONE"
    * сохраняем в память
      | EMAIL  | randomEmail |

    * сохраняем в память
      | PASSWORD  | Default |
    * сохраняем в память
      | ISSUEPLACE  | random |
    * сохраняем в память
      | CITY  | random |
    * сохраняем в память
      | STREET  | random |
    * сохраняем в память
      | BIRTHPLACE  | random |
    * сохраняем в память
      | CODEPLACE  | randomNumber 6 |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | DOCNUM  | randomNumber 6 |
    * сохраняем в память
      | DOCSERIES  | randomNumber 4 |
    * сохраняем в память
      | FLAT  | randomNumber 2 |
    * сохраняем в память
      | GENDER | randomSex |
    * сохраняем в память
      | SKYPELOGIN | skypeLoginGenerate |
    * сохраняем в память
      | INN | 775459885706 |
    * сохраняем в память
      | SNILS | 37487545236 |


  @api
  @Registration_fast_ver8
  Сценарий: Мобильная регистрация ver8 быстрая для пользоваетеля full/alt

#    * включаем экспресс-регистрацию

    * редактируем некоторые активные опции сайта
      |fast_registration  | true  |

    * выбираем fullalt пользователя "PHONE" "BIRTHDATE"
    * сохраняем в память
      | EMAIL  | randomEmail |

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" "лгпго"

    * запрос к API "api/mobile/v3/createUserFast" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  first_name            | FIRSTNAME  |
      |  birth_date            | BIRTHDATE  |
      |  phone                 | PHONE      |
      |  phoneConfirmationCode | CODE       |
      |  email                 | EMAIL      |
      |  pass                  | PASSWORD   |
      |  subscribe_to_sms      | false      |
      |  subscribe_to_email    | false      |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"

    * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "RESPONCE_API":
      | code   | CODEEMAIL |
      | source | 16        |


    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":15 |


  @api
  @Registration_fast_ver8
  Сценарий: Мобильная регистрация ver8 быстрая для пользоваетеля unknown

#    * включаем экспресс-регистрацию

    * редактируем некоторые активные опции сайта
      |fast_registration  | true  |

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" "лгпго"

    * запрос к API "api/mobile/v3/createUserFast" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  first_name            | FIRSTNAME  |
      |  phone                 | PHONE      |
      |  phoneConfirmationCode | CODE       |
      |  email                 | EMAIL      |
      |  pass                  | PASSWORD   |
      |  subscribe_to_sms      | false      |
      |  subscribe_to_email    | false      |
      |  birth_date            | ONEBIRTHDATE  |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"

    * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "RESPONCE_API":
      | code   | CODEEMAIL |
      | source | 16        |


    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":10 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"

#    отправим новые значения для ДР и для имени. в конце будем проверять что имя - перезатерлось. а ДР проигнорировалось
    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
      |  name                   | DIFFFIRSTNAME  |
      |  surname                | SURNAME        |
      |  middleName             | PATRONYMIC     |
      |  birthdate              | DIFFBIRTHDATE  |
      | citizenship             | rus            |
      | gender                  | GENDER         |
      | birthplace              | BIRTHPLACE     |
      | region                  | Москва         |
      | locality                | CITY           |
      | street                  | STREET         |
      | house                   | HOUSE          |
      | construction            |                |
      | housing                 |                |
      | flat                    | FLAT           |
      | docNum                  | DOCNUM         |
      | docSeries               | DOCSERIES      |
      | docType                 | 1              |
      | issueDate               | VALIDISSUEDATE |
      | issuePlace              | ISSUEPLACE     |
      | codePlace               | 123-456        |
      | publicPerson            | 1              |
      | regionKLADR             | 77             |

    * запрос к API "api/mobile/v8/submitPersonalData" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | personalData            | PERSONALDATA |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":11 |

    * проверим что в БД сохранены правильные значения
      | gender                  | GENDER          |
      | birthplace              | BIRTHPLACE      |
      | region                  | Москва          |
      | regionKLADR             | 77              |
      | locality                | CITY            |
      | street                  | STREET          |
      | house                   | HOUSE           |
      | flat                    | FLAT            |
      | docNum                  | DOCNUM          |
      | docSeries               | DOCSERIES       |
      | issueDate               | VALIDISSUEDATE  |
      | issuePlace              | ISSUEPLACE      |
      | codePlace               | 123-456         |
      | first_name              | DIFFFIRSTNAME   |
      | surname                 | SURNAME         |
      | patronymic              | PATRONYMIC      |
      | birth_date              | ONEBIRTHDATE    |
      | gender                  | GENDER          |
      | phone                   | PHONE           |
      | email                   | EMAIL           |