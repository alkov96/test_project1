# language: ru
Функционал: выполняются все необходимые запросы для пополнения баланса, бонусов, НДФЛ пользователю

  Предыстория:
    * сохраняем в память
      | USERID  | 51548549 |

    * сохраняем в память
      | AMOUNTRUB  | 1000000 |

    * сохраняем в память
      | AMOUNTBONUS  | 100000 |

    * сохраняем в память
      | AMOUNTNDFL  | 100000 |


  @refill_and_ndfl_rub
  Сценарий: пополнение счета рублей

    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |
    * запрос в swagger на пополнение баланса рублей/бонусов "RESPONCE_API"
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
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

  @refill_and_ndfl_bonus
  Сценарий: пополнение счета бонусов

    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |
    * запрос в swagger на пополнение баланса рублей/бонусов "RESPONCE_API"
      | amount        | AMOUNTBONUS   |
      | currency      | 999           |
      | operationId   | OPERATIONID   |
      | operationType | 5             |
      | transactionId | TRANSACTIONID |
      | userId        | USERID        |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

  @refill_and_ndfl_bonus
  Сценарий: пополнение счета НДФЛ

    * формируем параметры для запроса swagger
      | operationId     | OPERATIONID   |
      | transactionId   | TRANSACTIONID |
    * добавляем данные в JSON объект "OPERATIONS" сохраняем в память:
      | userId             | USERID          |
      | currency           | 643             |
      | amount             | AMOUNTNDFL      |
      | operationType      | 1               |
      | operationId        | OPERATIONID     |
      | description        | refill for test |
      | relatedOperationId | without         |
    * запрос в swagger на добавление НДФЛ "RESPONCE_API"
      | transactionId      | TRANSACTIONID   |
      | operations         | (OPERATIONS)      |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
