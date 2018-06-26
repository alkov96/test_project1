# language: ru
Функционал: API

  @api
  @login
  @correct
  Сценарий: 3_19 Аутентификация пользователя. Позитивный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 1                      |
      | email  | demo617@mailinator.com |
      | pass   | Parol123               |
      | source | 16                     |

    * проверка ответа API из "responceAPI":
    | exepted | "code":0 |

  @fail
  Сценарий: 3_19 Аутентификация пользователя. Негативный кейс

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId  | 0 |
      | email  | 0 |
      | pass   | 0 |
      | source | 0 |

    * проверка ответа API из "responceAPI":
      | exepted | "code":1 |


