# language: ru
Функционал: API
  Предыстория:
    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |

    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASSWORD  | Default |

    * сохраняем в память
      | TICKETID  | random 12|
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
  Сценарий: Успешная ставка vermantia

    * пополняем пользователю баланс если нужно, судя по ответу "RESPONCE" на сумму, достаточную для "AMOUNT"
      | accountCode   | string        |
      | accountName   | string        |
      | amount        | AMOUNTRUB     |
      | currency      | 643           |
      | feeAmount     | 0             |
      | operationId   | OPERATIONID   |
      | operationType | 3             |
      | paymentSystem | cupis_card    |
      | transactionId | TRANSACTIONID |
      | userId        | USERID        |

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

    * запрос в swagger для получения getHolds "HOLDRUB" "HOLDBONUS"
    | userId | ID |



