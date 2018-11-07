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
      | source                  | SOURCE       |
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
      | source                  | SOURCE       |
      | personalData            | PERSONALDATA |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
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

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

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
  Сценарий: Запрос на видеоидентификацию с неправильного устройства

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
      |video_identification_in_mobile_app|true|
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
      | exepted | "code":45 |



  @api
  @requestVideoChatConfirmation
  @incorrect
  Сценарий: Запрос на видеоидентификацию при выключенной настройке

    * поиск акаунта со статуом регистрации "=17" "EMAIL"

    * редактируем некоторые активные опции сайта
      |video_identification_in_mobile_app|false|
      |identification_with_video|true|

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
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

    * редактируем активные опции сайта, а старое значение сохраняем в "ACTIVE"
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




