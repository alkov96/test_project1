# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |
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
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | DOCNUM  | randomNumber 6 |
    * сохраняем в память
      | DOCSERIES  | randomNumber 4 |
    * сохраняем в память
      | FLAT  | randomNumber 2 |
    * сохраняем в память
      | GENDER | randomSex |

  @api
  @canDeposit
  @correct
  Сценарий: Проверка доступных способов пополнения для полностью зарегистрированного пользователя

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":8 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canDeposit" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "depositStatus":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |



  @api
  @canDeposit
  @incorrect
  Сценарий: Проверка доступных способов пополнения не до конца зарегистрированного пользователя
    * поиск акаунта со статуом регистрации ">=9" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canDeposit" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":12 |


  @api
  @canDeposit
  @correct
  Сценарий: Проверка доступных способов пополнения для пользователя, неподтвердившего офферту

    * поиск акаунта со статуом регистрации "=2" "EMAIL"
    * обновляем оферту пользователю "0" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canDeposit" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "depositStatus":1 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * запоминаем дату рождения пользователя "BIRTHDATE" "EMAIL"
    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"

    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
      | gender                  | GENDER           |
      | birthplace              | BIRTHPLACE       |
      | region                  | Москва           |
      | locality                | CITY             |
      | street                  | STREET           |
      | house                   | HOUSE            |
      | construction            |                  |
      | housing                 |                  |
      | flat                    | FLAT             |
      | docNum                  | DOCNUM           |
      | docSeries               | DOCSERIES        |
      | issueDate               | VALIDISSUEDATE   |
      | issuePlace              | ISSUEPLACE       |
      | codePlace               | 123-456          |

    * запрос к API "api/mobile/v3/submitAndCheckPersonalData" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID            |
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | personalData            | PERSONALDATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * обновляем оферту пользователю "3" "EMAIL"

    * подтверждаем видеорегистрацию "EMAIL"
    * подтверждаем от ЦУПИС "EMAIL"