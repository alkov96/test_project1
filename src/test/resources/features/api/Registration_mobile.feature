# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | BIRTHDATE | randomDate |
    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * определяем незанятый номер телефона и сохраняем в "PHONE"
    * сохраняем в память
      | EMAIL  | randomEmail |

    * сохраняем в память
      | PASS  | Default |
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


#
#  @api
#  @Registration_mobile
#  Сценарий: Мобильная регистрация полная
#
#    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
#      | devId | DEVID |
#      | phone | PHONE |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":0 |
#
#    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE"
#
#    * запрос к API "api/mobile/v3/createUser" и сохраняем в "RESPONCE_API":
#      |  devId                 | DEVID      |
#      |  source                | 16         |
#      |  first_name            | FIRSTNAME  |
#      |  surname               | SURNAME    |
#      |  patronymic            | PATRONYMIC |
#      |  birth_date            | BIRTHDATE  |
#      |  phone                 | PHONE      |
#      |  phoneConfirmationCode | CODE       |
#      |  email                 | EMAIL      |
#      |  pass                  | PASS       |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":0 |
#
#    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"
#
#    * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "RESPONCE_API":
#      | code   | CODEEMAIL |
#      | source | 16        |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "status":10 |
#
#    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
#      | devId  | DEVID |
#      | email  | EMAIL |
#      | pass   | PASS  |
#      | source | 16    |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "status":10 |
#
#    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
#
#    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"
#
#    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
#      | gender                  | GENDER           |
#      | birthplace              | BIRTHPLACE       |
#      | region                  | Москва           |
#      | locality                | CITY             |
#      | street                  | STREET           |
#      | house                   | HOUSE            |
#      | construction            |                  |
#      | housing                 |                  |
#      | flat                    | FLAT             |
#      | docNum                  | DOCNUM           |
#      | docSeries               | DOCSERIES        |
#      | issueDate               | INVALIDISSUEDATE |
#      | issuePlace              | ISSUEPLACE       |
#      | codePlace               | 123-456        |
#
#    * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "RESPONCE_API":
#      | devId                   | DEVID            |
#      | authToken               | AUTHTOKEN        |
#      | source                  | 16               |
#      | personalData            | PERSONALDATA     |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":27 |
#
#
#
#    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
#      | gender                  | GENDER           |
#      | birthplace              | BIRTHPLACE       |
#      | region                  | Москва           |
#      | locality                | CITY             |
#      | street                  | STREET           |
#      | house                   | HOUSE            |
#      | construction            |                  |
#      | housing                 |                  |
#      | flat                    | FLAT             |
#      | docNum                  | DOCNUM           |
#      | docSeries               | DOCSERIES        |
#      | issueDate               | VALIDISSUEDATE |
#      | issuePlace              | ISSUEPLACE       |
#      | codePlace               | 123-456        |
#
#    * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "RESPONCE_API":
#      | devId                   | DEVID            |
#      | authToken               | AUTHTOKEN        |
#      | source                  | 16               |
#      | personalData            | PERSONALDATA     |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | {"code":0,"data":{"status":12,"tsupisStatus":3}} |
#
#
#
#    * запрос к API "api/mobile/v3/submitInnSnils" и сохраняем в "RESPONCE_API":
#      | authToken               | AUTHTOKEN        |
#      | source                  | 16               |
#      | snilsNumber             | "000-000-000 00" |
#      | innNumber               |                  |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | {"code":0,"data":{"status":5,"tsupisStatus":3}}   |
#
#    * запрос к API "api/mobile/v3/requestSkypeCall" и сохраняем в "RESPONCE_API":
#      | authToken               | AUTHTOKEN        |
#      | source                  | 16               |
#      | skype                   | SKYPELOGIN       |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "status":6,"tsupisStatus":3   |
#
#    * находим и сохраняем "TIMELEFT" из "RESPONCE_API"
#
#    * ожидание "2000"
#
#    * запрос к API "api/mobile/v3/requestSkypeTimeLeft" и сохраняем в "RESPONCE_API":
#      | devId                   | DEVID            |
#      | authToken               | AUTHTOKEN        |
#      | source                  | 16               |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "status":6 |
#
#    * смотрим изменился ли "TIMELEFT" из "RESPONCE_API"
#
#    * подтверждаем видеорегистрацию "EMAIL"
#    * подтверждаем от ЦУПИС "EMAIL"
#
#* запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
#      | devId  | DEVID |
#      | email  | EMAIL |
#      | pass   | PASS  |
#      | source | 16    |


  @api
  @Registration_mobile
  Сценарий: Мобильная регистрация быстрая

    * выбираем fullalt пользователя "PHONE" "BIRTHDATE"
    * сохраняем в память
      | EMAIL  | randomEmail |

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE"

    * запрос к API "api/mobile/v3/createUser" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  first_name            | FIRSTNAME  |
      |  surname               | SURNAME    |
      |  patronymic            | PATRONYMIC |
      |  birth_date            | BIRTHDATE  |
      |  phone                 | PHONE      |
      |  phoneConfirmationCode | CODE       |
      |  email                 | EMAIL      |
      |  pass                  | PASS       |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"

    * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "RESPONCE_API":
      | code   | CODEEMAIL |
      | source | 16        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":15 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASS  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":15 |


