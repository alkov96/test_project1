# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |
    * сохраняем в память
      | PASSWORD  | Default |
    * сохраняем в память
      | SOURCE | 16 |
    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | DIFFBIRTHDATE | randomDate |
    * сохраняем в память
      | FLAT  | randomNumber 3 |
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
      | GENDER | randomSex |

    * редактируем некоторые активные опции сайта
      |identification_with_skype_only|false|
      |identification_with_wave|true|
      |identification_with_video|true|
      |back_call|false|
      |announcements|true|

  @api
  @goToStep
  Сценарий: Возвращение на шаг ввода ПД

    * поиск акаунта со статуом регистрации ">=14" "EMAIL"
    * обновляем поля в БД для пользователя "EMAIL":
      | registration_stage_id           | 20            |

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASSWORD  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v8/goToStepSubmitPersonalData" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | source    | SOURCE    |
      | authToken | AUTHTOKEN |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":10 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "personalData": |

    * находим и сохраняем "BIRTHDATE" из "RESPONCE_API"

    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"

    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
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

    * проверим что в БД сохранены правильные значения
      | birth_place             | BIRTHPLACE      |
      | region                  | Москва          |
      | city                    | CITY            |
      | street                  | STREET          |
      | apartment               | FLAT            |
      | passport_number         | DOCNUM          |
      | passport_series         | DOCSERIES       |
      | passport_date           | VALIDISSUEDATE  |
      | passport_issuer         | ISSUEPLACE      |
      | passport_issuer_code    | 123-456         |
      | first_name              | FIRSTNAME       |
      | surname                 | SURNAME         |
      | patronymic              | PATRONYMIC      |
      | birth_date              | BIRTHDATE       |
      | email                   | EMAIL           |

  @api
  @goToStep
  Сценарий: Возвращение на шаг выбора способа регистрации(wave)

    * поиск акаунта со статуом регистрации ">=14" "EMAIL"
    * обновляем поля в БД для пользователя "EMAIL":
      | registration_stage_id           | 20            |

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASSWORD  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v8/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v8/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               |  1           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * ждем пока для пользователя "EMAIL" станет "registration_stage_id=12"

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASSWORD  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v8/goToStepSubmitIdentType" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | source    | SOURCE    |
      | authToken | AUTHTOKEN |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":11 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "identType": |

    * запрос к API "api/mobile/v8/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               | 3            |

    * запрос к API "api/mobile/v8/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка вариантного ответа API из "RESPONCE_API":
    | exepted     | "status":12 or "status":5 |



  @api
  @goToStep
  Сценарий: Возвращение на шаг выбора способа регистрации (dostavista)

    * поиск акаунта со статуом регистрации ">=14" "EMAIL"
    * обновляем поля в БД для пользователя "EMAIL":
      | registration_stage_id           | 20            |

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASSWORD  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v8/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v8/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               |  6           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * ждем пока для пользователя "EMAIL" станет "registration_stage_id=21"

    * запрос к API "api/mobile/v8/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASSWORD  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v8/goToStepSubmitIdentType" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | source    | SOURCE    |
      | authToken | AUTHTOKEN |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":11 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "identType": |

    * запрос к API "api/mobile/v8/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |
      | identType               | 3            |

    * запрос к API "api/mobile/v8/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка вариантного ответа API из "RESPONCE_API":
      | exepted     | "status":12 or "status":5 |