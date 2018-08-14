# language: ru
Функционал: API

  Предыстория:
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | PASS  | Default |
    * сохраняем в память
      | FIRSTNAME  | random |
    * сохраняем в память
      | SURNAME  | random |
    * сохраняем в память
      | PATRONYMIC  | random |
    * сохраняем в память
      | BIRTHDATER  | randomDate |


  @api
  @canDeposit
  @correct

  Сценарий: Проверка дорегистрации пользователя при переходе с версии регистрации v2

    * поиск акаунта со статуом регистрации "=2" "ALLROWS"
    * обновляем поля в БД для юзера "EMAIL":
      | registration_stage_id | 4     |
      | tsupis_status         | null  |
      | phone_confirmed       | false |
      | first_name            | null  |
      | surname               | null  |
      | identified_in_tsupis  | false |
      | registered_in_tsupis  | false |
      | personality_confirmed | 0     |

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID |
      | email       | EMAIL |
      | pass        | PASS  |
      | source      | 16    |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * запрос к API "api/mobile/v3/submitFioBirthdate" и сохраняем в "RESPONCE_API":
      | devId       | DEVID       |
      | authToken   | AUTHTOKEN   |
      | source      | 16          |
      | first_name  | FIRSTNAME   |
      | surname     | SURNAME     |
      | patronymic  | PATRONYMIC  |
      | birth_date  | BIRTHDATER   |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":1 |

    * составляем новый номер телефона "NEWPHONE" вместо старого "PHONE"

    * запрос к API "api/mobile/v3/changePhone" и сохраняем в "RESPONCE_API":
      | devId       | DEVID       |
      | authToken   | AUTHTOKEN   |
      | source      | 16          |
      | phone       | NEWPHONE   |
      | pass        | PASS     |


    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":1 |

    * ожидание "1" сек

    * запрос к API "api/mobile/v3/resendPhoneCode" и сохраняем в "RESPONCE_API":
      | devId       | DEVID       |
      | authToken   | AUTHTOKEN   |
      | source      | 16          |


    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":1 |

    * получаем и сохраняем в память код подтверждения "CODE" телефона "NEWPHONE" "новый"

    * запрос к API "api/mobile/v3/confirmPhone" и сохраняем в "RESPONCE_API":
      | devId       | DEVID       |
      | authToken   | AUTHTOKEN   |
      | source      | 16          |
      | code        | CODE        |


    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":11 |

