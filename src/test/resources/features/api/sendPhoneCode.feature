# language: ru
Функционал: API

  @api
  Сценарий: Запрос смс подтверждения телефона.

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "responceAPI":
     | devId  | 3            |
     | phone  | 71110020700  |

    * проверка ответа API из "responceAPI":
    | exepted  |  "code":0  |


