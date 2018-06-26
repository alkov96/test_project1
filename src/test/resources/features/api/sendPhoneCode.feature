# language: ru
Функционал: API

  @api
  @sendPhoneCode
  @correct
  Сценарий: 3_6 Запрос смс подтверждения телефона. Позитивный кейс

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
     | devId | 3           |
     | phone | 71110020700 |

    * проверка ответа API из "RESPONCE_API":
    | exepted | "code":0 |

  @fail
  Сценарий: 3_6 Запрос смс подтверждения телефона. Негативный кейс
    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | 0 |
      | phone | 0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":5 |


