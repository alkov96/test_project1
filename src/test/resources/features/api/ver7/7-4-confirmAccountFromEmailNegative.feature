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
      | METHOD  | email |

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

  Сценарий: Проверка confirmAccountFromEmail при невалидном authToken
    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | INVALIDAUTHTOKEN  |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15         |


  Сценарий: Проверка confirmAccountFromEmail при неправильном коде

    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | 0000         |

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | AUTHTOKEN  |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":9         |
    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"code incorrect" |
