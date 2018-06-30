# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 1                      |
      | email  | demo617@mailinator.com |
      | pass   | Parol123               |
      | source | 16                     |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{"status": |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @confirmPhone
  @correct
  Сценарий: 3_10 Подтверждение телефона. Позитивный кейс

    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | 1         |
      | authToken | AUTHTOKEN |
      | source    | 16        |
      | сode      |  0        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{" |

  @fail
  Сценарий: 3_10 Подтверждение телефона. Негативный кейс
    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | 1         |
      | authToken | AUTHTOKEN |
      | source    | 16        |
      | сode      |  0        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":9,"data":{"message":"code incorrect" |
