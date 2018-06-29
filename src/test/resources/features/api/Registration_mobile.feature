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
        | BIRTHDATE | randomdate |
      * сохраняем в память
        | DEVID  | Default |
      * сохраняем в память
        | PHONE  | Default |
      * сохраняем в память
        | EMAIL  | EmailGenerate|
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
        | CODEPLACE  | randomint 6 |
      * сохраняем в память
        | HOUSE  | randomint 7  |
      * сохраняем в память
        | DOCNUM  | randomint 6 |
      * сохраняем в память
        | DOCSERIES  | randomint 4  |
      * сохраняем в память
        | FLAT  | randomint 2 |


  @api
  @Registration_mobile
  Сценарий: Мобильная регистрация

#
#    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "responceAPI":
#      |  devId                 | DEVID           |
#      |  phone                 | PHONE           |
#
#    * проверка ответа API из "responceAPI":
#      |  exepted               | "code":0        |
#
#    * получаем код подтверждения телефона "CODE"
#
#    * запрос к API "api/mobile/v3/createUser" и сохраняем в "responceAPI":
#      |  devId                 | DEVID           |
#      |  source                | 16              |
#      |  first_name            | FIRSTNAME       |
#      |  surname               | SURNAME         |
#      |  patronymic            | PATRONYMIC      |
#      |  birth_date            | BIRTHDATE       |
#      |  phone                 | PHONE           |
#      |  phoneConfirmationCode | CODE            |
#      |  email                 | EMAIL           |
#      |  pass                  | PASS            |
#
#    * проверка ответа API из "responceAPI":
#      |  exepted               | "code":0        |

#    * получаем код подтверждения почты "CODEEMAIL"
#
#    * запрос к API "api/mobile/v3/confirmEmail" и сохраняем в "responceAPI":
#      | code                    | CODEEMAIL       |
#      | source                  | 16              |
#
#    * проверка ответа API из "responceAPI":
#      |  exepted               | "code":0         |
#    * проверка ответа API из "responceAPI":
#      |  exepted               | "status":10      |

    * запрос к API "api/mobile/v3/login" и сохраняем в "responceAPI":
      | devId                   | DEVID           |
      | email                   | EMAIL           |
      | pass                    | PASS            |
      | source                  | 16              |

    * проверка ответа API из "responceAPI":
      |  exepted               | "code":0         |
    * проверка ответа API из "responceAPI":
      |  exepted               | "status":10      |


    * временная функция запоминания токена

    * определяем валидную и невалидную дату выдачи паспорта "VALIDISSUEDATE" "INVALIDISSUEDATE"

#    * запрос к API "api/mobile/v3/submitPersonalData" и сохраняем в "responceAPI":
#      | devId                   | DEVID           |
#      | authToken               | TOKEN           |
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

