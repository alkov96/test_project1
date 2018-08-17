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
  | PASSWORD  | Parol123 |
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


  * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
  | devId | DEVID |
  | phone | PHONE |

  * проверка ответа API из "RESPONCE_API":
  | exepted | "code":0 |

  * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" ""

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
  |  pass                  | PASSWORD   |

  * проверка ответа API из "RESPONCE_API":
  | exepted | "code":0 |

  * получаем и сохраняем в память код "CODEEMAIL" подтверждения почты "EMAIL"

  * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "RESPONCE_API":
  | code   | CODEEMAIL |
  | source | 16        |

  * проверка ответа API из "RESPONCE_API":
  | exepted | "code":0 |
  * проверка ответа API из "RESPONCE_API":
  | exepted | "status":10 |

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



  * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
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
  | issueDate               | INVALIDISSUEDATE |
  | issuePlace              | ISSUEPLACE     |
  | codePlace               | 123-456        |

  * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "RESPONCE_API":
  | devId                   | DEVID        |
  | authToken               | AUTHTOKEN    |
  | source                  | 16           |
  | personalData            | PERSONALDATA |

  * проверка ответа API из "RESPONCE_API":
  | exepted | "code":27 |


  * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
  | gender                  | GENDER         |
  | birthplace              | BIRTHPLACE     |
  | region                  | Москва         |
  | regionKLADR             | 77             |
  | locality                | CITY           |
  | street                  | STREET         |
  | house                   | HOUSE          |
  | construction            |                |
  | housing                 |                |
  | flat                    | FLAT           |
  | docNum                  | DOCNUM         |
  | docSeries               | DOCSERIES      |
  | issueDate               | VALIDISSUEDATE |
  | issuePlace              | ISSUEPLACE     |
  | codePlace               | 123-456        |

  * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "RESPONCE_API":
  | devId                   | DEVID        |
  | authToken               | AUTHTOKEN    |
  | source                  | 16           |
  | personalData            | PERSONALDATA |

  * проверка ответа API из "RESPONCE_API":
  | exepted | "code":0 |
  * проверка ответа API из "RESPONCE_API":
  | exepted | "status":11 |


  @api
  @smoke
  @Registration_mobile
  Сценарий: Мобильная регистрация полная через WAVE

    * запоминаем значение активных опций сайта "ACTIVE"
    * запрос к API "api/mobile/v3/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v3/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               |  1           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |


    * запрос к API "mobile/v5/sendEmailInstructions" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "errorMsg":null |


    * приводим дату к формату год-месяц-день "BIRTHDATE"
    * приводим дату к формату год-месяц-день "VALIDISSUEDATE"

    * добавляем данные в JSON объект "ADDRESS" сохраняем в память:
      | regionKLADR       | null          |
      | region            | Москва        |
      | town              | CITY          |
      | street            | STREET        |
      | building          | HOUSE         |
      | bulk              | null          |
      | flat              | FLAT          |

    * добавляем данные в JSON массив "DOCUMENTS" сохраняем в память:
      | type       | passportRus    |
      | series     | DOCSERIES      |
      | number     | DOCNUM         |
      | issuer     | ISSUEPLACE     |
      | issuedate  | VALIDISSUEDATE |
      | validto    | null           |
      | issuercode | 123-456        |

    * запрашиваем дату-время и сохраняем в память
      | DATE_TIME | Current |

    * эмулируем регистрацию через терминал Wave "api/stoloto/identification/approveUserByPhone" и сохраняем в "RESPONCE_API":
      | operationdatetime   | DATE_TIME     |
      | phone               | PHONE         |
      | firstname           | FIRSTNAME     |
      | lastname            | PATRONYMIC    |
      | paternalname        | PATERNALNAME  |
      | sex                 | GENDER        |
      | birthdate           | BIRTHDATE     |
      | birthlocation       | BIRTHPLACE    |
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


    * запрос к API "api/mobile/v3/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * подтверждаем от ЦУПИС "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL    |
      | pass   | PASSWORD |
      | source | 16       |


    * запрос к API "api/mobile/v3/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * выставляем обратно старое значение активных опций сайта "ACTIVE"


  @api
  @smoke
  @Registration_mobile
  Сценарий: Мобильная регистрация полная через SKYPE

    * добавляем активную опцию сайта "identification_with_video"

    * запрос к API "api/mobile/v3/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v3/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               |  3           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v3/submitInnSnils" и сохраняем в "RESPONCE_API":
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | snilsNumber             | "000-000-000 00" |
      | innNumber               |                  |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":5 |

    * запрос к API "api/mobile/v3/requestSkypeCall" и сохраняем в "RESPONCE_API":
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | skype                   | SKYPELOGIN       |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":6   |

    * находим и сохраняем "TIMELEFT" из "RESPONCE_API"

    * ожидание "2" сек

    * запрос к API "api/mobile/v3/requestSkypeTimeLeft" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID            |
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":6 |

    * смотрим изменился ли "TIMELEFT" из "RESPONCE_API"

    * подтверждаем видеорегистрацию "EMAIL"
    * подтверждаем от ЦУПИС "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | 16    |


    * запрос к API "api/mobile/v3/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |




  @api
  @smoke
  @Registration_mobile
  Сценарий: Мобильная регистрация полная через Евросеть

    * добавляем активную опцию сайта "identification_with_euroset"
    * запрос к API "api/mobile/v3/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v3/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               |  2           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "mobile/v5/sendEmailInstructions" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "errorMsg":null |













