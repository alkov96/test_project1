# language: ru
Функционал: API-vermantia
  Предыстория:
    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |

    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | TICKETID  | randomHex|
    * сохраняем в память
      | GROUPCODE  | randomNumber 4|
    * сохраняем в память
      | SHOPCODE  | randomNumber 4|
    * сохраняем в память
      | AMOUNT  | 300|

    * поиск акаунта со статуом регистрации "=2" "ALLROWS"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE":
      | devId  | DEVID |
      | email  | EMAIL |
      | pass   | PASSWORD  |
      | source | 16    |

    * проверка ответа API из "RESPONCE":
      | exepted | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE"

    * запрос типа GET, результат сохраняем в "RESPONCE"
    | data   | ESB_URL              |
    | string | rest/webuserbalance  |
    | stash  | GROUPCODE            |
    | stash  | SHOPCODE             |
    | stash  | AUTHTOKEN            |
    | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |



  @api
  @vermantia
  @susessfullbet
  Сценарий: Успешная ставка vermantia на рубли
    * сохраняем в память
      | CURRENCY  | RUB |

    * пополняем пользователю баланс если нужно, судя по ответу "RESPONCE" на сумму, достаточную для "AMOUNT"
      | accountCode   | string        |
      | accountName   | string        |
      | amount        | AMOUNT        |
      | currency      | 643           |
      | feeAmount     | 0             |
      | operationId   | OPERATIONID   |
      | operationType | 3             |
      | paymentSystem | cupis_card    |
      | transactionId | TRANSACTIONID |
      | userId        | ID        |

#запоминаем баланс
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL              |
      | string | rest/webuserbalance  |
      | stash  | GROUPCODE            |
      | stash  | SHOPCODE             |
      | stash  | AUTHTOKEN            |
      | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим и сохраняем "BALANCE" из "RESPONCE"

    #смотрим сколько сейчас у пользователя захолдировано денег
    * запрос в swagger для получения getHolds "HOLDRUB" "HOLDBONUS"
    | userId | ID |

    #авторизуемся на вермантии пользаком
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL                |
      | string | rest/webidentification |
      | stash  | GROUPCODE              |
      | stash  | SHOPCODE               |
      | stash  | AUTHTOKEN              |
      | string | 213.92.47.18           |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим и сохраняем "SESSION" из "RESPONCE"

    #делаем запрос на ставку
    * запрос по адресу "{ESB_URL}/rest/webreservebet" и сохраняем в "RESPONCE_API":
        | group_code  | GROUPCODE |
        | shop_code   | SHOPCODE  |
        | session     | SESSION   |
        | skin        | "MAIN"    |
        | ticket_id   | TICKETID  |
        | game        | "QF"      |
        | currency    | CURRENCY  |
        | amount      | AMOUNT    |
        | bonus       | false     |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    #проверям баланс пользователя. он должен был измениться
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL              |
      | string | rest/webuserbalance  |
      | stash  | GROUPCODE            |
      | stash  | SHOPCODE             |
      | stash  | AUTHTOKEN            |
      | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим "balance" и сохраняем "BALANCE2" из "RESPONCE"
    * проверим что "BALANCE" больше "BALANCE2" на "AMOUNT"

      #проверям что захолдированы рубли на эту ставку
    * запрос в swagger для получения getHolds "HOLDRUB2" "HOLDBONUS2"
      | userId | ID |

    * проверим что "HOLDRUB2" больше "HOLDRUB" на "AMOUNT"

    #подтверждаем ставку
    * запрос по адресу "{ESB_URL}/rest/webplacebet" и сохраняем в "RESPONCE_API":
      | group_code  | GROUPCODE |
      | shop_code   | SHOPCODE  |
      | session     | SESSION   |
      | skin        | main      |
      | ticket_id   | TICKETID  |
      | game        | qf        |
      | currency    | CURRENCY  |
      | amount      | AMOUNT    |
      | bonus       | false     |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * ждем некоторое время "1"
    #проверям что ставка расхолдировалась
    * запрос в swagger для получения getHolds "HOLDRUB2" "HOLDBONUS3"
      | userId | ID |

    * проверим что "HOLDRUB2" больше "HOLDRUB" на "0"




  @api
  @vermantia
  @susessfullbet
  Сценарий: Успешная ставка vermantia на бонусы
    * сохраняем в память
      | CURRENCY  | RUB |

    * пополняем пользователю бонусы если нужно, судя по ответу "RESPONCE" на сумму, достаточную для "AMOUNT"
      | accountCode   | string        |
      | accountName   | string        |
      | amount        | AMOUNT        |
      | currency      | 999           |
      | operationId   | OPERATIONID   |
      | operationType | 5             |
      | transactionId | TRANSACTIONID |
      | userId        | ID            |


#запоминаем баланс
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL              |
      | string | rest/webuserbalance  |
      | stash  | GROUPCODE            |
      | stash  | SHOPCODE             |
      | stash  | AUTHTOKEN            |
      | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим "bonus-balance" и сохраняем "BONUSES" из вложенного "RESPONCE"

    #смотрим сколько сейчас у пользователя захолдировано денег
    * запрос в swagger для получения getHolds "HOLDRUB" "HOLDBONUS"
      | userId | ID |

    #авторизуемся на вермантии пользаком
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL                |
      | string | rest/webidentification |
      | stash  | GROUPCODE              |
      | stash  | SHOPCODE               |
      | stash  | AUTHTOKEN              |
      | string | 213.92.47.18           |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим и сохраняем "SESSION" из "RESPONCE"

    #делаем запрос на ставку
    * запрос по адресу "{ESB_URL}/rest/webreservebet" и сохраняем в "RESPONCE_API":
      | group_code  | GROUPCODE |
      | shop_code   | SHOPCODE  |
      | session     | SESSION   |
      | skin        | "MAIN"    |
      | ticket_id   | TICKETID  |
      | game        | "QF"      |
      | currency    | CURRENCY  |
      | amount      | AMOUNT    |
      | bonus       | true      |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    #проверям баланс пользователя. он должен был измениться
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL              |
      | string | rest/webuserbalance  |
      | stash  | GROUPCODE            |
      | stash  | SHOPCODE             |
      | stash  | AUTHTOKEN            |
      | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим "bonus-balance" и сохраняем "BONUSES2" из вложенного "RESPONCE"
    * проверим что "BONUSES" больше "BONUSES2" на "AMOUNT"

      #проверям что захолдированы рубли на эту ставку
    * запрос в swagger для получения getHolds "HOLDRUB2" "HOLDBONUS2"
      | userId | ID |

    * проверим что "HOLDBONUS2" больше "HOLDBONUS" на "AMOUNT"

    #подтверждаем ставку
    * запрос по адресу "{ESB_URL}/rest/webplacebet" и сохраняем в "RESPONCE_API":
      | group_code  | GROUPCODE |
      | shop_code   | SHOPCODE  |
      | session     | SESSION   |
      | skin        | main      |
      | ticket_id   | TICKETID  |
      | game        | qf        |
      | currency    | CURRENCY  |
      | amount      | AMOUNT    |
      | bonus       | true     |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * ждем некоторое время "1"
    #проверям что ставка расхолдировалась
    * запрос в swagger для получения getHolds "HOLDRUB2" "HOLDBONUS2"
      | userId | ID |

    * проверим что "HOLDBONUS2" больше "HOLDBONUS" на "0"

