# language: ru

Функционал: API
  Предыстория:


  ./features/Registration_mobile/steps.rb:2:in

    * сохраняем в память
      | SOURCE | 16 |
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASSWORD  | Default |

#  * проверка ответа API из "RESPONCE_API":
#  | exepted | "status":11 |


  @api
  @requestVideoChatConfirmation
  @correct
  Сценарий: Запрос на видеоидентификацию для пользователя ожидающего звонок



    * сохраняем в память
      | FIRSTNAME | random |
    * сохраняем в память
      | SURNAME | random |
    * сохраняем в память
      | PATRONYMIC | random |
    * сохраняем в память
      | BIRTHDATE | randomDate |
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
      |  source                | SOURCE     |
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
      | source | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "status":10 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | SOURCE    |

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
      | source                  | SOURCE       |
      | personalData            | PERSONALDATA |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|true|
      |identification_with_video|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL    |
      | pass   | PASSWORD |
      | source | SOURCE   |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/getIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | SOURCE           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * запрос к API "api/mobile/v3/submitIdentType" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | SOURCE           |
      | identType               | 3            |

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

    * запрос к API "api/mobile/v5/getUserStatus" и сохраняем в "RESPONCE_API":
      | devId                   | DEVID        |
      | authToken               | AUTHTOKEN    |
      | source                  | 16           |

    * проверка вариантного ответа API из "RESPONCE_API":
      | exepted     | "status":16 |

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * проверка полей и типов в ответе "DATA":
      | Параметр      | Тип    |
      | videochatLink | String |


  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию с устаревше версии(v4 и ниже)

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|true|
      |identification_with_video|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    #этот запрос только для версий >=v5. на версии 3 - должна быть ошибка
    * неудачный запрос к API "api/mobile/v3/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * неудачный запрос к API "api/mobile/v4/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |




  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию с неправильного устройства

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|true|
      |identification_with_video|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    #этот запрос только для версий >=v5. на версии 3 - должна быть ошибка
    * неудачный запрос к API "api/mobile/v3/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 42    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":45 |

  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию при выключенной настройке

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|false|
      |identification_with_video|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | 42    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":44 |


  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию для полностью зарегистрированного пользователя

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|true|

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | EMAIL    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/requestVideoChatConfirmation" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":12 |




