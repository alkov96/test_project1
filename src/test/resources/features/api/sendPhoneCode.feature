# language: ru
Функционал: API

  @api
  Сценарий: Запрос смс подтверждения телефона.

    * запрос к API "api/mobile/v3/sendPhoneCode":
     | devId  | 3            |
     | phone  | 71110020700  |

    * проверка ответа API:
    | exepted  |  "code":0  |


