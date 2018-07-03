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

    * сохраняем в память
      | PHONE  | randomPhone |

    * сохраняем в память
      | EMAIL  | randomEmail|

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


  @api
  @Registration_mobile
  Сценарий: Мобильная регистрация

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
      | exepted | "status":10 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASS  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":10 |


    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

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
      | issueDate               | INVALIDISSUEDATE |
      | issuePlace              | ISSUEPLACE       |
      | codePlace               | CODEPLACE        |

    * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID            |
      | authToken               | AUTHTOKEN        |
      | source                  | 16               |
      | personalData            | PERSONALDATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"status":10,"tsupisStatus":3}} |


#    * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "responceAPI":
#      | devId                   | DEVID           |
#      | authToken               | AUTHTOKEN           |
#      | source                  | 16              |
#      | gender                  | MALE            |
#      | birthplace              | BIRTHPLACE      |
#      | region                  | Москва          |
#      | locality                | CITY            |
#      | street                  | STREET          |
#      | house                   | HOUSE           |
#      | construction            |                 |
#      | housing                 |                 |
#      | flat                    | FLAT            |
#      | docNum                  | DOCNUM          |
#      | docSeries               | DOCSERIES       |
#      | issueDate               | INVALIDISSUEDATE|
#      | issuePlace              | ISSUEPLACE      |
#      | codePlace               | CODEPLACE       |
