# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 1                      |
      | email  | demo617@mailinator.com |
      | pass   | Parol123               |
      | source | 16                     |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @getPersonalData
  @correct
  Сценарий: 3_4 Запрос персональных данных. Позитивный кейс
    * запрос к API "api/mobile/v3/getPersonalData" и сохраняем в "RESPONCE_API":
      | devId     | 1         |
      | authToken | AUTHTOKEN |
      | source    | 16        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":0,"data":{"personalData": |
