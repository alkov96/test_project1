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

    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID             |
      | authToken                   | AUTHTOKEN  |
      | source                      | SOURCE            |
      | data                        | DATA              |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0         |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"

  Сценарий: Проверка checkNewEmail при невалидном email

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | nevalid_email         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":3      |
    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"email not valid" |

    * ждем некоторое время "1"


  Сценарий: Проверка checkNewEmail при занятом email
    * определяем занятый адрес email и сохраняем в "OLDEMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | OLDEMAIL         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":7      |

    * ждем некоторое время "1"

  Сценарий: Проверка checkNewEmail при невалидном authToken

    * сохраняем в память
      | NEWEMAIL  | randomEmail |

    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | NEWEMAIL      |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | INVALIDAUTHTOKEN |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15     |

    * ждем некоторое время "1"


  Сценарий: Проверка checkNewEmail при невалидном updateToken
    * сохраняем в память
      | INVALIDUPDATETOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * сохраняем в память
      | NEWEMAIL  | randomEmail |

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | NEWEMAIL      |
      | updateToken                 | INVALIDUPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":101     |
    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"invalid update token" |
