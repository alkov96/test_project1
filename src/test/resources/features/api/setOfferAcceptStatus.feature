# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |
    * сохраняем в память
      | PASSWORD  | Default |
    * сохраняем в память
      | SOURCE | 16 |



  @api
  @setOfferAcceptStatus
  @correct
  Сценарий: 3_3	Подтверждение оферты

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * обновляем поля в БД для юзера "EMAIL":
      | offer_state | 0     |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | EMAIL     |
      | pass   | PASSWORD     |
      | source | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"
    * запрос к API "api/mobile/v3/setOfferAcceptStatus" и сохраняем в "RESPONCE_API":
      | devId             | DEVID     |
      | authToken         | AUTHTOKEN |
      | source            | SOURCE    |
      | offerAcceptStatus | 1         |

    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |
    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "offerAcceptStatus":1  |

    * запрос к API "api/mobile/v3/setOfferAcceptStatus" и сохраняем в "RESPONCE_API":
      | devId             | DEVID     |
      | authToken         | AUTHTOKEN |
      | source            | SOURCE    |
      | offerAcceptStatus | 2         |

    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |
    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "offerAcceptStatus":2  |