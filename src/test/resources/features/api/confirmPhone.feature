# language: ru
Функционал: API

  @api
  @confirmPhone
  @correct
  Сценарий: 3_10 Подтверждение телефона. Позитивный кейс

    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | 4       |
      | authToken |         |
      | source    |         |
      | сode      |         |


    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":0 |

  @fail
  Сценарий: 3_10 Подтверждение телефона. Негативный кейс
    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId     | 4       |
      | authToken |         |
      | source    |         |
      | сode      |         |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":5 |
