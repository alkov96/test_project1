# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * поиск акаунта со статуом регистрации "=2" "EMAIL"

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | EMAIL  | testregistrator79330000423@mailinator.com |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID |
      | email  | EMAIL  |
      | pass   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * сохраняем в память
      | PHONE  | randomPhone |

  @api
  @requestPhoneCall
  @correct
  Сценарий: 3_25 Запросить/отменить обратный телефонный звонок. Позитивный кейс

    * редактируем некоторые активные опции сайта
      | back_call | true  |

    * устанавливаем время ожидания обратного звонка "20"

    # Запрос 1
    * запрос к API "api/mobile/v3/requestPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | phone           | PHONE     |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "CALLBACKREQUESTS" из "RESPONCE_API"

    * проверка полей и типов в ответе "CALLBACKREQUESTS":
    | Параметр         | Тип       |
    | id               | Long      |
    | userId           | Integer   |
    | creationTime     | Timestamp |
    | isActive         | Boolean   |

    # Отмена 1
    * находим и сохраняем "ID" из "CALLBACKREQUESTS"

    * проверяем значение полей в ответе "CALLBACKREQUESTS":
    | Параметр | Значение |
    | id       | ID       |
    | isActive | true     |

    * запрос к API "api/mobile/v3/cancelPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | id              | ID        |
      | phone           | PHONE     |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    # Запрос 2
    * запрос к API "api/mobile/v3/requestPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | phone           | PHONE     |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

    * находим и сохраняем "CALLBACKREQUESTS" из "RESPONCE_API"

    # Отмена 2
    * находим и сохраняем "ID" из "CALLBACKREQUESTS"

#    * устанавливаем время ожидания обратного звонка "30"

    * ожидание "21" сек

    * проверяем значение полей в ответе "CALLBACKREQUESTS":
      | Параметр | Значение |
      | id       | ID       |
      | isActive | true     |

    * запрос к API "api/mobile/v3/cancelPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | id              | ID        |
      | phone           | PHONE     |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | {"code":36,"data":{"message":"active callback not found or cancellation time expired"}} |

    # Запрос 3
    * запрос к API "api/mobile/v3/requestPhoneCall" и сохраняем в "RESPONCE_API":
      | devId           | DEVID     |
      | authToken       | AUTHTOKEN |
      | source          | 16        |
      | phone           | PHONE     |
      | comment         |           |
      | operatingSystem |           |
      | appVersion      |           |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":35 |


