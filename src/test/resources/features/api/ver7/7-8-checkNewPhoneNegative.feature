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
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "ACCOUNTTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | confirmBy                   | METHOD        |
      | target                      | phone         |
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
      | confirmationCode            | CODE          |

    * запрос к API "api/mobile/v7/confirmAccountFromEmail" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":0      |

    * находим и сохраняем "UPDATETOKEN" из "RESPONCE_API"



  Сценарий: Проверка checkNewPhone при занятом номере телефона
    * определяем занятый номер телефона и сохраняем в "PHONEOLD"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | phone                       | PHONEOLD         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":8      |

    * ждем некоторое время "1"

  Сценарий: Проверка checkNewPhone при невалидном номере телефона(не хвататет цифр)

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | phone                       | 123456789     |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":5      |

    * ждем некоторое время "1"

#  Сценарий: Проверка checkNewPhone при невалидном номере телефона(буквы в номере телефона)
#
#    * добавляем данные в JSON объект "DATA" сохраняем в память:
#      | phone                       | 7dasd256818     |
#      | updateToken                 | UPDATETOKEN   |
#
#    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
#      | devId                       | DEVID         |
#      | authToken                   | AUTHTOKEN     |
#      | source                      | SOURCE        |
#      | data                        | DATA          |
#
#    * проверка ответа API из "RESPONCE_API":
#      | exepted                     | "code":5      |
#
#    * ждем некоторое время "1"

  Сценарий: Проверка checkNewPhone при невалидном authToken
    * сохраняем в память
      | INVALIDAUTHTOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * определяем незанятый номер телефона и сохраняем в "PHONE"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | phone                       | PHONE         |
      | updateToken                 | UPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | INVALIDAUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":15      |

    * ждем некоторое время "1"

  Сценарий: Проверка checkNewPhone при невалидном updateToken
    * сохраняем в память
      | INVALIDUPDATETOKEN  | 12345678-1234-1234-1234-ABCDEF123456 |

    * определяем незанятый номер телефона и сохраняем в "PHONE"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | phone                       | PHONE         |
      | updateToken                 | INVALIDUPDATETOKEN   |

    * запрос к API "api/mobile/v7/checkNewPhone" и сохраняем в "RESPONCE_API":
      | devId                       | DEVID         |
      | authToken                   | AUTHTOKEN     |
      | source                      | SOURCE        |
      | data                        | DATA          |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "code":101    |

    * проверка ответа API из "RESPONCE_API":
      | exepted                     | "message":"invalid update token" |
    * ждем некоторое время "1"