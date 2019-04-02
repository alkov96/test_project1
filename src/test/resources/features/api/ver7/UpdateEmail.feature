# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * генерим email в "NEWEMAIL"

    * поиск акаунта для проверки изменений базовых параметров "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAIL  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{ |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

  @api
  @changePassword
  @correct
  Сценарий: версия 7. Изменение EMAIL, подтверждение аккаунта - через ТЕЛЕФОН

    * сохраняем в память
      | METHOD                      | phone         |

    * запрос к API "api/mobile/v7/retryLimits" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "REMAININGATTEMPTS" из "RESPONCE_API"

    #далее идет проверка аккаунта. его нужно подтвердить. сначала - првоерка пароля

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | password                    | PASSWORD      |

    * запрос к API "api/mobile/v7/checkPass" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "ACCOUNTTOKEN" из "RESPONCE_API"

    #затем выбираем через что подтверждаем аккаунт - или телеон, или почта
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

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "remainingSeconds":59 |

    #теперь собственно подтверждение аккаунта


    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"
    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |
      | accountToken                | ACCOUNTTOKEN |

    * запрос к API "api/mobile/v7/confirmAccountFromSms" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | NEWEMAIL         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    #если телеон незанят, и нет других ошибок, то теперь нужно подтвердить телефон. запрсо кода - у Федора

    * запоминаем код подтверждения смены Базовых Бараметров "CODENEWPARAM" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODENEWPARAM  |

    * запрос к API "api/mobile/v7/confirmAndUpdateNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |


  @api
  @changePassword
  @correct
  Сценарий: версия 7. Изменение EMAIL, подтверждение аккаунта - через ПОЧТУ

    * сохраняем в память
      | METHOD                      | email         |

    * запрос к API "api/mobile/v7/retryLimits" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "REMAININGATTEMPTS" из "RESPONCE_API"

    #далее идет проверка аккаунта. его нужно подтвердить. сначала - првоерка пароля

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | password                    | PASSWORD      |

    * запрос к API "api/mobile/v7/checkPass" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "ACCOUNTTOKEN" из "RESPONCE_API"

    #затем выбираем через что подтверждаем аккаунт - или телеон, или почта
    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmBy                   | email         |
      | target                      | phone         |
      | accountToken                | ACCOUNTTOKEN  |

    * запрос к API "api/mobile/v7/chooseMethod" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "remainingSeconds":119 |

    #теперь собственно подтверждение аккаунта

    * запоминаем код подтверждения аккаунта "CODE" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODE         |

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | email                       | NEWEMAIL         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    #если телеон незанят, и нет других ошибок, то теперь нужно подтвердить телефон. запрсо кода - у Федора

    * запоминаем код подтверждения смены Базовых Бараметров "CODENEWPARAM" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODENEWPARAM  |

    * запрос к API "api/mobile/v7/confirmAndUpdateNewEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |



