# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | PASSWORD  | Default |
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





    ## предыстория для реги

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
      | STREET  | "Волгоградский проспект" |
    * сохраняем в память
      | FLAT  | randomNumber 3 |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | COMMENT  | random |


    * определяем незанятый номер телефона и сохраняем в "PHONE"
#    * сохраняем в память
#      | PHONE  | 71110024400 |
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
  @rega
  Сценарий: Мобильная регистрация полная через SKYPE для того, чтобы прошла ФинДоставка

    * редактируем некоторые активные опции сайта
      |identification_with_video|true|
      |back_call|false|
      |announcements|true|
    * редактируем некоторые активные опции сайта
      |identification_with_video|true|

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
      | identType               | 3            |

#    * проверка ответа API из "RESPONCE_API":
#      | exepted | "code":0 |

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


    * определяем user_id пользователя "EMAIL" и сохраняем в "ID"

    * запрос к API "api/stoloto/identification/approveVideoIdent" и сохраняем в "RESPONCE_API":
      | type       | skype |
      | customer   | ID    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

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

    * проверка вариантного ответа API из "RESPONCE_API":
      | exepted     | "status":8 or "status":7 |

    * выставляем обратно старое значение активных опций сайта "ACTIVE_SITE_OPTIONS"


#  @api
#  @fin_dostavka_identification
#  Сценарий: Отправка заявки в ФинДоставку и проверка статуса заявки

    * ищем пользователя с ограничениями "ALLROWS"

    * определяем дату завтрашнего дня "DATE"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | email       | EMAIL    |
      | pass        | PASSWORD |
      | source      | 16       |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":8 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | street       | "Тверская ул." |
      | house        | HOUSE          |
      | building     |                |
      | housing      |                |
      | flat         | FLAT           |
      | phone        | PHONE     |
      | comment      | COMMENT        |
      | date         | DATE           |
      | time         | "10:00 - 17:00"|

    * запрос к API "api/mobile/v5/createDostavistaOrder " и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | authToken   | AUTHTOKEN|
      | source      | 16       |
      | data        | DATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | identificationStatus":1 |

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                     |
      | partner_order_id        | PARTNERORDERID                  |
      | delivery_address        | "Тверская ул."                   |
      | delivery_time_start     | "2018-11-15T20:00:00+03:00"      |
      | delivery_time_finish    | "2018-11-15T21:00:00+03:00"      |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_agreed"              |
      | event_date   | "2018-11-15T15:05:23+03:00"     |
      | data         | DATA                            |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "taskId": |

    * ожидание "5" сек

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_AGREED"

    * добавляем данные в JSON объект "COURIER" сохраняем в память:
      | phone                   | "88005553535"                     |
      | name                    | "Мистер Курьер"                   |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |
      | order_id                | 649326                           |
      | courier                 | COURIER                          |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_courier_assigned"    |
      | event_date   | "2018-11-15T15:05:23+03:00"     |
      | data         | DATA                            |

    * проверка ответа API из "RESPONCE_API":
      | exepted     |  "taskId":|

    * ожидание "5" сек

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_COURIER_ASSIGNED"


    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |
      | order_id                | 649326                           |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_pack_verified_by_courier"    |
      | event_date   | "2018-11-15T15:05:23+03:00"             |
      | data         | DATA                                    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "taskId":|


    * ожидание "5" сек

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_PACK_VERIFIED_BY_COURIER"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | recipient_id            | RECIPIENTID                      |
      | partner_order_id        | PARTNERORDERID                   |


    * запрос к esb "tasktype_endpoint/partner_notification" и сохраняем в "RESPONCE_API":

      | event_type   | "recipient_completed"    |
      | event_date   | "2018-11-15T15:05:23+03:00"             |
      | data         | DATA                                    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "taskId": |

    * ожидание "5" сек

    * получаем и сохраняем в память все строки для достависты телефона "PHONE"

    * смотрим изменился ли статус "EVENTTYPE" на "RECIPIENT_COMPLETED"

