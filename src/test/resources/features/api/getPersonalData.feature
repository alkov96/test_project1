# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Parol123 |

    * сохраняем в память
      | SOURCE | 16 |

  @api
  @getPersonalData
  @correct
  Сценарий: 3_4 Запрос персональных данных. Позитивный кейс

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | email       | EMAIL    |
      | pass        | PASSWORD |
      | source      | 16       |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/getPersonalData" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"personalData": |
