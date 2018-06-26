# language: ru
Функционал: API

  @regress
  @AuthenticationOnSiteAndExit_1
  Сценарий: Запрос смс подтверждения телефона.

    * запрос к API "api/mobile/v3/login":
      | devId  | 1                           |
      | email  | demo617@mailinator.com |
      | pass   | Parol123                    |
      | source | 16                          |

    * проверка ответа API:
    | exepted  |  "code":0  |


