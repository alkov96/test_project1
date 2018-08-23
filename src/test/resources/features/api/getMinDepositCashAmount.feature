# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |
    * сохраняем в память
      | USER  | Default |
    * сохраняем в память
      | PASSWORD  | Default |
    * сохраняем в память
      | SOURCE | 16 |

  @api
  @getMinDepositCashAmount
  @correct
  Сценарий: Запрос минимального значения для вызова инкассатора

    * обновим значение минимальной суммы вывода в рублях для вызова инкассатора "1500"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | DEVID   |
      | email  | USER    |
      | pass   | PASSWORD    |
      | source | SOURCE  |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v5/getMinDepositCashAmount" и сохраняем в "RESPONCE_API":
      | devId     | DEVID     |
      | authToken | AUTHTOKEN |
      | source    | SOURCE    |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted | "MIN_DEPOSIT_CASH_AMOUNT":"1500" |
