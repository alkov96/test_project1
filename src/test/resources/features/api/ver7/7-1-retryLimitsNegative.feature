# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

  @api
  @changePassword
  @correct
  Сценарий: "ПРОВЕРКА ЗАПРОСА retryLimits ПРИ ЗАКОНЧИВШИХСЯ УСПЕШНЫХ ПОПЫТКАХ СМЕНЫ БАЗОВЫХ ПАРАМЕТРОВ

    * поиск акаунта с закончившимися успешными попытками смены БП "EMAILNOTEDIT"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAILNOTEDIT  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0      |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v7/retryLimits" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "remainingAttempts":0      |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "exceededLimitType":2      |
    * ждем некоторое время "1"

  Сценарий: ПРОВЕРКА ЗАПРОСА retryLimits ПРИ НЕПРАВИЛЬНОМ authtoken

    * поиск акаунта для проверки изменений базовых параметров "EMAIL"
    * сохраняем в память
      | INVALIDUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAIL  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0      |

    * запрос к API "api/mobile/v7/retryLimits" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | INVALIDUTHTOKEN  |
      | source                      | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":15      |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "message":"invalid auth token"      |
    * ждем некоторое время "1"

  Сценарий: ПРОВЕРКА ЗАПРОСА retryLimits ПРИ ЗАКОНЧИВШИХСЯ ПОПЫТКАХ В СУТКИ
    * поиск акаунта с закончившимися суточными попытками смены БП "EMAILNOTEDITONEDAY"
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAILNOTEDITONEDAY  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0      |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v7/retryLimits" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "remainingAttempts":0      |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "exceededLimitType":1      |
    * ждем некоторое время "1"