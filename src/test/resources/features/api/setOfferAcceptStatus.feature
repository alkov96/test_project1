# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 15                      |
      | email  | demo617@mailinator.com  |
      | pass   | Parol123                |
      | source | 16                      |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @setOfferAcceptStatus
  @correct
  Сценарий: 3_3	Подтверждение оферты

    * запрос к API "api/mobile/v3/setOfferAcceptStatus" и сохраняем в "RESPONCE_API":
      | devId             | 15        |
      | authToken         | AUTHTOKEN |
      | source            | 16        |
      | offerAcceptStatus | 2         |

    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |