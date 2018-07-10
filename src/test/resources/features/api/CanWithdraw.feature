# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASS  | Default |
    * сохраняем в память
      | ISSUEPLACER  | random |
    * сохраняем в память
      | CITYR  | random |
    * сохраняем в память
      | STREETR  | random |
    * сохраняем в память
      | BIRTHPLACER  | random |
    * сохраняем в память
      | HOUSER  | randomNumber 2 |
    * сохраняем в память
      | DOCNUMR  | randomNumber 6 |
    * сохраняем в память
      | DOCSERIESR  | randomNumber 4 |
    * сохраняем в память
      | FLATR  | randomNumber 2 |
    * сохраняем в память
      | GENDERR | randomSex |
    * сохраняем в память
      | FIRSTNAMER | random |
    * сохраняем в память
      | LASNAMER | random |
    * сохраняем в память
      | PARONIMYCR | random |
    * сохраняем в память
      | ISSUEDATER | randomDate |

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

    * поиск акаунта со статуом регистрации "=2" "ALLROWS"
    * обновляем поля в БД для юзера "EMAIL":
      | personality_confirmed   | b'0'             |
      | birth_place             | NULL             |
      | region                  | NULL             |
      | city                    | NULL             |
      | street                  | NULL             |
      | house_number            | NULL             |
      | building                | NULL             |
      | housing                 | NULL             |
      | apartment               | NULL             |
      | passport_number         | NULL             |
      | passport_series         | NULL             |
      | passport_date           | NULL             |
      | passport_issuer         | NULL             |
      | passport_issuer_code    | NULL             |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
#сначала отправлем неправильне данные
    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
      | gender                  | GENDERR          |
      | birthplace              | BIRTHPLACER      |
      | region                  | Москва           |
      | locality                | CITYR            |
      | street                  | STREETR          |
      | house                   | HOUSER           |
      | construction            |                  |
      | housing                 |                  |
      | flat                    | FLATR            |
      | docNum                  | DOCNUMR          |
      | docSeries               | DOCSERIESR       |
      | issueDate               | ISSUEDATER       |
      | issuePlace              | ISSUEPLACER      |
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


    #а теперь данные, совпадающие с ЦУПИС
    * добавляем данные в JSON объект "PERSONALDATA" сохраняем в память:
      | gender                  | GENDERID            |
      | birthplace              | BIRTHPLACE          |
      | region                  | REGION              |
      | locality                | CITY                |
      | street                  | STREET              |
      | house                   | HOUSENUMBER         |
      | construction            | BUILDING            |
      | housing                 | HOUSING             |
      | flat                    | APARTMENT           |
      | docNum                  | PASSPORTNUMBER      |
      | docSeries               | PASSPORTSERIES      |
      | issueDate               | PASSPORTDATE        |
      | issuePlace              | PASSPORTISSUER      |
      | codePlace               | PASSPORTISSUERCODE  |

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



