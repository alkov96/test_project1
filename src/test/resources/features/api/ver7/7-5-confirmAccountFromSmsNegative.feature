# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | METHOD  | phone |

    * поиск акаунта для проверки изменений базовых параметров "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAIL  |
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
      | exepted                     | "code":0      |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | password                    | PASSWORD      |

    * запрос к API "api/mobile/v7/checkPass" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "ACCOUNTTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmBy                   | METHOD        |
      | target                      | email         |
      | accountToken                | ACCOUNTTOKEN  |

    * запрос к API "api/mobile/v7/chooseMethod" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"

  Сценарий: Проверка confirmAccountFromSMS при невалидном authToken
    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |
      | accountToken                | ACCOUNTTOKEN |

    * запрос к API "api/mobile/v7/confirmAccountFromSms" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | INVALIDAUTHTOKEN  |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15      |

  Сценарий: Проверка confirmAccountFromSMS при невалидном accountToken
    * сохраняем в память
      | INVALIDACCOUNTTOKEN  | 12345678-9009-1234-1234-ABCDEF123456 |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |
      | accountToken                | INVALIDACCOUNTTOKEN |

    * запрос к API "api/mobile/v7/confirmAccountFromSms" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | AUTHTOKEN  |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":100      |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"invalid account token" |

  Сценарий: Проверка confirmAccountFromSMS при неправильном коде

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | 0000         |
      | accountToken                | ACCOUNTTOKEN |

    * запрос к API "api/mobile/v7/confirmAccountFromSms" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | AUTHTOKEN         |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":9      |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"code incorrect" |