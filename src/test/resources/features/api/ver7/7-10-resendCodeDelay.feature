# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |

    * поиск акаунта для проверки изменений базовых параметров "EMAIL"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID  |
      | email  | EMAIL  |
      | pass   | PASSWORD   |
      | source | SOURCE |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0,"data":{ |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

Сценарий: resendCodeDelay при методе подтверждения аккаунта = базовый параметр, который меняем = email позитив и негатив
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
    | confirmBy                   | METHOD         |
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

#кусочек для проверки remainingSeconds

  * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

  * ждем некоторое время "2"

  * добавляем данные в JSON объект "DATA" сохраняем в память:
    | step                        | account         |
    | method                      | METHOD       |
    | accountToken                | ACCOUNTTOKEN |

  * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
    | devId                       | DEVID         |
    | authToken                   | AUTHTOKEN     |
    | source                      | SOURCE        |
    | data                        | DATA          |

  * проверка ответа API из "RESPONCE_API":
    | exepted                     | "code":0      |

  * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"


  * сохраняем в память
    | INVALIDACCOUNTTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |
  * добавляем данные в JSON объект "DATA" сохраняем в память:
    | step                        | update       |
    | method                      | METHOD        |
    | accountToken                | ACCOUNTTOKEN   |

  * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
    | devId                       | DEVID         |
    | authToken                   | AUTHTOKEN     |
    | source                      | SOURCE        |
    | data                        | DATA          |

  * проверка ответа API из "RESPONCE_API":
    | exepted                     | "code":103      |

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

  * генерим email в "NEWEMAIL"

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
  * проверка ответа API из "RESPONCE_API":
    | exepted                     | "remainingSeconds":119 |

  #кусочек для проверки remainingSeconds

  * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

  * ждем некоторое время "2"

  * добавляем данные в JSON объект "DATA" сохраняем в память:
    | step                        | update       |
    | method                      | METHOD        |
    | updateToken                 | UPDATETOKEN   |

  * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
    | devId                       | DEVID         |
    | authToken                   | AUTHTOKEN     |
    | source                      | SOURCE        |
    | data                        | DATA          |

  * проверка ответа API из "RESPONCE_API":
    | exepted                     | "code":0      |
  * проверка ответа API из "RESPONCE_API", значение берем из памяти
    | exepted                     | NEWEMAIL |

  * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"

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


  Сценарий: resendCodeDelay при методе подтверждения аккаунта = базовый параметр, который меняем = phone позитив и негатив
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
      | confirmBy                   | METHOD         |
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
      | exepted                     | "remainingSeconds":59 |

#кусочек для проверки remainingSeconds

    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

    * ждем некоторое время "2"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | step                        | account         |
      | method                      | METHOD       |
      | accountToken                | ACCOUNTTOKEN |

    * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"

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

    * определяем незанятый номер телефона и сохраняем в "PHONE"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | phone                       | PHONE         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |
    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "remainingSeconds":59 |

  #кусочек для проверки remainingSeconds

    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

    * ждем некоторое время "2"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | step                        | update       |
      | method                      | METHOD        |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |
    * проверка ответа API из "RESPONCE_API", значение берем из памяти
      | exepted                     | PHONE |

    * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"


    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |
    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | step                        | update       |
      | method                      | METHOD        |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | INVALIDAUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15      |


    * сохраняем в память
      | INVALIDUPDATETOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |
    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | step                        | update       |
      | method                      | METHOD        |
      | updateToken                 | INVALIDUPDATETOKEN   |

    * запрос к API "api/mobile/v7/resendCodeDelay" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":103      |



    * запоминаем код подтверждения смены Базовых Бараметров "CODENEWPARAM" для пользователя "EMAIL"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmationCode            | CODENEWPARAM  |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/confirmAndUpdateNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |