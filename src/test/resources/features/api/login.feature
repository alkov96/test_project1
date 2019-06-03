# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | SOURCE | 16 |

  @api
  @login
  @correct
  Сценарий: 3_19 Аутентификация пользователя. Позитивный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | DEVID    |
      | email    | USER     |
      | pass     | PASSWORD |
      | source   | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
    | exepted | "code":0,"data":{ |

  @fail
  Сценарий: 3_19 Аутентификация пользователя. Негативный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | 0 |
      | email    | 0 |
      | pass     | 0 |
      | source   | 0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":1,"data":{"message": |

  @restassured
  Сценарий: kjkjk

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId    | DEVID    |
      | email    | USER     |
      | pass     | PASSWORD |
      | source   | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | 1 | "code":0    |
      | 2 | "status":7  |

    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | DIFFNAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | BIRTHDATE | randomDate |
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | STREET  | "Волгоградский проспект" |
    * сохраняем в память
      | FLAT  | randomNumber 3 |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | COMMENT  | random |


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

    * редактируем некоторые активные опции сайта
      |fast_registration  | false  |


    * запрос к API "api/mobile/v8/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" ""

    * запрос к API "api/mobile/v8/checkPhoneCode" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  phone                 | PHONE      |
      |  code                  | 0000       |

    * проверка ответа API из "RESPONCE_API":
      | 1 | "code":9 |
      | 2 | "message":"code incorrect" |

    * запрос к API "api/mobile/v8/checkPhoneCode" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  phone                 | PHONE      |
      |  code                  | CODE       |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v8/createUser" и сохраняем в "RESPONCE_API":
      |  devId                 | DEVID      |
      |  source                | 16         |
      |  first_name            | DIFFNAME  |
      |  phone                 | PHONE      |
      |  phoneConfirmationCode | CODE       |
      |  email                 | EMAIL      |
      |  pass                  | PASSWORD   |
      | subscribe_to_email     | false      |
      | subscribe_to_sms       | false      |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"

    * запрос к API "api/mobile/v8/confirmEmail" и сохраняем в "RESPONCE_API":
      | code   | CODEEMAIL |
      | source | 16        |

    * проверка ответа API из "RESPONCE_API":
      | 1 | "code":0 |
      | 2 | "status":10 |

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | 1 | "code":0 |
      | 2 | "status":10 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"

    * запрос к API "api/mobile/v8/getActiveSiteOptions" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | 1 | "code":0 |
      | 2 | "fast_registration","state":0 |

    * добавляем данные в JSON2 объект "PERSONALDATA" сохраняем в память:
      |  name                   | FIRSTNAME      |
      |  surname                | SURNAME        |
      |  middleName             | PATRONYMIC     |
      |  birthdate              | BIRTHDATE      |
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
      | issueDate               | INVALIDISSUEDATE |
      | issuePlace              | ISSUEPLACE     |
      | codePlace               | 123-456        |
      | publicPerson            | 1              |
      | regionKLADR             | 77             |

    * запрос к API "api/mobile/v8/submitPersonalData" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | personalData            | PERSONALDATA |

