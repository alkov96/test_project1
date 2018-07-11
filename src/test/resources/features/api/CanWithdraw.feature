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
      | BIRTHPLACE | random |
    * сохраняем в память
      | HOUSER  | randomNumber 2 |
    * сохраняем в память
      | DOCNUM  | randomNumber 6 |
    * сохраняем в память
      | DOCSERIES  | randomNumber 4 |
    * сохраняем в память
      | FLAT  | randomNumber 2 |
    * сохраняем в память
      | GENDER | randomSex |
    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | LASNAME | random |
    * сохраняем в память
      | PARONIMYC | random |
    * сохраняем в память
      | ISSUEDATE | randomDate |

#  @api
#  @canWithdraw
#  @correct
#  Сценарий: Проверка доступных способов вывода для полностью зарегистрированного пользователя
#
#    * поиск акаунта со статуом регистрации "=2" "EMAIL"
#
#    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | email       | EMAIL |
#      | pass        | PASS  |
#      | source      | 16    |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |
#
#    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
#
#    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | authToken   | AUTHTOKEN |
#      | source      | 16 |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "withdrawStatus":0 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |
#
#
#  @api
#  @canWithdraw
#  @incorrect
#  Сценарий: Проверка доступных способов вывода для не до конца зарегистрированного пользователя
#
#    * поиск акаунта со статуом регистрации ">=9" "EMAIL"
#
#    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | email       | EMAIL |
#      | pass        | PASS  |
#      | source      | 16    |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |
#
#    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
#
#    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | authToken   | AUTHTOKEN |
#      | source      | 16 |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":12 |
#
#
#  @api
#  @canWithdraw
#  @correct
#  Сценарий: Проверка доступных способов вывода пользователя, не вводившего ПД (full,alternative)
#
#    * поиск пользователя проходившего ускоренную регистрацию "EMAIL"
#
#    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | email       | EMAIL |
#      | pass        | PASS  |
#      | source      | 16    |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |
#
#    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
#
#    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
#      | devId       | DEVID |
#      | authToken   | AUTHTOKEN |
#      | source      | 16 |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "withdrawStatus":1 |
#    * проверка ответа API из "RESPONCE_API":
#      | exepted     | "code":0 |

  @api
  @canWithdraw
  @correct
  Сценарий: Проверка доступных способов вывода пользователя, вводившего ПД, но не совпавшие с данными из ЦУПИС(full,alternative)

    * поиск пользователя проходившего ускоренную регистрацию "EMAIL"
    * обновляем поля в БД для юзера "EMAIL":
      | registration_stage_id         | 2             |
#      | passport_series         | NULL             |
#      | birth_place             | NULL             |
#      | region                  | NULL             |
#      | city                    | NULL             |
#      | street                  | NULL             |
#      | house_number            | NULL             |
#      | building                | NULL             |
#      | housing                 | NULL             |
#      | apartment               | NULL             |
#      | passport_date           | NULL             |
#      | passport_issuer         | NULL             |
#      | passport_issuer_code    | NULL             |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

       * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":1 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |



    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
      | gender                  | GENDER          |
      | birthplace              | BIRTHPLACE      |
      | region                  | Москва           |
      | locality                | CITY            |
      | street                  | STREET          |
      | house                   | HOUSE           |
      | construction            |                  |
      | housing                 |                  |
      | flat                    | FLAT            |
      | docNum                  | DOCNUM          |
      | docSeries               | DOCSERIES       |
      | issueDate               | ISSUEDATE       |
      | issuePlace              | ISSUEPLACE      |
      | codePlace               | 123-456          |


    * запрос к API "api/mobile/v3/submitAndCheckPersonalData" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID            |
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | personalData            | PERSONALDATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":2 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |


    * запрос к API "api/mobile/v3/requestSkypeCall" и сохраняем в "RESPONCE_API":
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | skype                   | PHONE            |


    * подтверждаем видеорегистрацию "EMAIL"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | authToken   | AUTHTOKEN |
      | source      | 16 |


    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |


  @api
  @canWithdraw
  @correct
  Сценарий: Проверка доступных способов выводв для пользователя, неподтвердившего офферту



    * поиск акаунта со статуом регистрации "=2" "ALLROWS"
    * обновляем оферту пользователю "1" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID     |
      | authToken   | AUTHTOKEN |
      | source      | 16        |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":3 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

#    * запоминаем дату рождения пользователя "BIRTHDATE" "EMAIL"
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

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/canWithdraw" и сохраняем в "RESPONCE_API":
      | devId       | DEVID     |
      | authToken   | AUTHTOKEN |
      | source      | 16        |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "withdrawStatus":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |



