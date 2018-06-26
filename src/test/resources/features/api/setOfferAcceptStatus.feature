# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login":
      | devId  | 1                           |
      | email  | demo617@mailinator.com      |
      | pass   | Parol123                    |
      | source | 16                          |

  @api
  Сценарий: 3_3	Подтверждение оферты

    * запрос к API "setOfferAcceptStatus":
      | devId             | 1                        |
      | authToken         | demo617@mailinator.com   |
      | pass              | Parol123                    |
      | offerAcceptStatus | 16                          |



    * проверка ответа API:
      | exepted  |  "code":0  |