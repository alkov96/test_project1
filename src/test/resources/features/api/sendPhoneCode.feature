# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | DEVID | randomNumber 4 |

    * определяем незанятый номер телефона и сохраняем в "PHONE"

  @sendPhoneCode
  @correct
  Сценарий: 3_6 Запрос смс подтверждения телефона. Позитивный кейс

    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
     | devId | DEVID |
     | phone | PHONE |

    * проверка ответа API из "RESPONCE_API":
    | exepted | "code":0 |

  @fail
  Сценарий: 3_6 Запрос смс подтверждения телефона. Негативный кейс
    * запрос к API "api/mobile/v3/sendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId | 1 |
      | phone | 0 |

    * проверка ответа API из "RESPONCE_API":
      | exepted | "code":5,"data":{"message":"phone not valid" |


