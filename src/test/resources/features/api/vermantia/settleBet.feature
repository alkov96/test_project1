# language: ru
Функционал: API-vermantia
  Предыстория:
    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |

    * сохраняем в память
      | CURRENCY  | RUB |
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
    * сохраняем в память
      | AMOUNTSETTLE  | 300|

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
    * запрос по адресу "{ESB_URL}/rest/webreservebet" и сохраняем в "RESPONCE":
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

    * находим и сохраняем "TRANSACTION" из "RESPONCE"


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
    #запоминаем значение баланса
    * запрос типа GET, результат сохраняем в "RESPONCE"
      | data   | ESB_URL              |
      | string | rest/webuserbalance  |
      | stash  | GROUPCODE            |
      | stash  | SHOPCODE             |
      | stash  | AUTHTOKEN            |
      | string | 213.92.47.18         |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |

    * находим "balance" и сохраняем "BALANCE" из "RESPONCE"


  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланся, если ставка выиграла (payment)
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | AMOUNTSETTLE |
      | refund_amount   | 0            |
      | reason          | 1            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description":"Success" |

    #проверим что баланс увеличился на значение AMOUNTSETTLE
    * ждем некоторое время "1"

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
    * проверим что "BALANCE2" больше "BALANCE" на "AMOUNTSETTLE"


  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланся, если ставку отменили (refund)
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | 0            |
      | refund_amount   | AMOUNTSETTLE |
      | reason          | 2            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description": "Success" |

    #проверим что баланс увеличился на значение AMOUNTSETTLE
    * ждем некоторое время "1"

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
    * проверим что "BALANCE2" больше "BALANCE" на "AMOUNTSETTLE"


  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланся, если была отмена пополнения по ставке (cancel payment = отмена выигрыша)
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | AMOUNTSETTLE |
      | refund_amount   | 0            |
      | reason          | 3            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description": "Success" |

    #проверим что баланс уменьшился на значение AMOUNTSETTLE
    * ждем некоторое время "1"

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
    * проверим что "BALANCE2" больше "BALANCE" на "-AMOUNTSETTLE"

  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланся, если была отмена пополнения по ставке (cancel refund = отмена возврата)
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | 0            |
      | refund_amount   | AMOUNTSETTLE |
      | reason          | 4            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description": "Success" |

    #проверим что баланс уменьшился на значение AMOUNTSETTLE
    * ждем некоторое время "1"

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
    * проверим что "BALANCE2" больше "BALANCE" на "-AMOUNTSETTLE"

  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланса, если не было запроса в аккаунтинг (loser)
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | AMOUNTSETTLE |
      | refund_amount   | 0            |
      | reason          | 5            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description": "Success" |

    #проверим что баланс не изменился
    * ждем некоторое время "1"

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
    * проверим что "BALANCE2" больше "BALANCE" на "0"

  @api
  @vermantia
  @settlebet
  Сценарий: Проверка изменнеия баланса, если не было запроса в аккаунтинг ((ticket re opened))
    * запрос по адресу "{ESB_URL}/rest/websettlebet" и сохраняем в "RESPONCE_API":
      | group_code      | GROUPCODE    |
      | shop_code       | SHOPCODE     |
      | ticket_id       | TICKETID     |
      | game            | qf           |
      | bonus           | false        |
      | id_bonus        |              |
      | currency        | CURRENCY     |
      | total_amount    | AMOUNTSETTLE |
      | payment_amount  | AMOUNTSETTLE |
      | refund_amount   | 0            |
      | reason          | 6            |
      | skin            | main         |
      | user_account    | ID           |
      | transaction     | TRANSACTION  |

    * проверка ответа API из "RESPONCE":
      | exepted | "ret_code":1024 |
    * проверка ответа API из "RESPONCE":
      | exepted | "description":"Success" |

    #проверим что баланс не изменился
    * ждем некоторое время "1"
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
    * проверим что "BALANCE2" больше "BALANCE" на "0"


