# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * сохраняем в память
      | USER  | Default |

    * сохраняем в память
      | PASS  | Default |

    * сохраняем в память
      | SOURCE | 16 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID    |
      | email  | USER     |
      | pass   | PASS     |
      | source | SOURCE   |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @subscribeToNotifications
  @correct
  Сценарий: 3_26 Изменение подписки на уведомления

    * запрос к API "api/mobile/v3/subscribeToNotifications" и сохраняем в "RESPONCE_API":
      | devId              | DEVID     |
      | authToken          | AUTHTOKEN |
      | source             | SOURCE    |
      | subscribe_to_sms   | true      |
      | subscribe_to_phone | true      |


    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |