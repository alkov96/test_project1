# language: ru
Функционал: API
  Предыстория:
    * сохраняем в память
      | PASS  | Parol123 |
    * сохраняем в память
      | DEVID  | randomNumber 4 |
    * сохраняем в память
      | STREET  | "Волгоградский проспект" |
    * сохраняем в память
      | FLAT  | randomNumber 3 |
    * сохраняем в память
      | HOUSE  | randomNumber 2 |
    * сохраняем в память
      | COMMENT  | random |

  @api
  @DD_identification
  Сценарий: Отправка заявки в Домашние Деньги и проверка статуса заявки

    * ищем пользователя с ограничениями "ALLROWS"

    * определяем дату завтрашнего дня "DATE"

    * запрос к API "api/mobile/v3/login" и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | email       | EMAIL    |
      | pass        | PASS     |
      | source      | 16       |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |
    * проверка ответа API из "RESPONCE_API":
      | exepted     | "status":8 |

    * находим и сохраняем "AUTHTOKEN" из "RESPONCE_API"

    * добавляем данные в JSON объект "DATA" сохраняем в память:
      | street       | "Тверская ул." |
      | house        | HOUSE          |
      | building     |                |
      | housing      |                |
      | flat         | FLAT           |
      | phone        | PHONE          |
      | comment      | COMMENT        |
      | date         | DATE           |
      | time         | "10:00 - 17:00"|

    * запрос к API "api/mobile/v5/sendIdentificationOrderToDD" и сохраняем в "RESPONCE_API":
      | devId       | DEVID    |
      | authToken   | AUTHTOKEN|
      | source      | 16       |
      | data        | DATA     |

    * проверка ответа API из "RESPONCE_API":
      | exepted     | "code":0 |





