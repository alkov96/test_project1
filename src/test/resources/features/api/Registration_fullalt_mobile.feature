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
    * сохраняем в память
      | INN | 775459885706 |
    * сохраняем в память
      | SNILS | 37487545236 |


  @api
  @smoke
  @Registration_fullalt_mobile
  Сценарий: Мобильная регистрация быстрая

    * включаем экспресс-регистрацию
    * выбираем fullalt пользователя "PHONE" "BIRTHDATE"
    * сохраняем в память
      | EMAIL  | randomEmail |

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | DEVID |
      | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "PHONE" "лгпго"

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
      | pass   | PASS  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":15 |
