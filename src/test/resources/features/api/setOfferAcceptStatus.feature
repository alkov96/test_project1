# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 1                       |
      | email  | demo617@mailinator.com  |
      | pass   | Parol123                |
      | source | 16                      |

  @api
  @setOfferAcceptStatus
  @correct
  Сценарий: 3_3	Подтверждение оферты

    * запрос к API "setOfferAcceptStatus" и сохраняем в "RESPONCE_API":
      | devId             | 1           |
      | authToken         | responceAPI |
      | source            | 16          |
      | offerAcceptStatus | 1           |



    * проверка ответа API из "RESPONCE_API":
      | exepted  |  "code":0  |