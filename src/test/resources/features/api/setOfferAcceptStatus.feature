# language: ru
Функционал: API
  Предыстория:
    * запрос к API "api/mobile/v3/login" и сохраняем в "responceAPI":
      | devId  | 1                       |
      | email  | demo617@mailinator.com  |
      | pass   | Parol123                |
      | source | 16                      |

  @api
  Сценарий: 3_3	Подтверждение оферты

    * запрос к API "setOfferAcceptStatus" и сохраняем в "responceAPI":
      | devId             | 1           |
      | authToken         | responceAPI |
      | source            | 16          |
      | offerAcceptStatus | 1           |



    * проверка ответа API из "responceAPI":
      | exepted  |  "code":0  |