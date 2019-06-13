# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | METHOD  | email |

    * сохраняем в память
      | PASSWORD  | Default |

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

    Сценарий: Проверка chooseMethod при невалидном accountToken

      * сохраняем в память
        | INVALIDACCOUNTTOKEN  | 12345678-9009-1234-1234-ABCDEF123456 |
          #затем выбираем через что подтверждаем аккаунт - или телеон, или почта
      * добавляем данные в JSON объект "DATA" сохраняем в память:
        | confirmBy                   | METHOD        |
        | target                      | email         |
        | accountToken                | INVALIDACCOUNTTOKEN  |

      * запрос к API "api/mobile/v7/chooseMethod" и сохраняем в "RESPONCE_API":
        | devId                       | DEVID         |
        | authToken                   | AUTHTOKEN     |
        | source                      | SOURCE        |
        | data                        | DATA          |

      * проверка ответа API из "RESPONCE_API":
        | exepted                     | "code":100      |
      * проверка ответа API из "RESPONCE_API":
        | exepted                     | "message":"invalid account token" |
      * ждем некоторое время "1"

  Сценарий: Проверка chooseMethod при невалидном authToken

    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmBy                   | METHOD        |
      | target                      | email         |
      | accountToken                | ACCOUNTTOKEN  |

    * запрос к API "api/mobile/v7/chooseMethod" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | INVALIDAUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15      |
    * ждем некоторое время "1"