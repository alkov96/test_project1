# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | SOURCE  | 16 |

    * сохраняем в память
      | DEVID  | randomNumber 4 |

    * сохраняем в память
      | PASSWORD  | Default |
#    * сохраняем в память
#      | EMAIL  | testregistrator+73330018130@inbox.ru |

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
  Сценарий: версия 7. Изменение ТЕЛЕФОНА, подтверждение аккаунта - через ТЕЛЕФОН

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

    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

# на этом шаге можно првоерить сколько секунд осталось до попытки ворой раз отправить смс или письма. Но необходимо уточнить откуда тут updateToken

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
    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

    * ждем некоторое время "2"

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

#    * добавляем данные в JSON объект "DATA" сохраняем в память:
#      | confirmationCode            | CODE         |
#
#    * запрос к API "api/mobile/v7/сonfirmAccountFromEmail" и сохраняем в "RESPONCE_API":
#      | devId                       | DEVID         |
#      | authToken                   | AUTHTOKEN     |
#      | source                      | SOURCE        |
#      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"

#    * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"

  #аккаунт подтвержден, теперь пытаемся изменить телефон.
  # сначада вводим новый номер телефона. и онп рвоерится на незанятость
    # сначала отправим заняты номер телефона
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

    #если телеон незанят, и нет других ошибок, то теперь нужно подтвердить телефон. запрсо кода - у Федора

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

    * ждем некоторое время "1"








  @api
  @changePassword
  @correct
  Сценарий: версия 7. Изменение ТЕЛЕФОНА, подтверждение аккаунта - через ПОЧТУ

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

    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

# на этом шаге можно првоерить сколько секунд осталось до попытки ворой раз отправить смс или письма. Но необходимо уточнить откуда тут updateToken

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
    * находим и сохраняем "REMAININGSECONDS" из "RESPONCE_API"

    * ждем некоторое время "2"

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

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"

#    * проверим что время "REMAININGSECONDS" уменьшилось в "RESPONCE_API"

  #аккаунт подтвержден, теперь пытаемся изменить телефон.
  # сначада вводим новый номер телефона. и онп рвоерится на незанятость
    # сначала отправим заняты номер телефона
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

    #если телеон незанят, и нет других ошибок, то теперь нужно подтвердить телефон. запрсо кода - у Федора

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


    * ждем некоторое время "1"
